# -*- coding: utf-8 -*-

import java_lang_utils as jlu
import checkstyle
import os
from Corpus import *
import shutil
import random

__base_dir = "/home/benjaminl/Documents/synthetic-checkstyle-error-dataset"

# def gen_ugly(corpus):
#     modifications = {}
#     for id, file in corpus.get_files().items():
#         if id not in modifications:
#             modifications[id] = {}
#         modifications[id][folder] = {}
#         for index in range(n):
#             modifications[id][folder][index] = java_lang_utils.gen_ugly( file[2], self.get_dir( os.path.join("./ugly/" + str(id) + "/{}/".format(folder) + str(index) + "/")), action)

def get_dir(dir):
    return os.path.join(__base_dir, dir)

def get_repo_dir(repo):
    return get_dir(f'./dataset/{repo}')

def save_file(dir, file_name, content):
    with open(os.path.join(dir, file_name), 'w') as f:
        f.write(content)

def save_json(dir, file_name, content):
    with open(os.path.join(dir, file_name), 'w') as f:
        json.dump(content, f)

def open_file(file):
    content = ''
    with open(file, 'r') as file:
        content = file.read()
    return content

def open_json(file):
    with open(file) as f:
        data = json.load(f)
        return data
    return None

def create_dir(dir):
    if not os.path.exists(dir):
        os.makedirs(dir)

def copy_originals(corpus, repo_name):
    folder = os.path.join(get_repo_dir(repo_name), './originals')
    create_dir(folder)
    for id, file in corpus.files.items():
        create_dir(f'{folder}/{id}')
        shutil.copyfile(file[2], f'{folder}/{id}/{file[0]}')
        # print(corpus.files[file])

injection_operator_types={
'insertion-space': (1,0,0,0,0),
'insertion-tab': (0,1,0,0,0),
'insertion-newline': (0,0,1,0,0),
'deletion-space': (0,0,0,1,0),
'deletion-newline': (0,0,0,0,1)
}

def gen_errored(corpus, repo_name, id):
    folder = os.path.join(get_repo_dir(repo_name), f'./{id}')
    file =  random.choice(list(corpus.files.values()))
    file_dir = file[2]
    file_name = file[0].split('.')[0]
    done = False
    error = None
    while not done:
        if os.path.exists(folder):
            shutil.rmtree(folder)
        create_dir(folder)
        injection_operator = random.choice(list(injection_operator_types.keys()))
        ugly_file = os.path.join(folder, f'./{file_name}.java')
        modification = jlu.gen_ugly(file_dir, folder, modification_number=injection_operator_types[injection_operator])
        # print(modification)
        if not jlu.check_well_formed(ugly_file):
            continue
        cs_result, number_of_errors = checkstyle.check(corpus.checkstyle, ugly_file)
        if number_of_errors != 1:
            continue
        error = list(cs_result.values())[0]['errors'][0]
        done = True

    shutil.copyfile(file_dir, os.path.join(folder, f'./{file_name}-orig.java'))

    report = {}
    report['injection_operator'] = injection_operator
    report['line'] = error['line']
    if 'column' in error:
        report['column'] = error['column']
    report['message'] = error['message']
    report['type'] = error['source'].split('.')[-1][:-5]

    save_json(folder, 'metadata.json', report)


def gen_ugly(corpus, number):
    repo_name = corpus.name
    dir = get_repo_dir(repo_name)
    shutil.rmtree(dir)
    create_dir(dir)
    save_json(dir, 'repo.json', corpus.info)
    shutil.copyfile(corpus.checkstyle, os.path.join(dir, f'./checkstyle.xml'))
    for i in range(number):
        gen_errored(corpus, repo_name, i)
    # copy_originals(corpus, repo_name)

if __name__ == '__main__':
    corpora = []
    corpora.append( Corpus("./test_corpora/spoon", "spoon") )
    for corpus in corpora:
        gen_ugly(corpus, 10000)
