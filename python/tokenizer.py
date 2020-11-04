from core import *
from javalang import tokenizer as javalang_tokenizer
from token_utils import *

def tokenize_with_white_space(file_content, relative=True, new_line_at_the_end_of_file=True):
    """
    Tokenize the java source code
    :param file_content: the java source code
    :return: (whitespace, tokens)
    """
    position_last_line = 1;
    tokens = javalang_tokenizer.tokenize(file_content, parse_comments=True)
    tokens = [ t for t in tokens]
    whitespace = list()
    for index in range(0, len(tokens)-1):
        tokens_position = tokens[index].position;
        next_token_position = tokens[index+1].position;
        end_of_token = (tokens_position[0], tokens_position[1] + len(tokens[index].value))
        if end_of_token == next_token_position:
            whitespace.append((0,0))
        else :
            if ( end_of_token[0] == next_token_position[0] ):
                # same line
                whitespace.append(( 0, next_token_position[1] - end_of_token[1]))
            else:
                # new line
                if relative:
                    whitespace.append(( next_token_position[0] - end_of_token[0] - tokens[index].value.count('\n'), next_token_position[1] - position_last_line))
                    position_last_line = next_token_position[1]
                else:
                    whitespace.append(( next_token_position[0] - end_of_token[0] - tokens[index].value.count('\n'), next_token_position[1] - 1))
    if new_line_at_the_end_of_file:
        whitespace.append((1,0))
    else:
        if file_content[-1] == '\n':
            if file_content[-2] == '\n':
                whitespace.append((2,0))
            else:
                whitespace.append((1,0))
        else:
            whitespace.append((0,0))
    # rewritten = reformat(whitespace, tokens)
    # print(rewritten)
    # return rewritten
    return whitespace, tokens

def tokenize_file_to_repair(file_path, error):
    spaces, tokens = tokenize_with_white_space(open_file(file_path))

    info = {}

    token_started = False
    token_line_start = -1
    token_line_end = -1
    count = 0

    tokens_errored = []
    n_lines = 6

    start = len(tokens)
    end = 0

    from_token = 0
    to_token = 0

    for token, space in zip(tokens, spaces):
        if token.position[0] >= int(error['line']) - n_lines and token.position[0] <= int(error['line']) + n_lines :
            start = min(count, start)
            end = max(count, end)
        if not token_started and int(error['line']) == token.position[0]:
            token_started = True
            token_line_start = count
        if token_started and  int(error['line']) < token.position[0]:
            token_started = False
            token_line_end = count
        count += 1
    start = max(0, start - 2)
    end = min(len(tokens), end + 2)
    if token_line_end == -1:
        token_line_end = token_line_start

    # print(error)

    if 'column' in error and error['type'] != 'OneStatementPerLine':
        errored_token_index = -1
        around = 10
        for token, index in zip(tokens,range(len(tokens))):
            if token.position[0] <= int(error['line']) and token.position[1] <= int(error['column']):
                errored_token_index = index
        from_token = max(0, errored_token_index - around)
        to_token = min(len(tokens), errored_token_index + 1 + around)
    else:
        around = 2
        around_after = 13
        errored_token_index = -1
        if token_line_start != -1:
            from_token = max(start, token_line_start - around)
            to_token = min(end, token_line_end + around_after + 1)
        else:
            errored_token_index = -1
            around = 2
            around_after = 18
            for token, index in zip(tokens,range(len(tokens))):
                if token.position[0] < int(error['line']):
                    errored_token_index = index
            from_token = max(0, errored_token_index - around)
            to_token = min(len(tokens), errored_token_index + 1 + around_after)
    tokens_errored_in_tag = []
    for token, space in zip(tokens[from_token:to_token], spaces[from_token:to_token]):
        tokens_errored_in_tag.append(get_token_value(token))
        tokens_errored_in_tag.append(get_space_value(space))


    for token, space in zip(tokens[start:from_token], spaces[start:from_token]):
        tokens_errored.append(get_token_value(token))
        tokens_errored.append(get_space_value(space))
    tokens_errored.append(f'<{error["type"]}>')
    for token, space in zip(tokens[from_token:to_token], spaces[from_token:to_token]):
        tokens_errored.append(get_token_value(token))
        tokens_errored.append(get_space_value(space))
    tokens_errored.append(f'</{error["type"]}>')
    for token, space in zip(tokens[to_token:end], spaces[to_token:end]):
        tokens_errored.append(get_token_value(token))
        tokens_errored.append(get_space_value(space))

    info['from_token'] = from_token
    info['to_token'] = to_token
    info['start'] = start
    info['end'] = end
    info['error'] = error
    info['tokens_errored_in_tag'] = tokens_errored_in_tag

    return tokens_errored, info

def tokenize_errored_file_model2(file, file_orig, error):
    tokens_errored, info = tokenize_file_to_repair(file, error)

    tokens_errored_in_tag = info['tokens_errored_in_tag']
    from_token = info['from_token']
    to_token = info['to_token']

    spaces, tokens = tokenize_with_white_space(open_file(file_orig))
    tokens_correct = []

    for token, space in zip(tokens[from_token:to_token], spaces[from_token:to_token]):
        tokens_correct.append(get_token_value(token))
        tokens_correct.append(get_space_value(space))

    if len(tokens_errored_in_tag) != len(tokens_correct):
        print("WHAAAAATT")
    info['count_diff'] = 0
    for t_A, t_B in zip(tokens_errored_in_tag, tokens_correct):
        if t_A != t_B:
            info['count_diff'] += 1

    return tokens_errored, tokens_correct, tokens_errored_in_tag, info

def de_tokenize(errored_source, error_info, new_tokens, tabulations, only_formatting=False):
    whitespace, tokens = tokenize_with_white_space(errored_source)
    from_token = error_info['from_token']
    to_token = error_info['to_token']


    if only_formatting:
        new_white_space_tokens = new_tokens
    else:
        new_white_space_tokens = new_tokens[1::2]
    # print(new_white_space_tokens)
    new_white_space = [ whitespace_token_to_tuple(token) for token in new_white_space_tokens ]
    # print(new_white_space)

    # whitespace[from_token:to_token] = new_white_space
    # whitespace[from_token:min(from_token + len(new_white_space),to_token)] = new_white_space[:min(to_token - from_token, len(new_white_space))]
    for index in range(min(to_token - from_token, len(new_white_space))):
        whitespace[from_token + index] = new_white_space[index]

    result = reformat(whitespace, tokens, tabulations=tabulations)

    if 'error' in error_info:
        line = int(error_info['error']['line'])
        return mix_sources(errored_source, result, line-1, to_line=line+1) #result
    else:
        return mix_sources(errored_source, result, tokens[from_token].position[0], to_line=tokens[to_token].position[0]) #result
    # return mix_sources(errored_source, result, tokens[from_token].position[0], to_line=tokens[to_token].position[0])

def mix_files_v2(file_A_path, file_B_path, output_file, from_line, to_line=-1):
    java_source_A = open_file(file_A_path)
    whitespace_A, tokens_A = tokenize_with_white_space(java_source_A, relative=True)

    java_source_B = open_file(file_B_path)
    whitespace_B, tokens_B = tokenize_with_white_space(java_source_B, relative=True)

    if to_line == -1:
        to_line = from_line

    from_token = len(tokens_A)
    to_token = 0

    for pos, token in enumerate(tokens_A):
        if token.line < from_line:
            from_token = pos
        if token.line <= to_line:
            to_token = pos
    from_token += 1

    tokens = tokens_A
    whitespace = whitespace_A[:from_token] + whitespace_B[from_token:to_token+1] + whitespace_A[to_token+1:]

    new_java_source = reformat(whitespace, tokens, relative = True)

    output_dir = output_file.split('/')[:-1]
    file_name = output_file.split('/')[-1]

    return save_file(output_dir, file_name, new_java_source)


def mix_sources(source_A, source_B, from_line, to_line=-1):
    """Put a little bit of B into A
    """
    if to_line == -1:
        to_line = from_line

    file_A_lines = [ line + '\n' for line in source_A.split('\n') ]
    file_B_lines = [ line + '\n' for line in source_B.split('\n') ]

    tokens_A = javalang_tokenizer.tokenize(source_A)
    tokens_B = javalang_tokenizer.tokenize(source_B)

    tokens = zip(tokens_A, tokens_B)
    lines = range(from_line, to_line)

    output_source = ""

    first_part = ''.join(file_A_lines[:(from_line-1)])
    output_source += first_part
    from_token = None
    first_token_of_A = None
    to_token = None
    last_token_of_A = None
    for token_A, token_B in tokens:
        if token_A.position[0] >= from_line and token_A.position[0] <= to_line:
            if 'form_token' not in locals():
                form_token = token_B
                first_token_of_A = token_A
            to_token = token_B
            last_token_of_A = token_A
    # print(first_token_of_A,last_token_of_A)
    if last_token_of_A:
        if first_token_of_A.position[0] != from_line:
            output_source += ''.join(file_A_lines[(from_line-1):(first_token_of_A.position[0]-1)])
        output_source += " "*(first_token_of_A.position[1]-1)
        output_source += source_B[(len(''.join(file_B_lines[:(form_token.position[0]-1)])) + form_token.position[1] - 1):(len(''.join(file_B_lines[:(to_token.position[0]-1)])) + to_token.position[1] + len(to_token.value) - 1)]
        output_source += '\n'
        if last_token_of_A.position[0] != to_line:
            output_source += ''.join(file_A_lines[(last_token_of_A.position[0]):(to_line)])
        output_source += ''.join(file_A_lines[(to_line):])
    else:
        output_source += ''.join(file_A_lines[(from_line-1):])

    return output_source

# The tokens should be the same
# Patch parts of B into A,
def mix_files(file_A_path, file_B_path, output_file, from_line, to_line=-1):
    """Put a little bit of B into A
    """
    if to_line == -1:
        to_line = from_line

    with open(file_A_path) as f:
        file_A_lines = f.readlines()

    try:
        with open(file_B_path) as f:
            file_B_lines = f.readlines()
    except FileNotFoundError:
        with open(output_file, "w") as output_file_object:
            output_file_object.write("".join(file_A_lines))
            return output_file

    file_A_content = "".join(file_A_lines)
    file_B_content = "".join(file_B_lines)

    output_source = mix_sources(file_A_content, file_B_content, from_line, to_line=to_line)

    output_dir = "/".join(output_file.split("/")[:-1])
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)

    with open(output_file, "w") as output_file_object:
        output_file_object.write(output_source)

    return output_file


def reformat(whitespace, tokens, tabulations=False, relative=True):
    """
    Given the sequence of whitespaces and javat token reformat the java source code
    :return: the java source code
    """
    result = ''
    position = 0
    for ws, t in zip(whitespace, tokens):
        if ws[0] > 0:
            if relative:
                position = max(position + ws[1], 0)
                if tabulations:
                    result += str(t.value) + "\n" * ws[0] + "\t" * position
                else:
                    result += str(t.value) + "\n" * ws[0] + " " * position
            else:
                if tabulations:
                    result += str(t.value) + "\n" * ws[0] + "\t" * ws[1]
                else:
                    result += str(t.value) + "\n" * ws[0] + " " * ws[1]
        else:
            result += str(t.value) + " " * ws[1]
    return result


def get_char_pos_from_lines(file_path, from_line, to_line=-1):
    """
    Tokenize the java source code
    :param file_content: the java source code
    :return: (whitespace, tokens)
    """
    if to_line == -1:
        to_line = from_line
    file_lines = None
    with open(file_path) as f:
        file_lines = f.readlines()
    if file_lines:
        from_char = len(''.join(file_lines[:(from_line-1)]))
        to_char = from_char + len(''.join(file_lines[(from_line-1):to_line]))
        return (from_char, to_char)
    else:
        return (-1, -1)


if __name__ == "__main__":
    if (sys.argv[1] == "char_pos"):
        print(get_char_pos_from_lines(sys.argv[2], int(sys.argv[3])))
    elif (sys.argv[1] == "tokenize_ws"):
        whitespace, tokens = tokenize_with_white_space(open_file(sys.argv[2]))
        #print(reformat(whitespace, tokens))
        print("\n".join([str(e) for e in zip(whitespace, tokens)]))
    elif (sys.argv[1] == "mix"):
        mix_files(sys.argv[2], sys.argv[3], sys.argv[4], 62, 64)


class TokenizedSource:
    def __init__(self, white_spaces, tokens, tabulation=False, relative=True):
        self.tokens = tokens
        self.white_spaces = white_spaces
        self.tabulation = tabulation
        self.relative = relative

    def reformat(self):
        return reformat(self.white_spaces, self.tokens, tabulations=self.tabulation, relative=self.relative)

    def enumerate_3_grams(self):
        return enumerate(zip(self.tokens, self.white_spaces, self.tokens[1:]))

class Tokenizer:
    def __init__(self, tabulation=False, relative=True):
        self.tabulation = tabulation
        self.relative = relative

    def tokenize(self, source):
        white_spaces, tokens = tokenize_with_white_space(source, relative=self.relative)
        return TokenizedSource(white_spaces, tokens, tabulation=self.tabulation, relative=self.relative)
