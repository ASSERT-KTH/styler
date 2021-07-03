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
package org.wso2.siddhi.core.stream.packer.sequence;

import org.wso2.siddhi.core.event.AtomicEvent;
import org.wso2.siddhi.core.event.ComplexEvent;
import org.wso2.siddhi.core.event.StateEvent;
import org.wso2.siddhi.core.statemachine.sequence.OrSequenceState;
import org.wso2.siddhi.core.statemachine.sequence.SequenceState;
import org.wso2.siddhi.core.stream.packer.SingleStreamPacker;
import org.wso2.siddhi.core.stream.recevier.sequence.SequenceSingleStreamReceiver;

public class SequenceStreamPacker extends SingleStreamPacker {

    protected SequenceSingleStreamReceiver streamReceiver;
    protected SequenceState state;
    protected SequenceState nextState;

    public SequenceStreamPacker(SequenceState state) {
        this.state = state;
        this.nextState = state.getNextState();
    }

    public void setStreamReceiver(SequenceSingleStreamReceiver streamReceiver) {
        this.streamReceiver = streamReceiver;
    }

    @Override
    public void process(ComplexEvent complexEvent) {
        //System.out.println("sp state=" + state.getStateNumber() + " event=" + complexEvent);
        setEventState((StateEvent) complexEvent);
        if (state.isLast()) {
            sendEvent((StateEvent)complexEvent);
        }
        passToStreamReceivers((StateEvent) complexEvent);
    }

    protected void setEventState(StateEvent eventBundle) {
        eventBundle.setEventState(state.getStateNumber());
    }

    protected void sendEvent(AtomicEvent atomicEvent) {
        queryProjector.process(atomicEvent);
    }

    protected void passToStreamReceivers(StateEvent eventBundle) {
        if (nextState != null) {
            //System.out.println("->" + nextState.getStateNumber());
            if (nextState instanceof OrSequenceState) {
                //System.out.println("->" + ((OrSequenceState) nextState).getPartnerState().getStateNumber());
                ((OrSequenceState) nextState).getPartnerState().getSequenceSingleStreamReceiver().addToNextEvents(eventBundle);
            }
            nextState.getSequenceSingleStreamReceiver().addToNextEvents(eventBundle);
        }
    }
}
