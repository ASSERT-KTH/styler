#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""A basic python wrapper for checkstyle
    """


import xml.etree.ElementTree as ET
import subprocess
import sys

_CHECKSTYLE_JAR = "../jars/checkstyle-8.12-all.jar"


def check(checkstyle_file_path, file_path):
    cmd = "java -jar {} -f xml -c {} {}".format(_CHECKSTYLE_JAR, checkstyle_file_path, file_path)
    process = subprocess.Popen(cmd.split(" "), stdout=subprocess.PIPE)
    output = process.communicate()[0]
    # deletion of non xml strings
    if ( process.returncode > 0):
        output = b''.join(output.split(b'</checkstyle>')[0:-1]) + b'</checkstyle>'
    # parsing
    output = ET.fromstring(output)
    return (output, process.returncode)

if __name__ == "__main__":
    checkstyle_path = "./test_corpora/java-design-patterns/checkstyle.xml"
    file_path = "./ugly/java-design-patterns/Target.java ./ugly/java-design-patterns/Client.java"
    if ( len(sys.argv) >= 3):
        checkstyle_path = sys.argv[1]
        file_path = sys.argv[2]

    (output_raw, errorcode) = check(checkstyle_path, file_path)
    print(output_raw, errorcode)

    for child in output_raw[0]:
        print(child.tag, child.attrib)
