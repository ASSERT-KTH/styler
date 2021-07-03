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
package org.apache.servicecomb.serviceregistry.task;

import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.servicecomb.foundation.common.base.ServiceCombConstants;
import org.apache.servicecomb.serviceregistry.api.registry.Microservice;
import org.apache.servicecomb.serviceregistry.api.response.GetSchemaResponse;
import org.apache.servicecomb.serviceregistry.client.ServiceRegistryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.google.common.base.Charsets;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.hash.Hashing;

public class MicroserviceRegisterTask extends AbstractRegisterTask {
  private static final Logger LOGGER = LoggerFactory.getLogger(MicroserviceRegisterTask.class);

  private boolean schemaIdSetMatch;

  public MicroserviceRegisterTask(EventBus eventBus, ServiceRegistryClient srClient, Microservice microservice) {
    super(eventBus, srClient, microservice);
    this.taskStatus = TaskStatus.READY;
  }

  public boolean isSchemaIdSetMatch() {
    return schemaIdSetMatch;
  }

  @Subscribe
  public void onMicroserviceInstanceHeartbeatTask(MicroserviceInstanceHeartbeatTask task) {
    if (task.getHeartbeatResult() != HeartbeatResult.SUCCESS && isSameMicroservice(task.getMicroservice())) {
      LOGGER.info("read MicroserviceInstanceHeartbeatTask status is {}", task.taskStatus);
      this.taskStatus = TaskStatus.READY;
      this.registered = false;
    }
  }

  @Subscribe
  public void onInstanceRegistryFailed(MicroserviceInstanceRegisterTask task) {
    if (task.taskStatus != TaskStatus.FINISHED) {
      LOGGER.info("read MicroserviceInstanceRegisterTask status is {}", task.taskStatus);
      this.taskStatus = TaskStatus.READY;
      this.registered = false;
    }
  }

  @Override
  protected boolean doRegister() {
    LOGGER.info("running microservice register task.");
    String serviceId = srClient.getMicroserviceId(microservice.getAppId(),
        microservice.getServiceName(),
        microservice.getVersion(),
        microservice.getEnvironment());
    if (!StringUtils.isEmpty(serviceId)) {
      // 已经注册过了，不需要重新注册
      microservice.setServiceId(serviceId);
      LOGGER.info(
          "Microservice exists in service center, no need to register. id={} appId={}, name={}, version={}",
          serviceId,
          microservice.getAppId(),
          microservice.getServiceName(),
          microservice.getVersion());

      if (!checkSchemaIdSet()) {
        return false;
      }
    } else {
      serviceId = srClient.registerMicroservice(microservice);
      if (StringUtils.isEmpty(serviceId)) {
        LOGGER.error(
            "Registry microservice failed. appId={}, name={}, version={}",
            microservice.getAppId(),
            microservice.getServiceName(),
            microservice.getVersion());
        return false;
      }

      schemaIdSetMatch = true;
      // 重新注册服务场景下，instanceId不应该缓存
      microservice.getInstance().setInstanceId(null);

      LOGGER.info(
          "Registry Microservice successfully. id={} appId={}, name={}, version={}, schemaIds={}",
          serviceId,
          microservice.getAppId(),
          microservice.getServiceName(),
          microservice.getVersion(),
          microservice.getSchemas());
    }

    microservice.setServiceId(serviceId);
    microservice.getInstance().setServiceId(microservice.getServiceId());

    return registerSchemas();
  }

  private boolean checkSchemaIdSet() {
    Microservice existMicroservice = srClient.getMicroservice(microservice.getServiceId());
    if (existMicroservice == null) {
      LOGGER.error("Error to get microservice from service center when check schema set");
      return false;
    }
    Set<String> existSchemas = new HashSet<>(existMicroservice.getSchemas());
    Set<String> localSchemas = new HashSet<>(microservice.getSchemas());
    schemaIdSetMatch = existSchemas.equals(localSchemas);

    if (!schemaIdSetMatch) {
      LOGGER.error(
          "SchemaIds is different between local and service center. Please change microservice version. "
              + "id={} appId={}, name={}, version={}, local schemaIds={}, service center schemaIds={}",
          microservice.getServiceId(),
          microservice.getAppId(),
          microservice.getServiceName(),
          microservice.getVersion(),
          localSchemas,
          existSchemas);
      return true;
    }

    LOGGER.info(
        "SchemaIds is equals to service center. id={} appId={}, name={}, version={}, schemaIds={}",
        microservice.getServiceId(),
        microservice.getAppId(),
        microservice.getServiceName(),
        microservice.getVersion(),
        localSchemas);
    return true;
  }

  private boolean registerSchemas() {
    List<GetSchemaResponse> existSchemas = srClient.getSchemas(microservice.getServiceId());
    for (Entry<String, String> entry : microservice.getSchemaMap().entrySet()) {
      String schemaId = entry.getKey();
      String content = entry.getValue();
      GetSchemaResponse existSchema = extractSchema(schemaId, existSchemas);
      boolean exists = existSchema != null && existSchema.getSummary() != null;
      LOGGER.info("schemaId [{}] exists {}", schemaId, exists);
      if (!exists) {
        if (!srClient.registerSchema(microservice.getServiceId(), schemaId, content)) {
          return false;
        }
      } else {
        String curSchemaSummary = existSchema.getSummary();
        String schemaSummary = Hashing.sha256().newHasher().putString(content, Charsets.UTF_8).hash().toString();
        if (!schemaSummary.equals(curSchemaSummary)) {
          if (microservice.getEnvironment().equalsIgnoreCase(ServiceCombConstants.DEVELOPMENT_SERVICECOMB_ENV)) {
            LOGGER.info(
                "schemaId [{}]'s content changes and the current environment is {}, so re-register it!",
                schemaId, ServiceCombConstants.DEVELOPMENT_SERVICECOMB_ENV);
            if (!srClient.registerSchema(microservice.getServiceId(), schemaId, content)) {
              return false;
            }
          } else {
            throw new IllegalStateException("schemaId [" + schemaId
                + "] exists in service center, but the content does not match the local content that means there are interface change "
                + "and you need to increment microservice version before deploying. "
                + "Or you can configure service_description.environment="
                + ServiceCombConstants.DEVELOPMENT_SERVICECOMB_ENV
                + " to work in development environment and ignore this error");
          }
        }
      }
    }
    return true;
  }

  private GetSchemaResponse extractSchema(String schemaId, List<GetSchemaResponse> schemas) {
    if (schemas == null || schemas.isEmpty()) {
      return null;
    }
    GetSchemaResponse schema = null;
    for (GetSchemaResponse tempSchema : schemas) {
      if (tempSchema.getSchemaId().equals(schemaId)) {
        schema = tempSchema;
        break;
      }
    }
    return schema;
  }
}
