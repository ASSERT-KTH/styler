/*
 * Copyright 2021 Apollo Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.ctrip.framework.apollo.portal.entity.vo;

import com.ctrip.framework.apollo.common.dto.ClusterDTO;
import com.ctrip.framework.apollo.portal.environment.Env;

import java.util.List;

public class EnvClusterInfo {
  private String env;
  private List<ClusterDTO> clusters;

  public EnvClusterInfo(Env env) {
    this.env = env.toString();
  }

  public Env getEnv() {
    return Env.valueOf(env);
  }

  public void setEnv(Env env) {
    this.env = env.toString();
  }

  public List<ClusterDTO> getClusters() {
    return clusters;
  }

  public void setClusters(List<ClusterDTO> clusters) {
    this.clusters = clusters;
  }

}
