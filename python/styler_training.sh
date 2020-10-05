#! /bin/bash

PROJECT=$1

python styler.py gen_training_data $PROJECT > logs/gen_$PROJECT.log 2>&1
python styler.py tokenize_training_data $PROJECT > logs/tok_$PROJECT.log 2>&1
python styler.py preprocess_training_data $PROJECT > logs/pre_$PROJECT.log 2>&1
python styler.py train_model $PROJECT random general 2 512 512 True > logs/tra_$PROJECT-random.log 2>&1
python styler.py train_model $PROJECT three_grams general 1 512 256 True > logs/tra_$PROJECT-three-grams.log 2>&1
