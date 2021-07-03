package org.genxdm.bridgekit.filters;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.genxdm.bridgekit.xs.BuiltInSchema;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.ContentHandler;
import org.genxdm.names.NamespaceBinding;
import org.genxdm.names.RegisteredPrefixProvider;
import org.genxdm.typed.io.SequenceFilter;
import org.genxdm.typed.io.SequenceHandler;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.SchemaComponentCache;
import org.genxdm.xs.enums.DerivationMethod;
import org.genxdm.xs.types.Type;

public class NamespaceFixupSequenceFilter<A>
    extends AbstractNamespaceFixupHandler
    implements SequenceFilter<A>
{
    public NamespaceFixupSequenceFilter()
    {
        this(null);
    }
    
    public NamespaceFixupSequenceFilter(RegisteredPrefixProvider provider)
    {
        super(provider);
        // TODO: this initialization is for handling of QNames in context,
        // which we currently don't do. see attribute(). note that it should
        // probably also happen in text(List<? extends A>). but we haven't
        // had anyone complaining yet, so screw it.
        // you can't extend a simple type in attribute context (it can't have attributes)
        // methods.add(DerivationMethod.Extension);
        methods.add(DerivationMethod.Restriction); // allowed; enum or pattern or length/maxlen/minlen
        methods.add(DerivationMethod.List); // honestly, this is problematic to handle
        // we prolly ought to allow the union, right?
        //methods.add(DerivationMethod.Union); // also problematic ...
    }

    @Override
    public void attribute(String namespaceURI, String localName, String prefix, List<? extends A> data, QName type)
        throws GenXDMException
    {
        PreCondition.assertNotNull(output);
        if (localName.equalsIgnoreCase("xmlns") || ((prefix != null) && prefix.equalsIgnoreCase("xmlns")) )
        {
            // treat it as a mistaken attempt to declare a namespace using the wrong method.
            if (atoms != null)
                namespace(localName.equalsIgnoreCase("xmlns") ? "" : localName, atoms.getC14NString(data));
            return;
        }
        NamespaceBinding pns = handleAttributeNS(namespaceURI, localName, prefix);
        Type t = (type == null) ? BuiltInSchema.SINGLETON.UNTYPED_ATOMIC : schema.getComponentProvider().getTypeDefinition(type);
//        // i'm not sure about this test
//        if ( (t != null) && t.derivedFromType(BuiltInSchema.SINGLETON.QNAME, methods) )
//        {
//            // TODO: finish the job
//            // we need to check for qnames in content, here, and insure
//            // that any bindings that they require are also declared.
//        }
        attributes.add(new SeqAttr(pns.getNamespaceURI(), localName, pns.getPrefix(), data, type));
    }

    @Override
    public void startElement(String namespaceURI, String localName, String prefix, QName type)
        throws GenXDMException
    {
        PreCondition.assertNotNull(output);
        reconcile();
        newScope();
        elementPrefix = prefix;
        elementNs = namespaceURI;
        elementName = localName;
        elementType = type;
    }

    @Override
    public void text(List<? extends A> data)
        throws GenXDMException
    {
        PreCondition.assertNotNull(output);
        reconcile();
        output.text(data);
    }

    @Override
    public void setOutputSequenceHandler(SequenceHandler<A> handler)
    {
        output = PreCondition.assertNotNull(handler);
    }
    
    @Override
    public void setSchema(SchemaComponentCache cache)
    {
    	schema = PreCondition.assertNotNull(cache);
    }
    
    @Override
    public void setAtomBridge(AtomBridge<A> bridge)
    {
        atoms = PreCondition.assertNotNull(bridge);
    }
    
    @Override
    protected ContentHandler getOutputHandler()
    {
        return output;
    }

    @Override
    protected void outputAttribute(Attr a)
    {
        if (a instanceof NamespaceFixupSequenceFilter.SeqAttr)
        {
            @SuppressWarnings("unchecked")
            SeqAttr sa = (SeqAttr)a;
            if (sa.data != null)
            {
                output.attribute(sa.namespace, sa.name, sa.prefix, sa.data, sa.typeName);
                return;
            }
        }
        // this is so we can can actually call *some* method if we don't call
        // the SeqAttr-specific one in the block above. can't use a simple else
        // due to nested ifs.
        output.attribute(a.namespace, a.name, a.prefix, a.value, a.type);
    }

    @Override
    protected void outputCurrentElement()
    {
        PreCondition.assertNotNull(output);
        output.startElement(elementNs, elementName, elementPrefix, elementType);
        elementNs = null;
        elementName = null;
        elementPrefix = null;
        elementType = null;
    }

    private class SeqAttr extends AbstractNamespaceFixupHandler.Attr
    {
        SeqAttr(String namespaceURI, String localName, String prefix, List<? extends A> data, QName type)
        {
            this.namespace = namespaceURI;
            this.name = localName;
            this.prefix = prefix;
            this.data = data;
            this.typeName = type;
        }
        List<? extends A> data;
        QName typeName;
    }

    private QName elementType;
    private SequenceHandler<A> output;
    private AtomBridge<A> atoms;
    private SchemaComponentCache schema; // never used? why is this here? oh, for qnames in content; never mind
    private Set<DerivationMethod> methods = new HashSet<DerivationMethod>(3);
    
}
