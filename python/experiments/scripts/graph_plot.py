# -*- coding: utf-8 -*-

import sys
from os import path
sys.path.append(path.dirname(path.dirname(path.dirname(path.abspath(__file__)))))
from core import *

from experiments import *

from itertools import product
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
import seaborn as sns
from matplotlib import pyplot as plt
from matplotlib_venn import venn2, venn2_circles

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
    plt.subplots(figsize=(5,4))
    v = venn2(data.values(), labels, (styler_color, intellij_color))
    
    plt.subplots_adjust(bottom=0.05, left=0.01, right=0.99, top=0.99)
    plt.savefig(f'{get_experiment_dir()}/repair_venn.pdf', format='pdf')
    plt.savefig(f'{get_experiment_dir()}/repair_venn.png', format='png')

def hex_to_rgb(hex_color):
    hex = hex_color.lstrip('#')
    return tuple(int(hex[i:i+2], 16)/255. for i in (0, 2 ,4))

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
        pc.set_facecolor('#000000')
        pc.set_alpha(0.5)
    medianprops = dict(linestyle='-.', linewidth=3.5, color='#000000')
    axes.boxplot(dict_to_list(data, order), vert=False, whis=[5, 95], positions=range(len(data)), labels=['CodeBuff', 'Naturalize', 'Checkstyle-IDEA', 'Styler'], medianprops=medianprops)

    labels = {
        'codebuff': 'CodeBuff',
        'naturalize': 'Naturalize',
        'styler': 'Styler',
        'intellij': 'Checkstyle-IDEA',
    }

    plt.yticks(range(len(order)), ['CodeBuff', 'Naturalize', 'Checkstyle-IDEA', 'Styler'], fontsize=12)
    plt.xlabel(plot_data.get('x_label', ''), fontsize=12)
    plt.ylabel(plot_data.get('y_label', ''), fontsize=12)
    plt.xlim(0,200)
    plt.subplots_adjust(bottom=0.13, left=0.21, right=0.98, top=0.99)
    plt.savefig(f'{get_experiment_dir()}/repair_diffs.pdf', format='pdf')
    plt.savefig(f'{get_experiment_dir()}/repair_diffs.png', format='png')

def repair_heatmap(data):
    sns.set_context("paper", font_scale=1)                                                  
    fig, axes = plt.subplots(figsize=(6.2,7))
    color_map = plt.cm.get_cmap('Greys') #https://matplotlib.org/3.3.1/tutorials/colors/colormaps.html
    reversed_color_map = color_map.reversed()
    ax = sns.heatmap(data, annot=True, fmt=".1f", cbar=False, linewidths=.5, cmap=reversed_color_map)
    for t in ax.texts: t.set_text(t.get_text() + " %")
    plt.subplots_adjust(bottom=0.05, left=0.30, right=0.99, top=0.99)
    plt.savefig(f'{get_experiment_dir()}/repair_heatmap.pdf', format='pdf')
    plt.savefig(f'{get_experiment_dir()}/repair_heatmap.png', format='png')
