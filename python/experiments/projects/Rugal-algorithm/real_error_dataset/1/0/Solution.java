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
package ga.rugal.amazon.islandperimeter;

/**
 * https://leetcode.com/problems/island-perimeter
 *
 * @author rugal
 */
public class Solution {

  public int islandPerimeter(final int[][] grid) {
    if (grid == null || grid.length == 0 || grid[0].length == 0) {
      return 0;
    }

    int total = 0;
    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[0].length; j++) {
        if (grid[i][j] == 1) {
          total += 4;
          if (i > 0 && grid[i - 1][j] == 1) {
            //1 for each side, so 2 in total
            total -= 2;
          }
          if (j > 0 && grid[i][j - 1] == 1) {
            //1 for each side, so 2 in total
            total -= 2;
          }
        }
      }
    }
    return total;
  }
}
