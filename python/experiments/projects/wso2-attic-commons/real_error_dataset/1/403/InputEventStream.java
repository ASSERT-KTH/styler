/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.siddhi.core.eventstream;

import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.eventstream.handler.InputStreamHandler;
import org.wso2.siddhi.core.eventstream.handler.query.window.AbstractTimeWindowQueryInputHandler;
import org.wso2.siddhi.core.eventstream.handler.query.window.AbstractWindowQueryInputStreamHandler;
import org.wso2.siddhi.core.eventstream.queue.EventQueue;
import org.wso2.siddhi.core.eventstream.queue.EventQueueImpl;
import org.wso2.siddhi.core.exception.InvalidEventStreamIdException;
import org.wso2.siddhi.core.node.EventSink;
import org.wso2.siddhi.core.thread.SiddhiThreadPool;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InputEventStream {

    private EventQueue eventQueue = new EventQueueImpl();
    // private List<EventQueue> windowQueueList = new CopyOnWriteArrayList<EventQueue>();
    private List<InputStreamHandler> inputStreamHandlerList = new CopyOnWriteArrayList<InputStreamHandler>();
    //    private volatile int windowNumber = 0;
//    private volatile long windowMinExpireTime = -1;
//    private volatile boolean windowEventExist = false;
//    private int windowQueueListSize; //todo
    private EventSink eventSink;

    public InputEventStream(EventSink eventSink) {
        this.eventSink = eventSink;
    }

    public Event takeEvent() throws InterruptedException {
        return eventQueue.take();
    }

    public boolean removeEvent(Event event) throws InterruptedException {
        return eventQueue.remove(event);
    }
//    public Event takeEvent() throws InterruptedException {
//        int round = 0;
//        while (true) {
//            if (null != eventQueue.peek()) {
//                return eventQueue.poll();
//            }
//
//            for (; windowNumber < windowQueueListSize; windowNumber++) {
//                Event event = windowQueueList.get(windowNumber).peek();
//                if (null != event) {
//                    long currentTime = System.currentTimeMillis();
//                    if (event.getTimeStamp() <= currentTime) {
//                        windowMinExpireTime = -1;
//                        return cleanEvent( windowQueueList.get(windowNumber).poll());
//                    } else if (windowMinExpireTime == -1 || event.getTimeStamp() > windowMinExpireTime) {
//                        windowMinExpireTime = currentTime;
//                    }
//                }
//            }
//            if (round == 0) {
//                windowMinExpireTime = -1;
//                windowNumber = 0;
//                round++;
//            } else {
//                if (windowMinExpireTime != -1) {
//                    try {
//                        Thread.sleep(windowMinExpireTime);
//                    } catch (InterruptedException e) {
//                        windowMinExpireTime = -1;
//                        windowNumber = 0;
//                    }
//
//                } else {
//                    try {
//                        eventQueue.take();
//                    } catch (InterruptedException e) {
//                        throw new InterruptedException("Interrupted at " + eventSink.getQuery().getStreamId() +
//                                                       " head when taking event form event queue");
//                    }
//                }
//            }
//        }
//    }

    public EventQueue getWindow(String streamId) throws InvalidEventStreamIdException {
        for (InputStreamHandler inputStreamHandler : inputStreamHandlerList) {
            if (inputStreamHandler.getStreamId().equals(streamId) && inputStreamHandler instanceof AbstractWindowQueryInputStreamHandler) {
                return ((AbstractWindowQueryInputStreamHandler) inputStreamHandler).getWindow();
            }
        }
        throw new InvalidEventStreamIdException(streamId + " is not present in " +
                                                eventSink.getStreamId() + " head ");
    }

    public void assignInputStreamHandler(InputStreamHandler inputStreamHandler) {
//        InputStreamHandler inputStreamHandler =
        inputStreamHandler.assignEventQueue(eventQueue);
        inputStreamHandlerList.add(inputStreamHandler);
        // return inputStreamHandler;
    }

    public InputStreamHandler getQueryInputStreamHandler(String sourceStreamId) {
        for (InputStreamHandler inputStreamHandler : inputStreamHandlerList) {
            if (inputStreamHandler.getStreamId().equals(sourceStreamId)) {
                return inputStreamHandler;
            }
        }
        return null;
    }


    public void removeInputStream(String streamId) {

        for (Iterator<InputStreamHandler> iterator = inputStreamHandlerList.iterator(); iterator.hasNext(); ) {
            InputStreamHandler aInputStreamHandler = iterator.next();
            if (aInputStreamHandler.getStreamId().equals(streamId)) {
                if (aInputStreamHandler instanceof AbstractWindowQueryInputStreamHandler) {
                    ((AbstractWindowQueryInputStreamHandler) aInputStreamHandler).getWindow().clear();
                }
                iterator.remove();
                break;
            }

        }
    }

    public void startRunnable(SiddhiThreadPool siddhiThreadPool) {
        for (InputStreamHandler inputStreamHandler : inputStreamHandlerList) {
            if (inputStreamHandler instanceof AbstractTimeWindowQueryInputHandler) {
                ((AbstractTimeWindowQueryInputHandler) inputStreamHandler).startRunnable(siddhiThreadPool);
            }
        }
    }

    public void stopRunnable() throws InterruptedException {
        for (InputStreamHandler inputStreamHandler : inputStreamHandlerList) {
//            if (inputStreamHandler instanceof AbstractTimeWindowQueryInputHandler) {
                inputStreamHandler.put(SiddhiManager.generateKillEvent());
//            }
        }
    }

    public void resetRunnable() throws InterruptedException {
        for (InputStreamHandler inputStreamHandler : inputStreamHandlerList) {
//            if (inputStreamHandler instanceof AbstractTimeWindowQueryInputHandler) {
                inputStreamHandler.put(SiddhiManager.generateResetEvent());
//            }
        }
    }
}
