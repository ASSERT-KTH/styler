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

from core import *
import checkstyle

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
    checkstyle_relative_dir = './checkstyle.xml'
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
        if '/test/' in file_path:
            return False
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

def repair():
    dir= './styler/test'
    file_path = f'{dir}/QueryContext.java'
    metadata_path = f'{dir}/metadata.json'
    translate = gen_translator('./styler/model.pt')
    print_translations(file_path, metadata_path, translate)
    # for tokenized_errors, info in tokenize_errors(file_path, open_json(metadata_path)['errors']):
    #     for translation in translate(tokenized_errors):
    #         print(de_tokenize(file_path, info, translation))
    #         print()

def main(args):
    create_corpus('./styler/be5', '8cffdf6c26ed0d0ba420316d52e5cbff97218c61', './checkstyle.xml')



if __name__ == "__main__":
    main(sys.argv)
