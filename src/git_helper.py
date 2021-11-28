from git import Repo
from git import InvalidGitRepositoryError

from core import *

def clone_repo(user, repo_name, https=True):
    """
    Clone the repo into the repo_dir location
    """
    dir = get_repo_dir(user, repo_name)
    logger.debug(f'Cloning {user}/{repo_name}')
    if https:
        return (Repo.clone_from(f'https://github.com/{user}/{repo_name}.git', dir), dir)
    else:
        return (Repo.clone_from(f'git@github.com:{user}/{repo_name}.git', dir), dir)


def get_repo_dir(user, repo_name):
    """
    returns the location of a given repo
    """
    repo_slug = f'{user}-{repo_name}'
    return os.path.join(get_tmp_git_dir(repo_slug), repo_slug)


def open_repo(user, repo_name):
    """
    Open the repo. If the repo doe not exists it clones it before
    """
    dir = get_repo_dir(user, repo_name)
    if os.path.exists(dir):
        try:        
            return Repo(dir), dir
        except InvalidGitRepositoryError:
            logger.debug("Repo %s not found." % dir)
            return None
    else:
        return clone_repo(user, repo_name)


def has_commit(repo, sha):
    """
    Check is the commit is past of the repo
    """
    return sha in [ str(c) for c in repo.iter_commits() ]
