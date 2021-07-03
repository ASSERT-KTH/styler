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

/* Purpose	:	This is a sample client to verify the handling of "input/outputTypes" annotations of WSO2 Mashup Server.
 * Author	: 	Yumani Ranaweera
 */

public class clientinputOutputTypes {
	private static String toEpr = "http://localhost:7762/services/samples/inputOutputTypesPositiveTCs";
	 private static final String noParam = "";
	 private static final String testStringParam = "23";
	 private static final String testNumberParam = "2";
	 private static final String testBooleanParam ="true";
	 private static final String testDateParam ="2002-10-10T17:00:00Z";
	 private static final String testObjectParam ="cat";
	 private static final String stringParam = "hi";
	 private static final String integerParam = "10";
	 private static final String floatParam = "5.87";
	 private static final String xsNormalizedStringParam = "NORMALIZED";
	 private static final String xsNameParam = "Yumani";
	 private static final String xsTokenParam = "test";
	 private static final String readDateParam = "2002-10-10T17:00:00Z";
	 private static final String photoForDateParam = "2002-10-10";

	 
	public static void main(String[] args) throws Exception{
		testEcho("NoInput", noParam);
		testEcho("testString", testStringParam);
		testEcho("testNumber", testNumberParam);
		testEcho("testBoolean",testBooleanParam);
		testEcho("testDate",testDateParam);
		testEcho("testObject",testObjectParam);		
		testEcho("xsString", stringParam);
		testEcho("xsInt", integerParam);
		testEcho("xsfloat", floatParam);
		testEcho("xsNormalizedString", xsNormalizedStringParam);
		testEcho("xsName", xsNameParam);
		testEcho("xsToken", xsTokenParam);
		testEcho("readDate", readDateParam);
		testEcho("photoForDate", photoForDateParam);
   	}

		
	private static void testEcho(String opName, String param) throws XMLStreamException, AxisFault {
       	String str = "<" + opName + "Request>" +
                "<param>" + param + "</param>" +
                "</" + opName + "Request>";
        StAXOMBuilder staxOMBuilder = new StAXOMBuilder(new ByteArrayInputStream(str.getBytes()));
        OMElement payload = staxOMBuilder.getDocumentElement();
       //new ServiceClient();
        Options options = new Options();
        options.setTo(new EndpointReference(toEpr));
        ServiceClient sender= new ServiceClient();
        sender.setOptions(options);
        OMElement result = sender.sendReceive(payload);
        System.out.println(result);
    }
}
