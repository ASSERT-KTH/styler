# -*- coding: utf-8 -*-

from core import *
import tokenizer
from token_utils import *

from javalang import tokenizer as javalang_tokenizer
import checkstyle
import random
import intervals as I
import pandas as pd
from collections import Counter
import csv

MIN_BATCH_SIZE = 50
three_grams_dataset_path = os.path.join(os.path.dirname(__file__), 'three_grams.csv')

def word_counter_to_csv(counter):
    # field names
    fields = ['token_1', 'ws', 'token_2', 'count']

    # data rows of csv file
    rows = [
        [*three_gram, c]
        for three_gram, c in counter.items()
    ]

    with open(three_grams_dataset_path, 'w') as csvfile:
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

if os.path.exists(three_grams_dataset_path):
    df = pd.read_csv(three_grams_dataset_path)
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

def pick_random(alternatives):
    random_number = random.random()
    probability_sum = 0
    for alternative, probability in alternatives.items():
        probability_sum += probability
        if random_number <= probability_sum:
            return alternative
    return None


def modify_source_three_grams(source, n_insertion=1):
    tokenized_source = tokenizer_relative.tokenize(source)
    
    indentation_size = -1
    for line_break, nb_spaces, space_type in tokenized_source.white_spaces:
        if line_break > 0 and nb_spaces > 0:
            indentation_size = nb_spaces
            break

    insertion_spots = list(range(len(tokenized_source.tokens)-1))
    random.shuffle(insertion_spots)
    modification = None
    for spot in insertion_spots:
        token_a = tokenized_source.tokens[spot]
        token_b = tokenized_source.tokens[spot+1]
        old_ws_relative = tokenized_source.white_spaces[spot]
        if is_good_for_insertion(get_token_value(token_a), get_token_value(token_b)):
            alternatives = list_alternatives_with_probability(
                get_token_value(token_a),
                get_space_value(old_ws_relative),
                get_token_value(token_b)
            )
            alternative_selected = pick_random(alternatives)
            alternative_selected_tuple = whitespace_token_to_tuple(alternative_selected)
            
            new_ws = alternative_selected_tuple
            if new_ws[1]>=0:
                tokenized_source.white_spaces[spot] = new_ws
                modification = {
                    'token_a': get_token_value(token_a),
                    'token_b': get_token_value(token_b),
                    'modification': (get_space_value(old_ws_relative), alternative_selected),
                    'position': spot,
                    'old_ws': old_ws_relative,
                    'new_ws': new_ws,
                    'line': token_a.position[0]
                }
                reformatted = tokenized_source.reformat()
                if not check_source_well_formed(reformatted):
                    continue
                break

    return reformatted, modification


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
    
    # Take a sample of locations suitable for insertions
    insertions_sample = random.sample( tokens, min(insertions_sample_size, len(tokens)) )

    insertions = dict()

    insertions_chars = ([' '] * insertions_sample_size_space)
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
            prev_token_position = tokens[index-1].position
            tokens_position = tokens[index].position
            next_token_position = tokens[index+1].position
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
        spaces_violating, tokens_violating = tokenizer.tokenize_with_white_space(ugly_content)
        if len(tokens_original) != len(tokens_violating):
            continue
        return ugly_content, (modification, injection_operation)


def modify_source(source, protocol='random'):
    if protocol == 'random':
        return modify_source_random(source)
    elif protocol == 'three_grams':
        return modify_source_three_grams(source)
    return modify_source_random(source)


class Batch:
    def __init__(self, batch_size, files_dir, checkstyle_file_path, checkstyle_jar, batch_id=None, protocol='random'):
        self.batch_size = max(batch_size, MIN_BATCH_SIZE)
        self.checkstyle_file_path = checkstyle_file_path
        self.checkstyle_jar = checkstyle_jar
        if batch_id == None:
            self.batch_id = uuid.uuid4().hex
        else:
            self.batch_id = batch_id
        self.batch_files = [random.choice(files_dir) for _ in range(self.batch_size)]
        self.project_name = checkstyle_file_path.split('/')[-3]
        self.batch_dir = f'{get_tmp_batches_dir(self.project_name)}/{self.batch_id}'
        self.protocol = protocol
    
    def gen(self, max_time):
        create_dir(self.batch_dir)
        self.batch_injections = {}
        for index, file_dir in tqdm(enumerate(self.batch_files), total=self.batch_size):
            if datetime.now() >= max_time:
                logger.debug('Time out.')
                break
            
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
                logger.debug(err)
                continue

        self.checkstyle_result, _ = checkstyle.check(
            self.checkstyle_file_path,
            self.batch_dir,
            self.checkstyle_jar,
            only_targeted=True
        )
        if self.checkstyle_result is not None:
            for file_dir, res in self.checkstyle_result.items():
                index = int(file_dir.split('/')[-2])
                if index not in self.batch_injections:
                    continue
                self.batch_injections[index]['violations'] = res['violations']
                save_json(self.batch_injections[index]['dir'], 'violations.json', res['violations'])

            self.batch_information = {
                'batch_id': self.batch_id,
                'injection_report': self.batch_injections
            }
            save_json(self.batch_dir, 'metadata.json', self.batch_information)
            return self.batch_information
        return None
    
    def clean(self):
        shutil.rmtree(self.batch_dir)


def generate_violations(files_dir, checkstyle_file_path, checkstyle_jar, target, number_of_violations, protocol='random'):
    valid_violations = []
    batches = []
    MAX_EXEC = timedelta(hours=3)
    max_time = datetime.now() + MAX_EXEC

    with tqdm(total=number_of_violations) as pbar:
        while len(valid_violations) < number_of_violations:
            if datetime.now() >= max_time:
                number_of_violations = len(valid_violations)
                break
            
            remaining_to_be_generated = number_of_violations - len(valid_violations)
            batch = Batch(remaining_to_be_generated, files_dir, checkstyle_file_path, checkstyle_jar, protocol=protocol)
            batches.append(batch)
            try:
                batch_res = batch.gen(max_time)
            except KeyboardInterrupt:
                raise KeyboardInterrupt
            except UnicodeDecodeError:
                continue
            except: # UnicodeEncodeError
                logger.exception("Something went wrong.")
                continue
            if batch_res is None:
                continue
            batch_valid_violations = [
                info
                for info in batch_res['injection_report'].values() 
                if 'violations' in info and len(info['violations']) == 1
            ]
            valid_violations += batch_valid_violations
            pbar.update(len(batch_valid_violations))

    selected_violations = random.sample(valid_violations, number_of_violations)
    for violation_id, violation_metadata in enumerate(selected_violations):
        new_violation_dir = os.path.join(target, str(violation_id))
        old_dir = violation_metadata['dir']
        shutil.move(old_dir, new_violation_dir)
        violation_metadata['dir'] = new_violation_dir
        violation_metadata['violation'] = violation_metadata['violations'][0]
        violation_metadata['violation']['type'] = checkstyle_source_to_violation_type(violation_metadata['violation']['source'])
        
        file_name = violation_metadata['file_name']
        orig_file_name = '.'.join(file_name.split('.')[:-1]) + '-orig.java'
        shutil.copy(violation_metadata['orig'], os.path.join(new_violation_dir, orig_file_name))
        
        save_json(new_violation_dir, 'metadata.json', violation_metadata)
    for batch in batches:
        batch.clean()
    return selected_violations


def gen_dataset(corpus, checkstyle_file_path, share, number_of_synthetic_violations, synthetic_dataset_dir, checkstyle_jar, protocol='random'):
    training_folder_path = os.path.join(synthetic_dataset_dir, 'training')
    file_list = [file for (_,_,file) in corpus.values()]
    generate_violations(file_list, checkstyle_file_path, checkstyle_jar, training_folder_path, number_of_synthetic_violations, protocol=protocol)
    
    violation_types_to_violating_files = {}
    for training_file in list_dir_full_path(training_folder_path):
        with open(os.path.join(training_file, 'violations.json')) as json_file:
            data = json.load(json_file)
            if data[0]['source'] not in violation_types_to_violating_files:
                violation_types_to_violating_files[data[0]['source']] = []
            violation_types_to_violating_files[data[0]['source']].append(training_file)
    
    for subset_name, subset_share in share.items():
        if subset_share > 0:
            create_dir(os.path.join(synthetic_dataset_dir, subset_name))
    for violation_type in violation_types_to_violating_files:
        violating_files = violation_types_to_violating_files[violation_type]
        nb_files = len(violating_files)
        logger.debug(f'{violation_type}:{nb_files}')
        random.shuffle(violating_files)
        c_learning = int(share['learning']*nb_files)
        c_validation = int(share['validation']*nb_files)
        c_testing = int(share['testing']*nb_files)
        sum_of_shares = c_learning + c_validation + c_testing
        if sum_of_shares < nb_files:
            if c_learning == 0:
                c_learning += nb_files - sum_of_shares
            else:
                c_validation += nb_files - sum_of_shares
        splitted_violating_files = {
            'learning': violating_files[:c_learning],
            'validation': violating_files[c_learning: c_learning+c_validation],
            'testing': violating_files[c_learning+c_validation:]
        }
        for subset_name, subset_share in share.items():
            target_file = f'{os.path.join(synthetic_dataset_dir, subset_name)}'
            for training_file in splitted_violating_files[subset_name]:
                shutil.move(training_file, target_file)

    delete_dir_if_exists(training_folder_path)


if __name__ == '__main__':
    dataset_list = sys.argv[2:]

    if len(sys.argv) >= 2 and sys.argv[1] == 'run':
        corpora = map(
            lambda corpus: get_input_dir_to_analyze_and_repair(corpus),
            dataset_list
        )
        c = Counter()
        for corpus in tqdm(corpora, total=len(dataset_list)):
            for folder in os.walk(corpus):
                files = folder[2]
                for f in files:
                    file_path = os.path.join(folder[0], f)
                    if file_path.endswith('.java'):
                        c += tokenize_and_count(file_path)
        word_counter_to_csv(c)
    elif sys.argv[1] == 'gen':
        protocol = sys.argv[2]
        corpus_name = sys.argv[3]
        if protocol not in (('random', 'three_grams')):
            raise Exception('Unkown protocol')
        corpus = Corpus(core_config['CORPUS']['corpus_dir'] % corpus_name, corpus_name)
        gen_dataset(corpus, share, 500, f'./tmp/dataset/{protocol}/{corpus_name}', protocol=protocol)
    elif sys.argv[1] == 'test-threegrams':
        reformatted, modification = modify_source_three_grams(open_file('./experiments/projects/findbugsproject-findbugs/corpus/data/1/TestTestFields.java'))
        logger.debug(reformatted)
        logger.debug(modification)
