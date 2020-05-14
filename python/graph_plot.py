# -*- coding: utf-8 -*-

from core import *
from functools import reduce
import random
import colorsys
import sys
import os
import json
import datetime
from itertools import product

import numpy as np

import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
import seaborn as sns
from matplotlib import pyplot as plt
from matplotlib_venn import venn3, venn3_circles


def protocol6(results, repair_tools, disposition=110):
    fig = plt.figure(figsize=(15, 12))

    axs = []
    for tool, index in zip(repair_tools, range(len(repair_tools))):
        ax = fig.add_subplot(disposition + index + 1)
        axs.append(ax)
        protocol6_subplot(results, tool, ax, not index)

    return fig


def mix_colors(*colors):
    color_avg = [0] * 3
    colors_len = len(colors)
    for color in colors:
        rgb = hex_to_rgb(color)
        for i in range(len(rgb)):
            color_avg[i] += rgb[i] / colors_len
    print(color_avg)
    return color_avg


def gen_get_colors_venn(labels, colors):
    def get_colors_venn(group_id):
        result = []
        for index, value in enumerate(group_id):
            if value == '1':
                result.append(colors[labels[index]])
        return tuple(result)
    return get_colors_venn


def venn(data):
    labels = tuple(data.keys())
    v = venn3(data.values(), labels)
    colors = {
        'Codebuff': codebuff_color,
        'Naturalize': naturalize_color,
        'Styler': styler_color
    }
    get_colors_venn = gen_get_colors_venn(labels, colors)
    alpha = 2/3
    for id in [''.join(elements) for elements in product('01', repeat=3)]:
        if id != '000':
            v.get_patch_by_id(id).set_color(mix_colors(*get_colors_venn(id)))
            v.get_patch_by_id(id).set_alpha(alpha)

    plt.show()


def protocol6_subplot(results, repair_tool, ax, y_axis=True):
    error_type_repair = dict()
    for result in results:
        file_with_cs_errors = result["file_with_cs_errors_{}".format(repair_tool)]
        for file, liste in result["file_with_cs_errors_ugly"].items():
            for modification in liste:
                errors = list(set([error["source"].split(".")[-1] for error in modification["errors"]]))
                errors_after_repair = []
                if file in file_with_cs_errors:
                    modification_repaired = list(filter(lambda x: x["type"] == modification["type"] and x["modification_id"] == modification["modification_id"], file_with_cs_errors[file]))
                    if len(modification_repaired):
                        if len( modification_repaired[0]["errors"]):
                            errors_after_repair = list(set([error["source"].split(".")[-1] for error in modification_repaired[0]["errors"]]))
                        else:
                            errors_after_repair = errors

                for error in errors:
                    if error not in error_type_repair:
                        error_type_repair[error] = {"repaired": 0, "errors_remaining": 0, "not_repaired": 0}
                    if error in errors_after_repair:
                        error_type_repair[error]["not_repaired"] += 1
                    else:
                        if len(errors_after_repair):
                            error_type_repair[error]["errors_remaining"] += 1
                        else:
                            error_type_repair[error]["repaired"] += 1
    def f(x):
        s = sum(x.values())
        obj = {key: (value / s) for key, value in x.items()}
        obj["sum"] = s
        return obj
    error_type_repair = {key[:-5]:f(value) for key, value in error_type_repair.items()}

    objects = error_type_repair.keys()
    y_pos = np.arange(len(objects))
    types = ("repaired", "errors_remaining", "not_repaired")
    colors = {"repaired": "#2ecc71", "errors_remaining": "#f39c12", "not_repaired": "#e74c3c"}
    sum_left = [0] * len(error_type_repair)
    for type in types:
        performance = [item[type] for item in error_type_repair.values()]
        ax.barh(y_pos, performance, align='center', color=colors[type], left=sum_left, label=type)
        sum_left = list(map(lambda x, y: x+y, sum_left, performance))

    def with_percentage(error_type_repair, y_pos):
        for error, pos in zip(error_type_repair.values(), y_pos):
            value = error["sum"]
            plt.text(1.02, pos - 0.2, '%d' % int(value), ha='center', va='bottom')
    with_percentage(error_type_repair, y_pos)
    if y_axis:
        plt.yticks(y_pos, objects, rotation=0)
    else:
        plt.yticks(y_pos, [""]*len(objects), rotation=0)
    plt.xlabel('')
    plt.title('Percentage of repaired checkstyle errors by {}.'.format(repair_tool))
    plt.legend()


def plot_repaired_files(results):

    counts = ('naturalize', 'codebuff', 'both_sniper')

    barWidth = 1. / (len(counts) + 1)
    bars = [[] for i in range(len(counts)) ]

    labels = []

    for result in results:
        # labels.append( exp.corpus.name + "(" + str(exp.corpus.get_number_of_files()) + " files)" )
        with_errors = result["number_of_injections"] * result["corrupted_files_ratio_ugly"]
        labels.append("{}, \n /{} injections".format(result["name"], int(with_errors)))
        for count, i in zip(counts, range(len(counts))):
            if count == "both_sniper":
                file_with_cs_errors_codebuff_sniper = result["file_with_cs_errors_codebuff_sniper"]
                file_with_cs_errors_naturalize_sniper = result["file_with_cs_errors_naturalize_sniper"]
                c = 0
                for file, modifications in file_with_cs_errors_codebuff_sniper.items():
                    if file in file_with_cs_errors_naturalize_sniper:
                        errors = [m["type"]+str(m["modification_id"]) for m in file_with_cs_errors_naturalize_sniper[file]]
                        for modification in modifications:
                            if modification["type"]+str(modification["modification_id"]) in errors:
                                c += 1
                bars[i].append( 1 - c / with_errors )
            else:
                prop = result["corrupted_files_ratio_" + count]
                bars[i].append( 1 - ( result["number_of_injections"] * result["corrupted_files_ratio_" + count]) / with_errors )
            # bars[i].append( result["corrupted_files_ratio_" + count] )

    # Set position of bar on X axis
    r = []
    r.append(np.arange(len(labels)))
    for i in range(1,len(counts)):
        r.append([x + barWidth for x in r[i-1]])



    def with_percentage(bars):
        for bar in bars:
            height = bar.get_height()
            plt.text(bar.get_x() + bar.get_width()/2., 1*height, '%d' % int(height*100) + "%", ha='center', va='bottom')
    # Make the plot
    for count, i in zip(counts, range(len(counts))):
        with_percentage(plt.bar(r[i], bars[i], width=barWidth, edgecolor='white', label=count))

    modifications = (2,2,2,2,2)

    # Add xticks on the middle of the group bars
    plt.xlabel('Proportion of files with errors (m=' + str(modifications) + ')', fontweight='bold')
    plt.xticks([r + barWidth * (len(counts)-1) / 2 for r in range(len(results))], labels, rotation=45, fontsize=8)
    plt.subplots_adjust(bottom=0.20)
    # Create legend & Show graphic
    plt.legend()


def avg(array_list):
    return sum(array_list)/len(array_list)


def hex_to_rgb(hex_color):
    hex = hex_color.lstrip('#')
    return tuple(int(hex[i:i+2], 16)/255. for i in (0, 2 ,4))


def n_bar_plot(plot_data):
    counts = plot_data['labels']
    colors = plot_data['colors']

    plot_average = True

    horizontal = True

    barWidth = 1. / (len(counts) + 1)
    bars = [ # Transpose
        [ line[collumn] for line in plot_data['data'].values() ]
        for collumn in range(len(plot_data['labels']))
    ]

    labels = list(plot_data['data'].keys())

    if plot_average:
        print(bars)
        bars = [
            numbers + [avg(numbers)]
            for numbers in bars
        ]
        labels += ['Average']
        print(labels)


    r = []
    r.append(list(np.arange(len(labels))))
    if plot_average:
        r[0][-1] += 0.5
    for i in range(1,len(counts)):
        r.append([x + barWidth for x in r[i-1]])

    def with_percentage(bars, bar_color='#FFFFFF'):
        if horizontal:
            for bar in bars:
                width = bar.get_width()
                ha = 'right' if width > 0.2 else 'left'
                color = '#000000' if ha == 'left' or colorsys.rgb_to_hls(*hex_to_rgb(bar_color))[1] > 0.45 else '#FFFFFF'
                plt.text(1*width, bar.get_y() + bar.get_height()*1.25/2. , f'{(width*100):.1f}%', ha=ha, va='center', fontsize=12, color=color)
        else:
            for bar in bars:
                height = bar.get_height()
                plt.text(bar.get_x() + bar.get_width()/2., 1*height, f'{(height*100):.1f}%', ha='center', va='bottom')
    # Make the plot
    for i, count in enumerate(counts):
        if horizontal:
            with_percentage(plt.barh(r[i], bars[i], height=barWidth * 0.95, edgecolor='white', label=count, color=colors[count]), bar_color=colors[count])
        else:
            with_percentage(plt.bar(r[i], bars[i], width=barWidth * 0.95, edgecolor='white', label=count, color=colors[count]))

    # Add xticks on the middle of the group bars
    plt.xlabel(plot_data.get('x_label', ''), fontsize=15)
    plt.ylabel(plot_data.get('y_label', ''), fontsize=15)
    if horizontal:
        plt.yticks([r + barWidth * (len(counts)-1) / 2 for r in r[0]], labels, fontsize=15)
    else:
        plt.xticks([r + barWidth * (len(counts)-1) / 2 for r in r[0]], labels, rotation=45, fontsize=8)
    plt.subplots_adjust(bottom=0.05, left=0.17, right=0.99, top=0.99)

    if horizontal:
        plt.gca().invert_yaxis()

    # Create legend & Show graphic
    plt.legend()
    plt.show()


def plot_diffs(results):
    modifications = (2,2,2,2,2)

    counts = ('naturalize', 'naturalize_sniper', 'codebuff', 'codebuff_sniper')

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
    modifications = (5, 5)

    barWidth = 0.25
    bars1 = []
    naturalize_res = []
    codebuff_res = []
    labels = []

    for result in results:
        # labels.append( exp.corpus.name + "(" + str(exp.corpus.get_number_of_files()) + " files)" )
        labels.append(result["name"])
        bars1.append(result["corrupted_files_ratio_ugly"] )
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
    modifications = (2, 2, 2, 2, 2)

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
    if n_errors_labels > 1:
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
    for i in range(1, len(counts)):
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

def cumulatives_bars(plot_data):

    errors_labels = set()
    labels = tuple(plot_data['data'].keys())

    compute_avg = plot_data.get('avg', True)

    label_sum = {}

    for count in plot_data['data'].values():
        errors_labels = errors_labels | set(count.keys())

    data = plot_data['data']

    layers = {}

    for label in errors_labels:
        layers[label] = []
        label_sum[label] = 0
        for name in labels:
            layers[label].append(data[name].get(label,0))
            label_sum[label] += data[name].get(label,0)

    total_sum = sum(label_sum.values())
    avg = {label: (value/total_sum*100.) for label, value in label_sum.items()}
    print(avg)

    n_errors_labels = len(errors_labels)
    colors = []
    if ( n_errors_labels > 1):
        for i in range( 0, n_errors_labels ):
            colors.append('#%02x%02x%02x' % tuple(map(lambda x: int( x*256 ), colorsys.hls_to_rgb( 1 / (n_errors_labels-1) * i * 0.9 , random.uniform(0.3, 0.7), random.uniform(0.3, 0.7)))))
        random.shuffle(colors)
    else :
        colors.append('#ff00ff')

    lables_colors = dict()
    for i, error_label in enumerate(errors_labels):
        lables_colors[error_label] = colors[i]

    bar_width = 0.5
    # Set position of bar on X axis
    r = list()
    r.append(np.arange(len(data.keys())))
    # for i in range(1,len(counts)):
    #     r.append([x + bar_width for x in r[i-1]])


    def add_layers_to_the_graph(layers, position):
        sum = [0] * len(labels)
        for key, values in layers.items():
            plt.bar(position, values, width=bar_width, color=lables_colors[key], bottom=sum, edgecolor='white')
            sum = list(map( lambda x, y: x + y, sum, values))
        return sum
    # plt.bar(r2, naturalize_res, color='#f1c40f', width=bar_width, edgecolor='white', label='Naturalize')
    # plt.bar(r3, codebuff_res, color='#1abc9c', width=bar_width, edgecolor='white', label='Codebuff')
    i = 0
    sums = []
    sums.append(add_layers_to_the_graph(layers, r[0]))

    # Add xticks on the middle of the group bars
    plt.xlabel(plot_data['title'], fontweight='bold')
    plt.xticks( range(len(labels)), labels, rotation=45, fontsize=8)
    plt.subplots_adjust(top=0.75)
    plt.subplots_adjust(bottom=0.20)
    # Create legend & Show graphic
    patches = [ mpatches.Patch(color=c, label=f'{l} ({avg[l]:.2f}%)') for l, c in lables_colors.items()]
    plt.legend(handles = patches, loc='upper center', ncol=3, fancybox=True, bbox_to_anchor=(0.5, 1.4))
    plt.show()


def dict_to_list(dict, order):
    return [dict[key] for key in order]


def violin_plot(plot_data):
    data = plot_data['data']
    colors = plot_data['colors']
    order = tuple(colors.keys())
    print(order)

    fig, axes = plt.subplots(figsize=(7,4))

    parts = axes.violinplot([list(filter(lambda a: a<150, points)) for points in dict_to_list(data, order)], range(len(data)), points=1000, vert=False, widths=0.7,
                          showmeans=False, showextrema=False, showmedians=False,
                          bw_method='silverman')
    for pc, label in zip(parts['bodies'], order) :
        # print(pc)
        pc.set_facecolor(colors[label])
        pc.set_alpha(0.8)
    medianprops = dict(linestyle='-.', linewidth=3.5, color='#000000')
    axes.boxplot(dict_to_list(data, order), whis=[5, 95], positions=range(len(data)), vert=False, medianprops=medianprops)

    labels = {
        'codebuff': 'CodeBuff',
        'naturalize': 'Naturalize',
        'styler': 'Styler',
        'intellij': 'Checkstyle-IDEA',
    }

    patches = [ mpatches.Patch(color=c, label=labels[l]) for l, c in list(colors.items())[::-1]]
    plt.legend(handles = patches, loc='upper right', ncol=2, fancybox=True, fontsize=15)
    # plt.yticks( range(len(order)), order, fontsize=15)
    plt.yticks( [1], ('',), fontsize=15)
    plt.xlabel(plot_data.get('x_label', ''), fontsize=15)
    plt.ylabel(plot_data.get('y_label', ''), fontsize=15)
    plt.xlim(0,100)
    plt.subplots_adjust(bottom=0.15, left=0.01, right=0.99, top=0.99)
    plt.savefig('../results/Figure_RQ_3_real.pdf', format='pdf')

def boxplot(plot_data):
    labels = list(plot_data['data'].keys())
    sub_labels = plot_data['sub_labels']
    vert = plot_data.get('vert', False)
    colors = plot_data['colors']

    boxWidth = 1. / (len(sub_labels) + 1)

    show_all = plot_data.get('show_all', True)

    if show_all:
        labels.append('all')

    r = []
    r.append(list(np.arange(len(labels))))
    if show_all:
        r[0][-1] += 0.5

    for i in range(1,len(sub_labels)):
        r.append([x + boxWidth for x in r[i-1]])
    r = sorted(reduce(list.__add__,r))

    data = []
    for data_list in plot_data['data'].values():
        for label in sub_labels:
            data += [data_list[label]]
    print(len(data[0]))
    if show_all:
        all = [ reduce(list.__add__, data[i::len(sub_labels)]) for i in range(len(sub_labels)) ]
        print(all)
        data += all

    fig7, ax7 = plt.subplots()
    medianprops = dict(linestyle='-.', linewidth=3.5, color='#000000')
    bplot = ax7.boxplot(data, whis=[5, 95], positions=r, widths=boxWidth*0.8, vert=vert, patch_artist=True, labels=sub_labels*len(labels), medianprops=medianprops)
    for patch, color in zip(bplot['boxes'], colors  * len(labels)):
        patch.set_facecolor(color)
    patches = [ mpatches.Patch(color=c, label=l) for l, c in zip(sub_labels, colors)]
    plt.legend(handles = patches, loc='upper right', ncol=3, fancybox=True, fontsize=15)
    if vert:
        plt.xticks([pos + boxWidth * (len(sub_labels)-1) / 2 for pos in r[::len(sub_labels)]], labels, rotation=45, fontsize=15)
    else:
        plt.yticks([pos + boxWidth * (len(sub_labels)-1) / 2 for pos in r[::len(sub_labels)]], labels, fontsize=15)
    plt.xlabel(plot_data.get('x_label', ''), fontsize=15)
    plt.ylabel(plot_data.get('y_label', ''), fontsize=15)
    plt.xlim(0,40)
    plt.subplots_adjust(bottom=0.06, left=0.11, right=0.95, top=0.95)
    plt.gca().invert_yaxis()
    plt.show()


def plot_errors_types_per_injection_type(results):
    modifications = (2, 2, 2, 2, 2)

    counts = ("insertions-newline", "insertions-space", "insertions-tab", "deletions-newline", "deletions-space")

    labels = []
    errors_labels = set()

    for result in results:
        # labels.append( exp.corpus.name + "(" + str(exp.corpus.get_number_of_files()) + " files)" )
        labels.append("{} ({})".format(result["name"], result["number_of_injections"]))
        errors_labels = errors_labels | result["checkstyle_errors_count_ugly"].keys()

    n_errors_labels = len(errors_labels)
    colors = []
    if n_errors_labels > 1:
        for i in range(0, n_errors_labels):
            colors.append('#%02x%02x%02x' % tuple(map(lambda x: int( x*256 ), colorsys.hls_to_rgb( 1 / (n_errors_labels-1) * i * 0.9, random.uniform(0.4, 0.6), random.uniform(0.4, 0.6)))))
        random.shuffle(colors)
    else:
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


def repair_heatmap(data):
    sns.set_context("paper", font_scale=1.2)                                                  
    fig, axes = plt.subplots(figsize=(7,7))
    ax = sns.heatmap(data, annot=True, fmt=".1f", cbar=False, linewidths=.5)#, cmap='RdYlGn')
    for t in ax.texts: t.set_text(t.get_text() + " %")
    plt.subplots_adjust(bottom=0.05, left=0.30, right=0.99, top=0.99)
    plt.savefig('../results/repair_heatmap.pdf', format='pdf')

def repair_cluster(data):
    sns.set_context("paper", font_scale=1)                                                  
    fig, axes = plt.subplots(figsize=(7,7))
    g = sns.clustermap(data, annot=True, fmt=".1f", cbar=False, linewidths=.5)
    for t in g.ax_heatmap.texts: t.set_text(t.get_text() + " %")
    plt.subplots_adjust(right=0.75)
    plt.savefig('../results/repair_cluster.pdf', format='pdf')

def dist_from_modification(results):
    distances = []
    for result in results:
        for id, modifications in result["file_with_cs_errors_ugly"].items():
            for modification in modifications:
                errors_pos = list(set([(int(error["line"]), int(error.get("column", 0))) for error in modification["errors"]]))
                modifications_pos = result["modifications"][id][modification["type"]][modification["modification_id"]]
                if (errors_pos[0][0] != modifications_pos[0][0] or errors_pos[0][1] != modifications_pos[0][1] ):
                    print(errors_pos, modifications_pos)
                    distances.append(1)
                else:
                    distances.append(0)
    print(sum(distances)/len(distances))


def load_results(dir):
    data = {}
    with open(os.path.join(dir, "results.json")) as f:
        data = json.load(f)
    return data
