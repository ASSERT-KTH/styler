import git
from git import Repo
import os
import configparser

config = configparser.ConfigParser()
config.read('./travis/config.ini')

__git_repo_dir = config['DEFAULT']['git_repo_dir']


def clone_repo(user, repo_name):
    dir = get_repo_dir(user, repo_name)
    print(f'Clonning {user}/{repo_name}')
    return Repo.clone_from(f'git@github.com:{user}/{repo_name}.git', dir)


def get_repo_dir(user, repo_name):
    return os.path.join(__git_repo_dir, f'./{user}/{repo_name}')


def open_repo(user, repo_name):
    dir = get_repo_dir(user, repo_name)
    if os.path.exists(dir):
        return Repo(dir)
    else:
        return clone_repo(user, repo_name)


def has_commit(repo, sha):
    return sha in [ str(c) for c in repo.iter_commits() ]
