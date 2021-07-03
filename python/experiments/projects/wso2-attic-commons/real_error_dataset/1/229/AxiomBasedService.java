package org.wso2.wsas.service;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
/* This is a sample service to test AXIOM based service
 * handling capability of WSO2 WSAS. This service accepts OMElement as an argument 
 * and returns an OMElement.
 * Written by Charitha Kankanamge 
 */
import org.apache.axiom.om.OMFactory;


public class AxiomBasedService {
	
	public OMElement sayHello(OMElement element) throws XMLStreamException {
		element.build();
		//element.detach();

		OMElement childElement = element.getFirstElement();
		String echoUser = childElement.getText();

		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace(
				"http://service.wsas.wso2.org", "wsasns");
		OMElement method = fac.createOMElement("sayHelloResponse", omNs);
		OMElement value = fac.createOMElement("greeting", omNs);
		value.addChild(fac.createOMText(value, "Hello," + echoUser + " See how easier to work with OMElements"));
		method.addChild(value);

		return method;
	}
	
	

}
