package ga.rugal.leetcode.reorderlogfiles;

import java.util.Arrays;

/**
 * https://leetcode.com/problems/reorder-log-files/
 *
 * @author rugal
 */
public class Solution {

  /**
   * 1. Letter-logs come before digit-logs;<BR>
   * 2. Letter-logs are sorted alphanumerically, by content then identifier;<BR>
   * 3. Digit-logs remain in the same order.
   *
   * @param logs
   *
   * @return
   */
  public String[] reorderLogFiles(final String[] logs) {
    Arrays.sort(logs, (a, b) -> {
      final String[] sa = a.split(" ", 2);
      final String[] sb = b.split(" ", 2);
      final boolean d1 = Character.isDigit(sa[1].charAt(0));
      final boolean d2 = Character.isDigit(sb[1].charAt(0));
      final int c = sa[1].compareTo(sb[1]);

      return (!d1 && !d2)
             ? (0 != c // if both are letter
                ? c // try sort by content
                : sa[0].compareTo(sb[0])) // then by identifier
             : (d1 // if first is digit
                ? (d2 // if second is digit as well
                   ? 0 // remain same order
                   : 1) // letter before digit
                : -1); // otherwise, first is letter and second is digit for sure
              });
    return logs;
  }
}
