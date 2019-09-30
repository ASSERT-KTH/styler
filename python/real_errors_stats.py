#!/usr/bin/python

import os
import json

import configparser
import glob

from termcolor import colored

from core import *

dir_path = os.path.dirname(os.path.realpath(__file__))
config = configparser.ConfigParser()
config.read(os.path.join(dir_path, "config.ini"))

__real_errors_dir = config['DEFAULT']['real_errors_dir']
                        
def load_errors_info(only_targeted=False):
    def filepath_from_json_path(x):
        return safe_get_first(glob.glob(pathname=f'{x.rpartition("/")[0]}/*.java'))
    error_json_path = glob.glob(pathname=f'{__real_errors_dir}/*/*/*/*.json')
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
    
def real_errors_stats():
    errors_info = load_errors_info(only_targeted=True)
    # print(errors_info)

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
            
real_errors_stats()
