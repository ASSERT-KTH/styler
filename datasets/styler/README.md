# Styler repair attempts

## Structure

- {dataset}-corpus (the original data with errors)

- {dataset}

  - files (the files to be repaired)
  - repair-attempt 
    - batch_0
    - batch_1
    - ...
  - files-repaired
  - waste (the files that for some reasons are no longer understood by javalang in python)

  - checkstyle.xml


## Update the diffs

Go in {dataset}/repair-attempt and run the followinf cmd:

```bash
for batch in $(ls); do
        for id in $(ls $batch); do
                diff -C 5 ./$batch/$id/*.java ../files/1/$id/*.java > ./$batch/$id/diff.diff
        done
done
```
