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
package org.wso2.siddhi.core.stream.handler.window;

import org.wso2.siddhi.core.event.ComplexEvent;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.event.ListEvent;
import org.wso2.siddhi.core.event.StreamEvent;
import org.wso2.siddhi.core.event.in.InEvent;
import org.wso2.siddhi.core.event.in.InListEvent;
import org.wso2.siddhi.core.event.in.InStream;
import org.wso2.siddhi.core.event.remove.RemoveEvent;
import org.wso2.siddhi.core.event.remove.RemoveListEvent;
import org.wso2.siddhi.core.event.remove.RemoveStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimeBatchWindowHandler extends WindowHandler implements Runnable {

    long timeToKeep;
    List<Event> newEventList = new ArrayList<Event>();
    List<Event> oldEventList;

    @Override
    public void setParameters(Object[] parameters) {
        if (parameters[0] instanceof Integer) {
            timeToKeep = (Integer) parameters[0];
        } else {
            timeToKeep = (Long) parameters[0];
        }
        getEventRemoverScheduler().schedule(this, timeToKeep, TimeUnit.MILLISECONDS);
    }

    @Override
    public void process(ComplexEvent complexEvent) {
        if (complexEvent instanceof StreamEvent) {
            if (complexEvent instanceof InStream) {
                if (complexEvent instanceof Event) {
                    newEventList.add(((Event) complexEvent));
                } else {//ListEvent
                    Collections.addAll(newEventList, ((ListEvent) complexEvent).getEvents());
                }
            } else {
                if (complexEvent instanceof Event) {
                    newEventList.add(new InEvent((Event) complexEvent));
                } else {//ListEvent
                    for (Event aEvent : ((ListEvent) complexEvent).getEvents()) {
                        newEventList.add(new InEvent(aEvent));
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        oldEventList = new ArrayList<Event>();
        while (true) {
            StreamEvent event = getWindow().poll();
            if (event == null) {
                getEventRemoverScheduler().schedule(this, timeToKeep, TimeUnit.MILLISECONDS);

                sendRemoveEvents(oldEventList);

                oldEventList = newEventList;
                newEventList = new ArrayList<Event>();

                sendInEvents(oldEventList);

                for (Event aEvent : oldEventList) {
                    try {
                        getWindow().put(aEvent);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                oldEventList = null;

                break;
            }
            if (event instanceof RemoveStream) {
                if (event instanceof Event) {
                    oldEventList.add(((Event) event));
                } else {//ListEvent
                    Collections.addAll(oldEventList, ((ListEvent) event).getEvents());
                }
            } else {
                if (event instanceof Event) {
                    oldEventList.add(new RemoveEvent((Event) event,System.currentTimeMillis()));
                } else {//ListEvent
                    for (Event aEvent : ((ListEvent) event).getEvents()) {
                        oldEventList.add(new RemoveEvent(aEvent,System.currentTimeMillis()));
                    }
                }
            }
        }
    }

    private void sendInEvents(List<Event> eventList) {
        int size = eventList.size();
        if (size > 1) {
            getNextPreStreamFlowProcessor().process(new InListEvent((eventList.toArray(new Event[size]))));
        } else if (size == 1) {
            Event aEvent = eventList.get(0);
            getNextPreStreamFlowProcessor().process(new InEvent(aEvent));
        }
    }


    private void sendRemoveEvents(List<Event> eventList) {
        int size = eventList.size();
        if (size > 1) {
            getNextPreStreamFlowProcessor().process(new RemoveListEvent((eventList.toArray(new Event[size])), System.currentTimeMillis()));
        } else if (size == 1) {
            Event aEvent = eventList.get(0);
            getNextPreStreamFlowProcessor().process(new RemoveEvent(aEvent, System.currentTimeMillis()));
        }
    }


}
