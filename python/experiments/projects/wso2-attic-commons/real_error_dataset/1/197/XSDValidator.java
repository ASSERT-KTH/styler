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
package org.eclipse.wst.wsi.internal.core.wsdl.xsd;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.xerces.parsers.XMLGrammarPreparser;
import org.apache.xerces.util.XMLGrammarPoolImpl;
import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.grammars.XSGrammar;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xs.XSModel;


/**
 * Validate schemas from files or String.
 * 
 * @author Lawrence Mandel (lmandel@ca.ibm.com)
 */
public class XSDValidator
{
  private XSModel xsModel;
  private boolean isValidXSD;
  private List errors;
  private String filelocation;

  /**
   * Constructor.
   */
  public XSDValidator()
  {
    xsModel = null;
    isValidXSD = false;
    errors = new ArrayList();
  }
  /**
   * Validate an inline schema.
   * 
   * @param schema A schema represented as a string.
   * @param targetNamespace The target namespace of the schema.
   * @param fileloc The uri of the file that contains the schema.
   */
  public void validateInlineSchema(String schema, String targetNamespace, String fileloc)
  {
    validateInlineSchema(schema, targetNamespace, fileloc, null, null);
  }

  /**
   * Validate an inline schema and specify an entity resolver.
   * 
   * @param schema This schema represented as a string.
   * @param targetNamespace The target namespace of the schema.
   * @param fileloc The uri of the file that contains this schema.
   * @param entityResolverChain The entity resolver chain.
   * @param inlineSchemaEntityResolver An inline schema resolver for this schema.
   */
  public void validateInlineSchema(
    String schema,
    String targetNamespace,
    String fileloc,
    XMLEntityResolver entityResolverChain,
    XMLEntityResolver inlineSchemaEntityResolver)
  {
    filelocation = fileloc;

    validateXSD(schema, true, entityResolverChain,targetNamespace, inlineSchemaEntityResolver);
  }

  /**
   * Validate the file located at the uri specified with the given entity resolver.
   * 
   * @param uri An absolute uri for the schema location.
   * @param entityResolver An entity resolver to be used.
   */
  public void validate(String uri, XMLEntityResolver entityResolver)
  {
    validateXSD(uri, false, entityResolver, null, null);
  }

  /**
   * Validate the schema.
   * 
   * @param schema The schema or it's location.
   * @param inlineXSD True if an inline schema, false otherwise.
   * @param entityResolver An entity resolver to use.
   * @param targetNamespace The target namespace of the schema being validated.
   * @param inlineSchemaEntityResolver An inline schema entity resolver.
   */
  protected void validateXSD(String schema, boolean inlineXSD, XMLEntityResolver entityResolver, String targetNamespace, XMLEntityResolver inlineSchemaEntityResolver)
  {
    ValidateErrorHandler errorHandler = new ValidateErrorHandler();
    errorHandler.getErrorMessages().clear();
    try
    {
		XMLGrammarPreparser grammarPreparser = new XMLGrammarPreparser();
		XMLGrammarPool grammarPool = new XMLGrammarPoolImpl();
		grammarPreparser.setGrammarPool(grammarPool);
	 
      grammarPreparser.setErrorHandler(errorHandler);
      if (entityResolver != null)
      {
		grammarPreparser.setEntityResolver(entityResolver);
      }

      try
      {
		XMLInputSource is = null;
        // this allows support for the inline schema in WSDL documents
        if (inlineXSD)
        {
        	
        	Reader reader = new StringReader(schema);
			is = new XMLInputSource(null,filelocation,filelocation,reader,null);
			
			((InlineXSDResolver)inlineSchemaEntityResolver).addReferringSchema(is,targetNamespace);

        }
        // get the input source for an external schema file
        else
        {
          is = new XMLInputSource(null,schema,schema);
        }
        
        grammarPreparser.registerPreparser(XMLGrammarDescription.XML_SCHEMA,null/*schemaLoader*/);
        grammarPreparser.getLoader(XMLGrammarDescription.XML_SCHEMA);

		XSGrammar grammar = (XSGrammar)grammarPreparser.preparseGrammar(XMLGrammarDescription.XML_SCHEMA,is);
		xsModel = grammar.toXSModel();
      }
      catch (Exception e)
      {
        //parser will return null pointer exception if the document is structurally invalid
        errors.add("Schema is structurally invalid.");
      }

      errors.addAll(errorHandler.getErrorMessages());
    }
    catch (Exception e)
    {
    	System.out.println(e);
    }
    if (errors.isEmpty())
    {
      isValidXSD = true;
    }
  }

  /**
   * Returns the XSModel created.
   * 
   * @return The XSModel created.
   */

  public XSModel getXSModel()
  {
  	return xsModel;
  }
  /**
   * Returns true if the schema is valid, false otherwise.
   * 
   * @return True if the schema is valid, false otherwise.
   */
  public boolean isValid()
  {
    return isValidXSD;
  }

  /**
   * Return the error list.
   * 
   * @return A list of error from the schema.
   */
  public List getErrors()
  {
    return errors;
  }
}
