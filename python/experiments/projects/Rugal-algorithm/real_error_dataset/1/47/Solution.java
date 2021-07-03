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
package ga.rugal.leetcode.removeelement;

/**
 * https://leetcode.com/problems/remove-element/
 *
 * @author rugalbernstein
 */
public class Solution {

  public int removeElement(final int[] nums, final int val) {
    int unmatch = 0;
    for (int i = 0; i < nums.length; ++i) {
      if (val != nums[i]) {
        final int temp = nums[i];
        nums[i] = nums[unmatch];
        nums[unmatch++] = temp;
      }
    }

    return unmatch;
  }
}
