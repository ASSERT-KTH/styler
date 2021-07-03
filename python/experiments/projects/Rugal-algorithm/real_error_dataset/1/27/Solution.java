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
package ga.rugal.leetcode.tolowercase;

/**
 * https://leetcode.com/problems/to-lower-case/
 *
 * @author rugal
 */
public class Solution {

  public String toLowerCase(final String str) {
    final char[] myStr = str.toCharArray();
    for (int i = 0; i < str.length(); i++) {
      if (str.charAt(i) > 64 && str.charAt(i) < 92) {
        myStr[i] += 32;
      }
    }
    return String.valueOf(myStr);
  }
}
