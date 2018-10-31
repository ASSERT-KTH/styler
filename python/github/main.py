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
from functools import reduce
import csv

config = configparser.ConfigParser()
config.read('config.ini')


g = Github(config['DEFAULT']['githubKey'])

def find_repos(file, from_size, to_size):
    codes = g.search_code(query=f'maven-checkstyle-plugin language:Maven POM size:{from_size}..{to_size}') # l=Maven+POM&q=maven-checkstyle-plugin&type=Code
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
                time.sleep(15)
        except GithubException as e:
            reset = g.get_rate_limit().search.reset
            print(reset)
            delta = reset - datetime.now()
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

def density(from_size, to_size):
    time.sleep(2)
    query = f'maven-checkstyle-plugin language:Maven POM size:{from_size}..{to_size}'
    print(query)
    count = 0
    done = False
    while not done:
        try:
            codes = g.search_code(query=query) # l=Maven+POM&q=maven-checkstyle-plugin&type=Code
            # In order to get the real totalCount we have to get a page
            if len(codes.get_page(0)):
                count = codes.totalCount
            done = True
        except GithubException as e:
            reset = g.get_rate_limit().search.reset
            print(reset)
            delta = reset - datetime.now()
            sleep_time = delta.seconds % 3600 + 5
            print(f'Sleep for {sleep_time} sec')
            time.sleep(sleep_time)
    print(f'Results in [{from_size}, {to_size}] : {count}')
    if count < 1000 or from_size == to_size:
        return {"from_size": from_size, "to_size": to_size, "count": count}
    else:
        middle = int((from_size + to_size) / 2)
        return [ density(from_size, middle), density(middle + 1, to_size) ]

def flatten(l):
    return flatten(l[0]) + (flatten(l[1:]) if len(l) > 1 else []) if type(l) is list else [l]

def join_intervals(data):
    from_size = None
    to_size = None
    count = 0
    new_data = []
    for index in range(len(data)):
        has_next = index < len(data) - 1
        interval = data[index]
        if has_next:
            next_interval = data[index+1]
        if from_size == None:
            from_size = interval['from_size']
            to_size = interval['to_size']
            count = interval['count']
        else:
            count += interval['count']
            to_size = interval['to_size']
        if not has_next or count + next_interval['count'] > 1000:
            new_data.append({"from_size": from_size, "to_size": to_size, "count": count})
            from_size = None
            to_size = None
            count = 0
    return new_data

def compute_density(from_size, to_size):
    data = join_intervals(flatten(density(from_size, to_size)))
    data_mapped = map(lambda res: f'{res["from_size"]},{res["to_size"]},{res["count"]}', data)
    csv = 'from,to,count\n' + '\n'.join(data_mapped)
    with open('./density.csv', 'w') as f:
        f.write(csv)

def density_data():
    array = [{'from_size': 1500, 'to_size': 1750, 'count': 910}, [[[[[[{'from_size': 1751, 'to_size': 1754, 'count': 81}, [[{'from_size': 1755, 'to_size': 1755, 'count': 2}, {'from_size': 1756, 'to_size': 1756, 'count': 1000}], {'from_size': 1757, 'to_size': 1758, 'count': 0}]], [[{'from_size': 1759, 'to_size': 1760, 'count': 107}, [{'from_size': 1761, 'to_size': 1761, 'count': 1000}, {'from_size': 1762, 'to_size': 1762, 'count': 80}]], {'from_size': 1763, 'to_size': 1766, 'count': 13}]], {'from_size': 1767, 'to_size': 1782, 'count': 129}], [{'from_size': 1783, 'to_size': 1798, 'count': 63}, [{'from_size': 1799, 'to_size': 1806, 'count': 47}, {'from_size': 1807, 'to_size': 1813, 'count': 0}]]], {'from_size': 1814, 'to_size': 1875, 'count': 314}], [{'from_size': 1876, 'to_size': 1938, 'count': 313}, [[{'from_size': 1939, 'to_size': 1954, 'count': 407}, [{'from_size': 1955, 'to_size': 1962, 'count': 27}, [{'from_size': 1963, 'to_size': 1966, 'count': 22}, [[{'from_size': 1967, 'to_size': 1967, 'count': 3}, {'from_size': 1968, 'to_size': 1968, 'count': 0}], {'from_size': 1969, 'to_size': 1969, 'count': 5}]]]], [[[[{'from_size': 1970, 'to_size': 1971, 'count': 11}, [{'from_size': 1972, 'to_size': 1972, 'count': 3}, {'from_size': 1973, 'to_size': 1973, 'count': 1000}]], {'from_size': 1974, 'to_size': 1977, 'count': 23}], {'from_size': 1978, 'to_size': 1985, 'count': 0}], {'from_size': 1986, 'to_size': 2000, 'count': 167}]]]]]

    return flatten(array)

def load_intervals():
    intervals = []
    with open('density.csv', 'r') as csvfile:
        data = csv.reader(csvfile, delimiter=',')
        for row in data:
            if row[0].isnumeric():
                intervals.append((int(row[0]), int(row[1])))
    return intervals

if __name__ == "__main__":
    # repo_list = load_repo_list('repos.txt')
    # for repo in repo_list:
    #     try:
    #         get_travis(repo)
    #     except:
    #         print(f'Error getting {repo}')
    #     time.sleep(5)
    # get_information('Spirals-Team/repairnator')
    # stats(list(set(load_folders('travis.txt')) & set(load_folders('checkstyle.txt'))))
    # find_repos('repos.txt', from=1500, to=1520)
    # compute_density(1000, 2000)
    for interval in load_intervals():
        find_repos('repos.txt', interval[0], interval[1])
