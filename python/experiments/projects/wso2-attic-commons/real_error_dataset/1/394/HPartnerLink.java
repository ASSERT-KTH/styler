/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
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

package org.apache.ode.daohib.bpel.hobj;

import java.util.Set;

/**
 * The HEndpointReference can either be attached to a scope (when it's specific
 * to a scope instance, for example because it has been assigned during the
 * instance execution) or to a process definition (general endpoint
 * configuration).
 *
 * @hibernate.class table="BPEL_PLINK_VAL"
 * @hibernate.query name="SELECT_PARTNER_LINK_IDS_BY_INSTANCES" query="select l.id from HPartnerLink l, HScope s where l.scope = s and s.instance in (:instances)"
 */
public class HPartnerLink extends HObject {
    public final static String SELECT_PARTNER_LINK_IDS_BY_INSTANCES = "SELECT_PARTNER_LINK_IDS_BY_INSTANCES";

    private String _linkName;

    private String _partnerRole;

    private String _myrole;

    private String _svcName;

    private byte[] _myEPR;

    private byte[] _partnerEPR;

    private HScope _scope;

    private HProcess _process;

    private int _modelId;

    private String _mySessionId;

    private String _partnerSessionId;

    private Set<HMessageExchange> _mex;

    public HPartnerLink() {
        super();
    }

    /**
     * @hibernate.property column="PARTNER_LINK" length="100" not-null="true"
     */
    public String getLinkName() {
        return _linkName;
    }

    public void setLinkName(String linkName) {
        _linkName = linkName;
    }

    /**
     * @hibernate.property column="PARTNERROLE" length="100"
     */
    public String getPartnerRole() {
        return _partnerRole;
    }

    public void setPartnerRole(String partnerRoleName) {
        _partnerRole = partnerRoleName;
    }

    /**
     * @hibernate.property type="org.apache.ode.daohib.bpel.hobj.GZipDataType"
     *
     * @hibernate.column name="MYROLE_EPR_DATA" sql-type="blob(2G)"
     */
    public byte[] getMyEPR() {
        return _myEPR;
    }

    public void setMyEPR(byte[] myEPR) {
        _myEPR = myEPR;
    }

    /**
     * @hibernate.property type="org.apache.ode.daohib.bpel.hobj.GZipDataType"
     *
     * @hibernate.column name="PARTNERROLE_EPR_DATA" sql-type="blob(2G)"
     */
    public byte[] getPartnerEPR() {
        return _partnerEPR;
    }

    public void setPartnerEPR(byte[] partnerEPR) {
        _partnerEPR = partnerEPR;
    }

    /**
     * @hibernate.many-to-one column="PROCESS" foreign-key="none"
     */
    public HProcess getProcess() {
        return _process;
    }

    public void setProcess(HProcess process) {
        _process = process;
    }

    /**
     * @hibernate.many-to-one column="SCOPE" foreign-key="none"
     */
    public HScope getScope() {
        return _scope;
    }

    public void setScope(HScope scope) {
        _scope = scope;
    }

    public void setServiceName(String svcName) {
        _svcName = svcName;
    }

    /**
     * @hibernate.property column="SVCNAME"
     */
    public String getServiceName() {
        return _svcName;
    }

    /**
     * @hibernate.property column="MYROLE" length="100"
     * @return
     */
    public String getMyRole() {
        return _myrole;
    }

    public void setMyRole(String myrole) {
        _myrole = myrole;
    }

    /**
     * @hibernate.property column="MODELID"
     */
    public int getModelId() {
        return _modelId;
    }

    public void setModelId(int modelId) {
        _modelId = modelId;
    }

    /**
     * @hibernate.property column="MYSESSIONID"
     */
    public String getMySessionId() {
        return _mySessionId;
    }

    /**
     * @hibernate.property column="PARTNERSESSIONID"
     */
    public String getPartnerSessionId() {
        return _partnerSessionId;
    }

    public void setPartnerSessionId(String session) {
        _partnerSessionId = session;
    }

    public void setMySessionId(String sessionId) {
        _mySessionId = sessionId;
    }

    /**
     * @hibernate.set
     *    lazy="true"
     *    inverse="true"
     *    cascade="delete"
     * @hibernate.collection-key column="PARTNERLINK" foreign-key="none"
     * @hibernate.collection-one-to-many
     *    class="org.apache.ode.daohib.bpel.hobj.HMessageExchange"
     */
    public Set<HMessageExchange> getMessageExchanges() {
      return _mex;
    }

    public void setMessageExchanges(Set<HMessageExchange> mex) {
      _mex = mex;
    }

    /**
     * toString method: creates a String representation of the object
     * @return the String representation
     * @author Veresh Jain
     */
    public String toString() {
        StringBuilder  buffer = new StringBuilder ();
        buffer.append("HPartnerLink[");
        buffer.append("_linkName = ").append(_linkName);
        buffer.append(", _mex = ").append(_mex);
        buffer.append(", _modelId = ").append(_modelId);
        buffer.append(", _myEPR = ").append(_myEPR);
        buffer.append(", _myrole = ").append(_myrole);
        buffer.append(", _mySessionId = ").append(_mySessionId);
        buffer.append(", _partnerEPR = ").append(_partnerEPR);
        buffer.append(", _partnerRole = ").append(_partnerRole);
        buffer.append(", _partnerSessionId = ").append(_partnerSessionId);
        buffer.append(", _process = ").append(_process);
        buffer.append(", _scope = ").append(_scope);
        buffer.append(", _svcName = ").append(_svcName);
        buffer.append("]");
        return buffer.toString();
    }
}
