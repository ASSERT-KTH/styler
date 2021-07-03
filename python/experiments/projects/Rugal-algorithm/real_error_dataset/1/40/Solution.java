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
package ga.rugal.leetcode.simplifypath;

import java.util.Stack;

/**
 * https://leetcode.com/problems/simplify-path/
 *
 * @author rugal
 */
public class Solution {

  private static final String UP = "..";

  private static final String CURRENT = ".";

  /**
   * Stack idea.
   *
   * @param path
   *
   * @return
   */
  public String simplifyPath(final String path) {
    final Stack<String> stack = new Stack<>();
    for (String s : path.split("/")) {
      if (s.isEmpty() || s.equals(CURRENT)) {
        continue;
      }
      if (s.equals(UP)) {
        if (!stack.isEmpty()) {
          stack.pop();
        }
        continue;
      }
      stack.push(s);
    }
    final StringBuilder sb = new StringBuilder();
    for (int i = 0; i < stack.size(); ++i) {
      sb.append("/").append(stack.get(i));
    }
    return sb.length() == 0 ? "/" : sb.toString();
  }
}
