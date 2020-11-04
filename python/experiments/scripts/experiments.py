import sys
from os import path
sys.path.append(path.dirname(path.dirname(path.dirname(path.abspath(__file__)))))
from core import *
from Corpus import Corpus
import checkstyle

import repair
import graph_plot

from terminaltables import GithubFlavoredMarkdownTable
from terminaltables import SingleTable
from functools import reduce
import pandas as pd

tools = ['styler', 'intellij', 'naturalize', 'codebuff']

tools_plus_styler_protocols = tuple(list(tools) + list(styler_tools))
tools_plus_all = tuple(list(tools) + ['all_tools'])
tools_plus_styler_protocols_and_all = tuple(list(tools_plus_styler_protocols) + ['all_tools'])

tool_names = {
    'styler': 'Styler',
    'intellij': 'Checkstyle-IDEA',
    'naturalize': 'Naturalize',
    'codebuff': 'CodeBuff',
    'all_tools': 'All'
}

codebuff_color = '#1565c0'
styler_color = '#64dd17'
naturalize_color = '#fdd835'
intellij_color = '#ED4C67'

class Timer:
    """ Very basic class to time tasks
    """
    def __init__(self):
        self.tasks = {}

    def start_task(self, name):
        timestamp = time.time()
        self.tasks[name] = {
            'start': time.time()
        }
        return timestamp

    def end_task(self, name):
        timestamp = time.time()
        if name in self.tasks:
            self.tasks[name]['end'] = timestamp
            self.tasks[name]['duration'] = self.tasks[name]['end'] - self.tasks[name]['start']

        return None

    def get_durations(self):
        return {
            key:values['duration']
            for key, values in self.tasks.items()
            if 'duration' in values
        }

# timer = Timer()

def get_experiment_dir():
    return f'{get_output_dir()}/../results'

def get_experiment_dir_of_project(project_name):
    return f'{get_experiment_dir()}/{project_name}'

def setup_experiment(name, corpus_dir):
    logger.debug(f'Starting the experiment for project {name}')

    dataset_dir = create_dir(get_real_dataset_dir(name))
    dataset_info = open_json(os.path.join(dataset_dir, 'info.json'))
    checkstyle_jar = dataset_info["checkstyle_jar"]
    logger.debug(f'Checkstyle jar version: {checkstyle_jar}')

    logger.debug(f'Real dataset dir: {dataset_dir}')
    experiment_dir = get_experiment_dir_of_project(name)
    logger.debug(f'Experiment dir: {experiment_dir}')
    errored_dir = os.path.join(experiment_dir, f'./errored')
    clean_dir = os.path.join(experiment_dir, f'./clean')
    if not os.path.exists(experiment_dir):
        create_dir(experiment_dir)
        for number_of_errors in range(1,2):
            from_folder = os.path.join(dataset_dir, str(number_of_errors))
            to_folder = os.path.join(errored_dir, str(number_of_errors))
            shutil.copytree(from_folder, to_folder)
        shutil.copytree(
            os.path.join(corpus_dir, 'data'),
            clean_dir
        )
        shutil.copy(
            os.path.join(corpus_dir, 'checkstyle.xml'),
            experiment_dir
        )
        shutil.copy(
            os.path.join(corpus_dir, 'corpus.json'),
            os.path.join(experiment_dir, 'metadata.json')
        )

    metadata = open_json(os.path.join(experiment_dir, 'metadata.json'))

    return experiment_dir, clean_dir, errored_dir, metadata, checkstyle_jar
    

def experiment(name, corpus_dir, execute=True):
    experiment_dir, clean_dir, errored_dir, metadata, checkstyle_jar = setup_experiment(name, corpus_dir)

    result = {}

    for tool in tools_plus_styler_protocols:
        logger.debug(f'Start {tool} ({name})')
        # timer.start_task(f'{name}_{tool}')
        experiment_tool_dir = os.path.join(experiment_dir, tool)
        if not os.path.exists(experiment_tool_dir) and execute:
            if tool == 'styler':
                shutil.copytree(f'{get_styler_repairs(name)}/files-repaired', experiment_tool_dir)
            elif tool.startswith('styler'):
                protocol = '_'.join(tool.split('_')[1:])
                path = f'{get_styler_repairs_by_protocol(name, protocol)}/files-repaired'
                if os.path.exists(path):
                    shutil.copytree(path, experiment_tool_dir)
            elif tool == 'intellij':
                intellij_dir = os.path.join(experiment_dir, 'intellij')
                if not os.path.exists(intellij_dir):
                    create_dir(intellij_dir)
                pass
            #else:
            #    repair.call_repair_tool(tool, orig_dir=clean_dir, ugly_dir=f'{errored_dir}/1', output_dir=experiment_tool_dir, dataset_metadata=metadata)
        # timer.end_task(f'{name}_{tool}')
        repaired = repair.get_repaired(tool, experiment_dir, checkstyle_jar, only_targeted=True)
        result[tool] = repaired
        # print(f'{tool} : {len(repaired)}')
        # json_pp(repaired)
    result['out_of'] = list_folders(f'{get_real_dataset_dir(name)}/1')
    return result


def compute_diff_repairs_size(name, tool):
    diffs = []
    experiment_dir = get_experiment_dir_of_project(name)
    dataset_dir = get_real_dataset_dir(name)
    dataset_info = open_json(os.path.join(dataset_dir, 'info.json'))
    checkstyle_jar = dataset_info["checkstyle_jar"]
    repaired = repair.get_repaired(tool, experiment_dir, checkstyle_jar, only_targeted=True)

    errored_dir = os.path.join(experiment_dir, f'./errored/1')
    repaired_dir = os.path.join(experiment_dir, f'./{tool}')
    for repaired_id in repaired:
        original_folder = os.path.join(errored_dir, str(repaired_id))
        repaired_folder = os.path.join(repaired_dir, str(repaired_id))
        potential_files = glob.glob(f'{original_folder}/*.java')
        if len(potential_files) == 0:
            continue
        original_file = potential_files[0]
        potential_files = glob.glob(f'{original_folder}/*.java')
        if len(glob.glob(f'{repaired_folder}/*.java')) == 0:
            continue
        repaired_file = glob.glob(f'{repaired_folder}/*.java')[0]
        diffs_count = compute_diff_size(original_file, repaired_file)
        diffs += [diffs_count]
    return diffs


def get_diff_repairs(experiment_id, tools):
    dataset_name = experiment_id
    return {
        tool:compute_diff_repairs_size(experiment_id, tool)
        for tool in tools # if tool not in ['styler']
    }

def exp(projects):
    result_raw = {}
    for name in projects:
        # print(name)
        result_raw[name] = experiment(name, get_corpus_dir(name))
    # json_pp(result)
    
    result = {
        project:{
            tool:len(repair)
            for tool, repair in p_results.items()
        } 
        for project, p_results in result_raw.items()
    }
    keys = list(list(result.values())[0].keys())
        
    result['total'] = { key:sum([e[key] for e in result.values()]) for key in keys }
    #json_pp(total)
    table = [ [''] + keys]
    table += [ [key] + list(values.values()) for key, values in result.items() ]
    print(GithubFlavoredMarkdownTable(table).table)


def exp_stats(projects):
    repaired_error_types = {tool:[] for tool in (*tools_plus_styler_protocols_and_all, 'out_of')}
    for name in projects:
        with open(os.path.join(get_experiment_dir_of_project(name), 'report.json'), 'r') as fd:
            data = json.load(fd)
            for bug_id in data:
                all_tools_combined = False
                lenn = len(data[bug_id]['information']['errors'])

                experiment_dir = get_experiment_dir_of_project(name)
                errored_dir = os.path.join(experiment_dir, f'./errored/1')
                colectedError = open_json(os.path.join(errored_dir, f'{bug_id}/metadata.json'))['errors'][0]

                if len(data[bug_id]['information']['errors']) != 1:
                    print(f'Project {name}, {bug_id}, {lenn}', colectedError)
                    continue
                
                error = data[bug_id]['information']['errors'][0]
                if colectedError != error:
                    print(name, colectedError['source'], error['source'])
                error_type = checkstyle_source_to_error_type(error['source'])
                repaired_error_types['out_of'].append(error_type)
                for tool in tools_plus_styler_protocols:
                    r = data[bug_id]['results'][tool]    
                    if r is not None and len(r) == 0:
                        repaired_error_types[tool].append(error_type)
                        all_tools_combined = True
                if all_tools_combined:
                    repaired_error_types['all_tools'].append(error_type)

    repaired_error_types_count = {
        tool:dict_count(values)
        for tool, values in repaired_error_types.items()
    }
    repaired_error_types_count_relative = {
        tool:{
            error_type:(float(count) / repaired_error_types_count['out_of'][error_type] * 100.)
            for error_type, count in repaired_error_types_count[tool].items()
        }
        for tool in tools_plus_styler_protocols_and_all
    }
    # json_pp(repaired_error_types_count_relative)
    keys = list(repaired_error_types_count['out_of'].keys())
    # result = {project:{tool:len(repair) for tool, repair in p_results.items()} for project, p_results in result.items()}
    # result['total'] = { key:sum([e[key] for e in result.values()]) for key in keys }
    #json_pp(total)
    table_data = [ [''] + [f'{key}\n( /{repaired_error_types_count["out_of"][key]})' for key in keys]]
    table_data += [
        [tool] + [f'{repaired_error_types_count_relative[tool].get(error_type, 0):.1f}%' for error_type in keys]
        for tool in tools_plus_styler_protocols_and_all
    ]
    table = SingleTable(table_data)
    print(table.table)

    df = pd.DataFrame.from_dict({
        tool_names[tool]:{
            f'{error_type} ({repaired_error_types_count["out_of"][error_type]})':repaired_error_types_count_relative[tool].get(error_type, 0)
            for error_type in keys
        }
        for tool in tools_plus_all
    })
    graph_plot.repair_heatmap(df)
    return repaired_error_types_count

@logger.catch
def json_report(project_name):
    logger.debug(f'Project {project_name}')

    experiment_dir = get_experiment_dir_of_project(project_name)
    checkstyle_path = os.path.join(experiment_dir, 'checkstyle.xml')
    errored_dir = os.path.join(experiment_dir, 'errored/1')

    dataset_dir = get_real_dataset_dir(project_name)
    dataset_info = open_json(os.path.join(dataset_dir, 'info.json'))
    checkstyle_jar = dataset_info["checkstyle_jar"]

    logger.debug('Getting the original errors')
    (errored_result, _) = checkstyle.check(checkstyle_path, errored_dir, checkstyle_jar, only_targeted=True, only_java=True)

    def get_file_id(file_path):
        return file_path.split('/')[-2]

    file_ids = sorted([get_file_id(file_path) for file_path in errored_result.keys()], key=int)

    logger.debug('Getting the results from the tools')
    tools_results = {
        tool:open_json(os.path.join(experiment_dir, f'checkstyle_results_{tool}.json'))
        for tool in tools_plus_styler_protocols
    }

    report = {
        get_file_id(file_path):{
            'information':information,
            'results': {
                tool:None
                for tool in tools_plus_styler_protocols
            }
        }
        for file_path, information in errored_result.items()
    }

    for tool, result in tools_results.items():
        if result is not None and result['checkstyle_results'] is not None:
            for file_path, information in result['checkstyle_results'].items():
                file_id = get_file_id(file_path)
                if file_id in report:
                    report[file_id]['results'][tool] = information['errors']

    save_json(experiment_dir, 'report.json', report)


def exp_venn(projects):
    result = {}
    for name in projects:
        result[name] = experiment(name, get_corpus_dir(name), execute=False)

    flat_result = {
        tool:set(
            reduce(
                list.__add__,
                [
                    [f'{project}{repaired}' for repaired in p_results[tool] ]
                    for project, p_results in result.items()
                ]
            )
        )
        for tool in ('styler', 'intellij')
    }
    graph_plot.venn(map_keys(lambda x: x.capitalize(), flat_result))

def merge_reports(experiments):
    all_reports = {}
    for experiment in experiments:
        experiment_dir = get_experiment_dir_of_project(experiment)
        report_path = os.path.join(experiment_dir, 'report.json')
        if os.path.exists(report_path):
            report = open_json(report_path)
            all_reports[experiment] = report
    experiment_results = list(all_reports.keys())
    logger.debug(f'Merging the results from {len(experiment_results)} reports ({", ".join(experiment_results)})')
    merged_report = {}
    file_count = 0
    for experiment, report in all_reports.items():
        for file_id, res in report.items():
            merged_report[file_count] = res
            file_count += 1
    save_json(get_experiment_dir(), 'report.json', merged_report)

def compare_protocols(experiment_name):
    exp_result = experiment(experiment_name, get_corpus_dir(experiment_name), execute=False)
    res = {}
    res['only_random'] = len(set(exp_result['styler_random']) - set(exp_result['styler_three_grams']))
    res['only_three_grams'] = len(set(exp_result['styler_three_grams']) - set(exp_result['styler_random']))
    res['both'] = len(set(exp_result['styler_three_grams']).intersection(set(exp_result['styler_random'])))
    return res

def main(args):
    if len(args) >= 2 and args[1] == 'exp':
        exp(args[2:])
    elif len(args) >= 2 and args[1] == 'report':
        for project_name in args[2:]:
            json_report(project_name)
    elif len(args) >= 2 and args[1] == 'merge-reports':
        experiments = list_folders(get_experiment_dir())
        logger.debug(f'Found {len(experiments)} experiments ({", ".join(experiments)})')
        merge_reports(experiments)
    elif len(args) >= 2 and args[1] == 'styler-protocols':
        experiments = list_folders(get_experiment_dir())
        logger.debug(f'Found {len(experiments)} experiments ({", ".join(experiments)})')
        results = {}
        for experiment_name in experiments:
            results[experiment_name] = compare_protocols(experiment_name)
        keys = list(results[experiments[0]].keys())
        dict_sum = {key:0 for key in keys}
        for res in results.values():
            for key in keys:
                dict_sum[key] += res[key]
        json_pp(dict_sum)
    elif len(args) >= 2 and args[1] == 'exp-stats':
        projects = args[2:]
        exp_stats(projects)
        exp_venn(projects)
        
        result = {}
        for name in projects:
            result[name] = get_diff_repairs(name, tools)
        result_per_tool = {}
        for tool in tools:
            result_per_tool[tool] = []
            for project in projects:
                result_per_tool[tool] += result[project][tool]
        json_pp(result_per_tool)
        for tool in tools:
            print(f'{tool}: {len(result_per_tool[tool])}')

        #json_pp(result)
        keys = list(list(result.values())[0].keys())
        total = { key:reduce( list.__add__ ,[e[key] for e in result.values()]) for key in keys }
        graph = {}
        graph['data'] = total
        graph['x_label'] = 'Diff size'
        graph['colors'] = {
            'codebuff': codebuff_color,
            'naturalize': naturalize_color,
            'intellij': intellij_color,
            'styler': styler_color
        }
        graph_plot.violin_plot(graph)


if __name__ == "__main__":
    main(sys.argv)
