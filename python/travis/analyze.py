import tarfile
import os
from main import *
from functools import reduce
import atexit
import glob

opened_builds = {}

def get_logs(repo, build, job):
    build_folder = get_build_dir(repo, build)
    if not build_is_open(repo, build):
        open_build(repo, build)
    log_path =  get_dir(f'./{repo}/{build}/{job}/log.txt')
    logs = []
    with open(log_path, 'r') as file:
        logs = file.read().split('\n')
    return logs

def build_is_open(repo, build):
    return os.path.exists(get_build_dir(repo, build))

def open_build(repo, build):
    file = get_dir(f'./{repo}/{build}.tar.bz')
    target = get_build_dir(repo, build)
    if repo not in opened_builds:
        opened_builds[repo] = []
    opened_builds[repo] += [build]
    create_dir(target)
    with tarfile.open(file, 'r:*') as tar_handle:
        tar_handle.extractall(path=target)

def close_build(repo, build):
    delete_dir(get_build_dir(repo, build))
    opened_builds[repo].remove(build)

def get_logs_id(repo, build):
    if not build_is_open(repo, build):
        open_build(repo, build)
    return [ tar.split('/')[-2] for tar in glob.glob(f'{get_build_dir(repo, build)}/*/log.txt') ]

def parse_cs_error(plain_error):
    (file, error) = (None, None)
    if ': warning:' in plain_error:
        type = 'warning'
        (file, error) = plain_error.split(f': {type}:')
    elif ': error:' in plain_error:
        type = 'error'
        (file, error) = plain_error.split(f': {type}:')
    elif '[ERROR]' in plain_error:
        type = 'error'
        (file, error) = plain_error.split(': ')[:2]
    elif '[WARNING]' in plain_error:
        type = 'error'
        (file, error) = plain_error.split(': ')[:2]
    else:
        type = 'ukn'
        (file, error) = plain_error.split(': ')[:2]


    return {'type': type, 'plain_text': plain_error, 'file': file, 'error': error}

def find_cs_errors(logs):
    prev_line_maven_cs = False
    in_cs_audit = False
    plain_text_cs_errors = []
    for log in logs:
        if '--- maven-checkstyle-plugin:' in log:
            prev_line_maven_cs = True
        else:
            if in_cs_audit and 'Audit done.' in log:
                in_cs_audit = False
            if in_cs_audit:
                plain_text_cs_errors.append(log)
            if prev_line_maven_cs and 'Starting audit...' in log:
                in_cs_audit = True
            prev_line_maven_cs = False
    return [parse_cs_error(line) for line in plain_text_cs_errors]

def analyse_repo(repo):

    builds_id = get_builds_id(repo)
    cs_errors = []
    for build_id in builds_id:
        open_build(repo, build_id)
        logs_id = get_logs_id(repo, build_id)
        for log_id in logs_id:
            cs_errors += find_cs_errors(get_logs(repo, build_id, log_id))
        close_build(repo, build_id)
    print(f'Found {len(cs_errors)} cs errors/warnings in {repo}.')
    return cs_errors

def count_type(array):
    res = {'error': 0, 'warning': 0, 'ukn': 0}
    for e in array:
        res[e['type']] += 1
    return res

def print_res(repos, res):
    with_errors = { key:count_type(item) for key, item in res.items() if len(item) > 0 }
    print(with_errors)
    unique_errors = list(set(reduce(lambda acc, cur: acc + [ i['error'] for i in cur ], res.values(), [])))
    print('\n'.join(unique_errors))
    print(f'{len(unique_errors)} unique errors')
    repos_len = len(get_repo_names())
    print(f'{len(with_errors)}/{repos_len}')

if __name__ == '__main__':
    if len(sys.argv) >= 2 and sys.argv[1] == 'run':
        repos = get_repo_names()
        print(f'Found {len(repos)} repos')
        # repos = ['Spirals-Team/repairnator', 'googleapis/google-oauth-java-client']
        res = {}
        try:
            for repo in repos:
                print(f'Analyse {repo}, with {number_of_builds(repo)} builds')
                res[repo] = analyse_repo(repo)
            save_json('./', 'results.json', res)
        except Exception as e:
            print('somethig went wrong')
            print(e)
        except KeyboardInterrupt:
            print('ctrl-c')
        print_res(repos,res)
    elif len(sys.argv) >= 2 and sys.argv[1] == 'list':
        repos = get_repo_names()
        print('repo,count')
        for repo in repos:
            print(f'{repo},{number_of_builds(repo)}')
    else:
        res = open_json('./results.json')
        print_res(repos,res)


@atexit.register
def clean_up():
    print('Close repos')
    for repo, builds in opened_builds.items():
        for build in builds:
            close_build(repo, build)
