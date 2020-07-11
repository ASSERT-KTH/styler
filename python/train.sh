#! /bin/bash

export THC_CACHING_ALLOCATOR=0
PROJECT=$1
PROTOCOL=$2
global_attention=$3
layers=$4
rnn_size=$5
word_vec_size=$6

training_output_folder=$PROJECTS_FOLDER/$PROJECT/04_models
#if [ -d "$training_output_folder" ]; then
#	rm -rf $training_output_folder
#fi
if [ ! -d "$training_output_folder" ]; then
    mkdir -p $training_output_folder
fi

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
time python $DIR/OpenNMT-py/train.py \
	-data $PROJECTS_FOLDER/$PROJECT/03_preprocessed_error_dataset/$PROTOCOL/preprocessing \
	-global_attention $global_attention \
	-encoder_type brnn \
	-decoder_type rnn \
	-layers $layers \
	-rnn_size $rnn_size \
	-word_vec_size $word_vec_size \
	-batch_size 32 \
	-optim adagrad \
	-max_grad_norm 2 \
	-learning_rate 0.10 \
	-adagrad_accumulator_init 0.1 \
	-bridge \
	-gpu_ranks 0 \
	-train_steps 20000 \
	-save_checkpoint_steps 20000 \
	-save_model $training_output_folder/$PROTOCOL-$global_attention-$layers-$rnn_size-$word_vec_size-model
