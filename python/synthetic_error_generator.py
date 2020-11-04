# -*- coding: utf-8 -*-

from core import *
import tokenizer
from token_utils import *

from javalang import tokenizer as javalang_tokenizer
import checkstyle
import random
import intervals as I
import pandas as pd

BATCH_SIZE = 500

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
    tokens = tokenizer.tokenize_with_white_space(open_file(file_path))
    tokenized_file = []
    for token, ws in zip(map(get_token_value, tokens[1]), map(get_space_value, tokens[0])):
        tokenized_file += [token, ws]
    tokenized_file += ['EOF']
    three_grams = []
    for i in range(0, len(tokenized_file) - 2, 2):
        three_grams.append(tuple(tokenized_file[i: i+3]))
    counter = Counter(three_grams)
    return counter


df = pd.read_csv(os.path.join(os.path.dirname(__file__), 'three_grams.csv'))
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


tokenizer_relative = tokenizer.Tokenizer()
tokenizer_absolute = tokenizer.Tokenizer(relative=False)

def pick_random(alternatives):
    random_number = random.random()
    probability_sum = 0
    for alternative, probability in alternatives.items():
        probability_sum += probability
        if random_number <= probability_sum:
            return alternative
    return None


def modify_source_three_grams(source, n_insertion=1):
    lines = source.split("\n")
    nb_tab = 0
    nb_space = 0
    for line in lines:
        if len(line) == 0:
            continue
        if line[0] == "\t":
            nb_tab += 1
        elif line[0] == " ":
            nb_space += 1

    tokenizer_relative.tabulation = nb_tab >= nb_space
    tokenizer_absolute.tabulation = nb_tab >= nb_space

    tokenized_source = tokenizer_relative.tokenize(source)
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


def gen_ugly(file_path, output_dir, modification_number = (1,0,0,0,0)):
    """
    Gen an ugly vertsion of of .java file
    """
    with open(file_path) as f:
        file_lines = f.readlines()
    file_content = "".join(file_lines)

    output, modifications = gen_ugly_from_source(file_content, modification_number=modification_number)

    if not os.path.exists(output_dir):
        os.makedirs(output_dir)

    output_path = os.path.join(output_dir, f'./{file_path.split("/")[-1]}')

    with open(output_path, "w") as output_file_object:
        output_file_object.write(output)

    return modifications


def gen_ugly_from_source(file_content, modification_number = (1,0,0,0,0)):
    """
    Gen an ugly vertsion of of .java file
    """
    insertions_sample_size_space = modification_number[0]
    insertions_sample_size_tab = modification_number[1]
    insertions_sample_size_newline = modification_number[2]
    insertions_sample_size = insertions_sample_size_space + insertions_sample_size_tab + insertions_sample_size_newline
    deletions_sample_size_space = modification_number[3]
    deletions_sample_size_newline = modification_number[4]
    deletions_sample_size = deletions_sample_size_space + deletions_sample_size_newline
    # deletions_sample_size = modification_number - insertions_sample_size
    file_lines = [ line + '\n' for line in file_content.split('\n') ]

    tokens = javalang_tokenizer.tokenize(file_content)
    tokens = [ t for t in tokens]
    # print("\n".join([ str(t) for t in tokens]))


    # Take a sample of locations suitable for insertions
    insertions_sample = random.sample( tokens, min(insertions_sample_size, len(tokens)) )

    insertions = dict();

    insertions_chars = ([' '] * insertions_sample_size_space);
    insertions_chars.extend(['\t'] * insertions_sample_size_tab)
    insertions_chars.extend(['\n'] * insertions_sample_size_newline)
    random.shuffle(insertions_chars)

    for element, char in zip(insertions_sample, insertions_chars):
        insertions[element.position] = char

    # Select every locations suitable for deletions (i.e. before or after a separator/operator)
    deletions_spots = list()
    suitable_for_deletions = [javalang_tokenizer.Separator, javalang_tokenizer.Operator]
    for index in range(0, len(tokens)-1):
        if ( type(tokens[index]) in suitable_for_deletions):
            prev_token_position = tokens[index-1].position;
            tokens_position = tokens[index].position;
            next_token_position = tokens[index+1].position;
            end_of_prev_token = (prev_token_position[0], prev_token_position[1] + len(tokens[index-1].value))
            end_of_token = (tokens_position[0], tokens_position[1] + len(tokens[index].value))
            if (end_of_prev_token != tokens_position):
                #print("prev : ", tokens[index-1].value , tokens[index].value, tokens[index+1].value, tokens[index].position)
                deletions_spots.append((end_of_prev_token, tokens_position))
            if (end_of_token != next_token_position):
                #print("next : ", tokens[index-1].value , tokens[index].value, tokens[index+1].value, tokens[index].position)
                deletions_spots.append((end_of_token, next_token_position))
    deletions_spots = list(set(deletions_spots))

    # Take a sample of locations suitable for deletions
    deletions_sample = random.sample( deletions_spots, min(deletions_sample_size, len(deletions_spots)) )

    deletions = dict()
    for deletion_intervals in deletions_spots:
        #print(deletion_intervals)
        from_char = deletion_intervals[0]
        to_char = deletion_intervals[1]
        while from_char[0] <= to_char[0]:
            if from_char[0] == to_char[0]:
                interval = I.closedopen(from_char[1], to_char[1] )
            else:
                interval = I.closedopen(from_char[1], I.inf )
            if ( from_char[0] not in deletions):
                deletions[from_char[0]] = list()
            deletions[from_char[0]].append(interval)
            from_char=(from_char[0]+1, 0)


    deletions_spots_chars = dict()
    line_num = 1
    for line in file_lines:
        char_num = 1
        for char in line:
            if ( line_num in deletions ):
                for intervals in deletions[line_num]:
                    if char_num in intervals:
                        if (char not in deletions_spots_chars):
                            deletions_spots_chars[char] = []
                        deletions_spots_chars[char].append((line_num, char_num))
            char_num = char_num + 1
        line_num = line_num + 1


    deletions = []
    if (' ' in deletions_spots_chars):
        deletions.extend(random.sample(deletions_spots_chars[' '], deletions_sample_size_space))
    if ('\n' in deletions_spots_chars):
        deletions.extend(random.sample(deletions_spots_chars['\n'], deletions_sample_size_newline))

    # print(insertions)
    # print(deletions)
    # Write the output file
    output = ""
    line_num = 1
    for line in file_lines:
        char_num = 1
        for char in line:
            skip = False
            if ((line_num, char_num) in deletions):
                skip = True
            if ((line_num, char_num) in insertions):
                output += insertions[(line_num, char_num)]
            if ( not skip ):
                output += char
            char_num = char_num + 1
        line_num = line_num + 1
    return output, tuple(set(deletions) | set(insertions.keys()))

def modify_source_random(source):
    if not check_source_well_formed(source):
        raise InsertionException
    while True:
        injection_operation = random.choice(injection_operator_pool)
        ugly_content, modification = gen_ugly_from_source(source, modification_number=injection_operator_types[injection_operation])
        if not check_source_well_formed(ugly_content):
            continue
        spaces_original, tokens_original = tokenizer.tokenize_with_white_space(source)
        spaces_errored, tokens_errored = tokenizer.tokenize_with_white_space(ugly_content)
        if len(tokens_original) != len(tokens_errored):
            continue
        return ugly_content, (modification, injection_operation)


def modify_source(source, protocol='random'):
    if protocol == 'random':
        return modify_source_random(source)
    elif protocol == 'three_grams':
        return modify_source_three_grams(source)
    return modify_source_random(source)


class Batch:
    def __init__(self, files_dir, checkstyle_dir, checkstyle_jar, batch_id=None, protocol='random'):
        self.checkstyle_dir = checkstyle_dir
        self.checkstyle_jar = checkstyle_jar
        if batch_id == None:
            self.batch_id = uuid.uuid4().hex
        else:
            self.batch_id = batch_id
        self.batch_files = [random.choice(files_dir) for _ in range(BATCH_SIZE)]
        self.project_name = checkstyle_dir.split('/')[-3]
        self.batch_dir = f'{get_tmp_batches_dir(self.project_name)}/{self.batch_id}'
        self.protocol = protocol
    
    def gen(self):
        create_dir(self.batch_dir)
        self.batch_injections = {}
        for index, file_dir in tqdm(enumerate(self.batch_files), total=BATCH_SIZE):
            file_name = file_dir.split('/')[-1]
            original_source = open_file(file_dir)
            try:
                modified_source, modification = modify_source(original_source, protocol=self.protocol)    
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
            except InsertionException:
                logger.debug(InsertionException)
                continue
            except Exception as err:
                print(err)
                continue
        self.checkstyle_result, _ = checkstyle.check(
            self.checkstyle_dir,
            self.batch_dir,
            self.checkstyle_jar,
            only_java=True,
            only_targeted=True
        )
        if self.checkstyle_result is not None:
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
        return None
    
    def clean(self):
        shutil.rmtree(self.batch_dir)


def gen_errors(files_dir, checkstyle_dir, checkstyle_jar, target, number_of_errors, protocol='random'):
    valid_errors = []
    batches = []
    with tqdm(total=number_of_errors) as pbar:
        while len(valid_errors) < number_of_errors:
            batch = Batch(files_dir, checkstyle_dir, checkstyle_jar, protocol=protocol)
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
            if batch_res is None:
                continue
            batch_valid_errors = [
                info
                for info in batch_res['injection_report'].values() 
                if 'errors' in info and len(info['errors']) == 1
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


def gen_dataset(corpus, share, number_of_synthetic_errors, synthetic_dataset_dir, checkstyle_jar, protocol='random'):
    training_folder_path = os.path.join(synthetic_dataset_dir, 'training')
    file_list = [file for (_,_,file) in corpus.files.values()]
    gen_errors(file_list, corpus.checkstyle, checkstyle_jar, training_folder_path, number_of_synthetic_errors, protocol=protocol)
    
    error_types_to_errored_files = {}
    for training_file in list_dir_full_path(training_folder_path):
        with open(os.path.join(training_file, 'errors.json')) as json_file:
            data = json.load(json_file)
            if data[0]['source'] not in error_types_to_errored_files:
                error_types_to_errored_files[data[0]['source']] = []
            error_types_to_errored_files[data[0]['source']].append(training_file)
    
    for subset_name, subset_share in share.items():
        if subset_share > 0:
            create_dir(os.path.join(synthetic_dataset_dir, subset_name))
    for error_type in error_types_to_errored_files:
        errored_files = error_types_to_errored_files[error_type]
        nb_files = len(errored_files)
        logger.debug(f'{error_type}:{nb_files}')
        random.shuffle(errored_files)
        c_learning = int(share['learning']*nb_files)
        c_validation = int(share['validation']*nb_files)
        c_testing = int(share['testing']*nb_files)
        sum_of_shares = c_learning + c_validation + c_testing
        if sum_of_shares < nb_files:
            if c_learning == 0:
                c_learning += nb_files - sum_of_shares
            else:
                c_validation += nb_files - sum_of_shares
        splitted_errored_files = {
            'learning': errored_files[:c_learning],
            'validation': errored_files[c_learning: c_learning+c_validation],
            'testing': errored_files[c_learning+c_validation:]
        }
        for subset_name, subset_share in share.items():
            target_file = f'{os.path.join(synthetic_dataset_dir, subset_name)}'
            for training_file in splitted_errored_files[subset_name]:
                shutil.move(training_file, target_file)

    delete_dir_if_exists(training_folder_path)


if __name__ == '__main__':
    if sys.argv[2] == 'all':
        dataset_list = core_config['CORPUS']['corpus_names'].split(',')
    else:
        dataset_list = sys.argv[2:]

    if len(sys.argv) >= 2 and sys.argv[1] == 'run':
        corpora = map(
            lambda corpus: Corpus(core_config['CORPUS']['corpus_dir'] % corpus, corpus),
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
        corpus = Corpus(core_config['CORPUS']['corpus_dir'] % corpus_name, corpus_name)
        gen_dataset(corpus, share, 500, f'./tmp/dataset/{protocol}/{corpus_name}', protocol=protocol)

    elif (sys.argv[1] == "ugly"):
        print(gen_ugly( sys.argv[2], sys.argv[3] ))
    elif sys.argv[1] == "check":
        folder_dir = sys.argv[2]
        for file_path in glob.glob(f'{folder_dir}/*/*.java'):
            logger.debug(file_path)
            gen_ugly_from_source(open_file(file_path), modification_number = (1,0,0,0,0))
            gen_ugly_from_source(open_file(file_path), modification_number = (0,1,0,0,0))
            gen_ugly_from_source(open_file(file_path), modification_number = (0,0,1,0,0))
            gen_ugly_from_source(open_file(file_path), modification_number = (0,0,0,1,0))
            gen_ugly_from_source(open_file(file_path), modification_number = (0,0,0,0,1))