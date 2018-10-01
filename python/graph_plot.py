# -*- coding: utf-8 -*-

import matplotlib
matplotlib.use('TkAgg')
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches

import numpy as np

import random
import colorsys
import sys
import os
import json
import datetime


def plot_repaired_files(results):
    modifications = (2,2,2,2,2)

    counts = ('naturalize', 'naturalize_snipper', 'codebuff', 'codebuff_snipper')

    barWidth = 1. / (len(counts) + 1)
    bars = [[] for i in range(len(counts)) ]

    labels = []

    for result in results:
        # labels.append( exp.corpus.name + "(" + str(exp.corpus.get_number_of_files()) + " files)" )
        with_errors = result["number_of_injections"] * result["corrupted_files_ratio_ugly"]
        labels.append("{}, \n /{} injections".format(result["name"], int(with_errors)))
        for count, i in zip(counts, range(len(counts))):
            prop = result["corrupted_files_ratio_" + count]
            bars[i].append( 1 - ( result["number_of_injections"] * result["corrupted_files_ratio_" + count]) / with_errors )
            # bars[i].append( result["corrupted_files_ratio_" + count] )

    # Set position of bar on X axis
    r = []
    r.append(np.arange(len(labels)))
    for i in range(1,len(counts)):
        r.append([x + barWidth for x in r[i-1]])


    # Make the plot
    for count, i in zip(counts, range(len(counts))):
        plt.bar(r[i], bars[i], width=barWidth, edgecolor='white', label=count)


    # Add xticks on the middle of the group bars
    plt.xlabel('Proportion of files with errors (m=' + str(modifications) + ')', fontweight='bold')
    plt.xticks([r + barWidth * (len(counts)-1) / 2 for r in range(len(results))], labels, rotation=45, fontsize=8)
    plt.subplots_adjust(bottom=0.30)
    # Create legend & Show graphic
    plt.legend()

def plot_diffs(results):
    modifications = (2,2,2,2,2)

    counts = ('naturalize', 'naturalize_snipper', 'codebuff', 'codebuff_snipper')

    barWidth = 1. / (len(counts) + 1)
    bars = [[] for i in range(len(counts)) ]

    labels = []

    for result in results:
        # labels.append( exp.corpus.name + "(" + str(exp.corpus.get_number_of_files()) + " files)" )
        with_errors = result["number_of_injections"] * result["corrupted_files_ratio_ugly"]
        labels.append("{}, \n /{} injections".format(result["name"], int(with_errors)))
        for count, i in zip(counts, range(len(counts))):
            bars[i].append( result["diffs_avg_" + count] )
            # bars[i].append( result["corrupted_files_ratio_" + count] )

    # Set position of bar on X axis
    r = []
    r.append(np.arange(len(labels)))
    for i in range(1,len(counts)):
        r.append([x + barWidth for x in r[i-1]])


    # Make the plot
    for count, i in zip(counts, range(len(counts))):
        plt.bar(r[i], bars[i], width=barWidth, edgecolor='white', label=count)


    # Add xticks on the middle of the group bars
    plt.xlabel('Proportion of files with errors (m=' + str(modifications) + ')', fontweight='bold')
    plt.xticks([r + barWidth * (len(counts)-1) / 2 for r in range(len(results))], labels, rotation=45, fontsize=8)
    plt.subplots_adjust(bottom=0.30)
    # Create legend & Show graphic
    plt.legend()

def plot_percentage_of_errors(results):
    modifications = (5,5)

    barWidth = 0.25
    bars1 = []
    naturalize_res = []
    codebuff_res = []
    labels = []

    for result in results:
        # labels.append( exp.corpus.name + "(" + str(exp.corpus.get_number_of_files()) + " files)" )
        labels.append(result["name"])
        bars1.append( result["corrupted_files_ratio_ugly"] )
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
    plt.xlabel('Number of fully patched files (m=' + str(modifications) + ')', fontweight='bold')
    plt.xticks([r + barWidth for r in range(len(bars1))], labels, rotation=45, fontsize=8)
    plt.subplots_adjust(bottom=0.30)
    # Create legend & Show graphic
    plt.legend()

def plot_errors_types(results, counts): # protocol1
    modifications = (2,2,2,2,2)

    labels = []

    errors_labels = set()

    for result in results:
        # labels.append( exp.corpus.name + "(" + str(exp.corpus.get_number_of_files()) + " files)" )
        labels.append("{} ({})".format(result["name"], result["number_of_injections"]))
        for count in counts:
            errors_labels = errors_labels | result[count].keys()

    total_error_count = dict()
    for error in errors_labels:
        total_error_count[error] = 0
    for result in results:
        for count in counts:
            for error, n in result[count].items():
                total_error_count[error] += n
    sum_total_error_count = sum(total_error_count.values())

    n_errors_labels = len(errors_labels)
    colors = []
    if ( n_errors_labels > 1):
        for i in range( 0, n_errors_labels ):
            colors.append('#%02x%02x%02x' % tuple(map(lambda x: int( x*256 ), colorsys.hls_to_rgb( 1 / (n_errors_labels-1) * i * 0.9 , random.uniform(0.4, 0.6), random.uniform(0.4, 0.6)))))
        random.shuffle(colors)
    else :
        colors.append('#ff00ff')

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

    layers = dict()
    for count in counts:
        layers[count] = compute_errors_layer(count)


    barWidth = 1. / (len(counts) + 1)
    # Set position of bar on X axis
    r = []
    r.append(np.arange(len(labels)))
    for i in range(1,len(counts)):
        r.append([x + barWidth for x in r[i-1]])


    def add_layers_to_the_graph(layers, position):
        sum = [0] * len(labels)
        for key, values in layers.items():
            plt.bar(position, values, width=barWidth, color=lables_colors[key], bottom=sum, edgecolor='white')
            sum = list(map( lambda x, y: x + y, sum, values))
        return sum
    # plt.bar(r2, naturalize_res, color='#f1c40f', width=barWidth, edgecolor='white', label='Naturalize')
    # plt.bar(r3, codebuff_res, color='#1abc9c', width=barWidth, edgecolor='white', label='Codebuff')
    i = 0
    sums = []
    for count, i in zip(counts, range(len(counts))):
        sums.append(add_layers_to_the_graph(layers[count], r[i]))


    # Add xticks on the middle of the group bars
    plt.xlabel('Number of errors (m={}) \n {}'.format(modifications, counts), fontweight='bold')
    plt.xticks([r + barWidth * (len(counts)-1) / 2 for r in range(len(results))], labels, rotation=45, fontsize=8)
    plt.subplots_adjust(top=0.80)

    plt.subplots_adjust(bottom=0.30)
    # Create legend & Show graphic
    patches = [ mpatches.Patch(color=c, label="{} ({:.2f}%)".format(l.split(".")[-1], total_error_count[l] / sum_total_error_count * 100)) for l, c in lables_colors.items()]
    plt.legend(handles = patches, loc='upper center', ncol=3, fancybox=True, bbox_to_anchor=(0.5, 1.4))

def plot_errors_types_per_injection_type(results):
    modifications = (2,2,2,2,2)

    counts = ("insertions-newline", "insertions-space", "insertions-tab", "deletions-newline", "deletions-space")

    labels = []
    errors_labels = set()

    for result in results:
        # labels.append( exp.corpus.name + "(" + str(exp.corpus.get_number_of_files()) + " files)" )
        labels.append("{} ({})".format(result["name"], result["number_of_injections"]))
        errors_labels = errors_labels | result["checkstyle_errors_count_ugly"].keys()

    n_errors_labels = len(errors_labels)
    colors = []
    if ( n_errors_labels > 1):
        for i in range( 0, n_errors_labels ):
            colors.append('#%02x%02x%02x' % tuple(map(lambda x: int( x*256 ), colorsys.hls_to_rgb( 1 / (n_errors_labels-1) * i * 0.9 , random.uniform(0.4, 0.6), random.uniform(0.4, 0.6)))))
        random.shuffle(colors)
    else :
        colors.append('#ff00ff')

    lables_colors = dict()
    i = 0
    for error_label in errors_labels:
        lables_colors[error_label] = colors[i]
        i += 1

    def compute_error_origines(result):
        result["errors_origine"] = dict()
        for file_with_cs_errors in result["file_with_cs_errors_ugly"].values():
            for file_modification in file_with_cs_errors:
                type = file_modification["type"]
                if (type not in result["errors_origine"]):
                    result["errors_origine"][type] = dict()
                for error in file_modification["errors"]:
                    if (error["source"] not in result["errors_origine"][type]):
                        result["errors_origine"][type][error["source"]] = 0
                    result["errors_origine"][type][error["source"]] += 1

    for result in results:
        compute_error_origines(result)

    def compute_errors_layer(injection_type):
        layers = dict()
        for result in results:
            if injection_type in result["errors_origine"]:
                errors = result["errors_origine"][injection_type]
            else:
                errors = []
            for error_label in errors_labels:
                if ( error_label not in layers):
                    layers[error_label] = []
                if ( error_label in errors ):
                    layers[error_label].append(errors[error_label])
                else:
                    layers[error_label].append(0)
        return layers

    layers = dict()
    for count in counts:
        layers[count] = compute_errors_layer(count)


    barWidth = 1. / (len(counts) + 1)
    # Set position of bar on X axis
    r = []
    r.append(np.arange(len(labels)))
    for i in range(1,len(counts)):
        r.append([x + barWidth for x in r[i-1]])


    def add_layers_to_the_graph(layers, position):
        sum = [0] * len(labels)
        for key, values in layers.items():
            plt.bar(position, values, width=barWidth, color=lables_colors[key], bottom=sum, edgecolor='white')
            sum = list(map( lambda x, y: x + y, sum, values))
        return sum
    # plt.bar(r2, naturalize_res, color='#f1c40f', width=barWidth, edgecolor='white', label='Naturalize')
    # plt.bar(r3, codebuff_res, color='#1abc9c', width=barWidth, edgecolor='white', label='Codebuff')
    i = 0
    sums = []
    for count, i in zip(counts, range(len(counts))):
        sums.append(add_layers_to_the_graph(layers[count], r[i]))


    # Add xticks on the middle of the group bars
    plt.xlabel('Number of errors (m={}) \n {}'.format(modifications, counts), fontweight='bold')
    plt.xticks([r + barWidth * (len(counts)-1) / 2 for r in range(len(results))], labels, rotation=45, fontsize=8)
    plt.subplots_adjust(top=0.80)

    plt.subplots_adjust(bottom=0.30)
    # Create legend & Show graphic
    patches = [ mpatches.Patch(color=c, label="{}".format(l.split(".")[-1])) for l, c in lables_colors.items()]
    plt.legend(handles = patches, loc='upper center', ncol=3, fancybox=True, bbox_to_anchor=(0.5, 1.4))

def load_results(dir):
    data = {}
    with open(os.path.join(dir, "results.json")) as f:
        data = json.load(f)
    return data

if __name__ == "__main__":
    fig_name = "figure"
    now = datetime.datetime.now()
    if ( len(sys.argv) > 2):
        type = sys.argv[1]
        save = False
        show = True
        i = 2
        while sys.argv[i].startswith("--"):
            if ( sys.argv[i] == "--save" ):
                save = True
            if ( sys.argv[i] == "--dontShow" ):
                show = False
            i+=1
        folders = sys.argv[i:]
        results = [ load_results(dir) for dir in folders ]
        if (type == "protocol1" or type == "1"):
            fig_name = "Experiment_injection_protocol1_{}".format(now.strftime("%Y%m%d_%H%M%S"))
            plot_errors_types(results, ("checkstyle_errors_count_ugly", "checkstyle_errors_count_naturalize", "checkstyle_errors_count_naturalize_snipper", "checkstyle_errors_count_codebuff", "checkstyle_errors_count_codebuff_snipper"))
        elif (type == "protocol2" or type == "2"):
            fig_name = "Experiment_injection_protocol2_{}".format(now.strftime("%Y%m%d_%H%M%S"))
            plot_errors_types(results, ("checkstyle_errors_count_ugly",))
        elif (type == "protocol3" or type == "3"):
            fig_name = "Experiment_injection_protocol3_{}".format(now.strftime("%Y%m%d_%H%M%S"))
            plot_errors_types_per_injection_type(results)
        elif (type == "protocol4" or type == "4"):
            plot_repaired_files(results)
        elif (type == "protocol5" or type == "5"):
            plot_diffs(results)
        elif (type == "percentage_of_errors"):
            plot_percentage_of_errors(results)
        if show:
            try:
                plt.show()
            except UnicodeDecodeError:
                print("Bye mac os lover")
        if save:
            plt.savefig("../results/{}.pdf".format(fig_name), format='pdf')
