import os
import configparser

from core_real_dataset import *

dir_path = os.path.dirname(os.path.realpath(__file__))
config = configparser.ConfigParser()
config.read(os.path.join(dir_path, "config.ini"))

__real_dataset_dir = config['DEFAULT']['real_dataset_dir']

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

def create_real_error_dataset(target):
    if os.path.exists(target):
        shutil.rmtree(target)
    errors_info = load_errors_info(only_targeted=True)
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
