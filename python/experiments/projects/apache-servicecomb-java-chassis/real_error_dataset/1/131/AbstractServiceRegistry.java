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
package io.servicecomb.serviceregistry.registry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

import io.servicecomb.serviceregistry.Features;
import io.servicecomb.serviceregistry.ServiceRegistry;
import io.servicecomb.serviceregistry.api.Const;
import io.servicecomb.serviceregistry.api.registry.BasePath;
import io.servicecomb.serviceregistry.api.registry.Microservice;
import io.servicecomb.serviceregistry.api.registry.MicroserviceFactory;
import io.servicecomb.serviceregistry.api.registry.MicroserviceInstance;
import io.servicecomb.serviceregistry.cache.InstanceCacheManager;
import io.servicecomb.serviceregistry.cache.InstanceCacheManagerNew;
import io.servicecomb.serviceregistry.cache.InstanceCacheManagerOld;
import io.servicecomb.serviceregistry.client.IpPortManager;
import io.servicecomb.serviceregistry.client.ServiceRegistryClient;
import io.servicecomb.serviceregistry.config.ServiceRegistryConfig;
import io.servicecomb.serviceregistry.consumer.AppManager;
import io.servicecomb.serviceregistry.consumer.MicroserviceVersionFactory;
import io.servicecomb.serviceregistry.definition.MicroserviceDefinition;
import io.servicecomb.serviceregistry.task.MicroserviceServiceCenterTask;
import io.servicecomb.serviceregistry.task.ServiceCenterTask;
import io.servicecomb.serviceregistry.task.event.ShutdownEvent;

public abstract class AbstractServiceRegistry implements ServiceRegistry {
  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractServiceRegistry.class);

  private Features features = new Features();

  private MicroserviceFactory microserviceFactory = new MicroserviceFactory();

  protected EventBus eventBus;

  protected MicroserviceDefinition microserviceDefinition;

  protected Microservice microservice;

  protected AppManager appManager;

  protected InstanceCacheManager instanceCacheManager;

  protected IpPortManager ipPortManager;

  protected ServiceRegistryClient srClient;

  protected ServiceRegistryConfig serviceRegistryConfig;

  protected ServiceCenterTask serviceCenterTask;

  public AbstractServiceRegistry(EventBus eventBus, ServiceRegistryConfig serviceRegistryConfig,
      MicroserviceDefinition microserviceDefinition) {
    this.eventBus = eventBus;
    this.serviceRegistryConfig = serviceRegistryConfig;
    this.microserviceDefinition = microserviceDefinition;
    this.microservice = microserviceFactory.create(microserviceDefinition);
  }

  @Override
  public void init() {
    try {
      initAppManager();
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      throw new IllegalStateException("Failed to init appManager.", e);
    }

    initCacheManager();

    ipPortManager = new IpPortManager(serviceRegistryConfig, instanceCacheManager);
    if (srClient == null) {
      srClient = createServiceRegistryClient();
    }

    createServiceCenterTask();

    eventBus.register(this);
  }

  protected void initAppManager() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    appManager = new AppManager(eventBus);

    // we did not remove old InstanceCacheManager now
	// microserviceVersionFactoryClass is null, means use old InstanceCacheManager
	// must not throw exception
    String microserviceVersionFactoryClass = serviceRegistryConfig.getMicroserviceVersionFactory();
    if (microserviceVersionFactoryClass == null) {
      return;
    }

    MicroserviceVersionFactory microserviceVersionFactory =
        (MicroserviceVersionFactory) Class.forName(microserviceVersionFactoryClass).newInstance();
    appManager.setMicroserviceVersionFactory(microserviceVersionFactory);
    LOGGER.info("microserviceVersionFactory is {}.", microserviceVersionFactoryClass);
  }

  protected void initCacheManager() {
    // now only edge use new mechanism
    String microserviceVersionFactoryClass = serviceRegistryConfig.getMicroserviceVersionFactory();
    if (microserviceVersionFactoryClass == null) {
      instanceCacheManager = new InstanceCacheManagerOld(eventBus, this, serviceRegistryConfig);
      return;
    }

    instanceCacheManager = new InstanceCacheManagerNew(appManager);
  }

  @Override
  public AppManager getAppManager() {
    return appManager;
  }

  @Override
  public Features getFeatures() {
    return features;
  }

  public EventBus getEventBus() {
    return eventBus;
  }

  @Override
  public Set<String> getCombinedMicroserviceNames() {
    return microserviceDefinition.getCombinedFrom();
  }

  @Override
  public ServiceRegistryClient getServiceRegistryClient() {
    return srClient;
  }

  public void setServiceRegistryClient(ServiceRegistryClient serviceRegistryClient) {
    this.srClient = serviceRegistryClient;
  }

  public IpPortManager getIpPortManager() {
    return ipPortManager;
  }

  @Override
  public InstanceCacheManager getInstanceCacheManager() {
    return instanceCacheManager;
  }

  protected abstract ServiceRegistryClient createServiceRegistryClient();

  @Override
  public void run() {
    loadStaticConfiguration();

    // try register
    // if failed, then retry in thread
    serviceCenterTask.init();
  }

  private void loadStaticConfiguration() {
    // TODO 如果yaml定义了paths规则属性，替换默认值，现需要DynamicPropertyFactory支持数组获取
    List<BasePath> paths = microservice.getPaths();
    for (BasePath path : paths) {
      if (path.getProperty() == null) {
        path.setProperty(new HashMap<>());
      }
      path.getProperty().put(Const.PATH_CHECKSESSION, "false");
    }
  }

  private void createServiceCenterTask() {
    MicroserviceServiceCenterTask task =
        new MicroserviceServiceCenterTask(eventBus, serviceRegistryConfig, srClient, microservice);
    serviceCenterTask = new ServiceCenterTask(eventBus, serviceRegistryConfig.getHeartbeatInterval(), task);
  }

  public boolean unregisterInstance() {
    MicroserviceInstance microserviceInstance = microservice.getInstance();
    boolean result = srClient.unregisterMicroserviceInstance(microserviceInstance.getServiceId(),
        microserviceInstance.getInstanceId());
    if (!result) {
      LOGGER.error("Unregister microservice instance failed. microserviceId={} instanceId={}",
          microserviceInstance.getServiceId(),
          microserviceInstance.getInstanceId());
      return false;
    }
    LOGGER.info("Unregister microservice instance success. microserviceId={} instanceId={}",
        microserviceInstance.getServiceId(),
        microserviceInstance.getInstanceId());
    return true;
  }

  public List<MicroserviceInstance> findServiceInstance(String appId, String serviceName,
      String versionRule) {
    List<MicroserviceInstance> instances = srClient.findServiceInstance(microservice.getServiceId(),
        appId,
        serviceName,
        versionRule);
    if (instances == null) {
      LOGGER.error("find empty instances from service center. service={}/{}/{}", appId, serviceName, versionRule);
      return null;
    }

    LOGGER.info("find instances[{}] from service center success. service={}/{}/{}",
        instances.size(),
        appId,
        serviceName,
        versionRule);
    for (MicroserviceInstance instance : instances) {
      LOGGER.info("service id={}, instance id={}, endpoints={}",
          instance.getServiceId(),
          instance.getInstanceId(),
          instance.getEndpoints());
    }
    return instances;
  }

  @Override
  public boolean updateMicroserviceProperties(Map<String, String> properties) {
    boolean success = srClient.updateMicroserviceProperties(microservice.getServiceId(),
        properties);
    if (success) {
      microservice.setProperties(properties);
    }
    return success;
  }

  public boolean updateInstanceProperties(Map<String, String> instanceProperties) {
    MicroserviceInstance microserviceInstance = microservice.getInstance();
    boolean success = srClient.updateInstanceProperties(microserviceInstance.getServiceId(),
        microserviceInstance.getInstanceId(),
        instanceProperties);
    if (success) {
      microserviceInstance.setProperties(instanceProperties);
    }
    return success;
  }

  public Microservice getRemoteMicroservice(String microserviceId) {
    return srClient.getMicroservice(microserviceId);
  }

  public Microservice getMicroservice() {
    return microservice;
  }

  public MicroserviceInstance getMicroserviceInstance() {
    return microservice.getInstance();
  }

  public void destroy() {
    eventBus.post(new ShutdownEvent());
    unregisterInstance();
  }
}
