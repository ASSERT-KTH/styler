import ml
import sys
import json
import os
import subprocess
import glob
import git
from git import Repo
import shutil
from tqdm import tqdm
from functools import reduce
import pydash

from core import *
import checkstyle
import repair

from travis import analyze

def tokenize_errors(file_path, errors):
    inputs = []
    for error in errors:
        error['type'] = checkstyle_source_to_error_type(error['source'])
        if error['type'] in targeted_errors and error['type'] not in corner_cases_errors:
            tokenized_file, info = ml.tokenize_file_to_repair(file_path, error)
            inputs += [ (" ".join(tokenized_file), info) ]
    return inputs

def de_tokenize(original_file_path, info):
    pass

def gen_translator(model, batch_size=5):
    tmp_dir = './styler/tmp'
    ml.create_dir(tmp_dir)
    tmp_input_file_name = 'input.txt'
    tmp_input_file_path = os.path.join(tmp_dir, tmp_input_file_name)
    tmp_output_file_name = 'output.txt'
    tmp_output_file_path = os.path.join(tmp_dir, tmp_output_file_name)
    def translator(input):
        save_file(tmp_dir, tmp_input_file_name, input)
        run_translate(model, tmp_input_file_path, tmp_output_file_path, batch_size=batch_size)
        return list(filter(lambda a: a!='', open_file(tmp_output_file_path).split('\n')))
    return translator

def run_translate(model, input_file, output_file, batch_size=5):
    open_nmt_dir = './OpenNMT-py'
    translate_script = os.path.join(open_nmt_dir, 'translate.py')

    options = [
        f'-model {model}',
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

def de_tokenize(file_path, info, new_tokens):
    source_code = open_file(file_path)
    result = ml.de_tokenize(source_code, info, new_tokens.split(' '), tabulations=False)
    return result

def get_files_without_errors(checkstyle_result):
    return [ file for file, result in checkstyle_result.items() if len(result['errors']) == 0 ]

def get_files_with_errors(checkstyle_result):
    return [ file for file, result in checkstyle_result.items() if len(result['errors']) > 0 ]

def create_corpus(dir, commit, checkstyle_relative_dir):
    if dir.endswith('/'):
        dir = dir[:-1]
    corpus_dir = f'{dir}-corpus'

    repo = Repo(dir)
    repo.git.checkout(commit)

    checkstyle_results = find_all(dir, 'checkstyle-result.xml')
    for checkstyle_result in checkstyle_results:
        os.remove(checkstyle_result)

    # pom_relative_dir = './pom.xml'
    # checkstyle_relative_dir = './checkstyle.xml'
    # pom_dir = os.path.join(dir, pom_relative_dir)
    checkstyle_dir = os.path.join(dir, checkstyle_relative_dir)
    # cmd = f'java -jar ../jars/checkstyle-8.12-all.jar -c {checkstyle_dir} -f xml {dir}'
    (output, returncode) = checkstyle.check(checkstyle_file_path=checkstyle_dir, file_path=dir)

    # results_files = find_all(dir, 'checkstyle-result.xml')
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

def get_batch_results(checkstyle_results):
    return {
        batch:set([
            file.split('/')[-2]
            for file, result in checkstyle_results.items()
            if len(result['errors']) == 0
            and f'batch_{batch}' == file.split('/')[-3]
        ])
        for batch in range(5)
    }

def reverse_collection(collection, key_func=pydash.utilities.identity):
    result = {}
    for key, items in collection.items():
        for item in items:
            value = key_func(item)
            if value not in result:
                result[value] = []
            result[value] += [key]
    return result

def select_the_best_repair(correct_repairs, original):
    min_diff = 10000000
    file = ''
    for correct_repair in correct_repairs:
        diff_size = java_lang_utils.compute_diff_size(original, correct_repair)
        if diff_size < min_diff:
            file = correct_repair
    return file

def repair_files():
    dir= './styler/test'
    target = './styler/test-repaired'
    target_final = './styler/test-final'
    checkstyle_rules = './styler/checkstyle.xml'
    bin = './styler/test-bin'
    create_dir(target)
    create_dir(bin)
    translate = gen_translator('./styler/model.pt')
    for folder_id in tqdm(list_folders(dir)):
        file_path = glob.glob(f'{dir}/{folder_id}/*.java')[0]
        metadata_path = f'{dir}/{folder_id}/metadata.json'
        for tokenized_errors, info in tokenize_errors(file_path, open_json(metadata_path)['errors']):
            for proposal_id, translation in enumerate(translate(tokenized_errors)):
                de_tokenized_translation = de_tokenize(file_path, info, translation)
                folder = f'{target}/batch_{proposal_id}/{folder_id}'
                create_dir(folder)
                save_file(folder, file_path.split('/')[-1], de_tokenized_translation)
    move_parse_exception_files(target, bin)
    checkstyle_result, number_of_errors = checkstyle.check(checkstyle_rules, target)
    files_properly_repaired = reverse_collection(get_batch_results(checkstyle_result))
    final_repairs = {
        id:select_the_best_repair(
            [ glob.glob(f'{target}/batch_{batch}/{id}/*.java')[0] for batch in repairs ],
            glob.glob(f'{dir}/{id}/*.java')[0]
        )
        for id, repairs
        in files_properly_repaired.items()
    }
    json_pp(final_repairs)
    for id, path in final_repairs.items():
        folder = create_dir(f'{target_final}/{id}')
        shutil.copy(path, folder)

def test():
    repos = list(open_json('./travis/commits.json').keys()) + list(open_json('./travis/commits_oss.json').keys())
    for info in tqdm(analyze.get_repo_with_checkstyle(repos), desc='Total'):
        print(info['repo_full_name'])

def main(args):
    # create_corpus('./styler/be5', '8cffdf6c26ed0d0ba420316d52e5cbff97218c61', './checkstyle.xml')
    # create_corpus('./styler/auto', '0c06a2345f71f053714d37bb6549d3460c999f2d', '../checkstyle.xml')
    # repair_files()
    test()


if __name__ == "__main__":
    main(sys.argv)
