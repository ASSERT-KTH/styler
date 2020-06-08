from core import *
from synthetic import Synthetic_Checkstyle_Error
import java_lang_utils
import checkstyle
import styler
from functools import reduce


def call_repair_tool(tool, orig_dir, ugly_dir, output_dir, dataset_metadata):
    """
    Call a repair tool
    """
    if tool == 'naturalize':
        call_naturalize(orig_dir, ugly_dir, output_dir)
    if tool == 'naturalize_sniper':
        call_naturalize_sniper(orig_dir, ugly_dir, output_dir)
    if tool == 'codebuff':
        call_codebuff(orig_dir, ugly_dir, output_dir, grammar=dataset_metadata['grammar'], indent=dataset_metadata['indent'])
    if tool == 'codebuff_sniper':
        call_codebuff_sniper(orig_dir, ugly_dir, output_dir[:-7],output_dir)
    return move_parse_exception_files(output_dir, None)


def call_naturalize(orig_dir, ugly_dir, output_dir):
    """
    Call a Naturalize
    """
    args = ["-t " + orig_dir, "-o " + output_dir, "-f " + ugly_dir]
    return call_java("../jars/naturalize.jar", args)


def call_naturalize_sniper(orig_dir, ugly_dir, output_dir):
    """
    Call naturalize sniper.
    The dir needs to contain the metadata
    """
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
    """
    Call codebuff.
    """
    args = ["-g org.antlr.codebuff." + grammar, "-rule compilationUnit", "-corpus " + orig_dir, "-files java", "-comment LINE_COMMENT", "-indent " + str(indent), "-o " + output_dir]
    args.append(ugly_dir)
    return call_java("../jars/codebuff-1.5.1.jar", args)


def call_codebuff_sniper(orig_dir, ugly_dir, codebuff_dir, output_dir):
    """
    Call Codebuff sniper.
    The dir needs to contain the metadata
    """
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
    """
    Get the ugly generated files.
    The dir needs to contain the metadata
    """
    uglies_dir = [
        error
        for error in list_dir_full_path(ugly_dir) if os.path.isdir(error)
    ]
    uglies = [
        Synthetic_Checkstyle_Error(error)
        for error in uglies_dir
    ]
    return uglies


def get_checkstyle_results(tool, dir, only_targeted=False, checkstyle_rules=None):
    """
    Return the checkstyle results
    """
    tool_dir = os.path.join(dir, tool)
    file_name = f'checkstyle_results_{tool}.json'
    result_file_dir = f'{dir}/{file_name}'
    results_json = {}
    if os.path.exists(result_file_dir):
        results_json = open_json(result_file_dir)
    else:
        if checkstyle_rules is None:
            checkstyle_rules = os.path.join(dir, 'checkstyle.xml')
        checkstyle_results, number_of_errors = checkstyle.check(checkstyle_rules, tool_dir, only_targeted=only_targeted, only_java=True)
        results_json['checkstyle_results'] = checkstyle_results
        results_json['number_of_errors'] = number_of_errors
        save_json(dir, file_name, results_json)
    return results_json['checkstyle_results'], results_json['number_of_errors']


def get_repaired(tool, dir, batch=False, only_targeted=False, checkstyle_rules=None):
    """
    Return the list of files repaired during the experiment
    """
    if not os.path.exists(f'{dir}/{tool}'):
        return []
    checkstyle_results, number_of_errors = get_checkstyle_results(tool, dir,  only_targeted=only_targeted, checkstyle_rules=checkstyle_rules)
    if batch:
        batch_result = styler.get_batch_results(checkstyle_results)
        return list(
            reduce(
                lambda acc, cur: acc | cur,
                batch_result.values()
            )
        )
    else:
        return [
            file.split('/')[-2]
            for file, result in checkstyle_results.items()
            if len(result['errors']) == 0
        ]
