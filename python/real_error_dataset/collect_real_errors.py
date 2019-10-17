#!/usr/bin/python

import os
import sys

from tqdm import tqdm

dir_path = os.path.dirname(os.path.realpath(__file__))
sys.path.append(os.path.dirname(dir_path))
import git_helper
import checkstyle
from core import *

config = configparser.ConfigParser()
config.read(os.path.join(dir_path, "config.ini"))

__input_projects_path = config['DEFAULT']['input_projects_path']
__real_errors_dir = config['DEFAULT']['real_errors_dir']

checkstyle_file_names = ['checkstyle.xml', 'checkstyle_rules.xml', 'checkstyle-rules.xml', 'checkstyle_config.xml', 'checkstyle-config.xml', 'checkstyle_configuration.xml', 'checkstyle-configuration.xml', 'checkstyle_checker.xml', 'checkstyle-checker.xml', 'checkstyle_checks.xml', 'checkstyle-checks.xml', 'google_checks.xml', 'google-checks.xml', 'sun_checks.xml', 'sun-checks.xml']

def get_real_errors_repo_dir(repo):
    return os.path.join(__real_errors_dir, repo)

def get_real_errors_commit_dir(repo, commit):
    return os.path.join(get_real_errors_repo_dir(repo), commit)

def sanitize_checkstyle(content):
    lines = content.split('\n')
    result = ''
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
        result += line + '\n'
    return result

def is_there_suppressionsLocation_in_pom(repo):
    pom_line = open_file(find_the_pom(repo.working_dir)).split('\n')
    for l in pom_line:
        if 'suppressionsLocation' in l:
            return True
    return False

def find_the_checkstyle(repo):
    path = repo.working_dir
    
    checkstyle_files = []
    for checkstyle_file_name in checkstyle_file_names:
        files = find_all(path, checkstyle_file_name)
        if len(files) > 0:
            for f in files:
                checkstyle_files.append(f)
    
    checkstyle_path = safe_get_first(sorted(checkstyle_files, key=lambda x: x.count('/')))
    print(f'Checkstyle file path: {checkstyle_path}')
    
    if checkstyle_path:
        return checkstyle_path, '.' + checkstyle_path[len(path):]
    else:
        return None, None

def find_the_pom(path):
    return safe_get_first(sorted(find_all(path, 'pom.xml'), key=lambda x: x.count('/'))) # the main pom should be closer to the root

def get_repo_with_checkstyle(repos):
    result = []
    for repo_full_name in repos:
        user, repo_name = repo_full_name.split('/')
        repo = git_helper.open_repo(user, repo_name)
        if repo is not None:
            checkstyle_absolute_path, checkstyle_relative_path = find_the_checkstyle(repo)
            if not checkstyle_absolute_path:
                continue
            clean_checkstyle = sanitize_checkstyle(open_file(checkstyle_absolute_path))
            if clean_checkstyle:
                if not is_there_suppressionsLocation_in_pom(repo):
                    save_file(os.path.join(repo.working_dir, '../'), 'checkstyle.xml', clean_checkstyle)
                    result.append({
                        'repo': repo,
                        'checkstyle_absolute_path': checkstyle_absolute_path,
                        'checkstyle_relative_path': checkstyle_relative_path,
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
        print("The last modification in the checkstyle.xml file was in the commit %s." % commit_checkstyle)
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
    dir = repo.working_dir

    # clean_up
    checkstyle_result_xml = find_all(dir, 'checkstyle-result.xml')
    for file in checkstyle_result_xml:
        os.remove(file)

    if use_maven:
        pom = find_the_pom(dir)
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

    repo_name = dir.split('/')[-2] + "-" + dir.split('/')[-1]
    count = 0
    target = get_real_errors_commit_dir(repo_name, commit)
    for report_dir, results in reports_with_errors.items():
        # checkstyle_checker = f'{"/".join(report_dir.split("/")[:-1])}/checkstyle-checker.xml'
        for file, errors in results.items():
            print(f'{file} has {len(errors)} error(s)')
            file_name = file.split('/')[-1]
            dir = os.path.join(target, str(count))
            create_dir(dir)
            shutil.copyfile(file, os.path.join(dir, file_name))
            # shutil.copyfile(checkstyle_checker, os.path.join(dir, 'checkstyle.xml'))
            save_json(dir, 'errors.json', errors)
            count += 1
    
    print("# Files with at least one error: %s" % count)
    find_all(dir, 'checkstyle-checker.xml')
    return output

with open(__input_projects_path) as temp_file:
	repos = [line.rstrip('\n') for line in temp_file]
	print("# Repos: %s" % len(repos))
	for info in tqdm(get_repo_with_checkstyle(repos), desc='Total'):
	    repo = info['repo']
	    checkstyle_path = info['checkstyle_absolute_path']
	    repo_full_name = info['repo_full_name']
	    print("Repo %s..." % repo_full_name)
	    user, repo_name = repo_full_name.split('/')
	    valid_commits = shuffled(commit_until_last_modification(repo, checkstyle_path))
	    print("%s commits were found after the last modification in the checkstyle.xml file." % len(valid_commits))
	    try:
		    for commit in tqdm(valid_commits, desc=f'{user}/{repo_name}'):
		        repo.git.checkout(commit)
		        if not is_there_suppressionsLocation_in_pom(repo):
			        find_errored_files(repo, commit, use_maven=False, checkstyle_path=info['checkstyle_clean'])
			
		    repo_dir = get_real_errors_repo_dir(f'{user}-{repo_name}')
		    if len(os.listdir(repo_dir)) > 0:
		        shutil.copyfile(os.path.join(repo.working_dir, info['checkstyle_clean']), os.path.join(repo_dir, 'checkstyle.xml'))
		        info = {
		            'repo_url': f'https://github.com/{user}/{repo_name}.git',
		            'original_checkstyle_path': info['checkstyle_relative_path']
                }
		        save_json(repo_dir, 'info.json', info)
	    except:
		    print(f'[ERROR] The error collection of {repo_full_name} did not complete.')
		    # print(f'{repo_name}')

