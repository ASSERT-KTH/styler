package messageTypes;

/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/


import javax.jms.BytesMessage;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import java.awt.Image;
import java.io.Serializable;
import java.util.Properties;



public class QueueSenderMessage implements java.io.Serializable {
    public static final String QPID_ICF = "org.wso2.andes.jndi.PropertiesFileInitialContextFactory";
    private static final String CF_NAME_PREFIX = "connectionfactory.";
    private static final String QUEUE_NAME_PREFIX = "queue.";
    private static final String CF_NAME = "qpidConnectionfactory";
    String userName = "admin";
    String password = "admin";
    private static String CARBON_CLIENT_ID = "carbon";
    private static String CARBON_VIRTUAL_HOST_NAME = "carbon";
    private static String CARBON_DEFAULT_HOSTNAME = "localhost";//localhost
    private static String CARBON_DEFAULT_PORT = "5673";
    String queueName = "lanka";
 
 
    public static void main(String[] args) throws NamingException, JMSException {
        QueueSenderMessage queueSender = new QueueSenderMessage();
        queueSender.sendMessages();
    }
    public void sendMessages() throws NamingException, JMSException {

        Properties properties = new Properties();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, QPID_ICF);
        properties.put(CF_NAME_PREFIX + CF_NAME, getTCPConnectionURL(userName, password));
        properties.put(QUEUE_NAME_PREFIX + queueName, queueName);
        
        

        System.out.println("getTCPConnectionURL(userName,password) = " + getTCPConnectionURL(userName, password));

        InitialContext ctx = new InitialContext(properties);

        // Lookup connection factory
        QueueConnectionFactory connFactory = (QueueConnectionFactory) ctx.lookup(CF_NAME);
        QueueConnection queueConnection = connFactory.createQueueConnection();
        queueConnection.start();

        QueueSession queueSession =
                queueConnection.createQueueSession(true, QueueSession.AUTO_ACKNOWLEDGE);
        
        

        // Send message
        Queue queue = (Queue)ctx.lookup(queueName);
        
        // create the message to send

	javax.jms.QueueSender queueSender = queueSession.createSender(queue);
		
		

	for(int i=1;i<=10000;i=i+1){
       
		String myMessage = "sri lanka matha";
		
		byte[] data = myMessage.getBytes();
		
		Destination dst=queue;
		
		StreamMessage message= queueSession.createStreamMessage();
			message.writeBytes(data);
		
		
		message.setJMSCorrelationID("a");
		
		message.setJMSCorrelationIDAsBytes(data);
		
		message.setJMSDeliveryMode(1);//aaaaaaaaa
		
		message.setJMSDestination(dst);
		
		//message.setJMSExpiration(100000);//aaaaaaaaa
		
		message.setJMSMessageID("message ID");//aaaaaaaaa
		
		message.setJMSPriority(8);//aaaaaaaaa
		
		message.setJMSRedelivered(true);//aaaaaaaaa
		
		message.setJMSReplyTo(dst);
		
		message.setJMSTimestamp(9000);//aaaaaaaaa
		
		message.setJMSType("JMSType");
		
		
		queueSender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		queueSender.setPriority(1);
		//queueSender.setTimeToLive(900000000);
		//queueSender.setDisableMessageID(true);
		queueSender.setDisableMessageTimestamp(true);
		
		
		/**
		 String myMessage = "asaaaaa";
		byte[] arg0 = myMessage.getBytes();
		message.setJMSCorrelationIDAsBytes(arg0);
	    */
		
		
	   
		
        queueSender.send(message);
        

	}

        queueSender.close();
        queueSession.close();
        queueConnection.close();


    }
    public String getTCPConnectionURL(String username, String password) {
        // amqp://{username}:{password}@carbon/carbon?brokerlist='tcp://{hostname}:{port}'
        return new StringBuffer()
                .append("amqp://").append(username).append(":").append(password)
                .append("@").append(CARBON_CLIENT_ID)
                .append("/").append(CARBON_VIRTUAL_HOST_NAME)
                .append("?brokerlist='tcp://").append(CARBON_DEFAULT_HOSTNAME).append(":").append(CARBON_DEFAULT_PORT).append("'")
                .toString();
    }
 
}

