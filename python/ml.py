import java_lang_utils as jlu
import tensorflow as tf
from functools import reduce
import numpy as np
from tensorflow import keras
from javalang import tokenizer
from tqdm import tqdm
import os
import random
import json
import sys
import pprint
import glob
import matplotlib
import java_lang_utils
import shutil
import uuid
from termcolor import colored
matplotlib.use('TkAgg')
import matplotlib.pyplot as plt
from difflib import Differ

pp = pprint.PrettyPrinter(indent=4)

# tf.logging.set_verbosity(tf.logging.INFO)

__synthetic_dir = '/home/benjaminl/Documents/synthetic-checkstyle-error-dataset/dataset'
__protocol = 'protocol1'

def get_dataset_dir(dataset):
    return f'{__synthetic_dir}/{__protocol}/{dataset}'

def get_sub_set_dir(dataset, sub_set):
    return f'{get_dataset_dir(dataset)}/{sub_set}'

def open_file(file):
    content = ''
    with open(file, 'r') as file:
        content = file.read()
    return content

def save_file(dir, file_name, content):
    with open(os.path.join(dir, file_name), 'w') as f:
        f.write(content)

def open_json(file):
    with open(file) as f:
        data = json.load(f)
        return data
    return None

def save_json(dir, file_name, content, sort=False):
    with open(os.path.join(dir, file_name), 'w') as f:
        json.dump(content, f, indent=4, sort_keys=sort)

def create_dir(dir):
    if not os.path.exists(dir):
        os.makedirs(dir)

def list_folders(dir):
    return [ folder for folder in os.listdir(dir) if os.path.isdir(os.path.join(dir, folder)) ]

files = open_file('./ml_files.txt').split('\n')[:100]

def get_token_value(token):
    if isinstance(token, tokenizer.Keyword):
        return token.value
    if isinstance(token, tokenizer.Separator):
        return token.value

    if isinstance(token, tokenizer.Comment):
        return token.__class__.__name__
    if isinstance(token, tokenizer.Literal):
        return token.__class__.__name__
    if isinstance(token, tokenizer.Operator):
        return token.value
        if token.is_infix():
            return "InfixOperator"
        if token.is_prefix():
            return "PrefixOperator"
        if token.is_postfix():
            return "PostfixOperator"
        if token.is_assignment():
            return "AssignmentOperator"

    return token.__class__.__name__

def get_space_value(space):
    if space[0] == 0:
        return f'{space[1]}_SP'
    else:
        result = f'{space[0]}_NL'
        if space[1] == 0:
            pass
        elif space[1] > 0:
            result += f'_{space[1]}_ID'
        else:
            result += f'_{-space[1]}_DD'
        return result

def build_vocabulary(files):
    count = {}
    tokenized_files = [ jlu.tokenize_with_white_space(jlu.open_file(path)) for path in files ]
    whitespace_id = set()

    threshold = 30

    for spaces, tokens in tokenized_files:
        whitespace_id = set(spaces) | whitespace_id
        for token in tokens:
            name = get_token_value(token)
            if not name in count:
                count[name] = 0
            count[name] += 1

    litterals = list(filter(lambda key: count[key] >= threshold, count.keys()))
    litterals = { key:value for key, value in zip(litterals, range(len(litterals))) }

    whitespace_id = { key:value for key, value in zip(whitespace_id, range(len(whitespace_id))) }

    len_litterals = len(litterals)
    len_whitespace = len(whitespace_id)
    vec_size = len_litterals + 1 + len_whitespace

    def get_vector(space, token):
        vector = np.array([0]*vec_size)
        if get_token_value(token) in litterals:
            vector[litterals[get_token_value(token)]] = 1
        else:
            vector[len_litterals] = 1
        vector[len_litterals + 1 + whitespace_id[space]] = 1
        return vector

    print(litterals.keys())

    return get_vector, whitespace_id

def tokenize_errored_file_model2(file, file_orig, error):
    spaces, tokens = jlu.tokenize_with_white_space(jlu.open_file(file))

    info = {}

    token_started = False
    token_line_start = -1
    token_line_end = -1
    count = 0

    tokens_errored = []
    n_lines = 6

    start = len(tokens)
    end = 0

    from_token = 0
    to_token = 0

    for token, space in zip(tokens, spaces):
        if token.position[0] >= int(error['line']) - n_lines and token.position[0] <= int(error['line']) + n_lines :
            start = min(count, start)
            end = max(count, end)
        if not token_started and int(error['line']) == token.position[0]:
            token_started = True
            token_line_start = count
        if token_started and  int(error['line']) < token.position[0]:
            token_started = False
            token_line_end = count
        count += 1
    start = max(0, start - 2)
    end = min(len(tokens), end + 2)
    if token_line_end == -1:
        token_line_end = token_line_start

    if 'column' in error:
        errored_token_index = -1
        around = 5
        for token, index in zip(tokens,range(len(tokens))):
            if token.position[0] <= int(error['line']) and token.position[1] <= int(error['column']):
                errored_token_index = index
        from_token = max(0, errored_token_index - around)
        to_token = min(len(tokens), errored_token_index + 1 + around)
    else:
        around = 3
        errored_token_index = -1
        if token_line_start != -1:
            from_token = max(start, token_line_start - around)
            to_token = min(end, token_line_end + around + 1)
        else:
            errored_token_index = -1
            around = 4
            for token, index in zip(tokens,range(len(tokens))):
                if token.position[0] <= int(error['line']):
                    errored_token_index = index
            from_token = max(0, errored_token_index - around)
            to_token = min(len(tokens), errored_token_index + 1 + around)
    tokens_errored_in_tag = []
    for token, space in zip(tokens[from_token:to_token], spaces[from_token:to_token]):
        tokens_errored_in_tag.append(get_token_value(token))
        tokens_errored_in_tag.append(get_space_value(space))


    for token, space in zip(tokens[start:from_token], spaces[start:from_token]):
        tokens_errored.append(get_token_value(token))
        tokens_errored.append(get_space_value(space))
    tokens_errored.append(f'<{error["type"]}>')
    for token, space in zip(tokens[from_token:to_token], spaces[from_token:to_token]):
        tokens_errored.append(get_token_value(token))
        tokens_errored.append(get_space_value(space))
    tokens_errored.append(f'</{error["type"]}>')
    for token, space in zip(tokens[to_token:end], spaces[to_token:end]):
        tokens_errored.append(get_token_value(token))
        tokens_errored.append(get_space_value(space))
    # else:
    #     for token, space in zip(tokens[start:end], spaces[start:end]):
    #         tokens_errored.append(get_token_value(token))
    #         tokens_errored.append(get_space_value(space))
    #     tokens_errored.append(f'<{error["type"]}>')
    #     tokens_errored.append(f'</{error["type"]}>')


    spaces, tokens = jlu.tokenize_with_white_space(jlu.open_file(file_orig))
    tokens_correct = []

    for token, space in zip(tokens[from_token:to_token], spaces[from_token:to_token]):
        tokens_correct.append(get_token_value(token))
        tokens_correct.append(get_space_value(space))

    if len(tokens_errored_in_tag) != len(tokens_correct):
        print("WHAAAAATT")
    info['count_diff'] = 0
    for t_A, t_B in zip(tokens_errored_in_tag, tokens_correct):
        if t_A != t_B:
            info['count_diff'] += 1

    info['from_token'] = from_token
    info['to_token'] = to_token
    info['start'] = start
    info['end'] = end

    return tokens_errored, tokens_correct, tokens_errored_in_tag, info

def tokenize_errored_file(file, file_orig, error):
    spaces, tokens = jlu.tokenize_with_white_space(jlu.open_file(file))
    token_started = False
    from_token = -1
    to_token = -1
    count = 0
    tokens_errored = []
    n_lines = 5
    for token, space in zip(tokens, spaces):
        if not token_started and int(error['line']) == token.position[0]:
            token_started = True
            tokens_errored.append(f'<{error["type"]}>')
            from_token = count
        if token_started and  int(error['line']) < token.position[0]:
            token_started = False
            tokens_errored.append(f'</{error["type"]}>')
            to_token = count
        if token.position[0] >= int(error['line']) - n_lines and token.position[0] <= int(error['line']) + n_lines :
            tokens_errored.append(get_token_value(token))
            tokens_errored.append(get_space_value(space))
        count += 1
    if from_token == -1:
        tokens_errored.append(f'<{error["type"]}>')
        tokens_errored.append(f'</{error["type"]}>')

    spaces, tokens = jlu.tokenize_with_white_space(jlu.open_file(file_orig))
    tokens_correct = []
    for token, space in zip(tokens[from_token:to_token], spaces[from_token:to_token]):
        tokens_correct.append(get_token_value(token))
        tokens_correct.append(get_space_value(space))
    return tokens_errored, tokens_correct

def whatever(dataset, folder, id):
    dir = f'{get_dataset_dir(dataset)}/{folder}/{id}'
    file_name = [ java_file for java_file in glob.glob(f'{dir}/*.java') if 'orig' not in java_file ][0].split('/')[-1].split('.')[0]
    file = f'{dir}/{file_name}.java'
    file_orig = f'{dir}/{file_name}-orig.java'
    error_file = f'{dir}/metadata.json'
    error = open_json(error_file)
    return tokenize_errored_file_model2(file, file_orig, error)

def merge_IOs(sub_set, ids, target):
    dir = f'{target}/{sub_set}'
    for type in ['I', 'O', 'E']:
        with open(os.path.join(target, f'{sub_set}-{type}.txt'), 'w+') as f:
            for id in tqdm(ids, desc=f'merging {type}s...'):
                f.write(open_file(os.path.join(dir, f'{id}-{type}.txt')))
                f.write('\n')

def get_length_and_vocabulary(folder):
    files = os.listdir(folder)
    Is = [ file for file in files if 'I.txt' in file ]
    Os = [ file for file in files if 'O.txt' in file ]
    max_length = 0
    in_length = []
    out_length = []
    max_out_length = 0
    vocabulary = set()
    for file in tqdm(Is, desc='Is'):
        tokens = open_file(os.path.join(folder, file)).split(' ')
        if len(tokens)<1000:
            in_length.append(len(tokens))
        vocabulary = vocabulary | set(tokens)
    for file in tqdm(Os, desc='Os'):
        tokens = open_file(os.path.join(folder, file)).split(' ')
        out_length.append(len(tokens))
        vocabulary = vocabulary | set(tokens)
    return vocabulary, in_length, out_length

def print_max_length_and_vocabulary(folder):
    vocabulary, in_length, out_length = get_length_and_vocabulary(folder)
    print(vocabulary)
    print(f'Vocabulary size {len(vocabulary)}')
    print(f'Max in lenght : {max(in_length)}')
    print(f'Max out lenght : {max(out_length)}')
    n_bins = 20

    fig, axs = plt.subplots(1, 2, sharey=True, tight_layout=True)

    # We can set the number of bins with the `bins` kwarg
    axs[0].hist(in_length, bins=n_bins)
    axs[1].hist(out_length, bins=n_bins)

    plt.show()

def gen_IO(dataset, target):
    create_dir(target)
    dir = get_dataset_dir(dataset)
    sub_sets = ['learning', 'validation', 'testing']
    diffs = []
    weirdos = []
    for sub_set in sub_sets:
        sub_set_dir = get_sub_set_dir(dataset, sub_set)
        target_sub_set = f'{target}/{sub_set}'
        create_dir(target_sub_set)
        synthesis_error_ids = list_folders(sub_set_dir)
        synthesis_error_ids = sorted(synthesis_error_ids, key=int)
        for id in tqdm(synthesis_error_ids, desc=f'{dataset}/{sub_set}'):
            tokens_errored, tokens_correct, tokens_errored_in_tag, info = whatever(dataset, sub_set, id)
            save_file(target_sub_set, f'{id}-I.txt', " ".join(tokens_errored))
            save_file(target_sub_set, f'{id}-O.txt', " ".join(tokens_correct))
            save_file(target_sub_set, f'{id}-E.txt', " ".join(tokens_errored_in_tag))
            save_json(target_sub_set, f'{id}-info.json', info)
            diffs.append(info['count_diff'])
            if info['count_diff'] == 2:
                weirdos.append(f'{sub_set}/{id}')
        merge_IOs(sub_set, synthesis_error_ids, target)

    # print(weirdos)

def vectorize_file(path, vectorizer):
    spaces, tokens = jlu.tokenize_with_white_space(jlu.open_file(path))

    result = []
    for ws, t in zip(spaces, tokens):
        result.append(vectorizer(ws, t))

    return result

def is_odd(number):
    return number % 2 == 0

def print_diff(stringA, stringB):
    diffs = token_diff(stringA, stringB)
    count = 0
    for token in diffs:
        if token.startswith(' '):
            print(token[2:], end=' ')
            count += 1
        elif token.startswith('-'):
            if is_odd(count):
                print(colored(token[2:], 'blue'), end=' ')
            else:
                print(colored(token[2:], 'green'), end=' ')
            count += 1

    count = 0
    for token in diffs:
        if token.startswith(' '):
            print(token[2:], end=' ')
            count += 1
        elif token.startswith('+'):
            if is_odd(count):
                print(colored(token[2:], 'blue'), end=' ')
            else:
                print(colored(token[2:], 'red'), end=' ')
            count += 1
    print('')

def token_diff(stringA, stringB):
    d = Differ()
    tokensA = stringA.split(' ')
    tokensB = stringB.split(' ')
    result = list(d.compare(tokensA, tokensB))
    return result

def beam_search(target_dir, pred_dir, n=1):
    target_file = open(target_dir, 'r')
    pred_file = open(pred_dir, 'r')
    count = { i:0 for i in range(n) }
    count_whitepace = { i:0 for i in range(n) }
    total = 0
    target = target_file.readline()
    not_predicted = {}
    while target:
        preds = [ pred_file.readline() for i in range(n) ]
        select_whitespace = lambda x: " ".join(x.split(' ')[1::2])
        preds_whitespace = [ select_whitespace(pred) for pred in preds ]
        target_whitespace = select_whitespace(target)
        if target_whitespace in preds_whitespace:
            count_whitepace[preds_whitespace.index(target_whitespace)] += 1
            if preds_whitespace.index(target_whitespace) != 0:
                for pred in preds:
                    print_diff(target, pred)
                print('')
                print('')
        if target in preds:
            count[preds.index(target)] += 1
        else:
            not_predicted[target] = preds
        total += 1
        target = target_file.readline()
    target_file.close()
    pred_file.close()
    pp.pprint({ i:c/total for i,c in count.items() })
    pp.pprint(sum(count.values()) / total)
    pp.pprint({ i:c/total for i,c in count_whitepace.items() })
    pp.pprint(sum(count_whitepace.values()) / total)

    # pp.pprint(not_predicted)

def is_whitespace_token(token: str) -> bool:
    if ( 'SP' in token or 'NL' in token ) and '_' in token:
        return True
    return False

def whitespace_token_to_tuple(token: str) -> tuple:
    spaces: int = 0
    new_line: int = 0
    if 'SP' in token:
        spaces = int(token.split('_')[0])
    elif 'NL' in token:
        new_line = int(token.split('_')[0])
        if 'DD' in token or 'ID' in token:
            spaces = int(token.split('_')[2])
        if 'DD' in token:
            spaces = -spaces

    return (new_line, spaces)

def de_tokenize(errored_source, error_info, new_tokens):
    whitespace, tokens = jlu.tokenize_with_white_space(errored_source)
    from_token = error_info['from_token']
    to_token = error_info['to_token']

    new_white_space_tokens = new_tokens[1::2]
    # print(new_white_space_tokens)
    new_white_space = [ whitespace_token_to_tuple(token) for token in new_white_space_tokens ]
    # print(new_white_space)

    whitespace[from_token:to_token] = new_white_space

    result = jlu.reformat(whitespace, tokens)
    return result

def de_tokenize_file(dataset, n, id):
    # get the tokenization informations
    tokenized_dir = f'/home/benjaminl/Documents/kth/data/2/{dataset}'
    tokenized_testing_dir = f'{tokenized_dir}/testing'
    error_info = open_json(os.path.join(tokenized_testing_dir, f'{id}-info.json'))
    # Get the errored file
    synthetic_dir = os.path.join(get_dataset_dir(dataset), f'testing/{id}')
    errored_file_name = [ file for file in  os.listdir(synthetic_dir) if (file.endswith('.java') and 'orig' not in file ) ][0]
    errored_source = open_file(os.path.join(synthetic_dir, errored_file_name))
    # get the new tokens
    new_tokens_predictions = open_file(os.path.join(tokenized_dir, f'pred_{n}.txt')).split('\n')[(id*n):(id*n + n)]

    tokenized_results = [
        de_tokenize(errored_source, error_info, new_tokens.split(' '))
        for new_tokens in new_tokens_predictions
    ]

    return tokenized_results, errored_file_name

def de_tokenize_dataset(dataset, n):
    target = f'./experiments/ml/{dataset}/styler'
    # for id in [529]:
    for id in tqdm(range(1000)):
        tokenized_results, errored_file_name = de_tokenize_file(dataset, n, id)
        for index in range(n):
            new_file_folder = os.path.join(target, f'batch_{index}/{id}')
            create_dir(new_file_folder)
            save_file(new_file_folder, errored_file_name, tokenized_results[index])
    move_parse_exception_files(target, f'./experiments/ml/{dataset}/bin')

def move_parse_exception_files(from_dir, to_dir):
    files = java_lang_utils.get_bad_formated(from_dir)
    create_dir(to_dir)
    for file in files:
        shutil.move(file, f'{to_dir}/{uuid.uuid4().hex}.java')
    return files

def main(args):
    if len(args) >= 2 and args[1] == 'gen':
        target = '/home/benjaminl/Documents/kth/data/2'
        datasets = args[2:]
        for dataset in datasets:
            gen_IO(dataset, os.path.join(target, dataset))
    if len(args) >= 2 and args[1] == 'info':
        folder = args[2]
        print_max_length_and_vocabulary(folder)
    if args[1] == 'de-tokenize':
        de_tokenize_dataset(args[2], n=5)
    if len(args) == 4 and args[1] == 'beam':
        n = int(args[3])
        dataset = args[2]
        data_folder = f'/home/benjaminl/Documents/kth/data/2/{dataset}'
        pred_path = f'{data_folder}/pred_{n}.txt'
        testing_O_path = f'{data_folder}/testing-O.txt'
        beam_search(testing_O_path, pred_path, n=n)
    if len(args) >= 2 and args[1] == 'test':
        target = '/home/benjaminl/Documents/kth/data/2/spoon'
        sub_set = 'testing'
        target_sub_set = f'{target}/{sub_set}'
        count_diff_size = []
        for id in tqdm(range(1000)):
            tokens_errored, tokens_correct, tokens_errored_in_tag, info = whatever('spoon', 'testing', id)
            save_file('/home/benjaminl/Documents/kth/data/2/spoon/testing', f'{id}-I.txt', " ".join(tokens_errored))
            save_file('/home/benjaminl/Documents/kth/data/2/spoon/testing', f'{id}-O.txt', " ".join(tokens_correct))
            count_diff_size.append(info['count_diff'])
        print(sum([ c == 0 for c in count_diff_size ]))
    if len(args) >= 2 and args[1] == 'test2':
        dataset = 'java-design-patterns'
        target = f'/home/benjaminl/Documents/kth/data/2/{dataset}'
        sub_set = 'testing'
        target_sub_set = f'{target}/{sub_set}'
        id = 529
        tokens_errored, tokens_correct, tokens_errored_in_tag, info = whatever(dataset, sub_set, id)
        save_file(f'{target}/{sub_set}', f'{id}-I.txt', " ".join(tokens_errored))
        save_file(f'{target}/{sub_set}', f'{id}-O.txt', " ".join(tokens_correct))
        save_file(f'{target}/{sub_set}', f'{id}-E.txt', " ".join(tokens_errored_in_tag))

if __name__ == "__main__":
    main(sys.argv)

def old_ml():
    # k = 20
    # vectorizer, whitespace_id = build_vocabulary(files)
    # print(len(whitespace_id))
    # data = []
    # for file in files:
    #     vector = []
    #     vector = vectorize_file(file, vectorizer)
    #     for i in range(k, len(vector) - k, 1):
    #         io = dict()
    #         io['input'] = np.array(vector[i-k:i+k+1]).copy()
    #         for i in range(len(whitespace_id)):
    #             io['input'][k][-1-i] = 0
    #         ws = vector[i][-len(whitespace_id):]
    #         i = 0
    #         j = 0
    #         for a in ws:
    #             if j == 0 and a == 1:
    #                 j = i
    #             i += 1
    #         # print(io['input'].shape)
    #         io['output'] = j
    #         # print(io['input'])
    #         data.append(io)
    # random.shuffle(data)
    # train_len = int(len(data) * 0.8)
    # train_data = data[:train_len]
    # test_data = data[train_len:]
    # print(f'Train files {train_len}')
    #
    # train_input = np.array([d['input'] for d in train_data])
    # train_labels = np.array([d['output'] for d in train_data])
    # #
    # test_input = np.array([d['input'] for d in test_data])
    # test_labels = np.array([d['output'] for d in test_data])
    #
    # print(train_input[0])
    # tf.app.run()
    pass
