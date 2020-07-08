#!/usr/bin/python

import os
import sys
import configparser
import glob

from termcolor import colored

dir_path = os.path.dirname(os.path.realpath(__file__))
sys.path.append(os.path.dirname(os.path.dirname(dir_path)))

from core import *

config = configparser.ConfigParser()
config.read(os.path.join(dir_path, "config.ini"))

__checkstylerr_checkedout_projects_dir = config['DEFAULT']['checkstylerr_checkedout_projects_dir']
__dataset_dir = config['DEFAULT']['dataset_dir']

def load_errors_info(only_targeted=False):
    def filepath_from_json_path(x):
        return safe_get_first(glob.glob(pathname=f'{x.rpartition("/")[0]}/*.java'))
    error_json_path = glob.glob(pathname=f'{__checkstylerr_checkedout_projects_dir}/*/*/*/*.json')
    errors_info = unique([
        {
            'repo': path_splitted[-4],
            'commit': path_splitted[-3],
            'id': int(path_splitted[-2]),
            'errors': run_if_true(filter_targeted_error, only_targeted, open_json(path)),
            'only_targeted': only_targeted,
            'filepath': filepath_from_json_path(path),
            'hash': if_not_null(if_not_null(filepath_from_json_path(path),open_file), hash)
        }
        for path_splitted, path in tqdm(zip(map(lambda x: x.split('/'), error_json_path), error_json_path), desc='reading', total=len(error_json_path))
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

def create_dataset():
    if os.path.exists(__dataset_dir):
        shutil.rmtree(__dataset_dir)
    errors_info = load_errors_info(only_targeted=False)
    dataset = structure_real_error_dataset(errors_info)
    for project, number_of_errors_per_file in dataset.items():
        for number_of_errors, file_list in number_of_errors_per_file.items():
            if number_of_errors == 1:
                id = 0
                for file_info in file_list:
                    if file_info['errors'][0]['source'].split('.')[-1][:-5] in targeted_errors:
                        dir = os.path.join(__dataset_dir, f'{project}/{number_of_errors}/{id}')
                        metadata = {
                            'commit': file_info['commit'],
                            'file_name': file_info['filepath'].split('/')[-1],
                            'errors': file_info['errors']
                        }
                        create_dir(dir)
                        save_json(dir, 'metadata.json', metadata)
                        shutil.copy(file_info['filepath'], os.path.join(dir,metadata['file_name']))
                        id += 1
        shutil.copy(os.path.join(__checkstylerr_checkedout_projects_dir, f'{project}', 'checkstyle.xml'), os.path.join(__dataset_dir, f'{project}', 'checkstyle.xml'))
        shutil.copy(os.path.join(__checkstylerr_checkedout_projects_dir, f'{project}', 'info.json'), os.path.join(__dataset_dir, f'{project}', 'info.json'))

def checkstylerr_stats():
    loaded_errors_info = load_errors_info(only_targeted=True)

    for repo, errors_info in group_by(lambda e: e['repo'], loaded_errors_info).items():
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
                        print(colored(f'{error:<30} : {count}', color='green'))
                    else:
                        print(f'{error:<30} : {count}')

def checkstylerr_stats_customized():
    loaded_errors_info = load_errors_info(only_targeted=False)
    
    repos = []
    for repo_name, repo_errors_info in group_by(lambda e: e['repo'], loaded_errors_info).items():
        for size, errors_info in sorted(group_by(lambda e: len(e['errors']), repo_errors_info).items()):
            if size == 1:
                repo_errors = [
                    error['source'].split('.')[-1][:-5]
                    for info in errors_info
                    for error in info['errors']
                ]
                targeted_count = 0
                only_targeted_errors = {}
                for error, count in dict_count(repo_errors).items():
                    if error in targeted_errors:
                        targeted_count += count
                        only_targeted_errors[error] = count
                if targeted_count >= 20:
                    repos.append({
                        "repo_name": repo_name,
                        "nb_errors": targeted_count,
                        "nb_error_types": len(only_targeted_errors.items()),
                        "errors": only_targeted_errors
                    })
                break
    
    for repo in sorted(repos, key=lambda item: (item['nb_error_types'], item['nb_errors']), reverse=True):
        repo_name = repo['repo_name']
        repo_nb_errors = repo['nb_errors']
        print(colored(f'{repo_name}: {repo_nb_errors:.0f} errors', attrs=['bold']))
        for error, count in sorted(repo['errors'].items(), key=lambda item: item[1], reverse=True):
            print('\t\t', end='')
            print(colored(f'{error:<30}\t{count}', color='green'))

    print(f'Number of projects: {len(repos)}')

if __name__ == '__main__':
    if sys.argv[1] == 'show-checkstylerr-stats':
        checkstylerr_stats()
    if sys.argv[1] == 'show-checkstylerr-stats-customized':
        checkstylerr_stats_customized()
    if sys.argv[1] == 'create-dataset':
        create_dataset()
