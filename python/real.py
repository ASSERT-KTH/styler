from tqdm import tqdm
import git
from git import Repo
import shutil
import os
import subprocess
import sys
import configparser
import checkstyle
import glob
from termcolor import colored
import git_helper

from core import *

config = configparser.ConfigParser()
config.read('config.ini')

_OSS_dir = config['DEFAULT']['OSS_dir']
__git_repo_dir = config['DEFAULT']['git_repo_dir']
__real_errors_dir = config['DEFAULT']['real_errors_dir']
__real_dataset_dir = config['DEFAULT']['real_dataset_dir']


def get_real_errors_repo_dir(repo):
    return os.path.join(__real_errors_dir, repo)


def get_real_errors_commit_dir(repo, commit):
    return os.path.join(get_real_errors_repo_dir(repo), commit)


def real_errors_stats():
    errors_info = load_errors_info()
    print(errors_info)

    for repo, errors_info in group_by(lambda e: e['repo'], errors_info).items():
        errors = [
            error['source'].split('.')[-1][:-5]
            for info in errors_info
            if len(info['errors']) <= 10
            for error in info['errors']
        ]
        print(colored(f'{repo} : {len(errors)} errors', attrs=['bold']))
        for size, errors_info in sorted(group_by(lambda e: len(e['errors']), errors_info).items()):
            errors = [
                error['source'].split('.')[-1][:-5]
                for info in errors_info
                if len(info['errors']) <= 10
                for error in info['errors']
            ]
            if len(errors) > 0:
                print(colored(f'\t{size} errors per file ({len(errors)/size:.0f} file(s))', attrs=['bold']))

                for error, count in dict_count(errors).items():
                    print('\t\t', end='')
                    if error in targeted_errors:
                        if error not in corner_cases_errors:
                            print(colored(f'{error:<30} : {count}', color='green'))
                        else:
                            print(colored(f'{error:<30} : {count}', color='yellow'))
                    else:
                        print(f'{error:<30} : {count}')


def load_errors_info():
    def filepath_from_json_path(x):
        return safe_get_first(glob.glob(pathname=f'{x.rpartition("/")[0]}/*.java'))
    error_json_path = glob.glob(pathname=f'{__real_errors_dir}/*/*/*/*.json')
    errors_info = unique([
        {
            'repo': path_splitted[-4],
            'commit': path_splitted[-3],
            'id': int(path_splitted[-2]),
            'errors': open_json(path),
            'filepath': filepath_from_json_path(path),
            'hash': if_not_null(if_not_null(filepath_from_json_path(path),open_file), hash)
        }
        for path_splitted, path in zip(map(lambda x: x.split('/'), error_json_path), error_json_path)
    ], lambda obj: obj['hash'])
    return errors_info


def structure_real_error_dataset(errors_info):
    dataset = {}
    for error in errors_info:
        if error['repo'] not in dataset:
            dataset[error['repo']] = {}
        n_errors = len(error['errors'])
        if n_errors not in dataset[error['repo']]:
            dataset[error['repo']][n_errors] = []
        dataset[error['repo']][n_errors] += [error]

    return dataset


def find_the_pom(path):
    return safe_get_first(sorted(find_all(path, 'pom.xml'), key=lambda x: x.count('/'))) # the main pom should be closser to the root


def find_the_checkstyle(repo):
    path = repo.working_dir
    checkstyle_path = safe_get_first(sorted(find_all(path, 'checkstyle.xml'), key=lambda x: x.count('/')))
    if checkstyle_path:
        return checkstyle_path, '.' + checkstyle_path[len(path):]
    else:
        return None, None


def check_checkstyle_results(checkstyle_results):
    reports_with_errors = {}
    for id, results in enumerate(checkstyle_results):
        files_with_errors = { file:result['errors'] for file, result in results.items() if len(result['errors']) and file.endswith('.java')}
        if len(files_with_errors):
            reports_with_errors[id] = files_with_errors
    return reports_with_errors


def find_errored_files(repo, commit, use_maven=False, checkstyle_path='checkstyle.xml'):
    # print(f'{repo}/{commit}')
    repo.git.checkout(commit)
    dir = repo.working_dir

    # clean_up
    checkstyle_result_xml = find_all(dir, 'checkstyle-result.xml')
    for file in checkstyle_result_xml:
        os.remove(file)

    repo_name = dir.split('/')[-1]
    pom = find_the_pom(dir)
    if use_maven:
        cmd = f'mvn -f {pom} clean checkstyle:checkstyle'
        process = subprocess.Popen(cmd.split(" "), stdout=subprocess.PIPE)
        output = process.communicate()[0]
        checkstyle_results = [ checkstyle.parse_res(open_file(file)) for file in find_all(dir, 'checkstyle-result.xml') ]
    else:
        # checkstyle_relative_dir = find_all(dir, checkstyle_path)
        checkstyle_dir = os.path.join(dir, checkstyle_path)
        if not os.path.isfile(checkstyle_dir):
            checkstyle_results = []
        else :
            output, returncode = checkstyle.check(checkstyle_file_path=checkstyle_dir, file_path=dir)
            checkstyle_results = [output]


    reports_with_errors = check_checkstyle_results(checkstyle_results)

    count = 0
    target = get_real_errors_commit_dir(repo_name, commit)
    for report_dir, results in reports_with_errors.items():
        # checkstyle_checker = f'{"/".join(report_dir.split("/")[:-1])}/checkstyle-checker.xml'
        for file, errors in results.items():
            # print(f'{file} has {len(errors)} errors')
            file_name = file.split('/')[-1]
            dir = os.path.join(target, str(count))
            create_dir(dir)
            shutil.copyfile(file, os.path.join(dir, file_name))
            # shutil.copyfile(checkstyle_checker, os.path.join(dir, 'checkstyle.xml'))
            save_json(dir, 'errors.json', errors)
            count += 1
    find_all(dir, 'checkstyle-checker.xml')
    return output


def create_real_error_dataset(target):
    if os.path.exists(target):
        shutil.rmtree(target)
    errors_info = load_errors_info()
    dataset = structure_real_error_dataset(errors_info)
    # pp.pprint(dataset)
    for project, number_of_errors_per_file in dataset.items():
        for number_of_errors, file_list in number_of_errors_per_file.items():
            for id, file_info in enumerate(file_list):
                dir = os.path.join(target, f'{project}/{number_of_errors}/{id}')
                metadata = {
                    'commit': file_info['commit'],
                    'file_name': file_info['filepath'].split('/')[-1],
                    'errors': file_info['errors']
                }
                create_dir(dir)
                save_json(dir, 'metadata.json', metadata)
                shutil.copy(file_info['filepath'], os.path.join(dir,metadata['file_name']))


def get_repo_with_checkstyle(repos):
    result = []
    for repo_full_name in repos:
        user, repo_name = repo_full_name.split('/')
        repo = git_helper.open_repo(user, repo_name)
        checkstyle_absolute, checkstyle_relative = find_the_checkstyle(repo)
        if not checkstyle_absolute:
            continue
        if sanitize_checkstyle(open_file(checkstyle_absolute)):
            pom_line = open_file(find_the_pom(repo.working_dir)).split('\n')
            good = True
            for l in pom_line:
                if 'suppressionsLocation' in l:
                    good = False
            if good:
                result.append({
                    'repo': repo,
                    'checkstyle': checkstyle_absolute,
                    'checkstyle_relative': checkstyle_relative,
                    'repo_full_name': repo_full_name
                })
    return result


def sanitize_checkstyle(content):
    lines = content.split('\n')
    for line in lines:
        line_stripped: str = line.strip()
        if line_stripped.startswith('<!--') and line_stripped.endswith('-->'):
            continue
        if '${' in line:
            return False
        if 'suppressions' in line:
            return False
    return True


def clone():
    repos = list(open_json('./travis/commits.json').keys()) + list(open_json('./travis/commits_oss.json').keys())
    for info in tqdm(get_repo_with_checkstyle(repos), desc='Total'):
        repo = info['repo']
        checkstyle_path = info['checkstyle']
        repo_full_name = info['repo_full_name']
        user, repo_name = repo_full_name.split('/')
        valid_commits = shuffled(commit_until_last_modification(repo, checkstyle_path))
        try:
            for commit in tqdm(valid_commits, desc=f'{user}/{repo_name}'):
                find_errored_files(repo, commit, use_maven=False, checkstyle_path=checkstyle_path)
        except:
            print(f'did not complet the error collection of {repo_full_name}')
        # print(f'{repo_name} {}')


def find_commits(commits_data):
    result = {}
    for repo_full_name, commits in tqdm(commits_data.items()):
        user, repo_name = repo_full_name.split('/')
        repo = git_helper.open_repo(user, repo_name)
        result[repo_full_name] = []
        for commit in commits.values():
            if git_helper.has_commit(repo, commit):
                result[repo_full_name].append(commit)
    return result

def clone_old():
    commits_data = open_json('./commits.json')
    reduced_commits_data = { key:commits_data[key] for key in commits_data if key in ['DevelopmentOnTheEdge/be5']} # { key:commits_data[key] for key in commits_data if len(commits_data[key]) >= 10 } # 'facebook_presto',
    commits = find_commits(reduced_commits_data)
    repo_information = {}
    repo_information['facebook/presto'] = {
        'use_maven': False,
        'checkstyle_path': './src/checkstyle/checks.xml'
    }
    repo_information['vorburger/MariaDB4j'] = {
        'use_maven': False,
        'checkstyle_path': '../checkstyle.xml'
    }
    repo_information['google/auto'] = {
        'use_maven': False,
        'checkstyle_path': '../checkstyle.xml'
    }
    repo_information['DevelopmentOnTheEdge/be5'] = {
        'use_maven': False,
        'checkstyle_path': './checkstyle.xml'
    }
    commits['DevelopmentOnTheEdge/be5'] = shuffled(commit_until_last_modification(repo, repo_information[repo_full_name]['checkstyle_path']))
    for repo_full_name, valid_commits in commits.items():
        user, repo_name = repo_full_name.split('/')
        repo = git_helper.open_repo(user, repo_name)
        for commit in tqdm(valid_commits, desc=f'{user}/{repo_name}'):
            if not repo_information[repo_full_name]['use_maven']:
                find_errored_files(repo, commit, use_maven=False, checkstyle_path=repo_information[repo_full_name]['checkstyle_path'])
            else:
                find_errored_files(repo, commit, use_maven=True)

def commit_until_last_modification(repo, file):
    result = []
    wd = os.getcwd()
    try:
        repo.git.checkout('master')
        os.chdir(repo.git.rev_parse("--show-toplevel"))
        cmd_commit_list = 'git log --pretty=format:%H'
        cmd_commit_checkstyle = f'git log -n 1 --pretty=format:%H -- {file}'
        p1 = subprocess.Popen(cmd_commit_list.split(' '), stdout=subprocess.PIPE)
        commit_list_array = p1.communicate()[0].decode("utf-8")
        p2 = subprocess.Popen(cmd_commit_checkstyle.split(' '), stdout=subprocess.PIPE)
        commit_checkstyle = p2.communicate()[0].decode("utf-8")
        repo.git.checkout('master')
        commit_list = commit_list_array.split('\n')
        result = commit_list[:commit_list.index(commit_checkstyle)] + [commit_checkstyle]
    except:
        pass
    os.chdir(wd)
    return result


def main(args):
    if len(args) >= 2 and args[1] == 'clone':
        clone()
    elif len(args) >= 2 and args[1] == 'real-errors-stats':
        real_errors_stats()
    elif len(args) >= 2 and args[1] == 'copy-real-dataset':
        create_real_error_dataset(__real_dataset_dir)


if __name__ == "__main__":
    main(sys.argv)
