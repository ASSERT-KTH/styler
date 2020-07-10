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

package org.apache.iotdb.db.engine.modification;

import java.util.Objects;
import org.apache.iotdb.tsfile.read.common.Path;

/**
 * Modification represents an UPDATE or DELETE operation on a certain timeseries.
 */
public abstract class Modification {

  protected Type type;
  protected Path path;
  protected long versionNum;

  Modification(Type type, Path path, long versionNum) {
    this.type = type;
    this.path = path;
    this.versionNum = versionNum;
  }

  public String getPathString() {
    return path.getFullPath();
  }

  public Path getPath() {
    return path;
  }

  public String getDevice() {
    return path.getDevice();
  }

  public String getMeasurement() {
    return path.getMeasurement();
  }

  public void setPath(Path path) {
    this.path = path;
  }

  public long getVersionNum() {
    return versionNum;
  }

  public void setVersionNum(long versionNum) {
    this.versionNum = versionNum;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public enum Type {
    DELETION
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
       return true;
    }
    if (!(obj instanceof Modification)) {
      return false;
    }
    Modification mod = (Modification) obj;
    return mod.type.equals(this.type) && mod.path.equals(this.path)
            && mod.versionNum == this.versionNum;
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, path, versionNum);
  }
}
