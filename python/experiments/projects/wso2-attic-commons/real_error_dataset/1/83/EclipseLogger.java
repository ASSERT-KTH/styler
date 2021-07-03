/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.wsdl.validation.internal.eclipse;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.wsdl.validation.internal.logging.ILogger;

/**
 * A logger that will log to the log file in the Eclipse metadata directory.
 */
public class EclipseLogger implements ILogger
{

	
  /* (non-Javadoc)
   * @see org.eclipse.wst.wsdl.validation.internal.logging.ILogger#log(java.lang.String, int)
   */
  public void log(String message, int severity) 
  {
    log(message, severity, null);	
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.wsdl.validation.internal.logging.ILogger#log(java.lang.String, int, java.lang.Throwable)
   */
  public void log(String message, int severity, Throwable throwable)
  {
	// Don't log verbose information in the Eclipse log.
	if(severity == ILogger.SEV_VERBOSE)
	{
	  return;
	}
	int status = IStatus.INFO;
	if(severity == ILogger.SEV_WARNING)
	{
	  status = IStatus.WARNING;
	}
	else if(severity == ILogger.SEV_ERROR)
	{
	  status = IStatus.ERROR;
	}
    ValidateWSDLPlugin.getInstance().getLog().log(new Status(status, ValidateWSDLPlugin.getInstance().getBundle().getSymbolicName(), IStatus.OK, message, throwable));
  }
}
