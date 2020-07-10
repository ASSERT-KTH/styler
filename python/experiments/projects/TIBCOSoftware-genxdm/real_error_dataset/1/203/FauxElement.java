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
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
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

    public OMAttribute addAttribute(OMAttribute attr)
    {
        return null;
    }

    public OMAttribute addAttribute(String attributeName, String value, OMNamespace ns)
    {
        return null;
    }

    public OMElement cloneOMElement()
    {
        return null;
    }

    public OMNamespace declareDefaultNamespace(String uri)
    {
        return null;
    }

    public OMNamespace declareNamespace(OMNamespace namespace)
    {
        return null;
    }

    public OMNamespace declareNamespace(String uri, String prefix)
    {
        return null;
    }

    public OMNamespace findNamespace(String uri, String prefix)
    {
        return null;
    }

    public OMNamespace findNamespaceURI(String prefix)
    {
        return null;
    }

    @SuppressWarnings("rawtypes")
	public Iterator getAllAttributes()
    {
        return null;
    }

    @SuppressWarnings("rawtypes")
	public Iterator getAllDeclaredNamespaces()
        throws OMException
    {
        return null;
    }

    public OMAttribute getAttribute(QName qname)
    {
        return null;
    }

    public String getAttributeValue(QName qname)
    {
        return null;
    }

    public OMXMLParserWrapper getBuilder()
    {
        return null;
    }

    @SuppressWarnings("rawtypes")
	public Iterator getChildElements()
    {
        return null;
    }

    public OMNamespace getDefaultNamespace()
    {
        return null;
    }

    public OMElement getFirstElement()
    {
        return null;
    }

    public int getLineNumber()
    {
        return 0;
    }

    public String getLocalName()
    {
        return null;
    }

    public OMNamespace getNamespace()
        throws OMException
    {
        return null;
    }
    
    public String getNamespaceURI()
        throws OMException
    {
        return null;
    }

    public QName getQName()
    {
        return null;
    }

    public String getText()
    {
        return null;
    }

    public QName getTextAsQName()
    {
        return null;
    }

    public XMLStreamReader getXMLStreamReader()
    {
        return null;
    }

    public XMLStreamReader getXMLStreamReaderWithoutCaching()
    {
        return null;
    }

    public void removeAttribute(OMAttribute attr)
    {

    }

    public QName resolveQName(String qname)
    {
        return null;
    }

    public void setBuilder(OMXMLParserWrapper wrapper)
    {

    }

    public void setFirstChild(OMNode node)
    {

    }

    public void setLineNumber(int lineNumber)
    {

    }

    public void setLocalName(String localName)
    {

    }

    public void setNamespace(OMNamespace namespace)
    {

    }

    public void setNamespaceWithNoFindInCurrentScope(OMNamespace namespace)
    {

    }

    public void setText(String text)
    {

    }

    public void setText(QName text)
    {

    }

    public String toStringWithConsume()
        throws XMLStreamException
    {
        return null;
    }

    public void build()
    {

    }

    public void buildWithAttachments()
    {

    }

    public void close(boolean build)
    {

    }

    public OMNode detach()
        throws OMException
    {
        return null;
    }

    public void discard()
        throws OMException
    {

    }

    public OMNode getNextOMSibling()
        throws OMException
    {
        return null;
    }

    public OMFactory getOMFactory()
    {
        return null;
    }

    public OMContainer getParent()
    {
        return root;
    }

    public OMNode getPreviousOMSibling()
    {
        return null;
    }

    public int getType()
    {
        return 0;
    }

    public void insertSiblingAfter(OMNode sibling)
        throws OMException
    {

    }

    public void insertSiblingBefore(OMNode sibling)
        throws OMException
    {

    }

    public boolean isComplete()
    {
        return false;
    }

    public void serialize(XMLStreamWriter xmlWriter)
        throws XMLStreamException
    {

    }

    public void serialize(OutputStream output)
        throws XMLStreamException
    {

    }

    public void serialize(Writer writer)
        throws XMLStreamException
    {

    }

    public void serialize(OutputStream output, OMOutputFormat format)
        throws XMLStreamException
    {

    }

    public void serialize(Writer writer, OMOutputFormat format)
        throws XMLStreamException
    {

    }

    public void serializeAndConsume(XMLStreamWriter xmlWriter)
        throws XMLStreamException
    {

    }

    public void serializeAndConsume(OutputStream output)
        throws XMLStreamException
    {

    }

    public void serializeAndConsume(Writer writer)
        throws XMLStreamException
    {

    }

    public void serializeAndConsume(OutputStream output, OMOutputFormat format)
        throws XMLStreamException
    {

    }

    public void serializeAndConsume(Writer writer, OMOutputFormat format)
        throws XMLStreamException
    {

    }

    public void addChild(OMNode omNode)
    {

    }

    public void buildNext()
    {

    }

    @SuppressWarnings("rawtypes")
	public Iterator getChildren()
    {
        return null;
    }

    @SuppressWarnings("rawtypes")
	public Iterator getChildrenWithLocalName(String localName)
    {
        return null;
    }

    @SuppressWarnings("rawtypes")
	public Iterator getChildrenWithName(QName elementQName)
    {
        return null;
    }

    @SuppressWarnings("rawtypes")
	public Iterator getChildrenWithNamespaceURI(String uri)
    {
        return null;
    }

    public OMElement getFirstChildWithName(QName elementQName)
        throws OMException
    {
        return null;
    }

    public OMNode getFirstOMChild()
    {
        return null;
    }

	public XMLStreamReader getXMLStreamReader(boolean arg0) 
	{
		return null;
	}

	public void serialize(XMLStreamWriter writer, boolean cache)
			throws XMLStreamException 
	{
		// do nothing; this is not a real element
	}
	
	public void writeTextTo(Writer writer, boolean cache)
	{
	    // do nothing, this is not a real element
	}
	
	public Reader getTextAsStream(boolean cache)
	{
	    return null;
	}
	
	public NamespaceContext getNamespaceContext(boolean flag)
	{
	    return null;
	}
	
	public Iterator getNamespacesInScope()
	{
	    return null;
	}
	
	public void undeclarePrefix(String prefix)
	{
	}
	
	public SAXSource getSAXSource(boolean cache)
	{
	    return null;
	}
	
	public XMLStreamReader getXMLStreamReader(boolean cache, OMXMLStreamReaderConfiguration config)
	{
	    return null;
	}
	
	public Iterator getDescendants(boolean includeSelf)
	{
	    return null;
	}
	
	public String getPrefix()
	{
	    return null;
	}
	
    private final OMContainer root;
}
