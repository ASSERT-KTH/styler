/*
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.storage;

import static com.google.cloud.storage.Bucket.BucketSourceOption.toGetOptions;
import static com.google.cloud.storage.Bucket.BucketSourceOption.toSourceOptions;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.cloud.Page;
import com.google.cloud.storage.Acl.Entity;
import com.google.cloud.storage.Storage.BlobGetOption;
import com.google.cloud.storage.Storage.BlobListOption;
import com.google.cloud.storage.Storage.BucketTargetOption;
import com.google.cloud.storage.spi.StorageRpc;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A Google cloud storage bucket.
 *
 * <p>Objects of this class are immutable. Operations that modify the bucket like {@link #update}
 * return a new object. To get a {@code Bucket} object with the most recent information use
 * {@link #reload}. {@code Bucket} adds a layer of service-related functionality over
 * {@link BucketInfo}.
 * </p>
 */
public class Bucket extends BucketInfo {

  private static final long serialVersionUID = 8574601739542252586L;

  private final StorageOptions options;
  private transient Storage storage;

  /**
   * Class for specifying bucket source options when {@code Bucket} methods are used.
   */
  public static class BucketSourceOption extends Option {

    private static final long serialVersionUID = 6928872234155522371L;

    private BucketSourceOption(StorageRpc.Option rpcOption) {
      super(rpcOption, null);
    }

    private Storage.BucketSourceOption toSourceOption(BucketInfo bucketInfo) {
      switch (rpcOption()) {
        case IF_METAGENERATION_MATCH:
          return Storage.BucketSourceOption.metagenerationMatch(bucketInfo.metageneration());
        case IF_METAGENERATION_NOT_MATCH:
          return Storage.BucketSourceOption.metagenerationNotMatch(bucketInfo.metageneration());
        default:
          throw new AssertionError("Unexpected enum value");
      }
    }

    private Storage.BucketGetOption toGetOption(BucketInfo bucketInfo) {
      switch (rpcOption()) {
        case IF_METAGENERATION_MATCH:
          return Storage.BucketGetOption.metagenerationMatch(bucketInfo.metageneration());
        case IF_METAGENERATION_NOT_MATCH:
          return Storage.BucketGetOption.metagenerationNotMatch(bucketInfo.metageneration());
        default:
          throw new AssertionError("Unexpected enum value");
      }
    }

    /**
     * Returns an option for bucket's metageneration match. If this option is used the request will
     * fail if metageneration does not match.
     */
    public static BucketSourceOption metagenerationMatch() {
      return new BucketSourceOption(StorageRpc.Option.IF_METAGENERATION_MATCH);
    }

    /**
     * Returns an option for bucket's metageneration mismatch. If this option is used the request
     * will fail if metageneration matches.
     */
    public static BucketSourceOption metagenerationNotMatch() {
      return new BucketSourceOption(StorageRpc.Option.IF_METAGENERATION_NOT_MATCH);
    }

    static Storage.BucketSourceOption[] toSourceOptions(BucketInfo bucketInfo,
         BucketSourceOption... options) {
      Storage.BucketSourceOption[] convertedOptions =
          new Storage.BucketSourceOption[options.length];
      int index = 0;
      for (BucketSourceOption option : options) {
        convertedOptions[index++] = option.toSourceOption(bucketInfo);
      }
      return convertedOptions;
    }

    static Storage.BucketGetOption[] toGetOptions(BucketInfo bucketInfo,
        BucketSourceOption... options) {
      Storage.BucketGetOption[] convertedOptions = new Storage.BucketGetOption[options.length];
      int index = 0;
      for (BucketSourceOption option : options) {
        convertedOptions[index++] = option.toGetOption(bucketInfo);
      }
      return convertedOptions;
    }
  }

  /**
   * Class for specifying blob target options when {@code Bucket} methods are used.
   */
  public static class BlobTargetOption extends Option {

    private static final Function<BlobTargetOption, StorageRpc.Option> TO_ENUM =
        new Function<BlobTargetOption, StorageRpc.Option>() {
          @Override
          public StorageRpc.Option apply(BlobTargetOption blobTargetOption) {
            return blobTargetOption.rpcOption();
          }
        };
    private static final long serialVersionUID = 8345296337342509425L;

    private BlobTargetOption(StorageRpc.Option rpcOption, Object value) {
      super(rpcOption, value);
    }

    private StorageRpc.Tuple<BlobInfo, Storage.BlobTargetOption> toTargetOption(BlobInfo blobInfo) {
      BlobId blobId = blobInfo.blobId();
      switch (rpcOption()) {
        case PREDEFINED_ACL:
          return StorageRpc.Tuple.of(blobInfo,
              Storage.BlobTargetOption.predefinedAcl((Storage.PredefinedAcl) value()));
        case IF_GENERATION_MATCH:
          blobId = BlobId.of(blobId.bucket(), blobId.name(), (Long) value());
          return StorageRpc.Tuple.of(blobInfo.toBuilder().blobId(blobId).build(),
              Storage.BlobTargetOption.generationMatch());
        case IF_GENERATION_NOT_MATCH:
          blobId = BlobId.of(blobId.bucket(), blobId.name(), (Long) value());
          return StorageRpc.Tuple.of(blobInfo.toBuilder().blobId(blobId).build(),
              Storage.BlobTargetOption.generationNotMatch());
        case IF_METAGENERATION_MATCH:
          return StorageRpc.Tuple.of(blobInfo.toBuilder().metageneration((Long) value()).build(),
              Storage.BlobTargetOption.metagenerationMatch());
        case IF_METAGENERATION_NOT_MATCH:
          return StorageRpc.Tuple.of(blobInfo.toBuilder().metageneration((Long) value()).build(),
              Storage.BlobTargetOption.metagenerationNotMatch());
        default:
          throw new AssertionError("Unexpected enum value");
      }
    }

    /**
     * Returns an option for specifying blob's predefined ACL configuration.
     */
    public static BlobTargetOption predefinedAcl(Storage.PredefinedAcl acl) {
      return new BlobTargetOption(StorageRpc.Option.PREDEFINED_ACL, acl);
    }

    /**
     * Returns an option that causes an operation to succeed only if the target blob does not exist.
     * This option can not be provided together with {@link #generationMatch(long)} or
     * {@link #generationNotMatch(long)}.
     */
    public static BlobTargetOption doesNotExist() {
      return new BlobTargetOption(StorageRpc.Option.IF_GENERATION_MATCH, 0L);
    }

    /**
     * Returns an option for blob's data generation match. If this option is used the request will
     * fail if generation does not match the provided value. This option can not be provided
     * together with {@link #generationNotMatch(long)} or {@link #doesNotExist()}.
     */
    public static BlobTargetOption generationMatch(long generation) {
      return new BlobTargetOption(StorageRpc.Option.IF_GENERATION_MATCH, generation);
    }

    /**
     * Returns an option for blob's data generation mismatch. If this option is used the request
     * will fail if blob's generation matches the provided value. This option can not be provided
     * together with {@link #generationMatch(long)} or {@link #doesNotExist()}.
     */
    public static BlobTargetOption generationNotMatch(long generation) {
      return new BlobTargetOption(StorageRpc.Option.IF_GENERATION_NOT_MATCH, generation);
    }

    /**
     * Returns an option for blob's metageneration match. If this option is used the request will
     * fail if metageneration does not match the provided value. This option can not be provided
     * together with {@link #metagenerationNotMatch(long)}.
     */
    public static BlobTargetOption metagenerationMatch(long metageneration) {
      return new BlobTargetOption(StorageRpc.Option.IF_METAGENERATION_MATCH, metageneration);
    }

    /**
     * Returns an option for blob's metageneration mismatch. If this option is used the request will
     * fail if metageneration matches the provided value. This option can not be provided together
     * with {@link #metagenerationMatch(long)}.
     */
    public static BlobTargetOption metagenerationNotMatch(long metageneration) {
      return new BlobTargetOption(StorageRpc.Option.IF_METAGENERATION_NOT_MATCH, metageneration);
    }

    static StorageRpc.Tuple<BlobInfo, Storage.BlobTargetOption[]> toTargetOptions(
        BlobInfo info, BlobTargetOption... options) {
      Set<StorageRpc.Option> optionSet =
          Sets.immutableEnumSet(Lists.transform(Arrays.asList(options), TO_ENUM));
      checkArgument(!(optionSet.contains(StorageRpc.Option.IF_METAGENERATION_NOT_MATCH)
          && optionSet.contains(StorageRpc.Option.IF_METAGENERATION_MATCH)),
          "metagenerationMatch and metagenerationNotMatch options can not be both provided");
      checkArgument(!(optionSet.contains(StorageRpc.Option.IF_GENERATION_NOT_MATCH)
          && optionSet.contains(StorageRpc.Option.IF_GENERATION_MATCH)),
          "Only one option of generationMatch, doesNotExist or generationNotMatch can be provided");
      Storage.BlobTargetOption[] convertedOptions = new Storage.BlobTargetOption[options.length];
      BlobInfo targetInfo = info;
      int index = 0;
      for (BlobTargetOption option : options) {
        StorageRpc.Tuple<BlobInfo, Storage.BlobTargetOption> target =
            option.toTargetOption(targetInfo);
        targetInfo = target.x();
        convertedOptions[index++] = target.y();
      }
      return StorageRpc.Tuple.of(targetInfo, convertedOptions);
    }
  }

  /**
   * Class for specifying blob write options when {@code Bucket} methods are used.
   */
  public static class BlobWriteOption implements Serializable {

    private static final Function<BlobWriteOption, Storage.BlobWriteOption.Option> TO_ENUM =
        new Function<BlobWriteOption, Storage.BlobWriteOption.Option>() {
          @Override
          public Storage.BlobWriteOption.Option apply(BlobWriteOption blobWriteOption) {
            return blobWriteOption.option;
          }
        };
    private static final long serialVersionUID = 4722190734541993114L;

    private final Storage.BlobWriteOption.Option option;
    private final Object value;

    private StorageRpc.Tuple<BlobInfo, Storage.BlobWriteOption> toWriteOption(BlobInfo blobInfo) {
      BlobId blobId = blobInfo.blobId();
      switch (option) {
        case PREDEFINED_ACL:
          return StorageRpc.Tuple.of(blobInfo,
              Storage.BlobWriteOption.predefinedAcl((Storage.PredefinedAcl) value));
        case IF_GENERATION_MATCH:
          blobId = BlobId.of(blobId.bucket(), blobId.name(), (Long) value);
          return StorageRpc.Tuple.of(blobInfo.toBuilder().blobId(blobId).build(),
              Storage.BlobWriteOption.generationMatch());
        case IF_GENERATION_NOT_MATCH:
          blobId = BlobId.of(blobId.bucket(), blobId.name(), (Long) value);
          return StorageRpc.Tuple.of(blobInfo.toBuilder().blobId(blobId).build(),
              Storage.BlobWriteOption.generationNotMatch());
        case IF_METAGENERATION_MATCH:
          return StorageRpc.Tuple.of(blobInfo.toBuilder().metageneration((Long) value).build(),
              Storage.BlobWriteOption.metagenerationMatch());
        case IF_METAGENERATION_NOT_MATCH:
          return StorageRpc.Tuple.of(blobInfo.toBuilder().metageneration((Long) value).build(),
              Storage.BlobWriteOption.metagenerationNotMatch());
        case IF_MD5_MATCH:
          return StorageRpc.Tuple.of(blobInfo.toBuilder().md5((String) value).build(),
              Storage.BlobWriteOption.md5Match());
        case IF_CRC32C_MATCH:
          return StorageRpc.Tuple.of(blobInfo.toBuilder().crc32c((String) value).build(),
              Storage.BlobWriteOption.crc32cMatch());
        default:
          throw new AssertionError("Unexpected enum value");
      }
    }

    private BlobWriteOption(Storage.BlobWriteOption.Option option, Object value) {
      this.option = option;
      this.value = value;
    }

    @Override
    public int hashCode() {
      return Objects.hash(option, value);
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null) {
        return false;
      }
      if (!(obj instanceof BlobWriteOption)) {
        return false;
      }
      final BlobWriteOption other = (BlobWriteOption) obj;
      return this.option == other.option && Objects.equals(this.value, other.value);
    }

    /**
     * Returns an option for specifying blob's predefined ACL configuration.
     */
    public static BlobWriteOption predefinedAcl(Storage.PredefinedAcl acl) {
      return new BlobWriteOption(Storage.BlobWriteOption.Option.PREDEFINED_ACL, acl);
    }

    /**
     * Returns an option that causes an operation to succeed only if the target blob does not exist.
     * This option can not be provided together with {@link #generationMatch(long)} or
     * {@link #generationNotMatch(long)}.
     */
    public static BlobWriteOption doesNotExist() {
      return new BlobWriteOption(Storage.BlobWriteOption.Option.IF_GENERATION_MATCH, 0L);
    }

    /**
     * Returns an option for blob's data generation match. If this option is used the request will
     * fail if generation does not match the provided value. This option can not be provided
     * together with {@link #generationNotMatch(long)} or {@link #doesNotExist()}.
     */
    public static BlobWriteOption generationMatch(long generation) {
      return new BlobWriteOption(Storage.BlobWriteOption.Option.IF_GENERATION_MATCH, generation);
    }

    /**
     * Returns an option for blob's data generation mismatch. If this option is used the request
     * will fail if generation matches the provided value. This option can not be provided
     * together with {@link #generationMatch(long)} or {@link #doesNotExist()}.
     */
    public static BlobWriteOption generationNotMatch(long generation) {
      return new BlobWriteOption(Storage.BlobWriteOption.Option.IF_GENERATION_NOT_MATCH,
          generation);
    }

    /**
     * Returns an option for blob's metageneration match. If this option is used the request will
     * fail if metageneration does not match the provided value. This option can not be provided
     * together with {@link #metagenerationNotMatch(long)}.
     */
    public static BlobWriteOption metagenerationMatch(long metageneration) {
      return new BlobWriteOption(Storage.BlobWriteOption.Option.IF_METAGENERATION_MATCH,
          metageneration);
    }

    /**
     * Returns an option for blob's metageneration mismatch. If this option is used the request will
     * fail if metageneration matches the provided value. This option can not be provided together
     * with {@link #metagenerationMatch(long)}.
     */
    public static BlobWriteOption metagenerationNotMatch(long metageneration) {
      return new BlobWriteOption(Storage.BlobWriteOption.Option.IF_METAGENERATION_NOT_MATCH,
          metageneration);
    }

    /**
     * Returns an option for blob's data MD5 hash match. If this option is used the request will
     * fail if blobs' data MD5 hash does not match the provided value.
     */
    public static BlobWriteOption md5Match(String md5) {
      return new BlobWriteOption(Storage.BlobWriteOption.Option.IF_MD5_MATCH, md5);
    }

    /**
     * Returns an option for blob's data CRC32C checksum match. If this option is used the request
     * will fail if blobs' data CRC32C checksum does not match the provided value.
     */
    public static BlobWriteOption crc32cMatch(String crc32c) {
      return new BlobWriteOption(Storage.BlobWriteOption.Option.IF_CRC32C_MATCH, crc32c);
    }

    static StorageRpc.Tuple<BlobInfo, Storage.BlobWriteOption[]> toWriteOptions(
        BlobInfo info, BlobWriteOption... options) {
      Set<Storage.BlobWriteOption.Option> optionSet =
          Sets.immutableEnumSet(Lists.transform(Arrays.asList(options), TO_ENUM));
      checkArgument(!(optionSet.contains(Storage.BlobWriteOption.Option.IF_METAGENERATION_NOT_MATCH)
          && optionSet.contains(Storage.BlobWriteOption.Option.IF_METAGENERATION_MATCH)),
          "metagenerationMatch and metagenerationNotMatch options can not be both provided");
      checkArgument(!(optionSet.contains(Storage.BlobWriteOption.Option.IF_GENERATION_NOT_MATCH)
          && optionSet.contains(Storage.BlobWriteOption.Option.IF_GENERATION_MATCH)),
          "Only one option of generationMatch, doesNotExist or generationNotMatch can be provided");
      Storage.BlobWriteOption[] convertedOptions = new Storage.BlobWriteOption[options.length];
      BlobInfo writeInfo = info;
      int index = 0;
      for (BlobWriteOption option : options) {
        StorageRpc.Tuple<BlobInfo, Storage.BlobWriteOption> write = option.toWriteOption(writeInfo);
        writeInfo = write.x();
        convertedOptions[index++] = write.y();
      }
      return StorageRpc.Tuple.of(writeInfo, convertedOptions);
    }
  }

  /**
   * Builder for {@code Bucket}.
   */
  public static class Builder extends BucketInfo.Builder {
    private final Storage storage;
    private final BucketInfo.BuilderImpl infoBuilder;

    Builder(Bucket bucket) {
      this.storage = bucket.storage;
      this.infoBuilder = new BucketInfo.BuilderImpl(bucket);
    }

    @Override
    public Builder name(String name) {
      infoBuilder.name(name);
      return this;
    }

    @Override
    Builder generatedId(String generatedId) {
      infoBuilder.generatedId(generatedId);
      return this;
    }

    @Override
    Builder owner(Entity owner) {
      infoBuilder.owner(owner);
      return this;
    }

    @Override
    Builder selfLink(String selfLink) {
      infoBuilder.selfLink(selfLink);
      return this;
    }

    @Override
    public Builder versioningEnabled(Boolean enable) {
      infoBuilder.versioningEnabled(enable);
      return this;
    }

    @Override
    public Builder indexPage(String indexPage) {
      infoBuilder.indexPage(indexPage);
      return this;
    }

    @Override
    public Builder notFoundPage(String notFoundPage) {
      infoBuilder.notFoundPage(notFoundPage);
      return this;
    }

    @Override
    public Builder deleteRules(Iterable<? extends DeleteRule> rules) {
      infoBuilder.deleteRules(rules);
      return this;
    }

    @Override
    public Builder storageClass(String storageClass) {
      infoBuilder.storageClass(storageClass);
      return this;
    }

    @Override
    public Builder location(String location) {
      infoBuilder.location(location);
      return this;
    }

    @Override
    Builder etag(String etag) {
      infoBuilder.etag(etag);
      return this;
    }

    @Override
    Builder createTime(Long createTime) {
      infoBuilder.createTime(createTime);
      return this;
    }

    @Override
    Builder metageneration(Long metageneration) {
      infoBuilder.metageneration(metageneration);
      return this;
    }

    @Override
    public Builder cors(Iterable<Cors> cors) {
      infoBuilder.cors(cors);
      return this;
    }

    @Override
    public Builder acl(Iterable<Acl> acl) {
      infoBuilder.acl(acl);
      return this;
    }

    @Override
    public Builder defaultAcl(Iterable<Acl> acl) {
      infoBuilder.defaultAcl(acl);
      return this;
    }

    @Override
    public Bucket build() {
      return new Bucket(storage, infoBuilder);
    }
  }

  Bucket(Storage storage, BucketInfo.BuilderImpl infoBuilder) {
    super(infoBuilder);
    this.storage = checkNotNull(storage);
    this.options = storage.options();
  }

  /**
   * Checks if this bucket exists.
   *
   * <p>Example of checking if the bucket exists.
   * <pre> {@code
   * boolean exists = bucket.exists();
   * if (exists) {
   *   // the bucket exists
   * } else {
   *   // the bucket was not found
   * }
   * }</pre>
   *
   * @return true if this bucket exists, false otherwise
   * @throws StorageException upon failure
   */
  public boolean exists(BucketSourceOption... options) {
    int length = options.length;
    Storage.BucketGetOption[] getOptions = Arrays.copyOf(toGetOptions(this, options), length + 1);
    getOptions[length] = Storage.BucketGetOption.fields();
    return storage.get(name(), getOptions) != null;
  }

  /**
   * Fetches current bucket's latest information. Returns {@code null} if the bucket does not exist.
   *
   * <p>Example of getting the bucket's latest information, if its generation does not match the
   * {@link Bucket#metageneration()} value, otherwise a {@link StorageException} is thrown.
   * <pre> {@code
   * Bucket latestBucket = bucket.reload(BucketSourceOption.metagenerationMatch());
   * if (latestBucket == null) {
   *   // the bucket was not found
   * }
   * }</pre>
   *
   * @param options bucket read options
   * @return a {@code Bucket} object with latest information or {@code null} if not found
   * @throws StorageException upon failure
   */
  public Bucket reload(BucketSourceOption... options) {
    return storage.get(name(), toGetOptions(this, options));
  }

  /**
   * Updates the bucket's information. Bucket's name cannot be changed. A new {@code Bucket} object
   * is returned. By default no checks are made on the metadata generation of the current bucket.
   * If you want to update the information only if the current bucket metadata are at their latest
   * version use the {@code metagenerationMatch} option:
   * {@code bucket.update(BucketTargetOption.metagenerationMatch())}
   *
   * <p>Example of updating the bucket's information.
   * <pre> {@code
   * Bucket updatedBucket = bucket.toBuilder().versioningEnabled(true).build().update();
   * }</pre>
   *
   * @param options update options
   * @return a {@code Bucket} object with updated information
   * @throws StorageException upon failure
   */
  public Bucket update(BucketTargetOption... options) {
    return storage.update(this, options);
  }

  /**
   * Deletes this bucket.
   *
   * <p>Example of deleting the bucket, if its metageneration matches the
   * {@link Bucket#metageneration()} value, otherwise a {@link StorageException} is thrown.
   * <pre> {@code
   * boolean deleted = bucket.delete(BucketSourceOption.metagenerationMatch());
   * if (deleted) {
   *   // the bucket was deleted
   * } else {
   *   // the bucket was not found
   * }
   * }</pre>
   *
   * @param options bucket delete options
   * @return {@code true} if bucket was deleted, {@code false} if it was not found
   * @throws StorageException upon failure
   */
  public boolean delete(BucketSourceOption... options) {
    return storage.delete(name(), toSourceOptions(this, options));
  }

  /**
   * Returns the paginated list of {@code Blob} in this bucket.
   *
   * <p>Example of listing the blobs in the bucket.
   * <pre> {@code
   * Page<Blob> blobs = bucket.list();
   * Iterator<Blob> blobIterator = blobs.iterateAll();
   * while (blobIterator.hasNext()) {
   *   Blob blob = blobIterator.next();
   *   // do something with the blob
   * }
   * }</pre>
   *
   * @param options options for listing blobs
   * @throws StorageException upon failure
   */
  public Page<Blob> list(BlobListOption... options) {
    return storage.list(name(), options);
  }

  /**
   * Returns the requested blob in this bucket or {@code null} if not found.
   *
   * <p>Example of getting a blob in the bucket, only if its metageneration matches a value, otherwise
   * a {@link StorageException} is thrown.
   * <pre> {@code
   * String blobName = "my_blob_name";
   * long generation = 42;
   * Blob blob = bucket.get(blobName, BlobGetOption.generationMatch(generation));
   * }</pre>
   *
   * @param blob name of the requested blob
   * @param options blob search options
   * @throws StorageException upon failure
   */
  public Blob get(String blob, BlobGetOption... options) {
    return storage.get(BlobId.of(name(), blob), options);
  }

  /**
   * Returns a list of requested blobs in this bucket. Blobs that do not exist are null.
   *
   * <p>Example of getting some blobs in the bucket, using a batch request.
   * <pre> {@code
   * String blobName1 = "my_blob_name1";
   * String blobName2 = "my_blob_name2";
   * List<Blob> blobs = bucket.get(blobName1, blobName2);
   * for (Blob blob : blobs) {
   *   if (blob == null) {
   *     // the blob was not found
   *   }
   * }
   * }</pre>
   *
   * @param blobName1 first blob to get
   * @param blobName2 second blob to get
   * @param blobNames other blobs to get
   * @return an immutable list of {@code Blob} objects
   * @throws StorageException upon failure
   */
  public List<Blob> get(String blobName1, String blobName2, String... blobNames) {
    List<BlobId> blobIds = Lists.newArrayListWithCapacity(blobNames.length + 2);
    blobIds.add(BlobId.of(name(), blobName1));
    blobIds.add(BlobId.of(name(), blobName2));
    for (String blobName : blobNames) {
      blobIds.add(BlobId.of(name(), blobName));
    }
    return storage.get(blobIds);
  }

  /**
   * Returns a list of requested blobs in this bucket. Blobs that do not exist are null.
   *
   * <p>Example of getting some blobs in the bucket, using a batch request.
   * <pre> {@code
   * String blobName1 = "my_blob_name1";
   * String blobName2 = "my_blob_name2";
   * List<String> blobNames = new LinkedList<>();
   * blobNames.add(blobName1);
   * blobNames.add(blobName2);
   * List<Blob> blobs = bucket.get(blobNames);
   * for (Blob blob : blobs) {
   *   if (blob == null) {
   *     // the blob was not found
   *   }
   * }
   * }</pre>
   *
   * @param blobNames blobs to get
   * @return an immutable list of {@code Blob} objects
   * @throws StorageException upon failure
   */
  public List<Blob> get(Iterable<String> blobNames) {
    ImmutableList.Builder<BlobId> builder = ImmutableList.builder();
    for (String blobName : blobNames) {
      builder.add(BlobId.of(name(), blobName));
    }
    return storage.get(builder.build());
  }

  /**
   * Creates a new blob in this bucket. Direct upload is used to upload {@code content}.
   * For large content, {@link Blob#writer(com.google.cloud.storage.Storage.BlobWriteOption...)}
   * is recommended as it uses resumable upload. MD5 and CRC32C hashes of {@code content} are
   * computed and used for validating transferred data.
   *
   * <p>Example of creating a blob in the bucket from a byte array with a content type.
   * <pre> {@code
   * String blobName = "my_blob_name";
   * Blob blob = bucket.create(blobName, "Hello, World!".getBytes(UTF_8), "text/plain");
   * }</pre>
   *
   * @param blob a blob name
   * @param content the blob content
   * @param contentType the blob content type
   * @param options options for blob creation
   * @return a complete blob information
   * @throws StorageException upon failure
   */
  public Blob create(String blob, byte[] content, String contentType, BlobTargetOption... options) {
    BlobInfo blobInfo = BlobInfo.builder(BlobId.of(name(), blob)).contentType(contentType).build();
    StorageRpc.Tuple<BlobInfo, Storage.BlobTargetOption[]> target =
        BlobTargetOption.toTargetOptions(blobInfo, options);
    return storage.create(target.x(), content, target.y());
  }

  /**
   * Creates a new blob in this bucket. Direct upload is used to upload {@code content}.
   * For large content, {@link Blob#writer(com.google.cloud.storage.Storage.BlobWriteOption...)}
   * is recommended as it uses resumable upload.
   *
   * <p>Example of creating a blob in the bucket from an input stream with a content type.
   * <pre> {@code
   * String blobName = "my_blob_name";
   * InputStream content = new ByteArrayInputStream("Hello, World!".getBytes(UTF_8));
   * Blob blob = bucket.create(blobName, content, "text/plain");
   * }</pre>
   *
   * @param blob a blob name
   * @param content the blob content as a stream
   * @param contentType the blob content type
   * @param options options for blob creation
   * @return a complete blob information
   * @throws StorageException upon failure
   */
  public Blob create(String blob, InputStream content, String contentType,
      BlobWriteOption... options) {
    BlobInfo blobInfo = BlobInfo.builder(BlobId.of(name(), blob)).contentType(contentType).build();
    StorageRpc.Tuple<BlobInfo, Storage.BlobWriteOption[]> write =
        BlobWriteOption.toWriteOptions(blobInfo, options);
    return storage.create(write.x(), content, write.y());
  }

  /**
   * Creates a new blob in this bucket. Direct upload is used to upload {@code content}.
   * For large content, {@link Blob#writer(com.google.cloud.storage.Storage.BlobWriteOption...)}
   * is recommended as it uses resumable upload. MD5 and CRC32C hashes of {@code content} are
   * computed and used for validating transferred data.
   *
   * <p>Example of creating a blob in the bucket from a byte array.
   * <pre> {@code
   * String blobName = "my_blob_name";
   * Blob blob = bucket.create(blobName, "Hello, World!".getBytes(UTF_8));
   * }</pre>
   *
   * @param blob a blob name
   * @param content the blob content
   * @param options options for blob creation
   * @return a complete blob information
   * @throws StorageException upon failure
   */
  public Blob create(String blob, byte[] content, BlobTargetOption... options) {
    BlobInfo blobInfo = BlobInfo.builder(BlobId.of(name(), blob)).build();
    StorageRpc.Tuple<BlobInfo, Storage.BlobTargetOption[]> target =
        BlobTargetOption.toTargetOptions(blobInfo, options);
    return storage.create(target.x(), content, target.y());
  }

  /**
   * Creates a new blob in this bucket. Direct upload is used to upload {@code content}.
   * For large content, {@link Blob#writer(com.google.cloud.storage.Storage.BlobWriteOption...)}
   * is recommended as it uses resumable upload.
   *
   * <p>Example of creating a blob in the bucket from an input stream.
   * <pre> {@code
   * String blobName = "my_blob_name";
   * InputStream content = new ByteArrayInputStream("Hello, World!".getBytes(UTF_8));
   * Blob blob = bucket.create(blobName, content);
   * }</pre>
   *
   * @param blob a blob name
   * @param content the blob content as a stream
   * @param options options for blob creation
   * @return a complete blob information
   * @throws StorageException upon failure
   */
  public Blob create(String blob, InputStream content, BlobWriteOption... options) {
    BlobInfo blobInfo = BlobInfo.builder(BlobId.of(name(), blob)).build();
    StorageRpc.Tuple<BlobInfo, Storage.BlobWriteOption[]> write =
        BlobWriteOption.toWriteOptions(blobInfo, options);
    return storage.create(write.x(), content, write.y());
  }

  /**
   * Returns the ACL entry for the specified entity on this bucket or {@code null} if not found.
   *
   * @throws StorageException upon failure
   */
  public Acl getAcl(Entity entity) {
    return storage.getAcl(name(), entity);
  }

  /**
   * Deletes the ACL entry for the specified entity on this bucket.
   *
   * @return {@code true} if the ACL was deleted, {@code false} if it was not found
   * @throws StorageException upon failure
   */
  public boolean deleteAcl(Entity entity) {
    return storage.deleteAcl(name(), entity);
  }

  /**
   * Creates a new ACL entry on this bucket.
   *
   * @throws StorageException upon failure
   */
  public Acl createAcl(Acl acl) {
    return storage.createAcl(name(), acl);
  }

  /**
   * Updates an ACL entry on this bucket.
   *
   * @throws StorageException upon failure
   */
  public Acl updateAcl(Acl acl) {
    return storage.updateAcl(name(), acl);
  }

  /**
   * Lists the ACL entries for this bucket.
   *
   * @throws StorageException upon failure
   */
  public List<Acl> listAcls() {
    return storage.listAcls(name());
  }

  /**
   * Returns the default object ACL entry for the specified entity on this bucket or {@code null} if
   * not found.
   *
   * <p>Default ACLs are applied to a new blob within the bucket when no ACL was provided for that
   * blob.
   *
   * @throws StorageException upon failure
   */
  public Acl getDefaultAcl(Entity entity) {
    return storage.getDefaultAcl(name(), entity);
  }

  /**
   * Deletes the default object ACL entry for the specified entity on this bucket.
   *
   * <p>Default ACLs are applied to a new blob within the bucket when no ACL was provided for that
   * blob.
   *
   * @return {@code true} if the ACL was deleted, {@code false} if it was not found
   * @throws StorageException upon failure
   */
  public boolean deleteDefaultAcl(Entity entity) {
    return storage.deleteDefaultAcl(name(), entity);
  }

  /**
   * Creates a new default blob ACL entry on this bucket.
   *
   * <p>Default ACLs are applied to a new blob within the bucket when no ACL was provided for that
   * blob.
   *
   * @throws StorageException upon failure
   */
  public Acl createDefaultAcl(Acl acl) {
    return storage.createDefaultAcl(name(), acl);
  }

  /**
   * Updates a default blob ACL entry on this bucket.
   *
   * <p>Default ACLs are applied to a new blob within the bucket when no ACL was provided for that
   * blob.
   *
   * @throws StorageException upon failure
   */
  public Acl updateDefaultAcl(Acl acl) {
    return storage.updateDefaultAcl(name(), acl);
  }

  /**
   * Lists the default blob ACL entries for this bucket.
   *
   * <p>Default ACLs are applied to a new blob within the bucket when no ACL was provided for that
   * blob.
   *
   * @throws StorageException upon failure
   */
  public List<Acl> listDefaultAcls() {
    return storage.listDefaultAcls(name());
  }

  /**
   * Returns the bucket's {@code Storage} object used to issue requests.
   */
  public Storage storage() {
    return storage;
  }

  @Override
  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public final boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && !obj.getClass().equals(Bucket.class)) {
      return false;
    }
    Bucket other = (Bucket) obj;
    return Objects.equals(toPb(), other.toPb())
        && Objects.equals(options, other.options);
  }

  @Override
  public final int hashCode() {
    return Objects.hash(super.hashCode(), options);
  }

  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    this.storage = options.service();
  }

  static Bucket fromPb(Storage storage, com.google.api.services.storage.model.Bucket bucketPb) {
    return new Bucket(storage, new BucketInfo.BuilderImpl(BucketInfo.fromPb(bucketPb)));
  }
}
