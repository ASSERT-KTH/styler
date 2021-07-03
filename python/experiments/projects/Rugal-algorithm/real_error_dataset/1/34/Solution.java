/*
 * Copyright 2019 rugalbernstein.
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
package ga.rugal.leetcode.longestpalindrome;

/**
 * https://leetcode.com/problems/longest-palindrome/
 *
 * @author rugalbernstein
 */
public class Solution {

  public int longestPalindrome(final String s) {
    final int length = 'z' - 'A' + 1;
    final int[] map = new int[length];
    for (int i = 0; i < s.length(); ++i) {
      map[s.charAt(i) - 'A']++;
    }
    int result = 0;
    for (final int v : map) {
      result += (v / 2 * 2);
      if (result % 2 == 0 && v % 2 == 1) {
        ++result;
      }
    }
    return result;
  }
}
