import os

from Corpus import *
from Experiment import *

import numpy as np

pp = pprint.PrettyPrinter(indent=4)

def list_corpora(corpora_dir):
    folders = next(os.walk(corpora_dir))[1]
    corpora = list()
    for folder in folders:
        corpora.append( Corpus(os.path.join(corpora_dir, folder) , folder) )
    return corpora

if __name__ == "__main__":
    # corpora = list_corpora("./test_corpora")
    # if os.path.exists("./experiments"):
    #     shutil.rmtree("./experiments")

    corpora = []
    # corpora.append( Corpus("./test_corpora/commons-cli", "commons-cli") )
    # corpora.append( Corpus("./test_corpora/commons-io", "commons-io") )
    # corpora.append( Corpus("./test_corpora/commons-lang", "commons-lang") )
    # corpora.append( Corpus("./test_corpora/commons-collections", "commons-collections") )
    # corpora.append( Corpus("./test_corpora/neo4j", "neo4j") )
    corpora.append( Corpus("./test_corpora/java-design-patterns", "java-design-patterns") )




    starts = time.time()

    experiments = []
    res = []

    modifications = (2,2,2,2,2)

    for corpus in corpora:
        print(corpus)
        experiments.append(Exp_Uglify(corpus, modification_number = 1, iterations = modifications))

    for exp in experiments:
        exp.save_informations()
        exp.daemon = True
        exp.start()

    for exp in experiments:
        exp.join()
        exp.save_results()
        exp.present_results()
