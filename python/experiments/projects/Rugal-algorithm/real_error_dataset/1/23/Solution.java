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
package ga.rugal.leetcode.matrix01;

import java.awt.Point;
import java.util.LinkedList;
import java.util.Queue;

/**
 * https://leetcode.com/problems/01-matrix/
 *
 * @author rugal
 */
public class Solution {

  private static final int[][] N = new int[][]{{0, 1, 0, -1}, {1, 0, -1, 0}};

  private int[][] matrix;

  public int[][] updateMatrix(final int[][] matrix) {
    this.matrix = matrix;
    final int[][] result = new int[matrix.length][matrix[0].length];
    final Queue<Point> queue = new LinkedList<>();
    for (int i = 0; i < matrix.length; ++i) {
      for (int j = 0; j < matrix[0].length; ++j) {
        result[i][j] = Integer.MAX_VALUE;
        if (this.matrix[i][j] == 0) {
          result[i][j] = 0;
          queue.offer(new Point(i, j));
        }
      }
    }

    while (!queue.isEmpty()) {
      final Point top = queue.poll();
      for (int i = 0; i < N[0].length; ++i) {
        final int x = N[0][i] + top.x;
        final int y = N[1][i] + top.y;
        if (this.isValid(x, y) && result[x][y] > result[top.x][top.y] + 1) {
          result[x][y] = result[top.x][top.y] + 1;
          queue.offer(new Point(x, y));
        }
      }
    }
    return result;
  }

  private boolean isValid(final int row, final int column) {
    return 0 <= row && row < this.matrix.length
           && 0 <= column && column < this.matrix[0].length;
  }
}
