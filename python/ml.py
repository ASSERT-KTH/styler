import java_lang_utils as jlu
import tensorflow as tf
from javalang import tokenizer

def vectorize_file(path):
    spaces, tokens = jlu.tokenize_with_white_space(path)
    print("\n".join([str(token) for token in tokens]))
    def get_value(token):
        if isinstance(token, tokenizer.Comment):
            return token.__class__.__name__
        if isinstance(token, tokenizer.Literal):
            return token.__class__.__name__
        if isinstance(token, tokenizer.Operator):
            if token.is_infix():
                return "InfixOperator"
            if token.is_prefix():
                return "PrefixOperator"
            if token.is_postfix():
                return "PostfixOperator"
            if token.is_assignment():
                return "AssignmentOperator"

        return token.value
    tokens_set = set([get_value(token) for token in tokens])
    set_to_map = lambda set_to_map : { item:i for item, i in zip(set_to_map, range(len(set_to_map))) }
    tokens_map = set_to_map(tokens_set)
    print(tokens_map)
    spaces_set = set(spaces)
    spaces_map = set_to_map(spaces_set)

if __name__ == "__main__":
    vectorize_file("./investigations/TypeHandler.java")
