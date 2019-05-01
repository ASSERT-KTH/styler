# Styler

## Init the virtual env

Init virtualenv :
```
pip instal virtualenv
virtualenv --python=python3.6 env
source ./env/bin/activate
pip install -r ./requirements.txt
```
The required version of TorchText may not be available through PyPI, install as instructed here: https://github.com/pytorch/text/issues/517

Setup a `config.ini` file using the provided example: `config.example.ini`

## Training

Before predicting the repairs, Styler needs to be trained for the project.

### Synthetic data generation
First step is to produce synthetic errors. These are used later to train LSTM models.
```
python ./styler.py train [path to project] [path to checkstyle.xml] [project name]
```

The `[project name]` is used to generate paths where the training and repair data will be stored.

### LSTM model training
We use OpenNTM-py for model training.

```
cd OpenNMT-py
python preprocess.py -train_src [path to training source] -train_tgt [path to training target] -valid_src [path to validation source] -valid_tgt [path to validation target] -save_data [path to store data]
python train.py -data [path to store data] -save_model [model name]
```

Training and validation source and target files are stored in `./styler/[project name]-tokens/`

Refer to OpenNMT-py documentation for more information or if GPU usage is needed:
- https://github.com/OpenNMT/OpenNMT-py
- http://opennmt.net/OpenNMT-py/options/train.html

## Repair

Init virtualenv :
```
python ./styler.py repair [model name]
```
The model must be stored in the `./models` path.
