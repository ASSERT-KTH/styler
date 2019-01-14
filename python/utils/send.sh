mkdir remote_files
cp ./learning-I.txt ./remote_files/src-train.txt
cp ./learning-O.txt ./remote_files/tgt-train.txt
cp ./validation-I.txt ./remote_files/src-val.txt
cp ./validation-O.txt ./remote_files/tgt-val.txt
cp ./testing-I.txt ./remote_files/src-test.txt
# touch ./remote_files/src-test.txt
dataset=$(basename "$PWD")-sy-of
uid=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c 13)
name=$(echo "$dataset"_"$uid")
ssh snic $(echo "mkdir ~/pfs/data/dataset/$name")
scp ./remote_files/* snic:~/pfs/data/dataset/$name/
ssh snic $(echo "bash ~/pfs/scripts/run.sh $name")
