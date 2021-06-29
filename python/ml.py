from core import *
import tokenizer
import pprint
#import tensorflow as tf

pp = pprint.PrettyPrinter(indent=4)

# tf.logging.set_verbosity(tf.logging.INFO)

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

def print_diff(stringA, stringB, only_formatting=False):
    diffs = token_diff(stringA, stringB)
    print_aligned_strings(get_aligned_strings(zip(stringA.split(' '), stringB.split(' ')), color=False))
    return
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

def beam_search(target_dir, pred_dir, n=1, only_formatting=False):
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
                    print_diff(target, pred, only_formatting=True)
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
    if not only_formatting:
        pp.pprint({ i:c/total for i,c in count_whitepace.items() })
        pp.pprint(sum(count_whitepace.values()) / total)

    # pp.pprint(not_predicted)

def match_input_to_source(source, error_info, input):
    whitespace, tokens = tokenizer.tokenize_with_white_space(source)
    context_beginning_token = error_info['context_beginning_token']
    context_end_token = error_info['context_end_token']

    sub_sequence = tokens[context_beginning_token:context_end_token]
    ws_sub_sequence = whitespace[context_beginning_token:context_end_token]

    result = []
    count = 0
    ws_count = 0
    for input_token in input.split(' '):
        if token_utils.is_whitespace_token(input_token):
            result.append((input_token, get_space_value(ws_sub_sequence[ws_count])))
            ws_count += 1
        elif input_token.startswith('<') and input_token.endswith('>'):
            result.append((input_token, input_token))
        else:
            result.append((input_token, sub_sequence[count].value))
            count += 1


    return result

def get_predictions(dataset, n, id):
    print(dataset)
    tokenized_dir = get_tokenized_dir(dataset)
    return open_file(os.path.join(tokenized_dir, f'pred_{n}.txt')).split('\n')[(id*n):(id*n + n)]

def get_I(dataset, type, id):
    print(dataset)
    tokenized_dir = get_tokenized_dir(dataset)
    return open_file(os.path.join(tokenized_dir, f'{type}/{id}-I.txt'))

def get_O(dataset, type, id):
    print(dataset)
    tokenized_dir = get_tokenized_dir(dataset)
    return open_file(os.path.join(tokenized_dir, f'{type}/{id}-O.txt'))

def get_line(file, line):
    return open_file(file).split('\n')[line]

def get_error_filename_and_content(dataset, id):
    synthetic_dir = os.path.join(get_synthetic_dataset_dir(dataset), f'testing/{id}')
    errored_file_name = [ file for file in  os.listdir(synthetic_dir) if (file.endswith('.java') and 'orig' not in file ) ][0]
    errored_source = open_file(os.path.join(synthetic_dir, errored_file_name))
    return errored_file_name, errored_source

def get_orig_filename_and_content(dataset, id):
    synthetic_dir = os.path.join(get_synthetic_dataset_dir(dataset), f'testing/{id}')
    orig_file_name = [ file for file in  os.listdir(synthetic_dir) if (file.endswith('.java') and 'orig' in file ) ][0]
    orig_source = open_file(os.path.join(synthetic_dir, orig_file_name))
    return orig_file_name, orig_source

def get_error_info(dataset, id):
    tokenized_dir = get_tokenized_dir(dataset)
    tokenized_testing_dir = f'{tokenized_dir}/testing'
    error_info = open_json(os.path.join(tokenized_testing_dir, f'{id}-info.json'))
    return error_info

def get_aligned_strings(tokens, n=2, color=True):
    result = ['']*n
    for t in tokens:
        l = max([len(e) for e in t])
        pattern = f'{{:{l}}} '
        if token_utils.is_whitespace_token(t[0]):
            equals = True
            for token_to_compare in t[1:]:
                equals = equals and (token_to_compare == t[0])
            if not equals and color:
                pattern = colored(pattern, color='red')

        for i, content in enumerate(t):
            result[i] = result[i] + pattern.format(content)
    return result

def print_aligned_strings(strings):
    line_lenght = int(os.popen('stty size', 'r').read().split()[1])
    length = max([ len(s) for s in strings])
    for from_char in range(0,length, line_lenght):
        for string in strings:
            print(string[from_char:(from_char + line_lenght)])
        print()

def main(args):
    if sys.argv[2] == 'all':
        dataset_list = list_folders(get_synthetic_dataset_dir(''))
    else:
        dataset_list = sys.argv[2:]

    if len(args) >= 2 and args[1] == 'info':
        folder = args[2]
        print_max_length_and_vocabulary(folder)
    if len(args) == 4 and args[1] == 'beam':
        n = int(args[3])
        dataset = args[2]
        data_folder = get_tokenized_dir(dataset)
        pred_path = f'{data_folder}/pred_{n}.txt'
        testing_O_path = f'{data_folder}/testing-O.txt'
        beam_search(testing_O_path, pred_path, n=n, only_formatting=True)
    if len(args) == 5 and args[1] == 'get':
        n = int(args[3])
        id = int(args[4])
        dataset = args[2]
        input = get_I(dataset, 'testing', id)
        predictions = get_predictions(dataset, n, id)
        output = get_O(dataset, 'testing', id)
        error_info = get_error_info(dataset, id)
        orig_file_name, orig_source = get_orig_filename_and_content(dataset, id)
        print_aligned_strings(get_aligned_strings(match_input_to_source(orig_source, error_info, input)))

if __name__ == "__main__":
    main(sys.argv)
