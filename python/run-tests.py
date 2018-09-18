import os

from Corpus import *
from Experiment import *

import matplotlib
matplotlib.use('TkAgg')
import matplotlib.pyplot as plt

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
    # corpora.append( Corpus("./test_corpora/java-design-patterns-reduced", "java-design-patterns-reduced") )
    # corpora.append( Corpus("./test_corpora/javapoet", "javapoet") )
    # corpora.append( Corpus("./test_corpora/commons-io", "commons-io") )
    # corpora.append( Corpus("./test_corpora/commons-lang", "commons-lang") )
    corpora.append( Corpus("./test_corpora/commons-collections", "commons-collections") )



    starts = time.time()

    experiments = []
    res = []

    modifications = (5,5)

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


    barWidth = 0.25
    bars1 = []
    naturalize_res = []
    codebuff_res = []
    labels = []

    for exp in experiments:
        results = exp.results
        labels.append( exp.corpus.name + "(" + str(exp.corpus.get_number_of_files()) + " files)" )
        bars1.append( results["corrupted_files_ratio"] )
        naturalize_res.append( results["corrupted_files_ratio_naturalize"] )
        codebuff_res.append( results["corrupted_files_ratio_codebuff"] )


    # Set position of bar on X axis
    r1 = np.arange(len(bars1))
    r2 = [x + barWidth for x in r1]
    r3 = [x + barWidth for x in r2]


    # Make the plot
    plt.bar(r1, bars1, color='#3498db', width=barWidth, edgecolor='white', label='Error injection')
    plt.bar(r2, naturalize_res, color='#f1c40f', width=barWidth, edgecolor='white', label='Naturalize')
    plt.bar(r3, codebuff_res, color='#1abc9c', width=barWidth, edgecolor='white', label='Codebuff')


    # Add xticks on the middle of the group bars
    plt.xlabel('Proportion of files with errors (m=' + str(modifications) + ')', fontweight='bold')
    plt.xticks([r + barWidth for r in range(len(bars1))], labels, rotation=45, fontsize=8)
    plt.subplots_adjust(bottom=0.30)
    # Create legend & Show graphic
    plt.legend()
    plt.show()
