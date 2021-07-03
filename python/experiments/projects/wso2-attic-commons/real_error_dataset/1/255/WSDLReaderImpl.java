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
package org.eclipse.wst.wsdl.internal.impl.wsdl4j;


import java.io.IOException;
import java.io.InputStream;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.xml.WSDLLocator;
import javax.wsdl.xml.WSDLReader;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.wst.wsdl.internal.util.WSDLResourceFactoryImpl;
import org.eclipse.wst.wsdl.util.WSDLResourceImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;


public final class WSDLReaderImpl implements WSDLReader
{
  private String factoryImplName;

  private ExtensionRegistry extReg;

  public ExtensionRegistry getExtensionRegistry()
  {
    return extReg;
  }

  public void setExtensionRegistry(ExtensionRegistry extReg)
  {
    this.extReg = extReg;
  }

  public String getFactoryImplName()
  {
    return factoryImplName;
  }

  public void setFactoryImplName(String factoryImplName) throws UnsupportedOperationException
  {
    this.factoryImplName = factoryImplName;
  }

  public boolean getFeature(String name) throws IllegalArgumentException
  {
    return false;
  }

  public void setFeature(String name, boolean value) throws IllegalArgumentException
  {
    throw new IllegalArgumentException("Not Implemented");
  }

  /**
   * Read the WSDL document accessible via the specified
   * URI into a WSDL definition.
   *
   * @param wsdlURI a URI (can be a filename or URL) pointing to a
   * WSDL XML definition.
   * @return the definition.
   */
  public Definition readWSDL(String wsdlURI) throws WSDLException
  {
    URI uri;
    if (hasProtocol(wsdlURI))
      uri = URI.createURI(wsdlURI);
    else
      uri = URI.createFileURI(wsdlURI);

    // Create a resource set, create a wsdl resource, and load the main wsdl file into it.

    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("wsdl", new WSDLResourceFactoryImpl());

    WSDLResourceImpl wsdlMainResource = (WSDLResourceImpl)resourceSet.createResource(URI.createURI("*.wsdl"));
    wsdlMainResource.setURI(uri);

    try
    {
      InputStream inputStream = resourceSet.getURIConverter().createInputStream(uri);
      wsdlMainResource.load(inputStream, resourceSet.getLoadOptions());
      //wsdlMainResource.load(resourceSet.getLoadOptions());
    }
    catch (IOException e)
    {
      throw new WSDLException(WSDLException.INVALID_WSDL, "WSDL URI: " + wsdlURI, e);
    }

    // Return the definitions of the main resource.
    return wsdlMainResource.getDefinition();
  }

  private boolean hasProtocol(String uri)
  {
    boolean result = false;
    if (uri != null)
    {
      int index = uri.indexOf(":");
      if (index != -1 && index > 2) // assume protocol with be length 3 so that the'C' in 'C:/' is not interpreted as a protocol
      {
        result = true;
      }
    }
    return result;
  }

  /**
   * Read the WSDL document accessible via the specified
   * URI into a WSDL definition.
   *
   * @param contextURI the context in which to resolve the
   * wsdlURI, if the wsdlURI is relative. Can be null, in which
   * case it will be ignored.
   * @param wsdlURI a URI (can be a filename or URL) pointing to a
   * WSDL XML definition.
   * @return the definition.
   */
  public Definition readWSDL(String contextURI, String wsdlURI) throws WSDLException
  {
    throw new WSDLException(WSDLException.OTHER_ERROR, "Not Implemented");
  }

  /**
   * Read the specified &lt;wsdl:definitions&gt; element into a WSDL
   * definition.
   *
   * @param documentBaseURI the document base URI of the WSDL definition
   * described by the element. Will be set as the documentBaseURI
   * of the returned Definition. Can be null, in which case it
   * will be ignored.
   * @param definitionsElement the &lt;wsdl:definitions&gt; element
   * @return the definition described by the element.
   */
  public Definition readWSDL(String documentBaseURI, Element definitionsElement) throws WSDLException
  {
    throw new WSDLException(WSDLException.OTHER_ERROR, "Not Implemented");
  }

  /**
   * Read the specified WSDL document into a WSDL definition.
   *
   * @param documentBaseURI the document base URI of the WSDL definition
   * described by the document. Will be set as the documentBaseURI
   * of the returned Definition. Can be null, in which case it
   * will be ignored.
   * @param wsdlDocument the WSDL document, an XML 
   * document obeying the WSDL schema.
   * @return the definition described in the document.
   */
  public Definition readWSDL(String documentBaseURI, Document wsdlDocument) throws WSDLException
  {
    throw new WSDLException(WSDLException.OTHER_ERROR, "Not Implemented");
  }

  /**
   * Read a WSDL document into a WSDL definition.
   *
   * @param documentBaseURI the document base URI of the WSDL definition
   * described by the document. Will be set as the documentBaseURI
   * of the returned Definition. Can be null, in which case it
   * will be ignored.
   * @param inputSource an InputSource pointing to the
   * WSDL document, an XML document obeying the WSDL schema.
   * @return the definition described in the document pointed to
   * by the InputSource.
   */
  public Definition readWSDL(String documentBaseURI, InputSource inputSource) throws WSDLException
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("wsdl", new WSDLResourceFactoryImpl());
    WSDLResourceImpl wsdlMainResource = (WSDLResourceImpl)resourceSet.createResource(URI.createURI("*.wsdl"));

    try
    {
      if (documentBaseURI != null)
        wsdlMainResource.setURI(createURI(documentBaseURI));
      resourceSet.getLoadOptions().put(WSDLResourceImpl.CONTINUE_ON_LOAD_ERROR, new Boolean(false));
      wsdlMainResource.load(inputSource.getByteStream(), resourceSet.getLoadOptions());
    }
    catch (IOException ioe)
    {
      throw new WSDLException(WSDLException.INVALID_WSDL, "", ioe);
    }

    Definition definition = wsdlMainResource.getDefinition();
    if (definition != null)
      definition.setDocumentBaseURI(documentBaseURI);

    return definition;
  }

  /**
   * Read a WSDL document into a WSDL definition.
   *
   * @param locator A WSDLLocator object used to provide InputSources
   * pointing to the wsdl file.
   * @return the definition described in the document
   */
  public Definition readWSDL(WSDLLocator locator) throws WSDLException
  {
    throw new WSDLException(WSDLException.OTHER_ERROR, "Not Implemented");
  }

  private URI createURI(String uriString)
  {
    if (hasProtocol(uriString))
      return URI.createURI(uriString);
    else
      return URI.createFileURI(uriString);
  }

}
