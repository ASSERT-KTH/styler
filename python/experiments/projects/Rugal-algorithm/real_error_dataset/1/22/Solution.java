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
package ga.rugal.leetcode.wordsearchii;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * https://leetcode.com/problems/word-search-ii/
 *
 * @author rugalbernstein
 */
public class Solution {

  private char[][] board;

  private String word;

  private final Set<String> result = new HashSet<>();

  private boolean find = false;

  private static final int[] X = new int[]{0, 1, 0, -1};

  private static final int[] Y = new int[]{1, 0, -1, 0};

  public List<String> findWords(final char[][] board, final String[] words) {
    this.board = board;
    for (String s : words) {
      if (this.findWord(s)) {
        this.result.add(s);
      }
    }
    return new ArrayList<>(this.result);
  }

  private boolean findWord(final String word) {
    this.word = word;
    this.find = false;
    for (int i = 0; i < this.board.length; ++i) {
      for (int j = 0; j < this.board[0].length; ++j) {
        if (this.dfs(i, j, 0, new boolean[this.board.length][this.board[0].length])) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean isValid(final int row, final int column, final boolean[][] visit) {
    return row >= 0 && row < this.board.length
           && column >= 0 && column < this.board[0].length
           && !visit[row][column];
  }

  private boolean dfs(final int row, final int column, final int index, final boolean[][] visited) {
    if (this.find) {
      return true;
    }
    if (this.word.charAt(index) != this.board[row][column]) {
      return false;
    }
    if (index == this.word.length() - 1) {
      this.find = true;
      return true;
    }
    visited[row][column] = true;
    for (int i = 0; i < X.length; ++i) {
      if (this.isValid(row + X[i], column + Y[i], visited)
          && this.dfs(row + X[i], column + Y[i], index + 1, visited)) {
        return true;
      }
    }
    visited[row][column] = false;

    return false;
  }
}
