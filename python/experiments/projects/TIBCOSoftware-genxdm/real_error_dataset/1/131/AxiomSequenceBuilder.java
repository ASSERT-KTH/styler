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
package org.genxdm.bridge.axiom.enhanced;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMFactory;
import org.genxdm.bridge.axiom.AxiomFragmentBuilder;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.bridgekit.filters.FilteredFragmentBuilder;
import org.genxdm.bridgekit.tree.TypeAnnotator;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.io.DtdAttributeKind;
import org.genxdm.typed.io.SequenceBuilder;
import org.genxdm.typed.types.AtomBridge;

final class AxiomSequenceBuilder 
    implements SequenceBuilder<Object, XmlAtom>
{
    public AxiomSequenceBuilder(final FilteredFragmentBuilder<Object> base, final AtomBridge<XmlAtom> bridge, final OMFactory factory, final TypeAnnotator annotator, final boolean ignoreComments)
    {
		this.base = base;
		this.bridge = bridge;
		this.types = annotator;
	}
	
	public void attribute(final String namespaceURI, final String localName, final String prefix, final List<? extends XmlAtom> value, final QName type)
	{
	    attribute(namespaceURI, localName, prefix, bridge.getC14NString(value), /*map from schema to dtd?*/ DtdAttributeKind.CDATA);
	    types.annotate(((AxiomFragmentBuilder)base.getBaseBuilder()).lastNodeId(), type);
	    
	}
	
	public void attribute(final String namespaceURI, final String localName, final String prefix, final String value, DtdAttributeKind type) throws GenXDMException
	{
	    base.attribute(namespaceURI, localName, prefix, value, type); 
	}
	
	public void close()
	    throws IOException
	{
	    base.close();
	}
	
	public void comment(final String value)
	{
	    base.comment(value);
	}
	
	public void endDocument()
	{
		base.endDocument();
	}

	public void endElement()
	{
	    base.endElement();
	}

	public void flush()
	    throws IOException
	{
	    base.flush();
	}

	public List<Object> getNodes()
	{
	    return base.getNodes();
	}
	
	public Object getNode()
	{
	    return base.getNode();
	}

	public void namespace(final String prefix, final String namespaceURI)
	{
	    base.namespace(prefix, namespaceURI);
	}

	public void processingInstruction(final String target, final String data)
	{
	    base.processingInstruction(target, data);
	}

	public void reset()
	{
		base.reset();
	}

	public void startDocument(final URI documentURI, final String docTypeDecl)
	{
	    base.startDocument(documentURI, docTypeDecl);
	}

	public void startElement(final String namespaceURI, final String localName, final String prefix)
    {
	    base.startElement(namespaceURI, localName, prefix);
    }

	public void startElement(final String namespaceURI, final String localName, final String prefix, final QName type)
	{
	    startElement(namespaceURI, localName, prefix);
	    types.annotate(((AxiomFragmentBuilder)base.getBaseBuilder()).lastNodeId(), type);
	}

	public void text(final List<? extends XmlAtom> value)
	{
	    base.text(bridge.getC14NString(value));
	}

	public void text(final String value) throws GenXDMException
	{
	    base.text(value);
	}

	private final FilteredFragmentBuilder<Object> base;
	private final AtomBridge<XmlAtom> bridge;
	private final TypeAnnotator types;
}
