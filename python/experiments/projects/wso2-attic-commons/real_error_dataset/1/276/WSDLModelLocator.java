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


/**
 * An adapter interface used by {@link org.eclipse.wst.wsdl.ImportImpl to find referenced resources.
 * When the resource referenced by a wsdl import needs to be determined, 
 * the containing {@link org.eclipse.emf.ecore.resource.Resource}
 * will be {@link org.eclipse.emf.ecore.util.EcoreUtil#getRegisteredAdapter(org.eclipse.emf.ecore.resource.Resource,Object) inspected} 
 * for an adapter that implements this interface.
 * As such, you can register an adapter factory like this to tailor the algorithm used to locate the resource :
 *<pre>
 *  ResourceSet resourceSet = new ResourceSetImpl();
 *  resourceSet.getAdapterFactories().add
 *    (new AdapterFactoryImpl()
 *     {
 *       class CustomURIResolver extends AdapterImpl implements WSDLModelLocator
 *       {
 *         public String resolveURI(String baseLocation, String namespace, String location);
 *         {
 *           return null;  // Additional logic...
 *         }
 *
 *         public boolean isAdatperForType(Object type)
 *         {
 *           return type == WSDLModelLocator.class;
 *         }
 *       }
 *
 *       protected CustomURIResolver customURIResolver = new CustomURIResolver();
 *
 *       public boolean isFactoryForType(Object type)
 *       {
 *         return type == WSDLModelLocator.class;
 *       }
 *
 *       public Adapter adaptNew(Notifier target, Object type)
 *       {
 *         return customURIResolver;
 *       }
 *     });
 *</pre>
 * @see org.eclipse.emf.ecore.util.EcoreUtil#getRegisteredAdapter(org.eclipse.emf.ecore.resource.Resource,Object)
 */
public interface WSDLModelLocator
{
  public String resolveURI(String baseLocation, String namespace, String location);
}
