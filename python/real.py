from tqdm import tqdm
import git
from git import Repo
import shutil
import os
import subprocess
import sys
import configparser
import checkstyle
import glob
import repair
from termcolor import colored
import git_helper
from Corpus import Corpus
from terminaltables import GithubFlavoredMarkdownTable
from functools import reduce
import time
from scipy import stats

from core import *
import graph_plot

config = configparser.ConfigParser()
config.read('config.ini')

_OSS_dir = config['DEFAULT']['OSS_dir']
__git_repo_dir = config['DEFAULT']['git_repo_dir']
__real_errors_dir = config['DEFAULT']['real_errors_dir']
__real_dataset_dir = config['DEFAULT']['real_dataset_dir']

class Timer:
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

def get_real_errors_repo_dir(repo):
    return os.path.join(__real_errors_dir, repo)


def get_real_dataset_dir(name):
    return os.path.join(__real_dataset_dir, name)


def get_real_errors_commit_dir(repo, commit):
    return os.path.join(get_real_errors_repo_dir(repo), commit)


def real_errors_stats():
    errors_info = load_errors_info(only_targeted=True)
    # print(errors_info)

    for repo, errors_info in group_by(lambda e: e['repo'], errors_info).items():
        errors = [
            error['source'].split('.')[-1][:-5]
            for info in errors_info
            if len(info['errors']) <= 10
            for error in info['errors']
        ]
        print(colored(f'{repo} : {len(errors)} errors', attrs=['bold']))
        for size, errors_info in sorted(group_by(lambda e: len(e['errors']), errors_info).items()):
            errors = [
                error['source'].split('.')[-1][:-5]
                for info in errors_info
                if len(info['errors']) <= 10
                for error in info['errors']
            ]
            if len(errors) > 0:
                print(colored(f'\t{size} errors per file ({len(errors)/size:.0f} file(s))', attrs=['bold']))

                for error, count in dict_count(errors).items():
                    print('\t\t', end='')
                    if error in targeted_errors:
                        if error not in corner_cases_errors:
                            print(colored(f'{error:<30} : {count}', color='green'))
                        else:
                            print(colored(f'{error:<30} : {count}', color='yellow'))
                    else:
                        print(f'{error:<30} : {count}')


def load_errors_info(only_targeted=False):
    def filepath_from_json_path(x):
        return safe_get_first(glob.glob(pathname=f'{x.rpartition("/")[0]}/*.java'))
    error_json_path = glob.glob(pathname=f'{__real_errors_dir}/*/*/*/*.json')
    errors_info = unique([
        {
            'repo': path_splitted[-4],
            'commit': path_splitted[-3],
            'id': int(path_splitted[-2]),
            'errors': run_if_true(filter_targeted_error, only_targeted, open_json(path)),
            'only_targeted': only_targeted,
            'filepath': filepath_from_json_path(path),
            'hash': if_not_null(if_not_null(filepath_from_json_path(path),open_file), hash)
        }
        for path_splitted, path in tqdm(zip(map(lambda x: x.split('/'), error_json_path), error_json_path), desc='reading', total=len(error_json_path))
    ], lambda obj: obj['hash'])
    return errors_info


def structure_real_error_dataset(errors_info):
    dataset = {}
    for error in errors_info:
        if error['repo'] not in dataset:
            dataset[error['repo']] = {}
        n_errors = len(error['errors'])
        if n_errors not in dataset[error['repo']]:
            dataset[error['repo']][n_errors] = []
        dataset[error['repo']][n_errors] += [error]

    return dataset


def find_the_pom(path):
    return safe_get_first(sorted(find_all(path, 'pom.xml'), key=lambda x: x.count('/'))) # the main pom should be closser to the root


def find_the_checkstyle(repo):
    path = repo.working_dir
    checkstyle_path = safe_get_first(sorted(find_all(path, 'checkstyle.xml'), key=lambda x: x.count('/')))
    if checkstyle_path:
        return checkstyle_path, '.' + checkstyle_path[len(path):]
    else:
        return None, None


def check_checkstyle_results(checkstyle_results):
    reports_with_errors = {}
    for id, results in enumerate(checkstyle_results):
        files_with_errors = { file:result['errors'] for file, result in results.items() if len(result['errors']) and file.endswith('.java')}
        if len(files_with_errors):
            reports_with_errors[id] = files_with_errors
    return reports_with_errors


def find_errored_files(repo, commit, use_maven=False, checkstyle_path='checkstyle.xml'):
    # print(f'{repo}/{commit}')
    repo.git.checkout(commit)
    dir = repo.working_dir

    # clean_up
    checkstyle_result_xml = find_all(dir, 'checkstyle-result.xml')
    for file in checkstyle_result_xml:
        os.remove(file)

    repo_name = dir.split('/')[-1]
    pom = find_the_pom(dir)
    if use_maven:
        cmd = f'mvn -f {pom} clean checkstyle:checkstyle'
        process = subprocess.Popen(cmd.split(" "), stdout=subprocess.PIPE)
        output = process.communicate()[0]
        checkstyle_results = [ checkstyle.parse_res(open_file(file)) for file in find_all(dir, 'checkstyle-result.xml') ]
    else:
        # checkstyle_relative_dir = find_all(dir, checkstyle_path)
        checkstyle_dir = os.path.join(dir, checkstyle_path)
        if not os.path.isfile(checkstyle_dir):
            checkstyle_results = []
        else :
            output, returncode = checkstyle.check(checkstyle_file_path=checkstyle_dir, file_path=dir)
            checkstyle_results = [output]


    reports_with_errors = check_checkstyle_results(checkstyle_results)

    count = 0
    target = get_real_errors_commit_dir(repo_name, commit)
    for report_dir, results in reports_with_errors.items():
        # checkstyle_checker = f'{"/".join(report_dir.split("/")[:-1])}/checkstyle-checker.xml'
        for file, errors in results.items():
            # print(f'{file} has {len(errors)} errors')
            file_name = file.split('/')[-1]
            dir = os.path.join(target, str(count))
            create_dir(dir)
            shutil.copyfile(file, os.path.join(dir, file_name))
            # shutil.copyfile(checkstyle_checker, os.path.join(dir, 'checkstyle.xml'))
            save_json(dir, 'errors.json', errors)
            count += 1
    find_all(dir, 'checkstyle-checker.xml')
    return output


def create_real_error_dataset(target):
    if os.path.exists(target):
        shutil.rmtree(target)
    errors_info = load_errors_info(only_targeted=True)
    dataset = structure_real_error_dataset(errors_info)
    # pp.pprint(dataset)
    for project, number_of_errors_per_file in dataset.items():
        for number_of_errors, file_list in number_of_errors_per_file.items():
            for id, file_info in enumerate(file_list):
                dir = os.path.join(target, f'{project}/{number_of_errors}/{id}')
                metadata = {
                    'commit': file_info['commit'],
                    'file_name': file_info['filepath'].split('/')[-1],
                    'errors': file_info['errors']
                }
                create_dir(dir)
                save_json(dir, 'metadata.json', metadata)
                shutil.copy(file_info['filepath'], os.path.join(dir,metadata['file_name']))


def get_repo_with_checkstyle(repos):
    result = []
    for repo_full_name in repos:
        user, repo_name = repo_full_name.split('/')
        repo = git_helper.open_repo(user, repo_name)
        checkstyle_absolute, checkstyle_relative = find_the_checkstyle(repo)
        if not checkstyle_absolute:
            continue
        if sanitize_checkstyle(open_file(checkstyle_absolute)):
            pom_line = open_file(find_the_pom(repo.working_dir)).split('\n')
            good = True
            for l in pom_line:
                if 'suppressionsLocation' in l:
                    good = False
            if good:
                save_file(os.path.join(repo.working_dir, '../'), 'checkstyle.xml', sanitize_checkstyle(open_file(checkstyle_absolute)))
                result.append({
                    'repo': repo,
                    'checkstyle': checkstyle_absolute,
                    'checkstyle_relative': checkstyle_relative,
                    'checkstyle_clean': os.path.join(repo.working_dir, '../checkstyle.xml'),
                    'repo_full_name': repo_full_name
                })
    return result


def sanitize_checkstyle(content):
    lines = content.split('\n')
    restult = ''
    for line in lines:
        line_stripped: str = line.strip()
        if line_stripped.startswith('<!--') and line_stripped.endswith('-->'):
            continue
        if '${' in line:
            if 'cacheFile' not in line and 'basedir' not in line :
                print(line)
                return False
            else:
                continue
        if 'suppressions' in line:
            return False
        if 'RedundantThrows' in line:
            continue
        restult += line + '\n'
    return restult


def clone():
    repos = set(list(open_json('./travis/commits.json').keys()) + list(open_json('./travis/commits_oss.json').keys()))
    # repos = repos - set((
    #     'square/picasso',
    #     # 'pedrovgs/Renderers',
    #     # 'square/wire',
    #     # 'opentracing-contrib/java-spring-cloud',
    #     'ONSdigital/rm-notify-gateway',
    #     # 'INRIA/spoon',
    #     'eclipse/milo',
    #     'vorburger/MariaDB4j',
    #     'DevelopmentOnTheEdge/be5',
    #     'Spirals-Team/repairnator',
    #     # 'shyiko/mysql-binlog-connector-java'
    # ))
    # repos = list(repos)
    # repos = ['DevelopmentOnTheEdge/be5']
    for info in tqdm(get_repo_with_checkstyle(repos), desc='Total'):
        repo = info['repo']
        checkstyle_path = info['checkstyle']
        repo_full_name = info['repo_full_name']
        user, repo_name = repo_full_name.split('/')
        valid_commits = shuffled(commit_until_last_modification(repo, checkstyle_path))
        try:
            for commit in tqdm(valid_commits, desc=f'{user}/{repo_name}'):
                repo.git.checkout(commit)
                pom_line = open_file(find_the_pom(repo.working_dir)).split('\n')
                good = True
                for l in pom_line:
                    if 'suppressionsLocation' in l:
                        good = False
                if good:
                    find_errored_files(repo, commit, use_maven=False, checkstyle_path=info['checkstyle_clean'])
        except:
            print(f'did not complet the error collection of {repo_full_name}')
        # print(f'{repo_name}')


def find_commits(commits_data):
    result = {}
    for repo_full_name, commits in tqdm(commits_data.items()):
        user, repo_name = repo_full_name.split('/')
        repo = git_helper.open_repo(user, repo_name)
        result[repo_full_name] = []
        for commit in commits.values():
            if git_helper.has_commit(repo, commit):
                result[repo_full_name].append(commit)
    return result


def clone_old():
    commits_data = open_json('./travis/commits_oss.json')
    reduced_commits_data = { key:commits_data[key] for key in commits_data if key in ['google/auto']} # { key:commits_data[key] for key in commits_data if len(commits_data[key]) >= 10 } # 'facebook_presto',
    commits = find_commits(reduced_commits_data)
    repo_information = {}
    repo_information['facebook/presto'] = {
        'use_maven': False,
        'checkstyle_path': './src/checkstyle/checks.xml'
    }
    repo_information['vorburger/MariaDB4j'] = {
        'use_maven': False,
        'checkstyle_path': '../checkstyle.xml'
    }
    repo_information['google/auto'] = {
        'use_maven': False,
        'checkstyle_path': '../checkstyle.xml'
    }
    repo_information['DevelopmentOnTheEdge/be5'] = {
        'use_maven': False,
        'checkstyle_path': './checkstyle.xml'
    }
    repo = git_helper.open_repo('google', 'auto')
    commits['google/auto'] = shuffled(commit_until_last_modification(repo, './checkstyle.xml'))
    for repo_full_name, valid_commits in commits.items():
        user, repo_name = repo_full_name.split('/')
        repo = git_helper.open_repo(user, repo_name)
        for commit in tqdm(valid_commits, desc=f'{user}/{repo_name}'):
            if not repo_information[repo_full_name]['use_maven']:
                find_errored_files(repo, commit, use_maven=False, checkstyle_path=repo_information[repo_full_name]['checkstyle_path'])
            else:
                find_errored_files(repo, commit, use_maven=True)


def commit_until_last_modification(repo, file):
    result = []
    wd = os.getcwd()
    try:
        repo.git.checkout('master')
        os.chdir(repo.git.rev_parse("--show-toplevel"))
        cmd_commit_list = 'git log --pretty=format:%H'
        cmd_commit_checkstyle = f'git log -n 1 --pretty=format:%H -- {file}'
        p1 = subprocess.Popen(cmd_commit_list.split(' '), stdout=subprocess.PIPE)
        commit_list_array = p1.communicate()[0].decode("utf-8")
        p2 = subprocess.Popen(cmd_commit_checkstyle.split(' '), stdout=subprocess.PIPE)
        commit_checkstyle = p2.communicate()[0].decode("utf-8")
        repo.git.checkout('master')
        commit_list = commit_list_array.split('\n')
        result = commit_list[:commit_list.index(commit_checkstyle)] + [commit_checkstyle]
    except:
        pass
    os.chdir(wd)
    return result


def experiment(name, corpus_dir):
    dataset_dir = create_dir(get_real_dataset_dir(name))
    experiment_dir = f'./experiments/real/{name}'
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
    experiment_dir = f'./experiments/real/{name}'
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
    graph_plot.venn(flat_result)


def benchmark_stats(results):
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
    if len(args) >= 2 and args[1] == 'clone':
        clone()
    elif len(args) >= 2 and args[1] == 'stats':
        real_errors_stats()
    elif len(args) >= 2 and args[1] == 'copy':
        create_real_error_dataset(__real_dataset_dir)
    elif len(args) >= 2 and args[1] == 'exp':
        exp(args[2:])
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
