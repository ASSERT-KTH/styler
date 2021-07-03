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
package org.wso2.siddhi.core.node.processor.executor;

import org.wso2.siddhi.core.event.Event;

import java.util.HashSet;
import java.util.Set;

/**
 * This class implements the Executor for the sequence queries
 */
public class SequenceExecutor implements Executor {

    private Executor executor;       //holds the simple executor for this SequenceExecutor
    private int state;               // State of the pattern i.e. 0->1->2
    private Event[][] arrivedEvents;
    private SequenceExecutor nextExecutor;
    private long expiringTime = 0;
    private String checkingStreamName;
    private boolean fireEvent = false;
    private boolean isStarExecutor = false;
    private long lifeTime = -1;          // -1 indicates the unlimited life time

    /**
     * Get the life time of the Executor
     * @return  life time of the Executor
     */
    public long getLifeTime() {
        return lifeTime;
    }

    /**
     * Set life time of the executor
     * @param lifeTime life time of the executor
     */
    public void setLifeTime(long lifeTime) {
        this.lifeTime = lifeTime;
    }

    /**
     * Check whether the lifetime is set
     * @return  whether the lifetime is set
     */
    public boolean isLifeTimeSet() {
        if (lifeTime != -1) {
            return true;
        }
        return false;
    }

    /**
     *  Enable this executor to fire an Event
     * @param fireEvent
     */
    public void enableFireEvent(boolean fireEvent) {
        this.fireEvent = fireEvent;
    }

    /**
     * Set expiring time of the executor
     * @param currentTime current system time
     */
    public void setExpiringTime(long currentTime) {
        this.expiringTime = currentTime + lifeTime;
    }

    /**
     * Get the simple executor of the Pattern executor
     * @return executor
     */
    public Executor getExecutor() {
        return executor;
    }

    /**
     *  Get the state of the executor
     * @return  state
     */
    public int getState() {
        return state;
    }

    /**
     * Clear stored events in the executor
     */
    public void clearEvents() {
        arrivedEvents[state][0] = null;
        if (isStarExecutor) {
            arrivedEvents[state][1] = null;
            arrivedEvents[state][2] = null;
        }

    }

    /**
     *  Pattern Executor
     * @param state  state of the executor
     * @param executor simple executor for the sequence executor
     * @param checkingStreamName stream name
     */
    public SequenceExecutor(int state, Executor executor, String checkingStreamName) {
        this.executor = executor;
        this.state = state;
        this.checkingStreamName = checkingStreamName;
    }

  /**
     *
     * @param state  state of the executor
     * @param executor simple executor for the sequence executor
   * Used at Condition passer (1st time)
     */
    public SequenceExecutor(int state, Executor executor) {
        this.executor = executor;
        this.state = state;
        this.checkingStreamName = (String) getCheckingStreamNames().toArray()[0];
    }

    /**
     * @return whether the executor is alive
     */
    public boolean isAlive() {
        if (lifeTime == -1) {
            return true;
        } else if (expiringTime > System.currentTimeMillis()) {
            return true;
        }
        return false;
    }

    /**
     * initialize the arrivedEvents data structure
     */
    public void initArrivedEvents() {
        arrivedEvents = new Event[state + 1][];
        if (isStarExecutor) {
            arrivedEvents[state] = new Event[3];
        } else {
            arrivedEvents[state] = new Event[1];
        }
    }

    @Override
    public boolean execute(Event[] eventArray) {
        if (!isStarExecutor) {
            arrivedEvents[state][0] = eventArray[0];
        } else {
            if (null == arrivedEvents[state][0]) {
                arrivedEvents[state][0] = eventArray[0];
            } else {
                if (null == arrivedEvents[state][1]) {
                    arrivedEvents[state][2] = arrivedEvents[state][0];
                } else {
                    arrivedEvents[state][2] = arrivedEvents[state][1];
                }
                arrivedEvents[state][1] = eventArray[0];
            }
        }
        return this.executor.execute(arrivedEvents);
    }

    @Override
    public boolean execute(Event event) {
        if (!isStarExecutor) {
            arrivedEvents[state][0] = event;
        } else {
            if (null == arrivedEvents[state][0]) {
                arrivedEvents[state][0] = event;
            } else {
                if (null == arrivedEvents[state][1]) {
                    arrivedEvents[state][2] = arrivedEvents[state][0];
                } else {
                    arrivedEvents[state][2] = arrivedEvents[state][1];
                }
                arrivedEvents[state][1] = event;
            }
        }
        return this.executor.execute(arrivedEvents);
    }

    @Override
    public boolean execute(Event[][] eventArray) {
        return false;
    }

    /**
     * @param sequenceExecutor
     */
    public void setNextExecutor(SequenceExecutor sequenceExecutor) {
        this.nextExecutor = sequenceExecutor;
    }

    /**
     * Store the arrived events in the next executor and
     * @return Next Executor
     */
    public SequenceExecutor getNextExecutor() {
        return nextExecutor;
    }

    /**
     * Check whether the next executor exists
     * @return  whether the next executor exists
     */
    public boolean isNextExecutorExist() {
        return nextExecutor != null;
    }

    /**
     *  Check whether to fire an event
     * @return whether to fire an event
     */
    public boolean isFireEvent() {
        return fireEvent;
    }

    /**
     *  Enable executor to fire an event
     */
    public void enableFireEvent() {
        fireEvent = true;
    }

    /**
     * get stored events
     * @return the arrivedEvents
     */
    public Event[][] getArrivedEvents() {
        return arrivedEvents;
    }

    /**
     * Store arrived events
     * @param incomingEvents events to be stored
     */
    public void setArrivedEvents(Event[][] incomingEvents) {
        initArrivedEvents();

        for (int i=0; i < incomingEvents.length; i++) {
                arrivedEvents[i]=new Event[incomingEvents[i].length];
            for (int j=0; j< incomingEvents[i].length; j++) {
                arrivedEvents[i][j] = incomingEvents[i][j];
            }
        }
    }

    /**
     * @return checking stream names
     */
    public Set<String> getCheckingStreamNames() {
        return new HashSet<String>(executor.getCheckingStreamNames());
    }

    /**
     * @return checking stream name
     */
    public String getCheckingStreamName() {
        return checkingStreamName;
    }

    /**
     * Check whether the executor is a star executor
     * @return whether the executor is a star executor
     */
    public boolean isStarExecutor() {
        return isStarExecutor;
    }

    /**
     * Make executor a star executor
     * @param starExecutor
     */
    public void setStarExecutor(boolean starExecutor) {
        isStarExecutor = starExecutor;
    }

    /**
     * Get new instance of the Pattern Executor
     * @return a new instance of the Pattern Executor
     */
    public SequenceExecutor getNewInstance() {
        SequenceExecutor sequenceExecutor = null;
        sequenceExecutor = new SequenceExecutor(state, executor, checkingStreamName);

        if (isStarExecutor) {
            sequenceExecutor.isStarExecutor=(true);
        }

        if (isFireEvent()) {
            sequenceExecutor.enableFireEvent();
        }

        if (isNextExecutorExist()) {
            sequenceExecutor.nextExecutor=(nextExecutor);
        }

        sequenceExecutor.initArrivedEvents();

        return sequenceExecutor;
    }

    /**
     *
     * @return a new instance of the same executor
     */
    public SequenceExecutor getNextThisExecutor() {
        SequenceExecutor sequenceExecutor = null;
        sequenceExecutor = new SequenceExecutor(state, executor, checkingStreamName);

        if (isStarExecutor) {
            sequenceExecutor.isStarExecutor=(true);
        }

        if (fireEvent) {
            sequenceExecutor.enableFireEvent();
        }

        if (isNextExecutorExist()) {
            sequenceExecutor.nextExecutor=(nextExecutor);
        }
        sequenceExecutor.setArrivedEvents(arrivedEvents);

        return sequenceExecutor;
    }

    /**
     * @return as instance of next executor
     */
    public SequenceExecutor getNextNewExecutor() {
        SequenceExecutor sequenceExecutor = null;
        if (nextExecutor != null) {
            sequenceExecutor = new SequenceExecutor(nextExecutor.state, nextExecutor.executor, nextExecutor.checkingStreamName);

            if (nextExecutor.isLifeTimeSet()) {
                sequenceExecutor.lifeTime=(nextExecutor.lifeTime);
                sequenceExecutor.expiringTime=(System.currentTimeMillis());
            }

            if (nextExecutor.isStarExecutor) {
                sequenceExecutor.isStarExecutor=(true);
            }

            if (nextExecutor.fireEvent) {
                sequenceExecutor.enableFireEvent();
            }

            if (nextExecutor.isNextExecutorExist()) {
                sequenceExecutor.nextExecutor=(nextExecutor.nextExecutor);
            }

            sequenceExecutor.setArrivedEvents(arrivedEvents);
        }

        return sequenceExecutor;
    }

}
