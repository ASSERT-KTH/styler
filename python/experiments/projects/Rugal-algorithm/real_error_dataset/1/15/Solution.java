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
package ga.rugal.amazon.countunivaluesubtrees;

import ga.rugal.leetcode.TreeNode;

/**
 * https://leetcode.com/problems/count-univalue-subtrees/
 *
 * @author rugalbernstein
 */
public class Solution {

  private static final int NULL = Integer.MIN_VALUE;

  private static final int NO = Integer.MAX_VALUE;

  private int count = 0;

  public int countUnivalSubtrees(final TreeNode root) {
    if (root == null) {
      return 0;
    }
    this.dfs(root);
    return this.count;
  }

  private int dfs(final TreeNode root) {
    if (null == root) {
      return NULL;
    }
    final int left = this.dfs(root.left);
    final int right = this.dfs(root.right);
    //any of the leaf is not univalue
    if (left == NO || right == NO) {
      return NO;
    }
    if (left == NULL && right == NULL) {
      //if this is leaf
      ++this.count;
      return root.val;
    }
    if (left != NULL && right != NULL) {
      //if this is branch with both leafs
      if (root.val != left || root.val != right) {
        return NO;
      }
      ++this.count;
      return root.val;
    }

    if (left != NULL) {
      if (left != root.val) {
        return NO;
      }
      ++this.count;
      return root.val;
    }

    if (right != root.val) {
      return NO;
    }
    ++this.count;
    return root.val;
  }
}
