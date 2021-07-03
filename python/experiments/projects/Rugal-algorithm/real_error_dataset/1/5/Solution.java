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
package ga.rugal.leetcode.redundantconnection;

/**
 * https://leetcode.com/problems/redundant-connection/
 *
 * @author rugalbernstein
 */
public class Solution {

  public int[] findRedundantConnection(final int[][] edges) {
    final UnionFindSet set = new UnionFindSet(edges.length);
    for (int i = 0; i < edges.length; ++i) {
      if (!set.union(edges[i][0], edges[i][1])) {
        return edges[i];
      }
    }
    return null;
  }

  private static class UnionFindSet {

    private final int[] rank;

    private final int[] parent;

    public UnionFindSet(final int n) {
      this.rank = new int[n + 1];
      this.parent = new int[n + 1];

      for (int i = 0; i < this.parent.length; ++i) {
        this.parent[i] = i;
      }
    }

    int find(final int u) {
      if (u != this.parent[u]) {
        this.parent[u] = this.find(this.parent[u]);
      }
      return this.parent[u];
    }

    boolean union(final int u, final int v) {
      final int pu = this.find(u);
      final int pv = this.find(v);
      if (pu == pv) {
        return false;
      }
      if (this.rank[pv] > this.rank[pu]) {
        this.parent[pu] = pv;
        return true;
      }
      if (this.rank[pv] < this.rank[pu]) {
        this.parent[pv] = pu;
        return true;
      }
      this.parent[pv] = pu;
      ++this.rank[pv];
      return true;
    }
  }
}
