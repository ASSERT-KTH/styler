# -*- coding: utf-8 -*-

from core import *
import git_helper
from Corpus import Corpus
import synthetic_error_generator
import tokenizer
import ml
import checkstyle
import gotify

open_nmt_dir = os.path.join(os.path.dirname(__file__), 'OpenNMT-py')

def tokenize_errors(file_path, errors):
    inputs = []
    for error in errors:
        error['type'] = checkstyle_source_to_error_type(error['source'])
        if is_error_targeted(error):
            tokenized_file, info = tokenizer.tokenize_file_to_repair(file_path, error)
            inputs += [ (" ".join(tokenized_file), info) ]
    return inputs

def de_tokenize(original_file_path, info):
    pass

def run_preprocess(project, protocol):
    tokenized_dir = get_tokenized_dir_by_protocol(project, protocol)
    preprocessed_dir = get_preprocessed_dir_by_protocol(project, protocol)
    create_dir(preprocessed_dir)

    preprocess_script = os.path.join(open_nmt_dir, 'preprocess.py')
    options = [
        f'-train_src {tokenized_dir}/learning-I.txt',
        f'-train_tgt {tokenized_dir}/learning-O.txt',
        f'-valid_src {tokenized_dir}/validation-I.txt',
        f'-valid_tgt {tokenized_dir}/validation-O.txt',
        f'-save_data {preprocessed_dir}/preprocessing',
        '-src_seq_length 650',
        '-tgt_seq_length 105',
        '-src_vocab_size 165',
        '-tgt_vocab_size 165'
    ]
    cmd = f'python {preprocess_script} {" ".join(options)}'

    process = subprocess.Popen(cmd.split(" "), stdout=subprocess.PIPE)
    output = process.communicate()[0]

    return output

def run_train(project, protocol, global_attention, layers, rnn_size, word_vec_size, save_checkpoint_steps=20000, gpu=True):
    preprocessed_dir = get_preprocessed_dir_by_protocol(project, protocol)
    model_dir = get_model_dir(project)

    train_script = os.path.join(open_nmt_dir, 'train.py')
    options = [
        f'-data {preprocessed_dir}/preprocessing',
        f'-global_attention {global_attention}',
        '-encoder_type brnn',
        '-decoder_type rnn',
        f'-layers {layers}',
        f'-rnn_size {rnn_size}',
        f'-word_vec_size {word_vec_size}',
        '-decoder_type rnn',
        '-batch_size 32',
        '-optim adagrad',
        '-max_grad_norm 2',
        '-learning_rate 0.10',
        '-adagrad_accumulator_init 0.1',
        '-bridge',
        '-train_steps 20000',
        f'-save_checkpoint_steps {save_checkpoint_steps}',
        f'-save_model {model_dir}/{protocol}-{global_attention}-{layers}-{rnn_size}-{word_vec_size}-model'
    ]
    if gpu:
        options.append('-gpu_ranks 0')
    cmd = f'python {train_script} {" ".join(options)}'

    process = subprocess.Popen(cmd.split(" "), stdout=subprocess.PIPE)
    output = process.communicate()[0]

    return output

def gen_translator(model_name, protocol, batch_size=5):
    tmp_dir = get_tmp_dir(model_name)
    model = get_model(model_name, protocol)
    ml.create_dir(tmp_dir)
    def translator(input, output_dir, error_id):
        output_dir = f'{output_dir}/{error_id}'
        tmp_input_file_name = f'input-{error_id}.txt'
        tmp_input_file_path = os.path.join(output_dir, tmp_input_file_name)
        tmp_output_file_name = f'output-{error_id}.txt'
        tmp_output_file_path = os.path.join(output_dir, tmp_output_file_name)
        save_file(output_dir, tmp_input_file_name, input)
        run_translate(model, tmp_input_file_path, tmp_output_file_path, batch_size=batch_size)
        return list(filter(lambda a: a!='', open_file(tmp_output_file_path).split('\n')))
    return translator

def run_translate(model, input_file, output_file, batch_size=5):
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

def print_translations(file_path, metadata_path, translate):
    metadata = open_json(metadata_path)
    for tokenized_errors, info in tokenize_errors(file_path, metadata['errors']):
        for translation in translate(tokenized_errors):
            ml.print_diff(' '.join(info['tokens_errored_in_tag']) + '\n', translation)
            print()

def de_tokenize(file_path, info, new_tokens, only_formatting=False):
    source_code = open_file(file_path)
    result = tokenizer.de_tokenize(source_code, info, new_tokens.split(' '), only_formatting=only_formatting)
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

    logger.debug(f'Found {len(files_without_errors)} files with no errors.')
    logger.debug(f'Found {len(files_with_errors)} files with errors.')

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
            file.split('/')[-3]:len(result['errors'])
            for file, result in checkstyle_results.items()
            if f'batch_{batch}' == file.split('/')[-2]
        }
        for batch in range(n_batch)
    }

def select_the_smallest_repair(correct_repairs, original):
    min_diff = 10000000
    file = ''
    for correct_repair in correct_repairs:
        diff, diff_size = compute_diff(original, correct_repair)
        if diff_size < min_diff:
            file = correct_repair
            min_diff = diff_size
    return file

def repair_files(dir_repaired_files_by_protocol, dir_files_to_repair, model_name, protocol, checkstyle_jar, only_formatting=False):
    # set the dirs
    target = os.path.join(dir_repaired_files_by_protocol, 'repair-attempt')
    target_final = os.path.join(dir_repaired_files_by_protocol, 'files-repaired')
    checkstyle_rules = os.path.join(dir_files_to_repair, 'checkstyle.xml')
    waste = os.path.join(dir_repaired_files_by_protocol, 'waste')

    # yet we focus on single error files
    # TODO : Improve it
    dir_files_to_repair = os.path.join(dir_files_to_repair, f'./1')
    
    list_of_fileids = list_folders(dir_files_to_repair)
    number_of_files = len(list_of_fileids)
    if not os.path.exists(target):
        # create the folders
        create_dir(target)
        create_dir(waste)

        # Init of the translator
        translate = gen_translator(model_name, protocol, batch_size=5)

        #list_of_fileids = []
        for folder_id in tqdm(sorted(list_of_fileids)):
            file_path = glob.glob(f'{dir_files_to_repair}/{folder_id}/*.java')[0]
            logger.debug(file_path)
            metadata_path = f'{dir_files_to_repair}/{folder_id}/metadata.json'
            for error_id, error in enumerate(tokenize_errors(file_path, open_json(metadata_path)['errors'])):
                tokenized_errors, info = error
                create_dir(f'{target}/{int(folder_id) + error_id * number_of_files}')
                for proposal_id, translation in enumerate(translate(tokenized_errors, target, folder_id)):
                    if '<' in translation:
                        continue
                    de_tokenized_translation = de_tokenize(file_path, info, translation, only_formatting=only_formatting)
                    if de_tokenized_translation is None:
                        continue
                    folder = f'{target}/{int(folder_id) + error_id * number_of_files}/batch_{proposal_id}'
                    create_dir(folder)

                    if is_crlf(file_path):
                        de_tokenized_translation = de_tokenized_translation.replace('\n','\r\n')

                    save_file(folder, file_path.split('/')[-1], de_tokenized_translation)
                tmp_dir = get_tmp_dir(model_name)

        move_parse_exception_files(target, waste)
    checkstyle_result, number_of_errors = checkstyle.check(checkstyle_rules, target, checkstyle_jar, only_targeted=True)
    if checkstyle_result is None:
        return None
    res = reverse_collection(get_batch_results(checkstyle_result))
    
    def select_best_proposal(file_id, proposals):
        file_path = glob.glob(f'{dir_files_to_repair}/{int(file_id) % number_of_files}/*.java')[0]
        min_errors = min(list(proposals.values()))
        good_proposals = [
            id
            for id, n_errors in proposals.items()
            if n_errors == min_errors
        ]
        return select_the_smallest_repair(
            [ glob.glob(f'{target}/{file_id}/batch_{batch}/*.java')[0] for batch in good_proposals ],
            glob.glob(f'{dir_files_to_repair}/{int(file_id) % number_of_files}/*.java')[0]
        )

    best_proposals = {
        file_id:select_best_proposal(file_id, proposals)
        for file_id, proposals in res.items()
    }

    for id, path in best_proposals.items():
        folder = create_dir(f'{target_final}/{id}')
        shutil.copy(path, folder)

    return target_final

def join_protocols(name, protocols_repairs, checkstyle_jar):
    directory = get_styler_repairs(name)
    create_dir(directory)
    target = os.path.join(directory, 'repair-attempt')
    target_final = os.path.join(directory, 'files-repaired')
    create_dir(target)
    create_dir(target_final)
    checkstyle_rules = os.path.join(get_real_dataset_dir(name), 'checkstyle.xml')
    dir_files_to_repair = os.path.join(get_real_dataset_dir(name), '1')
    for (protocol_id, (protocol, path)) in enumerate(protocols_repairs.items()):
        if os.path.exists(path):
            for file_id in os.listdir(path):
                protocol_target = os.path.join(target, file_id, f'batch_{protocol_id}')
                if os.path.exists(protocol_target):
                    shutil.rmtree(protocol_target)
                if path is not None and os.path.exists(path):
                    shutil.copytree(os.path.join(path, file_id), protocol_target)
    checkstyle_result, number_of_errors = checkstyle.check(checkstyle_rules, target, checkstyle_jar, only_targeted=True)
    if checkstyle_result is None:
        return {}
    res = reverse_collection(get_batch_results(checkstyle_result, n_batch=len(protocols_repairs)))

    def select_best_proposal(file_id, proposals):
        file_path = glob.glob(f'{dir_files_to_repair}/{int(file_id)}/*.java')[0]
        min_errors = min(list(proposals.values()))
        good_proposals = [
            id
            for id, n_errors in proposals.items()
            if n_errors == min_errors
        ]
        return select_the_smallest_repair(
            [ glob.glob(f'{target}/{file_id}/batch_{batch}/*.java')[0] for batch in good_proposals ],
            glob.glob(f'{dir_files_to_repair}/{int(file_id)}/*.java')[0]
        )
    
    best_proposals = {
        file_id:select_best_proposal(file_id, proposals)
        for file_id, proposals in res.items()
    }

    for id, path in best_proposals.items():
        folder = create_dir(f'{target_final}/{id}')
        shutil.copy(path, folder)
    return best_proposals

def gen_training_data(project_path, checkstyle_file_path, checkstyle_jar, project_name, corpus_dir=None):
    protocols = (('random', 'three_grams'))
    try:
        if corpus_dir is None and project_path is None:
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
            synthetic_error_generator.gen_dataset(corpus, share, core_config['DATASHARE'].getint('number_of_synthetic_errors'), synthetic_dataset_dir_by_protocol, checkstyle_jar, protocol=protocol)
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
        start_time = datetime.now()
        
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
                dir_repaired_files_by_protocol = get_styler_repairs_by_protocol(dataset, protocol)
                create_dir(dir_repaired_files_by_protocol)
                dir_files_to_repair = get_real_dataset_dir(dataset)
                results[protocol] = repair_files(dir_repaired_files_by_protocol, dir_files_to_repair, dataset, protocol, checkstyle_jar, only_formatting=True)

            ## join protcols 
            choices = join_protocols(dataset, results, checkstyle_jar)
            for choice in choices.values():
                if 'batch_0' in choice:
                    protocol_choice_count['random'] += 1
                if 'batch_1' in choice:
                    protocol_choice_count['three_grams'] += 1
            save_json(get_styler_folder(dataset), 'protocol_choice_stats.json', protocol_choice_count)

        time_elapsed = datetime.now() - start_time
        logger.debug('Time elapsed (hh:mm:ss.ms) {}'.format(time_elapsed))

    if args[1] == 'gen_training_data':
        start_time = datetime.now()

        corpus_dir = None
        if args[2] == '--corpus':
            corpus_dir = args[3]
            errors_dataset_name = args[4]
        else:
            errors_dataset_name = args[2]
        errors_dataset_dir = get_real_dataset_dir(errors_dataset_name)
        dataset_info = open_json(os.path.join(errors_dataset_dir, 'info.json'))

        checkstyle_jar = dataset_info["checkstyle_jar"]

        if os.path.exists(get_corpus_dir(errors_dataset_name)):
            corpus_dir = get_corpus_dir(errors_dataset_name)
            repo_dir = None
        else:
            (repo_user, repo_name) = dataset_info['repo_url'].split('/')[-2:]

            repo, repo_dir = git_helper.clone_repo(repo_user, repo_name, https=True)
            repo.git.checkout(dataset_info["checkstyle_last_modification_commit"])

        checkstyle_file_path = os.path.join(errors_dataset_dir, 'checkstyle.xml')

        logger.debug("Starting data generation")

        gen_training_data(repo_dir, checkstyle_file_path, checkstyle_jar, errors_dataset_name, corpus_dir=corpus_dir)

        gotify.notify('[done][data generation]', errors_dataset_name)

        #delete_dir(repo_dir)

        time_elapsed = datetime.now() - start_time
        logger.debug('Time elapsed (hh:mm:ss.ms) {}'.format(time_elapsed))
    
    if args[1] == 'tokenize_training_data':
        start_time = datetime.now()

        project_name = args[2]
        for protocol in protocols:
            synthetic_dataset_dir_by_protocol = f'{get_synthetic_dataset_dir_by_protocol(project_name, protocol)}'
            ml.gen_IO(synthetic_dataset_dir_by_protocol, get_tokenized_dir_by_protocol(project_name, protocol), only_formatting=True)

        time_elapsed = datetime.now() - start_time
        logger.debug('Time elapsed (hh:mm:ss.ms) {}'.format(time_elapsed))

    if args[1] == 'preprocess_training_data':
        start_time = datetime.now()

        project_name = args[2]
        for protocol in protocols:
            run_preprocess(project_name, protocol)

        time_elapsed = datetime.now() - start_time
        logger.debug('Time elapsed (hh:mm:ss.ms) {}'.format(time_elapsed))

    if args[1] == 'train_model':
        start_time = datetime.now()

        project_name = args[2]
        protocol = args[3]
        global_attention = args[4]
        layers = args[5]
        rnn_size = args[6]
        word_vec_size = args[7]
        save_checkpoint_steps = 20000
        gpu = args[8]
        run_train(project_name, protocol, global_attention, layers, rnn_size, word_vec_size, save_checkpoint_steps, gpu)

        time_elapsed = datetime.now() - start_time
        logger.debug('Time elapsed (hh:mm:ss.ms) {}'.format(time_elapsed))


if __name__ == "__main__":
    main(sys.argv)
