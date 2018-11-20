import java_lang_utils as jlu
import tensorflow as tf
import numpy as np
from tensorflow import keras
from javalang import tokenizer
import random
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense, Dropout, Activation, Flatten
from tensorflow.keras.layers import Conv2D, MaxPooling2D

# tf.logging.set_verbosity(tf.logging.INFO)

def open_file(file):
    content = ''
    with open(file, 'r') as file:
        content = file.read()
    return content

files = open_file('./ml_files.txt').split('\n')[:100]

def build_vocabulary(files):
    count = {}
    tokenized_files = [ jlu.tokenize_with_white_space(jlu.open_file(path)) for path in files ]
    whitespace_id = set()

    threshold = 30

    def get_value(token):
        if isinstance(token, tokenizer.Comment):
            return token.__class__.__name__
        if isinstance(token, tokenizer.Literal):
            return token.__class__.__name__
        if isinstance(token, tokenizer.Operator):
            if token.is_infix():
                return "InfixOperator"
            if token.is_prefix():
                return "PrefixOperator"
            if token.is_postfix():
                return "PostfixOperator"
            if token.is_assignment():
                return "AssignmentOperator"

        return token.__class__.__name__

    for spaces, tokens in tokenized_files:
        whitespace_id = set(spaces) | whitespace_id
        for token in tokens:
            name = get_value(token)
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
        if get_value(token) in litterals:
            vector[litterals[get_value(token)]] = 1
        else:
            vector[len_litterals] = 1
        vector[len_litterals + 1 + whitespace_id[space]] = 1
        return vector

    print(litterals.keys())

    return get_vector, whitespace_id


def vectorize_file(path, vectorizer):
    spaces, tokens = jlu.tokenize_with_white_space(jlu.open_file(path))

    result = []
    for ws, t in zip(spaces, tokens):
        result.append(vectorizer(ws, t))

    return result

if __name__ == "__main__":
    k = 20
    vectorizer, whitespace_id = build_vocabulary(files)
    print(len(whitespace_id))
    data = []
    for file in files:
        vector = []
        vector = vectorize_file(file, vectorizer)
        for i in range(k, len(vector) - k, 1):
            io = dict()
            io['input'] = np.array(vector[i-k:i+k+1]).copy()
            for i in range(len(whitespace_id)):
                io['input'][k][-1-i] = 0
            ws = vector[i][-len(whitespace_id):]
            i = 0
            j = 0
            for a in ws:
                if j == 0 and a == 1:
                    j = i
                i += 1
            # print(io['input'].shape)
            io['output'] = j
            # print(io['input'])
            data.append(io)
    random.shuffle(data)
    train_len = int(len(data) * 0.8)
    train_data = data[:train_len]
    test_data = data[train_len:]
    print(f'Train files {train_len}')

    train_input = np.array([d['input'] for d in train_data])
    train_labels = np.array([d['output'] for d in train_data])
    #
    test_input = np.array([d['input'] for d in test_data])
    test_labels = np.array([d['output'] for d in test_data])

    print(train_input[0])


    # tf.app.run()
