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

package org.eclipse.wst.wsdl.validation.internal.ui.ant;

import org.apache.tools.ant.types.DTDLocation;
import org.apache.tools.ant.types.XMLCatalog;
import org.xml.sax.InputSource;

/**
 * An implementation of the WSDL Validator's XML Catalog that uses the XML Catalog
 * from ant.
 */
public class XMLCatalogImpl extends org.eclipse.wst.wsdl.validation.internal.xml.XMLCatalog
{
  protected XMLCatalog xmlCatalog = new XMLCatalog();

  /**
   * @see org.eclipse.wst.wsdl.validation.internal.xml.XMLCatalog#addEntryToCatalog(java.lang.String, java.lang.String)
   */
  public void addEntryToCatalog(String publicId, String systemId)
  {
    DTDLocation resLoc = new DTDLocation();
    resLoc.setLocation(systemId);
    resLoc.setPublicId(publicId);
    xmlCatalog.addEntity(resLoc);
  }

  /**
   * @see org.eclipse.wst.wsdl.validation.internal.xml.XMLCatalog#resolveEntityLocation(java.lang.String, java.lang.String)
   */
  public String resolveEntityLocation(String publicId, String systemId)
  {
    String resolvedId = null;

    try
    {
      InputSource is = xmlCatalog.resolveEntity(publicId, systemId);
      if (is != null)
      {
        resolvedId = is.getSystemId();
      }
    }
    // 
    catch (Exception e)
    {
      // Do nothing if the resource can't be resolved.
    }
    // needs to return null if it can't resolve the id
    if (resolvedId != null && resolvedId.equals(""))
    {
      resolvedId = null;
    }
    return resolvedId;
  }

  /**
   * Add a configured XML Catalog to this catalog.
   * 
   * @param catalog A configured XML catalog to add to this catalog.
   */
  public void addXMLCatalog(XMLCatalog catalog)
  {
    xmlCatalog.addConfiguredXMLCatalog(catalog);
  }

}
