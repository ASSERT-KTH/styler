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
import javax.jms.Message;
import javax.jms.QueueSession;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Properties;
 
public class DurableTopicSubscriber {
    public static final String ANDES_ICF = "org.wso2.andes.jndi.PropertiesFileInitialContextFactory";
    private static final String CF_NAME_PREFIX = "connectionfactory.";
    private static final String CF_NAME = "andesConnectionfactory";
    String userName = "admin";
    String password = "admin";
    private static String CARBON_CLIENT_ID = "carbon";
    private static String CARBON_VIRTUAL_HOST_NAME = "carbon";
    private static String CARBON_DEFAULT_HOSTNAME = "localhost";
    private static String CARBON_DEFAULT_PORT = "5672";
    private String topicName = "myTopic.asanka";
    private String subscriptionId = "mySub1";
    private boolean useListener = true;
    private int delayBetMessages = 200;
    private int messageCount = 100;
    private SampleMessageListener messageListener;
    public static void main(String[] args) {
        DurableTopicSubscriber durableTopicSubscriber = new DurableTopicSubscriber();
        durableTopicSubscriber.subscribe();
    }
    public void subscribe()  {
        try {
            Properties properties = new Properties();
            properties.put(Context.INITIAL_CONTEXT_FACTORY, ANDES_ICF);
            properties.put(CF_NAME_PREFIX + CF_NAME, getTCPConnectionURL(userName, password));
            properties.put("topic."+topicName,topicName);
            System.out.println("getTCPConnectionURL(userName,password) = " + getTCPConnectionURL(userName, password));
            InitialContext ctx = new InitialContext(properties);
            // Lookup connection factory
            TopicConnectionFactory connFactory = (TopicConnectionFactory) ctx.lookup(CF_NAME);
            TopicConnection topicConnection = connFactory.createTopicConnection();
            topicConnection.start();
            TopicSession topicSession =
                    topicConnection.createTopicSession(false, QueueSession.AUTO_ACKNOWLEDGE);
            // create durable subscriber with subscription ID
            Topic topic = (Topic) ctx.lookup(topicName);
            javax.jms.TopicSubscriber  topicSubscriber = topicSession.createDurableSubscriber(topic,subscriptionId);
            if(!useListener)  {
                for(int count=0;count<messageCount;count++) {
                    Message message = topicSubscriber.receive();
                    System.out.println("count = " + count);
                    if (message instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) message;
                        System.out.println(count+". textMessage.getText() = " + textMessage.getText());
                    }
                    if(delayBetMessages !=0)    {
                        Thread.sleep(delayBetMessages);
                    }
                }
                topicConnection.close();
            } else {
                 messageListener = new SampleMessageListener(topicConnection,topicSession,topicSubscriber,
                         delayBetMessages,messageCount,subscriptionId);
                 topicSubscriber.setMessageListener(messageListener);
                 Thread.sleep(90*1000*60);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String getTCPConnectionURL(String username, String password) {
        return new StringBuffer()
                .append("amqp://").append(username).append(":").append(password)
                .append("@").append(CARBON_CLIENT_ID)
                .append("/").append(CARBON_VIRTUAL_HOST_NAME)
                .append("?brokerlist='tcp://").append(CARBON_DEFAULT_HOSTNAME).append(":").append(CARBON_DEFAULT_PORT).append("'")
                .toString();
    }
    public void stop(){
          this.messageListener.close();
    }
}
