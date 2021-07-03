/*
 * Copyright 2005,2006 WSO2, Inc. http://www.wso2.org
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
package org.wso2.codegen.service;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.AxisService;

import java.net.MalformedURLException;
import java.net.URL;


public class WSDLInfoService {


    /**
     * funing whether a wsdl is available for processing
     *
     * @param serviceName
     * @return
     * @throws AxisFault
     * @deprecated
     */
    public boolean isWSDLAvailable(String serviceName)
            throws AxisFault {
        AxisService service = MessageContext.getCurrentMessageContext().getConfigurationContext()
                .getAxisConfiguration().getService(serviceName);

        return service.isWsdlFound();
    }

    public WSDLMetaData listServicesAndPorts(String serviceName, String wsdlVersion, String wsdlLocation)
            throws AxisFault {

        WSDLMetaDataFactory wsdlMetaDataFactory = WSDLMetaDataFactory.getFactory(wsdlVersion.trim(),
                                                                                 serviceName.trim(),
                                                                                 MessageContext.getCurrentMessageContext());
        URL url;
        try {
            url = new URL(wsdlLocation);
        } catch (MalformedURLException e) {
            throw AxisFault.makeFault(e);
        }
        return wsdlMetaDataFactory.createWSDLMetaData(url);
    }


}
