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
package org.eclipse.wst.wsi.internal.core.profile.validator.impl.wsdl;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;

import javax.wsdl.Definition;

import org.eclipse.wst.wsi.internal.core.WSIException;
import org.eclipse.wst.wsi.internal.core.profile.TestAssertion;
import org.eclipse.wst.wsi.internal.core.profile.validator.EntryContext;
import org.eclipse.wst.wsi.internal.core.profile.validator.impl.AssertionProcess;
import org.eclipse.wst.wsi.internal.core.report.AssertionResult;
import org.eclipse.wst.wsi.internal.core.util.StringTokenizer;

/**
 * BP2201. 
 * <context>For a candidate Web service definition within a WSDL document with a XML declaration statement. </context>
 * <assertionDescription>The XML declaration statement uses UTF-8 or UTF-16 for the encoding. </assertionDescription>
 */
public class BP2201 extends AssertionProcess
{
  private final WSDLValidatorImpl validator;

  /**
   * @param WSDLValidatorImpl
   */
  public BP2201(WSDLValidatorImpl impl)
  {
    super(impl);
    this.validator = impl;
  }

  private final char[] OMMITED_XML_DECLARATION_DELIMITERS = new char[]{0x20,
      0x9, 0xD, 0xA, '\'', '\"'};
  private final char[] XML_DECLARATION_DELIMITERS = new char[]{'='};
  private final String UTF_8_ENCODING = "UTF-8";
  private final String UTF_16_ENCODING = "UTF-16";
  private final String ENCODING_TOKEN = "encoding";
  /*
   * (non-Javadoc)
   * 
   * @see org.wsi.test.profile.validator.impl.BaseValidatorImpl.AssertionProcess#validate(org.wsi.test.profile.TestAssertion,
   *      org.wsi.test.profile.validator.EntryContext)
   */
  public AssertionResult validate(TestAssertion testAssertion,
      EntryContext entryContext) throws WSIException
  {
    result = AssertionResult.RESULT_PASSED;
    Definition definition = (Definition) entryContext.getEntry()
        .getEntryDetail();
    String location = definition.getDocumentBaseURI();
    String xmlDecl = readXMLDeclarationStatement(location);
    if (xmlDecl == null)
    {
      result = AssertionResult.RESULT_NOT_APPLICABLE;
    } else
    {
      if (!validEncoding(xmlDecl))
      {
        result = AssertionResult.RESULT_FAILED;
        failureDetail = validator.createFailureDetail(xmlDecl, entryContext);
      }
    }
    // Return assertion result
    return validator.createAssertionResult(testAssertion, result, failureDetail);
  }
  /**
   * Reads an XML declaration statement.
   * 
   * @param location
   * @return String
   */
  private String readXMLDeclarationStatement(String location)
  {
    String result = null;
    if (location != null)
    {
      URL url = null;
      Reader reader = null;
      try
      {
        try
        {
          url = new URL(location);
        } catch (MalformedURLException e)
        {
          // we should try to access location as file
        }
        if (url != null)
        {
          reader = new InputStreamReader(url.openStream());
        } else
        {
          reader = new InputStreamReader(new FileInputStream(location));
        }
        int charCode;
        boolean end = false;
        if (reader.ready())
        {
          charCode = reader.read();
          while (reader.ready() && !(charCode == '<'))
          {
            charCode = reader.read();
          }
          char[] c = new char[4];
          reader.read(c);
          StringBuffer buf = new StringBuffer();
          if (new String(c).toLowerCase().equals("?xml"))
          {
            buf.append("<?xml");
            while (reader.ready() && !end)
            {
              charCode = reader.read();
              buf.append((char) charCode);
              end = charCode == '>';
            }
          } else
          {
            // NOTE: This result does not get propogated back!
            this.result = AssertionResult.RESULT_NOT_APPLICABLE;
            return null;
          }
          result = buf.toString();
        }
      } catch (Exception e)
      {
        //VERBOSE
        if (validator.verboseOption)
        {
          System.err.println("  [WSI2201] Exception: " + e.getMessage());
        }
      } finally
      {
        if (reader != null)
        {
          try
          {
            reader.close();
          } catch (Throwable e)
          {
          }
        }
      }
    }
    return result;
  }
  /*
   * Verify xml declaration contains utf-16 or utf-8 encoding. @param xmlDecl -
   * xml declaration @return
   */
  private boolean validEncoding(String xmlDecl)
  {
    //boolean result = false;
    boolean result = true;
    if (xmlDecl != null)
    {
      StringTokenizer st = new StringTokenizer(
          OMMITED_XML_DECLARATION_DELIMITERS, XML_DECLARATION_DELIMITERS);
      Enumeration tokens = st.parse(xmlDecl);
      boolean found = false;
      while (tokens.hasMoreElements() && !found)
      {
        String token = (String) tokens.nextElement();
        if (token.equals(ENCODING_TOKEN))
        {
          found = true;
          tokens.nextElement();
          String enc = (String) tokens.nextElement();
          result = UTF_8_ENCODING.equalsIgnoreCase(enc)
              || UTF_16_ENCODING.equalsIgnoreCase(enc);
        }
      }
    }
    return result;
  }
}
