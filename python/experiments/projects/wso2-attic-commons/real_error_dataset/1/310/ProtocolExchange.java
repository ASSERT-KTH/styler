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
package org.wso2.xkms2.core;

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.impl.dom.DOOMAbstractFactory;
import org.apache.axiom.om.impl.dom.factory.OMDOMFactory;
import org.apache.axiom.om.util.UUIDGenerator;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.w3c.dom.Document;
import org.wso2.xkms2.FindBuilder;
import org.wso2.xkms2.LocateResult;
import org.wso2.xkms2.RecoverResult;
import org.wso2.xkms2.RegisterResult;
import org.wso2.xkms2.ReissueResult;
import org.wso2.xkms2.RequestAbstractType;
import org.wso2.xkms2.ResponseMechanism;
import org.wso2.xkms2.ResultType;
import org.wso2.xkms2.RevokeResult;
import org.wso2.xkms2.ValidateResult;
import org.wso2.xkms2.XKMSElement;
import org.wso2.xkms2.XKMSException;
import org.wso2.xkms2.builder.ElementBuilder;
import org.wso2.xkms2.builder.RequestAbstractTypeBuilder;
import org.wso2.xkms2.util.XKMSUtil;

/**
 * KMS protocol exchanges consist of a sequence of either one or two request
 * response pairs. <p/> The XKMS protocol consists of pairs of requests and
 * responses. The XKMS protocol binding allows for the case in which an
 * additional request/response round trip is required to support cases such as
 * pending responses and two-phase requests for replay attack protection. <p/>
 * Each XKMS response message contains a MajorResult code that determines
 * whether the response is final or further processing is required. <p/> XKMS
 * supports two processing modes, synchronous processing and asynchronous
 * processing. <p/> XKMS requests may employ a two-phase request protocol to
 * protect against a denial of service attack. <p/> The <ResponseMechanism>
 * element in the request specifies one or more strings included in the request
 * that specify extended protocol mechanisms that the client supports in
 * connection with a request. <p/> Initial implementation of this class is based
 * on synchronus and two-way protocol. Asynchronous processing will be dealt
 * with later.
 */

public class ProtocolExchange {

    /**
     * Retruned an DOOMElement
     * 
     * @param inMsgCtx
     * @return
     * @throws AxisFault
     * @throws XKMSException
     */
    public OMElement exchangeServer(MessageContext inMsgCtx) throws AxisFault,
            XKMSException {

        ProtocolExchange.BuildElementHelper buildElementHelper = buildElement(inMsgCtx);
        XKMSElement xkmsElementObj = buildElementHelper.getXkmsElement();
        ElementBuilder builder = buildElementHelper.getBuilder();
        QName qName = buildElementHelper.getQName();

        RequestAbstractTypeBuilder requestATBuider = (RequestAbstractTypeBuilder) builder;
        RequestAbstractType requestAbstractType = requestATBuider
                .getRequestAbstractType();

        List responseMechanismList = requestAbstractType.getResponseMechanism();

        ResponseMechanism[] responseMechanisms = responseMechanismList != null ? (ResponseMechanism[]) responseMechanismList
                .toArray(new ResponseMechanism[responseMechanismList.size()])
                : null;
        // Get a sepearte Document for response
        OMFactory fac = DOOMAbstractFactory.getOMFactory();
        OMDOMFactory omDomFac = (OMDOMFactory) fac;
        Document doc = omDomFac.getDocument();

        XKMSRequestData requestData = new XKMSRequestData();
        requestData.setMessageContext(inMsgCtx);
        requestData.setRequest((RequestAbstractType) xkmsElementObj);
        requestData.setDocument(doc);

        if (responseMechanisms == null || responseMechanisms.length == 0) {
            // Sync process
            XKMSServiceExecutor xkmsServiceExecutor = XKMSServiceExecutorManager
                    .get(qName.getLocalPart());
            ResultType result = xkmsServiceExecutor.execute(requestData, inMsgCtx);
            return getAsOMElement(result, fac);

        }

        throw new UnsupportedOperationException("TODO ");

    }

    // TODO need to fix this
    public XKMSElement exchangeClient(MessageContext inMsgCtx)
            throws XKMSException {
        ProtocolExchange.BuildElementHelper buildElementHelper = buildElement(inMsgCtx);
        XKMSElement xkmsElementObj = buildElementHelper.getXkmsElement();
        // TODO protocol handling.
        return xkmsElementObj;

    }

    private BuildElementHelper buildElement(MessageContext msgCtx)
            throws XKMSException {

        SOAPEnvelope env = msgCtx.getEnvelope();
        OMElement llomElement = env.getBody().getFirstElement();

        if (llomElement == null) {
            throw new XKMSException(XKMSException.INCOMPLETE, "RequiredDataMissing");    
        }

        XKMSUtil.DOOMElementMetadata doomMetadata = XKMSUtil
                .getDOOMElement(llomElement);
        OMElement xkmsElement = doomMetadata.getElement();
        QName qName = xkmsElement.getQName();

        ElementBuilder builder = FindBuilder.find(qName.getLocalPart());
        XKMSElement xkmsObj = builder.buildElement(xkmsElement);

        return new BuildElementHelper(qName, xkmsObj, builder);

    }

    public String generateRequestId() {
        return UUIDGenerator.getUUID();

    }

    private class BuildElementHelper {
        private QName qName;

        private XKMSElement xkmsElement;

        private ElementBuilder builder;

        BuildElementHelper(QName qName, XKMSElement xkmsElement,
                ElementBuilder builder) {
            this.qName = qName;
            this.xkmsElement = xkmsElement;
            this.builder = builder;
        }

        public QName getQName() {
            return qName;
        }

        public XKMSElement getXkmsElement() {
            return xkmsElement;
        }

        public ElementBuilder getBuilder() {
            return builder;
        }
    }

    private OMElement getAsOMElement(ResultType resultType, OMFactory factory)
            throws XKMSException {

        if (resultType instanceof RegisterResult) {
            return ((RegisterResult) resultType).serialize(factory);
            
        } else if (resultType instanceof ReissueResult) {
            return ((ReissueResult) resultType).serialize(factory);
            
        } else if (resultType instanceof RecoverResult) {
            try {
                ((RecoverResult) resultType).serialize(factory);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return ((RecoverResult) resultType).serialize(factory);
            
        } else if (resultType instanceof RevokeResult) {
            return ((RevokeResult) resultType).serialize(factory);
            
        } else if (resultType instanceof LocateResult) {
            return ((LocateResult) resultType).serialize(factory);   
            
        } else if (resultType instanceof ValidateResult) {
            return ((ValidateResult) resultType).serialize(factory);
            
        } else {
            return null;
        }

    }
}
