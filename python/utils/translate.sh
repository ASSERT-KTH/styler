#! /bin/bash

export THC_CACHING_ALLOCATOR=0
project=$1
n_best=5

python ../OpenNMT-py/translate.py \
	-model $TRAINING_DATA/$project/model_step_50000.pt\
	-src $TRAINING_DATA/$project/src-test.txt\
	-output $TRAINING_DATA/$project/pred_$n_best.txt\
	-n_best $n_best
	