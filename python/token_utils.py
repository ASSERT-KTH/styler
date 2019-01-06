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
