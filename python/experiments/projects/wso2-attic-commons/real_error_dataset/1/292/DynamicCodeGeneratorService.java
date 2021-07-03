/*
 * Copyright 2004,2005 The Apache Software Foundation.
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
package org.wso2.codegen.service.ajax;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.wso2.codegen.service.WSDLMetaDataFactory;
import org.wso2.utils.WSO2Constants;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Map;
/*
 *
 */

public class DynamicCodeGeneratorService {

    private static Log log = LogFactory.getLog(DynamicCodeGeneratorService.class);


    public String generate(String serviceName) throws AxisFault {

        MessageContext currentMessageContext = MessageContext.getCurrentMessageContext();
        AxisConfiguration axisConfiguration =
                currentMessageContext.getConfigurationContext().getAxisConfiguration();
        AxisService service = axisConfiguration.getService(serviceName);
        if (service == null) {
            String message = "Service " + serviceName + " cannot be found.";
            log.error(message);
            throw new AxisFault(message);
        }
        Parameter enableMTOM = service.getParameter(Constants.Configuration.ENABLE_MTOM);
        if (enableMTOM != null && enableMTOM.getValue().equals("true")) {
            throw new AxisFault("Try It feature is not available for MTOM enabled services");
        }

        Object parameterValue = service.getParameterValue("WSDLSupplier");
        if (parameterValue != null) {
            String message = "RPC/ENC WSDL found. Axis2 does not support RPC/ENC encoding scheme";
            log.warn(message);
            throw new AxisFault(message);
        }

        String uuidTryit;
        WSDLMetaDataFactory wsdlMetaDataFactory = WSDLMetaDataFactory
                .getFactory("2.0", serviceName, MessageContext.getCurrentMessageContext());

        WSDLMetaDataFactory.UniqueOutputDirInfo uniqueOutputDirInfo =
                wsdlMetaDataFactory.generateUniqueCodegenOutputDir();

        uuidTryit = uniqueOutputDirInfo.getUuid();


        String outputDir =
                wsdlMetaDataFactory.generateUniqueDir("dynamic_codegen", uniqueOutputDirInfo)
                        .getOutputDir();

        String wsdlFileURI = wsdlMetaDataFactory.getWSDLFileURI(outputDir);

        //Generating the .sig foramat using wsdl2sig.xslt to intermediate DOM Document. This doc will be
        // used to transformed with tryit.xslt and jsstub.xslt.

        try {
            InputStream wsdl2InStream = new FileInputStream(new File(wsdlFileURI));
            InputStream wsdl2sigXSLTStream =
                    getClass().getClassLoader().getResourceAsStream("wsdl2sig.xslt");

            Source wsdl2Source = new StreamSource(wsdl2InStream);
            Source wsdl2sigXSLTSource = new StreamSource(wsdl2sigXSLTStream);


            DocumentBuilder docB = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = docB.newDocument();

            //do the first transformation
            Result result = new DOMResult(doc);
            Transformer transformer =
                    TransformerFactory.newInstance().newTransformer(wsdl2sigXSLTSource);
            transformer.transform(wsdl2Source, result);

            ///////////////////////////////////////////////////
//            System.out.println(DOM2Writer.nodeToString(doc));
            ////////////////////////////////////////////////////

            DOMSource xmlSource = new DOMSource(doc);

            Map fileResourcesMap =
                    (Map) MessageContext.getCurrentMessageContext().getConfigurationContext()
                            .getProperty(WSO2Constants.FILE_RESOURCE_MAP);

            if (fileResourcesMap == null) {
                fileResourcesMap = new Hashtable();
                MessageContext.getCurrentMessageContext().getConfigurationContext()
                        .setProperty(WSO2Constants.FILE_RESOURCE_MAP,
                                     fileResourcesMap);
            }


            String uuidJsstub;
            WSDLMetaDataFactory.UniqueOutputDirInfo jsstubUniqueOutputDirInfo =
                    wsdlMetaDataFactory.generateUniqueCodegenOutputDir();
            uuidJsstub = jsstubUniqueOutputDirInfo.getUuid();

            String jstubOutputDir = wsdlMetaDataFactory
                    .generateUniqueDir("dynamic_codegen", jsstubUniqueOutputDirInfo).getOutputDir();
            File jstubOutputDirFile = new File(jstubOutputDir);
            jstubOutputDirFile.mkdirs();
            File jsStubFle = new File(jstubOutputDir, serviceName + ".stub.js");

            OutputStream jsStubOutputStream = new FileOutputStream(jsStubFle);
            Result jsStubResult = new StreamResult(jsStubOutputStream);
            InputStream jsStubXSLTStream =
                    getClass().getClassLoader().getResourceAsStream("jsstub.xslt");
            Source jsStubXSLSource = new StreamSource(jsStubXSLTStream);
            transformer = TransformerFactory.newInstance().newTransformer(jsStubXSLSource);
            transformer.transform(xmlSource, jsStubResult);

            FileFilter jsStubFileFilter = new JSStubFilter();
            String absolutePath = null;
            File[] files0 = jstubOutputDirFile.listFiles(jsStubFileFilter);
            if ((files0 != null) && (files0[0] != null) &&
                (files0[0].getAbsoluteFile() != null)) {
                absolutePath = files0[0].getAbsoluteFile().getAbsolutePath();
                fileResourcesMap.put(uuidJsstub, absolutePath);
            }

            if (absolutePath == null) {
                throw new AxisFault(
                        DynamicCodeGeneratorService.class.getName() + " cannot continue" +
                        " since an instance of jsstub.xslt is not available");
            }


            InputStream tryItXSLTStream =
                    getClass().getClassLoader().getResourceAsStream("tryit.xslt");
            Source tryItXSLSource = new StreamSource(tryItXSLTStream);
            File tryItOutFile = new File(outputDir, serviceName + ".html");
            OutputStream tryItOutFileStream = new FileOutputStream(tryItOutFile);
            Result tryItResult = new StreamResult(tryItOutFileStream);
            transformer = TransformerFactory.newInstance().newTransformer(tryItXSLSource);
            transformer.setParameter("wsrequest-location", "js/WSRequest.js");
            transformer.setParameter("stub-location", "filedownload?id=" + uuidJsstub);


            String injectValue =
                    "<div>" +
                    "<h4><a href=\"#\" onClick=\"javascript:top.showServiceInitializer(); return false;\">Services</a>&nbsp;&gt;&nbsp;" +
                    "<a href=\"#\" onClick=\"javascript:top.listServiceData('" + serviceName + "'); return false;\">" +
                    serviceName + "</a>&nbsp;&gt;&nbsp;" + "Try Web Service" +
                    "</h4>" +
                    "</div>";

            transformer.setParameter("show-alternate", "false");
            transformer.setParameter("breadcrumbs", injectValue);
            transformer.transform(xmlSource, tryItResult);

            FileFilter tryItFileFilter = new TryItFilter();
            File[] files1 = new File(outputDir).listFiles(tryItFileFilter);
            if ((files1 != null) && (files1[0] != null) &&
                (files1[0].getAbsoluteFile() != null)) {
                fileResourcesMap.put(uuidTryit, files1[0].getAbsoluteFile().getAbsolutePath());
            }
            log.debug("Try it page has been generated for the service: " + serviceName);
            return "filedownload" + "?id=" + uuidTryit;
        } catch (Exception e) {
            log.error(e);
            throw AxisFault.makeFault(e);
        } 

    }

    private class TryItFilter implements FileFilter {
        public boolean accept(File f) {
            return f.getName().endsWith(".html");
        }
    }


    private class JSStubFilter implements FileFilter {
        public boolean accept(File f) {
            return f.getName().endsWith(".stub.js");
        }
    }

}
