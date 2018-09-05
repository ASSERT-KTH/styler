import os

import juglify
import checkstyle

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
        for folder in os.walk(self.training_data_folder_path):
            files = folder[2]
            for file_name in files:
                if ( file_name[0] != '.' and file_name.endswith(self.training_data_file_extension) ):
                    file_list.append((file_name, os.path.join(folder[0], file_name)))
        self.training_files = file_list

    def get_training_files_path(self):
        return [ f[1] for f in self.training_files]

    def __repr__(self):
        return self.__str__()

    def __str__(self):
        return "( Corpus : " + self.name + " " + str(len(self.training_files)) + " training_files" + ")"

def run_test(corpus):
    files = corpus.get_training_files_path()
    ugly_files = []
    for f in files:
        ugly_files.append(juglify.gen_ugly(f, os.path.join(  "ugly/", corpus.name+"/" )))
    (res, errors) = checkstyle.check(corpus.checkstyle, " ".join(ugly_files))
    print(errors)

def list_corpora(corpora_dir):
    folders = next(os.walk(corpora_dir))[1]
    corpora = list()
    for folder in folders:
        corpora.append( Corpus(os.path.join(corpora_dir, folder) , folder) )
    return corpora

if __name__ == "__main__":
    corpora = list_corpora("./test_corpora")
    for corpus in corpora:
        print(corpus)
        run_test(corpus)
