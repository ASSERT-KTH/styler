/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.wsdl.internal.util;


import java.util.Iterator;

import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.XSDSchemaExtensibilityElement;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDSchemaLocator;


public class XSDSchemaLocatorImpl extends AdapterImpl implements XSDSchemaLocator
{
  /**
   * @see org.eclipse.xsd.util.XSDSchemaLocator#locateSchema(org.eclipse.xsd.XSDSchema,
   *      java.lang.String, java.lang.String, java.lang.String)
   */
  public XSDSchema locateSchema(XSDSchema xsdSchema, String namespaceURI, String rawSchemaLocationURI, String resolvedSchemaLocationURI)
  {
    XSDSchema resolvedSchema = null;

    // Bugzilla 122624:  WSDL model does not resolve <import> of inline schema with no namespace.
    // KB: We are not resolving <import>ed or <include>d schemas from the root inline schemas.
    // In other words, since we are resolving only between multiple root inline schemas,
    // do not attempt to resolve if "rawSchemaLocationURI" is not null. 
    if (namespaceURI != null && rawSchemaLocationURI == null)
    {
      Definition definition = null;

      for (EObject parent = xsdSchema.eContainer(); parent != null; parent = parent.eContainer())
      {
        if (parent instanceof Definition)
        {
          definition = (Definition)parent;
          break;
        }
      }

      if (definition != null && definition.getETypes() != null)
      {
        for (Iterator i = definition.getETypes().getEExtensibilityElements().iterator(); i.hasNext();)
        {
          Object o = i.next();
          if (o instanceof XSDSchemaExtensibilityElement)
          {
            XSDSchema schema = ((XSDSchemaExtensibilityElement)o).getSchema();
            if (schema != null && namespaceURI.equals(schema.getTargetNamespace()))
            {
              resolvedSchema = schema;
              break;
            }
          }
        }
      }
    }
    return resolvedSchema;
  }

  public boolean isAdatperForType(Object type)
  {
    return type == XSDSchemaLocator.class;
  }
}
