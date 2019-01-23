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

import org.eclipse.milo.opcua.sdk.client.api.model.types.variables.TwoStateVariableType;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;


public interface AcknowledgeableConditionType extends ConditionType {


    CompletableFuture<? extends TwoStateVariableType> enabledState();

    CompletableFuture<LocalizedText> getEnabledState();

    CompletableFuture<StatusCode> setEnabledState(LocalizedText value);

    CompletableFuture<? extends TwoStateVariableType> ackedState();

    CompletableFuture<LocalizedText> getAckedState();

    CompletableFuture<StatusCode> setAckedState(LocalizedText value);

    CompletableFuture<? extends TwoStateVariableType> confirmedState();

    CompletableFuture<LocalizedText> getConfirmedState();

    CompletableFuture<StatusCode> setConfirmedState(LocalizedText value);

}