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
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.wst.wsi.internal.core.WSIConstants;
import org.eclipse.wst.wsi.internal.core.analyzer.config.AssertionResultsOption;
import org.eclipse.wst.wsi.internal.core.analyzer.config.impl.AssertionResultsOptionImpl;
import org.eclipse.wst.wsi.internal.core.profile.TestAssertion;
import org.eclipse.wst.wsi.internal.core.report.AssertionResult;
import org.eclipse.wst.wsi.internal.core.report.Entry;
import org.eclipse.wst.wsi.internal.core.report.FailureDetail;
import org.eclipse.wst.wsi.internal.core.report.PrereqFailedList;

import com.ibm.wsdl.util.xml.DOMUtils;

/**
 * Test assertion result.
 *
 * @version 1.0.1
 * @author Peter Brittenham (peterbr@us.ibm.com)
 */
public class AssertionResultImpl implements AssertionResult
{
  /**
   * Assertion result.
   */
  protected String result = null;

  /**
   * Entry.
   */
  protected Entry entry = null;

  /**
   * Profile test assertion.
   */
  protected TestAssertion testAssertion = null;

  /**
   * Failure detail messages.
   */
  protected Vector failureDetailList = null;

  /**
   * Assertion results option.
   */
  protected AssertionResultsOption assertionResultsOption =
    new AssertionResultsOptionImpl();

  /**
   * Prereq failed list.
   */
  protected PrereqFailedList prereqFailedList = null;

  /**
   * Create a new assertion result.
   */
  public AssertionResultImpl()
  {
  }

  /* (non-Javadoc)
   * @see org.wsi.test.report.AssertionResult#getResult()
   */
  public String getResult()
  {
    return this.result;
  }

  /* (non-Javadoc)
   * @see org.wsi.test.report.AssertionResult#setResult(java.lang.String)
   */
  public void setResult(String result)
  {
    this.result = result;
  }

  /* (non-Javadoc)
   * @see org.wsi.test.report.AssertionResult#getAssertion()
   */
  public TestAssertion getAssertion()
  {
    return this.testAssertion;
  }

  /* (non-Javadoc)
   * @see org.wsi.test.report.AssertionResult#setAssertion(org.wsi.test.profile.TestAssertion)
   */
  public void setAssertion(TestAssertion testAssertion)
  {
    this.testAssertion = testAssertion;
  }

  /* (non-Javadoc)
   * @see org.wsi.test.report.AssertionResult#getEntry()
   */
  public Entry getEntry()
  {
    return this.entry;
  }

  /* (non-Javadoc)
   * @see org.wsi.test.report.AssertionResult#setEntry(org.wsi.test.report.Entry)
   */
  public void setEntry(Entry entry)
  {
    this.entry = entry;
  }

  /* (non-Javadoc)
   * @see org.wsi.test.report.AssertionResult#getFailureDetailList()
   */
  public Vector getFailureDetailList()
  {
    return this.failureDetailList;
  }

  /* (non-Javadoc)
   * @see org.wsi.test.report.AssertionResult#setFailureDetailList(java.util.Vector)
   */
  public void setFailureDetailList(Vector failureDetailList)
  {
    this.failureDetailList = failureDetailList;
  }

  /* (non-Javadoc)
   * @see org.wsi.test.report.AssertionResult#getAssertionResultsOption()
   */
  public AssertionResultsOption getAssertionResultsOption()
  {
    return this.assertionResultsOption;
  }

  /* (non-Javadoc)
   * @see org.wsi.test.report.AssertionResult#setAssertionResultsOption(org.wsi.test.analyzer.config.AssertionResultsOption)
   */
  public void setAssertionResultsOption(AssertionResultsOption assertionResultsOption)
  {
    this.assertionResultsOption = assertionResultsOption;
  }

  /* (non-Javadoc)
   * @see org.wsi.test.report.AssertionResult#getPrereqFailedList()
   */
  public PrereqFailedList getPrereqFailedList()
  {
    return this.prereqFailedList;
  }

  /* (non-Javadoc)
   * @see org.wsi.test.report.AssertionResult#setPrereqFailedList(org.wsi.test.report.PrereqFailedList)
   */
  public void setPrereqFailedList(PrereqFailedList prereqFailedList)
  {
    this.prereqFailedList = prereqFailedList;
  }

  /* (non-Javadoc)
   * @see org.wsi.test.document.DocumentElement#toXMLString(java.lang.String)
   */
  public String toXMLString(String namespaceName)
  {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);

    String nsName = namespaceName;
    if ((!nsName.equals("") && (!nsName.endsWith(":"))))
      nsName += ":";

    // Create element 
    pw.print("      <" + nsName + ELEM_NAME + " ");
    pw.print(WSIConstants.ATTR_ID + "=\"" + this.testAssertion.getId() + "\" ");
    pw.println(WSIConstants.ATTR_RESULT + "=\"" + this.result + "\">");

    // ADD: Add support for additional targets

    // Add prereq failed list
    if (this.prereqFailedList != null)
    {
      pw.print(prereqFailedList.toXMLString(nsName));
    }

    // ADD: Add support for multiple languages
    // If result is failed or warning, then add failure message
    if ((result.equals(RESULT_FAILED) || result.equals(RESULT_WARNING))
      && (assertionResultsOption.getShowFailureMessage()))
    {
      pw.print(
        "        <"
          + nsName
          + WSIConstants.ELEM_FAILURE_MESSAGE
          + " xml:lang=\"en\">");
      pw.print(DOMUtils.cleanString(this.testAssertion.getFailureMessage()));
      pw.println("</" + nsName + WSIConstants.ELEM_FAILURE_MESSAGE + ">");
    }

    // ADD: Add support for multiple languages
    // If any failure detail messages were specified, then add them
    if ((failureDetailList != null)
      && (assertionResultsOption.getShowFailureDetail()))
    {
      Iterator iterator = failureDetailList.iterator();
      while (iterator.hasNext())
      {
        pw.print(((FailureDetail) iterator.next()).toXMLString(nsName));
      }
    }

    // End the element
    pw.println("      </" + nsName + ELEM_NAME + ">");

    // Return string
    return sw.toString();
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    return result;
  }
}
