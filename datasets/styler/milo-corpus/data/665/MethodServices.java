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

package org.eclipse.milo.opcua.sdk.server.services;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.eclipse.milo.opcua.sdk.server.DiagnosticsContext;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.Session;
import org.eclipse.milo.opcua.sdk.server.api.MethodServices.CallContext;
import org.eclipse.milo.opcua.sdk.server.api.Namespace;
import org.eclipse.milo.opcua.sdk.server.util.PendingCall;
import org.eclipse.milo.opcua.stack.core.application.services.MethodServiceSet;
import org.eclipse.milo.opcua.stack.core.application.services.ServiceRequest;
import org.eclipse.milo.opcua.stack.core.types.builtin.DiagnosticInfo;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;
import org.eclipse.milo.opcua.stack.core.types.structured.CallMethodRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.CallMethodResult;
import org.eclipse.milo.opcua.stack.core.types.structured.CallRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.CallResponse;
import org.eclipse.milo.opcua.stack.core.types.structured.ResponseHeader;
import org.eclipse.milo.opcua.stack.core.util.FutureUtils;

import static org.eclipse.milo.opcua.stack.core.util.ConversionUtil.a;
import static org.eclipse.milo.opcua.stack.core.util.ConversionUtil.l;

public class MethodServices implements MethodServiceSet {

    private final ServiceMetric callCounter = new ServiceMetric();

    @Override
    public void onCall(ServiceRequest<CallRequest, CallResponse> service) {
        callCounter.record(service);

        DiagnosticsContext<CallMethodRequest> diagnosticsContext = new DiagnosticsContext<>();

        OpcUaServer server = service.attr(ServiceAttributes.SERVER_KEY).get();
        Session session = service.attr(ServiceAttributes.SESSION_KEY).get();

        CallRequest request = service.getRequest();

        List<PendingCall> pendingCalls = l(request.getMethodsToCall())
            .stream()
            .map(PendingCall::new)
            .collect(Collectors.toList());

        // Group by namespace and call asynchronously for each.

        Map<UShort, List<PendingCall>> byNamespace = pendingCalls.stream()
            .collect(Collectors.groupingBy(pending -> pending.getInput().getMethodId().getNamespaceIndex()));

        byNamespace.keySet().forEach(index -> {
            List<PendingCall> pending = byNamespace.get(index);

            List<CallMethodRequest> requests = pending.stream()
                .map(PendingCall::getInput)
                .collect(Collectors.toList());

            Namespace namespace = server.getNamespaceManager().getNamespace(index);

            CompletableFuture<List<CallMethodResult>> future = new CompletableFuture<>();

            CallContext context = new CallContext(
                server, session, future, diagnosticsContext);

            server.getExecutorService().execute(() -> namespace.call(context, requests));

            future.thenAccept(values -> {
                for (int i = 0; i < values.size(); i++) {
                    pending.get(i).getFuture().complete(values.get(i));
                }
            });
        });

        // When all PendingCalls have been completed send a CallResponse with the values.

        List<CompletableFuture<CallMethodResult>> futures = pendingCalls.stream()
            .map(PendingCall::getFuture)
            .collect(Collectors.toList());

        FutureUtils.sequence(futures).thenAcceptAsync(values -> {
            ResponseHeader header = service.createResponseHeader();
            CallResponse response = new CallResponse(
                header, a(values, CallMethodResult.class), new DiagnosticInfo[0]);

            service.setResponse(response);
        }, server.getExecutorService());
    }

}
