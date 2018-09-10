import juglify
import checkstyle
import threading
import uuid
import os
import time
import shutil
import subprocess

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
        self.results = None
        self.training_files_dir = self.get_dir( "training_files/" )
        self.training_files = self.corpus.copy_training_files(self.training_files_dir)
        self.date = time.time()

    def get_informations(self):
        informations = dict()
        informations["corpus"] = str(self.corpus)
        informations["name"] = self.name
        informations["parameters"] = self.parameters
        informations["training_files"] = self.training_files
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

    def __init__(self, corpus, modification_number, iterations = 1):
        Experiment.__init__(self, corpus, "uglify")
        self.add_parameter("iterations", iterations)
        self.add_parameter("modification_number", modification_number)
        self.left_out_files_dir = self.get_dir( "left_out_files/")
        self.left_out_files = self.corpus.copy_left_out_files(self.left_out_files_dir)

    def get_informations(self):
        informations = Experiment.get_informations(self)
        informations["left_out_files"] = self.left_out_files
        return informations

    def call_naturalize(self, training_dir, files_dir, output_dir):
        return call_java("../jars/naturalize.jar", [training_dir, output_dir, files_dir])

    def call_codebuff(self, training_dir, files_dir, output_dir):
        return call_java("../jars/codebuff-1.5.1.jar", ["-g org.antlr.codebuff.Java8", "-rule compilationUnit", "-corpus " + training_dir, "-files java", "-comment LINE_COMMENT", "-indent 2", "-o " + output_dir, files_dir])

    def run(self):
        self.log("starting")
        test_files = self.left_out_files
        ugly_files = []

        # (res, errors) = checkstyle.check(self.corpus.checkstyle, " ".join(test_files))
        # if (errors is not 0):
        #     raise ValueError('The corpus has checkstyle error(s)')

        for i in range(self.get_parameter("iterations")):
            for f in test_files:
                ugly_files.append(juglify.gen_ugly(f, self.get_dir( "ugly/" + str(i) + "/"), modification_number=self.get_parameter("modification_number")))

        # Naturalize
        self.call_naturalize(self.training_files_dir, self.get_dir( "ugly/"), self.get_dir( "naturalize/") )
        naturalized_files = get_files_path(self.get_dir( "naturalize/"))

        # Codebuff
        self.call_codebuff(self.training_files_dir, self.get_dir( "ugly/"), self.get_dir( "codebuff/") )
        codebuffed_files = get_files_path(self.get_dir( "codebuff/"))

        (res, errors) = checkstyle.check(self.corpus.checkstyle, " ".join(ugly_files))
        res = Exp_Uglify.rename_checkstyle_output(res, ugly_files)

        self.results = { "errors": errors, "details": {key:len(res[key]["errors"]) for key in ugly_files} }
        self.results["errors_per_file"] = errors / len(ugly_files)
        self.results["corrupted_file_proportion"] = sum([ len(res[file]["errors"]) > 0 for file in res]) / len(ugly_files)

        self.results["naturalize"] = dict()
        try:
            (res, errors) = checkstyle.check(self.corpus.checkstyle, " ".join(naturalized_files))
            res = Exp_Uglify.rename_checkstyle_output(res, naturalized_files)
            self.results["naturalize"]["corrupted_file_proportion"] = sum([ len(res[file]["errors"]) > 0 for file in res]) / len(naturalized_files)
        except:
            self.log("Error, cannot parse the naturaliz output")

        self.results["codebuff"] = dict()
        try:
            (res, errors) = checkstyle.check(self.corpus.checkstyle, " ".join(codebuffed_files))
            res = Exp_Uglify.rename_checkstyle_output(res, codebuffed_files)

            self.results["codebuff"]["corrupted_file_proportion"] = sum([ len(res[file]["errors"]) > 0 for file in res]) / len(codebuffed_files)
        except:
            self.log("Error, cannot parse the codebuff output")


        return self.results

    def rename_checkstyle_output(res, files):
        res_renamed = dict()
        for key in res:
            for file in files:
                if key.endswith(file):
                    res_renamed[file] = res[key]
                    continue
        return res_renamed
