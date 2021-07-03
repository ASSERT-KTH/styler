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

package org.eclipse.wst.wsdl.validation.internal.wsdl11.mime;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.eclipse.wst.wsdl.validation.internal.util.MessageGenerator;
import org.eclipse.wst.wsdl.validation.internal.wsdl11.IWSDL11Validator;
import org.eclipse.wst.wsdl.validation.internal.wsdl11.IWSDL11ValidationInfo;

/**
 * The MIME validator plugs into the WSDL validator to provide
 * validation for all elements in a WSDL document within the MIME namespace.
 */
public class MIMEValidator implements IWSDL11Validator
{
  private static String MIME_RESOURCE_BUNDLE_NAME = "validatewsdlmime";
  protected MessageGenerator messagegenerator;

  public MIMEValidator()
  {
	ResourceBundle rb = ResourceBundle.getBundle(MIME_RESOURCE_BUNDLE_NAME, Locale.getDefault());
	messagegenerator = new MessageGenerator(rb);
  }
  /**
   * @see org.eclipse.wst.wsdl.validation.internal.wsdl11.IWSDL11Validator#validate(java.lang.Object, java.util.List, org.eclipse.wsdl.validate.wsdl11.IWSDL11ValidationInfo)
   */
  public void validate(Object element, List parents, IWSDL11ValidationInfo valInfo)
  {

  }
}
