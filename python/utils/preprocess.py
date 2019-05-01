python ../OpenNMT-py/preprocess.py \
	-train_src $TRAINING_DATA/dataset/$dataset/src-train.txt \
	-train_tgt $TRAINING_DATA/dataset/$dataset/tgt-train.txt \
	-valid_src $TRAINING_DATA/dataset/$dataset/src-val.txt \
	-valid_tgt $TRAINING_DATA/dataset/$dataset/tgt-val.txt \
	-save_data $TRAINING_DATA/dataset/$dataset/preprocesing \
	-src_seq_length 650 \
	-tgt_seq_length 105 \
	-src_vocab_size 165 \
	-tgt_vocab_size 165
