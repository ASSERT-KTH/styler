# Styler

## Init the virtual env

Init virtualenv :
```
pip instal virtualenv
virtualenv --python=python3.6 env
source ./env/bin/activate
pip install -r ./requirements.txt
```


## Trainning

Before predicting the repairs, Styler needs to be trained for the project.
```
python ./styler.py train [path to project] [path to checkstyle.xml] [model name]
```

## Repair

Init virtualenv :
```
python ./styler.py repair [model name] [file dir]
```
