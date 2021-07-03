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
package ga.rugal.leetcode.implementstrstr;

/**
 * https://leetcode.com/problems/implement-strstr/
 *
 * @author Rugal Bernstein
 */
public class Solution {

  /**
   * Each element indicates the longest prefix length ends at that index<BR>
   * e.g., result[5] = 2 means, ends at index 5, the longest matched prefix length is 2<BR>
   *
   * @param pattern
   *
   * @return
   */
  public int[] buildNextTable(final String pattern) {
    final int[] result = new int[pattern.length()];
    //slow is the length of matched prefix
    //slow is the matched prefix index
    for (int slow = 0, fast = 1; fast < pattern.length(); ++fast) {
      while (slow > 0 && pattern.charAt(slow) != pattern.charAt(fast)) {
        //if match breaks, go back to the previous character to try to do another match
        //so we don't have to match everything from index 0
        slow = result[slow - 1];
      }
      if (pattern.charAt(slow) == pattern.charAt(fast)) {
        result[fast] = ++slow;
      }
    }
    return result;
  }

  public int strStr(final String source, final String pattern) {
    if (pattern.isEmpty()) {
      return 0;
    }
    if (source.length() < pattern.length()) {
      return -1;
    }

    final int[] next = this.buildNextTable(pattern);

    int s = 0; // index for txt[]
    for (int p = 0; s < source.length();) {
      if (pattern.charAt(p) == source.charAt(s)) {
        p++;
        s++;
      }
      //now we matched the whole pattern string
      if (p == pattern.length()) {
        return s - p;
        //to find all matched prefix
        //p = next[p - 1];
      }
      // mismatch after p matches
      if (s < source.length() && pattern.charAt(p) != source.charAt(s)) {
        //mismatch
        if (p != 0) {
          //then try to match previous character if there is any
          p = next[p - 1];
        } else {
          //there no match at all, we have to move forward
          s = s + 1;
        }
      }
    }
    return -1;
  }
}
