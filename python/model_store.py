import git
from core import *
import logging
import os
import difflib
import time


def clone_and_checkout_models():
    repo_dir = core_config['MODELS']['repo_dir']
    if not os.path.exists(repo_dir):
        # Clone if it doesn't exist
        repo = git.Repo.clone_from(f'git@github.com:{core_config["MODELS"]["repo_location"]}.git', repo_dir)
    else:
        repo = git.Repo(repo_dir)

    # Checkout branch
    branch = core_config['MODELS']['branch']
    if branch in repo.heads:
        repo.git.checkout(branch)
        # Pull
        repo.git.pull("origin", branch)
    else:
        repo.git.checkout('-b', branch)

    return repo


def no_diff(old, new):
    with open(old, "r") as f:
        old_content = f.readlines()
    with open(new, "r") as f:
        new_content = f.readlines()
    return sum(1 for _ in difflib.unified_diff(old_content, new_content)) == 0


def get_model(name, protocol, checkstyle_xml):
    """
    Downloads the most recent up-to-date model for a given project and protocol
    """
    repo = clone_and_checkout_models()

    # Get models for the project
    user = name.split("-")[0]
    project = name.split("-")[1]
    repo_dir = core_config['MODELS']['repo_dir']
    models_path = os.path.join(repo_dir, user, project)
    # Check if there are models
    if os.path.exists(models_path):
        # Get most recent releases first
        releases = sorted(list(map(lambda x: int(x), os.listdir(models_path))), reverse=True)
        for release in releases:
            release_path = os.path.join(models_path, str(release))
            # Check if checkstyle.xml is the same
            stored = os.path.join(release_path, 'checkstyle.xml')
            if os.path.exists(stored) and no_diff(stored, checkstyle_xml):
                # Return model for chosen protocol
                for model in os.scandir(release_path):
                    if model.name.startswith(protocol):
                        return model.path
        logger.debug(f'There are no stored models that match all criteria for project {name}')
        return None
    else:
        logger.debug(f'There are no models stored for project {name}')
        return None


def upload_model(model_path, checkstyle_path, name):
    """
    Upload model located at path to our persistent storage
    """
    repo = clone_and_checkout_models()

    # Copy generated model to release dir
    uid = str(int(time.time()))
    user = name.split("-")[0]
    repo_name = name.split("-")[1]
    repo_dir = core_config['MODELS']['repo_dir']
    new_path_dir = os.path.join(repo_dir, f'{user}/{repo_name}/{uid}/')
    os.system(f'mkdir -p {new_path_dir} && cp {model_path} {new_path_dir} && cp {checkstyle_path} {new_path_dir}/checkstyle.xml')

    # Commit changes
    repo.git.add(f'*')
    repo.git.commit('-m', f'Add release {uid} for project {user}-{repo_name}')

    # "Login"
    ssh_key_path = core_config['MODELS']['ssh_key_path']
    ssh_cmd = f'ssh -v -i {ssh_key_path}'
    branch = core_config['MODELS']['branch']
    with repo.git.custom_environment(GIT_SSH_COMMAND=ssh_cmd):
        repo.git.push('origin', branch)


if __name__ == "__main__":
    upload_model('./experiments/projects/Activiti-Activiti/04_models/random-general-2-512-512-model_step_20000.pt', "./experiments/projects/Activiti-Activiti/real_error_dataset/checkstyle.xml", "Activiti", "Activiti")
