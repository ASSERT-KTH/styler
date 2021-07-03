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

package org.eclipse.wst.wsdl.validation.internal;

import org.eclipse.wst.wsdl.validation.internal.logging.ILogger;
import org.eclipse.wst.wsdl.validation.internal.logging.LoggerFactory;



/**
 * A delegate holds a validator's information and can instantiate it
 * when requested to.
 */
public class ClassloaderWSDLValidatorDelegate extends WSDLValidatorDelegate
{
  private String validatorClassname = null;
  private ClassLoader classLoader = null;

  /**
   * Create a delegate for a validator by its class name.
   * 
   * @param validatorClassname The name of the validator class.
   */
  public ClassloaderWSDLValidatorDelegate(String validatorClassname)
  {
    this.validatorClassname = validatorClassname;
  }

  /**
   * Create a delegate for a validator by its class name and 
   * a class loader to load the validator.
   * 
   * @param validatorClassname The name of the validator class.
   * @param classLoader The class loader to use to load the validator.
   */
  public ClassloaderWSDLValidatorDelegate(String validatorClassname, ClassLoader classLoader)
  {
    this(validatorClassname);
    this.classLoader = classLoader;
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.wsdl.validation.internal.WSDLValidatorDelegate#loadValidator()
   */
  protected IWSDLValidator loadValidator()
  {
	IWSDLValidator validator = null;
    if(classLoader == null)
    {
      classLoader = getClass().getClassLoader();
    }
    try
    {
      Class validatorClass =
      classLoader != null ? classLoader.loadClass(validatorClassname) : Class.forName(validatorClassname);

      validator = (IWSDLValidator)validatorClass.newInstance();
    }
    catch(Throwable t)
    {
    	LoggerFactory.getInstance().getLogger().log("Unable to load validator " + validatorClassname, ILogger.SEV_ERROR, t);
    }
    return validator;
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.wst.wsdl.validation.internal.WSDLValidatorDelegate#getValidatorName()
   */
  public String getValidatorName()
  {
    return validatorClassname;
  }
}
