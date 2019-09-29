import sys
import git_helper
import checkstyle

from core import *
from tqdm import tqdm

dir_path = os.path.dirname(os.path.realpath(__file__))
config = configparser.ConfigParser()
config.read(os.path.join(dir_path, "config.ini"))

__real_errors_dir = config['DEFAULT']['real_errors_dir']

def get_real_errors_repo_dir(repo):
    return os.path.join(__real_errors_dir, repo)

def get_real_errors_commit_dir(repo, commit):
    return os.path.join(get_real_errors_repo_dir(repo), commit)

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

def find_the_checkstyle(repo):
    path = repo.working_dir
    checkstyle_path = safe_get_first(sorted(find_all(path, 'checkstyle.xml'), key=lambda x: x.count('/')))
    if checkstyle_path:
        return checkstyle_path, '.' + checkstyle_path[len(path):]
    else:
        return None, None

def find_the_pom(path):
    return safe_get_first(sorted(find_all(path, 'pom.xml'), key=lambda x: x.count('/'))) # the main pom should be closser to the root

def get_repo_with_checkstyle(repos):
    result = []
    for repo_full_name in repos:
        user, repo_name = repo_full_name.split('/')
        repo = git_helper.open_repo(user, repo_name)
        if repo is not None:
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

def clone():
    repos = set(list(open_json(os.path.join(dir_path, 'travis/commits.json')).keys()) + list(open_json(os.path.join(dir_path, 'travis/commits_oss.json')).keys()))
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

clone()

