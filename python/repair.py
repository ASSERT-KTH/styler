from core import *
from synthetic import Synthetic_Checkstyle_Error
import java_lang_utils

def call_repair_tool(tool, orig_dir, ugly_dir, output_dir, dataset_metadata):
    if tool == 'naturalize':
        return call_naturalize(orig_dir, ugly_dir, output_dir)
    if tool == 'naturalize_sniper':
        return call_naturalize_sniper(orig_dir, ugly_dir, output_dir)
    if tool == 'codebuff':
        return call_codebuff(orig_dir, ugly_dir, output_dir, grammar=dataset_metadata['grammar'], indent=dataset_metadata['indent'])
    if tool == 'codebuff_sniper':
        return call_codebuff_sniper(orig_dir, ugly_dir, output_dir[:-7],output_dir)

def call_naturalize(orig_dir, ugly_dir, output_dir):
    args = ["-t " + orig_dir, "-o " + output_dir, "-f " + ugly_dir]
    return call_java("../jars/naturalize.jar", args)

def call_naturalize_sniper(orig_dir, ugly_dir, output_dir):
    create_dir(output_dir)
    # TODO rebuild with sniper ...
    args = ["-mode snipper", "-t " + orig_dir, "-o " + output_dir, "-f " + ugly_dir]
    uglies = get_uglies(ugly_dir)
    for ugly in uglies:
        path = ugly.get_errored_path()
    #     erorrs_lines = [ int(e["line"]) for e in file["errors"]]
        erorrs_lines = [ int(ugly.get_metadata()["line"]) ]
        (from_char, to_char) = java_lang_utils.get_char_pos_from_lines(path, min(erorrs_lines) - 1, max(erorrs_lines) + 1)
        args.append(path + ":" + str(from_char) + ',' + str(to_char))
    return call_java("../jars/naturalize.jar", args)

def call_codebuff(orig_dir, ugly_dir, output_dir, grammar = "Java8", indent=2):
    args = ["-g org.antlr.codebuff." + grammar, "-rule compilationUnit", "-corpus " + orig_dir, "-files java", "-comment LINE_COMMENT", "-indent " + str(indent), "-o " + output_dir]
    args.append(ugly_dir)
    return call_java("../jars/codebuff-1.5.1.jar", args)

def call_codebuff_sniper(orig_dir, ugly_dir, codebuff_dir, output_dir):
    uglies = get_uglies(ugly_dir)
    for ugly in uglies:
        file_path = ugly.get_errored_path()
        codebuff_path = f'{codebuff_dir}/{ugly.id}/{ugly.file_name}.java'
        output_path = f'{output_dir}/{ugly.id}/{ugly.file_name}.java'
        erorrs_lines = [ int(ugly.get_metadata()["line"]) ]
        from_line, to_line = (min(erorrs_lines) - 1, max(erorrs_lines) + 1)
        try:
            java_lang_utils.mix_files(file_path, codebuff_path, output_path, from_line, to_line=to_line )
        except FileNotFoundError:
            print("No file (probably codebuff trash)")

def get_uglies(ugly_dir):
    uglies_dir = [
        error
        for error in list_dir_full_path(ugly_dir) if os.path.isdir(error)
    ]
    uglies = [
        Synthetic_Checkstyle_Error(error)
        for error in uglies_dir
    ]
    return uglies
