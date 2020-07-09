# -*- coding: utf-8 -*-

import ml
import sys
import glob
from git import Repo
from tqdm import tqdm
import experiments as real
from scipy import stats
import git_helper

from loguru import logger
import gotify

from core import *
import checkstyle
from Corpus import Corpus
import synthetic_2
import ml

def tokenize_errors(file_path, errors):
    inputs = []
    for error in errors:
        error['type'] = checkstyle_source_to_error_type(error['source'])
        if is_error_targeted(error):
            tokenized_file, info = ml.tokenize_file_to_repair(file_path, error)
            inputs += [ (" ".join(tokenized_file), info) ]
    return inputs

def de_tokenize(original_file_path, info):
    pass

def gen_translator(model_name, protocol, batch_size=5, only_formatting=False):
    tmp_dir = get_tmp_dir(model_name)
    model_dir = get_model_dir(model_name, protocol, only_formatting=only_formatting)
    ml.create_dir(tmp_dir)
    tmp_input_file_name = 'input.txt'
    tmp_input_file_path = os.path.join(tmp_dir, tmp_input_file_name)
    tmp_output_file_name = 'output.txt'
    tmp_output_file_path = os.path.join(tmp_dir, tmp_output_file_name)
    def translator(input):
        save_file(tmp_dir, tmp_input_file_name, input)
        run_translate(model_dir, tmp_input_file_path, tmp_output_file_path, batch_size=batch_size)
        return list(filter(lambda a: a!='', open_file(tmp_output_file_path).split('\n')))
    return translator

def run_translate(model_dir, input_file, output_file, batch_size=5):
    open_nmt_dir = './OpenNMT-py'
    translate_script = os.path.join(open_nmt_dir, 'translate.py')

    options = [
        f'-model {model_dir}',
        f'-src {input_file}',
        f'-output {output_file}',
        f'-n_best {batch_size}'
    ]
    cmd = f'python {translate_script} {" ".join(options)}'

    process = subprocess.Popen(cmd.split(" "), stdout=subprocess.PIPE)
    output = process.communicate()[0]

    return output

def join_token(tokens):
    return ' '.join(tokens)

def print_translations(file_path, metadata_path, translate):
    metadata = open_json(metadata_path)
    for tokenized_errors, info in tokenize_errors(file_path, metadata['errors']):
        print(info)
        for translation in translate(tokenized_errors):
            ml.print_diff(join_token(info['tokens_errored_in_tag']) + '\n', translation)
            print()

def de_tokenize(file_path, info, new_tokens, only_formatting=False):
    source_code = open_file(file_path)
    result = ml.de_tokenize(source_code, info, new_tokens.split(' '), tabulations=False, only_formatting=only_formatting)
    return result

def get_files_without_errors(checkstyle_result):
    return [ file for file, result in checkstyle_result.items() if len(result['errors']) == 0 ]

def get_files_with_errors(checkstyle_result):
    return [ file for file, result in checkstyle_result.items() if len(result['errors']) > 0 ]

def create_corpus(dir, name, checkstyle_dir, checkstyle_jar):
    if dir.endswith('/'):
        dir = dir[:-1]
    corpus_dir = get_corpus_dir(name)

    (output, returncode) = checkstyle.check(
        checkstyle_file_path=checkstyle_dir,
        file_to_checkstyle_path=dir,
        checkstyle_jar=checkstyle_jar,
        only_targeted=True,
        only_java=True
    )

    files_without_errors = get_files_without_errors(output)
    files_with_errors = get_files_with_errors(output)

    print(f'Found {len(files_without_errors)} files with no errors.')
    print(f'Found {len(files_with_errors)} files with errors.')

    def is_good_candidate(file_path):
        if not file_path.endswith('.java'):
            return False
        return True

    candidate_files = filter(is_good_candidate, files_without_errors)

    create_dir(corpus_dir)
    shutil.copy(checkstyle_dir, os.path.join(corpus_dir, 'checkstyle.xml'))
    for id, file in tqdm(enumerate(candidate_files), desc='Copy'):
        file_target_dir = os.path.join(corpus_dir, f'data/{id}')
        file_name = file.split('/')[-1]
        file_target = os.path.join(file_target_dir, file_name)
        create_dir(file_target_dir)
        shutil.copy(file, file_target)

    corpus_info = {
        'grammar': 'Java8',
        'indent': '4'
    }
    save_json(corpus_dir, 'corpus.json', corpus_info)
    return corpus_dir

def get_batch_results(checkstyle_results, n_batch=5):
    return {
        batch:{
            file.split('/')[-2]:len(result['errors'])
            for file, result in checkstyle_results.items()
            if f'batch_{batch}' == file.split('/')[-3]
        }
        for batch in range(n_batch)
    }

def select_the_best_repair(correct_repairs, original):
    min_diff = 10000000
    file = ''
    for correct_repair in correct_repairs:
        diff_size = java_lang_utils.compute_diff_size(original, correct_repair)
        if diff_size < min_diff:
            file = correct_repair
    return file

def repair_files(dir, dir_files, model_name, protocol, checkstyle_jar, only_formatting=False):
    # set the dirs
    target = os.path.join(dir, 'repair-attempt')
    target_final = os.path.join(dir, 'files-repaired')
    checkstyle_rules = os.path.join(dir_files, 'checkstyle.xml')
    waste = os.path.join(dir, 'waste')

    # yet we focus on single error files
    # TODO : Improve it
    dir_files = os.path.join(dir_files, f'./1')
    
    list_of_fileids = list_folders(dir_files)
    number_of_files = len(list_of_fileids)
    if not os.path.exists(target):
        # create the folders
        create_dir(target)
        create_dir(waste)

        # Init of the translator
        translate = gen_translator(model_name, protocol, batch_size=5, only_formatting=only_formatting)

        #list_of_fileids = []
        for folder_id in tqdm(list_of_fileids):
            file_path = glob.glob(f'{dir_files}/{folder_id}/*.java')[0]
            logger.debug(file_path)
            metadata_path = f'{dir_files}/{folder_id}/metadata.json'
            for error_id, error in enumerate(tokenize_errors(file_path, open_json(metadata_path)['errors'])):
                tokenized_errors, info = error
                for proposal_id, translation in enumerate(translate(tokenized_errors)):
                    de_tokenized_translation = de_tokenize(file_path, info, translation, only_formatting=only_formatting)
                    folder = f'{target}/batch_{proposal_id}/{int(folder_id) + error_id * number_of_files}'
                    create_dir(folder)
                    save_file(folder, file_path.split('/')[-1], de_tokenized_translation)

        move_parse_exception_files(target, waste)
    checkstyle_result, number_of_errors = checkstyle.check(checkstyle_rules, target, checkstyle_jar, only_targeted=True)
    #json_pp(checkstyle_result)
    #save_json('./', 'test.json', checkstyle_result)
    def select_best_proposal(file_id, proposals):
        file_path = glob.glob(f'{dir_files}/{int(file_id) % number_of_files}/*.java')[0]
        min_errors = min(list(proposals.values()))
        good_proposals = [
            id
            for id, n_errors in proposals.items()
            if n_errors == min_errors
        ]
        return select_the_best_repair(
            [ glob.glob(f'{target}/batch_{batch}/{file_id}/*.java')[0] for batch in good_proposals ],
            glob.glob(f'{dir_files}/{int(file_id) % number_of_files}/*.java')[0]
        )

    best_proposals = {
        file_id:select_best_proposal(file_id, proposals)
        for file_id, proposals in reverse_collection(get_batch_results(checkstyle_result)).items()
    }

    for id, path in best_proposals.items():
        folder = create_dir(f'{target_final}/{id}')
        shutil.copy(path, folder)

    return target_final

def repair_real(name, protocol, checkstyle_jar):
    directory = get_styler_repairs_by_protocol(name, protocol)
    create_dir(directory)
    dir_files = get_real_dataset_dir(name)
    return repair_files(directory, dir_files, name, protocol, checkstyle_jar, only_formatting=True)


def join_protocols(name, protocols_repairs, checkstyle_jar):
    directory = get_styler_repairs(name)
    create_dir(directory)
    target = os.path.join(directory, 'repair-attempt')
    target_final = os.path.join(directory, 'files-repaired')
    create_dir(target)
    create_dir(target_final)
    checkstyle_rules = os.path.join(get_real_dataset_dir(name), 'checkstyle.xml')
    dir_files = os.path.join(get_real_dataset_dir(name), './1')
    
    for (protocol_id, (protocol, path)) in enumerate(protocols_repairs.items()):
        protocol_target = os.path.join(target, f'./batch_{protocol_id}')
        if os.path.exists(protocol_target):
            shutil.rmtree(protocol_target)
        shutil.copytree(path, protocol_target)
    checkstyle_result, number_of_errors = checkstyle.check(checkstyle_rules, target, checkstyle_jar, only_targeted=True)
    res = reverse_collection(get_batch_results(checkstyle_result, n_batch=len(protocols_repairs)))

    def select_best_proposal(file_id, proposals):
        file_path = glob.glob(f'{dir_files}/{int(file_id)}/*.java')[0]
        min_errors = min(list(proposals.values()))
        good_proposals = [
            id
            for id, n_errors in proposals.items()
            if n_errors == min_errors
        ]
        return select_the_best_repair(
            [ glob.glob(f'{target}/batch_{batch}/{file_id}/*.java')[0] for batch in good_proposals ],
            glob.glob(f'{dir_files}/{int(file_id)}/*.java')[0]
        )

    best_proposals = {
        file_id:select_best_proposal(file_id, proposals)
        for file_id, proposals in res.items()
    }

    for id, path in best_proposals.items():
        folder = create_dir(f'{target_final}/{id}')
        shutil.copy(path, folder)
    return best_proposals

def gen_training_data_2(project_path, checkstyle_file_path, checkstyle_jar, project_name, corpus_dir=None):
    protocols = (('random', 'three_grams'))
    try:
        if corpus_dir is None:
            corpus_dir = create_corpus(
                project_path,
                project_name,
                checkstyle_file_path,
                checkstyle_jar
            )


        corpus = Corpus(corpus_dir, project_name)
        share = { key: core_config['DATASHARE'].getfloat(key) for key in ['learning', 'validation', 'testing'] }
        for protocol in protocols:
            gotify.notify('[data generation]', f'Start {protocol} on {project_name}')
            synthetic_dataset_dir_by_protocol = f'{get_synthetic_dataset_dir_by_protocol(project_name, protocol)}'
            synthetic_2.gen_dataset(corpus, share, core_config['DATASHARE'].getint('number_of_synthetic_errors'), synthetic_dataset_dir_by_protocol, checkstyle_jar, protocol=protocol)
            ml.gen_IO(synthetic_dataset_dir_by_protocol, get_tokenized_dir_by_protocol(project_name, protocol), only_formatting=True)
            gotify.notify('[data generation]', f'Done {protocol} on {project_name}')
    except:
        logger.exception("Something whent wrong during the generation training data")
        #delete_dir_if_exists(get_corpus_dir(project_name))

        for protocol in protocols:
            delete_dir_if_exists(f'{get_synthetic_dataset_dir_by_protocol(project_name, protocol)}')
            delete_dir_if_exists(get_tokenized_dir_by_protocol(project_name, protocol))
        gotify.notify('[error][data generation]', project_name)


def main(args):
    if args[1] == 'repair':
        datasets = args[2:]
        protocol_choice_count = {
            'random': 0,
            'three_grams': 0
        }
        for dataset in tqdm(datasets, desc='dataset'):
            errors_dataset_dir = get_real_dataset_dir(dataset)
            dataset_info = open_json(os.path.join(errors_dataset_dir, 'info.json'))
            checkstyle_jar = dataset_info["checkstyle_jar"]

            results = {}
            for protocol in tqdm(protocols, desc='protocol'):
                results[protocol] = repair_real(dataset, protocol, checkstyle_jar)
            ## join protcols 
            choices = join_protocols(dataset, results, checkstyle_jar)
            for choice in choices.values():
                if 'batch_0' in choice:
                    protocol_choice_count['random'] += 1
                if 'batch_1' in choice:
                    protocol_choice_count['three_grams'] += 1
        json_pp(protocol_choice_count)

    if args[1] == 'gen_training_data':
        corpus_dir = None
        if args[2] == '--corpus':
            corpus_dir = args[3]
            errors_dataset_name = args[4]
        else:
            errors_dataset_name = args[2]
        errors_dataset_dir = get_real_dataset_dir(errors_dataset_name)
        dataset_info = open_json(os.path.join(errors_dataset_dir, 'info.json'))

        checkstyle_jar = dataset_info["checkstyle_jar"]

        (repo_user, repo_name) = dataset_info['repo_url'].split('/')[-2:]

        repo, repo_dir = git_helper.clone_repo(repo_user, repo_name, https=True)
        repo.git.checkout(dataset_info["checkstyle_last_modification_commit"])

        checkstyle_file_path = os.path.join(errors_dataset_dir, 'checkstyle.xml')

        logger.debug("Starting data generation")

        gen_training_data_2(repo_dir, checkstyle_file_path, checkstyle_jar, errors_dataset_name, corpus_dir=corpus_dir)

        gotify.notify('[done][data generation]', errors_dataset_name)

        #delete_dir(repo_dir)

if __name__ == "__main__":
    main(sys.argv)
