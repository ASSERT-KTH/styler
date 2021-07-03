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

/* Purpose	:	This is a sample client to verify the handling of "JS schema types" used in WSO2 Mashup Server.
 * Author	: 	Yumani Ranaweera
 */

public class clientSchemaTest {
	 private static String toEpr = "http://localhost:7762/services/samples/schemaTest";
	 private static final String echoJSstringParam = "JSString";
	 private static final String echoJSStringParam = "JSString";
	 private static final String echoJSNumberParam = "44552455524425";
	 private static final String echoJSnumberParam = "55562527265256";
	 private static final String echoJSBooleanParam = "true";
	 private static final String echoJSbooleanParam = "false";
	 private static final String echoJSdateParam = "2007-08-27T10:20:30.040Z";
	 private static final String echoJSDateParam = "2007-08-27T10:20:30.040Z";
	 private static final String echoStringArrayParam = "test1, test2, test3, test4";
	 private static final String returnJSArrayWithIndicesParam = "";
	 private static final String returnJSArrayWithPropertiesParam = "";
//	 private static final String returnJSObjectParam = "test";
	 private static final String objectFunctionParam = "";
	 private static final String echoEnumerationParam = "a";
	 private static final String echoXSstringParam = "testXsString";
	 private static final String echoXSnormalizedStringParam = "testXSnormalizedString";
	 private static final String echoXStokenParam = "testXStoken";
	 private static final String echoXSlanguageParam = "en";
	 private static final String echoXSNameParam = "testXSName";
	 private static final String echoXSNCNameParam = "noncolnizzzeeeedd";
	// private static final String echoXSNOTATIONParam = "<notation id = ID  name = NCName></notation>";
	 private static final String echoXSanyURIParam = "http://www.yahoo.com";
	 private static final String echoXSfloatParam = "12.12";
	 private static final String echoXSdoubleParam = "12.22";
	 private static final String echoXSdurationParam = "P1Y2M3DT10H30M";
	 private static final String echoXSintegerParam = "12";
	 private static final String echoXSnonPositiveIntegerParam = "-120000";
	 private static final String echoXSnegativeIntegerParam = "-10000";
	 private static final String echoXSlongParam = "1200000000000000000";
	 private static final String echoXSintParam = "111";
	 private static final String echoXSshortParam = "0";
	 private static final String echoXSbyteParam = "127";
	 private static final String echoXSnonNegativeIntegerParam = "12678967543233";
	 private static final String echoXSunsignedLongParam = "12678967543233";
	 private static final String echoXSunsignedIntParam = "100000";
	 private static final String echoXSunsignedShortParam = "1267";
	 private static final String echoXSunsignedByteParam = "126";
	 private static final String echoXSdecimalParam = "2.001";
	 private static final String echoXSbooleanParam = "false";
	 private static final String echoXSdateTimeParam = "1999-05-31T13:20:00-05:00";
	 private static final String echoXSdateParam = "1999-05-31";
	 private static final String echoXStimeParam = "13:20:00-05:00";
	 private static final String echoXSgYearMonthParam = "1999-05";
	 private static final String echoXSgMonthDayParam = "--03-05";
	 private static final String echoXSgYearParam = "1999";
	 private static final String echoXSgDayParam = "---05";
	 private static final String echoXSgMonthParam = "--05--";
	 private static final String echoXSanyTypeParam = "test";
	 private static final String echoXSQNameParam = "<edi:price xmlns:edi='http://ecommerce.example.org/schema' units='Euro'>32.18</edi:price>";
	 private static final String echoXShexBinaryParam = "0FB7";
	 private static final String echoXSbase64BinaryParam = "TWFu";
	 private static final String returnInfinityParam = ""; // should return INF
	 private static final String returnStringParam = "this is a string";
	 private static final String returnNumberParam = "22";
	 private static final String returnBooleanParam = "true";
	 private static final String returnDateParam = "2007-08-27T10:20:30.040+00:00";
	 //private static final String complexEchoParam = "'apples', {1, true}, 123";
	 //private static final String complexEchoOptionalParam = "' ', {1, true}, 123";
	 //private static final String complexEchoArrayParam = "'apples', {1, true}, 123";
	 //private static final String echoComplexCompositionsParam = "true";
	 //private static final String echoComplexParam = "true";
	 private static final String noInputAsSimpleParam = "";
	 private static final String noInputAsComplexParam = "";
	 private static final String noInputAsNoneParam = "";
	 private static final String noOutputAsSimpleParam = "test";
	 //private static final String noOutputAsNoneParam = "test";	 
	 //private static final String echoXMLNodeParam = "test";
//	private static final String echoXMLNodeListParam = "test";
//	private static final String echoXMLNodeList2Param = "test";
//	private static final String echoXMLNodeList3 = "test";	 

	 
	public static void main(String[] args) throws Exception{
		testEcho("echoJSstring", echoJSstringParam);
		testEcho("echoJSString", echoJSStringParam);
		testEcho("echoJSNumber", echoJSNumberParam);
		testEcho("echoJSnumber", echoJSnumberParam);
		testEcho("echoJSBoolean", echoJSBooleanParam);
		testEcho("echoJSboolean", echoJSbooleanParam);
		testEcho("echoJSdate", echoJSdateParam);
		testEcho("echoJSDate", echoJSDateParam);
		testEcho("echoStringArray", echoStringArrayParam);
		testEcho("returnJSArrayWithIndices", returnJSArrayWithIndicesParam);
		testEcho("returnJSArrayWithProperties", returnJSArrayWithPropertiesParam);
//		testEcho("returnJSObject", returnJSObjectParam);
		testEcho("objectFunction", objectFunctionParam);
		testEcho("echoEnumeration", echoEnumerationParam);
		testEcho("echoXSstring", echoXSstringParam);
		testEcho("echoXSnormalizedString", echoXSnormalizedStringParam);
		testEcho("echoXStoken", echoXStokenParam);
		testEcho("echoXSlanguage", echoXSlanguageParam);
		testEcho("echoXSName", echoXSNameParam);
		testEcho("echoXSNCName", echoXSNCNameParam);
	//	testEcho("echoXSNOTATION", echoXSNOTATIONParam);
		testEcho("echoXSanyURI", echoXSanyURIParam);
		testEcho("echoXSfloat", echoXSfloatParam);
		testEcho("echoXSdouble", echoXSdoubleParam);
		testEcho("echoXSduration", echoXSdurationParam);
		testEcho("echoXSinteger", echoXSintegerParam);
		testEcho("echoXSnonPositiveInteger", echoXSnonPositiveIntegerParam);
		testEcho("echoXSnegativeInteger", echoXSnegativeIntegerParam);
		testEcho("echoXSlong", echoXSlongParam);
		testEcho("echoXSint", echoXSintParam);
		testEcho("echoXSshort", echoXSshortParam);
		testEcho("echoXSbyte", echoXSbyteParam);
		testEcho("echoXSnonNegativeInteger", echoXSnonNegativeIntegerParam);
		testEcho("echoXSunsignedLong", echoXSunsignedLongParam);
		testEcho("echoXSunsignedInt", echoXSunsignedIntParam);
		testEcho("echoXSunsignedShort", echoXSunsignedShortParam);
		testEcho("echoXSunsignedByte", echoXSunsignedByteParam);
		testEcho("echoXSdecimal", echoXSdecimalParam);
		testEcho("echoXSboolean", echoXSbooleanParam);
		testEcho("echoXSdateTime", echoXSdateTimeParam);
		testEcho("echoXSdate", echoXSdateParam);
		testEcho("echoXStime", echoXStimeParam);
		testEcho("echoXSgYearMonth", echoXSgYearMonthParam);
		testEcho("echoXSgMonthDay", echoXSgMonthDayParam);
		testEcho("echoXSgYear", echoXSgYearParam);
		testEcho("echoXSgDay", echoXSgDayParam);
		testEcho("echoXSgMonth", echoXSgMonthParam);
		testEcho("echoXSanyType", echoXSanyTypeParam);
		testEcho("echoXSQName", echoXSQNameParam);
		testEcho("echoXShexBinary", echoXShexBinaryParam);
		testEcho("echoXSbase64Binary", echoXSbase64BinaryParam);
		testEcho("returnInfinity", returnInfinityParam);
		testEcho("returnString", returnStringParam);
		testEcho("returnNumber", returnNumberParam);
		testEcho("returnBoolean", returnBooleanParam);
		testEcho("returnDate", returnDateParam);
		//testEcho("complexEcho", complexEchoParam);
		//testEcho("complexEchoOptionalParams", complexEchoOptionalParam);
		//testEcho("complexEchoArrayParams", complexEchoArrayParam);
		//testEcho("echoComplexCompositions", echoComplexCompositionsParam);
		//testEcho("echoComplexParam", echoComplexParam);
		testEcho("noInputAsSimple", noInputAsSimpleParam);
		testEcho("noInputAsComplex", noInputAsComplexParam);
		testEcho("noInputAsNone", noInputAsNoneParam);
		testEcho("noOutputAsSimple", noOutputAsSimpleParam);
		//testEcho("noOutputAsComplex", noOutputAsComplexParam);
		//testEcho("noOutputAsNone", noOutputAsNoneParam);
		//testEcho("echoXMLNode", echoXMLNodeParam);
		//testEcho("echoXMLNodeList", echoXMLNodeListParam);
		//testEcho("echoXMLNodeList2", echoXMLNodeList2Param);
		//testEcho("echoXMLNodeList3", echoXMLNodeList3Param);
		
		
		
   	}
		
	private static void testEcho(String opName, String param) throws XMLStreamException, AxisFault {
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
        System.out.println(result);
    }
}
