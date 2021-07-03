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
import org.wso2.siddhi.core.event.remove.RemoveEvent;
import org.wso2.siddhi.core.event.remove.RemoveListEvent;
import org.wso2.siddhi.core.util.SchedulerQueue;

public class LengthWindowHandler extends WindowHandler {

    int lengthToKeep;
    private volatile int currentLength = 0;

    @Override
    public void setParameters(Object[] parameters) {
        lengthToKeep = (Integer) parameters[0];
    }

    @Override
    public void process(ComplexEvent complexEvent) {
        try {
            if (complexEvent instanceof Event) {
                SchedulerQueue<StreamEvent> queue = getWindow();
                queue.put((StreamEvent) complexEvent);
                passToNextStreamProcessor(complexEvent);
                if (currentLength == lengthToKeep) {
                    passToNextStreamProcessor(new RemoveEvent((Event) queue.poll(), System.currentTimeMillis()));
                } else {
                    currentLength++;
                }
            } else if (complexEvent instanceof ListEvent) {
                SchedulerQueue<StreamEvent> queue = getWindow();
                Event[] inEvents = ((ListEvent) complexEvent).getEvents();
                int oldEventLength = inEvents.length - (lengthToKeep - currentLength);
                if (oldEventLength > 0) {
                    Event[] oldEvents = new Event[inEvents.length - (lengthToKeep - currentLength)];
                    int oldEventIndex = 0;
                    for (Event event : inEvents) {
                        queue.put(event);
                        passToNextStreamProcessor(complexEvent);
                        if (currentLength == lengthToKeep) {
                            oldEvents[oldEventIndex] = new RemoveEvent((Event) queue.poll(), System.currentTimeMillis());
                            oldEventIndex++;
                        } else {
                            currentLength++;
                        }
                    }
                    passToNextStreamProcessor(new RemoveListEvent(oldEvents, System.currentTimeMillis()));
                } else {
                    for (Event event : inEvents) {
                        queue.put(event);
                        passToNextStreamProcessor(complexEvent);
                        currentLength++;
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
