import requests
import sys
import json
import os

__base_dir = "/home/benjaminl/Documents/travis-ci-build-log-dataset"

def get_dir(dir):
    return os.path.join(__base_dir, dir)

def get_travis(endpoint, payload={}):
    r = requests.get('https://api.travis-ci.org' + endpoint, params=payload)
    print(f'Get {r.url}')
    return r;

def get_builds(repo):
    result = []
    after_number = 0
    while after_number != 1:
        if after_number:
            payload = {'after_number': after_number}
        else:
            payload = {}
        result += get_travis(f'/repos/{repo}/builds', payload=payload).json()
        after_number = int(result[-1]["number"])
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
    with open(os.path.join(dir, './info.json'), 'w') as f:
        json.dump(content, f)

def download_job_info(dir, job_id):
    folder = os.path.join(dir, str(job_id))
    if not os.path.exists(folder):
        os.makedirs(folder)
    log = get_job_log(job_id)
    save_file(folder, 'log.txt', log)

def download_build_info(dir, build_id):
    folder = os.path.join(dir, str(build_id))
    if not os.path.exists(folder):
        os.makedirs(folder)
    build = get_build(build_id)
    save_json(folder, 'info.json', build)
    jobs = build['matrix']
    jobs_id = [ job['id'] for job in jobs ]
    for job_id in jobs_id:
        download_job_info(folder, job_id)

def download_repo_info(dir, repo):
    folder = os.path.join(dir, repo)
    if not os.path.exists(folder):
        os.makedirs(folder)
    builds = get_builds(repo)
    builds_id = [ build['id'] for build in builds ]
    for build_id in builds_id:
        download_build_info(folder, build_id)

if __name__ == "__main__":
    if not os.path.exists(__base_dir):
        os.makedirs(__base_dir)
    if sys.argv[1] == "get":
        if len(sys.argv) >= 3:
            repo_list = sys.argv[2:]
            for repo in repo_list:
                download_repo_info(__base_dir, repo)
