# Checkstyle-repair

Automatic repair system for Checkstyle errors

## Usages

### Maven

You can call Checkstyle-repair on demand:

    mvn repair:checkstyle
   
It repairs all the checkstyle errors of the source directory, according to the [list of supported checkstyle errors](https://github.com/kth-tcs/checkstyle-repair/master/supported-error-types.md)

(depends on https://github.com/Spirals-Team/maven-repair)

### Web hook

Add `https://todo.kth.se/todo` as Travis web hook, and Checkstyle-repair will add checkstyle suggestions to the pull-requests of your project.

### Git hook

You can enforce the checkstyle rules using a Git pre-commit hook. TODO write doc. 

## License

This repository is under the MIT license.
