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
        else:
            if end_of_token[0] == next_token_position[0]:
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
                    whitespace.append((next_token_position[0] - end_of_token[0] - tokens[index].value.count('\n'), spaces, space_type))
                    indentation_last_line = next_token_position[1]
                else:
                    whitespace.append((next_token_position[0] - end_of_token[0] - tokens[index].value.count('\n'), next_token_position[1] - 1, space_type))
    
    count_line_break = 0
    for index in range(len(file_content)-1, 0, -1):
        if file_content[index] == '\n':
            count_line_break += 1
        elif file_content[index] != ' ' and file_content[index] != '\t':
            break

    whitespace.append((count_line_break, 0, 'None'))

    return whitespace, tokens

def tokenize_file_to_repair(file_path, violation):
    spaces, tokens = tokenize_with_white_space(open_file(file_path))

    info = {}

    n_lines = 6

    token_started = False
    token_line_start = -1
    token_line_end = -1
    count = 0

    tokens_violating = []

    context_beginning_token = len(tokens)
    context_end_token = 0

    violation_beginning_token = 0
    violation_end_token = 0

    for token, space in zip(tokens, spaces):
        if token.position[0] >= int(violation['line']) - n_lines and token.position[0] <= int(violation['line']) + n_lines:
            context_beginning_token = min(count, context_beginning_token)
            context_end_token = max(count, context_end_token)
        if not token_started and int(violation['line']) == token.position[0]:
            token_started = True
            token_line_start = count
        if token_started and int(violation['line']) < token.position[0]:
            token_started = False
            token_line_end = count
        count += 1
    context_beginning_token = max(0, context_beginning_token - 1)
    context_end_token = min(len(tokens), context_end_token + 1)
    if token_line_end == -1:
        token_line_end = token_line_start

    # print(violation)

    violating_token_index = -1
    if 'column' in violation:
        around = 1
        column = int(violation['column'])

        if column <= tokens[token_line_start].position[1]:
            violating_token_index = token_line_start
        elif column >= tokens[token_line_end - 1].position[1]:
            violating_token_index = token_line_end - 1
        else:
            index = token_line_start
            for token in tokens[token_line_start:token_line_end]:
                if token.position[1] <= column:
                    violating_token_index = index
                index += 1

        violation_beginning_token = max(0, violating_token_index - around)
        violation_end_token = min(len(tokens), violating_token_index + around)
    else:
        around = 1
        around_after = 1
        if token_line_start != -1:
            violation_beginning_token = max(context_beginning_token, token_line_start - around)
            violation_end_token = min(context_end_token, token_line_end + around_after)
        else:
            for token, index in zip(tokens,range(len(tokens))):
                if token.position[0] < int(violation['line']):
                    violating_token_index = index
            violation_beginning_token = max(0, violating_token_index - around)
            violation_end_token = min(len(tokens), violating_token_index + around_after)
    

    tokens_violating_in_tag = []
    for token, space in zip(tokens[violation_beginning_token:violation_end_token], spaces[violation_beginning_token:violation_end_token]):
        tokens_violating_in_tag.append(get_token_value(token))
        tokens_violating_in_tag.append(get_space_value(space))


    for token, space in zip(tokens[context_beginning_token:violation_beginning_token], spaces[context_beginning_token:violation_beginning_token]):
        tokens_violating.append(get_token_value(token))
        tokens_violating.append(get_space_value(space))
    tokens_violating.append(f'<{violation["type"]}>')
    for token, space in zip(tokens[violation_beginning_token:violation_end_token], spaces[violation_beginning_token:violation_end_token]):
        tokens_violating.append(get_token_value(token))
        tokens_violating.append(get_space_value(space))
    tokens_violating.append(f'</{violation["type"]}>')
    for token, space in zip(tokens[violation_end_token:context_end_token], spaces[violation_end_token:context_end_token]):
        tokens_violating.append(get_token_value(token))
        tokens_violating.append(get_space_value(space))


    info['violation_beginning_token'] = violation_beginning_token
    info['violation_end_token'] = violation_end_token
    info['context_beginning_token'] = context_beginning_token
    info['context_end_token'] = context_end_token
    info['violation'] = violation
    info['tokens_violating_in_tag'] = tokens_violating_in_tag

    return tokens_violating, info

def tokenize_file_to_repair_for_model(file, file_orig, violation_metadata):
    tokens_violating, info = tokenize_file_to_repair(file, violation_metadata)

    tokens_violating_in_tag = info['tokens_violating_in_tag']
    violation_beginning_token = info['violation_beginning_token']
    violation_end_token = info['violation_end_token']

    spaces, tokens = tokenize_with_white_space(open_file(file_orig))
    tokens_correct = []

    for token, space in zip(tokens[violation_beginning_token:violation_end_token], spaces[violation_beginning_token:violation_end_token]):
        tokens_correct.append(get_token_value(token))
        tokens_correct.append(get_space_value(space))

    if len(tokens_violating_in_tag) != len(tokens_correct):
        print("WHAAAAATT")
    info['count_diff'] = 0
    for t_A, t_B in zip(tokens_violating_in_tag, tokens_correct):
        if t_A != t_B:
            info['count_diff'] += 1

    return tokens_violating, tokens_correct, tokens_violating_in_tag, info

def de_tokenize(violating_source, violation_info, new_tokens, only_formatting=False):
    violating_whitespace, tokens = tokenize_with_white_space(violating_source)
    whitespace = copy.deepcopy(violating_whitespace)
    violation_beginning_token = violation_info['violation_beginning_token']
    violation_end_token = violation_info['violation_end_token']

    if only_formatting:
        new_white_space_tokens = new_tokens
    else:
        new_white_space_tokens = new_tokens[1::2]
    new_white_space = [ whitespace_token_to_tuple(token) for token in new_white_space_tokens ]

    for index in range(min(violation_end_token - violation_beginning_token, len(new_white_space))):
        whitespace[violation_beginning_token + index] = new_white_space[index]

    fixed_source_code = reformat(whitespace, violating_whitespace, tokens)
    #return fixed_source_code

    fixed_whitespace, fixed_tokens = tokenize_with_white_space(fixed_source_code)
    if fixed_tokens is None:
        return None

    return mix_sources(violating_source, fixed_source_code, tokens, fixed_tokens, violation_info)

def reformat(whitespace, violating_whitespace, tokens, relative=True):
    """
    Given the sequence of whitespaces and javat token reformat the java source code
    :return: the java source code
    """
    result = ''
    position = 0
    position_violating = 0
    isFirstLineBreakChanged = True
    positionBasedOnFirstLineBreakChanged = -1
    for ws, ews, t in zip(whitespace, violating_whitespace, tokens):
        if ws[2] == 'TB':
            space = "\t"
        else:
            space = " "
        if ws[0] > 0:
            if relative:
                position_violating = max(position_violating + ews[1], 0)
                if ws[0] == ews[0] and ws[1] == ews[1]:
                    position = max(position + ws[1], 0)
                    result += str(t.value) + "\n" * ws[0] + space * position_violating
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

def compute_abs_char_position(source, tokens, violation_info):
    output_source = ""

    file_lines = [ line + '\n' for line in source.split('\n') ]

    violation_beginning_token = tokens[violation_info['violation_beginning_token']]
    violation_beginning_token_position = violation_beginning_token.position

    violation_end_token = tokens[min(violation_info['violation_end_token'], len(tokens) - 1)]
    violation_end_token_position = violation_end_token.position

    output_source += ''.join(file_lines[:violation_beginning_token_position[0] -1] )

    # copy all code before the changes
    partial_start_line = file_lines[violation_beginning_token_position[0] -1]
    partial_start_line = partial_start_line[0: violation_beginning_token_position[1] + len(violation_beginning_token.value) - 1]
    output_source += partial_start_line

    index_start_changes = len(output_source)

    # where the change starts
    change_start_line = file_lines[violation_beginning_token_position[0] - 1]
    end_line = len(change_start_line) 
    if violation_beginning_token_position[0] == violation_end_token_position[0]:
        # all the fix will be copied
        end_line = violation_end_token_position[1] - 1
    change_start_line = change_start_line[violation_beginning_token_position[1] + len(violation_beginning_token.value) - 1:end_line]
    output_source += change_start_line

    # copy all full line changes in the line before the change
    output_source += ''.join(file_lines[violation_beginning_token_position[0]:violation_end_token_position[0] - 1])

    if violation_beginning_token_position[0] != violation_end_token_position[0]:
        # copy the partial line where the change stops if the changes are spread on more than one line
        change_end_line = file_lines[violation_end_token_position[0] - 1]
        change_end_line = change_end_line[0:violation_end_token_position[1] - 1]
        output_source += change_end_line

    index_end_changes = len(output_source)

    # where the change end
    change_end_line = file_lines[violation_end_token_position[0] - 1]
    change_end_line = change_end_line[violation_end_token_position[1] - 1:]
    output_source += change_end_line

    # copy all full line changes in the line before the change
    output_source += ''.join(file_lines[violation_end_token_position[0]:])[:-1]


    return (index_start_changes,index_end_changes)
    pass

def mix_sources(violating_source, fixed_source_code, tokens, fixed_tokens, violation_info):
    """Put a little bit of B into A
    """

    violation_position = compute_abs_char_position(violating_source, tokens, violation_info)
    fix_position = compute_abs_char_position(fixed_source_code, fixed_tokens, violation_info)

    output_source = violating_source[:violation_position[0]]

    output_source += fixed_source_code[fix_position[0]:fix_position[1]]

    output_source += violating_source[violation_position[1]:]
    
    return output_source

if __name__ == "__main__":
    if sys.argv[1] == 'tokenize_file_to_repair':
        path = sys.argv[2]
        violation = open_json(os.path.join(os.path.dirname(path), 'metadata.json'))
        violation = violation['violations'][0]
        violation['type'] = checkstyle_source_to_violation_type(violation['source'])
        tokens_violating, info = tokenize_file_to_repair(path, violation)
        print(tokens_violating)
    if (sys.argv[1] == "tokenize_ws"):
        whitespace, tokens = tokenize_with_white_space(open_file(sys.argv[2]))
        #print(reformat(whitespace, tokens))
        print("\n".join([str(e) for e in zip(whitespace, tokens)]))


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
