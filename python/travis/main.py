import requests
import sys
import json
import os
import os
import shutil
import threading
import time
import tarfile
import configparser
import glob

config = configparser.ConfigParser()
config.read('config.ini')

token = config['DEFAULT']['token']

__base_dir = "/home/benjaminl/Documents/travis-ci-build-log-dataset"

def get_dir(dir):
    return os.path.join(__base_dir, dir)

def get_repo_dir(repo):
    return get_dir(f'./{repo}')

def get_build_dir(repo, build):
    return os.path.join(get_repo_dir(repo), f'./{build}')

def get_job_dir(repo, build, job):
    return os.path.join(get_build_dir(repo, build), f'./{job}')

def get_travis(endpoint, payload={}):
    headers = {}
    if token:
        headers['Authorization'] = f'token {token}'
    r = requests.get(f'https://api.travis-ci.org{endpoint}', headers=headers, params=payload)
    # print(f'Get {r.url}')
    return r;

def get_builds(repo, max_builds_collected=100):
    result = []
    after_number = 0
    while after_number != 1 and len(result) < max_builds_collected:
        if after_number:
            payload = {'after_number': after_number}
        else:
            payload = {}
        result += get_travis(f'/repos/{repo}/builds', payload=payload).json()
        if len(result) > 0:
            after_number = int(result[-1]["number"])
        else:
            print(f'The repo {repo} is empty')
            after_number = 1
    if len(result) > max_builds_collected:
        result = result[:max_builds_collected]
    return result

def get_build(build):
    return get_travis(f'/builds/{build}').json()

def get_job_log(job):
    return get_travis(f'/jobs/{job}/log.txt').text

def get_jobs(repo, build):
    return get_build(repo, build)['matrix']

def save_file(dir, file_name, content):
    with open(os.path.join(dir, file_name), 'w') as f:
        f.write(content)

def save_json(dir, file_name, content):
    with open(os.path.join(dir, file_name), 'w') as f:
        json.dump(content, f)

def open_file(file):
    content = ''
    with open(file, 'r') as file:
        content = file.read()
    return content

def open_json(file):
    with open(file) as f:
        data = json.load(f)
        return data
    return None

def load_repo_list(file):
    content = open_file(file)
    return content.split('\n')

def download_job_info(repo, build_id, job_id):
    folder = get_job_dir(repo, build_id, job_id)
    create_dir(folder)
    log = get_job_log(job_id)
    save_file(folder, 'log.txt', log)

def download_build_info(repo, build_id):
    folder = get_build_dir(repo, build_id)
    create_dir(folder)
    build = get_build(build_id)
    save_json(folder, 'info.json', build)
    jobs = build['matrix']
    jobs_id = [ str(job['id']) for job in jobs ]
    for job_id in jobs_id:
        download_job_info(repo, build_id, job_id)
    tar_and_delete(folder)

def download_repo_info(repo):
    folder = get_repo_dir(repo)
    create_dir(folder)
    builds = get_builds(repo, max_builds_collected=100)
    builds_id = [ str(build['id']) for build in builds ]
    for build_id in builds_id:
        download_build_info(repo, build_id)

def tar_and_delete(path):
    tar_dir(path)
    delete_dir(path)

def delete_dir(path):
    shutil.rmtree(path)

def tar_dir(path):
    folder_name = path.split('/')[-1]
    parent_dir = "/".join(path.split('/')[:-1])
    with tarfile.open(f'{parent_dir}/{folder_name}.tar.bz', "w:bz2") as tar_handle:
        for root, dirs, files in os.walk(path):
            for file in files:
                file_path = os.path.join(root, file)
                tar_handle.add(file_path, arcname=file_path[len(path):])

def create_dir(dir):
    if not os.path.exists(dir):
        os.makedirs(dir)

def get_repo_names(min_size=1):
    repos = [ "/".join(d.split("/")[-3:-1]) for d in glob.glob(f'{get_dir("")}/*/*/') ]
    return [ repo for repo in repos if number_of_builds(repo) >= min_size ]

def get_builds_id(repo):
    return [ tar.split('/')[-1].split('.')[0] for tar in glob.glob(f'{get_repo_dir(repo)}/*.tar.bz') ]

def number_of_builds(repo):
    return len(glob.glob(f'{get_repo_dir(repo)}/*.tar.bz'))

def start_pool(repos, size):
    print(repos)

    def get_repo():
        if len(repos) > 0:
            return repos.pop()
        return False

    def process_download(get_next):
        repo = get_next()
        while repo:
            print(f'Get {repo}')
            download_repo_info(repo)
            repo = get_next()

    threads = []
    for i in range(size):
        threads.append(threading.Thread(target=process_download, args=(get_repo,)))
    for thread in threads:
        thread.start()
    for thread in threads:
        thread.join()

if __name__ == "__main__":
    create_dir(__base_dir)
    if len(sys.argv) >= 2 and sys.argv[1] == "get":
        if len(sys.argv) >= 3:
            repo_list = sys.argv[2:]
            for repo in repo_list:
                try:
                    download_repo_info(repo)
                except:
                    print(repo)
    if len(sys.argv) >= 4 and sys.argv[1] == "get-pool":
        number_of_threads = int(sys.argv[2])
        repo_list = sys.argv[3:]
        start_pool(repo_list, number_of_threads)
    if len(sys.argv) >= 3 and sys.argv[1] == "get-pool-missing":
        number_of_threads = int(sys.argv[2])
        repo_list = set(load_repo_list('./repos.txt')) - set(load_repo_list('./list.txt'))
        start_pool(repo_list, number_of_threads)
