/*
 * Copyright 2008,2009 WSO2, Inc. http://www.wso2.org.
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
package org.wso2.mercury.state;

import org.wso2.mercury.context.MercuryMessageContext;
import org.wso2.mercury.util.MercuryConstants;
import org.wso2.mercury.util.MercuryClientConstants;
import org.wso2.mercury.workers.InvokerWorker;
import org.wso2.mercury.persistence.dto.*;
import org.wso2.mercury.persistence.PersistenceManager;
import org.wso2.mercury.persistence.exception.PersistenceException;
import org.wso2.mercury.message.RMApplicationMessage;
import org.wso2.mercury.message.Sequence;
import org.wso2.mercury.exception.RMMessageBuildingException;
import org.apache.axis2.engine.AxisEngine;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.addressing.AddressingConstants;
import org.apache.axis2.transport.RequestResponseTransport;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.util.MessageContextBuilder;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.WSDL2Constants;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.client.Options;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.OperationContext;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ServiceContext;
import org.apache.axiom.om.util.UUIDGenerator;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.om.impl.builder.StAXBuilder;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;
import java.util.*;
import java.io.ByteArrayInputStream;


/**
 * this provides the exactly one inorder delivery
 * used to buffer the incomming messages so that
 * later it can invoke in correct order
 * the state of the invoker buffer is determined by the
 * following variables
 * LMR - last message received
 * MIB - messages in the buffer
 * TMR - terminate message received
 * <p/>
 * these three variables forms possible 8 states
 * but states 001,100,101 can be considered as one
 * complete sate.
 * <p/>
 * External events that would change this state are
 * LMR - last message receive
 * TMR - terminate message receive
 * AMR - application message receive
 * SMA(SC) - send messages to application so that it finish messages.
 */

public class InvokerBuffer {

    private static Log log = LogFactory.getLog(InvokerBuffer.class);

    public static final int STATE_000 = 1;
    public static final int STATE_010 = 2;
    public static final int STATE_011 = 3;
    public static final int STATE_111 = 4;
    public static final int STATE_110 = 5;
    public static final int STATE_COMPLETE = 6;

    private int state;

    private long timeoutTime;

    // message buffer contains the Message ID and the RMMessage context with
    // application message inside
    private Map messageBuffer;

    private Set receivedMessageNumbers;

    // this is the last message number of the original message
    // sequence.
    private long lastMessageNumber = 0;
    private long lastAccessTime = 0;

    // this is the heighest message number send to the applicaiton
    private long lastMessageSendToApplication = 0;

    private InvokerBufferDto persistanceDto;
    private PersistenceManager persistenceManager;

    private boolean isAnonymous;

    // this is used when sending the last message correctly
    private EndpointReference acksTo;

    private InvokerWorker invokerWorker;

    public InvokerBuffer(int state) {
        this.state = state;
        this.messageBuffer = new HashMap();
        this.receivedMessageNumbers = new HashSet();
        this.lastAccessTime = System.currentTimeMillis();
    }

    public synchronized void applicatinMessageReceived(
            long messageNumber,
            MercuryMessageContext mercuryMessageContext,
            RMDSequenceDto rmdSequenceDto) throws PersistenceException {

        this.lastAccessTime = System.currentTimeMillis();
        if (!this.receivedMessageNumbers.contains(new Long(messageNumber))) {
            //change the state
            int currentState = this.state;
            switch (state) {
                case STATE_000 : {
                    state = STATE_010;
                    if (this.persistanceDto != null){
                       this.persistanceDto.setState(state);
                    }
                    break;
                }
                case STATE_010 : {
                    break;
                }
                case STATE_011 : {
                    // application message can not be received in this stage
                    break;
                }
                case STATE_111 : {
                    // application message can not be received in this stage
                    break;
                }
                case STATE_110 : {
                    // application message can not be received in this stage
                    break;
                }
                case STATE_COMPLETE : {
                    break;
                }
            }

            // here we have to assume that that there is not state change in
            // RMD Sequence if it is not a new message.
            // further always the receivedMessageNumbers in RMDSeqnce is always equals to the
            // receivedNumbers in the invoker buffer.

            if (this.persistenceManager != null){
                SequenceReceivedNumberDto sequenceReceivedNumberDto = new SequenceReceivedNumberDto();
                sequenceReceivedNumberDto.setNumber(messageNumber);
                sequenceReceivedNumberDto.setRmdSequenceID(rmdSequenceDto.getId());
                if (mercuryMessageContext.getMessageContext().getRelatesTo() != null){
                   sequenceReceivedNumberDto.setRelatesToMessageID(
                           mercuryMessageContext.getMessageContext().getRelatesTo().getValue()); 
                }

                BufferReceivedNumberDto bufferReceivedNumberDto = new BufferReceivedNumberDto();
                bufferReceivedNumberDto.setNumber(messageNumber);
                bufferReceivedNumberDto.setInternalBufferID(this.persistanceDto.getId());

                RMDMessageDto rmdMessageDto = new RMDMessageDto();
                rmdMessageDto.setMessageNumber(messageNumber);
                rmdMessageDto.setSoapEnvelope(mercuryMessageContext.getMessageContext().getEnvelope().toString());
                rmdMessageDto.setSend(false);
                rmdMessageDto.setInternalBufferID(this.persistanceDto.getId());
                rmdMessageDto.setOperationName(
                        mercuryMessageContext.getMessageContext().getAxisOperation().getName());
                rmdMessageDto.setAction(mercuryMessageContext.getMessageContext().getOptions().getAction());
                rmdMessageDto.setTo(mercuryMessageContext.getMessageContext().getTo().getAddress());
                rmdMessageDto.setReplyTo(mercuryMessageContext.getMessageContext().getReplyTo().getAddress());
                rmdMessageDto.setMessageID(mercuryMessageContext.getMessageContext().getMessageID());

                // update the persistence storage
                try {
                    persistenceManager.updateMessageDetails(rmdSequenceDto,
                            this.persistanceDto,
                            sequenceReceivedNumberDto,
                            bufferReceivedNumberDto,
                            rmdMessageDto);
                    //store the persistence dto for future updates
                    mercuryMessageContext.setRmdPersistanceDto(rmdMessageDto);
                } catch (PersistenceException e) {
                    e.printStackTrace();
                    this.state = currentState;
                    this.persistanceDto.setState(currentState);
                    log.error("Can not update the persistence manager for message" +
                            " with message number ==> " + messageNumber, e);
                    throw new PersistenceException("Can not update the persistence manager for message" +
                            " with message number ==> " + messageNumber, e);
                }

            }
            // exactly one delivary. duplicate messages are avoid
            this.messageBuffer.put(new Long(messageNumber), mercuryMessageContext);
            this.receivedMessageNumbers.add(new Long(messageNumber));
        }
    }

    public synchronized void lastMessageReceived(
            long messageNumber,
            MercuryMessageContext mercuryMessageContext,
            RMDSequenceDto rmdSequenceDto) throws PersistenceException {
        this.lastAccessTime = System.currentTimeMillis();

        if (!this.receivedMessageNumbers.contains(new Long(messageNumber))) {
            int currentState = this.state;
            //change the state
            switch (state) {
                case STATE_000 : {
                    state = STATE_110;
                    if (this.persistanceDto != null){
                        this.persistanceDto.setState(state);
                    }
                    break;
                }
                case STATE_010 : {
                    state = STATE_110;
                    if (this.persistanceDto != null){
                        this.persistanceDto.setState(state);
                    }
                    break;
                }
                case STATE_011 : {
                    // application message can not be received in this stage
                    break;
                }
                case STATE_111 : {
                    // application message can not be received in this stage
                    break;
                }
                case STATE_110 : {
                    // application message can not be received in this stage
                    break;
                }
                case STATE_COMPLETE : {
                    break;
                }
            }

            if (this.persistenceManager != null){
                // we have to udate the persistence storage
                SequenceReceivedNumberDto sequenceReceivedNumberDto = new SequenceReceivedNumberDto();
                sequenceReceivedNumberDto.setNumber(messageNumber);
                sequenceReceivedNumberDto.setRmdSequenceID(rmdSequenceDto.getId());
                if (mercuryMessageContext.getMessageContext().getRelatesTo() != null){
                   sequenceReceivedNumberDto.setRelatesToMessageID(
                           mercuryMessageContext.getMessageContext().getRelatesTo().getValue());
                }

                BufferReceivedNumberDto bufferReceivedNumberDto = new BufferReceivedNumberDto();
                bufferReceivedNumberDto.setNumber(messageNumber);
                bufferReceivedNumberDto.setInternalBufferID(this.persistanceDto.getId());

                RMDMessageDto rmdMessageDto = new RMDMessageDto();
                rmdMessageDto.setMessageNumber(messageNumber);
                rmdMessageDto.setSoapEnvelope(
                        mercuryMessageContext.getMessageContext().getEnvelope().toString());
                rmdMessageDto.setSend(false);
                rmdMessageDto.setInternalBufferID(this.persistanceDto.getId());
                rmdMessageDto.setOperationName(
                        mercuryMessageContext.getMessageContext().getAxisOperation().getName());
                rmdMessageDto.setAction(mercuryMessageContext.getMessageContext().getOptions().getAction());
                rmdMessageDto.setTo(mercuryMessageContext.getMessageContext().getTo().getAddress());
                rmdMessageDto.setReplyTo(mercuryMessageContext.getMessageContext().getReplyTo().getAddress());
                rmdMessageDto.setMessageID(mercuryMessageContext.getMessageContext().getMessageID());

                this.persistanceDto.setLastMessage(messageNumber);

                // update the persistence storage
                try {
                    persistenceManager.updateMessageDetails(rmdSequenceDto,
                            this.persistanceDto,
                            sequenceReceivedNumberDto,
                            bufferReceivedNumberDto,
                            rmdMessageDto);
                    //store the persistence dto for future updates
                    mercuryMessageContext.setRmdPersistanceDto(rmdMessageDto);
                } catch (PersistenceException e) {
                    this.state = currentState;
                    this.persistanceDto.setState(currentState);
                    // unset the last message number since it shold not have set
                    // if we don't have received the last message number.
                    this.persistanceDto.setLastMessage(0);
                    log.error("Can not update the persistence manager for message" +
                            " with message number ==> " + messageNumber, e);
                    throw new PersistenceException("Can not update the persistence manager for message" +
                            " with message number ==> " + messageNumber, e);
                }
            }

            this.lastMessageNumber = messageNumber;
            this.messageBuffer.put(new Long(messageNumber), mercuryMessageContext);
            this.receivedMessageNumbers.add(new Long(messageNumber));

        }
    }

    public boolean isMessagesCompleted() {
        // this means me must sure that we have received all the messages.
        return (lastMessageNumber != 0) && (receivedMessageNumbers.size() == lastMessageNumber);
    }

    public synchronized void doActions() throws AxisFault {
        // first send what ever possible messages to the application
        while (this.messageBuffer.keySet().contains(new Long(lastMessageSendToApplication + 1))) {
            // if we have the next number in the buffer that must be send to the application
            lastMessageSendToApplication++;
            log.debug("Sending application message " + lastMessageSendToApplication + " to application");
            MercuryMessageContext mercuryMessageContext =
                    (MercuryMessageContext) this.messageBuffer.remove(new Long(lastMessageSendToApplication));
            //here we need to update the state per message since persistence storage
            // updation should happen atomically
            // update the state of this object
            int currentState = this.state;
            switch (state) {
                case STATE_000 : {
                    break;
                }
                case STATE_010 : {
                    if (this.messageBuffer.size() == 0) {
                        state = STATE_000;
                        if (this.persistanceDto != null) {
                            this.persistanceDto.setState(state);
                        }
                    }
                    break;
                }
                case STATE_011 : {
                    if (this.messageBuffer.size() == 0) {
                        state = STATE_COMPLETE;
                        if (this.persistanceDto != null) {
                            this.persistanceDto.setState(state);
                        }
                    }
                    break;
                }
                case STATE_111 : {
                    if (this.messageBuffer.size() == 0) {
                        state = STATE_COMPLETE;
                        if (this.persistanceDto != null) {
                            this.persistanceDto.setState(state);
                        }
                    }
                    break;
                }
                case STATE_110 : {
                    if (this.messageBuffer.size() == 0) {
                        state = STATE_COMPLETE;
                        if (this.persistanceDto != null) {
                            this.persistanceDto.setState(state);
                        }
                    }
                    break;
                }
                case STATE_COMPLETE : {
                    break;
                }
            }

            //set this message as send
            if (this.persistenceManager != null) {
                // update the rmd message
                try {
                    this.persistanceDto.setLastMessageToApplication(lastMessageSendToApplication);
                    mercuryMessageContext.getRmdPersistanceDto().setSend(true);
                    this.persistenceManager.update(
                            mercuryMessageContext.getRmdPersistanceDto(), this.persistanceDto);
                } catch (PersistenceException e) {
                    this.state = currentState;
                    this.persistanceDto.setState(state);
                    this.messageBuffer.put(new Long(lastMessageSendToApplication), mercuryMessageContext);
                    mercuryMessageContext.getRmdPersistanceDto().setSend(false);
                    lastMessageSendToApplication--;
                    log.error("Problem when updating the data base to send the message to application", e);
                    break;
                }
            }

            // AxisEngine.resumeReceive method invokes the last handler again
            // to avoid this problem we set the this message context as and mercury controll message.
            mercuryMessageContext.getMessageContext().setProperty(
                    MercuryConstants.PROCESS_RM_CONTROL_MESSAGE, Constants.VALUE_TRUE);
            // send the message to upper later.
            if (isAnonymous && !mercuryMessageContext.getMessageContext().isServerSide()) {
                // if this is an annonymous client side response. then we can not initiate
                // threads for this. the only thing we have to do is to notify the listning thread
                this.notifyAll();
            } else {
                // we can not start seperate threads here.
                // since we have to gurantee the inorder delivary
                try{
                   MessageContext applicationMsgContext = mercuryMessageContext.getMessageContext();
                   // dummy last messages should not send to the application.
                   if (!applicationMsgContext.getOptions().getAction().equals(
                           MercuryConstants.LAST_MESSAGE_ACTION)){
                       AxisEngine.resumeReceive(mercuryMessageContext.getMessageContext());
                   } else {
                       // mep can be inout only at the server side
                       // we send this to in only sequences as well but those will stop at the
                       // mercury out handler level.
                       String sessionID = mercuryMessageContext.getRmApplicationMessage().getSequence().getSequenceID();
                       MessageContext outMessageContext =
                               getNewMessageContextForLastMessage(applicationMsgContext, sessionID);
                       outMessageContext.setProperty(AddressingConstants.WS_ADDRESSING_VERSION,
                               applicationMsgContext.getProperty(AddressingConstants.WS_ADDRESSING_VERSION));
                       outMessageContext.getOptions().setAction(MercuryConstants.LAST_MESSAGE_ACTION);
                       outMessageContext.setProperty(MercuryClientConstants.INTERNAL_KEY,
                               applicationMsgContext.getProperty(MercuryConstants.SESSION_ID));
                       outMessageContext.setProperty(MercuryClientConstants.LAST_MESSAGE, Constants.VALUE_TRUE);


                       AxisOperation axisOperation = applicationMsgContext.getAxisService().getOperationByAction(
                               MercuryConstants.LAST_MESSAGE_ACTION);
                       outMessageContext.setAxisMessage(axisOperation.getMessage(WSDLConstants.MESSAGE_LABEL_IN_VALUE));

                       OperationContext operationContext =
                               applicationMsgContext.getServiceContext().createOperationContext(axisOperation);
                       axisOperation.registerOperationContext(outMessageContext, operationContext);
                       AxisEngine.send(outMessageContext);
                   }

                } catch (AxisFault e){
                   // throw it back with the new message context.
                    if (e.getFaultType() == Constants.APPLICATION_FAULT) {
                        MessageContext faultContext =
                                MessageContextBuilder.createFaultMessageContext(
                                        mercuryMessageContext.getMessageContext(), e);
                        // set the Axis Message as the out message if a fault message is not
                        // there
                        //TODO: set the fault message it is available.
                        AxisOperation axisOperation = mercuryMessageContext.getMessageContext().getAxisOperation();
                        if (axisOperation.getMessage(WSDLConstants.MESSAGE_LABEL_OUT_VALUE) != null){
                            faultContext.setAxisMessage(axisOperation.getMessage(WSDLConstants.MESSAGE_LABEL_OUT_VALUE));
                        } else {
                            faultContext.setAxisMessage(axisOperation.getMessage(WSDLConstants.MESSAGE_LABEL_IN_VALUE));
                        }

                        faultContext.setProperty("RECV_RESULTS",
                                mercuryMessageContext.getMessageContext().getProperty("RECV_RESULTS"));

                        faultContext.setProperty(Constants.APPLICATION_FAULT_STRING, Constants.VALUE_TRUE);
                        // set the servlet transport property
                        faultContext.setProperty(HTTPConstants.MC_HTTP_SERVLETRESPONSE,
                                mercuryMessageContext.getMessageContext().getProperty(
                                        HTTPConstants.MC_HTTP_SERVLETRESPONSE));
                        AxisFault axisFault = new AxisFault(e.getMessage(), faultContext);
                        axisFault.setDetail(e.getDetail());
                        axisFault.setFaultCode(e.getFaultCode());
                        axisFault.setFaultAction(e.getFaultAction());
                        axisFault.setFaultType(e.getFaultType());
                        throw axisFault;
                    } else {
                        //TODO: what to do with these exceptions??
                        log.error("Non application type fault occurs when sending the message to application ", e);
                    }
                } finally {
                    // duplex mode threads should wait until method invokations is over.
                    if (mercuryMessageContext.getMessageContext().isServerSide()) {
                        if (!isAnonymous || mercuryMessageContext.getMessageContext().getAxisOperation()
                                .getMessageExchangePattern().equals(WSDL2Constants.MEP_URI_IN_ONLY)) {
                            synchronized (mercuryMessageContext) {
                                mercuryMessageContext.notify();
                            }
                        }
                    }
                }
            }
        }
    }

    private MessageContext getNewMessageContextForLastMessage(
            MessageContext inMessageContext, String sessionID)
            throws AxisFault {
        MessageContext messageContext = new MessageContext();
        messageContext.setServiceContext(inMessageContext.getServiceContext());

        String soapNamespace = inMessageContext.getEnvelope().getNamespace().getNamespaceURI();
        SOAPFactory soapFactory = null;
        if (soapNamespace.equals(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI)){
            soapFactory = OMAbstractFactory.getSOAP12Factory();
        } else {
            soapFactory = OMAbstractFactory.getSOAP11Factory();
        }

        messageContext.getOptions().setSoapVersionURI(soapNamespace);
        messageContext.setEnvelope(soapFactory.getDefaultEnvelope());
        messageContext.setMessageID(UUIDGenerator.getUUID());

        inMessageContext.getOptions().setFrom(null);
        messageContext.setOptions(inMessageContext.getOptions());
        messageContext.setServerSide(inMessageContext.isServerSide());

        // set some security related properties
        // TODO: read these from the module xml file
        messageContext.setProperty("RECV_RESULTS", inMessageContext.getProperty("RECV_RESULTS"));

        messageContext.getOptions().setTo(this.acksTo);
        if (!this.isAnonymous) {
            messageContext.setTransportOut(inMessageContext.getTransportOut());
        } else {
            // i.e we have to send the message using the backchanel of the incomming message
            messageContext.setProperty(Constants.Configuration.MESSAGE_TYPE,
                              inMessageContext.getProperty(Constants.Configuration.MESSAGE_TYPE));
            messageContext.setTransportOut(inMessageContext.getTransportOut());
            messageContext.setProperty(MessageContext.TRANSPORT_OUT,
                    inMessageContext.getProperty(MessageContext.TRANSPORT_OUT));
            messageContext.setProperty(Constants.OUT_TRANSPORT_INFO,
                    inMessageContext.getProperty(Constants.OUT_TRANSPORT_INFO));
            // this is very important in writting the message using the same channel
            // once response input stream is written it updates this property
            messageContext.setProperty(RequestResponseTransport.TRANSPORT_CONTROL,
                    inMessageContext.getProperty(RequestResponseTransport.TRANSPORT_CONTROL));
            messageContext.setProperty(HTTPConstants.HTTP_HEADERS,
                    inMessageContext.getProperty(HTTPConstants.HTTP_HEADERS));
        }
        return messageContext;

    }

    public synchronized void terminateMessageReceived(RMDSequenceDto rmdSequenceDto) throws PersistenceException {
        // terminate mesage has received
        // update the state machine accordingly
        this.lastAccessTime = System.currentTimeMillis();
        int currentState = this.state;
        switch (state) {
            case STATE_000 : {
                state = STATE_COMPLETE;
                if (this.persistanceDto != null){
                    this.persistanceDto.setState(state);
                }
                break;
            }
            case STATE_010 : {
                state = STATE_011;
                if (this.persistanceDto != null) {
                    this.persistanceDto.setState(state);
                }
                break;
            }
            case STATE_011 : {
                // terminate has already received
                break;
            }
            case STATE_111 : {
                // terminate has already received
                break;
            }
            case STATE_110 : {
                state = STATE_111;
                if (this.persistanceDto != null) {
                    this.persistanceDto.setState(state);
                }
                break;
            }
            case STATE_COMPLETE : {
                break;
            }
        }

        if (this.persistenceManager != null){
            try {
                persistenceManager.update(this.persistanceDto,rmdSequenceDto);
            } catch (PersistenceException e) {
                this.state = currentState;
                this.persistanceDto.setState(currentState);
                log.error("Can not update the buffer sate", e);
                throw new PersistenceException("Can not update the buffer sate", e);
            }
        }
    }

    /**
     * saves both RMD Sequence and Invoker buffer objects at once.
     * @param rmdSequenceDto
     * @throws PersistenceException
     */
    public void save(RMDSequenceDto rmdSequenceDto,
                     Axis2InfoDto axis2InfoDto)
            throws PersistenceException {
        this.persistanceDto = new InvokerBufferDto();
        this.persistanceDto.setState(this.state);
        this.persistanceDto.setLastMessage(this.lastMessageNumber);
        this.persistanceDto.setLastMessageToApplication(this.lastMessageSendToApplication);
        this.persistenceManager.save(this.persistanceDto,rmdSequenceDto, axis2InfoDto);

    }

    public void loadInvokerBufferDetails(MessageContext messageContext) throws PersistenceException, AxisFault {
        // load the BufferReceivedMessageNumbers
        List bufferReceivedNumbers =
                this.persistenceManager.getBufferReceivedNumbersWithInvokerBufferID(this.persistanceDto.getId());
        BufferReceivedNumberDto bufferReceivedNumberDto = null;
        for (Iterator iter = bufferReceivedNumbers.iterator();iter.hasNext();){
            bufferReceivedNumberDto = (BufferReceivedNumberDto) iter.next();
            this.receivedMessageNumbers.add(new Long(bufferReceivedNumberDto.getNumber()));
        }

        // load the messages
        List bufferMessages = persistenceManager.getRMDMessagesWithInvokerBufferID(this.persistanceDto.getId());
        RMDMessageDto rmdMessageDto = null;
        for (Iterator iter = bufferMessages.iterator();iter.hasNext();){
            rmdMessageDto = (RMDMessageDto) iter.next();

            // creating a new message context
            MessageContext newMessageContext = new MessageContext();

            newMessageContext.setCurrentHandlerIndex(messageContext.getCurrentHandlerIndex());
            newMessageContext.setCurrentPhaseIndex(messageContext.getCurrentPhaseIndex());
            newMessageContext.setExecutionChain(messageContext.getExecutionChain());

            newMessageContext.setServiceContext(messageContext.getServiceContext());
            newMessageContext.setMessageID(rmdMessageDto.getMessageID());
            newMessageContext.setTransportIn(messageContext.getTransportIn());
            newMessageContext.setTransportOut(messageContext.getTransportOut());

            newMessageContext.setTo(messageContext.getTo());
            newMessageContext.setReplyTo(messageContext.getReplyTo());

            newMessageContext.setOptions(new Options());
            newMessageContext.getOptions().setTo(messageContext.getTo());
            newMessageContext.getOptions().setUseSeparateListener(messageContext.getOptions().isUseSeparateListener());
            newMessageContext.getOptions().setAction(rmdMessageDto.getAction());
            newMessageContext.setServerSide(messageContext.isServerSide());


            AxisOperation inOutOperation = messageContext.getAxisOperation();
            OperationContext operationContext =
                    messageContext.getServiceContext().createOperationContext(inOutOperation);
            inOutOperation.registerOperationContext(newMessageContext, operationContext);
            newMessageContext.setAxisMessage(inOutOperation.getMessage(WSDLConstants.MESSAGE_LABEL_IN_VALUE));

            RMApplicationMessage rmApplicationMessage =
                    new RMApplicationMessage(getSoapEnvelope(rmdMessageDto.getSoapEnvelope()));
            Sequence sequence = new Sequence();
            rmApplicationMessage.setSequence(sequence);
            sequence.setMessageNumber(rmdMessageDto.getMessageNumber());
            rmApplicationMessage.setSequenceHeaderAdded(true);
            try {
                newMessageContext.setEnvelope(rmApplicationMessage.toSOAPEnvelope());
            } catch (RMMessageBuildingException e) {
                log.error("Can not build the soap envelope", e);
                throw new PersistenceException("Can not build the soap envelope", e);
            }

            MercuryMessageContext mercuryMessageContext =
                    new MercuryMessageContext(rmApplicationMessage, newMessageContext);
            mercuryMessageContext.setRmdPersistanceDto(rmdMessageDto);
            this.messageBuffer.put(new Long(rmdMessageDto.getMessageNumber()), mercuryMessageContext);
        }

    }

    /**
     * this method is used when starting the sequences directly from the persistance storage.
     *
     * @throws PersistenceException
     * @throws AxisFault
     */

    public void loadInvokerBufferDetails(Axis2InfoDto axis2InfoDto,
                                         ConfigurationContext configurationContext,
                                         ServiceContext serviceContext) throws PersistenceException, AxisFault {
        // load the BufferReceivedMessageNumbers
        List bufferReceivedNumbers =
                this.persistenceManager.getBufferReceivedNumbersWithInvokerBufferID(this.persistanceDto.getId());
        BufferReceivedNumberDto bufferReceivedNumberDto = null;
        for (Iterator iter = bufferReceivedNumbers.iterator(); iter.hasNext();) {
            bufferReceivedNumberDto = (BufferReceivedNumberDto) iter.next();
            this.receivedMessageNumbers.add(new Long(bufferReceivedNumberDto.getNumber()));
        }

        AxisService axisServce
                = configurationContext.getAxisConfiguration().getService(axis2InfoDto.getServiceName());

        // load the messages
        List bufferMessages = persistenceManager.getRMDMessagesWithInvokerBufferID(this.persistanceDto.getId());
        RMDMessageDto rmdMessageDto = null;
        for (Iterator iter = bufferMessages.iterator(); iter.hasNext();) {
            rmdMessageDto = (RMDMessageDto) iter.next();

            // creating a new message context
            MessageContext newMessageContext = new MessageContext();
            AxisOperation axisOperation = axisServce.getOperation(rmdMessageDto.getOperationName());

            newMessageContext.setCurrentHandlerIndex(axis2InfoDto.getCurrentHanlderIndex());
            newMessageContext.setCurrentPhaseIndex(axis2InfoDto.getCurrentPhaseIndex());
            newMessageContext.setExecutionChain(axisOperation.getRemainingPhasesInFlow());

            newMessageContext.setServiceContext(serviceContext);
            newMessageContext.setMessageID(rmdMessageDto.getMessageID());


            newMessageContext.setTo(new EndpointReference(rmdMessageDto.getTo()));
            newMessageContext.setReplyTo(new EndpointReference(rmdMessageDto.getReplyTo()));


            newMessageContext.setOptions(new Options());
            newMessageContext.getOptions().setTo(new EndpointReference(rmdMessageDto.getTo()));
            newMessageContext.getOptions().setAction(rmdMessageDto.getAction());
            newMessageContext.setServerSide(axis2InfoDto.isServerSide());



            OperationContext operationContext =
                    serviceContext.createOperationContext(axisOperation);
            axisOperation.registerOperationContext(newMessageContext, operationContext);
            newMessageContext.setAxisMessage(axisOperation.getMessage(WSDLConstants.MESSAGE_LABEL_IN_VALUE));

            RMApplicationMessage rmApplicationMessage =
                    new RMApplicationMessage(getSoapEnvelope(rmdMessageDto.getSoapEnvelope()));
            Sequence sequence = new Sequence();
            rmApplicationMessage.setSequence(sequence);
            sequence.setMessageNumber(rmdMessageDto.getMessageNumber());
            rmApplicationMessage.setSequenceHeaderAdded(true);
            try {
                newMessageContext.setEnvelope(rmApplicationMessage.toSOAPEnvelope());
            } catch (RMMessageBuildingException e) {
                log.error("Can not build the soap envelope", e);
                throw new PersistenceException("Can not build the soap envelope", e);
            }

            MercuryMessageContext mercuryMessageContext =
                    new MercuryMessageContext(rmApplicationMessage, newMessageContext);
            mercuryMessageContext.setRmdPersistanceDto(rmdMessageDto);
            this.messageBuffer.put(new Long(rmdMessageDto.getMessageNumber()), mercuryMessageContext);
        }

    }

    private SOAPEnvelope getSoapEnvelope(String soapEnvelpe)
            throws PersistenceException {
        try {
            XMLStreamReader xmlReader =
                    StAXUtils.createXMLStreamReader(new ByteArrayInputStream(soapEnvelpe.getBytes()));
            StAXBuilder builder = new StAXSOAPModelBuilder(xmlReader);
            SOAPEnvelope soapEnvelope = (SOAPEnvelope) builder.getDocumentElement();
            soapEnvelope.build();
            String soapNamespace = soapEnvelope.getNamespace().getNamespaceURI();
            if (soapEnvelope.getHeader() == null){
                SOAPFactory soapFactory = null;
                if (soapNamespace.equals(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI)) {
                    soapFactory = OMAbstractFactory.getSOAP12Factory();
                } else {
                    soapFactory = OMAbstractFactory.getSOAP11Factory();
                }
                soapFactory.createSOAPHeader(soapEnvelope);
            }
            return soapEnvelope;
        } catch (XMLStreamException e) {
            log.error("Problem with the stored message", e);
            throw new PersistenceException("Problem with the stored message", e);
        }
    }

    /**
     * this method is used to wait the client side thread in annonymous invocations
     * until invoker message passes it.
     */

    public synchronized void waitUntilMessageInvoke(long messageNumber) {
        // this is an annonymous client response. this thread should have started
        // at the axis2 kernal level. but we can not let this thread proceed
        // all the sequence messages comes. here we wait this thread in
        // Mercury message context. at the invoke buffer level this thread will
        // notify when the time comes.
        // here we have to wait only if the invoker buffer has not
        // decided to release this message

        if (messageNumber > lastMessageSendToApplication) {
            try {
                // call continueWork to wake up InvokerWorker
                // this is an performance improvement to start the
                // invoker worker immediately
                // otherwise it would take a SLEEP time to wake up the thread

                // this thread has already has acquired the lock for Invoker buffer.
                // So wake up actually starts work after wait calls in next line.
                continueWork();

                this.wait();
                waitUntilMessageInvoke(messageNumber);
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * this method is called in an unexpected terminateion
     */
    public synchronized void terminate(){
        this.state = STATE_COMPLETE;
    }

    public void continueWork() {
        log.debug("continueWork");
        if (invokerWorker != null) {
            invokerWorker.wakeUp();
        } else {
            // not an error
            log.debug("NO Invoker Worker ??? ");
        }
    }


    public synchronized int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Map getMessageBuffer() {
        return messageBuffer;
    }

    public void setMessageBuffer(Map messageBuffer) {
        this.messageBuffer = messageBuffer;
    }

    public synchronized long getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public PersistenceManager getPersistanceManager() {
        return persistenceManager;
    }

    public void setPersistanceManager(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }

    public long getLastMessageNumber() {
        return lastMessageNumber;
    }

    public void setLastMessageNumber(long lastMessageNumber) {
        this.lastMessageNumber = lastMessageNumber;
    }

    public synchronized long getLastMessageSendToApplication() {
        return lastMessageSendToApplication;
    }

    public InvokerBufferDto getPersistanceDto() {
        return persistanceDto;
    }

    public void setPersistanceDto(InvokerBufferDto persistanceDto) {
        this.persistanceDto = persistanceDto;
    }

    public void setLastMessageSendToApplication(long lastMessageSendToApplication) {
        this.lastMessageSendToApplication = lastMessageSendToApplication;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }

    public EndpointReference getAcksTo() {
        return acksTo;
    }

    public void setAcksTo(EndpointReference acksTo) {
        this.acksTo = acksTo;
    }

    public void setInvokerWorker(InvokerWorker w) {
        this.invokerWorker = w;
    }

    public long getTimeoutTime() {
        return timeoutTime;
    }

    public void setTimeoutTime(long timeoutTime) {
        this.timeoutTime = timeoutTime;
    }

}
