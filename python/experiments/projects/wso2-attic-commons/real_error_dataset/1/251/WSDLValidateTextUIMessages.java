/**
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *   IBM - Initial API and implementation
 * 
 */
package org.eclipse.wst.wsdl.validation.internal.ui.text;

import java.util.ResourceBundle;

/**
 * Strings used by the WSDLValidate text UI.
 * This class mimics the behaviour of the OSGI NLS class for convenience.
 */
public class WSDLValidateTextUIMessages
{
	private static final String BUNDLE_NAME = "org.eclipse.wst.wsdl.validation.internal.ui.text.wsdlvalidatetextui";//$NON-NLS-1$

	// Error messages.
	public static String _ERROR_WRONG_ARGUMENTS = "_ERROR_WRONG_ARGUMENTS";
	public static String _ERROR_LOADING_LOGGER = "_ERROR_LOADING_LOGGER";
	public static String _ERROR_UNABLE_TO_READ_FILE = "_ERROR_UNABLE_TO_READ_FILE";
	
	// Validation messages.
	public static String _UI_INFORMATION_DELIMITER = "_UI_INFORMATION_DELIMITER";
	public static String _UI_FILE_VALID = "_UI_FILE_VALID";
	public static String _UI_FILE_INVALID = "_UI_FILE_INVALID";
	public static String _UI_ERROR_MARKER = "_UI_ERROR_MARKER";
	public static String _UI_WARNING_MARKER = "_UI_WARNING_MARKER";
	public static String _UI_VALIDATION_SUMMARY = "_UI_VALIDATION_SUMMARY";

	static 
	{
		// load message values from bundle file
		ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME);
		_ERROR_WRONG_ARGUMENTS = bundle.getString(String.valueOf(_ERROR_WRONG_ARGUMENTS));
		_ERROR_LOADING_LOGGER = bundle.getString(_ERROR_LOADING_LOGGER);
		_ERROR_UNABLE_TO_READ_FILE = bundle.getString(_ERROR_UNABLE_TO_READ_FILE);
		_UI_INFORMATION_DELIMITER = bundle.getString(_UI_INFORMATION_DELIMITER);
		_UI_FILE_VALID = bundle.getString(_UI_FILE_VALID);
		_UI_FILE_INVALID = bundle.getString(_UI_FILE_INVALID);
		_UI_ERROR_MARKER = bundle.getString(_UI_ERROR_MARKER);
		_UI_WARNING_MARKER = bundle.getString(_UI_WARNING_MARKER);
		_UI_VALIDATION_SUMMARY = bundle.getString(_UI_VALIDATION_SUMMARY);
	}

	private WSDLValidateTextUIMessages() 
	{
		// cannot create new instance
	}
}
