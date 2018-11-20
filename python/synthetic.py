# -*- coding: utf-8 -*-

import java_lang_utils as jlu
import checkstyle
import subprocess
import os
from Corpus import *
import shutil
import random
import configparser
import sys
import glob
from tqdm import tqdm
from functools import reduce

config = configparser.ConfigParser()
config.read('config.ini')

class Checkstyle_Synthetic_Error:

    def __init__(self, repo, id):
        self.dir = get_synthetic_error_dir(repo, id)
        self.metadata = None
        self.original = None
        self.errored = None
        self.file_name = [ java_file for java_file in glob.glob(f'{self.dir}/*.java') if 'orig' not in java_file ][0].split('/')[-1].split('.')[0]

    def get_metadata(self):
        if not self.metadata:
            self.load_metadata()
        return self.metadata

    def get_original(self):
        if not self.original:
            self.load_original()
        return self.original

    def get_errored(self):
        if not self.errored:
            self.load_errored()
        return self.errored

    def load_metadata(self):
        self.metadata = open_json(f'{self.dir}/metadata.json')

    def load_original(self):
        self.original = open_file(f'{self.dir}/{self.file_name}-orig.java')

    def load_errored(self):
        self.errored = open_file(f'{self.dir}/{self.file_name}.java')

__base_dir = config['DEFAULT']['SYNTHETIC_DIR']

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
    return get_dir(f'./dataset/protocol1/{repo}')

def get_synthetic_error_dir(repo, id):
    return get_dir(f'{get_repo_dir(repo)}/{id}')

def list_elements(repo):
    dir = get_repo_dir(repo)
    return [ element for element in os.listdir(dir) if os.path.isdir(get_synthetic_error_dir(repo, element)) ]

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

def run_diff(fileA, fileB):
    cmd = f'diff {fileA} {fileB}'
    # print(cmd)
    process = subprocess.Popen(cmd.split(" "), stdout=subprocess.PIPE)
    output = process.communicate()[0]
    return output.decode("utf-8")

def gen_get_random_file(corpus, numbers):
    files = list(corpus.files.values())
    corpus_size = len(files)
    shuffle_list = random.sample(list(range(corpus_size)), corpus_size) # random.shuffled() shuffle the list and does not returned the shuffled list
    print(shuffle_list)
    total_numbers = sum(numbers.values())
    values = {}
    for goal in numbers.keys():
        to = round(numbers[goal]/total_numbers*corpus_size)
        values[goal] = shuffle_list[:to]
        shuffle_list = shuffle_list[to:]
    print(values)
    def get_file(goal):
        return files[random.choice(values[goal])]
    return get_file

def gen_errored(corpus, get_random_corpus_file, repo_name, goal, id):
    folder = os.path.join(get_repo_dir(repo_name), f'./{goal}/{id}')
    file =  get_random_corpus_file(goal)
    file_dir = file[2]
    file_name = file[0].split('.')[0]
    done = False
    error = None
    ugly_file = ""
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

    original_file = os.path.join(folder, f'./{file_name}-orig.java')
    shutil.copyfile(file_dir, original_file)
    save_file(folder, 'diff.diff', run_diff(original_file, ugly_file))

    report = {}
    report['injection_operator'] = injection_operator
    report['line'] = error['line']
    if 'column' in error:
        report['column'] = error['column']
    report['message'] = error['message']
    report['type'] = error['source'].split('.')[-1][:-5]

    save_json(folder, 'metadata.json', report)


def gen_dataset(corpus, numbers):
    repo_name = corpus.name
    dir = get_repo_dir(repo_name)
    if os.path.exists(dir):
        shutil.rmtree(dir)
    create_dir(dir)
    save_json(dir, 'repo.json', corpus.info)
    get_random_corpus_file = gen_get_random_file(corpus, numbers)
    shutil.copyfile(corpus.checkstyle, os.path.join(dir, f'./checkstyle.xml'))
    for goal, number in numbers.items():
        for i in tqdm(range(number), desc=f'{repo_name}/{goal}'):
            gen_errored(corpus, get_random_corpus_file, repo_name, goal, i)
    # copy_originals(corpus, repo_name)

def map_and_count(reducer, data):
    result = {}
    for element in map(reducer, data):
        if element not in result:
            result[element] = 0
        result[element] += 1
    return result


def load_repo(repo):
    return [ Checkstyle_Synthetic_Error('spoon', synthetic_error) for synthetic_error in list_elements(repo) ]

def summary(repo):
    synthetic_errors = load_repo(repo)
    results = {}
    results['type_count'] = map_and_count(lambda x: x.get_metadata()['type'], synthetic_errors)
    results['operator_count'] = map_and_count(lambda x: x.get_metadata()['injection_operator'], synthetic_errors)
    results['file_count'] = map_and_count(lambda x: x.file_name, synthetic_errors)
    print(results)
    save_json(get_repo_dir(repo), 'stats.json', results)

if __name__ == '__main__':
    if len(sys.argv) >= 2 and sys.argv[1] == 'run':
        corpora = []
        for corpus in sys.argv[2:]:
            corpora.append( Corpus(config['CORPUS'][corpus], corpus) )
        share = { key:config['DATASHARE'].getint(key) for key in ['learning', 'validation', 'testing'] }
        for corpus in corpora:
            gen_dataset(corpus, share)
    if len(sys.argv) >= 2 and sys.argv[1] == 'analyse':
        summary('spoon')
