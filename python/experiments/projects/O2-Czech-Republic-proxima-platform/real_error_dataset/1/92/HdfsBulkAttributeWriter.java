/**
 * Copyright 2017-2020 O2 Czech Republic, a.s.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cz.o2.proxima.direct.hdfs;

import com.google.common.annotations.VisibleForTesting;
import cz.o2.proxima.direct.core.AbstractBulkAttributeWriter;
import cz.o2.proxima.direct.core.CommitCallback;
import cz.o2.proxima.repository.EntityDescriptor;
import cz.o2.proxima.storage.StreamElement;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Map;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.SequenceFile.Writer;
import org.apache.hadoop.io.SequenceFile.Writer.Option;
import org.apache.hadoop.io.compress.GzipCodec;

/** Bulk attribute writer to HDFS as {@code SequenceFiles}. */
@Slf4j
@SuppressWarnings("squid:S2160")
public class HdfsBulkAttributeWriter extends AbstractBulkAttributeWriter {

  private final Map<String, Object> cfg;

  private transient FileSystem fs;
  private final int minElementsToFlush;
  private final long rollInterval;
  private final String compressionCodec;

  private transient SequenceFile.Writer writer = null;
  private transient Path writerTmpPath = null;
  private long lastRoll = 0;
  private long elementsSinceFlush = 0;
  private long minElementStamp = Long.MAX_VALUE;
  private long maxElementStamp = Long.MIN_VALUE;

  @Nullable private transient CommitCallback lastWrittenCallback = null;

  public HdfsBulkAttributeWriter(
      EntityDescriptor entityDesc,
      URI uri,
      Map<String, Object> cfg,
      int minElementsToFlush,
      long rollInterval,
      String compressionCodec) {

    super(entityDesc, uri);
    this.cfg = cfg;
    this.minElementsToFlush = minElementsToFlush;
    this.rollInterval = rollInterval;
    this.compressionCodec = compressionCodec;
  }

  @Override
  @SuppressWarnings("squid:S00112")
  public void rollback() {
    if (writer != null) {
      try {
        writer.close();
        writer = null;
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }

    clearTmpDir();
  }

  @Override
  public void write(StreamElement data, long watermark, CommitCallback commitCallback) {

    try {

      if (writer == null) {
        clearTmpDir();
        openWriter(watermark);
      }
      if (minElementStamp > data.getStamp()) {
        minElementStamp = data.getStamp();
      }
      if (maxElementStamp < data.getStamp()) {
        maxElementStamp = data.getStamp();
      }
      writer.append(
          new BytesWritable(toKey(data)),
          new TimestampedNullableBytesWritable(data.getStamp(), data.getValue()));
      lastWrittenCallback = commitCallback;
      if (++elementsSinceFlush > minElementsToFlush) {
        writer.hflush();
        writer.hsync();
        log.debug("Hflushed chunk {}", writerTmpPath);
        elementsSinceFlush = 0;
      }
      if (watermark - lastRoll >= rollInterval) {
        flush();
      }
    } catch (Exception ex) {
      try {
        log.error("Error in writing data {}", ex.getMessage(), ex);
        writer.close();
      } catch (Exception ex1) {
        log.error("Failed to close writer. Skipping.", ex1);
      }
      writer = null;
      commitCallback.commit(false, ex);
    }
  }

  private byte[] toKey(StreamElement data) {
    return (data.getKey() + "#" + data.getAttribute()).getBytes();
  }

  @SuppressWarnings("squid:S00112")
  private void openWriter(long eventTime) {
    long part = eventTime / rollInterval * rollInterval;
    try {
      Path tmp = toTmpLocation(part);
      log.debug("Opening writer at {}", tmp);
      lastRoll = part;
      elementsSinceFlush = 0;
      writerTmpPath = tmp;
      minElementStamp = Long.MAX_VALUE;
      maxElementStamp = Long.MIN_VALUE;

      Option compression;
      switch (compressionCodec.toLowerCase()) {
        case "gzip":
          compression = Writer.compression(CompressionType.BLOCK, new GzipCodec());
          break;
        case "none":
          compression = Writer.compression(CompressionType.NONE);
          break;
        default:
          throw new IllegalArgumentException(
              String.format(
                  "Unknown compression codec %s. Please check settings of %s",
                  compressionCodec.toLowerCase(),
                  HdfsDataAccessor.HDFS_SEQUENCE_FILE_COMPRESSION_CODEC_CFG));
      }
      writer =
          SequenceFile.createWriter(
              HdfsDataAccessor.toHadoopConf(cfg),
              SequenceFile.Writer.file(tmp),
              SequenceFile.Writer.appendIfExists(false),
              SequenceFile.Writer.keyClass(BytesWritable.class),
              SequenceFile.Writer.valueClass(TimestampedNullableBytesWritable.class),
              compression);
    } catch (IOException | URISyntaxException ex) {
      throw new RuntimeException(ex);
    }
  }

  String toPartName(long part) throws UnknownHostException {
    return String.format("part-%d-%s", part, getLocalhost());
  }

  String toFinalName(long minStamp, long maxStamp) throws UnknownHostException {
    return String.format("part-%d_%d-%s", minStamp, maxStamp, getLocalhost());
  }

  @VisibleForTesting
  @SuppressWarnings("squid:S1075")
  Path toFinalLocation(long part, long minStamp, long maxStamp)
      throws URISyntaxException, UnknownHostException {

    Instant d = Instant.ofEpochMilli(part);
    // place the final file in directory of (YYYY/MM)
    return new Path(
        new URI(
            getUri().toString()
                + HdfsDataAccessor.DIR_FORMAT.format(
                    LocalDateTime.ofInstant(d, ZoneId.ofOffset("UTC", ZoneOffset.ofHours(0))))
                + "/"
                + toFinalName(minStamp, maxStamp)));
  }

  @VisibleForTesting
  Path toTmpLocation(long part) throws UnknownHostException, URISyntaxException {

    // place the final file in directory /.tmp/
    return new Path(new URI(getUri().toString() + "/.tmp/" + toPartName(part)));
  }

  private void clearTmpDir() {
    try {
      Path tmpDir = new Path(getUri().toString() + "/.tmp/");
      if (getFs().exists(tmpDir)) {
        RemoteIterator<LocatedFileStatus> files = getFs().listFiles(tmpDir, false);
        String localhost = getLocalhost();
        while (files.hasNext()) {
          LocatedFileStatus file = files.next();
          if (matchesHostname(localhost, file)) {
            getFs().delete(file.getPath(), false);
          }
        }
      }
    } catch (IOException ex) {
      log.warn("Failed to clean tmp dir", ex);
    }
  }

  private boolean matchesHostname(String hostname, LocatedFileStatus file) {
    return file.getPath().getName().endsWith("-" + hostname);
  }

  private String getLocalhost() throws UnknownHostException {
    return InetAddress.getLocalHost().getCanonicalHostName();
  }

  @SuppressWarnings("squid:S00112")
  private void flush() {
    try {
      // close the current writer
      writer.close();
      Path tmpLocation = toTmpLocation(lastRoll);
      Path target = toFinalLocation(lastRoll, minElementStamp, maxElementStamp);
      log.info("Flushing file to target location: {}", target);
      // move the .tmp file to final location
      FileSystem fileSystem = getFs();
      if (!fileSystem.exists(target.getParent())) {
        silentMkDirs(target, fileSystem);
      }
      fileSystem.rename(tmpLocation, target);
      log.info("Completed chunk {}", target);
      writer = null;
      lastWrittenCallback.commit(true, null);
    } catch (IOException | URISyntaxException ex) {
      throw new RuntimeException(ex);
    }
  }

  private void silentMkDirs(Path target, FileSystem fs) {
    try {
      fs.mkdirs(target.getParent());
    } catch (IOException ex) {
      log.warn("Failed to mkdir {}, proceeding for now", target.getParent(), ex);
    }
  }

  private FileSystem getFs() {
    if (fs == null) {
      fs = HdfsDataAccessor.getFs(getUri(), cfg);
    }
    return fs;
  }

  @Override
  public void close() {
    if (writer != null) {
      try {
        writer.close();
      } catch (Exception ex) {
        log.warn("Failed to close writer {}. Ignoring", writer, ex);
      }
      writer = null;
    }
  }
}
