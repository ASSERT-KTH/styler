from javalang import tokenizer as javalang_tokenizer

def is_whitespace_token(token: str) -> bool:
    if ( 'SP' in token or 'NL' in token ) and '_' in token:
        return True
    return False

def whitespace_token_to_tuple(token: str) -> tuple:
    spaces: int = 0
    new_line: int = 0
    if 'SP' in token:
        spaces = int(token.split('_')[0])
    elif 'NL' in token:
        new_line = int(token.split('_')[0])
        if 'DD' in token or 'ID' in token:
            spaces = int(token.split('_')[2])
        if 'DD' in token:
            spaces = -spaces

    return (new_line, spaces)

def get_line_indent(line):
    indent = 0
    for c in line:
        if c == ' ':
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
    if space[0] == 0:
        return f'{space[1]}_SP'
    else:
        result = f'{space[0]}_NL'
        if space[1] == 0:
            pass
        elif space[1] > 0:
            result += f'_{space[1]}_ID'
        else:
            result += f'_{-space[1]}_DD'
        return result
