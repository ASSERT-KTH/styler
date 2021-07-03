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
package org.wso2.siddhi.core.stream.recevier.sequence;

import org.wso2.siddhi.core.event.StreamEvent;
import org.wso2.siddhi.core.util.SchedulerQueue;
import org.wso2.siddhi.core.stream.StreamElement;
import org.wso2.siddhi.core.stream.recevier.StreamReceiver;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

public class SequenceStreamReceiver implements StreamReceiver, StreamElement, Runnable {

    //  private List<SingleStream> inputStreamList;
    private String streamId;
    private ThreadPoolExecutor threadPoolExecutor;
    private SchedulerQueue<StreamEvent> inputQueue = new SchedulerQueue<StreamEvent>();
    private List<SequenceSingleStreamReceiver> sequenceSingleStreamReceiverList;
    private int sequenceSingleStreamReceiverListSize;
    private List<SequenceSingleStreamReceiver> otherStreamReceiverList;
    private int otherStreamReceiverListSize;

    public SequenceStreamReceiver(String streamId,
                                  List<SequenceSingleStreamReceiver> sequenceSingleStreamReceiverList,
                                  ThreadPoolExecutor threadPoolExecutor) {
        this.streamId = streamId;
        this.sequenceSingleStreamReceiverList = sequenceSingleStreamReceiverList;
        this.threadPoolExecutor = threadPoolExecutor;
        this.sequenceSingleStreamReceiverListSize = sequenceSingleStreamReceiverList.size();
    }

    @Override
    public void receive(StreamEvent event) throws InterruptedException {
//        //System.out.println(event);
        if (!inputQueue.put(event)) {
            threadPoolExecutor.submit(this);
        }
    }

    @Override
    public void run() {
        int eventCounter = 0;
        while (true) {

            StreamEvent streamEvent = inputQueue.poll();
            //System.out.println("thread "+Thread.currentThread().getName()+" "+streamEvent);
            if (streamEvent == null) {
                break;
            } else if (eventCounter > 10) {
                threadPoolExecutor.submit(this);
                break;
            }
            eventCounter++;
            try{
            //in reverse order to execute the later states first to overcome to dependencies of count states
            for (int i = sequenceSingleStreamReceiverListSize - 1; i >= 0; i--) {
                sequenceSingleStreamReceiverList.get(i).moveNextEventsToCurrentEvents();
            }
            if (otherStreamReceiverListSize > 0) {
                StreamEvent resetEvent = new SequenceResetEvent(System.currentTimeMillis());
                for (int i = 0, otherStreamReceiverListSize = otherStreamReceiverList.size(); i < otherStreamReceiverListSize; i++) {
                    otherStreamReceiverList.get(i).receive(resetEvent);
                }
            }
            for (int i = sequenceSingleStreamReceiverListSize - 1; i >= 0; i--) {
                sequenceSingleStreamReceiverList.get(i).receive(streamEvent);
            }
            }catch (Throwable t){
                   t.printStackTrace();
            }

        }
    }

    public String getStreamId() {
        return streamId;
    }

    @Override
    public SchedulerQueue<StreamEvent> getWindow() {
        return null;
    }


    public void setOtherStreamReceivers(
            List<SequenceSingleStreamReceiver> otherStreamReceiverList) {
        this.otherStreamReceiverList = otherStreamReceiverList;
        otherStreamReceiverListSize = otherStreamReceiverList.size();
    }

    public List<SequenceSingleStreamReceiver> getSequenceSingleStreamReceiverList() {
        return sequenceSingleStreamReceiverList;
    }
}
