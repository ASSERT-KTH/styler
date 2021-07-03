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
package ga.rugal.leetcode.pathsum;

import java.util.LinkedList;
import java.util.Queue;

import ga.rugal.leetcode.TreeNode;

/**
 * https://leetcode.com/problems/path-sum/
 *
 * @author rugal
 */
public class Solution {

  public boolean hasPathSum(final TreeNode root, final int sum) {
    if (null == root) {
      return false;
    }
    final Queue<Pair> queue = new LinkedList<>();
    queue.offer(new Pair(root, root.val));
    while (!queue.isEmpty()) {
      final Pair top = queue.poll();
      if (top.node.left == null
          && top.node.right == null
          && top.sum == sum) {
        return true;
      }
      if (null != top.node.left) {
        queue.offer(new Pair(top.node.left, top.sum + top.node.left.val));
      }
      if (null != top.node.right) {
        queue.offer(new Pair(top.node.right, top.sum + top.node.right.val));
      }
    }
    return false;
  }

  static class Pair {

    public final TreeNode node;

    public final int sum;

    public Pair(final TreeNode node, final int sum) {
      this.node = node;
      this.sum = sum;
    }
  }
}
