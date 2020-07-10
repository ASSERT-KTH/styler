/*
 * Copyright (c) 2009-2011 TIBCO Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.genxdm.bridge.axiom;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.genxdm.exceptions.PreCondition;

/**
 * This wrapper class ensures that a namespace node has a parent.
 */
final class FauxNamespace implements OMNamespace
{
	private final OMContainer parent;
	private String prefix;
	private String uri;
	private OMNamespace namespace;

	// use for namespaces that exist like xmlns, etc.
	public FauxNamespace(final String prefix, final String uri, final OMContainer root)
	{
		this.prefix = PreCondition.assertNotNull(prefix, "prefix");
		this.uri = PreCondition.assertNotNull(uri, "uri");
		this.parent = root;
	}
	
	// use to wrap namespaces so that getParent() works.
	public FauxNamespace(final OMNamespace namespace, final OMElement parent)
	{
	    this.namespace = PreCondition.assertNotNull(namespace);
	    this.parent = PreCondition.assertNotNull(parent);
	}

	public boolean equals(final String uri, final String prefix)
	{
	    if (namespace != null)
	        return namespace.equals(uri, prefix);
        return (this.uri.equals(uri) &&
                (this.prefix == null ? prefix == null :
                        this.prefix.equals(prefix)));
	}
	
	public boolean equals(Object obj)
	{
        if (!(obj instanceof FauxNamespace))
        	return false;
        
	    if (namespace != null)
	        return namespace.equals(obj);
	    FauxNamespace other = (FauxNamespace)obj;
	    if (parent != other.getParent() ) {
	    	return false;
	    }
	    
        String otherPrefix = other.getPrefix();
        return (uri.equals(other.getNamespaceURI()) &&
                (prefix == null ? otherPrefix == null :
                        prefix.equals(otherPrefix)));
	}

	@SuppressWarnings("deprecation")
	public String getName()
	{
	    if (namespace != null)
	        return namespace.getName();
	    return uri;
	}

	public String getNamespaceURI()
	{
	    if (namespace != null)
	        return namespace.getNamespaceURI();
		return uri;
	}

	public OMContainer getParent()
	{
		return parent;
	}

	public String getPrefix()
	{
	    if (namespace != null)
	        return namespace.getPrefix();
		return prefix;
	}

    public int hashCode() 
    {
    	int parentHash = parent != null ? parent.hashCode() : 0x3f3f3f3f;
        if (namespace != null)
            return namespace.hashCode() ^ parentHash;
        return parentHash ^ uri.hashCode() ^ (prefix != null ? prefix.hashCode() : 0);
    }
}
