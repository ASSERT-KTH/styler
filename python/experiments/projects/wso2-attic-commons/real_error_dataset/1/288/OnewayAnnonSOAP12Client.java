package org.wso2.esb.client;

import java.rmi.RemoteException;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.client.async.AsyncResult;
import org.apache.axis2.client.async.AxisCallback;
import org.apache.axis2.client.async.Callback;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.context.MessageContext;
import org.apache.neethi.Policy;
import org.apache.neethi.PolicyEngine;
import org.apache.rampart.RampartMessageData;
import org.codehaus.groovy.sandbox.ui.Completer;

public class OnewayAnnonSOAP12Client {

	
	public static void main(String[] args)throws AxisFault {
		
		ConfigurationContext cc = ConfigurationContextFactory.createConfigurationContextFromFileSystem(args[0],args[1]+"/axis2.xml");
		OMElement payload = createPayLoad();
		
		ServiceClient serviceclient = new ServiceClient(cc, null);   
		
		Options opts = new Options();
		opts.setTo(new EndpointReference(args[2]+"/RMProxy"));
		opts.setAction("urn:Ping");
		opts.setSoapVersionURI(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);
		
		serviceclient.setOptions(opts);		
		
		//for (int i = 0; i < 3; i++) {

		try {
			serviceclient.fireAndForget(payload);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//}
	}
	
	public static OMElement createPayLoad(){		
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace("http://service.esb.wso2.org", "ns");
		OMElement method = fac.createOMElement("Ping", omNs);
		OMElement value = fac.createOMElement("ping", omNs);
		value.addChild(fac.createOMText(method, "pong"));

		method.addChild(value);
		
		return method;
	}
}

