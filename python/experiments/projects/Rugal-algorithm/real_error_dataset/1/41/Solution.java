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
package ga.rugal.leetcode.reversenodesinkgroup;

import ga.rugal.leetcode.ListNode;

/**
 * https://leetcode.com/problems/reverse-nodes-in-k-group
 *
 * @author rugalbernstein
 */
public class Solution {

  public ListNode reverseKGroup(ListNode head, final int k) {
    int count = 0;
    ListNode current = head;
    while (current != null && count < k) {
      ++count;
      current = current.next;
    }

    if (count != k) {
      return head;
    }
    current = this.reverseKGroup(current, k);
    while (count-- > 0) {
      final ListNode temp = head.next; //next head
      head.next = current;//start reverse, place head at the very bottom
      current = head;//the new bottom
      head = temp;//new head
    }
    //now current is the actual head, head is point to temp, which is the head of next group
    head = current;

    return head;
  }
}
