import unittest

import os
import core
import styler
import tokenizer

class TestSum(unittest.TestCase):
    
    def test_de_tokenize(self):
        file_path = '/home/fernanda/mnt/fernanda/git-styler/styler/python/experiments/projects/matsim-org-matsim-episim-libs/real_error_dataset/1/23/CreateBatteryForCluster.java'
        metadata_path = '/home/fernanda/mnt/fernanda/git-styler/styler/python/experiments/projects/matsim-org-matsim-episim-libs/real_error_dataset/1/23/metadata.json'
        for error in styler.tokenize_errors(file_path, core.open_json(metadata_path)['errors']):
            tokenized_errors, info = error
            translation = '1_SP 0_None'
            de_tokenized_translation = styler.de_tokenize(file_path, info, translation, only_formatting=True)
            print(f'de_tokenized_translation {de_tokenized_translation}')
            actual = '/home/fernanda/mnt/fernanda/CreateBatteryForCluster-actual.java'
            expected = '/home/fernanda/mnt/fernanda/CreateBatteryForCluster-expected.java'
            core.save_file('/home/fernanda/mnt/fernanda/', 'CreateBatteryForCluster-actual.java', de_tokenized_translation)
            assert [row for row in open(actual)] == [row for row in open(expected)]
            break

    def test_de_tokenize0(self):
        file_path = '/home/thomas/mnt/fernanda/styler-test2/python/experiments/projects/Activiti-Activiti/real_error_dataset/1/0/ErrorEventDefinition.java'
        metadata_path = '/home/thomas/mnt/fernanda/styler-test2/python/experiments/projects/Activiti-Activiti/real_error_dataset/1/0/metadata.json'
        for error in styler.tokenize_errors(file_path, core.open_json(metadata_path)['errors']):
            tokenized_errors, info = error
            translation = '2_NL_SP 1_SP 0_None 0_None 0_None 0_None 0_None 0_None 0_None 2_NL_SP 1_SP 1_SP'
            de_tokenized_translation = styler.de_tokenize(file_path, info, translation, only_formatting=True)
            print(f'de_tokenized_translation {de_tokenized_translation}')
            actual = '/home/thomas/mnt/fernanda/styler-test2/python/experiments/projects/Activiti-Activiti/test/ErrorEventDefinition-actual.java'
            expected = '/home/thomas/mnt/fernanda/styler-test2/python/experiments/projects/Activiti-Activiti/test/ErrorEventDefinition-expected.java'
            core.save_file('/home/thomas/mnt/fernanda/styler-test2/python/experiments/projects/Activiti-Activiti/test', 'ErrorEventDefinition-actual.java', de_tokenized_translation)
            assert [row for row in open(actual)] == [row for row in open(expected)]
            break

    def test_de_tokenize10(self):
        file_path = '/home/thomas/mnt/fernanda/styler-test2/python/experiments/projects/Activiti-Activiti/real_error_dataset/1/10/Deployment.java'
        metadata_path = '/home/thomas/mnt/fernanda/styler-test2/python/experiments/projects/Activiti-Activiti/real_error_dataset/1/10/metadata.json'
        for error in styler.tokenize_errors(file_path, core.open_json(metadata_path)['errors']):
            tokenized_errors, info = error
            translation = '2_NL_SP 1_SP 0_None 0_None 0_None 2_NL_4_DD_SP 1_NL'
            de_tokenized_translation = styler.de_tokenize(file_path, info, translation, only_formatting=True)
            print(f'de_tokenized_translation {de_tokenized_translation}')
            actual = '/home/thomas/mnt/fernanda/styler-test2/python/experiments/projects/Activiti-Activiti/test/Deployment-actual.java'
            expected = '/home/thomas/mnt/fernanda/styler-test2/python/experiments/projects/Activiti-Activiti/test/Deployment-expected.java'
            core.save_file('/home/thomas/mnt/fernanda/styler-test2/python/experiments/projects/Activiti-Activiti/test', 'Deployment-actual.java', de_tokenized_translation)
            assert [row for row in open(actual)] == [row for row in open(expected)]
            break

    def test_de_tokenize11(self):
        file_path = '/home/thomas/mnt/fernanda/styler-test2/python/experiments/projects/Activiti-Activiti/real_error_dataset/1/11/ProcessVariablesMapDeserializer.java'
        metadata_path = '/home/thomas/mnt/fernanda/styler-test2/python/experiments/projects/Activiti-Activiti/real_error_dataset/1/11/metadata.json'
        for error in styler.tokenize_errors(file_path, core.open_json(metadata_path)['errors']):
            tokenized_errors, info = error
            translation = '2_NL_SP 1_SP'
            de_tokenized_translation = styler.de_tokenize(file_path, info, translation, only_formatting=True)
            print(f'de_tokenized_translation {de_tokenized_translation}')
            actual = '/home/thomas/mnt/fernanda/styler-test2/python/experiments/projects/Activiti-Activiti/test/ProcessVariablesMapDeserializer-actual.java'
            expected = '/home/thomas/mnt/fernanda/styler-test2/python/experiments/projects/Activiti-Activiti/test/ProcessVariablesMapDeserializer-expected.java'
            core.save_file('/home/thomas/mnt/fernanda/styler-test2/python/experiments/projects/Activiti-Activiti/test', 'ProcessVariablesMapDeserializer-actual.java', de_tokenized_translation)
            assert [row for row in open(actual)] == [row for row in open(expected)]
            break

    def test_de_tokenizeXXXX(self):
        file_path = '/home/thomas/mnt/fernanda/styler-test2/python/experiments/results/Angel-ML-angel/errored/1/475/SparseDoubleMatrix.java'
        assert core.is_crlf(file_path)
        repaired_file_path = '/home/thomas/mnt/fernanda/styler-test2/python/experiments/results/Angel-ML-angel/styler/475/SparseDoubleMatrix.java'
        
        de_tokenized_translation = core.open_file(repaired_file_path).replace('\n','\r\n')
        core.save_file_in_path(repaired_file_path, de_tokenized_translation)

        diff, diff_size = core.compute_diff(file_path, repaired_file_path)
        print(f'diff_size {diff_size}')
        assert diff_size == 1

    def test_tokenize_LeftCurly(self):
        path = '/home/fernanda/mnt/fernanda/styler-test2/python/experiments/projects/atlanmod-NeoEMF/real_error_dataset/1/18/MongoDbBackendFactory.java'
        error = core.open_json(os.path.join(os.path.dirname(path), 'metadata.json'))
        error = error['errors'][0]
        error['type'] = core.checkstyle_source_to_error_type(error['source'])
        tokens_errored, info = tokenizer.tokenize_file_to_repair(path, error)
        expected = ['Identifier', '1_NL_SP', 'Annotation', '0_None', 'Identifier', '1_NL_SP', 'protected', '1_SP', 'Identifier', '1_SP', 'Identifier', '0_None', '(', '0_None', 'Identifier', '1_SP', 'Identifier', '0_None', ',', '1_SP', 'Identifier', '1_SP', 'Identifier', '0_None', ')', '1_SP', 'throws', '1_SP', '<LeftCurly>', 'Identifier', '1_NL_SP', '{', '1_NL_4_ID_SP', '</LeftCurly>', 'final', '1_SP', 'boolean', '1_SP', 'Identifier', '1_SP', '=', '1_SP', 'Identifier', '0_None', '.', '0_None', 'Identifier', '0_None', '(', '0_None', ')', '0_None', ';', '2_NL_SP']
        assert tokens_errored == expected

    def test_tokenize_EmptyLineSeparator(self):
        path = '/home/fernanda/mnt/fernanda/styler-test2/python/experiments/projects/Activiti-Activiti/real_error_dataset/1/0/ErrorEventDefinition.java'
        error = core.open_json(os.path.join(os.path.dirname(path), 'metadata.json'))
        error = error['errors'][0]
        error['type'] = core.checkstyle_source_to_error_type(error['source'])
        tokens_errored, info = tokenizer.tokenize_file_to_repair(path, error)
        expected = ['<EmptyLineSeparator>', 'Comment', '1_NL_SP', 'package', '1_SP', 'Identifier', '0_None', '.', '0_None', 'Identifier', '0_None', '.', '0_None', 'Identifier', '0_None', '.', '0_None', 'Identifier', '0_None', ';', '2_NL_SP', 'public', '1_SP', '</EmptyLineSeparator>', 'class', '1_SP', 'Identifier', '1_SP', 'extends', '1_SP', 'Identifier', '1_SP', '{', '2_NL_2_ID_SP']
        assert tokens_errored == expected

    def test_tokenize_FileTabCharacter(self):
        path = '/home/thomas/mnt/fernanda/styler-test2/python/experiments/projects/actiontech-txle/real_error_dataset/1/0/AutoCompensable.java'
        error = core.open_json(os.path.join(os.path.dirname(path), 'metadata.json'))
        error = error['errors'][0]
        error['type'] = core.checkstyle_source_to_error_type(error['source'])
        tokens_errored, info = tokenizer.tokenize_file_to_repair(path, error)
        expected = [')', '1_NL_SP', 'public', '1_SP', 'Annotation', '0_None', 'interface', '1_SP', 'Identifier', '1_SP', '<FileTabCharacter>', '{', '2_NL_1_ID_TB', 'int', '1_SP', '</FileTabCharacter>', 'Identifier', '0_None', '(', '0_None', ')', '1_SP', 'default', '1_SP', 'DecimalInteger', '0_None', ';', '2_NL_TB', 'int', '1_SP', 'Identifier', '0_None', '(', '0_None', ')', '1_SP', 'default', '1_SP', 'DecimalInteger', '0_None', ';', '2_NL_TB']
        assert tokens_errored == expected

    def test_tokenize_WhitespaceAround(self):
        path = '/home/thomas/mnt/fernanda/styler-test2/python/experiments/projects/Activiti-Activiti/real_error_dataset/1/11/ProcessVariablesMapDeserializer.java'
        error = core.open_json(os.path.join(os.path.dirname(path), 'metadata.json'))
        error = error['errors'][0]
        error['type'] = core.checkstyle_source_to_error_type(error['source'])
        tokens_errored, info = tokenizer.tokenize_file_to_repair(path, error)
        print(tokens_errored)
        print(info)
        expected = [';', '1_NL_SP', 'Identifier', '1_SP', 'Identifier', '1_SP', '=', '1_SP', 'Identifier', '0_None', '.', '0_None', 'Identifier', '0_None', '(', '0_None', ')', '0_None', '<WhitespaceAround>', ';', '2_NL_SP', 'if', '0_None', '</WhitespaceAround>', '(', '0_None', '!', '0_None', 'Identifier', '0_None', '.', '0_None', 'Identifier', '0_None', '(', '0_None', ')', '0_None', ')', '1_SP', '{', '1_NL_4_ID_SP', 'Identifier', '1_SP', 'Identifier', '1_SP', '=', '1_SP', 'Identifier', '0_None', '.', '0_None', 'Identifier', '0_None', '(', '0_None', 'String', '0_None', ')', '0_None', '.', '0_None', 'Identifier', '0_None', '(', '0_None', ')', '0_None', ';', '1_NL_SP', 'Identifier', '1_SP', 'Identifier', '1_SP', '=', '1_SP', 'Identifier', '0_None', '.', '0_None', 'Identifier', '0_None', '(', '0_None', 'String', '0_None', ')', '0_None', '.', '0_None', 'Identifier', '0_None', '(', '0_None', ')', '0_None', ';', '2_NL_SP']
        assert tokens_errored == expected


if __name__ == '__main__':
    unittest.main()
