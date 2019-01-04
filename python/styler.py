import ml
import sys
import json
import os
import subprocess

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
    content = ''
    with open(file, 'r') as file:
        content = file.read()
    return content

def save_file(dir, file_name, content):
    with open(os.path.join(dir, file_name), 'w') as f:
        f.write(content)

def open_json(file):
    with open(file) as f:
        data = json.load(f)
        return data
    return None

def save_json(dir, file_name, content, sort=False):
    with open(os.path.join(dir, file_name), 'w') as f:
        json.dump(content, f, indent=4, sort_keys=sort)

def checkstyle_source_to_error_type(source):
    class_name = source.split('.')[-1]
    if class_name.endswith('Check'):
        type = class_name[:-len('Check')]
    else:
        type = class_name
    return type

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
        for translation in translate(tokenized_errors):
            ml.print_diff(join_token(info['tokens_errored_in_tag']), translation)
            print()

def main(args):
    dir= './styler/test'
    file_path = f'{dir}/CharConsumer.java'
    metadata_path = f'{dir}/metadata.json'
    translate = gen_translator('./styler/model.pt')
    print_translations(file_path, metadata_path, translate)


if __name__ == "__main__":
    main(sys.argv)
