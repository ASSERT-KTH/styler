/*
 * Copyright 2020 rugal.
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
package ga.rugal.leetcode.diameterofbinarytree;

import ga.rugal.leetcode.TreeNode;

/**
 * https://leetcode.com/problems/diameter-of-binary-tree/
 *
 * @author rugal
 */
public class Solution {

  private int max = 0;

  public int diameterOfBinaryTree(final TreeNode root) {
    this.maxDepth(root);
    return this.max;
  }

  private int maxDepth(final TreeNode node) {
    if (null == node) {
      return 0;
    }

    final int left = this.maxDepth(node.left);
    final int right = this.maxDepth(node.right);
    this.max = Math.max(this.max, left + right);

    return Math.max(left, right) + 1;
  }
}
