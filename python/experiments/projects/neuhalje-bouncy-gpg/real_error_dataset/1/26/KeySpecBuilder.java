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
package name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.generation;

import static java.util.Objects.requireNonNull;

import name.neuhalfen.projects.crypto.bouncycastle.openpgp.algorithms.Feature;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.algorithms.PGPCompressionAlgorithms;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.algorithms.PGPHashAlgorithms;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.algorithms.PGPSymmetricEncryptionAlgorithms;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.generation.type.KeyType;
import org.bouncycastle.bcpg.sig.Features;
import org.bouncycastle.openpgp.PGPSignatureSubpacketGenerator;

public class KeySpecBuilder implements KeySpecBuilderInterface {

  private KeyType type;
  private PGPSignatureSubpacketGenerator hashedSubPackets = new PGPSignatureSubpacketGenerator();

  KeySpecBuilder(KeyType type) {
    this.type = requireNonNull(type);
  }

  @Override
  public WithDetailedConfiguration withKeyFlags(KeyFlag... flags) {
    int val = 0;
    for (final KeyFlag f : flags) {
      val |= f.getFlag();
    }
    this.hashedSubPackets.setKeyFlags(false, val);
    return new WithDetailedConfigurationImpl();
  }

  @Override
  public WithDetailedConfiguration withDefaultKeyFlags() {
    return withKeyFlags(
        KeyFlag.CERTIFY_OTHER,
        KeyFlag.SIGN_DATA,
        KeyFlag.ENCRYPT_COMMS,
        KeyFlag.ENCRYPT_STORAGE,
        KeyFlag.AUTHENTICATION);
  }

  @Override
  public KeySpec withInheritedSubPackets() {
    return new KeySpec(type, null, true);
  }

  class WithDetailedConfigurationImpl implements WithDetailedConfiguration {

    @Deprecated
    @Override
    public WithPreferredSymmetricAlgorithms withDetailedConfiguration() {
      return new WithPreferredSymmetricAlgorithmsImpl();
    }

    @Override
    public KeySpec withDefaultAlgorithms() {
      hashedSubPackets
          .setPreferredCompressionAlgorithms(false,
              PGPCompressionAlgorithms.recommendedAlgorithmIds());
      hashedSubPackets
          .setPreferredSymmetricAlgorithms(false,
              PGPSymmetricEncryptionAlgorithms.recommendedAlgorithmIds());
      hashedSubPackets
          .setPreferredHashAlgorithms(false, PGPHashAlgorithms.recommendedAlgorithmIds());
      hashedSubPackets.setFeature(false, Features.FEATURE_MODIFICATION_DETECTION);

      return new KeySpec(
          KeySpecBuilder.this.type,
          KeySpecBuilder.this.hashedSubPackets,
          false);
    }
  }

  class WithPreferredSymmetricAlgorithmsImpl implements WithPreferredSymmetricAlgorithms {

    @Override
    public WithPreferredHashAlgorithms withPreferredSymmetricAlgorithms(
        PGPSymmetricEncryptionAlgorithms... algorithms) {
      int[] ids = new int[algorithms.length];
      for (int i = 0; i < ids.length; i++) {
        ids[i] = algorithms[i].getAlgorithmId();
      }
      KeySpecBuilder.this.hashedSubPackets.setPreferredSymmetricAlgorithms(false, ids);
      return new WithPreferredHashAlgorithmsImpl();
    }

    @Override
    public WithPreferredHashAlgorithms withDefaultSymmetricAlgorithms() {
      KeySpecBuilder.this.hashedSubPackets.setPreferredSymmetricAlgorithms(false,
          PGPSymmetricEncryptionAlgorithms.recommendedAlgorithmIds());
      return new WithPreferredHashAlgorithmsImpl();
    }

    @Override
    public WithFeatures withDefaultAlgorithms() {
      hashedSubPackets.setPreferredSymmetricAlgorithms(false,
          PGPSymmetricEncryptionAlgorithms.recommendedAlgorithmIds());
      hashedSubPackets.setPreferredCompressionAlgorithms(false,
          PGPCompressionAlgorithms.recommendedAlgorithmIds());
      hashedSubPackets.setPreferredHashAlgorithms(false,
          PGPHashAlgorithms.recommendedAlgorithmIds());
      return new WithFeaturesImpl();
    }
  }

  class WithPreferredHashAlgorithmsImpl implements WithPreferredHashAlgorithms {

    @Override
    public WithPreferredCompressionAlgorithms withPreferredHashAlgorithms(
        PGPHashAlgorithms... algorithms) {
      int[] ids = new int[algorithms.length];
      for (int i = 0; i < ids.length; i++) {
        ids[i] = algorithms[i].getAlgorithmId();
      }
      KeySpecBuilder.this.hashedSubPackets.setPreferredHashAlgorithms(false, ids);
      return new WithPreferredCompressionAlgorithmsImpl();
    }

    @Override
    public WithPreferredCompressionAlgorithms withDefaultHashAlgorithms() {
      KeySpecBuilder.this.hashedSubPackets.setPreferredHashAlgorithms(false,
          PGPHashAlgorithms.recommendedAlgorithmIds());
      return new WithPreferredCompressionAlgorithmsImpl();
    }
  }

  class WithPreferredCompressionAlgorithmsImpl implements WithPreferredCompressionAlgorithms {

    @Override
    public WithFeatures withPreferredCompressionAlgorithms(
        PGPCompressionAlgorithms... algorithms) {
      int[] ids = new int[algorithms.length];
      for (int i = 0; i < ids.length; i++) {
        ids[i] = algorithms[i].getAlgorithmId();
      }
      KeySpecBuilder.this.hashedSubPackets.setPreferredCompressionAlgorithms(false, ids);
      return new WithFeaturesImpl();
    }

    @Override
    public WithFeatures withDefaultCompressionAlgorithms() {
      KeySpecBuilder.this.hashedSubPackets.setPreferredCompressionAlgorithms(false,
          PGPCompressionAlgorithms.recommendedAlgorithmIds());
      return new WithFeaturesImpl();
    }
  }

  class WithFeaturesImpl implements WithFeatures {

    @Override
    public WithFeatures withFeature(Feature feature) {
      KeySpecBuilder.this.hashedSubPackets.setFeature(false, feature.getFeatureId());
      return this;
    }

    @Override
    public KeySpec done() {
      return new KeySpec(
          KeySpecBuilder.this.type,
          hashedSubPackets,
          false);
    }
  }
}
