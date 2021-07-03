package cep;

import javax.jms.TopicConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;
public class JNDIContext {
    public static final String QPID_ICF = "org.wso2.andes.jndi.PropertiesFileInitialContextFactory";
    private static final String CF_NAME_PREFIX = "connectionfactory.";
    private static final String CF_NAME = "ConnectionFactory";
    private static final String userName = "admin";
    private static final String password = "admin";
    private static String CARBON_CLIENT_ID = "clientid";
    private static String CARBON_VIRTUAL_HOST_NAME = "carbon";
    private static String CARBON_DEFAULT_HOSTNAME = "localhost";
    private static String CARBON_DEFAULT_PORT = "5673";
    private InitialContext initContext = null;
    private TopicConnectionFactory topicConnectionFactory = null;
    public static JNDIContext instance = null;
    private JNDIContext() {
        createInitialContext();
        createConnectionFactory();
    }
    public InitialContext getInitContext() {
        return initContext;
    }
    public TopicConnectionFactory getTopicConnectionFactory() {
        return topicConnectionFactory;
    }
    public static JNDIContext getInstance() {
        if (instance == null) {
            instance = new JNDIContext();
        }
        return instance;
    }
    /**
     * Create Connection factory with initial context
     */
    private void createConnectionFactory() {
        try {
            topicConnectionFactory = (TopicConnectionFactory) initContext.lookup("ConnectionFactory");
        } catch (NamingException e) {
            System.out.println("Can not create topic connection factory." + e);
        }
    }
    /**
     * Create Initial Context with given configuration
     */
    private void createInitialContext() {
 
        try {
            Properties properties = new Properties();
            properties.put(Context.INITIAL_CONTEXT_FACTORY, QPID_ICF);
            properties.put(CF_NAME_PREFIX + CF_NAME, getTCPConnectionURL(userName, password));
            System.out.println("TCPConnectionURL: = " + getTCPConnectionURL(userName, password));
            initContext = new InitialContext(properties);
        } catch (NamingException e) {
            System.out.println("Can not create initial context with given parameters." + e);
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
}
