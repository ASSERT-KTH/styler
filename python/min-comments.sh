#!/bin/bash

FILE_PATH=$1
TEMP_FILE=mktemp
cat $FILE_PATH | perl -e '$_=join("",<>);s%/\*([^*].*?)?\*/%/\*\*/%gs;s%^([^\"\n\r]*(\"[^\"\n\r]*\"[^\"\n\r]*?)*?)//([^*\n\r].*)?$%$1%gm;print' > $TEMP_FILE
cat $TEMP_FILE > $FILE_PATH