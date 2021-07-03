/* DigiDoc4J library
 *
 * This software is released under either the GNU Library General Public
 * License (see LICENSE.LGPL).
 *
 * Note that the only valid version of the LGPL license as far as this
 * project is concerned is the original GNU Library General Public License
 * Version 2.1, February 1999
 */

package org.digidoc4j.impl.asic.asice.bdoc;

import eu.europa.esig.dss.model.Policy;
import org.digidoc4j.Configuration;
import org.digidoc4j.DataFile;
import org.digidoc4j.SignatureParameters;
import org.digidoc4j.SignatureProfile;
import org.digidoc4j.impl.asic.asice.AsicESignatureFinalizer;
import org.digidoc4j.utils.PolicyUtils;

import java.util.List;

/**
 * BDoc signature finalizer for datafiles signing process.
 */
public class BDocSignatureFinalizer extends AsicESignatureFinalizer {

  public BDocSignatureFinalizer(List<DataFile> dataFilesToSign, SignatureParameters signatureParameters, Configuration configuration) {
    super(dataFilesToSign, signatureParameters, configuration);
  }

  @Override
  protected void setSignaturePolicy() {
    if (isTimeMarkProfile() || isEpesProfile()) {
      Policy signaturePolicy = determineSignaturePolicy();
      facade.setSignaturePolicy(signaturePolicy);
    }
  }

  @Override
  protected void validateSignatureCompatibility() {
    // Do nothing
  }

  private Policy determineSignaturePolicy() {
    Policy policyDefinedByUser = signatureParameters.getPolicy();
    if (policyDefinedByUser != null && PolicyUtils.areAllPolicyValuesDefined(policyDefinedByUser)) {
      return policyDefinedByUser;
    }
    return PolicyUtils.createBDocSignaturePolicy();
  }

  private boolean isTimeMarkProfile() {
    return SignatureProfile.LT_TM == signatureParameters.getSignatureProfile();
  }

  private boolean isEpesProfile() {
    return SignatureProfile.B_EPES == signatureParameters.getSignatureProfile();
  }
}
