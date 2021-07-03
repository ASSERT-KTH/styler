package org.wso2.ws.dataservice.samples;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;

public class DeleteRecordClient {
	    private static EndpointReference targetEPR = new EndpointReference("http://localhost:8080/axis2/services/StudentDeleteService");
	    public static void main(String args[]){
	        try {
	            OMElement payload = getPayload();
	            Options options = new Options();
	            options.setTo(targetEPR);
	            options.setAction("urn:deleteStudentInfo");
	            ServiceClient sender = new ServiceClient();
	            sender.setOptions(options);
	            OMElement result = sender.sendReceive(payload);
	            System.out.println(result);
	        } catch (AxisFault axisFault) {
	            axisFault.printStackTrace();
	        }
	    }


	    private static OMElement getPayload() {
	        OMFactory fac = OMAbstractFactory.getOMFactory();
	        OMNamespace omNs = fac.createOMNamespace(
	                "http://example1.org/example1", "example1");
	        OMElement method = fac.createOMElement("deleteStudent", omNs);
	        
	        OMElement id = fac.createOMElement("id", omNs);
	        id.setText("7");
	        OMElement name = fac.createOMElement("name", omNs);
	        name.setText("amila");
	        
	        method.addChild(id);
	        method.addChild(name);
	        
	        return method;
	    }
}
