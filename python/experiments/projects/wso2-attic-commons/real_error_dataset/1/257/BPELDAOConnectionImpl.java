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

package org.apache.ode.dao.jpa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ode.bpel.common.BpelEventFilter;
import org.apache.ode.bpel.common.Filter;
import org.apache.ode.bpel.common.InstanceFilter;
import org.apache.ode.bpel.dao.*;
import org.apache.ode.bpel.evt.BpelEvent;
import org.apache.ode.bpel.evt.ScopeEvent;
import org.apache.ode.bpel.iapi.ProcessConf.CLEANUP_CATEGORY;
import org.apache.ode.utils.ISO8601DateParser;
import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.apache.openjpa.persistence.OpenJPAQuery;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.xml.namespace.QName;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

/**
 * @author Matthieu Riou <mriou at apache dot org>
 */
public class BPELDAOConnectionImpl implements BpelDAOConnection {
	static final Log __log = LogFactory.getLog(BPELDAOConnectionImpl.class);

	protected EntityManager _em;

    public BPELDAOConnectionImpl(EntityManager em) {
        _em = em;
    }

    public List<BpelEvent> bpelEventQuery(InstanceFilter ifilter,
                                          BpelEventFilter efilter) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public List<Date> bpelEventTimelineQuery(InstanceFilter ifilter,
                                             BpelEventFilter efilter) {
        // TODO
        throw new UnsupportedOperationException();
    }

	public ProcessInstanceDAO getInstance(Long iid) {
        return _em.find(ProcessInstanceDAOImpl.class, iid);
    }

    public void close() {
        _em = null;
    }

    public MessageExchangeDAO createMessageExchange(String mexId, char dir) {
        MessageExchangeDAOImpl ret = new MessageExchangeDAOImpl(mexId, dir);

        _em.persist(ret);
        return ret;
    }

    public MessageDAO createMessage(QName type) {
        MessageDAOImpl ret = new MessageDAOImpl(type, null);
        _em.persist(ret);
        return ret;  
    }

    public void releaseMessageExchange(String mexid) {
        MessageExchangeDAO dao = getMessageExchange(mexid);
        dao.release(true);
 	}

    public ProcessDAO createProcess(QName pid, QName type, String guid, long version) {
        ProcessDAOImpl ret = new ProcessDAOImpl(pid,type,guid,version);
        _em.persist(ret);
        return ret;
    }

    public ProcessDAO getProcess(QName processId) {
        List l = _em.createQuery("select x from ProcessDAOImpl x where x._processId = ?1")
                .setParameter(1, processId.toString()).getResultList();
        if (l.size() == 0) return null;
        return (ProcessDAOImpl) l.get(0);
    }

    public ScopeDAO getScope(Long siidl) {
        return _em.find(ScopeDAOImpl.class, siidl);
    }

    public void insertBpelEvent(BpelEvent event, ProcessDAO process, ProcessInstanceDAO instance) {
        EventDAOImpl eventDao = new EventDAOImpl();
        eventDao.setTstamp(new Timestamp(System.currentTimeMillis()));
        eventDao.setType(BpelEvent.eventName(event));
        String evtStr = event.toString();
        eventDao.setDetail(evtStr.substring(0, Math.min(254, evtStr.length())));
        if (process != null)
            eventDao.setProcess((ProcessDAOImpl) process);
        if (instance != null)
            eventDao.setInstance((ProcessInstanceDAOImpl) instance);
        if (event instanceof ScopeEvent)
            eventDao.setScopeId(((ScopeEvent) event).getScopeId());
        eventDao.setEvent(event);
        _em.persist(eventDao);
	}

    private static String dateFilter(String filter) {
        String date = Filter.getDateWithoutOp(filter);
        String op = filter.substring(0,filter.indexOf(date));
        Date dt;
        try {
            dt = ISO8601DateParser.parse(date);
        } catch (ParseException e) {
            __log.error("Error parsing date.", e);
            return "";
        }
        Timestamp ts = new Timestamp(dt.getTime());
        return op + " '" + ts.toString() + "'";
    }

	@SuppressWarnings("unchecked")
    public Collection<ProcessInstanceDAO> instanceQuery(InstanceFilter criteria) {
        StringBuffer query = new StringBuffer();
        query.append("select pi from ProcessInstanceDAOImpl as pi");
        query.append(genSQL(criteria));

        if (__log.isDebugEnabled()) {
        	__log.debug(query.toString());
        }

        // criteria limit
        Query pq = _em.createQuery(query.toString());
        OpenJPAQuery kq = OpenJPAPersistence.cast(pq);
        kq.getFetchPlan().setFetchBatchSize(0);      // set to 0 inorder to use the default value by JDBC driver
        List<ProcessInstanceDAO> ql = kq.getResultList();

        Collection<ProcessInstanceDAO> list = new ArrayList<ProcessInstanceDAO>();
        int num = 0;
        for (Object piDAO : ql) {
            if (num++ > criteria.getLimit()) break;
            ProcessInstanceDAO processInstanceDAO = (ProcessInstanceDAO) piDAO;
            list.add(processInstanceDAO);
        }

        return list;
	}

    @SuppressWarnings("unchecked")
    public int instanceQueryCount(InstanceFilter criteria) {
        StringBuffer query = new StringBuffer();
        query.append("select count(pi) from ProcessInstanceDAOImpl as pi");
        query.append(genSQL(criteria));

        if (__log.isDebugEnabled()) {
        	__log.debug(query.toString());
        }

        // criteria limit
        Query pq = _em.createQuery(query.toString());
        OpenJPAQuery kq = OpenJPAPersistence.cast(pq);
        kq.getFetchPlan().setFetchBatchSize(50);     // set to 0 inorder to use the default value by JDBC driver
        Number count = (Number)kq.getSingleResult();
        return count.intValue();
	}

    private String genSQL(InstanceFilter criteria) {
        StringBuffer query = new StringBuffer();

        if (criteria != null) {
            // Building each clause
            ArrayList<String> clauses = new ArrayList<String>();

            // iid filter
            if ( criteria.getIidFilter() != null ) {
                StringBuffer filters = new StringBuffer();
                List<String> iids = criteria.getIidFilter();
                for (int m = 0; m < iids.size(); m++) {
                    filters.append(" pi._instanceId = ").append(iids.get(m));
                    if (m < iids.size() - 1) filters.append(" or");
                }
                clauses.add(" (" + filters + ")");
            }

            // pid filter
            if (criteria.getPidFilter() != null) {
                StringBuffer filters = new StringBuffer();
                List<String> pids = criteria.getPidFilter();
                for (int m = 0; m < pids.size(); m++) {
                    filters.append(" pi._process._processId = '").append(pids.get(m)).append("'");
                    if (m < pids.size() - 1) filters.append(" or");
                }
                clauses.add(" (" + filters + ")");
            }

            // name filter
            if (criteria.getNameFilter() != null) {                                   
                String val = criteria.getNameFilter().replace("%", "\\%");
		        //name filter is updated to use "*" wildcard inside the name as well
                if (val.endsWith("*")) {
                    val = (val.substring(0, val.length()-1).equals("*") ? "" : val.substring(0, val.length()-1)) + "%";
                }
                //process type string begins with name space
                clauses.add(" pi._process._processType like '%}" + val.replace("*", "%") + "' ESCAPE '\\'");
            }

            // name space filter
            if (criteria.getNamespaceFilter() != null) {
                String val = criteria.getNamespaceFilter().replace("%", "\\%");
		        //name filter is updated to use "*" wildcard inside the name as well
                if (val.endsWith("*")) {
                    val = (val.substring(0, val.length()-1).equals("*") ? "" : val.substring(0, val.length()-1)) + "%";
                }
                //process type string begins with name space
                //Since we only search among {} only name space part is searched.
		        //namespace filter is updated to use "*" wildcard inside the namespace as well
                clauses.add(" pi._process._processType like '{" + val.replace("*", "%") + "}%' ESCAPE '\\'");
            }

            // version filter
            if (criteria.getVersionFilter() > 0) {
                clauses.add(" pi._process._version=" + criteria.getVersionFilter());
            }

            // started filter
            if (criteria.getStartedDateFilter() != null) {
                for ( String ds : criteria.getStartedDateFilter() ) {
                    clauses.add(" pi._dateCreated " + dateFilter(ds));
                }
            }

            // last-active filter
            if (criteria.getLastActiveDateFilter() != null) {
                for ( String ds : criteria.getLastActiveDateFilter() ) {
                    clauses.add(" pi._lastActive " + dateFilter(ds));
                }
            }

            // status filter
            if (criteria.getStatusFilter() != null) {
                StringBuffer filters = new StringBuffer();
                List<Short> states = criteria.convertFilterState();
                for (int m = 0; m < states.size(); m++) {
                    filters.append(" pi._state = ").append(states.get(m));
                    if (m < states.size() - 1) filters.append(" or");
                }
                clauses.add(" (" + filters.toString() + ")");
            }

            // $property filter
            if (criteria.getPropertyValuesFilter() != null) {
                Map<String,String> props = criteria.getPropertyValuesFilter();
                // join to correlation sets
                query.append(" inner join pi._rootScope._correlationSets as cs");
                int i = 0;
                for (String propKey : props.keySet()) {
                    i++;
                    // join to props for each prop
                    query.append(" inner join cs._props as csp"+i);
                    // add clause for prop key and value
                    clauses.add(" csp"+i+".propertyKey = '"+propKey+
                            "' and csp"+i+".propertyValue = '"+
                            // spaces have to be escaped, might be better handled in InstanceFilter
                            props.get(propKey).replaceAll("&#32;", " ")+"'");
                }
            }

            // order by
            StringBuffer orderby = new StringBuffer("");
            if (criteria.getOrders() != null) {
                orderby.append(" order by");
                List<String> orders = criteria.getOrders();
                for (int m = 0; m < orders.size(); m++) {
                    String field = orders.get(m);
                    String ord = " asc";
                    if (field.startsWith("-")) {
                        ord = " desc";
                    }
                    String fieldName = " pi._instanceId";
                    if ( field.endsWith("name") || field.endsWith("namespace")) {
                        fieldName = " pi._process._processType";
                    }
                    if ( field.endsWith("version")) {
                        fieldName = " pi._process._version";
                    }
                    if ( field.endsWith("status")) {
                        fieldName = " pi._state";
                    }
                    if ( field.endsWith("started")) {
                        fieldName = " pi._dateCreated";
                    }
                    if ( field.endsWith("last-active")) {
                        fieldName = " pi._lastActive";
                    }
                    orderby.append(fieldName + ord);
                    if (m < orders.size() - 1) orderby.append(", ");
                }

            }

            // Preparing the statement
            if (clauses.size() > 0) {
                query.append(" where");
                for (int m = 0; m < clauses.size(); m++) {
                    query.append(clauses.get(m));
                    if (m < clauses.size() - 1) query.append(" and");
                }
            }

            query.append(orderby);
        }

        return query.toString();
    }


	public Collection<ProcessInstanceDAO> instanceQuery(String expression) {
	    return instanceQuery(new InstanceFilter(expression));
	}

	public void setEntityManger(EntityManager em) {
		_em = em;
	}

    public MessageExchangeDAO getMessageExchange(String mexid) {
        List l = _em.createQuery("select x from MessageExchangeDAOImpl x where x._id = ?1")
        .setParameter(1, mexid).getResultList();
        if (l.size() == 0) return null;

        return (MessageExchangeDAOImpl) l.get(0);
    }

    public EntityManager getEntityManager() {
        return _em;
    }

    @SuppressWarnings("unchecked")
    public Map<Long, Collection<CorrelationSetDAO>> getCorrelationSets(Collection<ProcessInstanceDAO> instances) {
        if (instances.size() == 0) {
            return new HashMap<Long, Collection<CorrelationSetDAO>>();
        }
        ArrayList<Long> iids = new ArrayList<Long>(instances.size());
        for (ProcessInstanceDAO dao: instances) {
            iids.add(dao.getInstanceId());
        }
        Collection<CorrelationSetDAOImpl> csets = _em.createNamedQuery(CorrelationSetDAOImpl.SELECT_CORRELATION_SETS_BY_INSTANCES).setParameter("instances", iids).getResultList();
        Map<Long, Collection<CorrelationSetDAO>> map = new HashMap<Long, Collection<CorrelationSetDAO>>();
        for (CorrelationSetDAOImpl cset: csets) {
            Long id = cset.getScope().getProcessInstance().getInstanceId();
            Collection<CorrelationSetDAO> existing = map.get(id);
            if (existing == null) {
                existing = new ArrayList<CorrelationSetDAO>();
                map.put(id, existing);
            }
            existing.add(cset);
        }
        return map;
    }

    public ProcessManagementDAO getProcessManagement() {
        return new ProcessManagementDAOImpl(_em);
    }
}
