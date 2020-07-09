#! /bin/bash

PROJECT=$1
PROTOCOL=$2

preprocessing_output_folder=$PROJECTS_FOLDER/$PROJECT/03_preprocessed_error_dataset/$PROTOCOL
if [ -d "$preprocessing_output_folder" ]; then
	rm -rf $preprocessing_output_folder
fi
mkdir -p $preprocessing_output_folder

python ./OpenNMT-py/preprocess.py \
	-train_src $PROJECTS_FOLDER/$PROJECT/02_tokenized_error_dataset/$PROTOCOL/learning-I.txt \
	-train_tgt $PROJECTS_FOLDER/$PROJECT/02_tokenized_error_dataset/$PROTOCOL/learning-O.txt \
	-valid_src $PROJECTS_FOLDER/$PROJECT/02_tokenized_error_dataset/$PROTOCOL/validation-I.txt \
	-valid_tgt $PROJECTS_FOLDER/$PROJECT/02_tokenized_error_dataset/$PROTOCOL/validation-O.txt \
	-save_data $preprocessing_output_folder/preprocessing \
	-src_seq_length 650 \
	-tgt_seq_length 105 \
	-src_vocab_size 165 \
	-tgt_vocab_size 165
	