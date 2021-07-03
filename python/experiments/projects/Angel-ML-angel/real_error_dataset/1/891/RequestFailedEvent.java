/*
 * Tencent is pleased to support the open source community by making Angel available.
 *
 * Copyright (C) 2017 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.tencent.angel.psagent.matrix.transport;

import com.tencent.angel.ml.matrix.transport.Request;
import com.tencent.angel.ml.matrix.transport.ResponseType;

public class RequestFailedEvent extends RequestDispatchEvent {

  private final ResponseType failedType;
  private final String failedLog;
  /**
   * Create a new RequestDispatchEvent.
   *
   * @param type    event type
   * @param request rpc request
   */
  public RequestFailedEvent(EventType type, Request request, ResponseType failedType, String failedLog) {
    super(type, request);
    this.failedType = failedType;
    this.failedLog = failedLog;
  }

  public ResponseType getFailedType() {
    return failedType;
  }

  public String getFailedLog() {
    return failedLog;
  }
}
