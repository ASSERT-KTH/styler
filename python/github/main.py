import os
import sys
import configparser
import time
import requests
import json
import itertools
import re
from datetime import datetime, timedelta
from github import Github
from github.GithubException import GithubException
from github.GithubException import UnknownObjectException
from github.GithubException import RateLimitExceededException
from collections import OrderedDict
from tqdm import tqdm
import xml.etree.ElementTree as ET
from matplotlib import pyplot as plt
from matplotlib_venn import venn3

dir_path = os.path.dirname(os.path.realpath(__file__))
config = configparser.ConfigParser()
config.read(os.path.join(dir_path, "config.ini"))

tokens = []
tokenIndex = -1

checkstyle_file_names = ['checkstyle.xml', 'google_checks.xml', 'sun_checks.xml', 'checkstyle-config.xml', 'checkstyle-checker.xml']
checkstyle_file_names_search = ""
for checkstyle_file_name in checkstyle_file_names:
    checkstyle_file_names_search += f'filename:{checkstyle_file_name} '
checkstyle_file_names_search.strip()

def load_tokens():
    for token in config['DEFAULT']['tokens'].split(','):
        tokens.append(Github(token,per_page=100,timeout=60));
    print(f'Got {len(tokens)} GitHub tokens.')

def g():
    global tokenIndex
    if len(tokens) == 1 or (tokenIndex + 1) == len(tokens):
        tokenIndex = 0
    else:
        tokenIndex += 1
    return tokens[tokenIndex]

def sleep(sleep_time):
    print(f'Sleep for {sleep_time} seconds...')
    time.sleep(sleep_time)
    print ("Done waiting - resume.")

def RateLimitExceededException_handler(e, gh):
    print(f'[RateLimitExceededException]: {e}')
    delta = gh.get_rate_limit().search.reset - datetime.now()
    sleep_time = delta.seconds % 3600 + 5
    sleep(sleep_time)    

def GithubException_abuse_handler(e):
    print(f'[GithubException - ABUSE]: {e}')
    sleep_time = 10
    sleep(sleep_time)

def save_repos(file, repos):
    sorted_repos = sorted(repos, key=str.lower)
    with open(file,'w') as f:
        for repo in sorted_repos:
            if repo.strip(): 
                f.write(repo + '\n')

def iterate_repos(repositories, gh):
    repos = set()
    repos = set(load_repo_list(repos_file))
    nb_repos_before = len(repos)
    count = 0
    _iter = repositories.__iter__()
    run = True
    while run:
        try:
            repo = _iter.__next__()
            count += 1            
            repo_name = repo.full_name
            repos.add(repo_name)
            
            with open(repos_raw_file,'a') as f:
                f.write(repo_name + '\n')
        except RateLimitExceededException as e:
            RateLimitExceededException_handler(e, gh)
        except StopIteration:
            save_repos(repos_file, repos)
            nb_repos_after = len(repos)
            nb_addded = nb_repos_after - nb_repos_before
            nb_discarded = count - nb_addded
            print(f'Done: Added: {nb_addded} - Discarded (duplicates): {nb_discarded} - Total: {nb_repos_after}')
            run = False

def search_repos_in_dates(q, from_date, to_date, firstCall=False):
    query = f'{q} created:{from_date}..{to_date}'
    print(f'Query={query}')
    gh = g()    
    count = 0    
    done = False
    while not done:
        try:
            repositories = gh.search_repositories(query=query)
            if len(repositories.get_page(0)):
                count = repositories.totalCount
            if firstCall:
                print(f'Approximate number of repositories to be retrieved: {count}')
            done = True
        except RateLimitExceededException as e:
            RateLimitExceededException_handler(e, gh)
    if count < 1000 or from_date == to_date:
        print(f'Recording {count} repositories...')
        iterate_repos(repositories, gh)
        return {"from_date": from_date, "to_date": to_date, "count": count}
    else:
        middle = from_date + (to_date - from_date)/2
        return [ search_repos_in_dates(q, from_date, middle), search_repos_in_dates(q, middle + timedelta(days=1), to_date) ]

def download_and_save_file(repo, file, repo_dir):
    print(f'Get file ({file}) from {repo.full_name}')
    
    file_name = file.split('/')[-1]
    
    path_to_save = os.path.join(repo_dir, file_name)
    if '/' in file:
        path_to_file = os.path.join(repo_dir, file[:file.rindex('/')])
        if not os.path.exists(path_to_file):        
            os.makedirs(path_to_file)
        path_to_save = os.path.join(repo_dir, path_to_file, file_name)

    contents = repo.get_contents(file)
    request = requests.get(contents.download_url)
    
    with open(path_to_save, 'wb') as f:
        f.write(request.content)

def get_information(repo_name):
    print(f'Open {repo_name}')
    try:
        repo = g().get_repo(repo_name)
    except UnknownObjectException as e:
        print(f'[UnknownObjectException]: {e}')
        return 'Not found'

    repo_dir = f'{repos_folder_path}/{repo_name}'
    if not os.path.exists(repo_dir):
        os.makedirs(repo_dir)
    repo_info = dict()

    # Gather some information
    repo_info['name'] = repo_name
    repo_info['id'] = repo.id
    repo_info['created_at'] = repo.created_at.strftime("%Y-%m-%d %H:%M:%S")
    repo_info['watchers_count'] = repo.watchers_count
    repo_info['stargazers_count'] = repo.stargazers_count
    repo_info['forks_count'] = repo.forks_count
    repo_info['updated_at'] = repo.updated_at.strftime("%Y-%m-%d %H:%M:%S")
    repo_info['pushed_at'] = repo.pushed_at.strftime("%Y-%m-%d %H:%M:%S")
    repo_info['fetched_at'] = datetime.now().strftime("%Y-%m-%d %H:%M:%S")

    # Get interesting files
    files = [ file.path for file in g().search_code(query=f'{checkstyle_file_names_search} filename:pom.xml filename:build.gradle filename:build.xml filename:.travis.yml repo:{repo_name}') ]
    file_circleci = [ file.path for file in g().search_code(query=f'path:.circleci/ filename:config.yml repo:{repo_name}') ]
    files = files + file_circleci
    repo_info['files'] = files
    for file in files:
        cs_file = file.split('/')[-1]
        if cs_file in checkstyle_file_names:
            download_and_save_file(repo, file, repo_dir)

    with open(os.path.join(repo_dir, info_file_name), 'w') as fp:
        json.dump(repo_info, fp)

    return repo_info

def load_repo_list(path):
    repo_list = []
    if os.path.exists(path):
        with open(path, 'r') as file:
            repo_list = file.read().strip().split('\n')
    return repo_list;

def load_downloaded_repo_list(path):
    repo_list = []
    with open(path, 'r') as file:
        repo_list = file.read().split('\n')
    repo_list = [ re.sub("/info.json", "", re.sub(".*/repos/", "", line)) for line in repo_list ]
    return repo_list

def open_checkstyle(path):
    content = ''
    with open(path, 'r') as f:
        content = f.read()
    parsed_xml =  ET.fromstring(content)
    return parsed_xml

def get_cs_properties(cs):
    return { n.attrib['name']:n.attrib['value'] for n in cs if n.tag == 'property'}

def get_cs_modules(cs):
    keep_n_join = lambda a, l: [a] + [ a + '/' + e for e in l ]
    return flatten([ keep_n_join(n.attrib['name'], get_cs_modules(n)) for n in cs if n.tag == 'module' ])

def load_info(folder):
    with open(os.path.join(folder, info_file_name)) as f:
        data = json.load(f)
    return data
    
def has_checkstyle(folder):
    repo_info = load_info(folder)
    for file_path in repo_info['files']:
        file_name = file_path.split('/')[-1]
        if file_name in checkstyle_file_names:        
            return True
    return False

def has_travis(folder):
    repo_info = load_info(folder)
    for file_path in repo_info['files']:
        file_name = file_path.split('/')[-1]
        if file_name == '.travis.yml':        
            return True
    return False
    
def has_circleci(folder):
    repo_info = load_info(folder)
    for file_path in repo_info['files']:
        if file_path.endswith('.circleci/config.yml'):        
            return True
    return False

def stats(folders):
    count_modules = dict()
    count_properties = dict()
    count = 0
    for folder in tqdm(folders):
        info = load_info(folder)
        try:
            for checkstyle_file_name in checkstyle_file_names:
                path = os.path.join(folder, checkstyle_file_name)
                if os.path.exists(path):
                    cs_rules = open_checkstyle(path)
            cs_properties = get_cs_properties(cs_rules)
            cs_modules = get_cs_modules(cs_rules)
            for module in cs_modules:
                count_modules[module] = count_modules.get(module, 0) + 1
            for key, value in cs_properties.items():
                if key not in count_properties:
                    count_properties[key] = {}
                count_properties[key][value] = count_properties[key].get(value, 0) + 1
            # print(cs_properties)
            count += 1
        except:
            continue

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

def flatten(l):
    if len(l) == 0:
        return []
    return flatten(l[0]) + (flatten(l[1:]) if len(l) > 1 else []) if type(l) is list else [l]

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

def save_json(file_name, content, sort=False):
    with open(file_name, 'w') as f:
        json.dump(content, f, indent=4, sort_keys=sort)

result_folder_path = os.path.join(dir_path, 'results')
if not os.path.exists(result_folder_path):
    os.mkdir(result_folder_path)
repos_file = os.path.join(result_folder_path, 'repos.txt')
repos_raw_file = os.path.join(result_folder_path, 'repos_raw.txt')
repos_folder_path = os.path.join(result_folder_path, 'repos')
if not os.path.exists(repos_folder_path):
    os.mkdir(repos_folder_path)
download_file = os.path.join(result_folder_path, 'downloaded.txt')
info_file_name = 'info.json'
properties_file = os.path.join(result_folder_path, 'raw_count_properties.json')
modules_file = os.path.join(result_folder_path, 'raw_count_modules.json')

if __name__ == "__main__":
    print(f'Beginning: {datetime.now()}')

    if sys.argv[1] == "search-repos":
        load_tokens()

        try:        
            from_date = datetime.strptime(config['DEFAULT']['from_date'],"%Y/%m/%d").date()
            to_date = datetime.strptime(config['DEFAULT']['to_date'],"%Y/%m/%d").date()
        except:
            print("Provide dates (from_date and to_date) in the config.ini file following the format YYYY/MM/DD.")
            sys.exit()

        query_general_search = config['DEFAULT']['query_general_search']
        if query_general_search:
            query = f'{query_general_search} created:{from_date}..{to_date}'
            print(f'Query={query}')
            gh = g()    
            done = False
            while not done:
                try:
                    repositories = gh.search_repositories(query=query)
                    if len(repositories.get_page(0)):
                        count = repositories.totalCount
                    print(f'Approximate number of repositories: {count}')
                    done = True
                except RateLimitExceededException as e:
                    RateLimitExceededException_handler(e, gh)

        query_detailed_search = config['DEFAULT']['query_detailed_search']
        if query_detailed_search:
            search_repos_in_dates(query_detailed_search, from_date, to_date, firstCall=True)
        else:
            print("Provide a query for detailed search (query_detailed_search) in the config.ini file.")
            sys.exit()

    os.popen(f'find {repos_folder_path} -name \'{info_file_name}\' > {download_file}').read()
    if sys.argv[1] == "download":
        load_tokens()

        repo_list = set(load_repo_list(repos_file)) - set(load_downloaded_repo_list(download_file))
        print(f'{len(repo_list)} repos to download...')
        for repo in tqdm(repo_list, desc='Download the repos'):
            done = False
            while not done:
                try:
                    get_information(repo)
                    done = True
                except RateLimitExceededException as e:
                    RateLimitExceededException_handler(e, tokens[tokenIndex])
                except GithubException as e:
                    if e.status == 403: # abuse-rate-limits
                        GithubException_abuse_handler(e)
                    else:
                        print(f'[GithubException]: {e}')
                        done = True

    if sys.argv[1] == 'stats':
        repos = load_folders(download_file)
        count_properties, count_modules = stats(repos)
        save_json(properties_file, count_properties, sort=True)
        count_modules_ordered = OrderedDict(sorted(count_modules.items(), key=lambda k: k[1], reverse=True))
        save_json(modules_file, count_modules_ordered)

    if sys.argv[1] == "venn":
        repos = load_folders(download_file)
        sets = {'checkstyle': has_checkstyle, 'travis': has_travis, 'circleci': has_circleci}
        result = venn(sets, repos)
        sets_values = []
        sets_values.append(result[('checkstyle',)])
        sets_values.append(result[('travis',)])
        sets_values.append(result[('checkstyle', 'travis')])
        sets_values.append(result[('circleci',)])
        sets_values.append(result[('checkstyle', 'circleci')])
        sets_values.append(result[('travis', 'circleci')])
        sets_values.append(result[('checkstyle', 'travis', 'circleci')])
        sets_labels = ('checkstyle', 'travis', 'circleci')
        venn3(subsets = sets_values, set_labels = sets_labels)
        plt.show()

    if sys.argv[1] == "list":
        repos = load_folders(download_file)
        filtered_repos = map(lambda folder: "/".join(folder.split("/")[2:]), filters([has_checkstyle, has_travis], repos))
        print("\n".join(sorted(filtered_repos, key=str.lower)))

    print(f'End: {datetime.now()}')
