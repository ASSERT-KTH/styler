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
package org.wso2.mercury.persistence.hibernate;

import org.wso2.mercury.persistence.PersistenceManager;
import org.wso2.mercury.persistence.exception.PersistenceException;
import org.wso2.mercury.persistence.dto.*;
import org.wso2.mercury.handlers.MercuryOutHandler;
import org.wso2.mercury.state.Axis2Info;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.cfg.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.description.Parameter;

import java.util.List;
import java.util.Set;
import java.util.Iterator;

/**
 * Hibernate implementation of the persistence manager
 */

public class HibernatePersistenceManager implements PersistenceManager {

    public static final String HIBERNATE_CONNECTION_DRIVER_CLASS = "hibernate.connection.driver_class";
    public static final String HIBERNATE_CONNECTION_URL = "hibernate.connection.url";
    public static final String HIBERNATE_CONNECTION_USERNAME = "hibernate.connection.username";
    public static final String HIBERNATE_CONNECTION_PASSWORD = "hibernate.connection.password";
    public static final String HIBERNATE_CONNECTION_POOL_SIZE = "hibernate.connection.pool_size";
    public static final String HIBERNATE_CURRENT_SESSION_CONTEXT_CLASS = "hibernate.current_session_context_class";
    public static final String HIBERNATE_DIALECT = "hibernate.dialect";

    private static Log log = LogFactory.getLog(HibernatePersistenceManager.class);

    // session factory used to handle persistence
    private SessionFactory sessionFactory;

    public HibernatePersistenceManager(Configuration configuration) {
        this.sessionFactory = configuration.buildSessionFactory();
    }

    public HibernatePersistenceManager(AxisConfiguration axisConfiguration) {

        Configuration configuration = new Configuration();
        setProperty(configuration, HIBERNATE_CONNECTION_DRIVER_CLASS, axisConfiguration);
        setProperty(configuration, HIBERNATE_CONNECTION_URL, axisConfiguration);
        setProperty(configuration, HIBERNATE_CONNECTION_USERNAME, axisConfiguration);
        setProperty(configuration, HIBERNATE_CONNECTION_PASSWORD, axisConfiguration);
        setProperty(configuration, HIBERNATE_CONNECTION_POOL_SIZE, axisConfiguration);
        setProperty(configuration, HIBERNATE_CURRENT_SESSION_CONTEXT_CLASS, axisConfiguration);
        setProperty(configuration, HIBERNATE_DIALECT, axisConfiguration);
//        configuration.setProperty("hibernate.show_sql","true");
//        configuration.setProperty("hibernate.hbm2ddl.auto","create");
        configuration.addResource("org/wso2/mercury/persistence/hibernate/rm.hbm.xml");

        this.sessionFactory = configuration.buildSessionFactory();
    }

    private void setProperty(Configuration configuration,
                             String parameterName,
                             AxisConfiguration axisConfiguration){
          Parameter parameter = axisConfiguration.getParameter(parameterName);
          if (parameter != null){
              configuration.setProperty(parameterName, (String) parameter.getValue());
          } else {
              log.warn("Hibernate parameter " + parameterName + " has not been set");
          }
    }

    public void save(InternalKeyDto internalKeyDto) throws PersistenceException {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            session.save(internalKeyDto);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            log.error("Can not save the internal key object with key ==> " +
                    internalKeyDto.getKey() + " toAddress " + internalKeyDto.getToAddress(), e);
            throw new PersistenceException("Can not save the internal key object with key ==> " +
                    internalKeyDto.getKey() + " toAddress " + internalKeyDto.getToAddress(), e);
        }

    }

    public List getInternalKey(String key, String toAddress) throws PersistenceException {

        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            List keys = session.createQuery("from InternalKeyDto where key='" +
                    key + "' and toAddress='" + toAddress + "'").list();
            session.getTransaction().commit();
            return keys;

        } catch (HibernateException e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            log.error("Can not load the internal key objects with key ==> " +
                    key + " toAddress " + toAddress, e);
            throw new PersistenceException("Can not load the internal key objects with key ==> " +
                    key + " toAddress " + toAddress, e);
        }
    }

    public void save(RMSSequenceDto rmsSequenceDto, Axis2InfoDto axis2InfoDto) throws PersistenceException {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            session.save(axis2InfoDto);
            // save enaged modules
            for (EngagedModuleDto engagedModuleDto: axis2InfoDto.getEngagedModules()){
                engagedModuleDto.setAxis2InfoID(axis2InfoDto.getId());
                session.save(engagedModuleDto);
            }

            // save the properties
            for (PropertyDto propertyDto : axis2InfoDto.getProperties()){
                propertyDto.setAxis2InfoID(axis2InfoDto.getId());
                session.save(propertyDto);
            }

            rmsSequenceDto.setAxis2InfoID(axis2InfoDto.getId());
            session.save(rmsSequenceDto);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            log.error("Can not save the RMS Sequence object with state ==> " +
                    rmsSequenceDto.getState() + " toAddress " + rmsSequenceDto.getEndPointAddress(), e);
            throw new PersistenceException("Can not save the RMS Sequence object with state ==> " +
                    rmsSequenceDto.getState() + " toAddress " + rmsSequenceDto.getEndPointAddress(), e);
        }
    }

    public void update(RMSSequenceDto rmsSequenceDto) throws PersistenceException {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            session.update(rmsSequenceDto);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            log.error("Can not update the RMS Sequence object with state ==> " +
                    rmsSequenceDto.getState() + " toAddress " + rmsSequenceDto.getEndPointAddress(), e);
            throw new PersistenceException("Can not update the RMS Sequence object with state ==> " +
                    rmsSequenceDto.getState() + " toAddress " + rmsSequenceDto.getEndPointAddress(), e);
        }
    }

    public List getRMSSquenceWithInternalKey(long internalKeyID) throws PersistenceException {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            List keys = session.createQuery("from RMSSequenceDto where internalKeyID=" +
                    internalKeyID).list();
            session.getTransaction().commit();
            return keys;

        } catch (HibernateException e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            log.error("Can not load the RMSSeqence objects with internal key ==> " +
                    internalKeyID, e);
            throw new PersistenceException("Can not load the RMSSeqence objects with internal key ==> " +
                    internalKeyID, e);
        }
    }

    public RMSSequenceDto getRMSSquenceWithID(long id) throws PersistenceException {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            RMSSequenceDto rmsSequenceDto =
                    (RMSSequenceDto) session.load(RMSSequenceDto.class, id,LockMode.READ);
            session.getTransaction().commit();
            return rmsSequenceDto;

        } catch (HibernateException e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            log.error("Can not load the RMSSeqence objects with id ==> " + id, e);
            throw new PersistenceException("Can not load the RMSSeqence objects with id ==> " +
                    id, e);
        }
    }

    public void save(RMSMessageDto rmsMessageDto,
                     RMSSequenceDto rmsSequenceDto) throws PersistenceException {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            session.save(rmsMessageDto);
            session.update(rmsSequenceDto);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            log.error("Can not save the RMSMessage object with message number ==> " +
                    rmsMessageDto.getMessageNumber(), e);
            throw new PersistenceException("Can not save the RMSMessage object with message number ==> " +
                    rmsMessageDto.getMessageNumber(), e);
        }
    }

    public void updateMessagesAsSend(Set acknowledgedMessageDtos, RMSSequenceDto rmsSequenceDto)
            throws PersistenceException {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            for (Iterator iter = acknowledgedMessageDtos.iterator();iter.hasNext();){
                session.update(iter.next());
            }
            session.update(rmsSequenceDto);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            log.error("Can not update the RMSMessageDtos ", e);
            throw new PersistenceException("Can not update the RMSMessageDtos ", e);
        }
    }

    public RMSMessageDto getRMSMessageWithSequenceID(String sequenceID) throws PersistenceException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    //TODO: load only the not send messages
    public List getRMSMessagesWithRMSSequenceID(long rmsSequenceID) throws PersistenceException {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            List keys = session.createQuery("from RMSMessageDto where rmsSequenceID=" + rmsSequenceID).list();
            session.getTransaction().commit();
            return keys;

        } catch (HibernateException e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            log.error("Can not load the RMSMessage objects with rms Sequces key ==> " +
                    rmsSequenceID, e);
            throw new PersistenceException("Can not load the RMSMessage objects with rms Sequces key ==> " +
                    rmsSequenceID, e);
        }
    }

    public void update(RMSMessageDto rmsMessageDto) throws PersistenceException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void save(AcknowledgmentDto acknowledgmentsDto) throws PersistenceException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void save(RMDSequenceDto rmdSequenceDto) throws PersistenceException {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            session.save(rmdSequenceDto);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            log.error("Can not save the RMD Sequence object with state ==> " +
                    rmdSequenceDto.getState() + " Ackto address " + rmdSequenceDto.getAcksTo(), e);
            throw new PersistenceException("Can not save the RMD Sequence object with state ==> " +
                    rmdSequenceDto.getState() + " Ackto address " + rmdSequenceDto.getAcksTo(), e);
        }
    }

    public void save(InvokerBufferDto invokerBufferDto,
                     RMDSequenceDto rmdSequenceDto,
                     Axis2InfoDto axis2InfoDto) throws PersistenceException {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            session.save(axis2InfoDto);
            // save the properties
//            for (PropertyDto propertyDto : axis2InfoDto.getProperties()) {
//                propertyDto.setAxis2InfoID(axis2InfoDto.getId());
//                session.save(propertyDto);
//            }

            rmdSequenceDto.setAxis2InfoID(axis2InfoDto.getId());
            session.save(rmdSequenceDto);
            invokerBufferDto.setRmdSequenceID(rmdSequenceDto.getId());
            session.save(invokerBufferDto);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            log.error("Can not save the Invoker Buffer object with state ==> " +
                    invokerBufferDto.getState(), e);
            throw new PersistenceException("Can not save the Invoker Buffer object with state ==> " +
                    invokerBufferDto.getState(), e);
        }
    }

    public void save(RMDMessageDto rmdMessageDto) throws PersistenceException {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            session.save(rmdMessageDto);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            log.error("Can not save the RMD Message object with message number ==> " +
                    rmdMessageDto.getMessageNumber(), e);
            throw new PersistenceException("Can not save the RMD Message object with message number ==> " +
                    rmdMessageDto.getMessageNumber(), e);
        }
    }

    public void save(SequenceReceivedNumberDto sequenceReceivedNumberDto) throws PersistenceException {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            session.save(sequenceReceivedNumberDto);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            log.error("Can not save the sequence received number object with message number ==> " +
                    sequenceReceivedNumberDto.getNumber(), e);
            throw new PersistenceException("Can not save the sequence received number object with message number ==> " +
                    sequenceReceivedNumberDto.getNumber(), e);
        }
    }

    public void save(BufferReceivedNumberDto bufferReceivedNumberDto) throws PersistenceException {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            session.save(bufferReceivedNumberDto);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            log.error("Can not save the buffer received number object with message number ==> " +
                    bufferReceivedNumberDto.getNumber(), e);
            throw new PersistenceException("Can not save the buffer received number object with message number ==> " +
                    bufferReceivedNumberDto.getNumber(), e);
        }
    }

    public void updateMessageDetails(RMDSequenceDto rmdSequenceDto,
                                     InvokerBufferDto invokerBufferDto,
                                     SequenceReceivedNumberDto sequenceReceivedNumberDto,
                                     BufferReceivedNumberDto bufferReceivedNumberDto,
                                     RMDMessageDto rmdMessageDto) throws PersistenceException {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            session.update(rmdSequenceDto);
            session.update(invokerBufferDto);
            session.save(sequenceReceivedNumberDto);
            session.save(bufferReceivedNumberDto);
            session.save(rmdMessageDto);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            log.error("Can not add the new message ==> " +
                    bufferReceivedNumberDto.getNumber(), e);
            throw new PersistenceException("Can not add the new message ==> " +
                    bufferReceivedNumberDto.getNumber(), e);
        }
    }

    public void update(InvokerBufferDto invokerBufferDto,
                       RMDSequenceDto rmdSequenceDto) throws PersistenceException {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            session.update(rmdSequenceDto);
            session.update(invokerBufferDto);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            log.error("Can not save the Invoker Buffer object with state ==> " +
                    invokerBufferDto.getState(), e);
            throw new PersistenceException("Can not save the Invoker Buffer object with state ==> " +
                    invokerBufferDto.getState(), e);
        }
    }

     public void update(RMDMessageDto rmdMessageDto,
                       InvokerBufferDto invokerBufferDto) throws PersistenceException {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            session.update(rmdMessageDto);
            session.update(invokerBufferDto);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            log.error("Can not save the RMD Message object with message number ==> " +
                    rmdMessageDto.getMessageNumber(), e);
            throw new PersistenceException("Can not save the RMD Message object with message number ==> " +
                    rmdMessageDto.getMessageNumber(), e);
        }
    }

    public RMDSequenceDto getRMDSequeceWithSequenceID(String sequenceID) throws PersistenceException {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            List keys = session.createQuery("from RMDSequenceDto where sequenceID='" + sequenceID + "'").list();
            session.getTransaction().commit();
            if (keys.size() != 1) {
                throw new PersistenceException("Either there are more than one RMD sequence for sequenceID " +
                        " ==> " + sequenceID + " or no sequences");
            } else {
                return (RMDSequenceDto) keys.get(0);
            }

        } catch (HibernateException e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            log.error("Can not load the RMDSequenceDto objects with rmd Sequces key ==> " +
                    sequenceID, e);
            throw new PersistenceException("Can not load the RMDSequenceDto objects with rmd Sequces key ==> " +
                    sequenceID, e);
        }
    }

    public List getSequenceReceivedNumbersWithRMDSequenceID(long rmdSequenceID) throws PersistenceException {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            List numbers = session.createQuery("from SequenceReceivedNumberDto where rmdSequenceID=" + rmdSequenceID).list();
            session.getTransaction().commit();
            return numbers;
        } catch (HibernateException e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            log.error("Can not load the SequenceReceivedNumberDto " +
                    "objects with rmd sequence id ==> " + rmdSequenceID, e);
            throw new PersistenceException("Can not load the SequenceReceivedNumberDto " +
                    "objects with rmd sequence id ==> " + rmdSequenceID, e);
        }
    }

    public InvokerBufferDto getInvokerBufferWithRMDSequenceID(long rmdSequenceID) throws PersistenceException {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            List invokerBufferObjects = session.createQuery("from InvokerBufferDto where rmdSequenceID="
                    + rmdSequenceID).list();
            session.getTransaction().commit();
            if (invokerBufferObjects.size() != 1) {
                throw new PersistenceException("Either there are more than one Invoker Buffer for rmdSequenceID " +
                        " ==> " + rmdSequenceID + " or no sequences");
            } else {
                return (InvokerBufferDto) invokerBufferObjects.get(0);
            }

        } catch (HibernateException e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            log.error("Can not load the InvokerBufferDto objects with rms Sequces key id==> " +
                    rmdSequenceID, e);
            throw new PersistenceException("Can not load the InvokerBufferDto objects with rms Sequces key id==> " +
                    rmdSequenceID, e);
        }
    }

    public List getBufferReceivedNumbersWithInvokerBufferID(long invokerBufferID) throws PersistenceException {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            List numbers = session.createQuery("from BufferReceivedNumberDto where internalBufferID="
                    + invokerBufferID).list();
            session.getTransaction().commit();
            return numbers;
        } catch (HibernateException e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            log.error("Can not load the BufferReceivedNumberDto " +
                    "objects with invoker buffer id ==> " + invokerBufferID, e);
            throw new PersistenceException("Can not load the BufferReceivedNumberDto " +
                    "objects with invoker buffer id ==> " + invokerBufferID, e);
        }
    }

    public List getRMDMessagesWithInvokerBufferID(long invokerBufferID) throws PersistenceException {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            List numbers = session.createQuery("from RMDMessageDto where internalBufferID="
                    + invokerBufferID + "and send=false").list();
            session.getTransaction().commit();
            return numbers;
        } catch (HibernateException e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            log.error("Can not load the RMDMessageDto " +
                    "objects with invoker buffer id ==> " + invokerBufferID, e);
            throw new PersistenceException("Can not load the RMDMessageDto " +
                    "objects with invoker buffer id ==> " + invokerBufferID, e);
        }
    }

    public List getNonTerminatedRMDSequences() throws PersistenceException {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            List rmdSequences = session.createQuery("from RMDSequenceDto where state < 4").list();
            session.getTransaction().commit();
            return rmdSequences;
        } catch (HibernateException e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            log.error("from RMDSequenceDto where state < 4", e);
            throw new PersistenceException("from RMDSequenceDto where state < 4", e);
        }
    }

    public List<RMSSequenceDto> getNonTerminatedRMSSequences() throws PersistenceException {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            List<RMSSequenceDto> rmsSequences = session.createQuery("from RMSSequenceDto where state < 7").list();
            session.getTransaction().commit();
            return rmsSequences;
        } catch (HibernateException e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            log.error("from RMSSequenceDto where state < 7", e);
            throw new PersistenceException("from RMSSequenceDto where state < 7", e);
        }
    }

    public Axis2InfoDto getAxis2InfoID(long id) throws PersistenceException {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            Axis2InfoDto axis2InfoDto =
                    (Axis2InfoDto) session.load(Axis2InfoDto.class, id, LockMode.READ);
            session.getTransaction().commit();
            return axis2InfoDto;

        } catch (HibernateException e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            log.error("Can not load the axis2InfoDto objects with id ==> " + id, e);
            throw new PersistenceException("Can not load the axis2InfoDto objects with id ==> " +
                    id, e);
        }
    }

    public List<PropertyDto> getProperties(long axis2InfoID) throws PersistenceException {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            List<PropertyDto> keys = session.createQuery("from PropertyDto where axis2InfoID=" +
                    axis2InfoID ).list();
            session.getTransaction().commit();
            return keys;

        } catch (HibernateException e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            log.error("Can not load the Property objects with axi2 Info ID ==> " +
                    axis2InfoID, e);
            throw new PersistenceException("Can not load the Property objects with axis2 Info ID ==> " +
                    axis2InfoID, e);
        }
    }

    public List<EngagedModuleDto> getEngagedModules(long axis2InfoID) throws PersistenceException {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            List<EngagedModuleDto> keys = session.createQuery("from EngagedModuleDto where axis2InfoID=" +
                    axis2InfoID).list();
            session.getTransaction().commit();
            return keys;

        } catch (HibernateException e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            log.error("Can not load the Engage Module objects with axi2 Info ID ==> " +
                    axis2InfoID, e);
            throw new PersistenceException("Can not load the Engage Module objects with axis2 Info ID ==> " +
                    axis2InfoID, e);
        }
    }

    public InternalKeyDto getInternalKeyWithID(long id) throws PersistenceException {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            InternalKeyDto internalKeyDto =
                    (InternalKeyDto) session.load(InternalKeyDto.class, id, LockMode.READ);
            session.getTransaction().commit();
            return internalKeyDto;

        } catch (HibernateException e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            log.error("Can not load the InternalKey object with internal key id ==> " +
                    id, e);
            throw new PersistenceException("Can not load the Internalkey object with internal key id==> " +
                    id, e);
        }
    }

}
