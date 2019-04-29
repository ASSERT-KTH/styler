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

package org.eclipse.milo.opcua.sdk.server.api;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import org.eclipse.milo.opcua.sdk.server.DiagnosticsContext;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.Session;
import org.eclipse.milo.opcua.sdk.server.api.MethodInvocationHandler.NodeIdUnknownHandler;
import org.eclipse.milo.opcua.stack.core.StatusCodes;
import org.eclipse.milo.opcua.stack.core.types.builtin.DiagnosticInfo;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.structured.CallMethodRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.CallMethodResult;
import org.eclipse.milo.opcua.stack.core.util.FutureUtils;
import org.slf4j.LoggerFactory;

public interface MethodServices {

    /**
     * Invoke one or more methods belonging to this {@link MethodServices}.
     *
     * @param context  the {@link CallContext}.
     * @param requests The {@link CallMethodRequest}s for the methods to invoke.
     */
    default void call(CallContext context, List<CallMethodRequest> requests) {
        List<CompletableFuture<CallMethodResult>> results = Lists.newArrayListWithCapacity(requests.size());

        for (CallMethodRequest request : requests) {
            MethodInvocationHandler handler = getInvocationHandler(request.getMethodId())
                .orElse(new NodeIdUnknownHandler());

            CompletableFuture<CallMethodResult> resultFuture = new CompletableFuture<>();

            try {
                handler.invoke(request, resultFuture);
            } catch (Throwable t) {
                LoggerFactory.getLogger(getClass())
                    .error("Uncaught Throwable invoking method handler for methodId={}.", request.getMethodId(), t);

                resultFuture.complete(new CallMethodResult(
                    new StatusCode(StatusCodes.Bad_InternalError),
                    new StatusCode[0], new DiagnosticInfo[0], new Variant[0]
                ));
            }

            results.add(resultFuture);
        }

        FutureUtils.sequence(results).thenAccept(rs -> context.complete(rs));
    }

    /**
     * Get the {@link MethodInvocationHandler} for the method identified by {@code methodId}, if it exists.
     *
     * @param methodId the {@link NodeId} identifying the method.
     * @return the {@link MethodInvocationHandler} for {@code methodId}, if it exists.
     */
    default Optional<MethodInvocationHandler> getInvocationHandler(NodeId methodId) {
        return Optional.empty();
    }

    final class CallContext extends OperationContext<CallMethodRequest, CallMethodResult> {
        public CallContext(OpcUaServer server,
                           @Nullable Session session,
                           DiagnosticsContext<CallMethodRequest> diagnosticsContext) {

            super(server, session, diagnosticsContext);
        }

        public CallContext(OpcUaServer server,
                           @Nullable Session session,
                           CompletableFuture<List<CallMethodResult>> future,
                           DiagnosticsContext<CallMethodRequest> diagnosticsContext) {

            super(server, session, future, diagnosticsContext);
        }
    }

}
