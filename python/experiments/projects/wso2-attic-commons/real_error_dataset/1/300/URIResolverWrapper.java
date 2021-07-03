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

package org.eclipse.wst.wsdl.validation.internal.eclipse;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverPlugin;
import org.eclipse.wst.wsdl.validation.internal.resolver.IExtensibleURIResolver;
import org.eclipse.wst.wsdl.validation.internal.resolver.IURIResolutionResult;

/**
 * An wrapper URI resolver that wraps the Web Standard Tools URI resolver
 * in a WSDL validator URI resolver.
 */
public class URIResolverWrapper implements IExtensibleURIResolver
{
  /**
   * Constructor.
   */
  public URIResolverWrapper()
  {
  }

  /**
   * @see org.eclipse.wst.wsdl.validation.internal.resolver.IExtensibleURIResolver#resolve(java.lang.String, java.lang.String, java.lang.String, org.eclipse.wst.wsdl.validation.internal.resolver.IURIResolutionResult)
   */
  public void resolve(String baseLocation, String publicId, String systemId, IURIResolutionResult result)
  {
    URIResolver resolver = URIResolverPlugin.createResolver();
    String resolvedSystemId = resolvePlatformURL(systemId);
    String location = null;
    if (publicId != null || resolvedSystemId != null)
    {  
      location = resolver.resolve(baseLocation, publicId, resolvedSystemId);
    }  
    
    if (location != null)
    {       
      result.setLogicalLocation(location);
      String physical = resolver.resolvePhysicalLocation(baseLocation, publicId, location);
      if(physical != null)
      {
        result.setPhysicalLocation(physical);
      }
      else
      {
        result.setPhysicalLocation(location);
      }
    }
  }
  
  /**
   * Resolve platform URLs to standard URLs. This method has the
   * practical effect of transforming a URL such as
   * platform:/resource/test/test.wsdl
   * into
   * file:/c:/wtp/workspace/test/test.wsdl
   * 
   * @param url The URL that may contain the platform scheme.
   * @return A resolved URL that does not contain the platform scheme.
   */
  protected String resolvePlatformURL(String url)
  {
	  String result = url;
	  if(url != null && url.startsWith("platform:"))
      {
    	  try
    	  {
    		  URL fileURL = FileLocator.toFileURL(new URL(url));
    		  result = fileURL.toExternalForm();
    	  }
    	  catch(Exception e)
    	  {
    		  // Can't resolve using the FileLocator in this
    		  // case so do nothing.
    	  }
      }
	  return result;
  }
}
