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
package ga.rugal.leetcode.asteroidcollision;

import java.util.Stack;

/**
 * https://leetcode.com/problems/asteroid-collision/
 *
 * @author rugal
 */
public class Solution {

  public int[] asteroidCollision(final int[] asteroids) {
    final Stack<Integer> stack = new Stack<>();
    for (int a : asteroids) {
      boolean push = true;
      while (!stack.isEmpty()) {
        final int top = stack.peek();
        if (top * a > 0 || top < 0) {
          break;
        }
        if (top >= Math.abs(a)) {
          push = false;
          if (top == Math.abs(a)) {
            stack.pop();
          }
          break;
        }
        stack.pop();
      }
      if (push) {
        stack.push(a);
      }
    }

    return stack.stream()
      .mapToInt(Integer::intValue)
      .toArray();
  }
}
