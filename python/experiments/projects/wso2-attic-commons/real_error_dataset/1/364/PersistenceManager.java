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
package org.wso2.mercury.persistence;

import org.wso2.mercury.persistence.dto.*;
import org.wso2.mercury.persistence.exception.PersistenceException;

import java.util.List;
import java.util.Set;

/**
 * this is the persistence manager that any persistence
 * storage has to implement
 */

public interface PersistenceManager {

    /**
     * adds a new internal key to persistence storage
     * underline persistence storage should set the id
     * @param internalKeyDto
     */
    public void save(InternalKeyDto internalKeyDto) throws PersistenceException;

    /**
     * retrive the internal key object from the underline persintance storage
     * @param key
     * @param toAddress
     * @return internal key object
     */
    public List getInternalKey(String key,String toAddress) throws PersistenceException;

    /**
     * adds a new RMSSequenceDto to the persistence storage.
     * @param rmsSequenceDto
     * @throws PersistenceException
     */
    public void save(RMSSequenceDto rmsSequenceDto, Axis2InfoDto axis2InfoDto) throws PersistenceException;

    public void update(RMSSequenceDto rmsSequenceDto) throws PersistenceException;

    public List getRMSSquenceWithInternalKey(long internalKeyID) throws PersistenceException;

    public RMSSequenceDto getRMSSquenceWithID(long id) throws PersistenceException;

    public void save(RMSMessageDto rmsMessageDto, RMSSequenceDto rmsSequenceDto) throws PersistenceException;

    public RMSMessageDto getRMSMessageWithSequenceID(String sequenceID) throws PersistenceException;

    public List getRMSMessagesWithRMSSequenceID(long rmsSequenceID) throws PersistenceException;

    public void update(RMSMessageDto rmsMessageDto) throws PersistenceException;

    public void save(AcknowledgmentDto acknowledgmentsDto) throws PersistenceException;

    public void updateMessagesAsSend(Set acknowledgedMessageDtos, RMSSequenceDto rmsSequenceDto) throws PersistenceException;

    public void save(RMDSequenceDto rmdRmdSequenceDto) throws PersistenceException;

    public void save(InvokerBufferDto invokerBufferDto,
                     RMDSequenceDto rmsRmdSequenceDto,
                     Axis2InfoDto axis2InfoDto) throws PersistenceException;

    public void save(RMDMessageDto rmdMessageDto) throws PersistenceException;

    public void save(SequenceReceivedNumberDto sequenceReceivedNumberDto) throws PersistenceException;

    public void save(BufferReceivedNumberDto bufferReceivedNumberDto) throws PersistenceException;

    public void updateMessageDetails(RMDSequenceDto rmdSequenceDto,
                                     InvokerBufferDto invokerBufferDto,
                                     SequenceReceivedNumberDto sequenceReceivedNumberDto,
                                     BufferReceivedNumberDto bufferReceivedNumberDto,
                                     RMDMessageDto rmdMessageDto) throws PersistenceException;

    public void update(InvokerBufferDto invokerBufferDto,
                       RMDSequenceDto rmsRmdSequenceDto) throws PersistenceException;

    public void update(RMDMessageDto rmdMessageDto,
                       InvokerBufferDto invokerBufferDto) throws PersistenceException;

    public RMDSequenceDto getRMDSequeceWithSequenceID(String sequenceID) throws PersistenceException;

    public List getSequenceReceivedNumbersWithRMDSequenceID(long rmdSequenceID) throws PersistenceException;

    public InvokerBufferDto getInvokerBufferWithRMDSequenceID(long rmdSequenceID) throws PersistenceException;

    public List getBufferReceivedNumbersWithInvokerBufferID(long invokerBufferID) throws PersistenceException;

    public List getRMDMessagesWithInvokerBufferID(long invokerBufferID) throws PersistenceException;

    public List getNonTerminatedRMDSequences() throws PersistenceException;

    public List<RMSSequenceDto> getNonTerminatedRMSSequences() throws PersistenceException;

    public List<EngagedModuleDto> getEngagedModules(long axis2InfoID) throws PersistenceException;

    public Axis2InfoDto getAxis2InfoID(long id) throws PersistenceException;

    public List<PropertyDto> getProperties(long axis2InfoID) throws PersistenceException;

    public InternalKeyDto getInternalKeyWithID(long id) throws PersistenceException;
}
