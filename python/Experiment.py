import juglify
import checkstyle
import threading
import uuid
import os
import time
import shutil
import subprocess
import random
import re

import json

from Corpus import *

pp = pprint.PrettyPrinter(indent=4)

_GROUP = None

def set_experiment_group(name):
    _GROUP = name

def get_files_path(dir):
    print(dir)
    paths = []
    for path, subdirs, files in os.walk(dir):
        for name in files:
            paths.append(os.path.join(path, name))
    return paths

def call_java(jar, args):
    cmd = "java -jar {} {}".format(jar, " ".join(args))
    # print(cmd)
    process = subprocess.Popen(cmd.split(" "), stdout=subprocess.PIPE)
    output = process.communicate()[0]
    return output

# add multithreading + experiment_pool
class Experiment(threading.Thread):

    def __init__(self, corpus, name):
        threading.Thread.__init__(self)
        self.corpus = corpus
        self.name = name
        self.parameters = dict()
        self.id = uuid.uuid4().hex
        self.base_dir = "./experiments/" + name + "/" + corpus.name + "_" + self.id + "/"
        if not os.path.exists(self.base_dir):
             os.makedirs(self.base_dir)
        self.results = None
        self.date = time.time()

    def get_informations(self):
        informations = dict()
        informations["corpus"] = str(self.corpus)
        informations["name"] = self.name
        informations["parameters"] = self.parameters
        return informations

    def save_informations(self):
        with open(self.get_dir('./experiment.json'), 'w') as fp:
            json.dump(self.get_informations(), fp)

    def add_parameter(self, name, value):
        self.parameters[name] = value
        return value

    def get_parameter(self, name):
        return self.parameters[name]

    def get_dir(self, dir=""):
        return os.path.join(self.base_dir, dir)

    def run(self):
        pass

    def present_results(self):
        if ( self.results is not None ):
            pp.pprint(self.results)
        else :
            print("no results to present")

    def clean_up(self):
        shutil.rmtree(self.base_dir);

    def log(self, message):
        print("[" + self.name + "_" + self.id + "]" + message)

    def save_results(self):
        if ( self.results is not None ):
            with open(self.get_dir('./results.json'), 'w') as fp:
                json.dump(self.results, fp)
        else:
            print("no results to present")

class Exp_Uglify(Experiment):

    def __init__(self, corpus, modification_number = 1, iterations = (5,5)):
        Experiment.__init__(self, corpus, "uglify")
        self.add_parameter("iterations", iterations)
        self.add_parameter("modification_number", modification_number)

    def get_informations(self):
        informations = Experiment.get_informations(self)
        return informations

    def call_naturalize(self, training_dir, files_dir, output_dir):
        return call_java("../jars/naturalize.jar", [training_dir, output_dir, files_dir])

    def call_codebuff(self, training_dir, files_dir, output_dir):
        return call_java("../jars/codebuff-1.5.1.jar", ["-g org.antlr.codebuff.Java8", "-rule compilationUnit", "-corpus " + training_dir, "-files java", "-comment LINE_COMMENT", "-indent 2", "-o " + output_dir, files_dir])

    def run(self):
        self.log("Starting...")

        os.makedirs(self.get_dir("ugly/"))



        self.log("Insertions")
        for index in range(self.get_parameter("iterations")[0]):
            for id, file in self.corpus.get_files().items():
                juglify.gen_ugly( file[2], self.get_dir( os.path.join("./ugly/" + str(id) + "/insertions/" + str(index) + "/")), 1)

        self.log("Deletions")
        for index in range(self.get_parameter("iterations")[1]):
            for id, file in self.corpus.get_files().items():
                juglify.gen_ugly( file[2], self.get_dir( os.path.join("./ugly/" + str(id) + "/deletions/" + str(index) + "/")), 1)


        self.log("Checkstyle")
        (checkstyle_res, errors) = checkstyle.check(self.corpus.checkstyle, self.get_dir( "ugly/" ) )

        file_with_cs_errors, checkstyle_errors_count = self.parse_result(checkstyle_res, self.get_dir("ugly/"))

        self.log("Naturalize")
        for id, value in file_with_cs_errors.items():
            self.call_naturalize( self.corpus.training_data_folder_path, self.get_dir(os.path.join("./ugly/" + str(id))), self.get_dir(os.path.join("./naturalize/" + str(id))) )
        (checkstyle_res, errors) = checkstyle.check(self.corpus.checkstyle, self.get_dir( "naturalize/" ) )

        file_with_cs_errors_naturalize, checkstyle_errors_count_naturalize = self.parse_result(checkstyle_res, self.get_dir("naturalize/"))

        self.log("Codebuff")
        for id, value in file_with_cs_errors.items():
            self.call_codebuff( self.corpus.training_data_folder_path, self.get_dir(os.path.join("./ugly/" + str(id))), self.get_dir(os.path.join("./codebuff/" + str(id))) )
        (checkstyle_res, errors) = checkstyle.check(self.corpus.checkstyle, self.get_dir( "codebuff/" ) )

        file_with_cs_errors_codebuff, checkstyle_errors_count_codebuff = self.parse_result(checkstyle_res, self.get_dir("codebuff/"))


        self.results = dict()

        self.results["checkstyle_errors_count"] = checkstyle_errors_count
        self.results["corrupted_files_ratio"] = sum( [ len(val) for key, val in file_with_cs_errors.items() ] )  / (len(self.corpus.files) * sum(self.get_parameter("iterations")) )
        self.results["checkstyle_errors_count_naturalize"] = checkstyle_errors_count_naturalize
        self.results["corrupted_files_ratio_naturalize"] = sum( [ len(val) for key, val in file_with_cs_errors_naturalize.items() ] )  / (len(self.corpus.files) * sum(self.get_parameter("iterations")) )
        self.results["checkstyle_errors_count_codebuff"] = checkstyle_errors_count_codebuff
        self.results["corrupted_files_ratio_codebuff"] = sum( [ len(val) for key, val in file_with_cs_errors_codebuff.items() ] )  / (len(self.corpus.files) * sum(self.get_parameter("iterations")) )

        return self.results

    def parse_result(self, checkstyle_res, path):
        file_with_cs_errors = dict()
        checkstyle_errors_count = dict()
        for name, value in checkstyle_res.items():
            file_path = name[(name.find(path) + len(path)):]
            # print(file_path)
            file_path_args = file_path.split("/")
            file_id = int(file_path_args[0])
            corpus_file = self.corpus.get_file(file_id)
            file_path = corpus_file[1] + "/" + corpus_file[0]
            type = file_path_args[1]
            modification_id = file_path_args[2]
            # print(type, modification_id, file_path)
            if ( len(value["errors"]) > 0 ):
                if ( file_id not in file_with_cs_errors):
                    file_with_cs_errors[ file_id ] = []

                file_with_cs_errors[ file_id ].append({"type": type, "modification_id": modification_id, "errors": value["errors"]})
            # print(type, number, file_path)
            for error in value["errors"]:
                if ( error["source"] not in checkstyle_errors_count):
                    checkstyle_errors_count[error["source"]] = 0
                checkstyle_errors_count[error["source"]] += 1
        return file_with_cs_errors, checkstyle_errors_count

    def rename_checkstyle_output(res, files):
        res_renamed = dict()
        for key in res:
            for file in files:
                if key.endswith(file):
                    res_renamed[file] = res[key]
                    continue
        return res_renamed
