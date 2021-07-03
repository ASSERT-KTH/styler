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
package ga.rugal.leetcode.symmetrictree;

import java.util.LinkedList;
import java.util.Queue;

import ga.rugal.leetcode.TreeNode;

/**
 * https://leetcode.com/problems/symmetric-tree
 *
 * @author rugalbernstein
 */
public class Solution {

  public boolean isSymmetric(final TreeNode root) {
    return this.isSymmetricRecursive(root, root)
           && this.isSymmetricIterative(root);
  }

  private boolean isSymmetricRecursive(final TreeNode left, final TreeNode right) {
    if (null == left && null == right) {
      return true;
    }
    if (null == left || null == right) {
      return false;
    }

    return left.val == right.val
           && this.isSymmetricRecursive(left.left, right.right)
           && this.isSymmetricRecursive(left.right, right.left);
  }

  private boolean isSymmetricIterative(final TreeNode root) {
    final Queue<TreeNode> q = new LinkedList<>();
    q.add(root);
    q.add(root);
    while (!q.isEmpty()) {
      final TreeNode t1 = q.poll();
      final TreeNode t2 = q.poll();
      if (t1 == null && t2 == null) {
        continue;
      }
      if (t1 == null || t2 == null) {
        return false;
      }
      if (t1.val != t2.val) {
        return false;
      }
      q.add(t1.left);
      q.add(t2.right);
      q.add(t1.right);
      q.add(t2.left);
    }
    return true;
  }
}
