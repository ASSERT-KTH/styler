import os

from Corpus import *
from Experiment import *

pp = pprint.PrettyPrinter(indent=4)

def list_corpora(corpora_dir):
    folders = next(os.walk(corpora_dir))[1]
    corpora = list()
    for folder in folders:
        corpora.append( CorpusLeaveKOut(os.path.join(corpora_dir, folder) , folder, 1) )
    return corpora

if __name__ == "__main__":
    corpora = list_corpora("./test_corpora")
    # if os.path.exists("./experiments"):
    #     shutil.rmtree("./experiments")
    for corpus in corpora:
        print(corpus)
        experiments = []

        starts = time.time()

        for i in range(1):
            experiments.append(Exp_Uglify(corpus, 1, iterations = 200))

        for exp in experiments:
            exp.save_informations()
            exp.daemon = True
            exp.start()

        res = []

        for exp in experiments:
            exp.join()
            exp.save_results()
            res.append(exp.results["corrupted_file_proportion"])

        ends = time.time()

        print(ends - starts)

        print(res)
