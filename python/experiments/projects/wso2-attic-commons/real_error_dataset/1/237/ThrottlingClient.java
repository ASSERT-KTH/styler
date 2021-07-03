import org.apache.neethi.Policy;
 import org.apache.neethi.PolicyEngine;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.llom.util.AXIOMUtil;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.rampart.policy.model.RampartConfig;
import org.apache.rampart.policy.model.CryptoConfig;
import org.apache.rampart.RampartMessageData;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.ws.security.WSPasswordCallback;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.callback.CallbackHandler;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

public class ThrottlingClient {
    public static void main(String[] args) {
        ThrottlingClient throttlingCl = new ThrottlingClient();
        OMElement result = null;
        try{
            result = throttlingCl.runThrottlingClient();           
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private OMElement runThrottlingClient() throws Exception {
        Properties properties = new Properties();
        FileInputStream freader = new FileInputStream("."+File.separator+"src"+File.separator+"client.properties");
        properties.load(freader);
        String carbon_home = properties.getProperty("carbon.home");
        String client_repo = properties.getProperty("client_repo");
        String endpoint_https    = properties.getProperty("endpoint_https");
        String endpoint_http   = properties.getProperty("endpoint_http");
        String client_home = properties.getProperty("client_home");
        int cCounter = 0;

        OMElement result = null;

        String SoapAction = "urn:square";
//        String body ="<p:hello xmlns:p=\"http://ode/bpel/unit-test.wsdl\"> <TestPart>Wso2</TestPart> </p:hello>";
//        String body ="<p:greet xmlns:p=\"http://www.wso2.org/types\"> <name>Wso2</name> </p:greet>";
        String body ="<p:square xmlns:p=\"http://ws.apache.org/axis2\">\n" +
                "         <args0 xmlns=\"http://ws.apache.org/axis2\">5</args0>\n" +
                "      </p:square>";

        try {
            ConfigurationContext ctx = ConfigurationContextFactory.createConfigurationContextFromFileSystem(client_repo, null);
            ServiceClient sc = new ServiceClient(ctx, null);
            sc.engageModule("rampart");
            sc.engageModule("addressing");

            Options opts = new Options();
            opts.setTo(new EndpointReference(endpoint_http));
            opts.setAction(SoapAction);

            sc.setOptions(opts);
//            for(int i=0; i<=15; i++){
               result = sc.sendReceive(AXIOMUtil.stringToOM(body));
//               result = sc.sendReceive(getPayload("Hello world"));
               System.out.println(result.getFirstElement().getText());
//               cCounter = cCounter + 1;                   
//            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return result;
    }

    private static OMElement getPayload(String value) {
		OMFactory factory = null;
		OMNamespace ns = null;
		OMElement elem = null;
		OMElement childElem = null;

		factory = OMAbstractFactory.getOMFactory();
		ns = factory.createOMNamespace("http://www.wso2.org/types", "ns1");
		elem = factory.createOMElement("hello", ns);
		childElem = factory.createOMElement("in", null);
		childElem.setText(value);
		elem.addChild(childElem);

		return elem;
	}
}
