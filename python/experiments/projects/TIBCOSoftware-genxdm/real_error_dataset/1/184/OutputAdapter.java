/**
 * Copyright (c) 2009-2010 TIBCO Software Inc.
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
package org.genxdm.processor.w3c.xs.validation;

import java.io.IOException;
import java.util.List;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.processor.w3c.xs.validation.api.VxOutputHandler;
import org.genxdm.typed.io.SequenceHandler;
import org.genxdm.xs.types.SimpleType;
import org.genxdm.xs.types.Type;


public class OutputAdapter<A> implements VxOutputHandler<A>
{
	private final SequenceHandler<A> handler;

	public OutputAdapter(final SequenceHandler<A> handler)
	{
		this.handler = PreCondition.assertArgumentNotNull(handler, "handler");
	}

	public void attribute(final QName name, final List<? extends A> value, final SimpleType type)
	{
		handler.attribute(name.getNamespaceURI(), name.getLocalPart(), name.getPrefix(), value, type.getName());
	}

	public void attribute(final QName name, final String value)
	{
		handler.attribute(name.getNamespaceURI(), name.getLocalPart(), name.getPrefix(), value, null);
	}

	public void endDocument()
	{
		handler.endDocument();
	}

	public void endElement()
	{
		handler.endElement();
	}

	public void namespace(final String prefix, final String namespaceURI)
	{
		handler.namespace(prefix, namespaceURI);
	}

	public void startDocument() throws IOException
	{
		handler.startDocument(null, null);
	}

	public void startElement(final QName name, final Type type) throws IOException
	{
//System.out.println("QName is " + name);
//System.out.println("Type is " + type);
		handler.startElement(name.getNamespaceURI(), name.getLocalPart(), name.getPrefix(), type.getName());
	}

	public void text(final List<? extends A> value)
	{
		handler.text(value);
	}

	public void text(final String value)
	{
		handler.text(value);
	}
}
