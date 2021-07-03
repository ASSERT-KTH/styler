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
package ga.rugal.leetcode.romantointeger;

import java.util.HashMap;
import java.util.Map;

/**
 * https://leetcode.com/problems/roman-to-integer/
 *
 * @author rugal
 */
public class Solution {

  private final Map<Character, Integer> map = new HashMap<>();

  public Solution() {
    this.map.put('I', 1);
    this.map.put('V', 5);
    this.map.put('X', 10);
    this.map.put('L', 50);
    this.map.put('C', 100);
    this.map.put('D', 500);
    this.map.put('M', 1000);
  }

  public int romanToInt(final String s) {
    int result = 0;
    for (int i = 0; i < s.length(); ++i) {
      final char c = s.charAt(i);
      result += this.map.get(c);
      result -= (i > 0 && this.map.get(s.charAt(i - 1)) < this.map.get(c)
                 ? 2 * this.map.get(s.charAt(i - 1))
                 : 0);
    }
    return result;
  }
}
