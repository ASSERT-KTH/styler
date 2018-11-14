import unittest

import java_lang_utils as jlu

class TestJavaLangUtils(unittest.TestCase):

    def test_tokenize_with_white_space(self):
        whitespace, tokens = jlu.tokenize_with_white_space(jlu.open_file("./investigations/TypeHandler.java"))

        self.assertGreater(len(whitespace), 0)
        self.assertEqual(len(whitespace), len(tokens))

    def test_reformat(self):
        original_content = jlu.open_file("./investigations/TypeHandler.java")
        whitespace, tokens = jlu.tokenize_with_white_space(original_content)
        rewritten = jlu.reformat(whitespace, tokens)

        self.assertEqual(original_content, rewritten)


if __name__ == '__main__':
    unittest.main()
