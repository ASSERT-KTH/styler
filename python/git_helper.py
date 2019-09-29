import git
from git import Repo
from git import InvalidGitRepositoryError
import os
import configparser

dir_path = os.path.dirname(os.path.realpath(__file__))
config = configparser.ConfigParser()
config.read(os.path.join(dir_path, "config.ini"))

__git_repo_dir = config['DEFAULT']['git_repo_dir']


def clone_repo(user, repo_name):
    """
    Clone the repo into the repo_dir location
    """
    dir = get_repo_dir(user, repo_name)
    print(f'Cloning {user}/{repo_name}')
    return Repo.clone_from(f'git@github.com:{user}/{repo_name}.git', dir)


def get_repo_dir(user, repo_name):
    """
    returns the location of a given repo
    """
    return os.path.join(__git_repo_dir, f'./{user}/{repo_name}')


def open_repo(user, repo_name):
    """
    Open the repo. If the repo doe not exists it clones it before
    """
    dir = get_repo_dir(user, repo_name)
    if os.path.exists(dir):
        try:        
            return Repo(dir)
        except InvalidGitRepositoryError:
            print("Repo %s not found." % dir)
            return None
    else:
        return clone_repo(user, repo_name)


def has_commit(repo, sha):
    """
    Check is the commit is past of the repo
    """
    return sha in [ str(c) for c in repo.iter_commits() ]
