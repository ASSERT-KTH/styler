/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.servicecomb.serviceregistry.consumer;

import java.util.Map;

import org.apache.servicecomb.foundation.common.concurrent.ConcurrentHashMapEx;
import org.apache.servicecomb.foundation.common.event.EventManager;
import org.apache.servicecomb.serviceregistry.api.response.MicroserviceInstanceChangedEvent;
import org.apache.servicecomb.serviceregistry.task.event.SafeModeChangeEvent;

import com.google.common.eventbus.EventBus;

public class AppManager {
  // key: appId
  private Map<String, MicroserviceManager> apps = new ConcurrentHashMapEx<>();

  public AppManager() {
    getEventBus().register(this);
  }

  public EventBus getEventBus() {
    return EventManager.getEventBus();
  }

  public Map<String, MicroserviceManager> getApps() {
    return apps;
  }

  // microserviceName maybe normal name or alias name
  public MicroserviceVersionRule getOrCreateMicroserviceVersionRule(String appId, String microserviceName,
      String versionRule) {
    MicroserviceManager microserviceManager = getOrCreateMicroserviceManager(appId);

    return microserviceManager.getOrCreateMicroserviceVersionRule(microserviceName, versionRule);
  }

  public MicroserviceManager getOrCreateMicroserviceManager(String appId) {
    return apps.computeIfAbsent(appId, id -> new MicroserviceManager(this, appId));
  }

  public MicroserviceVersions getOrCreateMicroserviceVersions(String appId, String microserviceName) {
    MicroserviceManager microserviceManager = getOrCreateMicroserviceManager(appId);
    return microserviceManager.getOrCreateMicroserviceVersions(microserviceName);
  }

  public void onMicroserviceInstanceChanged(MicroserviceInstanceChangedEvent changedEvent) {
    MicroserviceManager microserviceManager = apps.get(changedEvent.getKey().getAppId());
    if (microserviceManager == null) {
      return;
    }

    microserviceManager.onMicroserviceInstanceChanged(changedEvent);
  }

  public void onSafeModeChanged(SafeModeChangeEvent modeChangeEvent) {
    apps.values().forEach(microserviceManager -> microserviceManager.onSafeModeChanged(modeChangeEvent));
  }

  public void pullInstances() {
    for (MicroserviceManager microserviceManager : apps.values()) {
      microserviceManager.pullInstances();
    }
  }
}