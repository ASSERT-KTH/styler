import os

from Corpus import *
from Experiment import *

import matplotlib
matplotlib.use('TkAgg')
import matplotlib.pyplot as plt

# from matplotlib import pyplot

pp = pprint.PrettyPrinter(indent=4)

def list_corpora(corpora_dir):
    folders = next(os.walk(corpora_dir))[1]
    corpora = list()
    for folder in folders:
        for i in range(20):
            corpora.append( CorpusLeaveKOut(os.path.join(corpora_dir, folder) , folder, 1) )
    return corpora

if __name__ == "__main__":
    corpora = list_corpora("./test_corpora")
    # if os.path.exists("./experiments"):
    #     shutil.rmtree("./experiments")

    starts = time.time()

    experiments = []

    for corpus in corpora:
        print(corpus)

        experiments.append(Exp_Uglify(corpus, 1, iterations = 25))

    for exp in experiments:
        exp.save_informations()
        exp.daemon = True
        exp.start()

    res = []
    res_naturalize = []
    res_codebuff = []


    for exp in experiments:
        exp.join()
        exp.save_results()
        res.append(exp.results["corrupted_file_proportion"])
        if "corrupted_file_proportion" in exp.results["naturalize"]:
            res_naturalize.append(exp.results["naturalize"]["corrupted_file_proportion"])
        if "corrupted_file_proportion" in exp.results["codebuff"]:
            res_codebuff.append(exp.results["codebuff"]["corrupted_file_proportion"])

    # for exp in experiments:
    #     exp.clean_up()

    ends = time.time()

    print(ends - starts, "s")

    print(res, res_naturalize, res_codebuff)

    # Create a figure instance
    fig = plt.figure(1, figsize=(6, 4))

    # Create an axes instance
    ax = fig.add_subplot(111)


    # Create the boxplot
    bp = ax.boxplot([res, res_naturalize, res_codebuff])

    ax.set_ylim(0,1)
    xtickNames = plt.setp(ax, xticklabels=["jUglify", "Naturalize", "Codebuff"])
    plt.setp(xtickNames, rotation=45, fontsize=8)

    plt.show()
