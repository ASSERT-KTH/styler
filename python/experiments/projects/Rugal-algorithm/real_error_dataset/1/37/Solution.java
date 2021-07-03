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
package ga.rugal.leetcode.validpalindrome;

/**
 * https://leetcode.com/problems/valid-palindrome/
 *
 * @author rugalbernstein
 */
public class Solution {

  public boolean isPalindrome(String s) {
    s = s.trim();
    if (s.isEmpty()) {
      return true;
    }
    for (int i = 0, j = s.length() - 1; i < s.length() / 2 && i < j;) {
      final char left = s.charAt(i);
      final char right = s.charAt(j);
      if (!Character.isLetterOrDigit(left)) {
        ++i;
        continue;
      }
      if (!Character.isLetterOrDigit(right)) {
        --j;
        continue;
      }
      if (Character.toLowerCase(left) != Character.toLowerCase(right)) {
        return false;
      }
      ++i;
      --j;
    }
    return true;
  }
}
