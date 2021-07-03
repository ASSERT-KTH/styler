package org.wso2.mashup.client;

import java.io.ByteArrayInputStream;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;

/* Purpose	:	This is a sample client to verify the handling of "documentation" annotations of WSO2 Mashup Server.
 * Author	: 	Yumani Ranaweera
 */

public class clientDocumentation {
	private static String toEpr = "http://localhost:7762/services/samples/documentation/";
	
	 
	public static void main(String[] args) throws Exception{
		testEcho("DocumentationTest1","testParam");
		testEcho("DocumentationTest2","testParam");
		
   	}

		
	private static void testEcho(String opName, String param) throws XMLStreamException, AxisFault {
	//	final String param = "VAL";
       	String str = "<" + opName + "Request>" +
                "<param>" + param + "</param>" +
                "</" + opName + "Request>";
        StAXOMBuilder staxOMBuilder = new StAXOMBuilder(new ByteArrayInputStream(str.getBytes()));
        OMElement payload = staxOMBuilder.getDocumentElement();
        
        Options options = new Options();
        options.setTo(new EndpointReference(toEpr));
        ServiceClient sender= new ServiceClient();
        sender.setOptions(options);
        
        OMElement result = sender.sendReceive(payload);
        System.out.println(result.toString());
    }
}


