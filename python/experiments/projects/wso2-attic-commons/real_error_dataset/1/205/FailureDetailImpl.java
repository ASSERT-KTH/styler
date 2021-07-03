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
package org.eclipse.wst.wsi.internal.core.report.impl;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.wst.wsi.internal.core.WSIConstants;
import org.eclipse.wst.wsi.internal.core.report.FailureDetail;
import org.eclipse.wst.wsi.internal.core.xml.dom.ElementLocation;

import com.ibm.wsdl.util.xml.DOMUtils;

/**
 * This class will ...
 * 
 * @author Peter  Brittenham (peterbr@us.ibm.com)
 * @version 1.0.1
 */
public class FailureDetailImpl implements FailureDetail
{
  protected String failureMessage = null;
  protected String referenceType = null;
  protected String referenceID = null;
  protected ElementLocation elementLocation = null;

  /* (non-Javadoc)
   * @see org.wsi.test.report.FailureDetail#getFailureMessage()
   */
  public String getFailureMessage()
  {
    return this.failureMessage;
  }

  /* (non-Javadoc)
   * @see org.wsi.test.report.FailureDetail#setFailureMessage(String)
   */
  public void setFailureMessage(String failureMessage)
  {
    this.failureMessage = failureMessage;
  }

  /* (non-Javadoc)
   * @see org.wsi.test.report.FailureDetail#getReferenceType()
   */
  public String getReferenceType()
  {
    return this.referenceType;
  }

  /* (non-Javadoc)
   * @see org.wsi.test.report.FailureDetail#setReferenceType(String)
   */
  public void setReferenceType(String referenceType)
  {
    this.referenceType = referenceType;
  }

  /* (non-Javadoc)
   * @see org.wsi.test.report.FailureDetail#getReferenceID()
   */
  public String getReferenceID()
  {
    return this.referenceID;
  }

  /* (non-Javadoc)
   * @see org.wsi.test.report.FailureDetail#setReferenceIDn(String)
   */
  public void setReferenceIDn(String referenceID)
  {
    this.referenceID = referenceID;
  }

  /* (non-Javadoc)
   * @see org.wsi.test.report.FailureDetail#getElementLocation()
   */
  public ElementLocation getElementLocation()
  {
    return this.elementLocation;
  }

  /* (non-Javadoc)
   * @see org.wsi.test.report.FailureDetail#setElementLocation(ElementLocation)
   */
  public void setElementLocation(ElementLocation elementLocation)
  {
    this.elementLocation = elementLocation;
  }

  /* (non-Javadoc)
   * @see org.wsi.test.document.DocumentElement#toXMLString(String)
   */
  public String toXMLString(String namespaceName)
  {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);

    String nsName = namespaceName;
    if ((!nsName.equals("") && (!nsName.endsWith(":"))))
      nsName += ":";

    // Create element 
    pw.print("      <" + nsName + ELEM_NAME + " xml:lang=\"en\" ");

    if (this.referenceType != null)
      pw.print(
        WSIConstants.ATTR_REFERENCE_TYPE + "=\"" + this.referenceType + "\" ");

    if (this.referenceID != null)
      pw.print(
        WSIConstants.ATTR_REFERENCE_ID + "=\"" + this.referenceID + "\" ");

    // ADD: Need to use a different namespace
    /*
    if (this.elementLocation != null) {
      String nsNameExt = "reportext";
      pw.print("xmlns:" + nsNameExt + "=\"" + WSIConstants.NS_URI_WSI_REPORT + "/ext/\"");
      pw.print(nsNameExt + ":" + WSIConstants.ATTR_LINE_NUMBER + "=\"" + this.elementLocation.getLineNumber() + "\" ");
      pw.print(nsNameExt + ":" + WSIConstants.ATTR_COLUMN_NUMBER + "=\"" + this.elementLocation.getColumnNumber() + "\" ");
    }
    */

    // Close start element
    pw.print(">");

    if (this.failureMessage != null)
      pw.println(DOMUtils.cleanString(this.failureMessage));

    if (this.elementLocation != null)
    {
      pw.println(" ");
      pw.print(this.elementLocation.toString());
    }

    // End the element
    pw.println("      </" + nsName + ELEM_NAME + ">");

    // Return string
    return sw.toString();
  }

}
