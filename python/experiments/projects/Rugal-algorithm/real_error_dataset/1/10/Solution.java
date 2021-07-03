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
package ga.rugal.leetcode.clonegraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import ga.rugal.leetcode.Node;

/**
 * https://leetcode.com/problems/clone-graph
 *
 * @author rugal
 */
public class Solution {

  public Node cloneGraph(final Node node) {

    if (node == null) {
      return null;
    }
    final Map<Node, Node> relationship = new HashMap<>();
    final Queue<Node> queue = new LinkedList<>();

    relationship.put(node, new Node(node.val, new ArrayList<>()));
    queue.add(node);

    while (!queue.isEmpty()) {
      final Node current = queue.remove();
      for (final Node neighbor : current.neighbors) {
        //if not created yet
        if (!relationship.containsKey(neighbor)) {
          //then first add to relationship cache
          relationship.put(neighbor, new Node(neighbor.val, new ArrayList<>()));
          //then need to queue the newly created node
          queue.add(neighbor);
        }
        //add newly created to neighbour of current node
        relationship.get(current).neighbors.add(relationship.get(neighbor));
      }
    }
    return relationship.get(node);
  }
}
