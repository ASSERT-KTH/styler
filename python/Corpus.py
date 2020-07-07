import os
import json

class Corpus:
    def __init__(self, path, name):
        self.path = path
        self.name = name
        self.read_info()
        self.training_data_folder_path = os.path.join(self.path, "data")
        self.training_data_file_extension = (".java")
        # Maybe create a future folder corpus.json with the specifiques files ?
        for file in os.listdir(self.path):
            if file == "checkstyle.xml":
                self.checkstyle = os.path.join(self.path, file)
                break
        self.files = []
        self.update_files_list()

    def read_info(self):
        with open(os.path.join(self.path, "corpus.json")) as f:
            data = json.load(f)
            self.info = data

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
