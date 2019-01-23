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

import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.structured.TestStackExRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.TestStackExResponse;
import org.eclipse.milo.opcua.stack.core.types.structured.TestStackRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.TestStackResponse;

import static org.eclipse.milo.opcua.stack.core.StatusCodes.Bad_ServiceUnsupported;

public interface TestServiceSet {

    default void onTestStack(
        ServiceRequest<TestStackRequest, TestStackResponse> serviceRequest) throws UaException {

        serviceRequest.setServiceFault(Bad_ServiceUnsupported);
    }

    default void onTestStackEx(
        ServiceRequest<TestStackExRequest, TestStackExResponse> serviceRequest) throws UaException {

        serviceRequest.setServiceFault(Bad_ServiceUnsupported);
    }

}
