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
package org.wso2.siddhi.core.query.stream.packer.sequence;

import org.apache.log4j.Logger;
import org.wso2.siddhi.core.event.AtomicEvent;
import org.wso2.siddhi.core.event.ComplexEvent;
import org.wso2.siddhi.core.event.StateEvent;
import org.wso2.siddhi.core.statemachine.sequence.OrSequenceState;
import org.wso2.siddhi.core.statemachine.sequence.SequenceState;
import org.wso2.siddhi.core.query.stream.packer.SingleStreamPacker;
import org.wso2.siddhi.core.query.stream.recevier.sequence.SequenceSingleStreamReceiver;

public class SequenceStreamPacker extends SingleStreamPacker {
    static final Logger log = Logger.getLogger(SequenceStreamPacker.class);

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
        if (log.isDebugEnabled()) {
            log.debug("sp state=" + state.getStateNumber() + " event=" + complexEvent);
        }
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
            if (log.isDebugEnabled()) {
                log.debug("->" + nextState.getStateNumber());
            }
            if (nextState instanceof OrSequenceState) {
                if (log.isDebugEnabled()) {
                    log.debug("->" + ((OrSequenceState) nextState).getPartnerState().getStateNumber());
                }
                ((OrSequenceState) nextState).getPartnerState().getSequenceSingleStreamReceiver().addToNextEvents(eventBundle);
            }
            nextState.getSequenceSingleStreamReceiver().addToNextEvents(eventBundle);
        }
    }
}
