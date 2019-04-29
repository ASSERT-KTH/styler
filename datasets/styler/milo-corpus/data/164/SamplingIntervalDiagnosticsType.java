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

import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;


public interface SamplingIntervalDiagnosticsType extends BaseDataVariableType {


    CompletableFuture<? extends BaseDataVariableType> samplingInterval();

    CompletableFuture<Double> getSamplingInterval();

    CompletableFuture<StatusCode> setSamplingInterval(Double value);

    CompletableFuture<? extends BaseDataVariableType> sampledMonitoredItemsCount();

    CompletableFuture<UInteger> getSampledMonitoredItemsCount();

    CompletableFuture<StatusCode> setSampledMonitoredItemsCount(UInteger value);

    CompletableFuture<? extends BaseDataVariableType> maxSampledMonitoredItemsCount();

    CompletableFuture<UInteger> getMaxSampledMonitoredItemsCount();

    CompletableFuture<StatusCode> setMaxSampledMonitoredItemsCount(UInteger value);

    CompletableFuture<? extends BaseDataVariableType> disabledMonitoredItemsSamplingCount();

    CompletableFuture<UInteger> getDisabledMonitoredItemsSamplingCount();

    CompletableFuture<StatusCode> setDisabledMonitoredItemsSamplingCount(UInteger value);


}