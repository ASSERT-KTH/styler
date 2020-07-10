/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.iotdb.tsfile.read.query.timegenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.iotdb.tsfile.exception.write.UnSupportedDataTypeException;
import org.apache.iotdb.tsfile.read.common.Path;
import org.apache.iotdb.tsfile.read.common.TimeColumn;
import org.apache.iotdb.tsfile.read.expression.ExpressionType;
import org.apache.iotdb.tsfile.read.expression.IBinaryExpression;
import org.apache.iotdb.tsfile.read.expression.IExpression;
import org.apache.iotdb.tsfile.read.expression.impl.SingleSeriesExpression;
import org.apache.iotdb.tsfile.read.query.timegenerator.node.AndNode;
import org.apache.iotdb.tsfile.read.query.timegenerator.node.LeafNode;
import org.apache.iotdb.tsfile.read.query.timegenerator.node.Node;
import org.apache.iotdb.tsfile.read.query.timegenerator.node.OrNode;
import org.apache.iotdb.tsfile.read.reader.IBatchReader;

/**
 * All SingleSeriesExpression involved in a IExpression will be transferred to a TimeGenerator tree
 * whose leaf nodes are all SeriesReaders, The TimeGenerator tree can generate the next timestamp
 * that satisfies the filter condition. Then we use this timestamp to get values in other series
 * that are not included in IExpression
 */
public abstract class TimeGenerator {


  private boolean hasCache;
  private TimeColumn cacheTimes;

  private HashMap<Path, List<LeafNode>> leafCache = new HashMap<>();
  private Node operatorNode;

  public boolean hasNext() throws IOException {
    if (hasCache) {
      return true;
    }

    while (operatorNode.hasNextTimeColumn()) {
      cacheTimes = operatorNode.nextTimeColumn();
      if (cacheTimes.hasCurrent()) {
        hasCache = true;
        break;
      }
    }
    return hasCache;
  }

  public long next() throws IOException {
    if (hasCache || hasNext()) {
      long currentTime = cacheTimes.currentTime();
      cacheTimes.next();
      hasCache = cacheTimes.hasCurrent();
      return currentTime;
    }
    throw new IOException("no more data");
  }

  public Object getValue(Path path, long time) {
    for (LeafNode leafNode : leafCache.get(path)) {
      Object value = leafNode.currentValue(time);
      if (value != null) {
        return value;
      }
    }
    return null;
  }

  public void constructNode(IExpression expression) throws IOException {
    operatorNode = construct(expression);
  }

  /**
   * construct the tree that generate timestamp.
   */
  private Node construct(IExpression expression) throws IOException {

    if (expression.getType() == ExpressionType.SERIES) {
      SingleSeriesExpression singleSeriesExp = (SingleSeriesExpression) expression;
      IBatchReader seriesReader = generateNewBatchReader(singleSeriesExp);
      Path path = singleSeriesExp.getSeriesPath();

      if (!leafCache.containsKey(path)) {
        leafCache.put(path, new ArrayList<>());
      }

      // put the current reader to valueCache
      LeafNode leafNode = new LeafNode(seriesReader);
      leafCache.get(path).add(leafNode);

      return leafNode;
    } else {
      Node leftChild = construct(((IBinaryExpression) expression).getLeft());
      Node rightChild = construct(((IBinaryExpression) expression).getRight());

      if (expression.getType() == ExpressionType.OR) {
        return new OrNode(leftChild, rightChild);
      } else if (expression.getType() == ExpressionType.AND) {
        return new AndNode(leftChild, rightChild);
      }
      throw new UnSupportedDataTypeException(
          "Unsupported ExpressionType when construct OperatorNode: " + expression.getType());
    }
  }

  protected abstract IBatchReader generateNewBatchReader(SingleSeriesExpression expression)
      throws IOException;

}
