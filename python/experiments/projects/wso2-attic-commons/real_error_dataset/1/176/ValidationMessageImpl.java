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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A validation message is created when there is information to report from
 * validating a WSDL document.
 */
public class ValidationMessageImpl implements IValidationMessage
{
  protected String message;
  protected int lineNumber;
  protected int columnNumber;
  protected String uri;
  protected int severity = SEV_ERROR;
  protected List nestedErrors;
  protected String errorKey = null;
  protected Object[] messageArguments = null;

  /**
   * Constructor.
   * 
   * @param message The validation message.
   * @param lineNumber The line where the message should be displayed.
   * @param columnNumber The column where the message should be displayed.
   * @deprecated Use contructor with URI parameter.
   */
  public ValidationMessageImpl(String message, int lineNumber, int columnNumber, int severity)
  {
    this(message, lineNumber, columnNumber, severity, null);
  }

  /**
   * Constructor.
   * Allows specifying a uri for the reference that the message refers to.
   * 
   * @param message The validation message.
   * @param lineNumber The line where the message should be displayed.
   * @param columnNumber The column where the message should be displayed.
   * @param uri The uri of the reference file for the message.
   */
  public ValidationMessageImpl(String message, int lineNumber, int columnNumber, int severity, String uri)
  {
      this(message, lineNumber, columnNumber, severity, uri, null, null);
  }
  
  /**
   * Constructor.
   * Allows specifying a uri for the reference that the message refers to.
   * 
   * @param message The validation message.
   * @param lineNumber The line where the message should be displayed.
   * @param columnNumber The column where the message should be displayed.
   * @param uri The uri of the reference file for the message.
   * @param errorKey The Xerces Error key
   * @param messageArguments The values used to "fill in the blanks" of a Xerces error Message
   */
  public ValidationMessageImpl(String message, int lineNumber, int columnNumber, int severity, String uri, String errorKey, Object[] messageArguments)
  {
    this.message = message;
    this.lineNumber = lineNumber;
    this.columnNumber = columnNumber;
    this.severity = severity;
    this.uri = uri;
    this.errorKey = errorKey;
    this.messageArguments = messageArguments;
  }

  /**
   * Returns the validation message.
   * 
   * @return The validation message.
   */
  public String getMessage()
  {
    return message;
  }

  /**
   * Returns the column number.
   * 
   * @return The column number where the message is located.
   */
  public int getColumn()
  {
    return columnNumber;
  }

  /**
   * Returns the line number.
   * 
   * @return The line number where the message is located.
   */
  public int getLine()
  {
    return lineNumber;
  }

  /**
   * returns the uri of the reference file for the validation message.
   * 
   * @return The uri of the resource that the message refers to.
   */
  public String getURI()
  {
    return uri;
  }
  /**
   * @see org.eclipse.wst.wsdl.validation.internal.IValidationMessage#getSeverity()
   */
  public int getSeverity()
  {
    return severity;
  }
  
  /**
   * Set the severity of the message.
   * 
   * @param severity The severity of the message.
   */
  public void setSeverity(int severity)
  {
    if(severity == SEV_ERROR || severity == SEV_WARNING)
    {
      this.severity = severity;
    }
  }
  
  /**
   * Add a nested validation message to this validation message.
   * 
   * @param validationMessage The validation message to add as a nested message.
   */
  public void addNestedMessage(IValidationMessage validationMessage)
  {
    if (nestedErrors == null)
    {
      nestedErrors = new ArrayList();
    }
    nestedErrors.add(validationMessage);
    int validaitonmessageSeverity = validationMessage.getSeverity();
    if(validaitonmessageSeverity == SEV_ERROR)
    {
      setSeverity(SEV_ERROR);
    }
  }

  /**
   * Get the list of nested validation messages.
   * 
   * @return The list of nested validation messages.
   */
  public List getNestedMessages()
  {
    return nestedErrors != null ? nestedErrors : Collections.EMPTY_LIST;
  }
  /**
   * @return the error key
   */
  public String getErrorKey()
  {
    return errorKey;
  }
  
  /**
   * @param errorKey the error key to set
   */
  public void setErrorKey(String errorKey)
  {
    this.errorKey = errorKey;
  }
  /**
   * @return the Xerces message arguments used to "fill in the blanks" of the messages
   */
  public Object[] getMessageArguments()
  {
    return messageArguments;
  }
}
