import json
import os
import subprocess
import java_lang_utils
import shutil
import random
import copy
import uuid

targeted_errors = (
    'MethodParamPad',
    'GenericWhitespace',
    'RegexpMultiline',
    'JavadocTagContinuationIndentation',
    'FileTabCharacter',
    'OneStatementPerLine',
    'Regexp',
    'VisibilityModifier',
    'WhitespaceAround',
    'EmptyLineSeparator',
    'OperatorWrap',
    'NoWhitespaceAfter',
    'SingleLineJavadoc',
    'MethodLength',
    'NoWhitespaceBefore',
    'AnnotationLocation',
    'UnusedImports',
    'JavadocMethod',
    'WhitespaceAfter',
    'LineLength',
    'SeparatorWrap',
    'RegexpSinglelineJava',
    'NoLineWrap',
    'RightCurly',
    'Indentation',
    'ParenPad',
    'JavadocType',
    'MissingDeprecated',
    'RegexpSingleline',
    'LeftCurly',
    'TypecastParenPad'
)

corner_cases_errors = (
    'VisibilityModifier',
    'SingleLineJavadoc',
    'UnusedImports',
    'JavadocMethod',
    'JavadocType',
    'MissingDeprecated'
)


def open_file(file):
    """Open a file and read the content
    """
    if not file:
        return ''
    content = ''
    with open(file, 'r') as file:
        content = file.read()
    return content


def save_file(dir, file_name, content):
    """Write the content in a file
    """
    with open(os.path.join(dir, file_name), 'w') as f:
        f.write(content)


def open_json(file):
    """Read a json file and returns its content has a dict
    """
    with open(file) as f:
        data = json.load(f)
        return data
    return None


def save_json(dir, file_name, content, sort=False):
    """Save a a given dict to the specified location
    """
    with open(os.path.join(dir, file_name), 'w') as f:
        json.dump(content, f, indent=4, sort_keys=sort)


def checkstyle_source_to_error_type(source):
    """Convert a checkstyle error source to an error type
    """
    class_name = source.split('.')[-1]
    if class_name.endswith('Check'):
        type = class_name[:-len('Check')]
    else:
        type = class_name
    return type


def group_by(key_func, iterable):
    """Group all the item of the iterable depending on their key
    """
    result = {}
    for item in iterable:
        key = key_func(item)
        if key not in result:
            result[key] = []
        result[key] += [item]
    return result


def find_all(path, name):
    """Find all the files that have this name
    """
    result = []
    for root, dirs, files in os.walk(path):
        if name in files:
            result.append(os.path.join(root, name))
    return result


def create_dir(dir):
    """Create the dir if the dir does not exists
    """
    if not os.path.exists(dir):
        os.makedirs(dir)
    return dir


def call_java(jar, args):
    """Call java
    """
    cmd = "java -jar {} {}".format(jar, " ".join(args))
    process = subprocess.Popen(cmd.split(" "), stdout=subprocess.PIPE)
    output = process.communicate()[0]
    return output


def list_folders(dir):
    """Give a list of all the folders in a given dir
    """
    return [ folder for folder in os.listdir(dir) if os.path.isdir(os.path.join(dir, folder)) ]


def list_dir_full_path(dir):
    """List the dir content whith the full path
    """
    return [ os.path.join(dir, element) for element in os.listdir(dir) ]


def is_odd(number):
    return number % 2 == 0


def move_parse_exception_files(from_dir, to_dir):
    """Move all the .java recursively contained in the dir that are not parsable
    """
    files = java_lang_utils.get_bad_formated(from_dir)
    create_dir(to_dir)
    for file in files:
        shutil.move(file, f'{to_dir}/{uuid.uuid4().hex}.java')
    return files


def json_pp(obj):
    """Pretty print a collection
    """
    print(json.dumps(obj, indent=4))


def shuffled(array):
    """Return a shallow copy of the list shuffled
    """
    array_copy = copy.copy(array)
    random.shuffle(array_copy)
    return array_copy


def safe_get_first(l):
    """Get the first element. Returns None if len == 0
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
    :return:
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

def delete_dir(path):
    shutil.rmtree(path)
