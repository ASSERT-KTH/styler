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
package ga.rugal.amazon.oneeditdistance;

/**
 * https://leetcode.com/problems/one-edit-distance/
 *
 * @author rugalbernstein
 */
public class Solution {

  public boolean isOneEditDistance(final String s, final String t) {
    final int ns = s.length();
    final int nt = t.length();

    // Ensure that s is shorter than t.
    if (ns > nt) {
      return this.isOneEditDistance(t, s);
    }

    // The strings are NOT one edit away distance
    // if the length diff is more than 1.
    if (nt - ns > 1) {
      return false;
    }

    for (int i = 0; i < ns; i++) {
      // if there is a diff
      if (s.charAt(i) != t.charAt(i)) {
        return ns == nt
               // we need to see if the rest of both string all matched
               ? s.substring(i + 1).equals(t.substring(i + 1))
               // need to see if small string matches rest of large string
               : s.substring(i).equals(t.substring(i + 1));
      }
    }

    // If there is no diffs on ns distance the strings are one edit away
    // only if t has one more character
    return ns + 1 == nt;
  }
}
