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

package org.eclipse.wst.wsdl.validation.internal.wsdl11.xsd;

import java.io.IOException;
import java.io.StringReader;
import java.util.Hashtable;
import java.util.Set;

import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLInputSource;

/**
 * An XMLEntityResolver that allows inline schemas to resolve each other through imports.
 */
public class InlineXSDResolver implements XMLEntityResolver
{
  public static final String INLINE_SCHEMA_ID = "#inlineschema";
  protected final String FILE_PREFIX = "file:";
  protected final String XMLNS = "xmlns";
  protected Hashtable entities = new Hashtable();
//  protected String refLocation = null;
  protected XMLEntityResolver externalResolver = null;
  protected XMLInputSource referringSchemaInputSource = null;
  protected String referringSchemaNamespace = null;
  
  /**
   * Constuctor.
   */
  public InlineXSDResolver()
  {
  }

  /* (non-Javadoc)
   * @see org.apache.xerces.xni.parser.XMLEntityResolver#resolveEntity(org.apache.xerces.xni.XMLResourceIdentifier)
   */
  public XMLInputSource resolveEntity(XMLResourceIdentifier resourceIdentifier)
		 throws XNIException, IOException {
	String systemId = resourceIdentifier.getExpandedSystemId();	 
	String publicId = resourceIdentifier.getPublicId();	
	String namespace = resourceIdentifier.getNamespace();
	XMLInputSource is = null;
	String schema = null;
	if (systemId == null)
	{
	  if(publicId == null)
	  {
	  	if(namespace == null)
	  	{	
	      return null;
	  	}
	  	else
	  	{
	  		systemId = namespace;
	  	}
	  }
	  else
	  {	
	    systemId = publicId;
	  }
	}
	  
	if(referringSchemaNamespace != null && referringSchemaNamespace.equals(systemId))
	{
		if(referringSchemaInputSource!=null)
		{
			return referringSchemaInputSource;
		}
	}
	else if ((schema = (String) entities.get(systemId)) != null && !schema.equals(""))
	{
		is = new XMLInputSource(publicId, referringSchemaInputSource.getSystemId() + INLINE_SCHEMA_ID, null, new StringReader(schema),null);
	}
	
    //if(is == null)
    //{
    //	throw new IOException();
    //}
	return is;
  }

  /**
   * Add an inline schema.
   * 
   * @param targetNamespace - the target namespace of the schema
   * @param schema - a string representation of the schema
   */
  public void add(String targetNamespace, String schema)
  {
    entities.put(targetNamespace, schema);
  }
  
  /**
   * Add the referring inline schema. 
   * 
   * @param inputSource - a representation of the inline schema
   * @param namespace - the namespace of the inline schema
   */
  public void addReferringSchema(XMLInputSource inputSource, String namespace)
  {
    referringSchemaInputSource = inputSource;
    referringSchemaNamespace = namespace;
  }
  
  /**
   * Return true if the namespace corresponds to an inline schema, false otherwise.
   * 
   * @param namespace The namespace of the schema.
   * @return True if the namespace corresponds to an inline schema, false otherwise.
   */
  public boolean isInlineSchema(String namespace)
  {
    return entities.containsKey(namespace);
  }
  
  /** 
   * Get the set of the inline schema namespaces. 
   * 
   * @return The set of the inline schema namespaces. 
   */ 
  public Set getInlineSchemaNSs() 
  { 
        return entities.keySet(); 
  } 

}
