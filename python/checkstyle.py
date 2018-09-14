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
    output = parse_res(output)
    return (output, process.returncode)

def parse_res(output):
    xml_output = ET.fromstring(output)
    output_parsed = dict()
    for elem_file in xml_output.getchildren():
        output_parsed[elem_file.attrib['name']] = dict()
        output_parsed[elem_file.attrib['name']]['errors'] = list()
        for elem_error in elem_file.getchildren():
            if ( elem_error.tag == 'error' ):
                output_parsed[elem_file.attrib['name']]['errors'].append(elem_error.attrib)
    return output_parsed

if __name__ == "__main__":
    checkstyle_path = "./test_corpora/commons-lang/checkstyle.xml"
    file_path = "./ugly/java-design-patterns/Target.java ./ugly/java-design-patterns/Client.java"
    if ( len(sys.argv) >= 3):
        checkstyle_path = sys.argv[1]
        file_path = sys.argv[2]

    (output_raw, errorcode) = check(checkstyle_path, file_path)
    print(output_raw, errorcode)
