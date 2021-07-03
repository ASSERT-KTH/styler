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

import org.wso2.siddhi.core.config.SiddhiConfiguration;
import org.wso2.siddhi.core.event.StreamEvent;
import org.wso2.siddhi.core.stream.StreamElement;
import org.wso2.siddhi.core.stream.recevier.RunnableStreamReceiver;
import org.wso2.siddhi.core.util.SchedulerQueue;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

public class SequenceStreamReceiver implements StreamElement,
                                               RunnableStreamReceiver {

    //  private List<SingleStream> inputStreamList;
    private String streamId;
    private ThreadPoolExecutor threadPoolExecutor;
    private SchedulerQueue<StreamEvent> inputQueue = new SchedulerQueue<StreamEvent>();
    private List<SequenceSingleStreamReceiver> sequenceSingleStreamReceiverList;
    private int sequenceSingleStreamReceiverListSize;
    private List<SequenceSingleStreamReceiver> otherStreamReceiverList;
    private int otherStreamReceiverListSize;
    private SiddhiConfiguration configuration;

    public SequenceStreamReceiver(String streamId,
                                  List<SequenceSingleStreamReceiver> sequenceSingleStreamReceiverList,
                                  ThreadPoolExecutor threadPoolExecutor) {
        this.streamId = streamId;
        this.sequenceSingleStreamReceiverList = sequenceSingleStreamReceiverList;
        this.threadPoolExecutor = threadPoolExecutor;
        this.sequenceSingleStreamReceiverListSize = sequenceSingleStreamReceiverList.size();
    }

    @Override
    public void receive(StreamEvent streamEvent) throws InterruptedException {
        if (configuration.isSingleThreading()) {
                process(streamEvent);
        } else {
            if (!inputQueue.put(streamEvent)) {
                threadPoolExecutor.execute(this);
            }
        }
    }

    @Override
    public void setSiddhiConfiguration(SiddhiConfiguration configuration) {
        this.configuration=configuration;
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void run() {
        int eventCounter = 0;
        while (true) {

            StreamEvent streamEvent = inputQueue.poll();
            //System.out.println("thread "+Thread.currentThread().getName()+" "+streamEvent);
            if (streamEvent == null) {
                break;
            } else if (configuration.getEventBatchSize() > 0 && eventCounter > configuration.getEventBatchSize()) {
                threadPoolExecutor.execute(this);
                break;
            }
            eventCounter++;
            process(streamEvent);

        }
    }

    private void process(StreamEvent streamEvent) {
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
