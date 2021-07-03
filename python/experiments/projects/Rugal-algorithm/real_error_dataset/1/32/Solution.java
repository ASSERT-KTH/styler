/*
 * Copyright 2020 u6105440.
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
package ga.rugal.leetcode.laststoneweight;

import java.util.PriorityQueue;

/**
 * https://leetcode.com/problems/last-stone-weight/
 *
 * @author Rugal Bernstein
 */
public class Solution {

  public int lastStoneWeight(final int[] stones) {
    final PriorityQueue<Integer> pq = new PriorityQueue<>(stones.length, (a, b) -> b - a);
    for (int stone : stones) {
      pq.add(stone);
    }
    while (pq.size() > 1) {
      final int y = pq.poll();
      final int x = pq.poll();
      if (x == y) {
        continue;
      }
      pq.add(y - x);
    }
    return pq.isEmpty() ? 0 : pq.poll();
  }
}
