/*
 * Copyright 2019 rugal.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ga.rugal.lintcode.amazon.longestpalindromicsubstring;

/**
 * https://www.lintcode.com/problem/longest-palindromic-substring
 *
 * @author rugal
 */
public class Solution {

  public String longestPalindrome(final String s) {
    if (s == null || s.isEmpty()) {
      return "";
    }
    int length = 1;
    String result = s.substring(0, 1);
    for (int i = 0; i < s.length(); ++i) {
      if (this.isPalindrome(s, i - length - 1, i)) {
        result = s.substring(i - length - 1, i + 1);
        length += 2;
        continue;
      }
      if (this.isPalindrome(s, i - length, i)) {
        result = s.substring(i - length, i + 1);
        ++length;
      }
    }
    return result;
  }

  private boolean isPalindrome(final String s, int begin, int end) {
    if (begin < 0) {
      return false;
    }
    while (begin < end) {
      if (s.charAt(begin++) != s.charAt(end--)) {
        return false;
      }
    }
    return true;
  }
}
