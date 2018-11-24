import configparser
from github import Github
from github.GithubException import UnknownObjectException
from github.GithubException import GithubException
from collections import OrderedDict
import time
import datetime
import os
import requests
import json
import xml.etree.ElementTree as ET
from datetime import datetime, timedelta
from functools import reduce
import csv
import random
import sys
import itertools
import glob
from tqdm import tqdm

from matplotlib import pyplot as plt
from matplotlib_venn import venn3

config = configparser.ConfigParser()
config.read('config.ini')

githubs = []

print("Got the keys of : ")
for owner_key in config['DEFAULT']['githubKeys'].split(','):
    owner, key = owner_key.split(':')
    print(owner)
    githubs.append(Github(key));
print("------------")

def g():
    return random.choice(githubs)


def find_repos(file, from_size, to_size):
    query = f'maven-checkstyle-plugin file:pom.xml size:{from_size}..{to_size}'
    codes = g().search_code(query=query) # l=Maven+POM&q=maven-checkstyle-plugin&type=Code
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
            count += 1
            repos.add(repo_name)
            if count >= 30:
                save_repos(file, repos)
                count = 0
                time.sleep(15)
        except GithubException as e:
            print("GithubException")
        except StopIteration:
            print("Done")
            run = False


def save_repos(file, repos):
    sorted_repos = sorted(repos, key=str.lower)
    with open(file ,'w') as f:
        for repo in sorted_repos:
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
        repo = g().get_repo(repo_name)
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
    files_checkstyle = [ file.path for file in g().search_code(query=f'filename:checkstyle.xml repo:{repo_name}') ]
    files_travis = [ file.path for file in g().search_code(query=f'filename:.travis.yml repo:{repo_name}') ]
    files = files_checkstyle + files_travis
    repo_info['files'] = files
    checkstyle_file = ''
    checkstyle_suppressions_file = ''
    travis_file = ''
    for file in files:
        if file.split('/')[-1] == 'checkstyle.xml':
            checkstyle_file = file
        if file.split('/')[-1] == 'checkstyle-suppressions.xml':
            checkstyle_suppressions_file = file

        if file == '.travis.yml':
            travis_file = file

    # Download cs and travis
    if checkstyle_file:
        dowload_and_save(repo, checkstyle_file, base_dir)
    if checkstyle_suppressions_file:
        dowload_and_save(repo, checkstyle_suppressions_file, base_dir)
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

def load_downloaded_repo_list(path):
    repo_list = []
    with open(path, 'r') as file:
        repo_list = file.read().split('\n')
    repo_list = [ "/".join(line.split('/')[2:4]) for line in repo_list ]
    return repo_list;

def open_checkstyle(path):
    content = ''
    with open(path, 'r') as f:
        content = f.read()
    parsed_xml =  ET.fromstring(content)
    return parsed_xml

def open_travis(path):
    content = ''
    with open(path, 'r') as f:
        content = f.read()
    return content

def get_cs_properties(cs):
    return { n.attrib['name']:n.attrib['value'] for n in cs if n.tag == 'property'}

def get_cs_modules(cs):
    keep_n_join = lambda a, l: [a] + [ a + '/' + e for e in l ]
    return flatten([ keep_n_join(n.attrib['name'], get_cs_modules(n)) for n in cs if n.tag == 'module' ])

def load_info(folder):
    with open(os.path.join(folder, 'info.json')) as f:
        data = json.load(f)
    return data

def has_activity(folder):
    info = load_info(folder)
    return info['past_week_commits'] > 0

def has_checkstyle(folder):
    try:
        open_checkstyle(os.path.join(folder, 'checkstyle.xml'))
    except:
        return False
    return True

def has_travis(folder):
    try:
        open_travis(os.path.join(folder, '.travis.yml'))
    except:
        return False
    return True

def stats(folders):
    count_modules = dict()
    count_properties = dict()
    count = 0
    for folder in tqdm(folders):
        info = load_info(folder)
        try:
            cs_rules = open_checkstyle(os.path.join(folder, 'checkstyle.xml'))
        except:
            continue
        cs_properties = get_cs_properties(cs_rules)
        cs_modules = get_cs_modules(cs_rules)
        for module in cs_modules:
            count_modules[module] = count_modules.get(module, 0) + 1
        for key, value in cs_properties.items():
            if key not in count_properties:
                count_properties[key] = {}
            count_properties[key][value] = count_properties[key].get(value, 0) + 1
        # print(cs_properties)
        count+=1

    sanitized_count_modules = {}
    remove_check = lambda x: x if not x.endswith('Check') else x[:-len('Check')]
    for module, count in count_modules.items():
        sanitized_module = "/".join([ remove_check(e.split('.')[-1]) for e in module.split('/') ])
        if sanitized_module not in sanitized_count_modules:
            sanitized_count_modules[sanitized_module] = 0
        sanitized_count_modules[sanitized_module] += count

    # Compute totals of count_properties
    for key, value in count_properties.items():
        count_properties[key]['total'] = sum(value.values())
    return count_properties, sanitized_count_modules

def load_folders(file):
    dirs = []
    with open(file, 'r') as f:
        dirs = f.read().split('\n')
    dirs = [ '/'.join(dir.split('/')[:-1]) for dir in dirs if dir != '']
    return dirs;

def density(from_size, to_size):
    time.sleep(1)
    query = f'maven-checkstyle-plugin file:pom.xml size:{from_size}..{to_size}'
    print(query)
    count = 0
    done = False
    while not done:
        try:
            codes = g().search_code(query=query) # l=Maven+POM&q=maven-checkstyle-plugin&type=Code
            # In order to get the real totalCount we have to get a page
            if len(codes.get_page(0)):
                count = codes.totalCount
            done = True
        except GithubException as e:
            reset = g().get_rate_limit().search.reset
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
    if len(l) == 0:
        return []
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

def load_intervals():
    intervals = []
    with open('density.csv', 'r') as csvfile:
        data = csv.reader(csvfile, delimiter=',')
        for row in data:
            if row[0].isnumeric():
                intervals.append((int(row[0]), int(row[1])))
    return intervals

def findsubsets(S):
    return flatten([ list(itertools.combinations(S,r)) for r in range(0, len(S) + 1) ])

def filters(filters_list, list_to_be_filtered):
    if len(filters_list) == 0:
        return list_to_be_filtered
    else:
        filter_to_apply = filters_list.pop()
        return filters(filters_list, filter(filter_to_apply, list_to_be_filtered))

def venn(sets, repos):
    groups = findsubsets(sets.keys())
    result = dict()
    for group in tqdm(groups, desc='Compute the venn'):
        group_filters = [ sets[key] for key in group ]
        result[group] = len(list(filters(group_filters, repos)))
    return result

def save_json(dir, file_name, content, sort=False):
    with open(os.path.join(dir, file_name), 'w') as f:
        json.dump(content, f, indent=4, sort_keys=sort)

if __name__ == "__main__":
    # I know it's bad...
    os.popen("find . -name 'info.json' > downloaded.txt").read()
    if sys.argv[1] == "dl":
        # Download the repos of the file repos.txt
        if len(sys.argv) >= 3:
            repo_list = sys.argv[2:]
        else:
            repo_list = set(load_repo_list('repos.txt')) - set(load_downloaded_repo_list('downloaded.txt'))
        print(f'{len(repo_list)} repos to download')
        for repo in tqdm(repo_list, desc='Download the repos'):
            try:
                get_information(repo)
            except Exception as e:
                print(f'Error getting {repo}')
                print(e)
    if sys.argv[1] == 'stats':
        repos = load_folders('downloaded.txt')
        count_properties, count_modules = stats(repos)
        save_json('./', 'raw_count_properties.json', count_properties, sort=True)
        count_modules_ordered = OrderedDict(sorted(count_modules.items(), key=lambda k: k[1], reverse=True))
        save_json('./', 'raw_count_modules.json', count_modules_ordered)
    if sys.argv[1] == "venn":
        repos = load_folders('downloaded.txt')
        sets = {'checkstyle': has_checkstyle, 'activity': has_activity, 'travis': has_travis}
        result = venn(sets, repos)
        sets_values = []
        sets_values.append(result[('checkstyle',)])
        sets_values.append(result[('activity',)])
        sets_values.append(result[('checkstyle', 'activity')])
        sets_values.append(result[('travis',)])
        sets_values.append(result[('checkstyle', 'travis')])
        sets_values.append(result[('activity', 'travis')])
        sets_values.append(result[('checkstyle', 'activity', 'travis')])
        sets_labels = ('checkstyle', 'activity', 'travis')
        print(sets_values)
        venn3(subsets = sets_values, set_labels = sets_labels)
        plt.show()
    if sys.argv[1] == "list":
        repos = load_folders('downloaded.txt')
        filtered_repos = map(lambda folder: "/".join(folder.split("/")[2:]), filters([has_checkstyle, has_activity, has_travis],repos))
        print("\n".join(sorted(filtered_repos, key=str.lower)))
    if sys.argv[1] == "update-list":
        # compute_density(0, 100000)
        for interval in load_intervals():
            print(f'get {interval}')
            find_repos('repos.txt', interval[0], interval[1])
