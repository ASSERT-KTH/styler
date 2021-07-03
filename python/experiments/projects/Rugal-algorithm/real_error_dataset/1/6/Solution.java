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
package ga.rugal.leetcode.palindromepartitioningii;

/**
 * https://leetcode.com/problems/palindrome-partitioning-ii/
 *
 * @author rugal
 */
public class Solution {

  public int minCut(final String s) {
    final char[] c = s.toCharArray();
    final int[] cut = new int[c.length];
    //means string [x, y] is palindrome or not
    final boolean[][] pal = new boolean[c.length][c.length];

    for (int i = 0; i < c.length; i++) {
      //initial minimum cut, cut at every character
      int min = i;
      for (int j = 0; j <= i; j++) {
        //since [j+1, i-1] is palindrome already, and [i] == [j]
        //so [j, i] is palindrome as well
        if (c[j] == c[i]//if [j] == [i] is same
            && (j + 1 > i - 1 || pal[j + 1][i - 1])) { // and [j+1, i-1] is palindrome as well
          //means [j, i] is palidrome
          pal[j][i] = true;
          min = j == 0 ? 0 : Math.min(min, cut[j - 1] + 1);
        }
      }
      cut[i] = min;
    }
    return cut[c.length - 1];
  }
}
