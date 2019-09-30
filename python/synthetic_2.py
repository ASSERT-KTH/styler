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
        corpus_name = sys.argv[2]
        file_id = sys.argv[2]
        corpus = Corpus(config['CORPUS']['corpus_dir'] % corpus_name, corpus_name)
        
