#! /bin/bash

export THC_CACHING_ALLOCATOR=0
project=$1

python ../OpenNMT-py/train.py \
	-data $TRAINING_DATA/$project/preprocesing \
	-global_attention mlp \
	-word_vec_size 64 \
	-rnn_size 256 \
	-layers 1 \
	-encoder_type brnn \
	-max_grad_norm 2 \
	-dropout 0.3 \
	-batch_size 8 \
	-optim adagrad \
	-learning_rate 0.10 \
	-adagrad_accumulator_init 0.1 \
	-bridge \
	-train_steps 50000 \
	-gpu_ranks 0 \
	-valid_batch_size 2 \
	-save_model $TRAINING_DATA/$project/model
