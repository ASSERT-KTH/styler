/*
 * Copyright 2018 Paul Schaub.
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
package name.neuhalfen.projects.crypto.bouncycastle.openpgp.algorithms;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.bcpg.sig.Features;

/**
 * Wraps bouncy castles org.bouncycastle.bcpg.sig.Features into an enum.
 *
 * @see Features
 */
public enum Feature {

  /**
   * Add modification detection package.
   *
   * @see <a href="https://tools.ietf.org/html/rfc4880#section-5.14">
   *     RFC-4880 §5.14: Modification Detection Code Packet</a>
   *
   *     <blockquote>The Modification Detection Code packet contains a SHA-1 hash of
   *     plaintext data, which is used to detect message modification.  It is
   *     only used with a Symmetrically Encrypted Integrity Protected Data
   *     packet.  The Modification Detection Code packet MUST be the last
   *     packet in the plaintext data that is encrypted in the Symmetrically
   *     Encrypted Integrity Protected Data packet, and MUST appear in no
   *     other place.
   *
   *     A Modification Detection Code packet MUST have a length of 20 octets.
   *
   *     The body of this packet consists of:
   *
   *     - A 20-octet SHA-1 hash of the preceding plaintext data of the
   *     Symmetrically Encrypted Integrity Protected Data packet,
   *     including prefix data, the tag octet, and length octet of the
   *     Modification Detection Code packet.
   *
   *     Note that the Modification Detection Code packet MUST always use a
   *     new format encoding of the packet tag, and a one-octet encoding of
   *     the packet length.  The reason for this is that the hashing rules for
   *     modification detection include a one-octet tag and one-octet length
   *     in the data hash.  While this is a bit restrictive, it reduces
   *     complexity.
   *     </blockquote>
   */
  MODIFICATION_DETECTION(Features.FEATURE_MODIFICATION_DETECTION),
  ;

  private static final Map<Byte, Feature> MAP = new HashMap<>();

  static {
    for (final Feature f : Feature.values()) {
      MAP.put(f.featureId, f);
    }
  }


  private final byte featureId;

  Feature(byte featureId) {
    this.featureId = featureId;
  }

  public static Feature fromId(byte id) { // NOPMD: ShortVariable
    return MAP.get(id);
  }

  public byte getFeatureId() {
    return featureId;
  }
}
