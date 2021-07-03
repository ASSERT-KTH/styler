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
package ga.rugal.leetcode.sortcolors;

/**
 * https://leetcode.com/problems/sort-colors/
 *
 * @author rugal
 */
public class Solution {

  public void sortColors(final int[] nums) {
    int zero = 0;
    int one = 0;
    for (int i = 0; i < nums.length; ++i) {
      final int current = nums[i];
      nums[i] = 2;
      if (current < 2) {
        nums[one++] = 1;
      }
      if (current == 0) {
        nums[zero++] = 0;
      }
    }
  }
}
