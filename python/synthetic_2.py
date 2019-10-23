# -*- coding: utf-8 -*-

from core import *
from Corpus import *
import java_lang_utils as jlu
import sys
from ml import get_token_value, get_space_value
from collections import Counter
from functools import reduce
from tqdm import tqdm
import csv
import uuid
import checkstyle
import shutil
import pandas as pd
from token_utils import whitespace_token_to_tuple
import random

from loguru import logger

BATCH_SIZE = 500

config = configparser.ConfigParser()
config.read('config.ini')


def word_counter_to_csv(counter):
    # field names
    fields = ['token_1', 'ws', 'token_2', 'count']

    # data rows of csv file
    rows = [
        [*three_gram, c]
        for three_gram, c in counter.items()
    ]
    filename = "three_grams.csv"

    with open(filename, 'w') as csvfile:
        csvwriter = csv.writer(csvfile)

        csvwriter.writerow(fields)

        csvwriter.writerows(rows)


def tokenize_and_count(file_path):
    tokens = jlu.tokenize_with_white_space(open_file(file_path))
    tokenized_file = []
    for token, ws in zip(map(get_token_value, tokens[1]), map(get_space_value, tokens[0])):
        tokenized_file += [token, ws]
    tokenized_file += ['EOF']
    three_grams = []
    for i in range(0, len(tokenized_file) - 2, 2):
        three_grams.append(tuple(tokenized_file[i: i+3]))
    counter = Counter(three_grams)
    return counter



df = pd.read_csv('./three_grams.csv')
count_df = df.loc[:,['token_1', 'token_2', 'count']].groupby(['token_1', 'token_2']).agg(['count', 'sum'])
count = count_df['count']['count']
good_for_insertion = list(count[count>=2].index)
good_for_insertion_index = []
for index, (token_1, token_2) in df.loc[:,['token_1', 'token_2']].iterrows():
    if (token_1, token_2) in good_for_insertion:
        good_for_insertion_index.append(index)
good_for_insertion_df = df.iloc[good_for_insertion_index ,:]
good_for_insertion_set = set(
    good_for_insertion
)


def is_good_for_insertion(token_a, token_b):
    return (token_a, token_b) in good_for_insertion_set


def list_alternatives_with_probability(token_a, ws, token_b):
    df_token_1 = good_for_insertion_df[good_for_insertion_df.token_1 == token_a]
    df_token_1_and_2 = df_token_1[df_token_1.token_2 == token_b]
    final = df_token_1_and_2[df_token_1_and_2.ws != ws]
    total = final['count'].sum()
    return {
        ws:count/total
        for _, (_, ws, _, count) in final.iterrows()
    }


tokenizer = jlu.Tokenizer()
tokenizer_absolute = jlu.Tokenizer(relative=False)

def pick_random(alternatives):
    random_number = random.random()
    probability_sum = 0
    for alternative, probability in alternatives.items():
        probability_sum += probability
        if random_number <= probability_sum:
            return alternative
    return None


def get_line_indent(line):
    indent = 0
    for c in line:
        if c == ' ':
            indent+=1
        else:
            return indent


def modify_source_three_grams(source, n_insertion=1):
    tokenized_source = tokenizer.tokenize(source)
    tokenized_source_absolute = tokenizer_absolute.tokenize(source)
    insertion_spots = list(range(len(tokenized_source.tokens)-1))
    random.shuffle(insertion_spots)
    modification = None
    for spot in insertion_spots:
        token_a = tokenized_source.tokens[spot]
        token_b = tokenized_source.tokens[spot+1]
        ws = tokenized_source.white_spaces[spot]
        if is_good_for_insertion(get_token_value(token_a), get_token_value(token_b)):
            alternatives = list_alternatives_with_probability(
                get_token_value(token_a),
                get_space_value(ws),
                get_token_value(token_b)
            )
            alternative_selected = pick_random(alternatives)
            alternative_selected_tuple = whitespace_token_to_tuple(alternative_selected)
            new_ws = tokenized_source_absolute.white_spaces[spot]
            if alternative_selected_tuple[0] == 0:
                new_ws = alternative_selected_tuple
            elif alternative_selected_tuple[0] != 0 and new_ws[0] == 0:
                line = token_a.position[0]
                indent = get_line_indent(source.split('\n')[line-1])
                new_ws = (alternative_selected_tuple[0], indent + alternative_selected_tuple[1])
            else:
                new_ws = (alternative_selected_tuple[0], new_ws[1] + (alternative_selected_tuple[1] - ws[1]))
            if new_ws[1]>=0:
                tokenized_source_absolute.white_spaces[spot] = new_ws
                modification = {
                    'token_a': get_token_value(token_a),
                    'token_b': get_token_value(token_b),
                    'modification': (get_space_value(ws), alternative_selected),
                    'position': spot
                }
                break
    return tokenized_source_absolute.reformat(), modification


injection_operator_types={
    'insertion-space': (1,0,0,0,0),
    'insertion-tab': (0,1,0,0,0),
    'insertion-newline': (0,0,1,0,0),
    'deletion-space': (0,0,0,1,0),
    'deletion-newline': (0,0,0,0,1)
}
injection_operator_pool = [
    'insertion-space',
    'insertion-tab',
    'insertion-newline',
    'deletion-space',
    'deletion-newline',
    'insertion-space',
    'insertion-newline',
    'deletion-space',
    'deletion-newline',
    'insertion-space',
    'insertion-newline',
    'deletion-space',
    'deletion-newline'
]


class InsertionException(Exception):
    pass


def modify_source_random(source):
    if not jlu.check_source_well_formed(source):
        raise InsertionException
    while True:
        injection_operation = random.choice(injection_operator_pool)
        ugly_content, modification = jlu.gen_ugly_from_source(source, modification_number=injection_operator_types[injection_operation])
        if not jlu.check_source_well_formed(ugly_content):
            logger.debug('Not well formed')
            continue
        spaces_original, tokens_original = jlu.tokenize_with_white_space(source)
        spaces_errored, tokens_errored = jlu.tokenize_with_white_space(ugly_content)
        if len(tokens_original) != len(tokens_errored):
            logger.debug('Not same length')
            continue
        return ugly_content, (modification, injection_operation)


def modify_source(source, protocol='random'):
    if protocol == 'random':
        return modify_source_random(source)
    elif protocol == 'three_grams':
        return modify_source_three_grams(source)
    return modify_source_random(source)


def diff(file_A, file_B, unified=True):
    if unified:
        cmd = 'diff -u {} {}'.format(file_A, file_B)
    else:
        cmd = 'diff {} {}'.format(file_A, file_B)
    process = subprocess.Popen(cmd.split(" "), stdout=subprocess.PIPE)
    output = process.communicate()[0]
    return output.decode("utf-8")


share = {
    'learning': 0.8,
    'validation': 0.1,
    'testing': 0.1
}

def split_files(corpus, share, random_state=100):
    files_list = [file for (_,_,file) in corpus.files.values()]
    n_files = len(files_list)
    random.seed(random_state)
    random.shuffle(files_list)
    c_learning = int(share['learning']*n_files)
    c_validation = int(share['validation']*n_files)
    return {
        'learning': files_list[:c_learning],
        'validation': files_list[c_learning: c_learning+c_validation],
        'testing': files_list[c_learning+c_validation:]
    }

class Batch:
    def __init__(self, files_dir, checkstyle_dir, batch_id=None, protocol='random'):
        self.checkstyle_dir = checkstyle_dir
        if batch_id == None:
            self.batch_id = uuid.uuid4().hex
        else:
            self.batch_id = batch_id
        self.batch_files = [random.choice(files_dir) for _ in range(BATCH_SIZE)]
        self.batch_dir = f'./tmp/batches/{self.batch_id}'
        self.protocol = protocol
    
    def gen(self):
        create_dir(self.batch_dir)
        self.batch_injections = {}
        for index, file_dir in tqdm(enumerate(self.batch_files), total=BATCH_SIZE):
            file_name = file_dir.split('/')[-1]
            original_source = open_file(file_dir)
            try:
                modified_source, modification = modify_source(original_source, protocol=self.protocol)
            except InsertionException:
                logger.debug(InsertionException)
                continue
            modification_folder = os.path.join(self.batch_dir, str(index))
            create_dir(modification_folder)
            modified_file_dir = save_file(modification_folder, file_name, modified_source)
            
            diff_str = diff(file_dir, modified_file_dir)
            diff_path = save_file(modification_folder, 'diff.diff', diff_str)
            self.batch_injections[index] = {
                'modification': modification,
                'diff': diff_str,
                'dir': modification_folder,
                'orig': file_dir,
                'file_name': file_name
            }
        self.checkstyle_result, _ = checkstyle.check(
            self.checkstyle_dir,
            self.batch_dir,
            only_java=True,
            only_targeted=True
        )
        for file_dir, res in self.checkstyle_result.items():
            index = int(file_dir.split('/')[-2])
            self.batch_injections[index]['errors'] = res['errors']
            save_json(self.batch_injections[index]['dir'], 'errors.json', res['errors'])
        self.batch_information = {
            'batch_id': self.batch_id,
            'injection_report': self.batch_injections
        }
        save_json(self.batch_dir, 'metadata.json', self.batch_information)
        return self.batch_information
    
    def clean(self):
        shutil.rmtree(self.batch_dir)


def gen_errors(files_dir, checkstyle_dir, target, number_of_errors, protocol='random'):
    valid_errors = []
    batches = []
    with tqdm(total=number_of_errors) as pbar:
        while len(valid_errors) < number_of_errors:
            batch = Batch(files_dir, checkstyle_dir, protocol=protocol)
            batches.append(batch)
            try:
                batch_res = batch.gen()
            except KeyboardInterrupt:
                raise KeyboardInterrupt
            except UnicodeDecodeError:
                continue
            except: # UnicodeEncodeError
                logger.exception("Something went whrong")
                continue
            batch_valid_errors = [
                info
                for info in batch_res['injection_report'].values() 
                if len(info['errors']) == 1
            ]
            valid_errors += batch_valid_errors
            pbar.update(len(batch_valid_errors))
    selected_errors = random.sample(valid_errors, number_of_errors)
    for error_id, error_metadata in enumerate(selected_errors):
        new_error_dir = os.path.join(target, str(error_id))
        old_dir = error_metadata['dir']
        shutil.move(old_dir, new_error_dir)
        error_metadata['dir'] = new_error_dir
        error_metadata['error'] = error_metadata['errors'][0]
        error_metadata['error']['type'] = checkstyle_source_to_error_type(error_metadata['error']['source'])
        
        file_name = error_metadata['file_name']
        orig_file_name = '.'.join(file_name.split('.')[:-1]) + '-orig.java'
        shutil.copy(error_metadata['orig'], os.path.join(new_error_dir, orig_file_name))
        
        save_json(new_error_dir, 'metadata.json', error_metadata)
    for batch in batches:
        batch.clean()
    return selected_errors


def gen_dataset(corpus, share, total, target, protocol='random'):
    splitted_files = split_files(corpus, share)
    for name, subset_share in share.items():
        gen_errors(splitted_files[name], corpus.checkstyle, os.path.join(target, name), int(subset_share*total), protocol=protocol)


if __name__ == '__main__':
    if sys.argv[2] == 'all':
        dataset_list = config['CORPUS']['corpus_names'].split(',')
    else:
        dataset_list = sys.argv[2:]

    if len(sys.argv) >= 2 and sys.argv[1] == 'run':
        corpora = map(
            lambda corpus: Corpus(config['CORPUS']['corpus_dir'] % corpus, corpus),
            dataset_list
        )
        c = Counter()
        for corpus in tqdm(corpora, total=len(dataset_list)):
            files_path = [file[2] for file in corpus.get_files().values()]
            for file_path in tqdm(files_path):
                c += tokenize_and_count(file_path)
        word_counter_to_csv(c)
    elif sys.argv[1] == 'gen':
        protocol = sys.argv[2]
        corpus_name = sys.argv[3]
        if protocol not in (('random', 'three_grams')):
            raise Exception('Unkown protocol')
        corpus = Corpus(config['CORPUS']['corpus_dir'] % corpus_name, corpus_name)
        gen_dataset(corpus, share, 500, f'./tmp/dataset/{protocol}/{corpus_name}', protocol=protocol)