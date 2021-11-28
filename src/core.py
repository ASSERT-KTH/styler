# -*- coding: utf-8 -*-

import os
import sys
import json
import subprocess
import shutil
import glob
import uuid
import shlex
from datetime import datetime
from datetime import timedelta
from tqdm import tqdm
import configparser
from loguru import logger
import GPUtil

import javalang
from javalang import parse

core_config = configparser.ConfigParser()
core_config.read(os.path.join(os.path.dirname(__file__), 'config.ini'))

__input_dir = core_config['DEFAULT']['input_dir']
__output_dir = core_config['DEFAULT']['output_dir']
__tmp_dir = core_config['DEFAULT']['tmp_dir']

targeted_violations = (
    'AnnotationLocation',
    'AnnotationOnSameLine',
    'CommentsIndentation',
    'EmptyForInitializerPad',
    'EmptyForIteratorPad',
    'EmptyLineSeparator',
    'FileTabCharacter',
    'GenericWhitespace',
    'Indentation',
    'LeftCurly',
    'LineLength',
    'MethodParamPad',
    #'NewlineAtEndOfFile',
    'NoLineWrap',
    'NoWhitespaceAfter',
    'NoWhitespaceBefore',
    'OneStatementPerLine',
    'OperatorWrap',
    'ParenPad',
    'Regexp',
    'RegexpMultiline',
    'RegexpSingleline',
    'RegexpSinglelineJava',
    'RightCurly',
    'SeparatorWrap',
    'SingleSpaceSeparator',
    'TrailingComment',
    'TypecastParenPad',
    'WhitespaceAfter',
    'WhitespaceAround'
)

protocols = (
    'random',
    'three_grams'
)

styler_tools = tuple([f'styler_{protocol}' for protocol in protocols])

def get_input_dir_to_analyze_and_repair(name):
    return f'{get_project_dir(name)}/{__input_dir}'

def get_output_dir():
    global __output_dir
    if __output_dir[0] != '/':
        __output_dir = os.path.join(os.path.dirname(__file__), __output_dir)
    return __output_dir

def get_project_dir(project_name):
    return f'{get_output_dir()}/{project_name}'

def get_styler_dir(project_name):
    return f'{get_project_dir(project_name)}/styler'

def get_tmp_dir(project_name):
    return f'{get_styler_dir(project_name)}/{__tmp_dir}'

def get_tmp_git_dir(project_name):
    return f'{get_tmp_dir(project_name)}/git'

def get_tmp_batches_dir(project_name):
    return f'{get_tmp_dir(project_name)}/batches'

def get_violation_free_file_dir(project_name):
    return f'{get_styler_dir(project_name)}/violation_free_file_dir'

def get_synthetic_dataset_dir(project_name):
    return f'{get_styler_dir(project_name)}/01_synthetic_violation_dataset'

def get_synthetic_dataset_dir_by_protocol(project_name, protocol):
    return f'{get_synthetic_dataset_dir(project_name)}/{protocol}'

def get_tokenized_dir(project_name):
    return f'{get_styler_dir(project_name)}/02_tokenized_violation_dataset'

def get_tokenized_dir_by_protocol(project_name, protocol):
    return f'{get_tokenized_dir(project_name)}/{protocol}'

def get_preprocessed_dir_by_protocol(project_name, protocol):
    return f'{get_styler_dir(project_name)}/03_preprocessed_violation_dataset/{protocol}'

def get_model_dir(project_name):
    return f'{get_styler_dir(project_name)}/04_models'

def get_model(project_name, protocol):
    if protocol == 'random' and 'MODEL_PATH_RANDOM' in os.environ:
        model_path = os.environ['MODEL_PATH_RANDOM']
    elif protocol == 'three_grams' and 'MODEL_PATH_THREE_GRAMS' in os.environ:
        model_path = os.environ['MODEL_PATH_THREE_GRAMS']
    elif protocol == 'random':
        model_path = 'random-mlp-1-256-512-model_step_5000.pt'
    else:
        model_path = 'three_grams-general-1-512-512-model_step_5000.pt'
    return f'{get_model_dir(project_name)}/{model_path}'

def get_predictions_folder(project_name):
    return f'{get_styler_dir(project_name)}/05_predictions'

def get_predictions(project_name):
    return f'{get_predictions_folder(project_name)}/final'

def get_predictions_by_protocol(project_name, protocol):
    return f'{get_predictions_folder(project_name)}/{protocol}'


def open_file(file_path):
    """
    Opens the file and reads its content
    """
    content = None
    if file_path:
        try:
            with open(file_path, 'r+', encoding='utf-8') as file:
                content = file.read()
        except Exception as err:
            try:
                with open(file_path, 'r+', encoding='ISO-8859-1') as file:
                    content = file.read()
            except Exception as err2:
                logger.debug(f'[Exception] The file {file_path} cannot be read.')
                logger.debug(f'Detail of the first exception: {err}')
                logger.debug(f'Detail of the second exception: {err2}')
    return content


def save_file(dir, file_name, content):
    """
    Write the content in a file
    """
    path = os.path.join(dir, file_name)
    return save_file_in_path(path, content)

def save_file_in_path(file_path, content):
    """
    Writes the content in a file
    """
    try:
        with open(file_path, 'w', encoding='utf-8') as file:
            file.write(content)
    except Exception as err:
        try:
            with open(file_path, 'w', encoding='ISO-8859-1') as file:
                file.write(content)
        except Exception as err2:
            logger.debug(f'[Exception] The file {file_path} cannot be written.')
            logger.debug(f'Detail of the first exception: {err}')
            logger.debug(f'Detail of the second exception: {err2}')
            return None
    return file_path

def open_json(file):
    """
    Read a json file and returns its content has a dict
    """
    if os.path.exists(file):
        with open(file) as f:
            data = json.load(f)
            return data
    logger.debug('File not found:' + file)
    return None


def save_json(dir, file_name, content, sort=False):
    """
    Save a a given dict to the specified location
    """
    with open(os.path.join(dir, file_name), 'w') as f:
        json.dump(content, f, indent=4, sort_keys=sort)


def checkstyle_source_to_violation_type(source):
    """
    Convert a checkstyle violation source to a violation type
    """
    class_name = source.split('.')[-1]
    if class_name.endswith('Check'):
        type = class_name[:-len('Check')]
    else:
        type = class_name
    return type


def group_by(key_func, iterable):
    """
    Group all the item of the iterable depending on their key
    """
    result = {}
    for item in iterable:
        key = key_func(item)
        if key not in result:
            result[key] = []
        result[key] += [item]
    return result


def find_all(path, name):
    """
    Find all the files that have this name
    """
    result = []
    for root, dirs, files in os.walk(path):
        if name in files:
            result.append(os.path.join(root, name))
    return result


def create_dir(dir):
    """
    Create the dir if the dir does not exists
    """
    if not os.path.exists(dir):
        os.makedirs(dir)
    return dir


def call_java(jar, args):
    """
    Call java
    """
    cmd = "java -jar {} {}".format(jar, " ".join(args))
    process = subprocess.Popen(cmd.split(" "), stdout=subprocess.PIPE)
    output = process.communicate()[0]
    return output


def list_folders(dir):
    """
    Give a list of all the folders in a given dir
    """
    return [ folder for folder in os.listdir(dir) if os.path.isdir(os.path.join(dir, folder)) ]


def list_dir_full_path(dir):
    """
    List the dir content whith the full path
    """
    return [ os.path.join(dir, element) for element in os.listdir(dir) ]


def is_odd(number):
    return number % 2 == 0


def move_parse_exception_files(from_dir, to_dir):
    """
    Move all the .java recursively contained in the dir that are not parsable
    """
    files = get_bad_formated(from_dir)
    if to_dir:
        create_dir(to_dir)
        for file in files:
            shutil.move(file, f'{to_dir}/{uuid.uuid4().hex}_{"_".join(file.split("/")[-3:-1])}.java')
    else:
        for file in files:
            os.remove(file)
    return files


def json_pp(obj):
    """
    Pretty print a collection
    """
    logger.debug(json.dumps(obj, indent=4))


def shuffled(array):
    """
    Return a shallow copy of the list shuffled
    """
    array_copy = copy.copy(array)
    random.shuffle(array_copy)
    return array_copy


def safe_get_first(l):
    """
    Get the first element. Returns None if len == 0
    """
    if len(l) > 0:
        return l[0]
    else:
        return None


def if_not_null(data, f):
    """
    Execute the given function if the data is not null
    :param data:
    :param f:
    :return: f(data) if data != None, else None
    """
    if data:
        return f(data)
    return None


def unique(array, key):
    """
    Return a array with unique element according to the key
    :param array:
    :param key:
    :return:
    """
    seen = set()
    return [seen.add(key(obj)) or obj for obj in array if key(obj) not in seen and key(obj) != None]


def dict_count(array):
    result = {}
    for i in array:
        if i not in result:
            result[i] = 0
        result[i] += 1
    return result


def find_all(path, name):
    result = []
    for root, dirs, files in os.walk(path):
        if name in files:
            result.append(os.path.join(root, name))
    return result


def delete_dir_if_exists(path):
    if os.path.exists(path):
        shutil.rmtree(path)


def run_if_true(function, boolean, data):
    """
    Run the given function if the boolean is true.
    Else return the data.
    """
    if boolean:
        return function(data)
    return data


def filter_targeted_violations(violations):
    """
    Return the elements that are targeted
    :param error: list of error sources
    """
    return list(filter(is_violation_type_targeted, violations))


def is_violation_type_targeted(violation):
    """
    Return true if the violation is targeted
    :param violation: source of the checkstyle violation
    """
    violation_type = checkstyle_source_to_violation_type(violation['source'])
    return (violation_type in targeted_violations)


def map_keys(func, dictionary: dict) -> dict:
    """
    Map the keys of a dict
    """
    return { func(key):value for key, value in dictionary.items()}

def identity(a):
    return a

def reverse_collection(collection, key_func=None):
    if key_func == None:
        key_func = identity
    result = {}
    for (keyA, itemsA) in collection.items():
        for (keyB, itemB) in itemsA.items():
            value = key_func(keyB)
            if value not in result:
                result[value] = {}
            result[value][keyA] = itemB
    return result

def check_well_formed(file_path):
    """
    Check if javalang can parse the file
    :param file_path: the java file dir
    """
    with open(file_path) as f:
        file_content = f.read()
    return check_source_well_formed(file_content)


def check_source_well_formed(file_content):
    """
    Check if javalang can parse the file
    :param file_path: the java file dir
    """
    try:
        tree = parse.parse(file_content)
        return True
    except javalang.parser.JavaSyntaxError as error:
        return False
    except StopIteration as error:
        return False
    except:
        return False


def get_bad_formated(dir):
    """
    Get all the bad formated files from a dir
    :param dir: dir to check recursively
    :return: list of path to java files
    """
    bad_formated_files = []
    for folder in os.walk(dir):
        for file_name in folder[2]:
            file_path = os.path.join(folder[0], file_name)
            if file_path.endswith('.java'):
                if ( not check_well_formed(file_path) ):
                    bad_formated_files.append(file_path)
    return bad_formated_files

def is_crlf(file_name):
    with open(file_name, 'rb') as infile:
        for index, line in enumerate(infile.readlines()):
            return line[-2:] == b'\r\n'


def diff(file_A, file_B, unified=True):
    if unified:
        cmd = 'diff -u {} {}'.format(file_A, file_B)
    else:
        cmd = 'diff {} {}'.format(file_A, file_B)
    process = subprocess.Popen(cmd.split(" "), stdout=subprocess.PIPE)
    output = process.communicate()[0]
    return output.decode("utf-8")

def compute_diff(file_A, file_B):
    """
    Check the diff size between file A and B
    :return: the size of the diff
    """
    cmd = 'git diff --no-index {} {}'.format(file_A, file_B)
    process = subprocess.Popen(cmd.split(" "), stdout=subprocess.PIPE)
    output = process.communicate()[0]
    try:
        diff = output.decode('utf-8')
    except:
        diff = output.decode('ISO-8859-1')
    ans = []
    diff_lines = diff.split("\n")
    # remove the header of the diff
    diff_lines = diff_lines[5:]
    removed_lines = 0
    added_lines = 0
    changed_lines = 0
    for line in diff_lines:
        
        if line.startswith('-'):
            removed_lines += 1
            continue

        if line.startswith('+'):
            added_lines += 1
            continue

        if removed_lines == added_lines:
            changed_lines += removed_lines
        else:
            cl = abs(removed_lines - added_lines)
            if removed_lines > added_lines:
                changed_lines += cl + (removed_lines - cl)
            else:
                changed_lines += cl + (added_lines - cl)

        removed_lines = 0
        added_lines = 0

    return (diff, changed_lines)
    
