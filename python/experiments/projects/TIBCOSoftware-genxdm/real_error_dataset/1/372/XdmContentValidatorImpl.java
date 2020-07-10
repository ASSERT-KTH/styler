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
package org.genxdm.processor.w3c.xs.validation;

import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.GenXDMException;
import org.genxdm.io.DtdAttributeKind;
import org.genxdm.processor.w3c.xs.validation.api.VxMapping;
import org.genxdm.processor.w3c.xs.validation.api.VxValidator;
import org.genxdm.typed.ValidationHandler;
import org.genxdm.typed.io.SequenceHandler;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.typed.types.Emulation;
import org.genxdm.xs.SchemaComponentCache;
import org.genxdm.xs.exceptions.AbortException;
import org.genxdm.xs.exceptions.SchemaExceptionHandler;


final class XdmContentValidatorImpl<A> implements ValidationHandler<A>
{
    public XdmContentValidatorImpl(final VxValidator<A> kernel, final AtomBridge<A> atomBridge)
    {
		this.kernel = kernel;
		this.atomBridge = atomBridge;
	}

	public void attribute(final String namespaceURI, final String localName, final String prefix, final List<? extends A> data, final QName ignoreMe) throws GenXDMException
	{
		// TODO: We don't want to throw the value away.
		final String strval = Emulation.C14N.atomsToString(data, atomBridge);
		m_attributes.add(new VxMapping<QName, String>(new QName(namespaceURI, localName, prefix), strval));
	}
	
	public void attribute(final String namespaceURI, final String localName, final String prefix, final String untypedAtomic, final DtdAttributeKind type) throws GenXDMException
	{
		m_attributes.add(new VxMapping<QName, String>(new QName(namespaceURI, localName, prefix), untypedAtomic));
	}

	public void close() throws IOException
	{
	}

	public void comment(final String value) throws GenXDMException
	{
		// Ignore. Comments must not interfere with validation.
	}

	public void endDocument() throws GenXDMException
	{
		try
		{
			kernel.endDocument();
		}
		catch (final Exception e)
		{
			throw new GenXDMException(e);
		}
	}

	public void endElement() throws GenXDMException
	{
		flush();
		try
		{
			kernel.endElement();
		}
		catch (final Exception e)
		{
			throw new GenXDMException(e);
		}
	}

	public void flush()
	{
        if (m_elementName != null)
        {
            try
            {
                // if m_elementType is non-null, do an ignore-the-element-name validation
                kernel.startElement(m_elementName, m_namespaces, m_attributes, m_elementType);
            }
            catch (final Exception e)
            {
                throw new GenXDMException(e);
            }
            m_elementType = null; // usually null anyway
            m_elementName = null; // reset for next in doc orde
            m_namespaces.clear(); // done with these
            m_attributes.clear(); // done with these, too
        }
        // if we have an element without a name, we should prolly become quite angry
	}

	public void namespace(final String prefix, final String namespaceURI) throws GenXDMException
	{
		m_namespaces.add(new VxMapping<String, String>(prefix, namespaceURI));
	}

	public void processingInstruction(final String target, final String data) throws GenXDMException
	{
		// Ignore. Processing instructions must not interfere with validation.
	}

	public void reset()
	{
		kernel.reset();
	}

	public void startDocument(final URI documentURI, final String docTypeDecl) 
	    throws GenXDMException
	{
		try
		{
			kernel.startDocument(documentURI);
		}
		catch (final Exception e)
		{
			throw new GenXDMException(e);
		}
	}

	public void startElement(final String namespaceURI, final String localName, final String prefix, final QName ignoreMe) throws GenXDMException
	{
	    startElement(namespaceURI, localName, prefix);
	}

    public void startElement(final String namespaceURI, final String localName, final String prefix) throws GenXDMException
    {
        flush();
        m_elementName = new QName(namespaceURI, localName, prefix);
    }

	public void text(final List<? extends A> value) throws GenXDMException
	{
		flush();
		try
		{
			kernel.text(value);
		}
		catch (final IOException e)
		{
			throw new GenXDMException(e);
		}
		catch (final AbortException e)
		{
			throw new GenXDMException(e);
		}
	}

	public void text(final String untypedAtomic) throws GenXDMException
	{
		flush();
		try
		{
			kernel.characters(untypedAtomic.toCharArray(), 0, untypedAtomic.length());
		}
		catch (final IOException e)
		{
			throw new GenXDMException(e);
		}
		catch (final AbortException e)
		{
			throw new GenXDMException(e);
		}
	}

    @Override
    public SchemaExceptionHandler getSchemaExceptionHandler()
    {
        return errors;
    }

    @Override
    public void setSchema(SchemaComponentCache cache)
    {
        kernel.setComponentProvider(cache.getComponentProvider());
    }

    @Override
    public void setSchemaExceptionHandler(SchemaExceptionHandler errors)
    {
        this.errors = errors;
        kernel.setExceptionHandler(errors);
    }

    @Override
    public void setSequenceHandler(SequenceHandler<A> handler)
    {
        kernel.setOutputHandler(new OutputAdapter<A>(handler));
    }
    
    @Override
    public void setInitialElementType(QName name)
    {
        m_elementType = name;
    }


    private SchemaExceptionHandler errors;
    private VxValidator<A> kernel;

    private final AtomBridge<A> atomBridge;
    private final LinkedList<VxMapping<QName, String>> m_attributes = new LinkedList<VxMapping<QName, String>>();
    // The name of the element that has yet to be passed to the validation kernel
    // because we are buffering namespace and attribute events.
    private QName m_elementName = null;
    private QName m_elementType = null; // only used once!
    private final LinkedList<VxMapping<String, String>> m_namespaces = new LinkedList<VxMapping<String, String>>();

}
