//import org.apache.axis2.addressing.EndpointReference;
//import org.apache.axis2.client.Options;
//import org.apache.axis2.client.ServiceClient;
//import org.apache.axis2.rpc.client.RPCServiceClient;
//import org.apache.axis2.databinding.utils.Constants;
//import static org.apache.axis2.databinding.utils.Constants.*;
//import org.apache.axiom.om.OMElement;
//import org.apache.axiom.om.OMNamespace;
//import org.apache.axiom.om.OMAbstractFactory;
//import org.apache.axiom.om.OMFactory;
//import org.apache.abdera.protocol.server.provider.managed.Configuration;
//
//import javax.xml.namespace.QName;
//public class RestClient {
//    public static void main(String[] args) throws Exception {
//         ServiceClient client = new ServiceClient();
//         Options opts = new Options();
//         opts.setTo(new EndpointReference("http://localhost:8281/services/SquareService/square"));
//         opts.setAction("square");
//         opts.setProperty(org.apache.axis2.Constants.Configuration.ENABLE_REST, Boolean.TRUE);
//         opts.setProperty(org.apache.axis2.Constants.Configuration.HTTP_METHOD, org.apache.axis2.Constants.Configuration.HTTP_METHOD_GET);
//         client.setOptions(opts);
//         OMFactory fac = OMAbstractFactory.getOMFactory();
//         OMNamespace omNs = fac.createOMNamespace("http://ws.apache.org/axis2", "ns");
//         OMElement method = fac.createOMElement("square", omNs);
//         OMElement value = fac.createOMElement("a", omNs);
//         method.addChild(value);
//         value.setText("10");
////          OMElement value2 = fac.createOMElement("b", omNs);
////         method.addChild(value2);
////         value2.setText("10");
//         OMElement res = client.sendReceive(method);
//         System.out.println(res);
//         }
//}
