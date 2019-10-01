# Real error dataset

This folder contains all scripts related to the real errors.

## Files

- `config.example.ini`: it's an example file of the configuration for working with real errors. One should create a file `config.ini` based on `config.example.ini`.
- `input_projects.txt`: it should contain a list of GitHub projects, one per line, which are the projects to collect errors from.
- `core_real_dataset.py`: the script with shared things between the scripts below.
- `collect_real_errors.py`: the script responsible to collect errors from the projects defined in `input_projects.txt`.
- `create_real_error_dataset.py`: the script responsible for processing the errors collected with `collect_real_errors.py`.
- `real_errors_stats.py`: the script responsible to present stats of the errors collected with `collect_real_errors.py`.
- `experiments.py`: the script with experimentation-related things.

## Output of `collect_real_errors.py`

The collection process results in the following structure:

```
├── <repo name>: the name of the GitHub repository
│   └── <commit id>: the commit where Checkstyle errors were reproduced
│       └── <file with error id>: an id for the file; the ids of files start with 0 and go until the number of files with errors - 1 in the commit
│           ├── <file name>.java: the Java class with error(s)
│           └── errors.json: a json file containing information on the error(s) such as Checkstyle rule violation type and error line
└── ...
```

## Output of `create_real_error_dataset.py`

The errors collected with `collect_real_errors.py` are processed by `create_real_error_dataset.py` so that duplicated errors are removed, for instance. This process results in the following structure:

```
├── <dataset root folder>: 
│   └── <repo name>: the name of the GitHub repository
│       └── <folder id>: an id for the folder; the ids of the folders represent the number of error per file inside the folder, e.g. if the id of the folder is "1", it means that inside the folder there will be files containing 1 error.
│           ├── <file with error id>: an id for the file; the ids of files start with 0 and go until the number of files with errors - 1 in the commit
│               ├── <file name>.java: the Java class with error(s)
│               └── metadata.json: a json file containing information on the error(s) such as Checkstyle rule violation type and error line
└── ...
```

