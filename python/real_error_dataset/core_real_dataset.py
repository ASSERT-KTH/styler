#!/usr/bin/python

import os
import sys
import configparser
import glob

dir_path = os.path.dirname(os.path.realpath(__file__))
sys.path.append(os.path.dirname(dir_path))
from core import *

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
