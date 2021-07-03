/*
 * Copyright 2013-2017 Indiana University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.iu.benchmark;

import edu.iu.harp.example.DoubleArrPlus;
import edu.iu.harp.partition.Partition;
import edu.iu.harp.partition.Table;
import edu.iu.harp.resource.DoubleArray;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.CollectiveMapper;

import java.io.IOException;
import java.util.Random;

public class BenchmarkMapper extends
  CollectiveMapper<String, String, Object, Object> {

  private String cmd;
  private int bytesPerPartition;
  private int numPartitions;
  private int numMappers;
  private int numIterations;

  /**
   * Mapper configuration.
   */
  @Override
  protected void setup(Context context)
    throws IOException, InterruptedException {
    Configuration configuration =
      context.getConfiguration();
    cmd = configuration
      .get(Constants.BENCHMARK_CMD, "bcast");
    numMappers = configuration
      .getInt(Constants.NUM_MAPPERS, 1);
    numPartitions = configuration
      .getInt(Constants.NUM_PARTITIONS, 1);
    bytesPerPartition = configuration
      .getInt(Constants.BYTES_PER_PARTITION, 1);
    numIterations = configuration
      .getInt(Constants.NUM_ITERATIONS, 1);
    LOG.info("Benchmark CMD " + cmd);
    LOG.info("Num Mappers " + numMappers);
    LOG.info("Num Partitions " + numPartitions);
    LOG.info(
      "Bytes per Partition " + bytesPerPartition);
    LOG.info("Num Iterations " + numIterations);
  }

  protected void mapCollective(
    KeyValReader reader, Context context)
    throws IOException, InterruptedException {
    // Read key-value pairs
    while (reader.nextKeyValue()) {
      String key = reader.getCurrentKey();
      String value = reader.getCurrentValue();
      LOG.info(
        "Key: " + key + ", Value: " + value);
    }
    int workerID = this.getSelfID();
    int numWorkers = this.getNumWorkers();
    Random rand = new Random(workerID);
    if (cmd.equals("allreduce")) {
      long startTime = System.currentTimeMillis();
      for (int i = 0; i < numIterations; i++) {
        Table<DoubleArray> arrTable =
          new Table<>(i, new DoubleArrPlus());
        // Create DoubleArray
        int size = bytesPerPartition / 8;
        for (int j = 0; j < numPartitions; j++) {
          DoubleArray array =
            DoubleArray.create(size, false);
          array.get()[0] = rand.nextInt(1000);
          array.get()[array.size() - 1] =
            rand.nextInt(1000);
          LOG.info("before allreduce: " + j + " "
            + array.get()[0] + " "
            + array.get()[array.size() - 1]);
          arrTable.addPartition(
            new Partition<>(j, array));
        }
        allreduce("main", "allreduce-" + i,
          arrTable);
        for (Partition<DoubleArray> partition : arrTable
          .getPartitions()) {
          DoubleArray array = partition.get();
          LOG.info(
            "after allreduce: " + partition.id()
              + " " + array.get()[0] + " "
              + array.get()[array.size() - 1]);
        }
        arrTable.release();
      }
      long endTime = System.currentTimeMillis();
      LOG.info("Total allreduce time: "
        + (endTime - startTime)
        + " number of iterations: "
        + numIterations);
    } else if (cmd.equals("allgather")) {
      long startTime = System.currentTimeMillis();
      for (int i = 0; i < numIterations; i++) {
        Table<DoubleArray> arrTable =
          new Table<>(i, new DoubleArrPlus());
        int size = bytesPerPartition / 8;
        // Generate data
        for (int j = 0; j < numPartitions; j++) {
          int partitionID =
            workerID + numWorkers * j;
          DoubleArray array =
            DoubleArray.create(size, false);
          array.get()[0] = rand.nextDouble();
          array.get()[array.size() - 1] =
            rand.nextDouble();
          LOG.info(
            "before allgather: " + partitionID
              + " " + array.get()[0] + " "
              + array.get()[array.size() - 1]);
          arrTable.addPartition(
            new Partition<DoubleArray>(
              partitionID, array));
        }
        allgather("main", "allgather-" + i,
          arrTable);
        for (Partition<DoubleArray> partition : arrTable
          .getPartitions()) {
          DoubleArray array = partition.get();
          LOG.info(
            "after allgather: " + partition.id()
              + " " + array.get()[0] + " "
              + array.get()[array.size() - 1]);
        }
        arrTable.release();
      }
      long endTime = System.currentTimeMillis();
      LOG.info("Total allgather time: "
        + (endTime - startTime));
    }
  }
}
