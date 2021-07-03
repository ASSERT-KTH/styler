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
package ga.rugal.leetcode.combinationsumiii;

import java.util.ArrayList;
import java.util.List;

/**
 * https://leetcode.com/problems/combination-sum-iii/
 *
 * @author rugalbernstein
 */
public class Solution {

  private final List<List<Integer>> result = new ArrayList<>();

  private int leftNumber;

  public List<List<Integer>> combinationSum3(final int leftNumber, final int target) {
    this.leftNumber = leftNumber;
    this.backtrack(new ArrayList<>(), target);
    return this.result;
  }

  private void backtrack(final List<Integer> temp, final int target) {
    if (temp.size() > this.leftNumber || target < 0) {
      //too many element, or too much data
      return;
    }
    if (temp.size() == this.leftNumber && target == 0) {
      //right number right data
      this.result.add(new ArrayList<>(temp));
      return;
    }
    for (int i = 1; i < 10; ++i) {
      //to prevent from duplication
      if (temp.size() > 0 && temp.get(temp.size() - 1) < i
          || temp.isEmpty()) {
        temp.add(i);
        this.backtrack(temp, target - i);
        temp.remove(temp.size() - 1);
      }
    }
  }
}
