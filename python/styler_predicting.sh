#! /bin/bash

PROJECT=$1

python styler.py repair $PROJECT > logs/rep_$PROJECT.log 2>&1
