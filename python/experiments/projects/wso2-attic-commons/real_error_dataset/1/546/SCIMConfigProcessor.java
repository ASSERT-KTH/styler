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
package org.wso2.charon.core.config;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.wso2.charon.core.exceptions.CharonException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class responsible for building a programmatic representation of provisioning-config.xml.
 * Any application using this library can either pass the file path or root config element
 * in expected format to get it parsed.
 */
public class SCIMConfigProcessor {

    public SCIMConfig buildConfigFromFile(String filepath) throws CharonException {
        try {
            InputStream inputStream = null;
            File provisioningConfig = new File(filepath);
            if (provisioningConfig.exists()) {
                inputStream = new FileInputStream(provisioningConfig);
            }
            StAXOMBuilder staxOMBuilder = new StAXOMBuilder(inputStream);
            OMElement documentElement = staxOMBuilder.getDocumentElement();
            if (inputStream != null) {
                inputStream.close();
            }
            return buildConfigFromRootElement(documentElement);
        } catch (FileNotFoundException e) {
            throw new CharonException(SCIMConfigConstants.PROVISIONING_CONFIG_NAME + "not found.");
        } catch (XMLStreamException e) {
            throw new CharonException("Error in building the configuration file.");
        } catch (IOException e) {
            throw new CharonException("Error in building the configuration file.");
        }
    }

    public SCIMConfig buildConfigFromRootElement(OMElement rootElement) {

        //build scim config
        SCIMConfig scimConfig = new SCIMConfig();

        OMElement scimConsumersElement = rootElement.getFirstChildWithName(
                new QName(SCIMConfigConstants.ELEMENT_NAME_SCIM_CONSUMERS));

        OMElement scimProvidersElement = rootElement.getFirstChildWithName(
                new QName(SCIMConfigConstants.ELEMENT_NAME_SCIM_PROVIDERS));

        //iterate over the individual elements and create scim provider map
        Iterator<OMElement> scimProvidersIterator = scimProvidersElement.getChildrenWithName(new QName(
                SCIMConfigConstants.ELEMENT_NAME_SCIM_PROVIDER));

        //build providers map
        if (scimProvidersIterator != null) {
            Map<String, SCIMProvider> providers = buildSCIMProviderMap(scimProvidersIterator);
            scimConfig.setProvidersMap(providers);
        }

        //iterate over the individual elements and create scim consumer map
        Iterator<OMElement> scimConsumersIterator = scimConsumersElement.getChildrenWithName(new QName(
                SCIMConfigConstants.ELEMENT_NAME_SCIM_CONSUMER));

        //build consumers map
        if (scimConsumersIterator != null) {
            Map<String, SCIMConsumer> consumers = buildSCIMConsumersMap(scimConsumersIterator);
            scimConfig.setConsumersMap(consumers);
        }

        return scimConfig;
    }

    private Map<String, SCIMProvider> buildSCIMProviderMap(Iterator<OMElement> providersIterator) {

        Map<String, SCIMProvider> providersMap = new ConcurrentHashMap<String, SCIMProvider>();

        while (providersIterator.hasNext()) {
            OMElement providerElement = providersIterator.next();
            SCIMProvider scimProvider = new SCIMProvider();
            Map<String, String> propertiesMap = new HashMap<String, String>();

            //get provider id
            String providerId = providerElement.getAttributeValue(new QName(
                    SCIMConfigConstants.ATTRIBUTE_NAME_ID));
            scimProvider.setId(providerId);

            //read provider properties
            Iterator<OMElement> propertiesIterator = providerElement.getChildrenWithName(
                    new QName(SCIMConfigConstants.ELEMENT_NAME_PROPERTY));
            while (propertiesIterator.hasNext()) {
                OMElement propertyElement = propertiesIterator.next();
                String propertyName = propertyElement.getAttributeValue(
                        new QName(SCIMConfigConstants.ATTRIBUTE_NAME_NAME));
                String propertyValue = propertyElement.getText();
                propertiesMap.put(propertyName, propertyValue);
            }
            scimProvider.setProperties(propertiesMap);
            providersMap.put(providerId, scimProvider);
        }
        return providersMap;
    }

    private Map<String, SCIMConsumer> buildSCIMConsumersMap(Iterator<OMElement> consumersIterator) {

        Map<String, SCIMConsumer> consumersMap = new ConcurrentHashMap<String, SCIMConsumer>();

        //iterate
        while (consumersIterator.hasNext()) {
            SCIMConsumer scimConsumer = new SCIMConsumer();
            OMElement scimConsumerElement = consumersIterator.next();
            //get consumer id
            String consumerId = scimConsumerElement.getAttributeValue(new QName(
                    SCIMConfigConstants.ATTRIBUTE_NAME_ID));

            //get providers
            Iterator<OMElement> scimProviders = scimConsumerElement.getChildrenWithName(
                    new QName(SCIMConfigConstants.ELEMENT_NAME_SCIM_PROVIDER));

            Map<String, SCIMProvider> providersMap = new HashMap<String, SCIMProvider>();

            //iterate through providers and build the consumer specific provider map
            if (scimProviders != null) {
                while (scimProviders.hasNext()) {
                    SCIMProvider scimProvider = new SCIMProvider();

                    OMElement scimProviderElement = scimProviders.next();
                    //get attributes if exist
                    String providerId = scimProviderElement.getAttributeValue(new QName(SCIMConfigConstants.ATTRIBUTE_NAME_ID));
                    scimProvider.setId(providerId);

                    String userName = scimProviderElement.getAttributeValue(new QName(
                            SCIMConfigConstants.ATTRIBUTE_NAME_USERNAME));
                    if (userName != null) {
                        scimProvider.setProperty(SCIMConfigConstants.ELEMENT_NAME_USERNAME, userName);
                    }
                    String password = scimProviderElement.getAttributeValue(new QName(
                            SCIMConfigConstants.ATTRIBUTE_NAME_PASSWORD));
                    if (password != null) {
                        scimProvider.setProperty(SCIMConfigConstants.ELEMENT_NAME_PASSWORD, password);
                    }

                    //read properties if exist
                    Map<String, String> providerProperties = new HashMap<String, String>();
                    Iterator<OMElement> propertiesMap = scimConsumerElement.getChildrenWithName(
                            new QName(SCIMConfigConstants.ELEMENT_NAME_PROPERTY));
                    if (propertiesMap != null) {
                        //iterate through propertiesMap
                        while (propertiesMap.hasNext()) {
                            OMElement property = propertiesMap.next();
                            String propertyName = property.getAttributeValue(new QName(
                                    SCIMConfigConstants.ATTRIBUTE_NAME_NAME));
                            String propertyValue = property.getText();
                            providerProperties.put(propertyName, propertyValue);
                        }
                    }
                    scimProvider.setProperties(providerProperties);
                    providersMap.put(providerId, scimProvider);
                }
                scimConsumer.setScimProviders(providersMap);
            }
            consumersMap.put(consumerId, scimConsumer);
        }
        return consumersMap;
    }
}
