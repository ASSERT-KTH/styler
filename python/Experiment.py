import java_lang_utils
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
        self.results = dict()
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
        print("[" + self.name + "_" + self.id + "]" + str(message))

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

    def call_naturalize(self, training_dir, files_dir, output_dir, exclude=None):
        args = ["-t " + training_dir, "-o " + output_dir, "-f " + files_dir]
        if exclude:
            args.append("-exclude " + exclude)
        return call_java("../jars/naturalize.jar", args)

    def call_naturalize_snipper(self, training_dir, files_dir, output_dir, file_with_cs_errors, id, exclude=None):
        args = ["-mode snipper", "-t " + training_dir, "-o " + output_dir, "-f " + files_dir]
        if exclude:
            args.append("-exclude " + exclude)
        for file in file_with_cs_errors[id]:
            path = os.path.join(files_dir, './' + file["type"] + "/" + str(file["modification_id"]) + "/" + self.corpus.get_files()[id][0])
            erorrs_lines = [ int(e["line"]) for e in file["errors"]]
            (from_char, to_char) = java_lang_utils.get_char_pos_from_lines(path, min(erorrs_lines) - 1, max(erorrs_lines) + 1)
            args.append(path + ":" + str(from_char) + ',' + str(to_char))
        return call_java("../jars/naturalize.jar", args)

    def call_codebuff_snipper(self, files_dir, codebuff_dir, output_dir, file_with_cs_errors, id):
        for file in file_with_cs_errors[id]:
            file_path = os.path.join(files_dir, './' + file["type"] + "/" + str(file["modification_id"]) + "/" + self.corpus.get_files()[id][0])
            codebuff_path = os.path.join(codebuff_dir, './' + file["type"] + "/" + str(file["modification_id"]) + "/" + self.corpus.get_files()[id][0])
            output_path = os.path.join(output_dir, './' + file["type"] + "/" + str(file["modification_id"]) + "/" + self.corpus.get_files()[id][0])
            erorrs_lines = [ int(e["line"]) for e in file["errors"]]
            from_line, to_line = (min(erorrs_lines) - 1, max(erorrs_lines) + 1)
            print(file_path, from_line, to_line)
            java_lang_utils.mix_files(file_path, codebuff_path, output_path, from_line, to_line=to_line )

    def call_codebuff(self, training_dir, files_dir, output_dir, exclude=None, grammar = "Java"):
        args = ["-g org.antlr.codebuff." + grammar, "-rule compilationUnit", "-corpus " + training_dir, "-files java", "-comment LINE_COMMENT", "-indent 2", "-o " + output_dir]
        if ( exclude ):
            args.append("-exclude " + exclude)
        args.append(files_dir)
        return call_java("../jars/codebuff-1.5.1.jar", args)

    def move_parse_exception_files(self, from_dir, to_dir):
        files = java_lang_utils.get_bad_formated(self.get_dir(from_dir))
        os.makedirs(self.get_dir(to_dir))
        for file in files:
            shutil.move(file, self.get_dir(to_dir + uuid.uuid4().hex + ".java"))
        return files

    def add_cs_to_results(self, file_with_cs_errors, checkstyle_errors_count, bad_formated, name = ""):
        if (len(name) > 0 and name[0] != "_"):
            name = "_" + name
        self.results["file_with_cs_errors" + name] = file_with_cs_errors
        self.results["checkstyle_errors_count" + name] = checkstyle_errors_count
        self.results["bad_formated" + name] = bad_formated
        self.results["corrupted_files_ratio" + name] = sum( [ len(val) for key, val in file_with_cs_errors.items() ] )  / self.results["number_of_injections"]
        self.save_results()

    def review_checkstyle(self, name):
        bad_formated = self.move_parse_exception_files("./{}/".format(name), "./trash/{}/".format(name))
        self.log("{}-checkstyle".format(name))
        (checkstyle_res, errors) = checkstyle.check(self.corpus.checkstyle, self.get_dir( "{}/".format(name) ) )
        file_with_cs_errors, checkstyle_errors_count = self.parse_result(checkstyle_res, self.get_dir("{}/".format(name)))
        for bad_formated_file in bad_formated:
            splited_path = bad_formated_file.split('/')
            error = {}
            error["type"] = splited_path[-3]
            error["modification_id"] = splited_path[-2]
            error["errors"] = []
            if splited_path[-4] not in file_with_cs_errors:
                file_with_cs_errors[splited_path[-4]] = []
            file_with_cs_errors[splited_path[-4]].append(error)
        checkstyle_errors_count["java.core.ParseException"] = len(bad_formated)
        self.add_cs_to_results(file_with_cs_errors, checkstyle_errors_count, bad_formated, name=name)
        return file_with_cs_errors, checkstyle_errors_count

    def run(self):
        number_of_injections = (len(self.corpus.files) * sum(self.get_parameter("iterations")));
        self.results["number_of_injections"] = number_of_injections
        self.results["name"] = self.corpus.name
        self.results["iterations"] = self.get_parameter("iterations")

        self.log("Starting...")

        os.makedirs(self.get_dir("ugly/"))

        self.log("Insertions")
        for index in range(self.get_parameter("iterations")[0]):
            for id, file in self.corpus.get_files().items():
                java_lang_utils.gen_ugly( file[2], self.get_dir( os.path.join("./ugly/" + str(id) + "/insertions-space/" + str(index) + "/")), (1,0,0,0,0))
        for index in range(self.get_parameter("iterations")[1]):
            for id, file in self.corpus.get_files().items():
                java_lang_utils.gen_ugly( file[2], self.get_dir( os.path.join("./ugly/" + str(id) + "/insertions-tab/" + str(index) + "/")), (0,1,0,0,0))
        for index in range(self.get_parameter("iterations")[2]):
            for id, file in self.corpus.get_files().items():
                java_lang_utils.gen_ugly( file[2], self.get_dir( os.path.join("./ugly/" + str(id) + "/insertions-newline/" + str(index) + "/")), (0,0,1,0,0))

        self.log("Deletions")
        for index in range(self.get_parameter("iterations")[3]):
            for id, file in self.corpus.get_files().items():
                java_lang_utils.gen_ugly( file[2], self.get_dir( os.path.join("./ugly/" + str(id) + "/deletions-space/" + str(index) + "/")), (0,0,0,1,0))
        for index in range(self.get_parameter("iterations")[4]):
            for id, file in self.corpus.get_files().items():
                java_lang_utils.gen_ugly( file[2], self.get_dir( os.path.join("./ugly/" + str(id) + "/deletions-newline/" + str(index) + "/")), (0,0,0,0,1))

        file_with_cs_errors, checkstyle_errors_count = self.review_checkstyle("ugly")

        dirs = []
        for id, files in file_with_cs_errors.items():
            for file in files:
                dirs.append(str(id) + '/' + file["type"] + '/' + str(file["modification_id"]))

        dirs_to_delete = []
        for folder in os.walk(self.get_dir( "ugly/" )):
            sub_dir = folder[0][len(self.get_dir( "ugly/" )):]
            if ( sub_dir.count("/") >= 2):
                if ( sub_dir not in dirs ):
                    dirs_to_delete.append(folder[0])

        for folder in dirs_to_delete:
            shutil.rmtree(folder)

        self.log("Naturalize")
        len_files = len(file_with_cs_errors.keys())
        starts = time.time()
        step = starts
        i = 0;
        for id, value in file_with_cs_errors.items():
            exluded_file = self.corpus.get_file(id)[2]
            i += 1
            self.log("File " + str(id) + ' (' + str(i) + '/' + str(len_files) + ')')
            self.call_naturalize( self.corpus.training_data_folder_path, self.get_dir(os.path.join("./ugly/" + str(id))), self.get_dir(os.path.join("./naturalize/" + str(id))), exclude=exluded_file )
            self.log("File " + str(id) + ' done in ' + str(time.time() - step) + 's, (' + str( (time.time() - starts) / i * (len_files - i) ) + 's remaining)')
            step = time.time()

        file_with_cs_errors_naturalize, checkstyle_errors_count_naturalize = self.review_checkstyle("naturalize")

        self.log("Naturalize-Snipper")
        for id, value in file_with_cs_errors.items():
            exluded_file = self.corpus.get_file(id)[2]
            self.call_naturalize_snipper( self.corpus.training_data_folder_path, self.get_dir(os.path.join("./ugly/" + str(id))), self.get_dir(os.path.join("./naturalize_snipper/" + str(id))), file_with_cs_errors, id, exclude=exluded_file )

        file_with_cs_errors_naturalize_snipper, checkstyle_errors_count_naturalize_snipper = self.review_checkstyle("naturalize_snipper")

        self.log("Codebuff")
        len_files = len(file_with_cs_errors.keys())
        starts = time.time()
        step = starts
        i = 0;
        for id, value in file_with_cs_errors.items():
            exluded_file = self.corpus.get_file(id)[2]
            i += 1
            self.log("File " + str(id) + '(' + str(i) + '/' + str(len_files) + ')')
            res = self.call_codebuff( self.corpus.training_data_folder_path, self.get_dir(os.path.join("./ugly/" + str(id))), self.get_dir(os.path.join("./codebuff/" + str(id))), exclude=exluded_file, grammar=self.corpus.info["grammar"] )
            self.log("File " + str(id) + ' done in ' + str(time.time() - step) + 's, (' + str( (time.time() - starts) / i * (len_files - i) ) + 's remaining)')
            step = time.time()

        file_with_cs_errors_codebuff, checkstyle_errors_count_codebuff = self.review_checkstyle("codebuff")

        self.log("Codebuff-Snipper")
        for id, value in file_with_cs_errors.items():
            self.call_codebuff_snipper(self.get_dir(os.path.join("./ugly/" + str(id))), self.get_dir(os.path.join("./codebuff/" + str(id))), self.get_dir(os.path.join("./codebuff_snipper/" + str(id))), file_with_cs_errors, id)

        file_with_cs_errors_codebuff_snipper, checkstyle_errors_count_codebuff_snipper = self.review_checkstyle("codebuff_snipper")

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
                if ( error["severity"] == "error"):
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


# java -jar ../jars/codebuff-1.5.1.jar -g org.antlr.codebuff.Java8 -rule compilationUnit -corpus ./test_corpora/java-design-patterns-reduced/data -files java -comment LINE_COMMENT -indent 2 -o ./9_codebuff -exclude ./test_corpora/java-design-patterns-reduced/data/composite/src/main/java/com/iluwatar/composite/Messenger.java ./9
