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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMText;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.IllegalNullArgumentException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.DtdAttributeKind;
import org.genxdm.io.FragmentBuilder;

public class AxiomFragmentBuilder
    implements FragmentBuilder<Object>
{
    
    public AxiomFragmentBuilder(final OMFactory factory, boolean ignoreComments)
    {
        this.factory = PreCondition.assertNotNull(factory, "factory");
        this.ignoreComments = ignoreComments;
    }

    public void attribute(String namespaceURI, String localName, String prefix, String value, DtdAttributeKind type)
        throws GenXDMException
    {
        PreCondition.assertNotNull(namespaceURI, "namespaceURI");
        PreCondition.assertNotNull(localName, "localName");
        PreCondition.assertNotNull(prefix, "prefix");
        PreCondition.assertNotNull(value, "value");

        if (null != currentNode)
        {
            final OMElement element = AxiomSupport.dynamicDowncastElement(currentNode);
            OMNamespace namespace = element.findNamespace(namespaceURI, prefix);
            if (namespace == null)
                namespace = factory.createOMNamespace(namespaceURI, prefix);
            final OMAttribute attribute = factory.createOMAttribute(localName, namespace, value);
            if (type != null)
                attribute.setAttributeType(type.toString());
            if ( (type == DtdAttributeKind.ID) ||
                (namespaceURI.equals(XMLConstants.XML_NS_URI) &&
                 localName.equals("id")) )
            {
                Map<String, OMElement> ids = AxiomSupport.getIdMap(documentNode);
                if (ids != null) // only null if we don't have a document.  *shrug*
                    ids.put(value, element);
            }
                
            element.addAttribute(attribute);
        }
        else
        {
            final OMNamespace namespace = factory.createOMNamespace(namespaceURI, prefix);
            nodes.add(factory.createOMAttribute(localName, namespace, value));
        }
    }

    public void comment(String value)
        throws GenXDMException
    {
        prolog();
        if (!ignoreComments)
        {
            if (null != currentNode)
            {
                final OMContainer container = AxiomSupport.dynamicDowncastContainer(currentNode);
                if (null != container)
                {
                    final OMComment comment = factory.createOMComment(container, value);
                    container.addChild(comment);
                    currentNode = comment;
                }
                else
                {
                    throw new IllegalStateException("comment");
                }
            }
            else
            {
                // Axiom won't let use create a comment without a parent
                // node so we have to put it inside a document.
                final OMDocument document = factory.createOMDocument();
                currentNode = factory.createOMComment(document, value);
            }
        }
        epilog();
    }

    public void endDocument()
        throws GenXDMException
    {
        epilog();
        documentNode = null;
        if (level > 0)
            throw new IllegalStateException("Document ended with unclosed elements.");
    }

    public void endElement()
        throws GenXDMException
    {
        epilog();
    }

    public void namespace(String prefix, String namespaceURI)
        throws GenXDMException
    {
        if (currentNode != null)
        {
            final OMElement parent = (OMElement)currentNode;
            if (namespaceURI == null) {
            	namespaceURI = XMLConstants.NULL_NS_URI;
            }
            if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX))
                parent.declareDefaultNamespace(namespaceURI);
            else
                parent.declareNamespace(namespaceURI, prefix);
        }
        else
        {
            nodes.add(factory.createOMNamespace(namespaceURI.toString(), prefix));
        }
    }

    public void processingInstruction(String target, String data)
        throws GenXDMException
    {
        prolog();
        if (null != currentNode)
        {
            final OMContainer container = AxiomSupport.dynamicDowncastContainer(currentNode);
            if (null != container)
            {
                final OMProcessingInstruction pi = factory.createOMProcessingInstruction(container, target, data);
                // Note AXIOM bug: https://issues.apache.org/jira/browse/AXIOM-359
                pi.setValue(data);
                container.addChild(pi);
                currentNode = pi;
            }
            else
            {
                throw new IllegalStateException("processingInstruction");
            }
        }
        else
        {
            currentNode = factory.createOMProcessingInstruction(null, target, data);
        }
        epilog();
    }

    public void startDocument(final URI documentURI, final String docTypeDecl)
        throws GenXDMException
    {
        prolog();
        if (null == currentNode)
        {
            documentNode = factory.createOMDocument();
            currentNode = documentNode;
        }
        else
        {
            throw new IllegalStateException("A document cannot be contained by a document or element.");
        }
    }

    public void startElement(String namespaceURI, String localName, String prefix)
        throws GenXDMException
    {
        prolog();
        IllegalNullArgumentException.check(namespaceURI, "namespaceURI");
        IllegalNullArgumentException.check(localName, "localName");
        IllegalNullArgumentException.check(prefix, "prefix");
    	OMNamespace ns = factory.createOMNamespace(namespaceURI, prefix);
        if (null != currentNode)
        {
            final OMContainer container = AxiomSupport.dynamicDowncastContainer(currentNode);
            if (null != container)
            {
                final OMElement element = factory.createOMElement(localName, ns, container);
                currentNode = element;
            }
            else
            {
                throw new IllegalStateException("startElement");
            }
        }
        else
        {
            final OMElement element = factory.createOMElement(localName, ns);
            
            currentNode = element;
        }
    }

    public void text(String data)
        throws GenXDMException
    {
        prolog();
        if (currentNode != null)
        {
            final OMContainer container = AxiomSupport.dynamicDowncastContainer(currentNode);
            if (container != null)
            {
                final OMText text = factory.createOMText(data);
                container.addChild(text);
                currentNode = text;
            }
            else
            {
                throw new IllegalStateException();
            }
        }
        else
        {
            currentNode = factory.createOMText(data);
        }
        epilog();
    }

    public void close()
    {
        // TODO: implement?
    }

    public void flush()
    {
        // TODO: implement?
    }

    public List<Object> getNodes()
    {
        flush();
        return nodes;
    }
    
    public Object getNode()
    {
        if (nodes.size() > 0)
            return getNodes().get(0);
        return null;
    }

    public void reset()
    {
        nodes.clear();
        currentNode = null;
        documentNode = null;
        level = 0;
    }
    
    public OMFactory getFactory()
    {
        return factory;
    }
    
    public Object lastNodeId()
    {
        // TODO: assign to nodeId; examine AxiomModel to see how to do it.
        return nodeId;
    }

    private void epilog()
    {
        level--;
        if (level < 0)
            throw new IllegalStateException("Closed a container that was never opened.");
        if (level == 0)
        {
            nodes.add(currentNode);
            currentNode = null;
        }
        else
        {
            final OMContainer parentNode = AxiomSupport.getParent(currentNode);
            if (null != parentNode)
            {
                currentNode = parentNode;
            }
        }
    }

    private void prolog()
    {
        level++;
    }

    protected int level;
    protected final OMFactory factory;
    protected ArrayList<Object> nodes = new ArrayList<Object>();
    protected Object currentNode;
    protected Object nodeId;
    protected OMDocument documentNode;
    protected boolean ignoreComments;
}
