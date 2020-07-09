#! /bin/bash

export THC_CACHING_ALLOCATOR=0
PROJECT=$1
n_best=5

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
python $DIR/OpenNMT-py/translate.py \
	-model $PROJECTS_FOLDER/$PROJECT/04_models/$PROTOCOL/model_step_20000.pt\
	-src $PROJECTS_FOLDER/$PROJECT/02_tokenized_error_dataset/$PROTOCOL/testing-I.txt\
	-output $PROJECTS_FOLDER/$PROJECT/05_predictions/$PROTOCOL/pred_$n_best.txt\
	-n_best $n_best
	