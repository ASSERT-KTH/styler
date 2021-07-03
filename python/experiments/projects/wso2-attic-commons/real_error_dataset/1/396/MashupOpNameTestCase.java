package org.wso2.mashup.client;
import java.io.ByteArrayInputStream;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;

import junit.framework.TestCase;

/* Purpose	:	This is a sample client to verify the handling of "operationName" annotation of WSO2 Mashup Server.
 * Author	: 	Yumani Ranaweera
 */

public class MashupOpNameTestCase extends TestCase {

	public final static String toEpr = "http://localhost:7762/services/samples/operationName/";
	ServiceClient sender;
	public void setUp() throws Exception{
		Options options = new Options();
        options.setTo(new EndpointReference(toEpr));
        sender= new ServiceClient();
        sender.setOptions(options);					
	}

	public void testEcho(String opName) throws XMLStreamException, AxisFault {
		final String param = "VAL";
       	String str = "<" + opName + ">" +
                "<param>" + param + "</param>" +
                "</" + opName + ">";
        StAXOMBuilder staxOMBuilder = new StAXOMBuilder(new ByteArrayInputStream(str.getBytes()));
        OMElement payload = staxOMBuilder.getDocumentElement();
        OMElement result = sender.sendReceive(payload);
        System.out.println(result.toString());
        
        assertTrue(result.toString().indexOf("VAL")>0);
    }
	
	public void testEcho()throws AxisFault,XMLStreamException {		
		testEcho("noOpName");
		testEcho("GETVERSION");
		testEcho("GET_VERSION");
		
	}
}
