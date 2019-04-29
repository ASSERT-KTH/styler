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

package org.eclipse.milo.opcua.sdk.client.api.model.types.objects;

import java.util.concurrent.CompletableFuture;

import org.eclipse.milo.opcua.sdk.client.api.model.types.variables.StateVariableType;
import org.eclipse.milo.opcua.sdk.client.api.model.types.variables.TransitionVariableType;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;


public interface TransitionEventType extends BaseEventType {


    CompletableFuture<? extends TransitionVariableType> transition();

    CompletableFuture<LocalizedText> getTransition();

    CompletableFuture<StatusCode> setTransition(LocalizedText value);

    CompletableFuture<? extends StateVariableType> fromState();

    CompletableFuture<LocalizedText> getFromState();

    CompletableFuture<StatusCode> setFromState(LocalizedText value);

    CompletableFuture<? extends StateVariableType> toState();

    CompletableFuture<LocalizedText> getToState();

    CompletableFuture<StatusCode> setToState(LocalizedText value);

}