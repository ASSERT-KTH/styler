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
package ga.rugal.leetcode.palindromepartitioning;

import java.util.ArrayList;
import java.util.List;

/**
 * https://leetcode.com/problems/palindrome-partitioning/
 *
 * @author rugalbernstein
 */
public class Solution {

  private final List<List<String>> result = new ArrayList<>();

  private String text;

  public List<List<String>> partition(final String s) {
    this.text = s;
    this.backtrack(new ArrayList<>(), 0);
    return this.result;
  }

  private boolean isPalindrome(final String s) {
    for (int i = 0; i < s.length() / 2; ++i) {
      if (s.charAt(i) != s.charAt(s.length() - 1 - i)) {
        return false;
      }
    }
    return true;
  }

  private void backtrack(final List<String> temp, final int start) {
    if (start == this.text.length()) {
      this.result.add(new ArrayList<>(temp));
      return;
    }
    for (int i = start; i < this.text.length(); ++i) {
      final String substring = this.text.substring(start, i + 1);
      if (this.isPalindrome(substring)) {
        temp.add(substring);
        this.backtrack(temp, i + 1);
        temp.remove(temp.size() - 1);
      }
    }
  }
}
