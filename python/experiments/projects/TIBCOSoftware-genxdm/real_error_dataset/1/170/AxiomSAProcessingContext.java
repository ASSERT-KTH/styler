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

import java.net.URI;
import java.util.EnumSet;

import javax.xml.stream.XMLReporter;

import org.genxdm.bridge.axiom.AxiomProcessingContext;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.bridgekit.atoms.XmlAtomBridge;
import org.genxdm.bridgekit.filters.FilteredSequenceBuilder;
import org.genxdm.bridgekit.filters.NamespaceFixupSequenceFilter;
import org.genxdm.bridgekit.tree.CoreModelDecoration;
import org.genxdm.bridgekit.tree.CoreModelDecorator;
import org.genxdm.bridgekit.tree.CursorOnTypedModel;
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
import org.genxdm.typed.io.TypedDocumentHandler;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.typed.types.TypesBridge;
import org.genxdm.xs.Schema;

public final class AxiomSAProcessingContext 
    implements TypedContext<Object, XmlAtom>
{
    public AxiomSAProcessingContext(final AxiomProcessingContext context)
	{
	    this.context = PreCondition.assertNotNull(context, "context");
		final SchemaCacheFactory cacheFactory = new SchemaCacheFactory();
		this.cache = cacheFactory.newSchemaCache();
		this.metaBridge = new TypesBridgeImpl();
        this.atomBridge = new XmlAtomBridge(this.cache);
		EnumSet<CoreModelDecoration> delegations = EnumSet.noneOf(CoreModelDecoration.class);
		delegations.add(CoreModelDecoration.CHILD_AXIS);
		delegations.add(CoreModelDecoration.CHILD_ELEMENTS);
		this.model = new CoreModelDecorator<Object, XmlAtom>(delegations, new AxiomSAModel(new org.genxdm.bridge.axiom.AxiomModel(), atomBridge), atomBridge);
	}
	
	public AtomBridge<XmlAtom> getAtomBridge()
	{
		return atomBridge;
	}

    public TypesBridge getTypesBridge()
	{
		return metaBridge;
	}

    public Schema getSchema()
    {
    	return cache;
    }
	public TypedModel<Object, XmlAtom> getModel()
	{
		return model;
	}

	public AxiomProcessingContext getProcessingContext()
	{
	    return context;
	}

	public TypedCursor<Object, XmlAtom> newCursor(Object node)
	{
		return new CursorOnTypedModel<Object, XmlAtom>(node, model);
	}

    public SequenceBuilder<Object, XmlAtom> newSequenceBuilder()
    {
        // TODO: this is temporary; it enables namespace fixup that we
        // need, but does so by piling on the virtual calls.  fix is
        // either combining the filter and the wrapper, or pulling the
        // implementation into here.
        SequenceFilter<XmlAtom> filter = new NamespaceFixupSequenceFilter<XmlAtom>();
        filter.setSchema(cache);
        filter.setAtomBridge(atomBridge);
	    return new FilteredSequenceBuilder<Object, XmlAtom>(filter, new AxiomSequenceBuilder(this, context.getOMFactory(), true));
    }
    
    @Override
    public TypedDocumentHandler<Object, XmlAtom> newDocumentHandler(SAXValidator<XmlAtom> validator, XMLReporter reporter, Resolver resolver)
    {
        // TODO Auto-generated method stub
        return new ValidatingDocumentHandler<Object, XmlAtom>(this, validator, reporter, resolver);
    }

    @Override
    public Object validate(Object source, ValidationHandler<XmlAtom> validator, URI namespace)
    {
        // TODO: implement
        throw new UnsupportedOperationException();
    }
    
    private final AxiomProcessingContext context;
	private final AtomBridge<XmlAtom> atomBridge;
	private final Schema cache;
	
	@SuppressWarnings("unused")
	private boolean locked;
	private final TypesBridge metaBridge;
	private final TypedModel<Object, XmlAtom> model;
}
