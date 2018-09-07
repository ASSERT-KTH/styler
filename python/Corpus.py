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
        self.training_files = []
        self.update_training_files_list()

    def update_training_files_list(self):
        file_list = []
        l = len(self.training_data_folder_path)
        for folder in os.walk(self.training_data_folder_path):
            files = folder[2]
            if (len(folder[0]) >= l):
                relative_folder = "." + folder[0][l:]
                for file_name in files:
                    if ( file_name[0] != '.' and file_name.endswith(self.training_data_file_extension) ):
                        file_list.append((file_name, relative_folder, os.path.join(folder[0], file_name) ))
            else:
                pass
        self.training_files = file_list

    def copy_training_files(self, dir):
        return self.copy_files(dir, self.training_files)

            # if not os.path.exists(output_dir):
            #     os.makedirs(output_dir)

    def copy_files(self, dir, files):
        files_dir = []
        for file in files:
            copy_dir = os.path.join(dir, file[1])
            if not os.path.exists(copy_dir):
                os.makedirs(copy_dir)
            file_dir = os.path.join(copy_dir, file[0])
            shutil.copy(file[2], file_dir)
            files_dir.append(file_dir)
        return files_dir

    def get_training_files_path(self):
        return [ os.join(self.training_data_folder_path, f[1]) for f in self.training_files]

    def __repr__(self):
        return self.__str__()

    def __str__(self):
        return "( Corpus : " + self.name + " " + str(len(self.training_files)) + " training_files" + ")"

class CorpusLeaveKOut(Corpus):
    def __init__(self, path, name, k):
        self.k = k
        Corpus.__init__(self, path, name)


    def update_training_files_list(self):
        Corpus.update_training_files_list(self)
        self.left_out_files = random.sample(self.training_files, self.k)
        for f in self.left_out_files:
            self.training_files.remove(f)

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
