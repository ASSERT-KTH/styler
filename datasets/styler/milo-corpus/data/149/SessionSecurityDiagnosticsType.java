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

package org.eclipse.milo.opcua.sdk.client.api.model.types.variables;

import java.util.concurrent.CompletableFuture;

import org.eclipse.milo.opcua.stack.core.types.builtin.ByteString;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MessageSecurityMode;


public interface SessionSecurityDiagnosticsType extends BaseDataVariableType {


    CompletableFuture<? extends BaseDataVariableType> sessionId();

    CompletableFuture<NodeId> getSessionId();

    CompletableFuture<StatusCode> setSessionId(NodeId value);

    CompletableFuture<? extends BaseDataVariableType> clientUserIdOfSession();

    CompletableFuture<String> getClientUserIdOfSession();

    CompletableFuture<StatusCode> setClientUserIdOfSession(String value);

    CompletableFuture<? extends BaseDataVariableType> clientUserIdHistory();

    CompletableFuture<String[]> getClientUserIdHistory();

    CompletableFuture<StatusCode> setClientUserIdHistory(String[] value);

    CompletableFuture<? extends BaseDataVariableType> authenticationMechanism();

    CompletableFuture<String> getAuthenticationMechanism();

    CompletableFuture<StatusCode> setAuthenticationMechanism(String value);

    CompletableFuture<? extends BaseDataVariableType> encoding();

    CompletableFuture<String> getEncoding();

    CompletableFuture<StatusCode> setEncoding(String value);

    CompletableFuture<? extends BaseDataVariableType> transportProtocol();

    CompletableFuture<String> getTransportProtocol();

    CompletableFuture<StatusCode> setTransportProtocol(String value);

    CompletableFuture<? extends BaseDataVariableType> securityMode();

    CompletableFuture<MessageSecurityMode> getSecurityMode();

    CompletableFuture<StatusCode> setSecurityMode(MessageSecurityMode value);

    CompletableFuture<? extends BaseDataVariableType> securityPolicyUri();

    CompletableFuture<String> getSecurityPolicyUri();

    CompletableFuture<StatusCode> setSecurityPolicyUri(String value);

    CompletableFuture<? extends BaseDataVariableType> clientCertificate();

    CompletableFuture<ByteString> getClientCertificate();

    CompletableFuture<StatusCode> setClientCertificate(ByteString value);


}