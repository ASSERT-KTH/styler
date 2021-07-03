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
import org.eclipse.wst.wsi.internal.core.document.DocumentElement;
import org.eclipse.wst.wsi.internal.core.profile.validator.EnvelopeValidator;
import org.eclipse.wst.wsi.internal.core.profile.validator.MessageValidator;
import org.eclipse.wst.wsi.internal.core.report.AssertionResult;
import org.eclipse.wst.wsi.internal.core.report.Entry;
import org.eclipse.wst.wsi.internal.core.report.EntryContainer;
import org.eclipse.wst.wsi.internal.core.util.EntryType;
import org.eclipse.wst.wsi.internal.core.xml.XMLUtils;

/**
 * This class represents an entry in a report.
 * 
 * @version 1.0.1
 * @author Peter Brittenham  (peterbr@us.ibm.com)
 */
public class EntryImpl extends EntryResultImpl implements Entry
{
  /**
   * Entry type.
   */
  protected EntryType entryType = null;

  /**
   * Artifact name (optional).
   */
  protected String artifactName = null;

  /**
   * Reference ID.
   */
  protected String referenceID = null;

  /**
   * Parent element name (only used when entry type is "port" or "operation").
   */
  protected String parentElementName = null;

  /**
   * Entry detail object.
   */
  protected Object entryDetail = null;

  /**
   * Entry container.
   */
  protected EntryContainer entryContainer = null;

  /* (non-Javadoc)
   * @see org.wsi.test.report.Entry#getEntryType()
   */
  public EntryType getEntryType()
  {
    return this.entryType;
  }

  /* (non-Javadoc)
   * @see org.wsi.test.report.Entry#setEntryType(EntryType)
   */
  public void setEntryType(EntryType entryType)
  {
    this.entryType = entryType;
  }

  /* (non-Javadoc)
   * @see org.wsi.test.report.Entry#getArtifactName()
   */
  public String getArtifactName()
  {
    return this.artifactName;
  }

  /* (non-Javadoc)
   * @see org.wsi.test.report.Entry#setArtifactName(java.lang.String)
   */
  public void setArtifactName(String artifactName)
  {
    this.artifactName = artifactName;
  }

  /* (non-Javadoc)
   * @see org.wsi.test.report.Entry#getReferenceID()
   */
  public String getReferenceID()
  {
    return this.referenceID;
  }

  /* (non-Javadoc)
   * @see org.wsi.test.report.Entry#setReferenceID(String)
   */
  public void setReferenceID(String referenceID)
  {
    this.referenceID = referenceID;
  }

  /* (non-Javadoc)
   * @see org.wsi.test.report.Entry#getParentElementName()
   */
  public String getParentElementName()
  {
    return this.parentElementName;
  }

  /* (non-Javadoc)
   * @see org.wsi.test.report.Entry#setParentElementName(String)
   */
  public void setParentElementName(String parentElementName)
  {
    this.parentElementName = parentElementName;
  }

  /* (non-Javadoc)
   * @see org.wsi.test.report.Entry#getEntryDetail()
   */
  public Object getEntryDetail()
  {
    return this.entryDetail;
  }

  /* (non-Javadoc)
   * @see org.wsi.test.report.Entry#setEntryDetail(Object)
   */
  public void setEntryDetail(Object entryDetail)
  {
    this.entryDetail = entryDetail;
  }

  /* (non-Javadoc)
   * @see org.wsi.test.report.Entry#getEntryContainer()
   */
  public EntryContainer getEntryContainer()
  {
    return this.entryContainer;
  }

  /* (non-Javadoc)
   * @see org.wsi.test.report.Entry#setEntryContainer(org.wsi.test.report.EntryContainer)
   */
  public void setEntryContainer(EntryContainer entryContainer)
  {
    this.entryContainer = entryContainer;
  }

  /* (non-Javadoc)
   * @see org.wsi.test.report.EntryResult#addAssertionResult(AssertionResult)
   */
  public void addAssertionResult(AssertionResult assertionResult)
  {
    super.addAssertionResult(assertionResult);

    if (this.entryContainer != null)
      this.entryContainer.addAssertionResult(assertionResult);
  }

  /* (non-Javadoc)
   * @see org.wsi.test.report.Entry#getStartXMLString(String)
   */
  public String getStartXMLString(String namespaceName, boolean showLogEntry, boolean envelopeArtifactType)
  {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);

    String nsName = namespaceName;
    if ((!nsName.equals("") && (!nsName.endsWith(":"))))
      nsName += ":";

    // Create element 
    pw.print("    <" + nsName + ELEM_NAME + " ");

    if (this.entryType != null)
    {
      if (envelopeArtifactType)
	  {
      	// we use the same actual entry for both message and envelope artifacts
      	// we now need to output entry type names that actually pertain to the artifact type
      	pw.print(
                WSIConstants.ATTR_TYPE + "=\"" + getCorrespondingEnvelopeEntryTypeName(this.entryType.getTypeName()) + "\" ");
	  }
	  else
	  {
        pw.print(
          WSIConstants.ATTR_TYPE + "=\"" + this.entryType.getTypeName() + "\" ");
	  }
    }

    else if (this.artifactName != null)
    {
      pw.print(WSIConstants.ATTR_TYPE + "=\"[" + this.artifactName + "]\" ");
    }

    if (this.referenceID != null)
      pw.print(WSIConstants.ATTR_REFERENCE_ID + "=\"" + XMLUtils.xmlEscapedString(this.referenceID) + "\" ");


    // If service name was set then add it
    //if (parentElementName != null) {
    //  pw.print(" " + WSIConstants.ATTR_PARENT_ELEMENT_NAME + "=\"" + this.parentElementName + "\"");
    //}

    // ADD: Need to check for config option that specifies 
    //      that log entries should be added

    // If target is a log entry, then add reference to it
    if ((entryType != null) && (entryType.getArtifactType().isLoggable()) && (showLogEntry)) 
    {
      DocumentElement logEntry = (DocumentElement) entryDetail;
      pw.println("value=\"" + logEntry.toXMLString(WSIConstants.NS_NAME_WSI_LOG) + "\" ");
    }
   
    // End element
    pw.println(">");

    return sw.toString();
  }

  /* (non-Javadoc)
   * @see org.wsi.test.report.Entry#getStartXMLString(String)
   */
  public String getStartXMLString(String namespaceName, boolean showLogEntry)
  {
  	return getStartXMLString(namespaceName, showLogEntry, false);
  }

  /* (non-Javadoc)
   * @see org.wsi.test.report.Entry#getEndXMLString(String)
   */
  public String getEndXMLString(String namespaceName)
  {
    String nsName = namespaceName;
    if ((!nsName.equals("") && (!nsName.endsWith(":"))))
      nsName += ":";

    return "    </" + nsName + ELEM_NAME + ">";
  }

  /* (non-Javadoc)
   * @see org.wsi.test.document.DocumentElement#toXMLString(String)
   */
  public String toXMLString(String namespaceName)
  {
    // Return string
    return getStartXMLString("", false) + getEndXMLString("");
  }

  public String getCorrespondingEnvelopeEntryTypeName(String messageEntryTypeName)
  {
  	String envelopeEntryTypeName = messageEntryTypeName;
  	if (messageEntryTypeName != null)
  	{
  	if (messageEntryTypeName.equals(MessageValidator.TYPE_MESSAGE_ANY))
  		envelopeEntryTypeName = EnvelopeValidator.TYPE_ENVELOPE_ANY;
  	else if (messageEntryTypeName.equals(MessageValidator.TYPE_MESSAGE_REQUEST))
  		envelopeEntryTypeName = EnvelopeValidator.TYPE_ENVELOPE_REQUEST;
  	else if (messageEntryTypeName.equals(MessageValidator.TYPE_MESSAGE_RESPONSE))
  		envelopeEntryTypeName = EnvelopeValidator.TYPE_ENVELOPE_RESPONSE;
  	}

  	return envelopeEntryTypeName;
  }
}
