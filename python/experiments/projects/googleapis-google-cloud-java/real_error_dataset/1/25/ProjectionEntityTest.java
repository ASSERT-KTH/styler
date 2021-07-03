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

package com.google.gcloud.datastore;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ProjectionEntityTest {

  private static final Key KEY = Key.builder("ds1", "k1", "n1").build();
  private static final StringValue STRING_INDEX_VALUE = StringValue.builder("foo").meaning(18).build();
  private static final BlobValue BLOB_VALUE = BlobValue.of(Blob.copyFrom(new byte[]{1}));
  private static final DateTimeValue DATE_TIME_VALUE = DateTimeValue.of(DateTime.now());
  private static final LongValue LONG_INDEX_VALUE =
      LongValue.builder(DATE_TIME_VALUE.get().timestampMicroseconds()).meaning(18).build();
  private static final ProjectionEntity ENTITY1 =
      new ProjectionEntity.Builder().key(KEY).set("a", "b").build();
  private static final ProjectionEntity ENTITY2 = new ProjectionEntity.Builder()
      .set("a", STRING_INDEX_VALUE)
      .set("b", BLOB_VALUE)
      .set("c", DATE_TIME_VALUE)
      .set("d", LONG_INDEX_VALUE)
      .build();

  @Test
  public void testHasKey() throws Exception {
    assertTrue(ENTITY1.hasKey());
    assertFalse(ENTITY2.hasKey());
  }

  @Test
  public void testKey() throws Exception {
    assertEquals(KEY, ENTITY1.key());
    assertNull(ENTITY2.key());
  }

  @Test
  public void testGetBlob() throws Exception {
    assertArrayEquals(STRING_INDEX_VALUE.get().getBytes(), ENTITY2.getBlob("a").toByteArray());
    assertEquals(BLOB_VALUE.get(), ENTITY2.getBlob("b"));
  }

  @Test
  public void testGetDateTime() throws Exception {
    assertEquals(DATE_TIME_VALUE.get(), ENTITY2.getDateTime("c"));
    assertEquals(DATE_TIME_VALUE.get(), ENTITY2.getDateTime("d"));
  }
}
