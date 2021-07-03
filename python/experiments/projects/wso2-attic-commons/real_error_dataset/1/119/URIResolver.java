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

package org.eclipse.wst.wsdl.validation.internal.resolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.eclipse.wst.wsdl.validation.internal.util.LazyURLInputStream;
import org.eclipse.wst.wsdl.validation.internal.xml.XMLCatalog;

/**
 * This is the main URI resolver that calls out to all of the registered
 * external URI resolvers to locate an entity. If none of the external resolvers
 * can locate the entity the resolver will ask the internal WSDL validator XML
 * catalog to resolve the location.
 */
public class URIResolver implements IExtensibleURIResolver, XMLEntityResolver
{
  private List extURIResolversList = new ArrayList();

  /**
   * Constructor. 
   */
  public URIResolver()
  {
  }


  /**
   * Add an extension URI resolver.
   * 
   * @param uriResolver
   *          The extension URI resolver.
   */
  public void addURIResolver(IExtensibleURIResolver uriResolver)
  {
    extURIResolversList.add(uriResolver);
  }

  
  /**
   * @see org.eclipse.wst.wsdl.validation.internal.resolver.IExtensibleURIResolver#resolve(java.lang.String, java.lang.String, java.lang.String, org.eclipse.wst.wsdl.validation.internal.resolver.IURIResolutionResult)
   */
  public void resolve(String baseLocation, String publicId, String systemId, IURIResolutionResult result)
  {
    Iterator resolverIter = extURIResolversList.iterator();
    while(resolverIter.hasNext())
    {
      IExtensibleURIResolver resolver = (IExtensibleURIResolver)resolverIter.next();
      if (resolver == null)
      {
        continue;
      }
      resolver.resolve(baseLocation, publicId, systemId, result);
      if (result.getLogicalLocation() != null && !result.getPhysicalLocation().equals(systemId))
      {
        break;
      }
    }

    // If we haven't been able to locate the result yet ask the internal XML
    // catalog.
    if (result.getLogicalLocation() == null && (publicId != null || systemId != null))
    {
      String tempresult = XMLCatalog.getInstance().resolveEntityLocation(publicId, systemId);
      if(tempresult != null)
      {
    	result.setLogicalLocation(tempresult);
    	result.setPhysicalLocation(tempresult);
      }
    }
    if(result.getLogicalLocation() == null)
    {
      result.setLogicalLocation(normalize(baseLocation, systemId));
      result.setPhysicalLocation(result.getLogicalLocation());
    }
  }
  
  public IURIResolutionResult resolve(String baseLocation, String publicId, String systemId)
  {
	IURIResolutionResult result= new URIResolutionResult();
	resolve(baseLocation, publicId, systemId, result);
	return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.xerces.xni.parser.XMLEntityResolver#resolveEntity(org.apache.xerces.xni.XMLResourceIdentifier)
   */
  public XMLInputSource resolveEntity(XMLResourceIdentifier resourceIdentifier) throws XNIException, IOException
  {
    String publicId = resourceIdentifier.getPublicId();
    String systemId = resourceIdentifier.getLiteralSystemId();
    if (publicId == null || publicId.equals(""))
    {
      publicId = resourceIdentifier.getNamespace();
    }
    IURIResolutionResult result = resolve(resourceIdentifier.getBaseSystemId(), publicId, systemId);
    XMLInputSource xmlInputSource = null;
    if (result != null)
    {
      LazyURLInputStream is = new LazyURLInputStream(result.getPhysicalLocation());
      xmlInputSource = new XMLInputSource(publicId, result.getLogicalLocation(), result.getLogicalLocation(), is, null);
    }
    return xmlInputSource;
  }
  
  /**
   * Normalize the systemId. Make it absolute with respect to the
   * baseLocation if necessary.
   * 
   * @param baseLocation The base location of the file.
   * @param systemId The system id of the file.
   * @return A normalized version of the system id.
   */
  protected String normalize(String baseLocation, String systemId)
  {
  	if(systemId == null)
  	{
  	  return systemId;
  	}
  	// Try to find a scheme in the systemId.
  	int schemaLoc = systemId.indexOf(':');
  	if(schemaLoc != -1 && systemId.charAt(schemaLoc+1) == '/')
  	{
  	  // A scheme has been found. The systemId is an
  	  // absolute location so return it.
  	  return systemId;
  	}
  	if(baseLocation == null)
  	{
  	  return baseLocation;
  	}
  	
  	String result = "";
  	
  	// Ensure all slashes in the locations are /.
  	baseLocation = baseLocation.replace('\\','/');
  	systemId = systemId.replace('\\','/');
  	
  	// Remove the trailing section of the baseLocation.
	int lastSlash = baseLocation.lastIndexOf('/');
  	String tempresult = baseLocation.substring(0, lastSlash+1);
  	
  	if(systemId.startsWith("/"))
  	{
  	  systemId = systemId.substring(1);
  	}
  	
  	// Join the base location with the systemid
  	tempresult = tempresult + systemId;
  	
  	// While the relative location starts with a ../ or ./ change
  	// the result and the relative location.
  	int loc;
  	while((loc = tempresult.lastIndexOf("./")) != -1)
  	{
  	  result = tempresult.substring(loc + 2) + result;
  	  if(tempresult.charAt(loc - 1) == '.')
  	  {
  	  	if(tempresult.charAt(loc - 2) == '/')
  	  	{
  	  	  String temp = tempresult.substring(0, loc - 2);
  	  	  int loc2 = temp.lastIndexOf('/');
  	  	  if(loc2 == -1)
  	  	  {
  	  	  	// If there is no other / before this the URL must start with scheme:/../
  	  	  	result = "../" + result;
  	  	  	tempresult = tempresult.substring(0, loc - 1);
  	  	  }
  	  	  else
  	  	  {
  	  	  	// Remove the section that comes before this one from tempresult unless it's ../.
  	  	    tempresult = tempresult.substring(0, loc - 1);
  	  	    int numSectsToRemove = 1;
  	  	    
  	  	    while(tempresult.endsWith("./"))
  	  	    {
  	  	      int tempreslen = tempresult.length();
  	  	      if(tempreslen > 2 && tempresult.charAt(tempreslen -3) == '.')
  	  	      {
  	  	      	if(tempreslen > 3 && tempresult.charAt(tempreslen - 4) == '/')
  	  	      	{
  	  	      	  numSectsToRemove++;
  	  	      	  tempresult = tempresult.substring(0, tempresult.length() -3);
  	  	        }
  	  	      	else
  	  	      	{
  	  	      	  break;
  	  	      	}
  	  	      }
  	  	      else
  	  	      {
  	  	      	if(tempresult.charAt(tempresult.length() -2) == '/')
  	  	      	{
  	  	      	  tempresult = tempresult.substring(0, tempresult.length() -2);
  	  	      	}
  	  	      	else
  	  	      	{
  	  	      	  break;
  	  	      	}
  	  	      }
  	  	    }
  	  	    // Remove the sections.
  	  	    for(int i = 0; i < numSectsToRemove; i++)
  	  	    {
  	  	      String temp2 = tempresult.substring(0,tempresult.length()-1);
  	  	      int loc3 = temp2.lastIndexOf('/');
  	  	      if(loc3 == -1)
  	  	      {
  	  	      	break;
  	  	      }
  	  	      tempresult = tempresult.substring(0, loc3+1);
  	  	    }
  	  	  } 
  	  	}
  	  	else
  	  	{
  	  	  // The URI is of the form file://somedir../ so copy it as is
  	  	  String temp = tempresult.substring(0, loc - 1);
	  	  int loc2 = temp.lastIndexOf('/');
	  	  if(loc2 == -1)
	  	  {
	  	  	// The URI must look like file:../ or ../ so copy over the whole tempresult.
	  	  	result = tempresult.substring(0,loc+2) + result;
	  	  	tempresult = "";
	  	  }
	  	  else
	  	  {
	  	  	// Copy over the whole somedir../
	  	  	result = tempresult.substring(loc2 + 1, tempresult.length());
	  	    tempresult = tempresult.substring(0, loc2+1);
	  	  }
  	  	}
  	  }
  	  else
  	  {
  	    if(tempresult.charAt(loc -1 ) == '/')
	  	{
  	  	  // Result is of the form file://something/./something so remove the ./
  	      tempresult = tempresult.substring(0,loc);
	  	}
  	    else
  	    {
  	      // Result URI is of form file://somedir./ so copy over the whole section.
    	  String temp = tempresult.substring(0, loc - 1);
  	  	  int loc2 = temp.lastIndexOf('/');
  	  	  if(loc2 == -1)
  	  	  {
  	  	  	// The URI must look like file:./ or ./ so copy over the whole tempresult.
  	  	  	result = tempresult.substring(0, loc) + result;
  	  	  	tempresult = "";
  	  	  }
  	  	  else
  	  	  {
  	  	  	// Copy over the whole somedir./
  	  	  	result = tempresult.substring(loc2 + 1, tempresult.length());
  	  	    tempresult = tempresult.substring(0, loc2+1);
  	  	  }
  	    }
  	  }
  	}
  	result = tempresult + result;
  	return result;
  }

}
