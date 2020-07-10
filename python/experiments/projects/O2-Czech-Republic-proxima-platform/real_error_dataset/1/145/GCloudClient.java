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
package cz.o2.proxima.direct.gcloud.storage;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageClass;
import com.google.cloud.storage.StorageOptions;
import com.google.common.annotations.VisibleForTesting;
import cz.o2.proxima.repository.EntityDescriptor;
import cz.o2.proxima.storage.AbstractStorage;
import cz.o2.proxima.storage.UriUtil;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import lombok.Getter;

class GCloudClient extends AbstractStorage {

  final Map<String, Object> cfg;

  @Getter final String bucket;

  @Getter final String path;

  @Getter final StorageClass storageClass;

  @Nullable @Getter private transient Storage client;

  GCloudClient(EntityDescriptor entityDesc, URI uri, Map<String, Object> cfg) {
    super(entityDesc, uri);
    this.cfg = cfg;
    this.bucket = uri.getAuthority();
    this.path = toPath(uri);
    this.storageClass =
        Optional.ofNullable(cfg.get("storage-class"))
            .map(Object::toString)
            .map(StorageClass::valueOf)
            .orElse(StorageClass.STANDARD);
  }

  public Map<String, Object> getCfg() {
    return Collections.unmodifiableMap(cfg);
  }

  // normalize path to not start and to end with slash
  private static String toPath(URI uri) {
    return UriUtil.getPathNormalized(uri) + "/";
  }

  Blob createBlob(String name) {
    final String nameNoSlash = dropSlashes(name);
    return client()
        .create(
            BlobInfo.newBuilder(bucket, path + nameNoSlash).setStorageClass(storageClass).build(),
            Storage.BlobTargetOption.doesNotExist());
  }

  private String dropSlashes(String name) {
    String ret = name;
    while (ret.startsWith("/")) {
      ret = ret.substring(1);
    }
    return ret;
  }

  @VisibleForTesting
  Storage client() {
    if (client == null) {
      client = StorageOptions.getDefaultInstance().getService();
    }
    return client;
  }
}
