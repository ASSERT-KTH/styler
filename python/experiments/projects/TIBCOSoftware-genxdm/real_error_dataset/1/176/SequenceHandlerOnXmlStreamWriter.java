package org.genxdm.processor.output;

import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamWriter;

import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.DtdAttributeKind;
import org.genxdm.typed.io.SequenceHandler;
import org.genxdm.typed.types.AtomBridge;

public class SequenceHandlerOnXmlStreamWriter<A> extends
		ContentHandlerOnXmlStreamWriter implements SequenceHandler<A> {

	public SequenceHandlerOnXmlStreamWriter(XMLStreamWriter output, AtomBridge<A> atomBridge) {
		super(output);
		this.atomBridge = PreCondition.assertNotNull(atomBridge, "atomBridge");
	}

	@Override
	public void attribute(String namespaceURI, String localName, String prefix,
			List<? extends A> data, QName type) throws GenXDMException {
		super.attribute(namespaceURI, localName, prefix, atomBridge.getC14NString(data), (DtdAttributeKind)null);
	}

	@Override
	public void startElement(String namespaceURI, String localName,
			String prefix, QName type) throws GenXDMException {
		super.startElement(namespaceURI, localName, prefix);
	}

	@Override
	public void text(List<? extends A> data) throws GenXDMException {
		super.text(atomBridge.getC14NString(data));
	}

	final private AtomBridge<A> atomBridge;
}
