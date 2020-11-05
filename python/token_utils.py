from javalang import tokenizer as javalang_tokenizer

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
        if space[1] == 0:
            result += f'_{space[2]}'
        elif space[1] > 0:
            result += f'_{space[1]}_ID_{space[2]}'
        else:
            result += f'_{-space[1]}_DD_{space[2]}'
        return result
