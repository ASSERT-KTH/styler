package org.apache.ode.bpel.engine;

import javax.xml.namespace.QName;

import org.apache.ode.bpel.dao.MessageDAO;
import org.apache.ode.bpel.dao.MessageExchangeDAO;
import org.apache.ode.bpel.iapi.MessageExchange.AckType;
import org.apache.ode.bpel.iapi.MessageExchange.FailureType;
import org.apache.ode.bpel.iapi.MessageExchange.Status;
import org.w3c.dom.Element;

/**
 * Some handy utilities methods for dealing with MEX daos.
 *  
 * @author Maciej Szefler <mszefler at gmail dot com>
 *
 */
class MexDaoUtil {

    static void setFailed(MessageExchangeDAO mex, FailureType ftype, String explanation) {
        mex.setStatus(Status.ACK);
        mex.setAckType(AckType.FAILURE);
        mex.setFailureType(ftype);
        mex.setFaultExplanation(explanation);
    }

    static void setFaulted(MessageExchangeDAO mex, QName faultType, Element faultmsg) {
        mex.setStatus(Status.ACK);
        mex.setAckType(AckType.FAULT);
        mex.setFailureType(null);
        mex.setFault(faultType);
        MessageDAO flt = mex.getConnection().createMessage(faultType);
        flt.setData(faultmsg);
        mex.setResponse(flt);
    }

    static void setResponse(MessageExchangeDAO mex, Element response) {
        mex.setStatus(Status.ACK);
        mex.setAckType(AckType.RESPONSE);
        mex.setFailureType(null);
        mex.setFault(null);
        MessageDAO resp = mex.getConnection().createMessage(null);
        resp.setData(response);
        mex.setResponse(resp);
    }

    static void copyMyRoleMexDAOToPartnerRoleMexDAOInP2PInvoke(MessageExchangeDAO myRole, MessageExchangeDAO partnerRole){
        partnerRole.setStatus(myRole.getStatus());
        partnerRole.setAckType(myRole.getAckType());
        partnerRole.setFailureType(myRole.getFailureType());
        partnerRole.setFault(myRole.getFault());
        partnerRole.setFaultExplanation(myRole.getFaultExplanation());
        partnerRole.setResponse(myRole.getResponse());
        for(String propName: myRole.getPropertyNames()){
            partnerRole.setProperty(propName, myRole.getProperty(propName));
        }
        partnerRole.setPartnersKey(myRole.getPartnersKey());
    }
}
