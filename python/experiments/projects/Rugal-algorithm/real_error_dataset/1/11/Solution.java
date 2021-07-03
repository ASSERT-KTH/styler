package ga.rugal.amazon.exclusivetimeoffunctions;

import java.util.List;
import java.util.Stack;

/**
 * https://leetcode.com/problems/exclusive-time-of-functions/
 *
 * @author rugal
 */
public class Solution {

  public int[] exclusiveTime(final int n, final List< String> logs) {
    final Stack<Integer> stack = new Stack<>();
    final int[] result = new int[n];
    String[] s = logs.get(0).split(":");
    stack.push(Integer.parseInt(s[0]));
    int previous = Integer.parseInt(s[2]);
    for (int i = 1; i < logs.size(); ++i) {
      s = logs.get(i).split(":");
      final int time = Integer.parseInt(s[2]);
      if (s[1].equals("start")) {
        //start a new function
        if (!stack.isEmpty()) {
          result[stack.peek()] += time - previous;
        }
        stack.push(Integer.parseInt(s[0]));
        // the start time of current function
        previous = time;
      } else {
        // end the current function
        // it is always the current function that ends, so add time to it
        result[stack.pop()] += time - previous + 1;
        // move to next second, serve for the next function
        previous = time + 1;
      }
    }
    return result;
  }
}
