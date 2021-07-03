/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.wsdl.validation.internal.eclipse;

import org.eclipse.wst.wsdl.validation.internal.logging.ILogger;
import org.eclipse.wst.wsdl.validation.internal.logging.LoggerFactory;
import org.eclipse.wst.wsdl.validation.internal.wsdl11.IWSDL11Validator;
import org.eclipse.wst.wsdl.validation.internal.wsdl11.WSDL11ValidatorDelegate;
import org.osgi.framework.Bundle;

public class EclipseWSDL11ValidatorDelegate extends WSDL11ValidatorDelegate
{
  private String validatorClassname = null;
  private Bundle bundle = null;

  /**
   * Create a delegate for a validator by its class name and 
   * a class loader to load the validator.
   * 
   * @param validatorClassname The name of the validator class.
   * @param bundle The bundle to use to load the validator.
   */
  public EclipseWSDL11ValidatorDelegate(String validatorClassname, Bundle bundle)
  {
	this.validatorClassname = validatorClassname;
    this.bundle = bundle;
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.wst.wsdl.validation.internal.wsdl11.WSDL11ValidatorDelegate#loadValidator()
   */
  protected IWSDL11Validator loadValidator()
  {
	IWSDL11Validator validator = null;
	if(bundle != null)
	{
	  try
	  {
        Class validatorClass = bundle.loadClass(validatorClassname);

        validator = (IWSDL11Validator)validatorClass.newInstance();
      
      }
      catch (Throwable t)
      {
    	LoggerFactory.getInstance().getLogger().log("Unable to load validator " + validatorClassname, ILogger.SEV_ERROR, t);
      }
	}
	return validator;
  }
}
