# Styler : Learning Formatting Conventions to Repair Checkstyle Errors


## Abstract

Styler is an automatic repair tool to fix formatting errors raised by Checkstyle, a highly configurable formatting-checker for Java. Styler learns fixes for formatting errors and predicts repairs for new errors using machine learning.

Reference: [Styler: Learning Formatting Conventions to Repair Checkstyle Errors](https://arxiv.org/pdf/1904.01754) (Benjamin Loriot, Fernanda Madeiral, Martin Monperrus), arXiv 1904.01754, 2019

## Quickstart

Don't forget to clone the submodules :
```
git clone --recursive [repo]
```
or after clonning :
```
git submodule init
git submodule update
```

## Workflow

## Sample output
For exemple
```Java
...
static class RootModule {
  final A a;
  RootModule(A a) {
    this.a = a;
  }
  @Provides A provideA() { return a; } // <Checkstyle error : "'{' at column 28 should have line break after.">
}
...
```
```Java
...
static class RootModule {
  final A a;
  RootModule(A a) {
    this.a = a;
  }
  @Provides A provideA() {
    return a;
  }

}
...
```

## Content of the repository

This repository is organized as follows:

  * [python](/python) contains the source of Styler and a guide for it's usage.
  * [datasets](/datasets) contains all the datasets used for the experiments in the paper.
  * [jars](/resources) contains the jars of Codebuff, Naturalize and Checkstyle used during the experiments.
  * [results](/website) contains some results of the experiments conducted on Styler.

Each directory contains its own Readme explaining its own internal organization.

## License

This repository is under the MIT license.
