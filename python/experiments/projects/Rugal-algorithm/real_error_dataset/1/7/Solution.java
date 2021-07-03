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
package ga.rugal.leetcode.uniquepathsii;

/**
 * https://leetcode.com/problems/unique-paths-ii/
 *
 * @author rugal
 */
public class Solution {

  /**
   * DP.
   *
   * @param obstacleGrid
   *
   * @return
   */
  public int uniquePathsWithObstacles(final int[][] obstacleGrid) {
    final int row = obstacleGrid.length;
    final int column = obstacleGrid[0].length;

    //stop here if the final grid is blocked
    if (obstacleGrid[row - 1][column - 1] == 1) {
      return 0;
    }

    final int[][] value = new int[row][column];
    //initialize the right most and bottom most to 1
    //because we can only move to one direction anyway
    value[row - 1][column - 1] = 1;
    for (int i = row - 2; i >= 0; i--) {
      if (0 == obstacleGrid[i][column - 1]) {
        value[i][column - 1] = value[i + 1][column - 1];
      }
    }
    for (int i = column - 2; i >= 0; i--) {
      if (0 == obstacleGrid[row - 1][i]) {
        value[row - 1][i] = value[row - 1][i + 1];
      }
    }

    for (int i = row - 2; i >= 0; i--) {
      for (int j = column - 2; j >= 0; j--) {
        if (obstacleGrid[i][j] != 1) {
          //is the summation of right and down
          value[i][j] = value[i][j + 1] + value[i + 1][j];
        }
      }
    }

    return value[0][0];
  }
}
