package ga.rugal.amazon.meetingroomsii;

import java.util.Arrays;
import java.util.PriorityQueue;

/**
 * https://leetcode.com/problems/meeting-rooms-ii/
 *
 * @author rugal
 */
public class Solution {

  public int minMeetingRooms(final int[][] intervals) {
    return this.minMeetingRooms_bucketSort(intervals);
  }

  public int minMeetingRooms_sort(final int[][] intervals) {

    // Check for the base case. If there are no intervals, return 0
    if (intervals.length == 0) {
      return 0;
    }

    // Min heap
    final PriorityQueue<Integer> allocator = new PriorityQueue<>(intervals.length, (a, b) -> a - b);

    // Sort the intervals by start time
    Arrays.sort(intervals, (a, b) -> a[0] - b[0]);

    // Add the first meeting, record the end time of meeting
    allocator.add(intervals[0][1]);

    // Iterate over remaining intervals
    for (int i = 1; i < intervals.length; i++) {
      // keep checking if the top meeting room is freed. If not, we have to push the current one into heap
      // If the room due to free up the earliest is free, assign that room to this meeting.
      if (intervals[i][0] >= allocator.peek()) {
        // if new meeting starts after the the earliest meeting ends
        // the earliest meeting will not block any one after this meeting anymore
        allocator.poll();
      }

      // If a new room is to be assigned, then also we add to the heap,
      // If an old room is allocated, then also we have to add to the heap with updated end time.
      allocator.add(intervals[i][1]);
    }

    // The size of the heap tells us the minimum rooms required for all the meetings.
    return allocator.size();
  }

  public int minMeetingRooms_bucketSort(final int[][] intervals) {
    int max = -1;
    for (int[] interval : intervals) {
      max = Math.max(max, interval[1]);
    }
    final int[] start = new int[max + 1];
    final int[] stop = new int[max + 1];

    for (int[] interval : intervals) {
      ++start[interval[0]];
      --stop[interval[1]];
    }

    max = 0;
    int count = 0;
    for (int i = 0; i < start.length; ++i) {
      if (0 == start[i] && 0 == stop[i]) {
        continue;
      }
      count += start[i];
      max = Math.max(max, count);
      count += stop[i];
    }

    return max;
  }
}
