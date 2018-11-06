import tarfile
import os
from main import *
import atexit
import glob

opened_builds = {}

def get_logs(repo, build, job):
    build_folder = get_build_dir(repo, build)
    if not build_is_open(repo, build):
        open_build(repo, build)
    log_path =  get_dir(f'./{repo}/{build}/{job}/log.txt')
    logs = []
    with open(log_path, 'r') as file:
        logs = file.read().split('\n')
    return logs

def build_is_open(repo, build):
    return os.path.exists(get_build_dir(repo, build))

def open_build(repo, build):
    file = get_dir(f'./{repo}/{build}.tar.bz')
    target = get_build_dir(repo, build)
    create_dir(target)
    with tarfile.open(file, 'r:*') as tar_handle:
        tar_handle.extractall(path=target)
    if repo not in opened_builds:
        opened_builds[repo] = []
    opened_builds[repo] += [build]

def close_build(repo, build):
    delete_dir(get_build_dir(repo, build))

def get_logs_id(repo, build):
    if not build_is_open(repo, build):
        open_build(repo, build)
    return [ tar.split('/')[-2] for tar in glob.glob(f'{get_build_dir(repo, build)}/*/log.txt') ]

def get_builds_id(repo):
    return [ tar.split('/')[-1].split('.')[0] for tar in glob.glob(f'{get_repo_dir(repo)}/*.tar.bz') ]

if __name__ == "__main__":
    print(get_logs('danglotb/dspot', '450796277', '450796278'))


@atexit.register
def clean_up():
    print("Close")
    for repo, builds in opened_builds.items():
        for build in builds:
            close_build(repo, build)
