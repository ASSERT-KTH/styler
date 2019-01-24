# Styler datasets

## Real Error dataset

Structure of the dataset:
* real-errors/
  * &lt;project name>/
    * &lt;number of errors>/
      * &lt;numerical identifier>/
        * <java files with the error>.java
        * metadata.json
        * metadata.json
    * checkstyle.xml

## Synthetic errors

In order to avoid slowdowns on git you have to manually clone the synthetic dataset and keep the repo untracked:
```
git clone https://github.com/kth-tcs/synthetic-checkstyle-error-dataset.git
```

For more information on the structure of the dataset you can have a look on the repo on [GitHub](https://github.com/kth-tcs/synthetic-checkstyle-error-dataset.git)
