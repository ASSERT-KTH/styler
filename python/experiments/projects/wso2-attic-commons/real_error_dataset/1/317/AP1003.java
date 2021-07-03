/*******************************************************************************
 * Copyright (c) 2002-2005 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.wsi.internal.core.profile.validator.impl.message;

import org.eclipse.wst.wsi.internal.core.WSIException;
import org.eclipse.wst.wsi.internal.core.profile.TestAssertion;
import org.eclipse.wst.wsi.internal.core.profile.validator.EntryContext;
import org.eclipse.wst.wsi.internal.core.profile.validator.impl.BaseMessageValidator;
import org.eclipse.wst.wsi.internal.core.report.AssertionResult;

/**
 * AP1003
 *
 * <context>For a candidate non-multipart/related message in the log file, which has a non-empty entity-body</context>
 * <assertionDescription>
 *  The logged SOAP envelope is a UTF-8 transcript of an envelope originally encoded as UTF-8 or UTF-16. 
 *  The HTTP Content-Type header's charset value is either UTF-8 or UTF-16. Looking at the messageContent 
 *  element of the logged message, either 
 *    (1) it has a BOM attribute which maps the charset value in the Content-Type header, or 
 *    (2) it has it has an XML declaration which matches the charset value in the Content-Type header, or 
 *    (3) there is no BOM attribute and no XML declaration, and the charset value is UTF-8.
 * </assertionDescription>
 *
 * @author lauzond
 */
public class AP1003 extends SSBP1003 {

	/**
	 * @param impl
	 */
	public AP1003(BaseMessageValidator impl) 
	{
		super(impl);
	}

	public AssertionResult validate(
	  TestAssertion testAssertion,
	  EntryContext entryContext)
	  throws WSIException
	{
	  // If there is a SOAP Message with Attachments, the assertion is not applicable
	  if (entryContext.getMessageEntry().isMimeContent())
	  {
	  	return validator.createAssertionResult(testAssertion, AssertionResult.RESULT_NOT_APPLICABLE, failureDetail);
	  }
      else
      {
        return super.validate(testAssertion, entryContext);
	  }
	}
}
