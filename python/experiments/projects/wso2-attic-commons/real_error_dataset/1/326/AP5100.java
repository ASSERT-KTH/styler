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
 * AP5100
 *
 * <context>For a candidate  non-multipart/related message in the log file, with an HTTP entity-body.</context>
 * <assertionDescription>The SOAP envelope is the exclusive payload of the HTTP entity-body.</assertionDescription>
 *
 * @author lauzond
 */
public class AP5100 extends SSBP5100 
{
	/**
     * Constructor.
	 * @param impl
	 */
	public AP5100(BaseMessageValidator impl) 
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
