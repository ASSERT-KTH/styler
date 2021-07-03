/*
*  Copyright (c) 2005-2012, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.siddhi.core.persistence;

import org.wso2.siddhi.core.config.SiddhiContext;
import org.wso2.siddhi.core.event.management.PersistenceManagementEvent;

import java.util.ArrayList;
import java.util.List;

public class PersistenceService {

    long nodeCounter = 0;
    String executionPlanIdentifier;
    List<Persister> persisterList = new ArrayList<Persister>();
    PersistenceStore persistenceStore;
    PersistenceManagementEvent lastPersistEvent;

    public PersistenceService(
            SiddhiContext siddhiContext) {
        executionPlanIdentifier = siddhiContext.getExecutionPlanIdentifier();
    }

    public void addPersister(Persister persister) {
        persister.setNodeId(nodeCounter + "");
        nodeCounter++;
        persister.setPersistenceStore(persistenceStore);
        persisterList.add(persister);
    }

    public String persist() {
        PersistenceManagementEvent persistEvent = new PersistenceManagementEvent(System.currentTimeMillis(), executionPlanIdentifier);

        for (Persister persister : persisterList) {
            persister.save(persistEvent);
        }
        lastPersistEvent = persistEvent;
        return persistEvent.getRevision();
    }

    public void restoreRevision(String revision) {
        PersistenceManagementEvent persistEvent = new PersistenceManagementEvent(revision);
        for (Persister persister : persisterList) {
            persister.load(persistEvent);
        }
    }

    public PersistenceStore getPersistenceStore() {
        return persistenceStore;
    }

    public void setPersistenceStore(PersistenceStore persistenceStore) {
        this.persistenceStore = persistenceStore;
    }


    public void restoreLastRevision() {
        String revision = persistenceStore.getLastRevision();
        if (revision != null) {
            restoreRevision(revision);
        }
    }
}
