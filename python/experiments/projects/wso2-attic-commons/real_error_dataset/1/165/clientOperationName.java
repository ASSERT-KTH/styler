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

/* Purpose	:	This is a sample client to verify the handling of "OperationName" annotation of WSO2 Mashup Server.
 * Author	: 	Yumani Ranaweera
 */
 
public class clientOperationName {
	private static String opname1 = "noOpName";
	private static String opname2 = "GETVERSION";
	private static String opname3 = "GETVERSION1";
	private static String opname4 = "GET_VERSION";
	private static String toEpr = "http://localhost:7762/services/samples/operationName/";
	
	 
	public static void main(String[] args) throws Exception{
		testEcho(opname1);		
		testEcho(opname2);
		testEcho(opname4);
		testEcho(opname3);
	
		
   	}

		
	/*private static void testEcho(String toEpr) throws XMLStreamException, AxisFault {
		String str = "<" + opName + ">" +
        "<param>" + param + "</param>" +
        "</" + opName + ">";
		ServiceClient sender= new ServiceClient();
	    Options options = new Options();
	    options.setTo(new EndpointReference(toEpr));
	    sender.setOptions(options);
	    OMElement result = sender.sendReceive(null);
	    System.out.println(result);
      
    }*/
	
	private static void testEcho(String opName) throws XMLStreamException, AxisFault {
		final String param = "VAL";
       	String str = "<" + opName + ">" +
                "<param>" + param + "</param>" +
                "</" + opName + ">";
        StAXOMBuilder staxOMBuilder = new StAXOMBuilder(new ByteArrayInputStream(str.getBytes()));
        OMElement payload = staxOMBuilder.getDocumentElement();
        new ServiceClient();
        Options options = new Options();
        options.setTo(new EndpointReference(toEpr));
        ServiceClient sender= new ServiceClient();
        sender.setOptions(options);
        OMElement result = sender.sendReceive(payload);
        System.out.println(result.toString());
    }
}
