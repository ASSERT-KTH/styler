/*
 * Tencent is pleased to support the open source community by making Angel available.
 *
 * Copyright (C) 2017-2018 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/Apache-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package com.tencent.angel.graph.client.node2vec.data;

import com.tencent.angel.graph.data.NodeUtils;
import com.tencent.angel.ml.math2.vector.IntFloatVector;
import com.tencent.angel.ps.storage.vector.element.IElement;
import io.netty.buffer.ByteBuf;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Node implements IElement {

  private IntFloatVector feats;
  private long[] neighbors;
  private int[] types;
  private float[] attrs;

  public Node(IntFloatVector feats, long[] neighbors) {
    this(feats, neighbors, null);
  }

  public Node(IntFloatVector feats, long[] neighbors, int[] types) {
    this.feats = feats;
    this.neighbors = neighbors;
    this.types = types;
  }

  public Node() {
    this(null, null, null);
  }

  public IntFloatVector getFeats() {
    return feats;
  }

  public void setFeats(IntFloatVector feats) {
    this.feats = feats;
  }

  public long[] getNeighbors() {
    return neighbors;
  }

  public void setNeighbors(long[] neighbors) {
    this.neighbors = neighbors;
  }

  public int[] getTypes() {
    return types;
  }

  public void setTypes(int[] types) {
    this.types = types;
  }

  public float getWeight() {
    return attrs[0];
  }

  public void setWeight(float attrW) {
    attrs[0] = attrW;
  }

  public float getJ() {
    return attrs[1];
  }

  public void setJ(float sttr_j) {
    attrs[1] = sttr_j;
  }

  public float getP() {
    return attrs[2];
  }

  public void setP(float sttr_p) {
    attrs[2] = sttr_p;
  }

  @Override
  public com.tencent.angel.graph.data.Node deepClone() {
    IntFloatVector cloneFeats = feats.clone();

    long[] cloneNeighbors = new long[neighbors.length];
    System.arraycopy(neighbors, 0, cloneNeighbors, 0, neighbors.length);

    if (types == null) {
      return new com.tencent.angel.graph.data.Node(cloneFeats, cloneNeighbors);
    } else {
      int[] cloneTypes = new int[types.length];
      System.arraycopy(types, 0, cloneTypes, 0, types.length);
      return new com.tencent.angel.graph.data.Node(cloneFeats, cloneNeighbors, cloneTypes);
    }
  }

  @Override
  public void serialize(ByteBuf output) {
    NodeUtils.serialize(feats, output);

    output.writeInt(neighbors.length);
    for (int i = 0; i < neighbors.length; i++) {
      output.writeLong(neighbors[i]);
    }

    if (types != null) {
      output.writeInt(types.length);
      for (int i = 0; i < types.length; i++) {
        output.writeInt(types[i]);
      }
    }
  }

  @Override
  public void deserialize(ByteBuf input) {
    feats = NodeUtils.deserialize(input);

    int len = input.readInt();
    neighbors = new long[len];
    for (int i = 0; i < len; i++) {
      neighbors[i] = input.readLong();
    }

    if (types != null) {
      len = input.readInt();
      types = new int[len];
      for (int i = 0; i < len; i++) {
        types[i] = input.readInt();
      }
    }
  }

  @Override
  public int bufferLen() {
    int len = NodeUtils.dataLen(feats);
    len += 4 + 8 * neighbors.length;
    if (types != null) {
      len += 4 + 4 * types.length;
    }
    return len;
  }

  @Override
  public void serialize(DataOutputStream output) throws IOException {
    NodeUtils.serialize(feats, output);

    output.writeInt(neighbors.length);
    for (int i = 0; i < neighbors.length; i++) {
      output.writeLong(neighbors[i]);
    }

    if (types != null) {
      output.writeInt(types.length);
      for (int i = 0; i < types.length; i++) {
        output.writeInt(types[i]);
      }
    }
  }

  @Override
  public void deserialize(DataInputStream input) throws IOException {
    feats = NodeUtils.deserialize(input);

    int len = input.readInt();
    neighbors = new long[len];
    for (int i = 0; i < len; i++) {
      neighbors[i] = input.readLong();
    }

    if (types != null) {
      len = input.readInt();
      types = new int[len];
      for (int i = 0; i < len; i++) {
        types[i] = input.readInt();
      }
    }
  }

  @Override
  public int dataLen() {
    return bufferLen();
  }
}
