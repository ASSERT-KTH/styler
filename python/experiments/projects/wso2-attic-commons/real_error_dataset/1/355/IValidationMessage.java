/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.wsdl.validation.internal;

import java.util.List;

import org.eclipse.core.runtime.QualifiedName;

/**
 * An interface for a validation message. A validation message contains information
 * for the message, its severity and where it's located.
 */
public interface IValidationMessage
{
  public static final QualifiedName ERROR_MESSAGE_MAP_QUALIFIED_NAME = new QualifiedName("org.eclipse.wsdl.validate", "errorMessageMap");
  /**
   * Severity error.
   */
  public static final int SEV_ERROR = 0;
  /**
   * Severity warning.
   */
  public static final int SEV_WARNING = 1;
  
  /**
   * Returns the URI for the file that contains the validation message.
   * 
   * @return The URI for the file that contains the validation message.
   */
  public String getURI();
  
  /**
   * Return the message for this validation message.
   * 
   * @return The message for this validation message.
   */
  public String getMessage();
  
  /**
   * Return the severity of this validation message.
   * 
   * @return The severity of this validation message.
   */
  public int getSeverity();
  
  /**
   * Return the line where this validation message is located.
   * 
   * @return The line where this validation message is located.
   */
  public int getLine();
  
  /**
   * Return the column where this validation message is located.
   * 
   * @return The column where this validation message is located.
   */
  public int getColumn();
  
  /**
   * Get the list of nested validation messages.
   * 
   * @return The list of nested validation messages.
   */
  public List getNestedMessages();
}
