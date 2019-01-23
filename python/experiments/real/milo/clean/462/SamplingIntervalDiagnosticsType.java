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

package org.eclipse.milo.opcua.sdk.server.model.types.variables;

import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;


public interface SamplingIntervalDiagnosticsType extends BaseDataVariableType {


    Double getSamplingInterval();

    BaseDataVariableType getSamplingIntervalNode();

    void setSamplingInterval(Double value);

    UInteger getSampledMonitoredItemsCount();

    BaseDataVariableType getSampledMonitoredItemsCountNode();

    void setSampledMonitoredItemsCount(UInteger value);

    UInteger getMaxSampledMonitoredItemsCount();

    BaseDataVariableType getMaxSampledMonitoredItemsCountNode();

    void setMaxSampledMonitoredItemsCount(UInteger value);

    UInteger getDisabledMonitoredItemsSamplingCount();

    BaseDataVariableType getDisabledMonitoredItemsSamplingCountNode();

    void setDisabledMonitoredItemsSamplingCount(UInteger value);
}
