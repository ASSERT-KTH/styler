/*
 * Copyright (c) 2016 Kevin Herron
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 *
 * The Eclipse Public License is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *   http://www.eclipse.org/org/documents/edl-v10.html.
 */

package org.eclipse.milo.opcua.stack.core.application.services;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import org.eclipse.milo.opcua.stack.core.serialization.UaRequestMessage;
import org.eclipse.milo.opcua.stack.core.serialization.UaResponseMessage;
import org.eclipse.milo.opcua.stack.core.types.structured.ServiceFault;

public class ServiceResponse {

    private final UaRequestMessage request;
    private final UaResponseMessage response;
    private final long requestId;
    private final boolean serviceFault;

    public ServiceResponse(UaRequestMessage request, long requestId, UaResponseMessage response) {
        this.request = request;
        this.requestId = requestId;
        this.response = response;
        this.serviceFault = false;
    }

    public ServiceResponse(UaRequestMessage request, long requestId, ServiceFault serviceFault) {
        this.request = request;
        this.requestId = requestId;
        this.response = serviceFault;
        this.serviceFault = true;
    }

    public UaRequestMessage getRequest() {
        return request;
    }

    public long getRequestId() {
        return requestId;
    }

    public UaResponseMessage getResponse() {
        return response;
    }

    public boolean isServiceFault() {
        return serviceFault;
    }

    @Override
    public String toString() {
        ToStringHelper helper = MoreObjects.toStringHelper(this)
            .add("requestId", requestId)
            .add("request", request.getClass().getSimpleName())
            .add("response", response.getClass().getSimpleName());

        if (serviceFault) {
            helper.add("result", response.getResponseHeader().getServiceResult());
        }

        return helper.toString();
    }

}
