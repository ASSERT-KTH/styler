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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMText;
import org.genxdm.NodeKind;

/**
 * A bunch of helper functions for supporting Axiom integration.
 */
final public class AxiomSupport
{
	public static OMAttribute dynamicDowncastAttribute(final Object node)
	{
		if (node instanceof OMAttribute)
		{
			return staticDowncastAttribute(node);
		}
		else
		{
			return null;
		}
	}

	public static OMComment dynamicDowncastComment(final Object node)
	{
		if (node instanceof OMComment)
		{
			return staticDowncastComment(node);
		}
		else
		{
			return null;
		}
	}

	public static OMContainer dynamicDowncastContainer(final Object node)
	{
		if (node instanceof OMContainer)
		{
			return staticDowncastContainer(node);
		}
		else
		{
			return null;
		}
	}

	public static OMDocument dynamicDowncastDocument(final Object node)
	{
		if (node instanceof OMDocument)
		{
			return staticDowncastDocument(node);
		}
		else
		{
			return null;
		}
	}

	public static OMElement dynamicDowncastElement(final Object node)
	{
		if (node instanceof OMElement)
		{
			return staticDowncastElement(node);
		}
		else
		{
			return null;
		}
	}

	public static FauxNamespace dynamicDowncastFauxNamespace(final Object node)
	{
		if (node instanceof FauxNamespace)
		{
			return staticDowncastFauxNamespace(node);
		}
		else
		{
			return null;
		}
	}

	public static OMNamespace dynamicDowncastNamespace(final Object node)
	{
		if (node instanceof OMNamespace)
		{
			return staticDowncastNamespace(node);
		}
		else
		{
			return null;
		}
	}

	public static OMNode dynamicDowncastNode(final Object node)
	{
		if (node instanceof OMNode)
		{
			return staticDowncastNode(node);
		}
		else
		{
			return null;
		}
	}

	public static OMProcessingInstruction dynamicDowncastProcessingInstruction(final Object node)
	{
		if (node instanceof OMProcessingInstruction)
		{
			return staticDowncastProcessingInstruction(node);
		}
		else
		{
			return null;
		}
	}

	public static OMText dynamicDowncastText(final Object node)
	{
		if (node instanceof OMText)
		{
			return staticDowncastText(node);
		}
		else
		{
			return null;
		}
	}

	public static NodeKind getNodeKind(final Object origin)
	{
		if (null != dynamicDowncastElement(origin))
		{
			return NodeKind.ELEMENT;
		}
		if (null != dynamicDowncastText(origin))
		{
			return NodeKind.TEXT;
		}
		if (null != dynamicDowncastAttribute(origin))
		{
			return NodeKind.ATTRIBUTE;
		}
		if (null != dynamicDowncastNamespace(origin))
		{
			return NodeKind.NAMESPACE;
		}
		if (null != dynamicDowncastDocument(origin))
		{
			return NodeKind.DOCUMENT;
		}
		if (null != dynamicDowncastProcessingInstruction(origin))
		{
			return NodeKind.PROCESSING_INSTRUCTION;
		}
		if (null != dynamicDowncastComment(origin))
		{
			return NodeKind.COMMENT;
		}
		return null;
	}

	public static OMContainer getParent(final Object origin)
	{
		// Some work required here...
		{
			final OMElement element = dynamicDowncastElement(origin);
			if (null != element)
			{
				return element.getParent();
			}
		}
		{
			final OMText text = dynamicDowncastText(origin);
			if (null != text)
			{
				return text.getParent();
			}
		}
		{
			final FauxNamespace namespace = dynamicDowncastFauxNamespace(origin);
			if (null != namespace)
			{
				return namespace.getParent();
			}
		}
		// We have to check for document first because some implementations
		// implement OMNode but don't support getParent.
		final OMDocument document = dynamicDowncastDocument(origin);
		if (null != document)
		{
			return null;
		}

		final OMNode node = dynamicDowncastNode(origin);
		if (null != node)
		{
			return node.getParent();
		}
		else
		{
			final OMAttribute attribute = dynamicDowncastAttribute(origin);
			if (null != attribute)
			{
				return attribute.getOwner();
			}
			else
			{
				return null;
			}
		}
	}

	public static OMAttribute staticDowncastAttribute(final Object node)
	{
		return (OMAttribute)node;
	}

	public static OMComment staticDowncastComment(final Object node)
	{
		return (OMComment)node;
	}

	public static OMContainer staticDowncastContainer(final Object node)
	{
		return (OMContainer)node;
	}

	public static OMDocument staticDowncastDocument(final Object node)
	{
		return (OMDocument)node;
	}

	public static OMElement staticDowncastElement(final Object node)
	{
		return (OMElement)node;
	}

	public static FauxNamespace staticDowncastFauxNamespace(final Object node)
	{
		return (FauxNamespace)node;
	}

	public static OMNamespace staticDowncastNamespace(final Object node)
	{
		return (OMNamespace)node;
	}

	public static OMNode staticDowncastNode(final Object node)
	{
		return (OMNode)node;
	}

	public static OMProcessingInstruction staticDowncastProcessingInstruction(final Object node)
	{
		return (OMProcessingInstruction)node;
	}

	public static OMText staticDowncastText(final Object node)
	{
		return (OMText)node;
	}
	
	public static Map<String, OMElement> getIdMap(OMDocument document)
	{
	    HashMap<String, OMElement> map = idMaps.get(document);
	    if (map == null)
	    {
	        map = new HashMap<String, OMElement>();
	        addIdMap(document, map);
	    }
	    return Collections.synchronizedMap(map);
	}
	
	public static void addIdMap(OMDocument document, HashMap<String, OMElement> map)
	{
	    synchronized(idMaps)
	    {
	        idMaps.put(document, map);
	    }
	}
	
	static final Map<OMDocument, HashMap<String, OMElement>> idMaps = new WeakHashMap<OMDocument, HashMap<String, OMElement>>();
}
