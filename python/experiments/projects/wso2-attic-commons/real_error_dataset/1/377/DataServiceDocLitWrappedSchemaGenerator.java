package org.wso2.ws.dataservice;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axis2.description.AxisMessage;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.description.java2wsdl.Java2WSDLConstants;
import org.apache.axis2.description.java2wsdl.TypeTable;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaForm;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.utils.NamespaceMap;
import org.wso2.ws.dataservice.beans.Param;

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
*
*
*/
/**
 * This class is used to generate XML Schema for Data Service
 */
public class DataServiceDocLitWrappedSchemaGenerator {

    private AxisService axisService;
    protected Map schemaMap = new Hashtable();
    protected TypeTable typeTable = new TypeTable();
    protected String schemaTargetNameSpace;
    protected String schema_namespace_prefix;
    public static final String NAME_SPACE_PREFIX = "ax2";// axis2 name space

    private static int prefixCount = 1;
    protected String attrFormDefault = null;

    protected String elementFormDefault = null;

    protected XmlSchemaCollection xmlSchemaCollection = new XmlSchemaCollection();

    protected Map targetNamespacePrefixMap = new Hashtable();
    private Map queryRefMap;

    public DataServiceDocLitWrappedSchemaGenerator(AxisService axisService, Map queryRefMap) {
        this.axisService = axisService;
        schemaTargetNameSpace = axisService.getTargetNamespace();
        axisService.setSchemaTargetNamespace(schemaTargetNameSpace);
        schema_namespace_prefix = axisService.getSchemaTargetNamespacePrefix();
        this.queryRefMap = queryRefMap;
    }

    /**
     * This go through AxisService and generate XMLSchema
     */
    public void generateSchema() {
        Iterator operations = axisService.getOperations();
        while (operations.hasNext()) {
            AxisOperation axisOperation = (AxisOperation) operations.next();
            generateSchemaforOperation(axisOperation);
        }
        axisService.addSchema(schemaMap.values());
//        try {
//			axisService.printSchema(System.out);
//		} catch (AxisFault e) {
//			e.printStackTrace();
//		}
    }

    /**
     * To generate schema element for operation , this will generate doc/lit/wrapped type schema
     *
     * @param axisOperation : AxisOperation
     */
    private void generateSchemaforOperation(AxisOperation axisOperation) {
        String operationName = axisOperation.getName().getLocalPart();
        AxisMessage inMessage = axisOperation.getMessage(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
        Parameter callQueryParamter = axisOperation.getParameter(DBConstants.CALL_QUERY_ELEMENT);
        if (callQueryParamter == null) {
            return;
        }
        CallQuery callQuery = (CallQuery) callQueryParamter.getValue();
        if (inMessage != null) {
            inMessage.setName(operationName + Java2WSDLConstants.MESSAGE_SUFFIX);
        }
        generateSchemaForInput(operationName, callQuery, inMessage);
        //processing outMessage
        AxisMessage outMessage = axisOperation.getMessage(WSDLConstants.MESSAGE_LABEL_OUT_VALUE);
        outMessage.setName(operationName + Java2WSDLConstants.RESPONSE);
        if (callQuery.getElementName() != null) {
        	//generateSchemaForOutput(callQuery, false);
            generateSchemaForOutput(callQuery, true);
            outMessage.setElementQName(typeTable.getQNamefortheType(callQuery.getElementName()));
        }
    }

    private void generateSchemaForOutput(CallQuery callQuery , boolean addComplexType) {
        XmlSchemaComplexType methodSchemaType =
                createSchemaTypeForMethodPart(callQuery.getElementName(), callQuery , addComplexType);
        XmlSchemaSequence sequence = new XmlSchemaSequence();
        methodSchemaType.setParticle(sequence);
        QName rawQName = generateSchemaForRaw(callQuery);
        XmlSchemaElement elt1 = new XmlSchemaElement();
        elt1.setName(callQuery.getRawName());
        elt1.setSchemaTypeName(rawQName);
        sequence.getItems().add(elt1);
        elt1.setMinOccurs(0);
        elt1.setNillable(true);
        elt1.setMaxOccurs(Long.MAX_VALUE);
        //elt1.setQName(rawQName);

    }

    private QName generateSchemaForRaw(CallQuery callQuery) {
        //XmlSchema xmlSchema = getXmlSchema(schemaTargetNameSpace);
    	XmlSchema xmlSchema = getXmlSchema(callQuery.getDefaultNamespace());    		
        
//        QName elementName =
//                new QName(this.schemaTargetNameSpace, callQuery.getRawName(),
//                        this.schema_namespace_prefix);
    	
        QName elementName = new QName(callQuery.getDefaultNamespace(), callQuery.getRawName(),
                    callQuery.getNsPrefix());
        
        XmlSchemaComplexType methodSchemaType = getComplexTypeForElement(xmlSchema, elementName);
        if (methodSchemaType == null) {
            methodSchemaType = new XmlSchemaComplexType(xmlSchema);
            methodSchemaType.setName(callQuery.getRawName());
            xmlSchema.getItems().add(methodSchemaType);
            xmlSchema.getElements().add(elementName, methodSchemaType);
        }
       	typeTable.addComplexSchema(callQuery.getRawName(), elementName);	
    	
        XmlSchemaSequence sequence = new XmlSchemaSequence();
        methodSchemaType.setParticle(sequence);
        ArrayList outParams = callQuery.getOutputParms();
        for (int i = 0; i < outParams.size(); i++) {
            Param param = (Param)outParams.get(i);
            generateSchemaForQueryElement(param, sequence);
        }
        ArrayList hrefs = callQuery.getQueryRefList();
        if (hrefs != null) {
            for (int i = 0; i < hrefs.size(); i++) {
                String ref = (String) hrefs.get(i);
                QName refQname = typeTable.getQNamefortheType(ref);
                if (refQname == null) {
                    CallQuery query = (CallQuery) queryRefMap.get(ref);
                    //if (query == null) {
                    if (query != null) {
                        refQname = generateTypeforRef(query);
                    }
                    if (refQname != null) {
                        XmlSchemaElement elt1 = new XmlSchemaElement();
                        elt1.setName(query.getElementName());
                        elt1.setQName(refQname);
                        elt1.setSchemaTypeName(refQname);
                        sequence.getItems().add(elt1);
                        elt1.setMinOccurs(0);
                        elt1.setNillable(true);
                    }
                }
            }
        }
        return typeTable.getQNamefortheType(callQuery.getRawName());
    }

    private QName generateTypeforRef(CallQuery query) {
        if (query.getElementName() != null) {
            generateSchemaForOutput(query, true);
            return typeTable.getQNamefortheType(query.getElementName());
        }
        return null;
    }

    private void generateSchemaForQueryElement(Param param, XmlSchemaSequence sequence) {
        QName schemaTypeName = typeTable.getSimpleSchemaTypeName(param.getSqlType());
        if (schemaTypeName == null) {
            //something has gone wrong.
        } else {
            XmlSchemaElement elt1 = new XmlSchemaElement();
            elt1.setName(param.getName());
            elt1.setSchemaTypeName(schemaTypeName);
            sequence.getItems().add(elt1);
            elt1.setMinOccurs(0);
            elt1.setNillable(true);

        }
    }

    private void generateSchemaForInput(String operationName, CallQuery callQuery, AxisMessage inMessage) {
        XmlSchemaSequence sequence = new XmlSchemaSequence();
        XmlSchemaComplexType methodSchemaType = createSchemaTypeForMethodPart(operationName,callQuery , false);
        methodSchemaType.setParticle(sequence);

        ArrayList inputParams = callQuery.getInputParms();
        for (int i = 0; i < inputParams.size(); i++) {
            Param param = (Param) inputParams.get(i);
            if("IN".equals(param.getType()) 
            		|| "INOUT".equals(param.getType())){
            	generateSchemaForQueryElement(param, sequence);
            }            
        }
        inMessage.setElementQName(typeTable.getQNamefortheType(operationName));
    }

    private XmlSchemaComplexType createSchemaTypeForMethodPart(String operationName,CallQuery callQuery ,
                                                               boolean addComplextype) {
        XmlSchema xmlSchema = getXmlSchema(callQuery.getDefaultNamespace());
        QName elementName =
                new QName(callQuery.getDefaultNamespace(), operationName, callQuery.getNsPrefix());

        XmlSchemaComplexType complexType = getComplexTypeForElement(xmlSchema, elementName);
        if (complexType == null) {
            complexType = new XmlSchemaComplexType(xmlSchema);

            XmlSchemaElement globalElement = new XmlSchemaElement();
            globalElement.setName(operationName);
            globalElement.setQName(elementName);

            if (addComplextype) {
                complexType.setName(operationName);
                xmlSchema.getItems().add(complexType);
                globalElement.setSchemaTypeName(complexType.getQName());
            } else {
                globalElement.setSchemaType(complexType);
            }
             xmlSchema.getItems().add(globalElement);
            xmlSchema.getElements().add(elementName, globalElement);
        }
        typeTable.addComplexSchema(operationName, elementName);        	
        
        return complexType;
    }

    private XmlSchema getXmlSchema(String targetNamespace) {
        XmlSchema xmlSchema;

        if ((xmlSchema = (XmlSchema) schemaMap.get(targetNamespace)) == null) {
            String targetNamespacePrefix;

            if (targetNamespace.equals(schemaTargetNameSpace) &&
                    schema_namespace_prefix != null) {
                targetNamespacePrefix = schema_namespace_prefix;
            } else {
                targetNamespacePrefix = generatePrefix();
            }


            xmlSchema = new XmlSchema(targetNamespace, xmlSchemaCollection);
            xmlSchema.setAttributeFormDefault(getAttrFormDefaultSetting());
            xmlSchema.setElementFormDefault(getElementFormDefaultSetting());
            //xmlSchema.setAttributeFormDefault(new XmlSchemaForm(XmlSchemaForm.QUALIFIED));
            //xmlSchema.setElementFormDefault(new XmlSchemaForm(XmlSchemaForm.QUALIFIED));
            
            


            targetNamespacePrefixMap.put(targetNamespace, targetNamespacePrefix);
            schemaMap.put(targetNamespace, xmlSchema);

            NamespaceMap prefixmap = new NamespaceMap();
            prefixmap.put(Java2WSDLConstants.DEFAULT_SCHEMA_NAMESPACE_PREFIX,
                    Java2WSDLConstants.URI_2001_SCHEMA_XSD);
            prefixmap.put(targetNamespacePrefix, targetNamespace);
            xmlSchema.setNamespaceContext(prefixmap);
        }
        return xmlSchema;
    }


    protected String generatePrefix() {
        return NAME_SPACE_PREFIX + prefixCount++;
    }

    protected XmlSchemaForm getAttrFormDefaultSetting() {
        if (Java2WSDLConstants.FORM_DEFAULT_UNQUALIFIED.equals(getAttrFormDefault())) {
            return new XmlSchemaForm(XmlSchemaForm.UNQUALIFIED);
        } else {
            return new XmlSchemaForm(XmlSchemaForm.QUALIFIED);
        }
    }

    protected XmlSchemaForm getElementFormDefaultSetting() {
        if (Java2WSDLConstants.FORM_DEFAULT_UNQUALIFIED.equals(getElementFormDefault())) {
            return new XmlSchemaForm(XmlSchemaForm.UNQUALIFIED);
        } else {
            return new XmlSchemaForm(XmlSchemaForm.QUALIFIED);
        }
    }

    public String getAttrFormDefault() {
        return attrFormDefault;
    }

    public void setAttrFormDefault(String attrFormDefault) {
        this.attrFormDefault = attrFormDefault;
    }

    public String getElementFormDefault() {
        return elementFormDefault;
    }

    public void setElementFormDefault(String elementFormDefault) {
        this.elementFormDefault = elementFormDefault;
    }

    protected XmlSchemaComplexType getComplexTypeForElement(XmlSchema xmlSchema, QName name) {
        Iterator iterator = xmlSchema.getItems().getIterator();
        while (iterator.hasNext()) {
            XmlSchemaObject object = (XmlSchemaObject) iterator.next();
            if (object instanceof XmlSchemaElement && ((XmlSchemaElement) object).getQName().equals(name)) {
                return (XmlSchemaComplexType) ((XmlSchemaElement) object).getSchemaType();
            }
            else if (object instanceof XmlSchemaComplexType && ((XmlSchemaComplexType) object).getQName().equals(name)) {
                //return (XmlSchemaComplexType) ((XmlSchemaElement) object).getSchemaType();
            	return (XmlSchemaComplexType) object;
            } 
            
        }
        return null;
    }
}
