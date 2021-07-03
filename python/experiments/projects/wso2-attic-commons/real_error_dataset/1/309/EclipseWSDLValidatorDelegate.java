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

import org.eclipse.wst.wsdl.validation.internal.WSDLValidatorDelegate;
import org.eclipse.wst.wsdl.validation.internal.IWSDLValidator;
import org.eclipse.wst.wsdl.validation.internal.logging.ILogger;
import org.eclipse.wst.wsdl.validation.internal.logging.LoggerFactory;
import org.osgi.framework.Bundle;


/**
 * A delegate holds a validator's information and can instantiate it
 * when requested to.
 */
public class EclipseWSDLValidatorDelegate extends WSDLValidatorDelegate
{
  private String validatorClassname = null;
  private Bundle bundle = null;

  /**
   * Create a delegate for a validator by its class name and 
   * an OSGI bundle to load the validator.
   * 
   * @param validatorClassname The name of the validator class.
   * @param bundle The OSGI bundle to use to load the validator.
   */
  public EclipseWSDLValidatorDelegate(String validatorClassname, Bundle bundle)
  {
	this.validatorClassname = validatorClassname;
    this.bundle = bundle;
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.wsdl.validation.internal.AbstractWSDLValidatorDelegate#loadValidator()
   */
  protected IWSDLValidator loadValidator()
  {
	IWSDLValidator validator = null;
    if(bundle != null)
    {
      try
      {
    	Class validatorClass = bundle.loadClass(validatorClassname);

        validator = (IWSDLValidator)validatorClass.newInstance();
      }
      catch(Throwable t)
      {
    	LoggerFactory.getInstance().getLogger().log("Unable to load validator " + validatorClassname, ILogger.SEV_ERROR, t);
      }
    }
    return validator;
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.wst.wsdl.validation.internal.AbstractWSDLValidatorDelegate#getValidatorName()
   */
  public String getValidatorName()
  {
    return validatorClassname;
  }
}
