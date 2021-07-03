package org.wso2.wsas.client;

import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.wso2.wsas.service.RestServiceStub;


/* This is a sample client to verify RESTful service invocation of WSO2 WSAS. 
 * Monitor the message on wire using Tcpmon.
 * Written by Charitha Kankanamge 
 */

public class RestClient {
	
	public static void main(String[] args)throws AxisFault{

		RestServiceStub stub = new RestServiceStub();
		RestServiceStub.EchoString stringrequest = new RestServiceStub.EchoString();
		RestServiceStub.AddNumbers addrequest = new RestServiceStub.AddNumbers();
		
		stringrequest.setParam1("Welcome");
		stringrequest.setParam2("WSO2 WSAS!!!");
		
		addrequest.setX(100);
		addrequest.setY(200);
		
		stub._getServiceClient().getOptions().setProperty(Constants.Configuration.ENABLE_REST, Constants.VALUE_TRUE);
		
		try {
			RestServiceStub.AddNumbersResponse addresponse = stub.addNumbers(addrequest);
			System.out.println(addresponse.get_return());
			RestServiceStub.EchoStringResponse stringresponse = stub.echoString(stringrequest);
			System.out.println(stringresponse.get_return());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		

}
}
