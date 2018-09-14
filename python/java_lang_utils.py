from javalang import tokenizer
from javalang import parse
import javalang

import random
import intervals as I
import collections
import sys
import os

def gen_ugly(file_path, output_dir, modification_number = (1,0)):
    insertions_sample_size = modification_number[0]
    deletions_sample_size = modification_number[1]
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

    for element in insertions_sample:
        insertions[element.position] = " "

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

    for deletion_intervals in deletions_sample:
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

    #print(insertions)
    #print(deletions)

    if not os.path.exists(output_dir):
        os.makedirs(output_dir)

    output_path = output_dir + file_path.split("/")[-1]

    # Write the output file
    with open(output_path, "w") as output_file_object:
        line_num = 1
        for line in file_lines:
            char_num = 1
            for char in line:
                skip = False
                if ( line_num in deletions ):
                    for intervals in deletions[line_num]:
                        if char_num in intervals:
                            skip = True
                            continue
                if ( not skip ):
                    if ((line_num, char_num) in insertions):
                        output_file_object.write(insertions[(line_num, char_num)])
                    output_file_object.write(char)
                char_num = char_num + 1
            line_num = line_num + 1
    return output_path

def check_well_formed(file_path):
    with open(file_path) as f:
        file_content = f.read()
    try:
        tree = parse.parse(file_content)
        return True
    except javalang.parser.JavaSyntaxError:
        return False

def get_bad_formated(dir):
    bad_formated_files = []
    for folder in os.walk(dir):
        for file_name in folder[2]:
            file_path = os.path.join(folder[0], file_name)
            if ( not check_well_formed(file_path) ):
                bad_formated_files.append(file_path)
    return bad_formated_files


if __name__ == "__main__":
    if ( len(sys.argv) < 3):
        print(get_bad_formated(sys.argv[1]))
    else:
        print(sys.argv)
        gen_ugly( sys.argv[1], sys.argv[2] )
