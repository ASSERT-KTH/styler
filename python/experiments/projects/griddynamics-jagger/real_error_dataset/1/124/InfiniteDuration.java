/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.griddynamics.jagger.monitoring;

import com.griddynamics.jagger.coordinator.NodeId;

import java.util.Map;

/**
 * Infinite duration of monitoring. Should be used <strong>only</strong> as attendant process in {@link com.griddynamics.jagger.master.CompositeTask}.
 *
 * @author Mairbek Khadikov
 */
public class InfiniteDuration implements MonitoringTerminationStrategyConfiguration {
    @Override
    public MonitoringTerminationStrategy get() {
        return Impl.INSTANCE;
    }

    private static enum Impl implements MonitoringTerminationStrategy {
        INSTANCE;

        @Override
        public boolean isTerminationRequired(Map<NodeId, MonitoringStatus> statuses) {
            return false;
        }


        @Override
        public String toString() {
            return "infinite duration";
        }
    }

    @Override
    public String toString() {
        return "infinite duration";
    }

}
