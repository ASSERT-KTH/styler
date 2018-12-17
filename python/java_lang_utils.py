from javalang import tokenizer
from javalang import parse
import javalang

import subprocess
import random
import intervals as I
import collections
import sys
import os

def gen_ugly(file_path, output_dir, modification_number = (1,0,0,0,0)):
    insertions_sample_size_space = modification_number[0]
    insertions_sample_size_tab = modification_number[1]
    insertions_sample_size_newline = modification_number[2]
    insertions_sample_size = insertions_sample_size_space + insertions_sample_size_tab + insertions_sample_size_newline
    deletions_sample_size_space = modification_number[3]
    deletions_sample_size_newline = modification_number[4]
    deletions_sample_size = deletions_sample_size_space + deletions_sample_size_newline
    # deletions_sample_size = modification_number - insertions_sample_size
    with open(file_path) as f:
        file_lines = f.readlines()
    file_content = "".join(file_lines)

    tokens = tokenizer.tokenize(file_content)
    tokens = [ t for t in tokens]
    # print("\n".join([ str(t) for t in tokens]))


    # Take a sample of locations suitable for insertions
    insertions_sample = random.sample( tokens, min(insertions_sample_size, len(tokens)) )

    insertions = dict();

    insertions_chars = ([' '] * insertions_sample_size_space);
    insertions_chars.extend(['\t'] * insertions_sample_size_tab)
    insertions_chars.extend(['\n'] * insertions_sample_size_newline)
    random.shuffle(insertions_chars)

    for element, char in zip(insertions_sample, insertions_chars):
        insertions[element.position] = char

    # Select every locations suitable for deletions (i.e. before or after a separator/operator)
    deletions_spots = list()
    suitable_for_deletions = [tokenizer.Separator, tokenizer.Operator]
    for index in range(0, len(tokens)-1):
        if ( type(tokens[index]) in suitable_for_deletions):
            prev_token_position = tokens[index-1].position;
            tokens_position = tokens[index].position;
            next_token_position = tokens[index+1].position;
            end_of_prev_token = (prev_token_position[0], prev_token_position[1] + len(tokens[index-1].value))
            end_of_token = (tokens_position[0], tokens_position[1] + len(tokens[index].value))
            if (end_of_prev_token != tokens_position):
                #print("prev : ", tokens[index-1].value , tokens[index].value, tokens[index+1].value, tokens[index].position)
                deletions_spots.append((end_of_prev_token, tokens_position))
            if (end_of_token != next_token_position):
                #print("next : ", tokens[index-1].value , tokens[index].value, tokens[index+1].value, tokens[index].position)
                deletions_spots.append((end_of_token, next_token_position))
    deletions_spots = list(set(deletions_spots))

    # Take a sample of locations suitable for deletions
    deletions_sample = random.sample( deletions_spots, min(deletions_sample_size, len(deletions_spots)) )

    deletions = dict()
    for deletion_intervals in deletions_spots:
        #print(deletion_intervals)
        from_char = deletion_intervals[0]
        to_char = deletion_intervals[1]
        while from_char[0] <= to_char[0]:
            if from_char[0] == to_char[0]:
                interval = I.closedopen(from_char[1], to_char[1] )
            else:
                interval = I.closedopen(from_char[1], I.inf )
            if ( from_char[0] not in deletions):
                deletions[from_char[0]] = list()
            deletions[from_char[0]].append(interval)
            from_char=(from_char[0]+1, 0)


    deletions_spots_chars = dict()
    line_num = 1
    for line in file_lines:
        char_num = 1
        for char in line:
            if ( line_num in deletions ):
                for intervals in deletions[line_num]:
                    if char_num in intervals:
                        if (char not in deletions_spots_chars):
                            deletions_spots_chars[char] = []
                        deletions_spots_chars[char].append((line_num, char_num))
            char_num = char_num + 1
        line_num = line_num + 1


    deletions = []
    if (' ' in deletions_spots_chars):
        deletions.extend(random.sample(deletions_spots_chars[' '], deletions_sample_size_space))
    if ('\n' in deletions_spots_chars):
        deletions.extend(random.sample(deletions_spots_chars['\n'], deletions_sample_size_newline))

    # print(insertions)
    # print(deletions)

    if not os.path.exists(output_dir):
        os.makedirs(output_dir)

    output_path = os.path.join(output_dir, f'./{file_path.split("/")[-1]}')

    # Write the output file
    with open(output_path, "w") as output_file_object:
        line_num = 1
        for line in file_lines:
            char_num = 1
            for char in line:
                skip = False
                if ((line_num, char_num) in deletions):
                    skip = True
                if ((line_num, char_num) in insertions):
                    output_file_object.write(insertions[(line_num, char_num)])
                if ( not skip ):
                    output_file_object.write(char)
                char_num = char_num + 1
            line_num = line_num + 1
    return tuple(set(deletions) | set(insertions.keys()))

# The tokens should be the same
# Patch parts of B into A,
def mix_files(file_A_path, file_B_path, output_file, from_line, to_line=-1):
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

    tokens_A = tokenizer.tokenize(file_A_content)
    tokens_B = tokenizer.tokenize(file_B_content)

    output_dir = "/".join(output_file.split("/")[:-1])
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)

    tokens = zip(tokens_A, tokens_B)
    lines = range(from_line, to_line)
    with open(output_file, "w") as output_file_object:
        first_part = ''.join(file_A_lines[:(from_line-1)])
        output_file_object.write(first_part)
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
        print(first_token_of_A,last_token_of_A)
        if last_token_of_A:
            if first_token_of_A.position[0] != from_line:
                output_file_object.write(''.join(file_A_lines[(from_line-1):(first_token_of_A.position[0]-1)]))
            output_file_object.write(" "*(first_token_of_A.position[1]-1))
            output_file_object.write(file_B_content[(len(''.join(file_B_lines[:(form_token.position[0]-1)])) + form_token.position[1] - 1):(len(''.join(file_B_lines[:(to_token.position[0]-1)])) + to_token.position[1] + len(to_token.value) - 1)])
            output_file_object.write('\n')
            if last_token_of_A.position[0] != to_line:
                output_file_object.write(''.join(file_A_lines[(last_token_of_A.position[0]):(to_line)]))
            output_file_object.write(''.join(file_A_lines[(to_line):]))
        else:
            output_file_object.write(''.join(file_A_lines[(from_line-1):]))

    return output_file

def open_file(file_path):
    with open(file_path) as f:
        content = f.read()
    return content

def reformat(whitespace, tokens, tabulations=False):
    result = ''
    position = 0
    for ws, t in zip(whitespace, tokens):
        if ws[0] > 0:
            position += ws[1]
            if tabulations:
                result += str(t.value) + "\n" * ws[0] + "\t" * position
            else:
                result += str(t.value) + "\n" * ws[0] + " " * position
        else:
            result += str(t.value) + " " * ws[1]
    return result

def tokenize_with_white_space(file_content):
    position_last_line = 1;
    tokens = tokenizer.tokenize(file_content, parse_comments=True)
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
                whitespace.append(( next_token_position[0] - end_of_token[0] - tokens[index].value.count('\n'), next_token_position[1] - position_last_line))
                position_last_line = next_token_position[1]
    whitespace.append((1,0))
    # rewritten = reformat(whitespace, tokens)
    # print(rewritten)
    # return rewritten
    return whitespace, tokens

def get_char_pos_from_lines(file_path, from_line, to_line=-1):
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

def check_well_formed(file_path):
    with open(file_path) as f:
        file_content = f.read()
    try:
        tree = parse.parse(file_content)
        return True
    except javalang.parser.JavaSyntaxError:
        return False
    except:
        return False

def get_bad_formated(dir):
    bad_formated_files = []
    for folder in os.walk(dir):
        for file_name in folder[2]:
            file_path = os.path.join(folder[0], file_name)
            if ( not check_well_formed(file_path) ):
                bad_formated_files.append(file_path)
    return bad_formated_files

def compute_diff_size(file_A, file_B):
    cmd = 'diff {} {}'.format(file_A, file_B)
    process = subprocess.Popen(cmd.split(" "), stdout=subprocess.PIPE)
    output = process.communicate()[0]
    return output.count(b'\n>') + output.count(b'\n<')

if __name__ == "__main__":
    if (sys.argv[1] == "char_pos"):
        print(get_char_pos_from_lines(sys.argv[2], int(sys.argv[3])))
    elif (sys.argv[1] == "ugly"):
        print(gen_ugly( sys.argv[2], sys.argv[3] ))
    elif (sys.argv[1] == "tokenize_ws"):
        whitespace, tokens = tokenize_with_white_space(open_file(sys.argv[2]))
        # print("\n".join([str(e) for e in zip(whitespace, tokens)]))
    elif (sys.argv[1] == "mix"):
        mix_files(sys.argv[2], sys.argv[3], sys.argv[4], 62, 64)
    elif (sys.argv[1] == "diff"):
        print(compute_diff_size(sys.argv[2], sys.argv[3]))
