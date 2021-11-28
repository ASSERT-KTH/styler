from javalang import tokenizer as javalang_tokenizer
import os
import re
import core

def is_whitespace_token(token: str) -> bool:
    if ( 'SP' in token or 'TB' in token or 'NL' in token ) and '_' in token:
        return True
    return False

def whitespace_token_to_tuple(token: str) -> tuple:
    spaces: int = 0
    new_line: int = 0
    splitted_token = token.split('_')
    if 'NL' not in token:
        spaces = int(splitted_token[0])
    elif 'NL' in token:
        new_line = int(splitted_token[0])
        if 'DD' in token or 'ID' in token:
            spaces = int(splitted_token[2])
        if 'DD' in token:
            spaces = -spaces
    
    if 'SP' not in token and 'TB' not in token:
        space_type = 'None'
    else:
        space_type = splitted_token[len(splitted_token) - 1]

    return (new_line, spaces, space_type)

def get_line_indent(line):
    indent = 0
    for c in line:
        if c == ' ' or c == '\t':
            indent+=1
        else:
            return indent

def most_frequent(List):
    return max(set(List), key = List.count)

def get_project_indent(project_dir):
    indent_files = []
    for folder in os.walk(project_dir):
        for file_name in folder[2]:
            file_path = os.path.join(folder[0], file_name)
            if file_path.endswith('.java'):
                file_content = core.open_file(file_path).strip()

                # remove comments
                file_content = re.sub(r'//.*|("(?:\\[^"]|\\"|.)*?")|(?s)/\*.*?\*/', '', file_content, flags=re.MULTILINE)

                indent_lines = []
                for line in file_content.split('\n'):
                    leading_spaces = len(line) - len(line.lstrip())
                    if leading_spaces > 0:
                        indent_lines.append(leading_spaces)
                leading_spaces_file = min(indent_lines, default="EMPTY")
                if leading_spaces_file != "EMPTY":
                    indent_files.append(leading_spaces_file)
    if len(indent_files) == 0:
        return 4
    return most_frequent(indent_files)
    
def get_token_value(token):
    if isinstance(token, javalang_tokenizer.Keyword):
        return token.value
    if isinstance(token, javalang_tokenizer.Separator):
        return token.value

    if isinstance(token, javalang_tokenizer.Comment):
        return token.__class__.__name__
    if isinstance(token, javalang_tokenizer.Literal):
        return token.__class__.__name__
    if isinstance(token, javalang_tokenizer.Operator):
        return token.value
        if token.is_infix():
            return "InfixOperator"
        if token.is_prefix():
            return "PrefixOperator"
        if token.is_postfix():
            return "PostfixOperator"
        if token.is_assignment():
            return "AssignmentOperator"

    return token.__class__.__name__

def get_space_value(space):
    # space[0] is the number of line breaks
    # space[1] is the number of spaces
    # space[2] is the type of spaces (SP, TB, or None)
    if space[0] == 0:
        return f'{space[1]}_{space[2]}'
    else:
        result = f'{space[0]}_NL'
        if space[1] > 0:
            result += f'_{space[1]}_ID_{space[2]}'
        elif space[1] < 0:
            result += f'_{-space[1]}_DD_{space[2]}'
        return result
