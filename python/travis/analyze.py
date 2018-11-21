# -*- coding: utf-8 -*-

import tarfile
import os
from main import *
from functools import reduce
import atexit
import subprocess
import glob
import pprint
from tqdm import tqdm
from tqdm import trange
import git
from git import Repo

_OSS_dir = '/home/benjaminl/Documents/dataset-travis-log-oss'
__git_repo_dir = '/home/benjaminl/Documents/kth/repos'

pp = pprint.PrettyPrinter(indent=4)

opened_builds = {}

known_check = (
'UnusedImports',
'RedundantImport',
'WhitespaceAround',
'RegexpSinglelineJava',
'AnnotationLocation',
'AvoidStaticImport',
'JavadocMethod',
'FileTabCharacter',
'RegexpSingleline',
'JavadocStyle',
'Indentation',
'AvoidStarImport',
'ParenPad',
'RedundantModifier',
'CustomImportOrder',
'LineLength',
'NamePattern',
'JavadocMethod',
'MagicNumber',
'ParameterNumber',
'FinalLocalVariable',
'Rcurly',
'Lcurly',
'MultipleStringLiterals',
'NewlineAtEndOfFile',
'EmptyBlock',
'VisibilityModifier',
'ArrayTypeStyle')

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

def error_check_parser(error):
    check = ''

    error = error.lower()

    if 'unused import' in error or 'import.unused' in error:
        check = 'UnusedImports'
    elif 'RedundantImport' in error:
        check = 'RedundantImport'
    elif 'is not followed by whitespace' in error or 'is not preceded with whitespace' in error or 'whitespacearound' in error:
        check = 'WhitespaceAround' # after or before
    elif 'must use tab characters' in error or 'has leading space characters' in error or 'tabs only.' in error:
        check = 'RegexpSinglelineJava'
    elif 'annotation' in error and 'should be' in error:
        check = 'AnnotationLocation'
    elif 'a static member import should be avoided' in error:
        check = 'AvoidStaticImport'
    elif 'Expected' in error and 'tag' in error or 'expected @param tag' in error:
        check = 'JavadocMethod'
    elif 'contains' in error and 'tab' in error:
        check = 'FileTabCharacter'
    elif 'has trailing spaces' in error or 'trailing whitespace' in error:
        check = 'RegexpSingleline'
    elif 'should end with a period' in error or 'html tag' in error:
        check = 'JavadocStyle'
    elif 'incorrect indentation' in error:
        check = 'Indentation'
    elif 'form of import' in error and 'avoided' in error and '*' in error:
        check = 'AvoidStarImport'
    elif 'is followed by whitespace' in error:
        check = 'ParenPad'
    elif 'redundant' in error and 'modifier' in error:
        check = 'RedundantModifier'
    elif 'wrong' in error and 'order' in error:
        check = 'CustomImportOrder'
    elif 'is longer than' in error:
        check = 'LineLength'
    elif 'name' in error and 'must match pattern' in error:
        check = 'NamePattern'
    elif 'missing' in error and 'javadoc' in error:
        check = 'JavadocMethod'
    elif 'magic number' in error:
        check = 'MagicNumber'
    elif 'more than' in error and 'parameters' in error:
        check = 'ParameterNumber'
    elif 'should be final' in error or 'localvariablecouldbefinal' in error:
        check = 'FinalLocalVariable'
    elif '}' in error and 'should be' in error:
        check = 'Rcurly'
    elif '{' in error and 'should' in error:
        check = 'Lcurly'
    elif 'the string' in error and 'appears' in error and 'times' in error:
        check = 'MultipleStringLiterals'
    elif 'file does not end with a newline' in error:
        check = 'NewlineAtEndOfFile'
    elif 'must have at least one statement' in error:
        check = 'EmptyBlock'
    elif 'variable' in error and 'must be' in error and 'accessor' in error:
        check = 'VisibilityModifier'
    elif 'array brackets at illegal position' in error:
        check = 'ArrayTypeStyle'

    # print(check)
    return check

checks_messages = dict()

def parse_cs_error(plain_error):
    # CustomImportOrder, Indentation, RegexpSingleline, WhitespaceAfter, WhitespaceAfter, UnusedImports
    (file, error) = (None, None)
    check = ''
    try:
        if ': warning:' in plain_error:
            type = 'warning'
            (file, error) = plain_error.split(f': {type}:')
            check = error_check_parser(error)
        elif ': error:' in plain_error:
            type = 'error'
            (file, error) = plain_error.split(f': {type}:')
            # print(error)
            check = error_check_parser(error)
        elif '[ERROR]' in plain_error:
            type = 'error'
            (file, error) = plain_error.split(': ')[:2]
            check = error[error.rfind('[')+1:-1]
            if not check in checks_messages:
                checks_messages[check] = []
            checks_messages[check].append(error)
        elif '[WARNING]' in plain_error:
            type = 'error'
            (file, error) = plain_error.split(': ')[:2]
            check = error[error.rfind('[')+1:-1]
            if not check in checks_messages:
                checks_messages[check] = []
            checks_messages[check].append(error)
        else:
            type = 'ukn'
            (file, error) = plain_error.split(': ')[:2]
            if not error.find('[') is -1:
                check = error[error.rfind('[')+1:-1]
                if not check in checks_messages:
                    checks_messages[check] = []
                checks_messages[check].append(error)
            else:
                check = error_check_parser(error)
    except:
        return None

    if check not in known_check:
        check = error_check_parser(check)

    return {'type': type, 'plain_text': plain_error, 'file': file, 'error': error, 'check': check}

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
    return plain_text_cs_errors

def analyse_repo(repo):

    builds_id = get_builds_id(repo)
    result = {}
    count = 0
    for build_id in tqdm(builds_id):
        open_build(repo, build_id)
        logs_id = get_logs_id(repo, build_id)
        cs_errors = []
        for log_id in logs_id:
            cs_errors = find_cs_errors(get_logs(repo, build_id, log_id))
        not_none = lambda x: x is not None
        cs_errors = list(filter(not_none, [parse_cs_error(line) for line in set(cs_errors)]))
        result[build_id] = cs_errors
        count += len(cs_errors)
        close_build(repo, build_id)
    print(f'Found {count} cs errors/warnings in {repo}.')
    return result

def count_type(array):
    res = {'error': 0, 'warning': 0, 'ukn': 0}
    for e in array:
        res[e['type']] += 1
    return res

def analyse_builds(builds):
    cs_errors = []
    build_with_errors = []
    error_type_count = {'error': 0, 'warning': 0, 'ukn': 0}
    check_type_count = {}
    errored_build_ids = []

    for build_key in sorted(builds.keys()):
        errors_not_parsed = builds[build_key]
        n_errors = 0
        errors = [ parse_cs_error(error['plain_text']) for error in errors_not_parsed]
        if len(errors) > 0:
            errors_in_the_log = [ error['plain_text'] for error in errors ]
            for error in errors:
                error_type_count[error['type']] += 1
                if error['check'] != '':
                    check_type_count[error['check']] = check_type_count.get(error['check'], 0) + 1
            cs_errors += errors_in_the_log
            n_errors += len(errors_in_the_log)
        build_with_errors.append(n_errors)
        if n_errors:
            errored_build_ids.append(build_key)

    result = {}
    result['number_of_builds'] = len(builds)
    result['number_of_build_with_errors'] = sum(build_with_errors)
    result['number_of_errors'] = len(cs_errors)
    result['number_of_unique_errors'] = len(set(cs_errors))
    def density_char(a):
        if a==0:
            return '_'
        elif a < 0.25:
            return chr(9617) # 9618
        elif a < 0.5:
            return chr(9618) # 9618
        elif a < 0.75:
            return chr(9619) # 9618
        else:
            return chr(9608)  # █ = chr(9608)
    max_errors = max(build_with_errors)
    result['build_with_errors'] = ''.join([ density_char(build / max_errors) for build in build_with_errors ])
    result['max_errors_in_a_single_build'] = max_errors
    result['error_type_count'] = error_type_count
    result['check_type_count'] = check_type_count
    result['errored_build_ids'] = errored_build_ids
    return result

def print_res(res):
    synthesis = get_synthesis(res)
    pp.pprint(synthesis)
    # pp.pprint(set(checks_messages['CustomImportOrder']))
    # print([ key for key, value in checks_messages.items() if len(value) > 5])


def get_synthesis(res):
    repos = res.keys()

    def has_errors(repo):
        builds = res[repo]
        for errors in builds.values():
            if len(errors) > 0:
                return True
        return False

    repo_with_errors = { repo:builds for repo, builds in res.items() if has_errors(repo) }
    print(f'Found {len(repo_with_errors)} repos with cs errors in the logs.')
    synthesis_repo = { repo:analyse_builds(builds) for repo, builds in repo_with_errors.items()}
    synthesis = {'repo': synthesis_repo}
    synthesis['number_of_unique_errors'] = reduce(lambda acc, cur: acc + cur['number_of_unique_errors'], synthesis_repo.values(), 0)

    return synthesis

    # with_errors = { key:count_type(item) for key, item in res.items() if len(item) > 0 }
    # print(with_errors)
    # unique_errors = list(set(reduce(lambda acc, cur: acc + [ i['error'] for i in cur ], res.values(), [])))
    # print('\n'.join(unique_errors))
    # print(f'{len(unique_errors)} unique errors')
    # repos_len = len(get_repo_names())
    # print(f'{len(with_errors)}/{repos_len}')

def get_oss_dir(dir):
    return os.path.join(_OSS_dir, dir)

def get_oss_repo_dir(repo):
    return get_oss_dir(f'./java_logs/{repo}')

def get_oss_repo_names():
    return [ repo for repo in os.listdir(get_oss_dir('java_logs')) if os.path.isdir(get_oss_repo_dir(repo)) ]

def get_oss_builds_id(repo):
    return list(set([ log_file.split('/')[-1].split('_')[0] for log_file in os.listdir(get_oss_repo_dir(repo)) ]))

def get_oss_logs_id(repo, build):
    return [ log_file.split('/')[-1].split('_')[1].split('.')[0] for log_file in glob.glob(f'{get_oss_repo_dir(repo)}/{build}_*.txt') ]

def get_oss_logfile_path(repo, build, log):
    return f'{get_oss_repo_dir(repo)}/{build}_{log}.txt'

def get_oss_logs(repo, build, log):
    log_path = get_oss_logfile_path(repo, build, log)
    logs = []
    with open(log_path, 'r') as file:
        logs = file.read().split('\n')
    return logs

def analyse_oss_repo(repo):
    builds_id = get_oss_builds_id(repo)
    result = {}
    count = 0
    for build_id in tqdm(builds_id):
        logs_id = get_oss_logs_id(repo, build_id)
        cs_errors = []
        for log_id in logs_id:
            cs_errors = find_cs_errors(get_oss_logs(repo, build_id, log_id))
        not_none = lambda x: x is not None
        cs_errors = list(filter(not_none, [parse_cs_error(line) for line in set(cs_errors)]))
        result[build_id] = cs_errors
        count += len(cs_errors)
    print(f'Found {count} cs errors/warnings in {repo}.')
    return result

def get_build_commits(res):
    synthesis = get_synthesis(res)
    result = {}
    for repo in tqdm(synthesis['repo'].keys(), desc='Repos'):
        build_ids = synthesis['repo'][repo]['errored_build_ids']
        result[repo] = {}
        for build_id in tqdm(build_ids, desc=repo):
            result[repo][build_id] = get_build(build_id)['commit']
    return result

def clone_repo(user, repo_name):
    dir = get_repo_dir(user, repo_name)
    print(f'Clonning {user}/{repo_name}')
    return Repo.clone_from(f'git@github.com:{user}/{repo_name}.git', dir)

def get_repo_dir(user, repo_name):
    return os.path.join(__git_repo_dir, f'./{user}/{repo_name}')

def open_repo(user, repo_name):
    dir = get_repo_dir(user, repo_name)
    if os.path.exists(dir):
        return Repo(dir)
    else:
        return clone_repo(user, repo_name)

def find_all(path, name):
    result = []
    for root, dirs, files in os.walk(path):
        if name in files:
            result.append(os.path.join(root, name))
    return result

def find_the_pom(path):
    return sorted(find_all(path, 'pom.xml'), key=lambda x: x.count('/'))[0] # the main pom should be closser to the root

def maven_checkstyle(repo, commit):
    repo.git.checkout(commit)
    dir = repo.working_dir
    pom = find_the_pom(dir)
    print(pom)
    cmd = f'mvn -f {pom} checkstyle:checkstyle'
    # print(cmd)
    process = subprocess.Popen(cmd.split(" "), stdout=subprocess.PIPE)
    output = process.communicate()[0]
    return output

def has_commit(repo, sha):
    return sha in [ str(c) for c in repo.iter_commits() ]

def find_commits(commits_data):
    result = {}
    for repo_full_name, commits in tqdm(commits_data.items()):
        user, repo_name = repo_full_name.split('_')
        repo = open_repo(user, repo_name)
        result[repo_full_name] = {'found': 0}
        result[repo_full_name]['total'] = len(commits)
        for commit in commits.values():
            result[repo_full_name]['found'] += has_commit(repo, commit)
    return result

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
        print_res(res)
    if len(sys.argv) >= 2 and sys.argv[1] == 'run-oss':
        repos = get_oss_repo_names()
        print(f'Found {len(repos)} repos')
        # repos = ['Spirals-Team/repairnator', 'googleapis/google-oauth-java-client']
        res = {}
        try:
            for repo in repos:
                print(f'Analyse {repo}, with {len(get_oss_builds_id(repo))} builds')
                res[repo] = analyse_oss_repo(repo)
            save_json('./', 'results_oss.json', res)
        except Exception as e:
            print('somethig went wrong')
            print(e)
        except KeyboardInterrupt:
            print('ctrl-c')
        print_res(res)
    elif len(sys.argv) >= 2 and sys.argv[1] == 'list':
        repos = sorted(get_repo_names(min_size=1), key=str.lower)
        if len(sys.argv) >= 3 and sys.argv[2] == 'csv':
            out = 'repo,count'
            repo_with_count = { repo:number_of_builds(repo) for repo in repos }
            ordered_res = [ (key, str(repo_with_count[key])) for key in sorted(repo_with_count, key=repo_with_count.get) ]
            out += '\n'.join([ ','.join(line) for line in  ordered_res])
        else:
            out = '\n'.join(repos)
        save_file('./', 'list.txt', out)
    elif len(sys.argv) >= 2 and sys.argv[1] == 'utf-8':
        res = open_json('./results.json')
        repos = res.keys()
        print(f'Found {len(repos)} repos')
        synthesis = get_synthesis(res)
        plots = [ f'{repo:40s}{synthesis[repo]["build_with_errors"]}' for repo in synthesis.keys()]
        print('\n'.join(plots))
    elif len(sys.argv) >= 2 and sys.argv[1] == 'res-oss':
        res = open_json('./results_oss.json')
        repos = res.keys()
        print(f'Found {len(repos)} repos')
        print_res(res)
    elif len(sys.argv) >= 2 and sys.argv[1] == 'github-commits':
        # square_picasso : ['17673347', '26883826']
        res = open_json('./results_oss.json')
        commits = get_build_commits(res)
        pp.pprint(commits)
        save_json('./', 'commits.json', commits)
    elif len(sys.argv) >= 2 and sys.argv[1] == 'clone':
        repo = open_repo('google', 'auto')
        maven_checkstyle(repo, 'eb0bafd6c00069fee58f5cb513dc73f1754bd02d')
        # commits_data = open_json('./commits.json')
        # reduced_commits_data = { key:commits_data[key] for key in ['facebook_presto', 'square_okhttp']}
        # find_commits(reduced_commits_data)
    else:
        res = open_json('./results.json')
        repos = res.keys()
        print(f'Found {len(repos)} repos')
        print_res(res)


@atexit.register
def clean_up():
    print('Close repos')
    for repo, builds in opened_builds.items():
        for build in builds:
            close_build(repo, build)
