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
package ga.rugal.leetcode.movingstonesuntilconsecutive;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * https://leetcode.com/contest/weekly-contest-134/problems/moving-stones-until-consecutive/
 *
 * @author rugal
 */
public class Solution {

  public int[] numMovesStones(int a, int b, int c) {
    final List<Integer> list = Arrays.asList(a, b, c);
    Collections.sort(list);
    a = list.get(0);
    b = list.get(1);
    c = list.get(2);
    if (a + 1 == b && b + 1 == c) {
      return new int[]{0, 0};
    }
    return new int[]{
      a + 1 == b || b + 1 == c || a + 2 == b || b + 2 == c ? 1 : 2,
      c - a - 2
    };
  }
}
