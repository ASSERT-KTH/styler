# -*- coding: utf-8 -*-

import glob
from core import *

class Synthetic_Checkstyle_Error:
    def __init__(self, dir):
        self.dir = dir
        self.id = int(dir.split('/')[-1])
        self.type = dir.split('/')[-2]
        self.metadata = None
        self.diff = None
        self.original = None
        self.errored = None
        self.file_name = glob.glob(f'{self.dir}/*.java')[0].split('/')[-1].split('.')[0]
        if 'orig' in self.file_name:
             self.file_name = self.file_name[:-5]

    def get_diff(self):
        if not self.diff:
            self.load_diff()
        return self.diff

    def get_count(self):
        diff = self.get_diff()
        plus = 0
        minus = 0
        for line in diff.split('\n'):
            if line.startswith('> '):
                plus += 1
            if line.startswith('< '):
                minus += 1
        return max(plus, minus)

    def get_metadata(self):
        if not self.metadata:
            self.load_metadata()
        return self.metadata

    def get_original(self):
        if not self.original:
            self.load_original()
        return self.original

    def get_errored(self):
        if not self.errored:
            self.load_errored()
        return self.errored

    def get_diff_path(self):
        return f'{self.dir}/diff.diff'

    def get_errored_path(self):
        return f'{self.dir}/{self.file_name}.java'

    def get_original_path(self):
        return f'{self.dir}/{self.file_name}-orig.java'

    def get_metadata_path(self):
        return f'{self.dir}/metadata.json'

    def load_diff(self):
        self.diff = open_file(self.get_diff_path())

    def load_metadata(self):
        self.metadata = open_json(self.get_metadata_path())

    def load_original(self):
        self.original = open_file(self.get_original_path())

    def load_errored(self):
        self.errored = open_file(self.get_errored_path())
