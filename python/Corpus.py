import os

import random
import shutil
import pprint

import uuid

from Corpus import *

pp = pprint.PrettyPrinter(indent=4)

class Corpus:
    def __init__(self, path, name):
        self.path = path
        self.name = name
        self.training_data_folder_path = os.path.join(self.path, "data")
        self.training_data_file_extension = (".java")
        # Maybe create a future folder corpus.json with the specifiques files ?
        for file in os.listdir(self.path):
            if file == "checkstyle.xml":
                self.checkstyle = os.path.join(self.path, file)
                break
        self.files = []
        self.update_files_list()

    def update_files_list(self):
        file_list = dict()
        l = len(self.training_data_folder_path)
        id = 0
        for folder in os.walk(self.training_data_folder_path):
            files = folder[2]
            if (len(folder[0]) >= l):
                relative_folder = "." + folder[0][l:]
                for file_name in files:
                    if ( file_name[0] != '.' and file_name.endswith(self.training_data_file_extension) ):
                        file_list[id] = (file_name, relative_folder, os.path.join(folder[0], file_name))
                        id += 1
            else:
                pass
        self.files = file_list

    def get_number_of_files(self):
        return len(self.files)

    def get_files(self):
        return self.files

    def get_file(self, id):
        return self.files[id]

    def get_file_path(self, id):
        return self.files[id][2]

    def __repr__(self):
        return self.__str__()

    def __str__(self):
        return "( Corpus : " + self.name + " " + str(len(self.files)) + " files" + ")"

class CorpusLeaveKOut(Corpus):
    def __init__(self, path, name, k):
        self.k = k
        Corpus.__init__(self, path, name)


    def update_files_list(self):
        Corpus.update_files_list(self)
        self.left_out_files = random.sample(self.files, self.k)
        for f in self.left_out_files:
            self.files.remove(f)

    def copy_left_out_files(self, dir):
        return self.copy_files(dir, self.left_out_files)
        # files_dir = []
        # if not os.path.exists(dir):
        #     os.makedirs(dir)
        # for file in self.left_out_files:
        #     file_dir = os.path.join(dir, file[2])
        #     shutil.copy(file[1], file_dir)
        #     files_dir.append(file_dir)
        # return files_dir

    def get_left_out_files_path(self):
        return [ f[1] for f in self.left_out_files]
