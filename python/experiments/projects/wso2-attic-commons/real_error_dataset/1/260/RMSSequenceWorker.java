/*
 * Copyright 2008,2009 WSO2, Inc. http://www.wso2.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.mercury.workers;

import org.wso2.mercury.state.RMSSequence;
import org.wso2.mercury.exception.RMMessageBuildingException;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * there is a sender worker for each sequence
 * they always look into the sequence and do the appropriate
 * work.
 */
public class RMSSequenceWorker implements Runnable {

    private static Log log = LogFactory.getLog(RMSSequenceWorker.class);

    private long sequenceWorkerSleepTime;

    private RMSSequence rmsSequence;

    public RMSSequenceWorker(RMSSequence rmsSequence) {
        this.rmsSequence = rmsSequence;
        this.rmsSequence.setRMSSequenceWorker(this);
    }

    private Object lock = new Object();

    public void run() {
        // this worker works until this sequnce ends
        while ((rmsSequence.getState() != RMSSequence.STATE_TERMINATE) &&
                ((System.currentTimeMillis() - this.rmsSequence.getLastAccessedTime()) < this.rmsSequence.getTimeoutTime())){
            try {
                rmsSequence.doActions();
            } catch (AxisFault e) {
                log.error("Fault occured when doing actions for the RMS Sequence "
                        + this.rmsSequence.getSequenceID(), e);
            } catch (RMMessageBuildingException e) {
                log.error("Could not build the message ", e);
            }

            try {
                log.debug("Wait");
                // wait on a lock to make it possible to notify in any time
                synchronized (lock) {
                    lock.wait(sequenceWorkerSleepTime);
                }
                log.debug("Wakeup...");
            } catch (InterruptedException e) {
                log.error("Interupted :" + e );
            }
        }

        try {
            if (rmsSequence.getState() == RMSSequence.STATE_TERMINATE){
                // this is a properly terminated sequence
                // so send the terminate message and finish the sequence
                // here we donot bother to send the termainate message reliably.
                // if the server does not receive it, it will time out
                rmsSequence.sendTerminateSequenceMessage(null);
            } else {
                // this is a time out sequence
                // in this case we don't have to send a terminate message for annonymous
                // sequences
                if (rmsSequence.isAnnonymous()){
                    rmsSequence.rmsSequenceExpired();
                } else {
                    // TODO: check this
                    rmsSequence.sendTerminateSequenceMessage(null);
                }
            }

        } catch (AxisFault e) {
            log.error("Fault occured when doing actions for the RMS Sequence "
                        + this.rmsSequence.getSequenceID(), e);
        } catch (RMMessageBuildingException e) {
             log.error("Could not build the message ", e);
        }
        log.info("Stopping the RMS thread for RMS sequence " + this.rmsSequence.getSequenceID());
        
    }
    
    public void wakeUp() {
        try {
            log.debug("WakeUp");
            synchronized (lock) {
                lock.notify();
            }
            log.debug("Thread notified");
        } catch(Exception e) {
            log.error("Failed to notify : " +e, e);
        }
    }

    public long getSequenceWorkerSleepTime() {
        return sequenceWorkerSleepTime;
    }

    public void setSequenceWorkerSleepTime(long sequenceWorkerSleepTime) {
        this.sequenceWorkerSleepTime = sequenceWorkerSleepTime;
    }
}
