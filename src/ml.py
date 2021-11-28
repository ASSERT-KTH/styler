from core import *
import tokenizer
import pprint

pp = pprint.PrettyPrinter(indent=4)

def gen_IO(dir, target, only_formatting=False):
    create_dir(target)
    sub_sets = ['learning', 'validation', 'testing']
    diffs = []
    weirdos = []
    for sub_set in sub_sets:
        sub_set_dir = os.path.join(dir, sub_set)
        if not os.path.exists(sub_set_dir):
            continue
        target_sub_set = f'{target}/{sub_set}'
        create_dir(target_sub_set)
        synthetic_violation_ids = list_folders(sub_set_dir)
        synthetic_violation_ids = sorted(synthetic_violation_ids, key=int)
        for id in tqdm(synthetic_violation_ids, desc=f'{dir.split("/")[-1]}/{sub_set}'):
            try:
                violation_dir = os.path.join(dir, f'./{sub_set}/{id}')
                file_name = [ java_file for java_file in glob.glob(f'{violation_dir}/*.java') if 'orig' not in java_file ][0].split('/')[-1].split('.')[0]
                file = f'{violation_dir}/{file_name}.java'
                file_orig = f'{violation_dir}/{file_name}-orig.java'
                violation_metadata_file = f'{violation_dir}/metadata.json'
                violation_metadata = open_json(violation_metadata_file)
                # Compatibility
                if 'line' not in violation_metadata:
                    violation_metadata = violation_metadata['violation']
                tokens_violating, tokens_correct, tokens_violating_in_tag, info =  tokenizer.tokenize_file_to_repair_for_model(file, file_orig, violation_metadata)
            except:
                continue
            if only_formatting:
                tokens_correct = tokens_correct[1::2]
                tokens_violating_in_tag = tokens_violating_in_tag[1::2]
            save_file(target_sub_set, f'{id}-I.txt', " ".join(tokens_violating))
            save_file(target_sub_set, f'{id}-O.txt', " ".join(tokens_correct))
            save_file(target_sub_set, f'{id}-E.txt', " ".join(tokens_violating_in_tag))
            save_json(target_sub_set, f'{id}-info.json', info)
            diffs.append(info['count_diff'])
            if info['count_diff'] == 2:
                weirdos.append(f'{sub_set}/{id}')
        merge_IOs(sub_set, synthetic_violation_ids, target)
    # print(weirdos)

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
    if len(args) >= 2 and args[1] == 'info':
        folder = args[2]
        print_max_length_and_vocabulary(folder)

if __name__ == "__main__":
    main(sys.argv)
