#FIXME: a lot of things were extracted from here, and this code is not tested (it probably doesn't work for the moment)

import os
import subprocess
import sys
import configparser
import checkstyle
import glob
import repair
import shutil
from tqdm import tqdm
from termcolor import colored
import git_helper
from Corpus import Corpus
from terminaltables import GithubFlavoredMarkdownTable
from terminaltables import SingleTable
from functools import reduce
import time
from scipy import stats
import string

from core import *
import graph_plot

config = configparser.ConfigParser()
config.read('config.ini')

__real_dataset_dir = config['DEFAULT']['real_dataset_dir']

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
def get_experiment_dir(name):
    return f'./experiments/real/{name}'

def get_real_dataset_dir(name):
    return os.path.join(__real_dataset_dir, name)

def experiment(name, corpus_dir):
    dataset_dir = create_dir(get_real_dataset_dir(name))
    experiment_dir = get_experiment_dir(name)
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

    result = {}

    for tool in ('naturalize', 'codebuff', 'styler'):
        # timer.start_task(f'{name}_{tool}')
        target = os.path.join(experiment_dir, f'./{tool}')
        if not os.path.exists(target):
            if tool == 'styler':
                shutil.copytree(f'./styler/{name}/files-repaired', target)
            else:
                repair.call_repair_tool(tool, orig_dir=clean_dir, ugly_dir=f'{errored_dir}/1', output_dir=target, dataset_metadata=metadata)
        # timer.end_task(f'{name}_{tool}')
        repaired = repair.get_repaired(tool, experiment_dir, only_targeted=True)
        result[tool] = repaired
        # print(f'{tool} : {len(repaired)}')
        # json_pp(repaired)
    result['out_of'] = list_folders(f'./styler/{name}/repair-attempt/batch_0')
    return result


def compute_diff_size(name, tool):
    diffs = []
    experiment_dir = get_experiment_dir(name)
    repaired = repair.get_repaired(tool, experiment_dir, only_targeted=True)
    errored_dir = os.path.join(experiment_dir, f'./errored/1')
    repaired_dir = os.path.join(experiment_dir, f'./{tool}')
    for repaired_id in repaired:
        original_folder = os.path.join(errored_dir, str(repaired_id))
        repaired_folder = os.path.join(repaired_dir, str(repaired_id))
        original_file = glob.glob(f'{original_folder}/*.java')[0]
        repaired_file = glob.glob(f'{repaired_folder}/*.java')[0]
        diffs_count = java_lang_utils.compute_diff_size(original_file, repaired_file)
        diffs += [diffs_count]
    return diffs


def get_diff_dataset(experiment_id, tools):
    dataset_name = experiment_id
    return {
        tool:compute_diff_size(experiment_id, tool)
        for tool in tools # if tool not in ['styler']
    }


def benchmark(name, corpus_dir, tools):
    points = [1,5,10,15,20,25,30,35,40]
    dataset_dir = get_real_dataset_dir(name)
    experiment_dir = f'./experiments/benchmark/{name}'
    errored_dir = os.path.join(experiment_dir, f'./errored')
    clean_dir = os.path.join(experiment_dir, f'./clean')
    if not os.path.exists(experiment_dir):
        create_dir(experiment_dir)
        number_of_errors = 1
        from_folder = os.path.join(dataset_dir, str(number_of_errors))
        files = list_folders(from_folder)
        points = list(filter(lambda e: e < len(files), points))
        for point in points:
            sample = random.sample(files, point)
            to_folder = create_dir(os.path.join(errored_dir, str(point)))
            for folder in sample:
                shutil.copytree(os.path.join(from_folder, str(folder)), os.path.join(to_folder, folder))
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
    timers = {tool:Timer() for tool in tools}
    metadata = open_json(os.path.join(experiment_dir, 'metadata.json'))
    for point in tqdm(points):
        for tool in tools:
            timers[tool].start_task(point)
            target = os.path.join(experiment_dir, f'./{tool}/{point}')
            if not os.path.exists(target):
                repair.call_repair_tool(tool, orig_dir=clean_dir, ugly_dir=f'{errored_dir}/{point}', output_dir=target, dataset_metadata=metadata)
            timers[tool].end_task(point)
            # repaired = repair.get_repaired(tool, experiment_dir, only_targeted=True)
            # result[tool] = len(repaired)
    return {tool:timer.get_durations() for tool,timer in timers.items()}


def exp(projects):
    result = {}
    for name in projects:
        # print(name)
        result[name] = experiment(name, f'./styler/{name}-corpus')
    # json_pp(result)
    keys = list(list(result.values())[0].keys())
    result = {project:{tool:len(repair) for tool, repair in p_results.items()} for project, p_results in result.items()}
    result['total'] = { key:sum([e[key] for e in result.values()]) for key in keys }
    #json_pp(total)
    table = [ [''] + keys]
    table += [ [key] + list(values.values()) for key, values in result.items() ]
    print(GithubFlavoredMarkdownTable(table).table)


def exp_stats(projects):
    exp_result = {}
    all_tools = ('naturalize', 'codebuff', 'styler')
    for name in projects:
        # print(name)
        exp_result[name] = experiment(name, f'./styler/{name}-corpus')
        exp_result[name]['all_tools'] = list(reduce(lambda a,b: a|b, [ set(exp_result[name][tool]) for tool in all_tools]))
    tools = ('naturalize', 'codebuff', 'styler', 'all_tools')
    repaired_error_types = {tool:[] for tool in (*tools, 'out_of')}
    for project, project_result in exp_result.items():
        experiment_dir = get_experiment_dir(project)
        errored_dir = os.path.join(experiment_dir, f'./errored/1')
        for tool, repaired_files in project_result.items():
            # repaired_files = project_result[tool]
            error_types = reduce(
                list.__add__,
                [
                    [
                        checkstyle_source_to_error_type(error['source'])
                        for error in open_json(os.path.join(errored_dir, f'{id}/metadata.json'))['errors']
                    ]
                    for id in repaired_files
                ]
            )
            repaired_error_types[tool] += error_types
    repaired_error_types_count = {
        tool:dict_count(values)
        for tool, values in repaired_error_types.items()
    }
    repaired_error_types_count_relative = {
        tool:{
            error_type:(float(count) / repaired_error_types_count['out_of'][error_type] * 100.)
            for error_type, count in repaired_error_types_count[tool].items()
        }
        for tool in tools
    }
    # json_pp(repaired_error_types_count_relative)
    keys = list(repaired_error_types_count['out_of'].keys())
    # result = {project:{tool:len(repair) for tool, repair in p_results.items()} for project, p_results in result.items()}
    # result['total'] = { key:sum([e[key] for e in result.values()]) for key in keys }
    #json_pp(total)
    table_data = [ [''] + [f'{key}\n( /{repaired_error_types_count["out_of"][key]})' for key in keys]]
    table_data += [
        [tool] + [f'{repaired_error_types_count_relative[tool].get(error_type, 0):.1f}%' for error_type in keys]
        for tool in tools
    ]
    table = SingleTable(table_data)
    print(table.table)
    return repaired_error_types_count

def exp_venn(projects):
    result = {}
    for name in projects:
        result[name] = experiment(name, f'./styler/{name}-corpus')

    tools = ('naturalize', 'styler', 'codebuff')
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
        for tool in tools
    }
    graph_plot.venn(map_keys(lambda x: x.capitalize(), flat_result))


def benchmark_stats(results):
    """
    Return the stats given a lit of results
    """
    result = reduce(dict_sum, results)
    length = len(results)
    # print(result['be5']['code'])
    regression = {
        name:{
            tool:stats.linregress([float(x) for x in data.keys()], [y/length for y in data.values()])
            for tool, data in values.items()
        }
        for name, values in result.items()
    }
    json_pp(regression)


def main(args):
    if len(args) >= 2 and args[1] == 'exp':
        exp(args[2:])
    elif len(args) >= 2 and args[1] == 'exp-stats':
        exp_stats(args[2:])
    elif len(args) >= 2 and args[1] == 'exp-venn':
        exp_venn(args[2:])
    elif len(args) >= 2 and args[1] == 'benchmark':
        result = {}
        for name in args[2:]:
            result[name] = benchmark(name, f'./styler/{name}-corpus', ('naturalize', 'codebuff'))
            save_json('./', 'benchmark.json', result)
        json_pp(result)
        save_json('./', 'benchmark.json', result)
    elif len(args) >= 2 and args[1] == 'benchmark-stats':
        results = [open_json('./benchmark.json')] + [open_json(f'./benchmark{id}.json') for id in range(2)]
        benchmark_stats(results)
    elif len(args) >= 2 and args[1] == 'diff':
        tools = ('styler', 'naturalize', 'codebuff')
        result = {}
        for name in args[2:]:
            result[name] = get_diff_dataset(name, ('naturalize', 'codebuff', 'styler'))
        json_pp(result)
        keys = list(list(result.values())[0].keys())
        total = { key:reduce( list.__add__ ,[e[key] for e in result.values()]) for key in keys }
        graph = {}
        graph['data'] = total
        graph['x_label'] = 'Diff size'
        graph['colors'] = {
            'codebuff': codebuff_color,
            'naturalize': naturalize_color,
            'styler': styler_color
        }
        graph_plot.violin_plot(graph)


if __name__ == "__main__":
    main(sys.argv)
