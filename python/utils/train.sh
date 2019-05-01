#! /bin/bash

export THC_CACHING_ALLOCATOR=0
data_path="/home/l/loriotbe/pfs/data"
python ../OpenNMT-py/train.py \
	-data $data_path/dataset/$dataset/preprocesing \
	-global_attention mlp \
	-word_vec_size 64 \
	-rnn_size 256 \
	-layers 1 \
	-encoder_type brnn \
	-max_grad_norm 2 \
	-dropout 0.3 \
	-batch_size 32 \
	-optim adagrad \
	-learning_rate 0.10 \
	-adagrad_accumulator_init 0.1 \
	-bridge \
	-train_steps 30000  \
	-gpu_ranks 0 \
	-valid_batch_size 2 \
	-save_model $data_path/dataset/$dataset/model
