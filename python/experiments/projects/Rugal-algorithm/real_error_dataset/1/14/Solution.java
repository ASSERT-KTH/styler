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
    int match;
    for (match = 0; match < nums.length; ++match) {
      if (nums[match] == val) {
        break;
      }
    }
    if (match >= nums.length) {
      return nums.length;
    }
    int mismatch;

    for (mismatch = nums.length - 1; mismatch >= 0; --mismatch) {
      if (nums[mismatch] != val) {
        break;
      }
    }
    if (mismatch < 0) {
      return 0;
    }
    while (match < mismatch) {
      //swap
      final int temp = nums[match];
      nums[match] = nums[mismatch];
      nums[mismatch] = temp;

      for (; match < nums.length; ++match) {
        if (nums[match] == val) {
          break;
        }
      }

      for (; mismatch >= 0; --mismatch) {
        if (nums[mismatch] != val) {
          break;
        }
      }
    }

    return match;
  }
}
