/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.sample;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.wsdl.WSDLConstants;
import org.wso2.xfer.WSTransferConstants;

public class WSTransferSampleClient {
    
    public static void main(String[] args) throws Exception {
        
        ServiceClient serviceClient = new ServiceClient();
        OperationClient opClient = serviceClient.createClient(ServiceClient.ANON_OUT_IN_OP);
        
        Options options = opClient.getOptions();
        options.setAction(WSTransferConstants.ACTION_URI_CREATE);
                
        EndpointReference epr = new EndpointReference("http://127.0.0.1:6060/axis2/services/WSTransferSampleService");
        options.setTo(epr);
        
        MessageContext msgCtx = new MessageContext();
        opClient.addMessageContext(msgCtx);
        
        SOAPFactory factory = OMAbstractFactory.getSOAP12Factory();
        SOAPEnvelope env = factory.getDefaultEnvelope();
        
        Customer customer = new Customer();
        
        customer.setFirst("Roy");
        customer.setLast("Hill");
        customer.setAddress("321, Main Street");
        customer.setCity("Manhattan Beach");
        customer.setState("CA");
        customer.setZip("9226");
        
        OMElement customerIdHeader = factory.createOMElement(Customer.Q_ELEM_CUSTOMER_ID.getLocalPart(), Customer.Q_ELEM_CUSTOMER_ID.getNamespaceURI(), "xxx");
//        customerIdHeader.setText("732199");
        customerIdHeader.setText("1");
        
//        env.getHeader().addChild(customerIdHeader);
        env.getBody().addChild(CustomerUtil.toOM(customer));
        msgCtx.setEnvelope(env);
        
        opClient.execute(true);
        
        
        opClient = serviceClient.createClient(ServiceClient.ANON_OUT_IN_OP);
        options = opClient.getOptions();
        
        options.setTo(epr);
        options.setAction(WSTransferConstants.ACTION_URI_GET);
        
        env = factory.getDefaultEnvelope();
        
        msgCtx = new MessageContext();
        opClient.addMessageContext(msgCtx);
        
        env.getHeader().addChild(customerIdHeader);
        msgCtx.setEnvelope(env);
        
        opClient.execute(true);
        
        MessageContext inMsgCtx = opClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
        OMElement element = inMsgCtx.getEnvelope().getBody().getFirstElement();
        Customer customer2 = CustomerUtil.fromOM(element);
        
        System.out.println("First" + customer2.getFirst());
    }

}
