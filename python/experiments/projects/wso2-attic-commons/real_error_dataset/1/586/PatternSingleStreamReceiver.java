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
package org.wso2.siddhi.core.query.stream.recevier.pattern;

import org.apache.log4j.Logger;
import org.wso2.siddhi.core.event.StateEvent;
import org.wso2.siddhi.core.event.StreamEvent;
import org.wso2.siddhi.core.event.in.InStateEvent;
import org.wso2.siddhi.core.event.management.PersistenceManagementEvent;
import org.wso2.siddhi.core.persistence.PersistenceObject;
import org.wso2.siddhi.core.persistence.PersistenceStore;
import org.wso2.siddhi.core.persistence.Persister;
import org.wso2.siddhi.core.query.stream.StreamElement;
import org.wso2.siddhi.core.query.stream.StreamProcessor;
import org.wso2.siddhi.core.query.stream.recevier.StreamReceiver;
import org.wso2.siddhi.core.statemachine.pattern.PatternState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PatternSingleStreamReceiver implements StreamReceiver, StreamElement, Persister {
    static final Logger log = Logger.getLogger(PatternSingleStreamReceiver.class);
    protected int complexEventSize;
    protected PatternState state;
    protected PatternState nextState;
    protected PatternState nextEveryState;
    protected StreamProcessor firstSimpleStreamProcessor;
    protected BlockingQueue<StateEvent> currentEvents = new LinkedBlockingQueue<StateEvent>();
    protected BlockingQueue<StateEvent> nextEvents = new LinkedBlockingQueue<StateEvent>();
    //    private final boolean first;
    protected final int currentState;
    protected String nodeId;
    protected PersistenceStore persistenceStore;


    public PatternSingleStreamReceiver(PatternState state,
                                       StreamProcessor firstSimpleStreamProcessor,
                                       int complexEventSize) {
        this.state = state;
        this.nextState = state.getNextState();
        this.nextEveryState = state.getNextEveryState();
        this.currentState = state.getStateNumber();
        this.complexEventSize = complexEventSize;
        this.firstSimpleStreamProcessor = firstSimpleStreamProcessor;
//        init(state, complexEventSize);
    }

    public void init() {
        if (state.isFirst()) {
            //first event
            addToNextEvents(new InStateEvent(new StreamEvent[complexEventSize]));
        }
    }

    @Override
    public void receive(StreamEvent event) {
        if (log.isDebugEnabled()) {
            log.debug("pr state=" + currentState + " event=" + event + " ||currentEvents=" + currentEvents);
        }
        for (StateEvent currentEvent : currentEvents) {

            currentEvent.setStreamEvent(currentState, event);
            firstSimpleStreamProcessor.process(currentEvent);
            if (currentEvent.getEventState() < currentState) {
                currentEvent.setStreamEvent(currentState, null);
                addToNextEvents(currentEvent);
            }
        }
//        currentEvents.clear();
//        }
    }

    public String getStreamId() {
        return state.getSingleStream().getStreamId();
    }

    public void addToNextEvents(StateEvent stateEvent) {
//        System.out.println("add to next ss");
        try {
            nextEvents.put(stateEvent);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void moveNextEventsToCurrentEvents() {
        //todo need to check which is faster
        // 1
//        currentEvents.clear();
//        currentEvents.addAll(nextEvents);
//        nextEvents.clear();

//        // 2
        currentEvents = nextEvents;
        nextEvents = new LinkedBlockingQueue<StateEvent>();
    }

    @Override
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public void setPersistenceStore(PersistenceStore persistenceStore) {
        this.persistenceStore=persistenceStore;
    }

    @Override
    public void save(PersistenceManagementEvent persistenceManagementEvent) {
        persistenceStore.save(persistenceManagementEvent,nodeId,new PersistenceObject(new ArrayList<StateEvent>(currentEvents),new ArrayList<StateEvent>(nextEvents)));
    }

    @Override
    public void load(PersistenceManagementEvent persistenceManagementEvent) {
      PersistenceObject persistenceObject = persistenceStore.load(persistenceManagementEvent,nodeId);
        currentEvents=new LinkedBlockingQueue<StateEvent>((List)persistenceObject.getData()[0]);
        nextEvents=new LinkedBlockingQueue<StateEvent>((List)persistenceObject.getData()[1]);
    }
}
