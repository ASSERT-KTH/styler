# -*- coding: utf-8 -*-

from core import *
import git_helper
import synthetic_violation_generator
import tokenizer
import ml
import checkstyle
import gotify

open_nmt_dir = os.path.join(os.path.dirname(__file__), 'OpenNMT-py')

def tokenize_violations(file_path, violations):
    inputs = []
    for violation in violations:
        violation['type'] = checkstyle_source_to_violation_type(violation['source'])
        if is_violation_type_targeted(violation):
            tokenized_file, info = tokenizer.tokenize_file_to_repair(file_path, violation)
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

def run_train(project, protocol, global_attention, layers, rnn_size, word_vec_size, train_steps=20000, save_checkpoint_steps=20000, gpu=True):
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
        f'-train_steps {train_steps}',
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
    def translator(input, output_dir, violation_id):
        output_dir = f'{output_dir}/{violation_id}'
        tmp_input_file_name = f'input-{violation_id}.txt'
        tmp_input_file_path = os.path.join(output_dir, tmp_input_file_name)
        tmp_output_file_name = f'output-{violation_id}.txt'
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
    for tokenized_violations, info in tokenize_violations(file_path, metadata['violations']):
        for translation in translate(tokenized_violations):
            ml.print_diff(' '.join(info['tokens_violating_in_tag']) + '\n', translation)
            print()

def de_tokenize(file_path, info, new_tokens, only_formatting=False):
    source_code = open_file(file_path)
    result = tokenizer.de_tokenize(source_code, info, new_tokens.split(' '), only_formatting=only_formatting)
    return result

def get_files_without_violations(checkstyle_result):
    return [ file for file, result in checkstyle_result.items() if len(result['violations']) == 0 ]

def get_files_with_violations(checkstyle_result):
    return [ file for file, result in checkstyle_result.items() if len(result['violations']) > 0 ]

def create_corpus(repo, dataset_info, project_name, checkstyle_file_path):
    violation_free_file_dir_path = get_violation_free_file_dir(project_name)

    create_dir(violation_free_file_dir_path)

    checkstyle_jar = dataset_info["checkstyle_jar"]

    default_branch = None
    commits = []
    checkstyle_last_modification_commit = dataset_info["checkstyle_last_modification_commit"]
    wd = os.getcwd()
    os.chdir(repo.git.rev_parse('--show-toplevel'))
    cmd_commit_list = 'git log --pretty=format:%H'
    p2 = subprocess.Popen(shlex.split(cmd_commit_list), stdout=subprocess.PIPE)
    commit_list_array = p2.communicate()[0].decode("utf-8")
    commit_list = commit_list_array.split('\n')
    commits = commit_list[:commit_list.index(checkstyle_last_modification_commit)] + [checkstyle_last_modification_commit]
    commits.reverse()
    os.chdir(wd)
    
    for commit in commits:
        logger.debug(f'Checking out {commit}')
        repo.git.checkout(commit)

        (output, returncode) = checkstyle.check(
            checkstyle_file_path=checkstyle_file_path,
            file_to_checkstyle_path=repo.working_dir,
            checkstyle_jar=checkstyle_jar,
            only_targeted=True
        )

        files_without_violations = get_files_without_violations(output)
        files_with_violations = get_files_with_violations(output)

        logger.debug(f'Found {len(files_without_violations)} files with no violations.')
        logger.debug(f'Found {len(files_with_violations)} files with violations.')

        def is_good_candidate(file_path):
            if not file_path.endswith('.java'):
                return False
            return True

        candidate_files = filter(is_good_candidate, files_without_violations)

        files = 0
        for id, file in tqdm(enumerate(candidate_files), desc='Copy'):
            file_target_dir = os.path.join(violation_free_file_dir_path, f'{id}')
            file_name = file.split('/')[-1]
            file_target = os.path.join(file_target_dir, file_name)
            create_dir(file_target_dir)
            shutil.copy(file, file_target)
            files += 1

        if files > 1:
            break

    return violation_free_file_dir_path

def get_batch_results(checkstyle_results, n_batch=5):
    return {
        batch:{
            file.split('/')[-3]:len(result['violations'])
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
    checkstyle_rules = os.path.join(get_project_dir(model_name), 'checkstyle.xml')
    waste = os.path.join(dir_repaired_files_by_protocol, 'waste')

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
            for violation_id, violation in enumerate(tokenize_violations(file_path, open_json(metadata_path)['violations'])):
                tokenized_violations, info = violation
                create_dir(f'{target}/{int(folder_id) + violation_id * number_of_files}')
                for proposal_id, translation in enumerate(translate(tokenized_violations, target, folder_id)):
                    if '<' in translation:
                        continue
                    de_tokenized_translation = de_tokenize(file_path, info, translation, only_formatting=only_formatting)
                    if de_tokenized_translation is None:
                        continue
                    folder = f'{target}/{int(folder_id) + violation_id * number_of_files}/batch_{proposal_id}'
                    create_dir(folder)

                    if is_crlf(file_path):
                        de_tokenized_translation = de_tokenized_translation.replace('\n','\r\n')

                    save_file(folder, file_path.split('/')[-1], de_tokenized_translation)
                tmp_dir = get_tmp_dir(model_name)

        move_parse_exception_files(target, waste)
    checkstyle_result, number_of_violations = checkstyle.check(checkstyle_rules, target, checkstyle_jar, only_targeted=True)
    if checkstyle_result is None:
        return None
    res = reverse_collection(get_batch_results(checkstyle_result))
    
    def select_best_proposal(file_id, proposals):
        file_path = glob.glob(f'{dir_files_to_repair}/{int(file_id) % number_of_files}/*.java')[0]
        min_violations = min(list(proposals.values()))
        good_proposals = [
            id
            for id, n_violations in proposals.items()
            if n_violations == min_violations
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
    directory = get_predictions(name)
    create_dir(directory)
    target = os.path.join(directory, 'repair-attempt')
    target_final = os.path.join(directory, 'files-repaired')
    create_dir(target)
    create_dir(target_final)
    checkstyle_rules = os.path.join(get_project_dir(name), 'checkstyle.xml')
    dir_files_to_repair = get_input_dir_to_analyze_and_repair(name)
    for (protocol_id, (protocol, path)) in enumerate(protocols_repairs.items()):
        if os.path.exists(path):
            for file_id in os.listdir(path):
                protocol_target = os.path.join(target, file_id, f'batch_{protocol_id}')
                if os.path.exists(protocol_target):
                    shutil.rmtree(protocol_target)
                if path is not None and os.path.exists(path):
                    shutil.copytree(os.path.join(path, file_id), protocol_target)
    checkstyle_result, number_of_violations = checkstyle.check(checkstyle_rules, target, checkstyle_jar, only_targeted=True)
    if checkstyle_result is None:
        return {}
    res = reverse_collection(get_batch_results(checkstyle_result, n_batch=len(protocols_repairs)))

    def select_best_proposal(file_id, proposals):
        file_path = glob.glob(f'{dir_files_to_repair}/{int(file_id)}/*.java')[0]
        min_violations = min(list(proposals.values()))
        good_proposals = [
            id
            for id, n_violations in proposals.items()
            if n_violations == min_violations
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

def gen_training_data(repo, dataset_info, checkstyle_file_path, project_name, violation_free_file_dir_path=None):
    protocols = (('random', 'three_grams'))
    try:
        checkstyle_jar = dataset_info["checkstyle_jar"]

        if violation_free_file_dir_path is None:
            violation_free_file_dir_path = create_corpus(
                repo,
                dataset_info,
                project_name,
                checkstyle_file_path
            )

        corpus_files = dict()
        l = len(violation_free_file_dir_path)
        id = 0
        for folder in os.walk(violation_free_file_dir_path):
            files = folder[2]
            if len(folder[0]) >= l:
                relative_folder = "." + folder[0][l:]
                for file_name in files:
                    if file_name[0] != '.' and file_name.endswith('.java'):
                        corpus_files[id] = (file_name, relative_folder, os.path.join(folder[0], file_name))
                        id += 1
            else:
                pass
        
        share = { key: core_config['DATASHARE'].getfloat(key) for key in ['learning', 'validation', 'testing'] }
        for protocol in protocols:
            gotify.notify('[data generation]', f'Start {protocol} on {project_name}')
            synthetic_dataset_dir_by_protocol = f'{get_synthetic_dataset_dir_by_protocol(project_name, protocol)}'
            synthetic_violation_generator.gen_dataset(corpus_files, checkstyle_file_path, share, core_config['DATASHARE'].getint('number_of_synthetic_violations'), synthetic_dataset_dir_by_protocol, checkstyle_jar, protocol=protocol)
            gotify.notify('[data generation]', f'Done {protocol} on {project_name}')
    except:
        logger.exception("Something went wrong during the training data generation.")
        #delete_dir_if_exists(get_violation_free_file_dir(project_name))

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
            violations_dataset_dir = get_input_dir_to_analyze_and_repair(dataset)
            dataset_info = open_json(os.path.join(violations_dataset_dir, 'info.json'))
            checkstyle_jar = dataset_info["checkstyle_jar"]

            results = {}
            for protocol in tqdm(protocols, desc='protocol'):
                dir_repaired_files_by_protocol = get_predictions_by_protocol(dataset, protocol)
                create_dir(dir_repaired_files_by_protocol)
                dir_files_to_repair = get_input_dir_to_analyze_and_repair(dataset)
                results[protocol] = repair_files(dir_repaired_files_by_protocol, dir_files_to_repair, dataset, protocol, checkstyle_jar, only_formatting=True)

            ## join protocols 
            choices = join_protocols(dataset, results, checkstyle_jar)
            for choice in choices.values():
                if 'batch_0' in choice:
                    protocol_choice_count['random'] += 1
                if 'batch_1' in choice:
                    protocol_choice_count['three_grams'] += 1
            save_json(get_predictions_folder(dataset), 'protocol_choice_stats.json', protocol_choice_count)

        time_elapsed = datetime.now() - start_time
        logger.debug('Time elapsed (hh:mm:ss.ms) {}'.format(time_elapsed))

    if args[1] == 'gen_training_data':
        start_time = datetime.now()

        violations_dataset_name = args[2]
        violations_dataset_dir = get_input_dir_to_analyze_and_repair(violations_dataset_name)
        dataset_info = open_json(os.path.join(violations_dataset_dir, 'info.json'))

        (repo_user, repo_name) = dataset_info['repo_url'].split('/')[-2:]
        checkstyle_file_path = os.path.join(get_project_dir(violations_dataset_name), 'checkstyle.xml')
        
        violation_free_file_dir_path = None
        if os.path.exists(get_violation_free_file_dir(violations_dataset_name)):
            violation_free_file_dir_path = get_violation_free_file_dir(violations_dataset_name)
        
        repo, repo_dir = git_helper.open_repo(repo_user, repo_name)

        logger.debug("Starting data generation")

        gen_training_data(repo, dataset_info, checkstyle_file_path, violations_dataset_name, violation_free_file_dir_path=violation_free_file_dir_path)

        gotify.notify('[done][data generation]', violations_dataset_name)

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
        train_steps = args[8]
        save_checkpoint_steps = args[9]
        gpu = args[10]
        run_train(project_name, protocol, global_attention, layers, rnn_size, word_vec_size, train_steps, save_checkpoint_steps, gpu)

        time_elapsed = datetime.now() - start_time
        logger.debug('Time elapsed (hh:mm:ss.ms) {}'.format(time_elapsed))


if __name__ == "__main__":
    main(sys.argv)
