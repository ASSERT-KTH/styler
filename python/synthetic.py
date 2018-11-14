# -*- coding: utf-8 -*-

import java_lang_utils
import checkstyle
import os
from Corpus import *
import shutil

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
    return get_dir(f'./{repo}')

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
    folder = os.path.join(get_repo_dir(corpus.name), './originals')
    create_dir(folder)
    for id, file in corpus.files.items():
        create_dir(f'{folder}/{id}')
        shutil.copyfile(file[2], f'{folder}/{id}/{file[0]}')
        # print(corpus.files[file])

def gen_errored(corpus, repo_name):
    folder = os.path.join(get_repo_dir(corpus.name), './errored')
    for id, file in corpus.files.items():
        create_dir(f'{folder}/{id}')
        shutil.copyfile(file[2], f'{folder}/{id}/{file[0]}')

def gen_ugly(corpus):
    repo_name = corpus.name
    dir = get_repo_dir(corpus.name)
    create_dir(dir)
    save_json(dir, 'repo.json', corpus.info)
    copy_originals(corpus, repo_name)

if __name__ == '__main__':
    corpora = []
    corpora.append( Corpus("./test_corpora/spoon", "spoon") )
    for corpus in corpora:
        gen_ugly(corpus)
