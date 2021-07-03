package org.wso2.wsas.service;

import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.ServiceContext;

/* This is a working example to test Soap session handling in WSAS */ 
public class SoapSessionService {

	 public int multiply(int k, int j){
		 
		 ServiceContext serviceContext =
             MessageContext.getCurrentMessageContext().getServiceContext();
		 if ((Integer) serviceContext.getProperty("VALUE") == null){
		 serviceContext.setProperty("VALUE", new Integer(k*j));
		 return ((Integer) serviceContext.getProperty("VALUE")).intValue();
		 } else
		 {
			 serviceContext.setProperty("VALUE1", (Integer) serviceContext.getProperty("VALUE"));
			 int result = ((Integer) serviceContext.getProperty("VALUE1")).intValue()+((Integer) serviceContext.getProperty("VALUE")).intValue();
			 serviceContext.setProperty("VALUE", (Integer) result);
			 return result;
		 }
		 
				 
	 }
	 
	 

}
