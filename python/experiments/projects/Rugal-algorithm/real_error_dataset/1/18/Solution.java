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
    return this.dfs(root, sum);
  }

  private boolean dfs(final TreeNode root, final int sum) {
    if (null == root.left && null == root.right) {
      return sum - root.val == 0;
    }
    if (root.left == null || root.right == null) {
      return this.dfs(root.left == null ? root.right : root.left, sum - root.val);
    }
    return this.dfs(root.left, sum - root.val) || this.dfs(root.right, sum - root.val);
  }
}
