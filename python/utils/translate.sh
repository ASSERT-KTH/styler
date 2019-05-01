#! /bin/bash

export THC_CACHING_ALLOCATOR=0
data_path="/home/l/loriotbe/pfs/data"
n_best=5
python ../OpenNMT-py/translate.py \
	-model $data_path/dataset/$dataset/model_step_50000.pt\
	-src $data_path/dataset/$dataset/src-test.txt\
	-output $data_path/dataset/$dataset/pred_$n_best.txt\
	-n_best $n_best
