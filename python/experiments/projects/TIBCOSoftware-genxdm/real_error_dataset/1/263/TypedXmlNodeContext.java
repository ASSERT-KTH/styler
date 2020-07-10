/*
 * Copyright (c) 2011 TIBCO Software Inc.
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
package org.genxdm.bridge.cx.typed;

import java.io.IOException;
import java.net.URI;

import javax.xml.stream.XMLReporter;

import org.genxdm.ProcessingContext;
import org.genxdm.bridge.cx.base.XmlNodeContext;
import org.genxdm.bridge.cx.tree.XmlNode;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.bridgekit.atoms.XmlAtomBridge;
import org.genxdm.bridgekit.filters.FilteredSequenceBuilder;
import org.genxdm.bridgekit.filters.NamespaceFixupSequenceFilter;
import org.genxdm.bridgekit.xs.SchemaCacheFactory;
import org.genxdm.bridgekit.xs.TypesBridgeImpl;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.Resolver;
import org.genxdm.processor.io.ValidatingDocumentHandler;
import org.genxdm.typed.TypedContext;
import org.genxdm.typed.TypedCursor;
import org.genxdm.typed.TypedModel;
import org.genxdm.typed.ValidationHandler;
import org.genxdm.typed.io.SAXValidator;
import org.genxdm.typed.io.SequenceBuilder;
import org.genxdm.typed.io.SequenceFilter;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.typed.types.TypesBridge;
import org.genxdm.xs.Schema;

public class TypedXmlNodeContext
    implements TypedContext<XmlNode, XmlAtom>
{

    public TypedXmlNodeContext(XmlNodeContext context, Schema schema)
    {
        this.context = PreCondition.assertNotNull(context, "context");
        if (schema == null)
        {
            this.schema = new SchemaCacheFactory().newSchemaCache();
        }
        else
        {
        	this.schema = schema;
        }
        this.types = new TypesBridgeImpl();
        this.atoms = new XmlAtomBridge(this.schema);
        this.model = new TypedXmlNodeModel(atoms);
    }
    
    public AtomBridge<XmlAtom> getAtomBridge()
    {
        return atoms;
    }

    public TypesBridge getTypesBridge()
    {
        return types;
    }

    public Schema getSchema()
    {
    	return schema;
    }
    
    public TypedModel<XmlNode, XmlAtom> getModel()
    {
        return model;
    }

    public ProcessingContext<XmlNode> getProcessingContext()
    {
        return context;
    }

    public TypedCursor<XmlNode, XmlAtom> newCursor(final XmlNode node)
    {
        return new TypedXmlNodeCursor(this, node);
    }

    public SequenceBuilder<XmlNode, XmlAtom> newSequenceBuilder()
    {
        // TODO: this is temporary; it enables namespace fixup that we
        // need, but does so by piling on the virtual calls.  fix is
        // either combining the filter and the wrapper, or pulling the
        // implementation into TypedXmlNodeBuilder.
        SequenceFilter<XmlAtom> filter = new NamespaceFixupSequenceFilter<XmlAtom>();
        filter.setAtomBridge(atoms);
        filter.setSchema(schema);
        return new FilteredSequenceBuilder<XmlNode, XmlAtom>(filter, new TypedXmlNodeBuilder(this));
    }

    @Override
    public ValidatingDocumentHandler<XmlNode, XmlAtom> newDocumentHandler(final SAXValidator<XmlAtom> validator, final XMLReporter reporter, final Resolver resolver)
    {
        return new ValidatingDocumentHandler<XmlNode, XmlAtom>(this, validator, reporter, resolver);
    }

    @Override
    public XmlNode validate(XmlNode source, ValidationHandler<XmlAtom> validator, URI namespace)
    {
        SequenceBuilder<XmlNode, XmlAtom> builder = newSequenceBuilder();
        // TODO: this assumes building a new tree and returning it.
        // can we instead provide a tool that walks the existing tree and modifies it?
        validator.setSchema(this.getSchema());
        validator.setSequenceHandler(builder);
        model.stream(source, validator);
        try 
        {
            validator.flush();
        }
        catch (IOException ioe)
        {
            // oh, get real
            throw new RuntimeException(ioe);
        }

        return builder.getNode();
    }

    private final XmlNodeContext context;
    private final TypedXmlNodeModel model;
    private final XmlAtomBridge atoms;
    private final TypesBridge types;
    private final Schema schema;
}
