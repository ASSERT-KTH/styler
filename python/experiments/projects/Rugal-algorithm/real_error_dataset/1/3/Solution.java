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
package ga.rugal.leetcode.findfirstandlastpositionofelementinsortedarray;

/**
 * https://leetcode.com/problems/find-first-and-last-position-of-element-in-sorted-array/
 *
 * @author rugalbernstein
 */
public class Solution {

  private int[] nums;

  private int target;

  public int[] searchRange(final int[] nums, final int target) {
    this.nums = nums;
    this.target = target;
    final int mid = this.binarySearch(0, nums.length);
    if (mid == -1) {
      return new int[]{-1, -1};
    }
    int left;
    int right;
    int found = mid;
    //search left part for the left bound
    do {
      left = found;
      found = this.binarySearch(0, left);
    } while (-1 != found);
    found = mid;
    //search right part for the right bound
    do {
      right = found + 1;
      found = this.binarySearch(right, this.nums.length);
    } while (-1 != found);
    return new int[]{left, right - 1};
  }

  private int binarySearch(int left, int right) {
    while (left < right) {
      final int mid = (left + right) / 2;
      if (this.nums[mid] == this.target) {
        return mid;
      }
      if (this.nums[mid] < this.target) {
        left = mid + 1;
      } else {
        right = mid;
      }
    }
    return -1;
  }
}
