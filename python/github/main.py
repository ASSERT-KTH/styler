import configparser
from github import Github
from github.GithubException import UnknownObjectException
from github.GithubException import GithubException
import time
import datetime
import os
import requests
import json
import xml.etree.ElementTree as ET
from datetime import datetime, timedelta

config = configparser.ConfigParser()
config.read('config.ini')


g = Github(config['DEFAULT']['githubKey'])

def find_repos(file):
    codes = g.search_code(query='maven-checkstyle-plugin+language:Maven+POM') # l=Maven+POM&q=maven-checkstyle-plugin&type=Code
    print(codes.totalCount)
    repos = set()
    repos = set(load_repo_list(file))
    count = 0
    codes_iter = codes.__iter__()
    run = True
    count=0
    while run:
        try:
            page = codes_iter.__next__();
            repo_name = page.repository.full_name
            print(repo_name)
            count += 1
            repos.add(repo_name)
            if count >= 30:
                print("save")
                rate = g.get_rate_limit().search
                print(f'Used {rate.remaining} search queries of {rate.limit} ({rate.reset})')
                save_repos(file, repos)
                count = 0
                time.sleep(10)
        except GithubException as e:
            reset = g.get_rate_limit().search.reset
            print(reset)
            delta = reset - datetime.datetime.now()
            sleep_time = delta.seconds % 3600 + 5
            print(f'Sleep for {sleep_time} sec')
            time.sleep(sleep_time)


def save_repos(file, repos):
    with open(file ,'w') as f:
        for repo in repos:
            f.write(repo + '\n')

def dowload_and_save(repo, file, dir):
    file_name = file.split('/')[-1]
    contents = repo.get_contents(file)
    request = requests.get(contents.download_url)
    print(f'Get file ({file}) of {repo.name}')
    with open(os.path.join(dir, file_name), 'wb') as f:
        f.write(request.content)

def get_information(repo_name):
    print(f'Open repo {repo_name}')
    try:
        repo = g.get_repo(repo_name)
    except UnknownObjectException:
        print(f'Repo {repo_name} not found')
        return 'Not found'
    base_dir = f'./repos/{repo_name}'
    if not os.path.exists(base_dir):
        os.makedirs(base_dir)
    repo_info = dict()

    # datetime info
    now = datetime.now()
    one_week_before_now = now - timedelta(days=7)

    # commits
    if repo.updated_at >= one_week_before_now:
        commits = repo.get_commits(since=one_week_before_now, until=now)
        repo_info['past_week_commits'] = commits.totalCount
    else:
        repo_info['past_week_commits'] = 0

    # Gather some information
    repo_info['name'] = repo_name
    repo_info['stargazers_count'] = repo.stargazers_count
    repo_info['subscribers_count'] = repo.subscribers_count
    repo_info['forks_count'] = repo.forks_count
    repo_info['last_update'] = repo.updated_at.strftime("%Y-%m-%d %H:%M:%S")
    repo_info['fetched_at'] = now.strftime("%Y-%m-%d %H:%M:%S")

    # Get interesting files
    files_checkstyle = [ file.path for file in g.search_code(query=f'filename:checkstyle.xml repo:{repo_name}') ]
    files_travis = [ file.path for file in g.search_code(query=f'filename:.travis.yml repo:{repo_name}') ]
    files = files_checkstyle + files_travis
    repo_info['files'] = files
    checkstyle_file = ''
    travis_file = ''
    for file in files:
        if file.split('/')[-1] == 'checkstyle.xml':
            checkstyle_file = file
        if file == '.travis.yml':
            travis_file = file

    # Download cs and travis
    if checkstyle_file:
        dowload_and_save(repo, checkstyle_file, base_dir)
    if travis_file:
        dowload_and_save(repo, travis_file, base_dir)

    with open(os.path.join(base_dir, './info.json'), 'w') as fp:
        json.dump(repo_info, fp)

    return repo_info;

def load_repo_list(path):
    repo_list = []
    with open(path, 'r') as file:
        repo_list = file.read().split('\n')
    return repo_list;

def open_checkstyle(path):
    content = ''
    with open(path, 'r') as f:
        content = f.read()
    parsed_xml =  ET.fromstring(content)
    return parsed_xml

def get_cs_properties(cs):
    return { n.attrib['name']:n.attrib['value'] for n in cs if n.tag == 'property'}

def stats(folders):
    for folder in folders:
        cs_rules = open_checkstyle(os.path.join(folder, 'checkstyle.xml'))
        cs_properties = get_cs_properties(cs_rules)
        print(cs_properties)

def load_folders(file):
    dirs = []
    with open(file, 'r') as f:
        dirs = f.read().split('\n')
    dirs = [ '/'.join(dir.split('/')[:-1]) for dir in dirs if dir != '']
    return dirs;

if __name__ == "__main__":
    # repo_list = load_repo_list('repos.txt')
    # for repo in repo_list:
    #     try:
    #         get_travis(repo)
    #     except:
    #         print(f'Error getting {repo}')
    #     time.sleep(5)
    get_information('Spirals-Team/repairnator')
    # stats(list(set(load_folders('travis.txt')) & set(load_folders('checkstyle.txt'))))
    # find_repos('repos.txt')
