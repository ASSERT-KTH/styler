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

package com.griddynamics.jagger.engine.e1.scenario;

import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.user.ProcessingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * User: dkotlyarov
 */
public class UserWorkload {
    private static final Logger log = LoggerFactory.getLogger(UserWorkload.class);

    private final boolean main;
    private final UserClock clock;
    ArrayList<UserGroup> groups;
    private final int delay;

    public UserWorkload(boolean main, UserClock clock, List<ProcessingConfig.Test.Task.User> users, int delay, long time) {
        this.main = main;
        this.clock = clock;
        this.groups = new ArrayList<UserGroup>(users.size());
        this.delay = delay;

        for (ProcessingConfig.Test.Task.User userConfig : users) {
            groups.add(new UserGroup(this.getClock(), groups.size(), userConfig, time));
        }
    }

    public boolean isMain() {
        return main;
    }

    public UserClock getClock() {
        return clock;
    }

    public int getDelay() {
        return delay;
    }

    public int getTotalUserCount() {
        int totalUserCount = 0;
        for (UserGroup group : groups) {
            totalUserCount += group.getCount();
        }
        return totalUserCount;
    }

    public int getStartedUserCount() {
        int totalStartedCount = 0;
        for (UserGroup group : groups) {
            totalStartedCount += group.getStartedUserCount();
        }
        return totalStartedCount;
    }

    public int getActiveUserCount() {
        int activeUserCount = 0;
        for (UserGroup group : groups) {
            activeUserCount += group.getActiveUserCount();
        }
        return activeUserCount;
    }

    public void tick(long time, LinkedHashMap<NodeId, WorkloadConfiguration> workloadConfigurations) {
        for (Map.Entry<NodeId, WorkloadConfiguration> entry : new ArrayList<Map.Entry<NodeId, WorkloadConfiguration>>(workloadConfigurations.entrySet())) {
            if (entry.getValue().getDelay() != delay) {
                workloadConfigurations.put(entry.getKey(), WorkloadConfiguration.with(entry.getValue().getThreads(), delay));
            }
        }

        for (UserGroup group : groups) {
            group.tick(time, workloadConfigurations);
        }
    }
}
