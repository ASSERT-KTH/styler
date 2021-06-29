from core import *
from javalang import tokenizer as javalang_tokenizer
from token_utils import *

import copy

def tokenize_with_white_space(file_content, relative=True):
    """
    Tokenize the java source code
    :param file_content: the java source code
    :return: (whitespace, tokens)
    """
    indentation_last_line = 1
    file_content_lines = file_content.split('\n')
    javalang_tokens = javalang_tokenizer.tokenize(file_content, parse_comments=True)
    tokens = []
    count = 0
    try:
        for t in javalang_tokens:
            count += 1
            if count > 1000000:
                break
            tokens.append(t)
            pass
    except Exception as err:
        print('Something wrong happened while tokenizing the following content: ' + file_content)
        return None, None
    whitespace = list()
    for index in range(0, len(tokens)-1):
        tokens_position = tokens[index].position
        next_token_position = tokens[index+1].position
        end_of_token = (tokens_position[0], tokens_position[1] + len(tokens[index].value))
        if end_of_token == next_token_position:
            whitespace.append((0,0,'None'))
        else :
            if ( end_of_token[0] == next_token_position[0] ):
                # same line
                if file_content_lines[tokens_position[0]-1] is not '':
                    if len(file_content_lines[tokens_position[0]-1]) > end_of_token[1] and file_content_lines[tokens_position[0]-1][end_of_token[1]] == '\t':
                        space_type = 'TB'
                    else:
                        space_type = 'SP'
                else:
                    space_type = 'None'
                whitespace.append(( 0, next_token_position[1] - end_of_token[1], space_type))
            else:
                # new line
                new_line = file_content_lines[next_token_position[0]-1]
                if new_line is not '':
                    if new_line[get_line_indent(new_line) - 1] == '\t':
                        space_type = 'TB'
                    else:
                        space_type = 'SP'
                else:
                    space_type = 'None'
                if relative:
                    spaces = next_token_position[1] - indentation_last_line
                    whitespace.append(( next_token_position[0] - end_of_token[0] - tokens[index].value.count('\n'), spaces, space_type))
                    indentation_last_line = next_token_position[1]
                else:
                    whitespace.append(( next_token_position[0] - end_of_token[0] - tokens[index].value.count('\n'), next_token_position[1] - 1, space_type))
    
    count_line_break = 0
    for index in range(len(file_content)-1, 0, -1):
        if file_content[index] == '\n':
            count_line_break += 1
        elif file_content[index] != ' ' and file_content[index] != '\t':
            break

    whitespace.append((count_line_break, 0, 'None'))

    return whitespace, tokens

def tokenize_file_to_repair(file_path, error):
    spaces, tokens = tokenize_with_white_space(open_file(file_path))

    info = {}

    n_lines = 6

    token_started = False
    token_line_start = -1
    token_line_end = -1
    count = 0

    tokens_errored = []

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

    if 'column' in error:
        errored_token_index = -1
        around = 1
        column = int(error['column'])

        if column <= tokens[token_line_start].position[1]:
            errored_token_index = token_line_start
        elif column >= tokens[token_line_end - 1].position[1]:
            errored_token_index = token_line_end - 1
        else:
            index = token_line_start
            for token in tokens[token_line_start:token_line_end]:
                if token.position[1] <= column:
                    errored_token_index = index
                index += 1

        from_token = max(0, errored_token_index - around)
        to_token = min(len(tokens), errored_token_index + around)
    else:
        around = 1
        around_after = 1
        errored_token_index = -1
        if token_line_start != -1:
            from_token = max(start, token_line_start - around)
            to_token = min(end, token_line_end + around_after + 1)
        else:
            errored_token_index = -1
            around = 1
            around_after = 1
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

def de_tokenize(errored_source, error_info, new_tokens, only_formatting=False):
    errored_whitespace, tokens = tokenize_with_white_space(errored_source)
    whitespace = copy.deepcopy(errored_whitespace)
    from_token = error_info['from_token']
    to_token = error_info['to_token']

    if only_formatting:
        new_white_space_tokens = new_tokens
    else:
        new_white_space_tokens = new_tokens[1::2]
    new_white_space = [ whitespace_token_to_tuple(token) for token in new_white_space_tokens ]

    # whitespace[from_token:to_token] = new_white_space
    # whitespace[from_token:min(from_token + len(new_white_space),to_token)] = new_white_space[:min(to_token - from_token, len(new_white_space))]
    for index in range(min(to_token - from_token, len(new_white_space))):
        whitespace[from_token + index] = new_white_space[index]

    fixed_source_code = reformat(whitespace, errored_whitespace, tokens)
    #return fixed_source_code

    fixed_whitespace, fixed_tokens = tokenize_with_white_space(fixed_source_code)
    if fixed_tokens is None:
        return None

    return mix_sources3(errored_source, fixed_source_code, tokens, fixed_tokens, error_info)

def compute_abs_char_position(source, tokens, error_info):
    output_source = ""

    file_lines = [ line + '\n' for line in source.split('\n') ]

    from_token = tokens[error_info['from_token']]
    from_token_position = from_token.position

    to_token = tokens[min(error_info['to_token'], len(tokens) - 1)]
    to_token_position = to_token.position

    output_source += ''.join(file_lines[:from_token_position[0] -1] )

    # copy all code before the changes
    partial_start_line = file_lines[from_token_position[0] -1]
    partial_start_line = partial_start_line[0: from_token_position[1] + len(from_token.value) - 1]
    output_source += partial_start_line

    index_start_changes = len(output_source)

    # where the change starts
    change_start_line = file_lines[from_token_position[0] - 1]
    change_start_line = change_start_line[from_token_position[1] + len(from_token.value) - 1:]
    output_source += change_start_line

    # copy all full line changes in the line before the change
    output_source += ''.join(file_lines[from_token_position[0]:to_token_position[0] - 1])

    # copy the partial line where the chyange stops
    change_end_line = file_lines[to_token_position[0] - 1]
    change_end_line = change_end_line[0:to_token_position[1] + len(to_token.value) - 1]
    output_source += change_end_line

    index_end_changes = len(output_source)

    # where the change starts
    change_end_line = file_lines[to_token_position[0] - 1]
    change_end_line = change_end_line[to_token_position[1] + len(to_token.value) - 1:]
    output_source += change_end_line

    # copy all full line changes in the line before the change
    output_source += ''.join(file_lines[to_token_position[0]:])[:-1]


    return (index_start_changes,index_end_changes)
    pass

def mix_sources3(errored_source, fixed_source_code, tokens, fixed_tokens, error_info):
    """Put a little bit of B into A
    """

    error_position = compute_abs_char_position(errored_source, tokens, error_info)
    fix_position = compute_abs_char_position(fixed_source_code, fixed_tokens, error_info)

    output_source = errored_source[:error_position[0]]

    output_source += fixed_source_code[fix_position[0]:fix_position[1]]

    output_source += errored_source[error_position[1]:]
    
    return output_source

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

    tokens_A = javalang_tokenizer.tokenize(source_A, parse_comments=True)
    tokens_B = javalang_tokenizer.tokenize(source_B, parse_comments=True)

    tokens = zip(tokens_A, tokens_B)
    lines = range(from_line, to_line)

    # copy everything before from from
    output_source = ''.join(file_A_lines[:(from_line-1)])

    # find first and last tokens from the context
    from_token = None
    first_token_of_A = None
    to_token = None
    last_token_of_A = None
    for token_A, token_B in tokens:
        if ((token_A.position[0] >= from_line) 
            and (token_A.position[0] <= to_line )):
            if 'form_token' not in locals():
                form_token = token_B
                first_token_of_A = token_A
            to_token = token_B
            last_token_of_A = token_A

    if last_token_of_A:

        # copy the statement that was potentially before the context
        if first_token_of_A.position[0] > from_line and form_token.position[0] > from_line:
            output_source += ''.join(file_A_lines[from_line-1: first_token_of_A.position[0]-1])
        # if the transformation added a line before the context
        if first_token_of_A.position[0] != form_token.position[0]:
            output_source += ''.join(file_B_lines[first_token_of_A.position[0]-1:form_token.position[0]-1])
        # add the original indentation TODO: handle \t
        #output_source += " " * (first_token_of_A.position[1] - 1)
        
        # copy the transformation
        output_source += ''.join(file_B_lines[(form_token.position[0]-1):(to_token.position[0])])
        for line in file_B_lines[to_token.position[0]+1:]:
            if len(line.strip()) != 0:
                break
            output_source += line

        #start_transormation = len(''.join(file_B_lines[:(form_token.position[0]-1)])) + form_token.position[1] - 1
        #end_transormation = len(''.join(file_B_lines[:(to_token.position[0]-1)])) + to_token.position[1] + len(to_token.value) - 1
        #output_source += source_B[start_transormation:end_transormation]
        #output_source += '\n'
        

        # copy the otkens between the last token and to_line
        if last_token_of_A.position[0] < to_line  and to_token.position[0] < to_line:
            output_source += ''.join(file_A_lines[last_token_of_A.position[0]:to_line])
        
        for line in file_A_lines[to_line -1:]:
            if len(line.strip()) == 0:
                continue
            output_source += line
        
        # copy the rest of the file (everything after to_line)
        #output_source += ''.join(file_A_lines[(to_line):])[:-1] # we remove the final \n that we added

        """
        output_source += " "*(first_token_of_A.position[1]-1)
        print(f'output_source space {output_source.replace(" ","_")}')
        #output_source += source_B[(len(''.join(file_B_lines[:(form_token.position[0]-1)])) + form_token.position[1] - 1):(len(''.join(file_B_lines[:(to_token.position[0]-1)])) + to_token.position[1] + len(to_token.value) - 1)]
        print((first_token_of_A.position[0]-1),(to_token.position[0]))
        output_source += ''.join(file_B_lines[(first_token_of_A.position[0]-1):(to_token.position[0])])
        print(f'output_source source_B {output_source.replace(" ","_")}')
        #output_source += '\n'
        if last_token_of_A.position[0] != to_line:
            output_source += ''.join(file_A_lines[(last_token_of_A.position[0]):(to_line)])
            print(f'output_source if {output_source.replace(" ","_")}')
        output_source += ''.join(file_A_lines[(to_line):])[:-1] # we remove the final \n that we added
        print(f'output_source final {output_source.replace(" ","_")}')"""
    else:
        output_source += ''.join(file_A_lines[(from_line-1):])[:-1] # we remove the final \n that we added

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


def reformat(whitespace, errored_whitespace, tokens, relative=True):
    """
    Given the sequence of whitespaces and javat token reformat the java source code
    :return: the java source code
    """
    result = ''
    position = 0
    position_errored = 0
    isFirstLineBreakChanged = True
    positionBasedOnFirstLineBreakChanged = -1
    for ws, ews, t in zip(whitespace, errored_whitespace, tokens):
        if ws[2] == 'TB':
            space = "\t"
        else:
            space = " "
        if ws[0] > 0:
            if relative:
                position_errored = max(position_errored + ews[1], 0)
                if ws[0] == ews[0] and ws[1] == ews[1]:
                    position = max(position + ws[1], 0)
                    result += str(t.value) + "\n" * ws[0] + space * position_errored
                else:
                    if isFirstLineBreakChanged:
                        positionBasedOnFirstLineBreakChanged = max(position + ws[1], 0)
                    else:
                        positionBasedOnFirstLineBreakChanged = max(positionBasedOnFirstLineBreakChanged + ws[1], 0)
                    result += str(t.value) + "\n" * ws[0] + space * positionBasedOnFirstLineBreakChanged
            else:
                result += str(t.value) + "\n" * ws[0] + space * ws[1]
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
    if sys.argv[1] == 'tokenize_file_to_repair':
        path = sys.argv[2]
        error = open_json(os.path.join(os.path.dirname(path), 'metadata.json'))
        error = error['errors'][0]
        error['type'] = checkstyle_source_to_error_type(error['source'])
        tokens_errored, info = tokenize_file_to_repair(path, error)
        print(tokens_errored)
    if (sys.argv[1] == "char_pos"):
        print(get_char_pos_from_lines(sys.argv[2], int(sys.argv[3])))
    elif (sys.argv[1] == "tokenize_ws"):
        whitespace, tokens = tokenize_with_white_space(open_file(sys.argv[2]))
        #print(reformat(whitespace, tokens))
        print("\n".join([str(e) for e in zip(whitespace, tokens)]))
    elif (sys.argv[1] == "mix"):
        mix_files(sys.argv[2], sys.argv[3], sys.argv[4], 62, 64)


class TokenizedSource:
    def __init__(self, white_spaces, tokens, relative=True):
        self.tokens = tokens
        self.original_whitespace = copy.deepcopy(white_spaces)
        self.white_spaces = white_spaces
        self.relative = relative

    def reformat(self):
        return reformat(self.white_spaces, self.original_whitespace, self.tokens, relative=self.relative)

    def enumerate_3_grams(self):
        return enumerate(zip(self.tokens, self.white_spaces, self.tokens[1:]))

class Tokenizer:
    def __init__(self, relative=True):
        self.relative = relative

    def tokenize(self, source):
        white_spaces, tokens = tokenize_with_white_space(source, relative=self.relative)
        return TokenizedSource(white_spaces, tokens, relative=self.relative)
