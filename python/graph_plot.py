import matplotlib
matplotlib.use('TkAgg')
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches

import numpy as np

import random
import colorsys


def plot_percentage_of_errors(results):
    modifications = (5,5)

    barWidth = 0.25
    bars1 = []
    naturalize_res = []
    codebuff_res = []
    labels = []

    for result in results:
        # labels.append( exp.corpus.name + "(" + str(exp.corpus.get_number_of_files()) + " files)" )
        labels.append("sdf")
        bars1.append( result["corrupted_files_ratio"] )
        naturalize_res.append( result["corrupted_files_ratio_naturalize"] )
        codebuff_res.append( result["corrupted_files_ratio_codebuff"] )


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

def plot_errors_distribution(results):
    modifications = (5,5)

    barWidth = 0.25
    bars1 = []
    naturalize_res = []
    codebuff_res = []
    labels = []

    errors_labels = set()

    for result in results:
        # labels.append( exp.corpus.name + "(" + str(exp.corpus.get_number_of_files()) + " files)" )
        labels.append("sdf")
        bars1.append( result["corrupted_files_ratio"] )
        errors_labels = errors_labels | result["checkstyle_errors_count"].keys() | result["checkstyle_errors_count_naturalize"].keys()

        # naturalize_res.append( result["corrupted_files_ratio_naturalize"] )
        # codebuff_res.append( result["corrupted_files_ratio_codebuff"] )

    n_errors_labels = len(errors_labels)
    colors = []
    for i in range( 0, n_errors_labels ):
        colors.append('#%02x%02x%02x' % tuple(map(lambda x: int( x*256 ), colorsys.hls_to_rgb( 1 / (n_errors_labels-1) * i * 0.9 , 0.5, 0.5))))
    random.shuffle(colors)
    print(colors)
    lables_colors = dict()
    i = 0
    for error_label in errors_labels:
        lables_colors[error_label] = colors[i]
        i += 1

    def compute_errors_layer(errors_count_name):
        layers = dict()
        for result in results:
            errors = result[errors_count_name]
            for error_label in errors_labels:
                if ( error_label not in layers):
                    layers[error_label] = []
                if ( error_label in errors ):
                    layers[error_label].append(errors[error_label])
                else:
                    layers[error_label].append(0)
        return layers


    layers = compute_errors_layer("checkstyle_errors_count")
    layers_naturalize = compute_errors_layer("checkstyle_errors_count_naturalize")
    layers_codebuff = compute_errors_layer("checkstyle_errors_count_codebuff")



    # Set position of bar on X axis
    r1 = np.arange(len(labels))
    r2 = [x + barWidth for x in r1]
    r3 = [x + barWidth for x in r2]


    def add_layers_to_the_graph(layers, position):
        sum = [0] * len(labels)
        for key, values in layers.items():
            plt.bar(position, values, width=barWidth, color=lables_colors[key], bottom=sum, edgecolor='white')
            sum = list(map( lambda x, y: x + y, sum, values))
    # plt.bar(r2, naturalize_res, color='#f1c40f', width=barWidth, edgecolor='white', label='Naturalize')
    # plt.bar(r3, codebuff_res, color='#1abc9c', width=barWidth, edgecolor='white', label='Codebuff')
    add_layers_to_the_graph(layers, r1)
    add_layers_to_the_graph(layers_naturalize, r2)
    add_layers_to_the_graph(layers_codebuff, r3)


    # Add xticks on the middle of the group bars
    plt.xlabel('Proportion of files with errors (m=' + str(modifications) + ')', fontweight='bold')
    plt.xticks([r + barWidth for r in range(len(bars1))], labels, rotation=45, fontsize=8)
    plt.subplots_adjust(top=0.80)

    plt.subplots_adjust(bottom=0.30)
    # Create legend & Show graphic
    patches = [ mpatches.Patch(color=c, label=l.split(".")[-1]) for l, c in lables_colors.items()]
    plt.legend(handles = patches, loc='upper center', ncol=3, fancybox=True, bbox_to_anchor=(0.5, 1.4))
    plt.show()

if __name__ == "__main__":
    result1 = {"checkstyle_errors_count": {"com.puppycrawl.tools.checkstyle.checks.whitespace.ParenPadCheck": 14, "com.puppycrawl.tools.checkstyle.checks.whitespace.WhitespaceAroundCheck": 102, "com.puppycrawl.tools.checkstyle.checks.whitespace.WhitespaceAfterCheck": 30, "com.puppycrawl.tools.checkstyle.checks.blocks.LeftCurlyCheck": 17, "com.puppycrawl.tools.checkstyle.checks.blocks.RightCurlyCheck": 8, "com.puppycrawl.tools.checkstyle.checks.whitespace.NoWhitespaceAfterCheck": 14, "com.puppycrawl.tools.checkstyle.checks.TodoCommentCheck": 10, "com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocMethodCheck": 12, "com.puppycrawl.tools.checkstyle.checks.whitespace.NoWhitespaceBeforeCheck": 7, "com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocVariableCheck": 1, "com.puppycrawl.tools.checkstyle.checks.coding.MultipleVariableDeclarationsCheck": 1}, "corrupted_files_ratio": 0.6391304347826087, "checkstyle_errors_count_naturalize": {"com.puppycrawl.tools.checkstyle.checks.TodoCommentCheck": 10, "com.puppycrawl.tools.checkstyle.checks.whitespace.WhitespaceAroundCheck": 5, "com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocMethodCheck": 12, "com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocTypeCheck": 1, "com.puppycrawl.tools.checkstyle.checks.whitespace.NoWhitespaceAfterCheck": 1, "com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocVariableCheck": 2}, "corrupted_files_ratio_naturalize": 0.12608695652173912, "checkstyle_errors_count_codebuff": {"com.puppycrawl.tools.checkstyle.checks.whitespace.NoWhitespaceAfterCheck": 40, "com.puppycrawl.tools.checkstyle.checks.TodoCommentCheck": 10, "com.puppycrawl.tools.checkstyle.checks.whitespace.WhitespaceAfterCheck": 10, "com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocTypeCheck": 1, "com.puppycrawl.tools.checkstyle.checks.whitespace.NoWhitespaceBeforeCheck": 10, "com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocVariableCheck": 2, "com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocMethodCheck": 10, "com.puppycrawl.tools.checkstyle.checks.whitespace.ParenPadCheck": 10}, "corrupted_files_ratio_codebuff": 0.3130434782608696}
    result2 = {"checkstyle_errors_count": {"com.puppycrawl.tools.checkstyle.checks.whitespace.NoWhitespaceAfterCheck": 14, "com.puppycrawl.tools.checkstyle.checks.whitespace.ParenPadCheck": 17, "com.puppycrawl.tools.checkstyle.checks.whitespace.WhitespaceAfterCheck": 28, "com.puppycrawl.tools.checkstyle.checks.blocks.RightCurlyCheck": 9, "com.puppycrawl.tools.checkstyle.checks.whitespace.WhitespaceAroundCheck": 104, "com.puppycrawl.tools.checkstyle.checks.blocks.LeftCurlyCheck": 19, "com.puppycrawl.tools.checkstyle.checks.whitespace.NoWhitespaceBeforeCheck": 8, "com.puppycrawl.tools.checkstyle.checks.TodoCommentCheck": 10, "com.puppycrawl.tools.checkstyle.checks.coding.MultipleVariableDeclarationsCheck": 1, "com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocMethodCheck": 10}, "corrupted_files_ratio": 0.6521739130434783, "checkstyle_errors_count_naturalize": {"com.puppycrawl.tools.checkstyle.checks.TodoCommentCheck": 10, "com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocTypeCheck": 1, "com.puppycrawl.tools.checkstyle.checks.whitespace.NoWhitespaceAfterCheck": 1, "com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocVariableCheck": 1, "com.puppycrawl.tools.checkstyle.checks.whitespace.WhitespaceAroundCheck": 1, "com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocMethodCheck": 10}, "corrupted_files_ratio_naturalize": 0.10434782608695652, "checkstyle_errors_count_codebuff": {"com.puppycrawl.tools.checkstyle.checks.whitespace.NoWhitespaceAfterCheck": 40, "com.puppycrawl.tools.checkstyle.checks.TodoCommentCheck": 10, "com.puppycrawl.tools.checkstyle.checks.whitespace.WhitespaceAfterCheck": 10, "com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocTypeCheck": 1, "com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocVariableCheck": 1, "com.puppycrawl.tools.checkstyle.checks.whitespace.NoWhitespaceBeforeCheck": 10, "com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocMethodCheck": 10, "com.puppycrawl.tools.checkstyle.checks.whitespace.ParenPadCheck": 10}, "corrupted_files_ratio_codebuff": 0.3130434782608696}

    results = [result1, result2]
    plot_errors_distribution(results)
