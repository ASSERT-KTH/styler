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

import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.sax.SAXSource;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMInformationItem;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.OMXMLStreamReaderConfiguration;
import org.genxdm.exceptions.PreCondition;

// this exists so that we can attach namespaces to documents.
// in axiom, they aren't.  this faux element is only ever used
// by faux namespace.
public class FauxElement
    extends Object
    implements OMElement
{
    
    public FauxElement(OMContainer root)
    {
        PreCondition.assertNotNull(root, "root");
        this.root = root;
    }

    @Override
    public OMAttribute addAttribute(OMAttribute attr)
    {
        return null;
    }

    @Override
    public OMAttribute addAttribute(String attributeName, String value, OMNamespace ns)
    {
        return null;
    }

    @Override
    public OMElement cloneOMElement()
    {
        return null;
    }

    @Override
    public OMNamespace declareDefaultNamespace(String uri)
    {
        return null;
    }

    @Override
    public OMNamespace declareNamespace(OMNamespace namespace)
    {
        return null;
    }

    @Override
    public OMNamespace declareNamespace(String uri, String prefix)
    {
        return null;
    }

    @Override
    public OMNamespace findNamespace(String uri, String prefix)
    {
        return null;
    }

    @Override
    public OMNamespace findNamespaceURI(String prefix)
    {
        return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
	public Iterator getAllAttributes()
    {
        return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
	public Iterator getAllDeclaredNamespaces()
        throws OMException
    {
        return null;
    }

    @Override
    public OMAttribute getAttribute(QName qname)
    {
        return null;
    }

    @Override
    public String getAttributeValue(QName qname)
    {
        return null;
    }

    @Override
    public OMXMLParserWrapper getBuilder()
    {
        return null;
    }

    @Override
    @SuppressWarnings("rawtypes")
	public Iterator getChildElements()
    {
        return null;
    }

    @Override
    public OMNamespace getDefaultNamespace()
    {
        return null;
    }

    @Override
    public OMElement getFirstElement()
    {
        return null;
    }

    @Override
    public int getLineNumber()
    {
        return 0;
    }

    @Override
    public String getLocalName()
    {
        return null;
    }

    @Override
    public OMNamespace getNamespace()
        throws OMException
    {
        return null;
    }
    
    @Override
    public String getNamespaceURI()
        throws OMException
    {
        return null;
    }

    @Override
    public QName getQName()
    {
        return null;
    }

    @Override
    public String getText()
    {
        return null;
    }

    @Override
    public QName getTextAsQName()
    {
        return null;
    }

    @Override
    public XMLStreamReader getXMLStreamReader()
    {
        return null;
    }

    @Override
    public XMLStreamReader getXMLStreamReaderWithoutCaching()
    {
        return null;
    }

    @Override
    public void removeAttribute(OMAttribute attr)
    {

    }

    @Override
    public QName resolveQName(String qname)
    {
        return null;
    }

    @Override
    public void setBuilder(OMXMLParserWrapper wrapper)
    {

    }

    @Override
    public void setLineNumber(int lineNumber)
    {

    }

    @Override
    public void setLocalName(String localName)
    {

    }

    @Override
    public void setNamespace(OMNamespace namespace)
    {

    }

    @Override
    public void setNamespaceWithNoFindInCurrentScope(OMNamespace namespace)
    {

    }

    @Override
    public void setText(String text)
    {

    }

    @Override
    public void setText(QName text)
    {

    }

    @Override
    public String toStringWithConsume()
        throws XMLStreamException
    {
        return null;
    }

    @Override
    public void build()
    {

    }

    @Override
    public void buildWithAttachments()
    {

    }

    @Override
    public void close(boolean build)
    {

    }

    @Override
    public OMNode detach()
        throws OMException
    {
        return null;
    }

    @Override
    public void discard()
        throws OMException
    {

    }

    @Override
    public OMNode getNextOMSibling()
        throws OMException
    {
        return null;
    }

    @Override
    public OMFactory getOMFactory()
    {
        return null;
    }

    @Override
    public OMContainer getParent()
    {
        return root;
    }

    @Override
    public OMNode getPreviousOMSibling()
    {
        return null;
    }

    @Override
    public int getType()
    {
        return 0;
    }

    @Override
    public void insertSiblingAfter(OMNode sibling)
        throws OMException
    {

    }

    @Override
    public void insertSiblingBefore(OMNode sibling)
        throws OMException
    {

    }

    @Override
    public boolean isComplete()
    {
        return false;
    }

    @Override
    public void serialize(XMLStreamWriter xmlWriter)
        throws XMLStreamException
    {

    }

    @Override
    public void serialize(OutputStream output)
        throws XMLStreamException
    {

    }

    @Override
    public void serialize(Writer writer)
        throws XMLStreamException
    {

    }

    @Override
    public void serialize(OutputStream output, OMOutputFormat format)
        throws XMLStreamException
    {

    }

    @Override
    public void serialize(Writer writer, OMOutputFormat format)
        throws XMLStreamException
    {

    }

    @Override
    public void serializeAndConsume(XMLStreamWriter xmlWriter)
        throws XMLStreamException
    {

    }

    @Override
    public void serializeAndConsume(OutputStream output)
        throws XMLStreamException
    {

    }

    @Override
    public void serializeAndConsume(Writer writer)
        throws XMLStreamException
    {

    }

    @Override
    public void serializeAndConsume(OutputStream output, OMOutputFormat format)
        throws XMLStreamException
    {

    }

    @Override
    public void serializeAndConsume(Writer writer, OMOutputFormat format)
        throws XMLStreamException
    {

    }

    @Override
    public void addChild(OMNode omNode)
    {

    }

    @SuppressWarnings("rawtypes")
    @Override
	public Iterator getChildren()
    {
        return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
	public Iterator getChildrenWithLocalName(String localName)
    {
        return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
	public Iterator getChildrenWithName(QName elementQName)
    {
        return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
	public Iterator getChildrenWithNamespaceURI(String uri)
    {
        return null;
    }

    @Override
    public OMElement getFirstChildWithName(QName elementQName)
        throws OMException
    {
        return null;
    }

    @Override
    public OMNode getFirstOMChild()
    {
        return null;
    }

    @Override
	public XMLStreamReader getXMLStreamReader(boolean arg0) 
	{
		return null;
	}

    @Override
	public void serialize(XMLStreamWriter writer, boolean cache)
			throws XMLStreamException 
	{
		// do nothing; this is not a real element
	}
	
    @Override
	public void writeTextTo(Writer writer, boolean cache)
	{
	    // do nothing, this is not a real element
	}
	
    @Override
	public Reader getTextAsStream(boolean cache)
	{
	    return null;
	}
	
    @Override
	public NamespaceContext getNamespaceContext(boolean flag)
	{
	    return null;
	}
	
    @Override
    @SuppressWarnings("rawtypes")
	public Iterator getNamespacesInScope()
	{
	    return null;
	}
	
    @Override
	public void undeclarePrefix(String prefix)
	{
	}
	
    @Override
	public SAXSource getSAXSource(boolean cache)
	{
	    return null;
	}
	
    @Override
	public XMLStreamReader getXMLStreamReader(boolean cache, OMXMLStreamReaderConfiguration config)
	{
	    return null;
	}
	
    @Override
    @SuppressWarnings("rawtypes")
	public Iterator getDescendants(boolean includeSelf)
	{
	    return null;
	}
	
    @Override
	public String getPrefix()
	{
	    return null;
	}
	
    @Override
    public OMInformationItem clone(OMCloneOptions arg0)
    {
        // will not be implemented.
        return null;
    }

    @Override
    public void removeChildren()
    {
        // will not implement.
    }
    
    private final OMContainer root;
}
