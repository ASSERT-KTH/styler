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

package org.eclipse.milo.opcua.sdk.server.items;

import org.eclipse.milo.opcua.sdk.server.api.DataItem;
import org.eclipse.milo.opcua.sdk.server.util.DataChangeMonitoringFilter;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.StatusCodes;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExtensionObject;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.DataChangeTrigger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.DeadbandType;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.AggregateFilter;
import org.eclipse.milo.opcua.stack.core.types.structured.DataChangeFilter;
import org.eclipse.milo.opcua.stack.core.types.structured.EventFilter;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoredItemNotification;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoringFilter;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

public class MonitoredDataItem extends BaseMonitoredItem<DataValue> implements DataItem {

    private static final DataChangeFilter DefaultFilter = new DataChangeFilter(
        DataChangeTrigger.StatusValue,
        uint(DeadbandType.None.getValue()),
        0.0
    );

    private volatile DataValue lastValue = null;
    private volatile DataChangeFilter filter = null;
    private volatile ExtensionObject filterResult = null;

    public MonitoredDataItem(
        UInteger id,
        UInteger subscriptionId,
        ReadValueId readValueId,
        MonitoringMode monitoringMode,
        TimestampsToReturn timestamps,
        UInteger clientHandle,
        double samplingInterval,
        ExtensionObject filter,
        UInteger queueSize,
        boolean discardOldest) throws UaException {

        super(id, subscriptionId, readValueId, monitoringMode,
            timestamps, clientHandle, samplingInterval, queueSize, discardOldest);

        installFilter(filter);
    }

    @Override
    public synchronized void setValue(DataValue value) {
        boolean valuePassesFilter = DataChangeMonitoringFilter.filter(lastValue, value, filter);

        if (valuePassesFilter) {
            lastValue = value;

            enqueue(value);

            if (triggeredItems != null) {
                triggeredItems.values().forEach(item -> item.triggered = true);
            }
        }
    }

    @Override
    protected void enqueue(DataValue value) {
        if (queue.size() < queue.maxSize()) {
            queue.add(value);
        } else {
            if (getQueueSize() > 1) {
                /* Set overflow if queueSize > 1... */
                value = value.withStatus(value.getStatusCode().withOverflow());
            } else if (value.getStatusCode().isOverflowSet()) {
                /* But make sure it's clear otherwise. */
                value = value.withStatus(value.getStatusCode().withoutOverflow());
            }

            if (discardOldest) {
                queue.add(value);
            } else {
                queue.set(queue.maxSize() - 1, value);
            }
        }
    }

    @Override
    public synchronized void setQuality(StatusCode quality) {
        if (lastValue == null) {
            setValue(new DataValue(Variant.NULL_VALUE, quality, DateTime.now(), DateTime.now()));
        } else {
            DataValue value = new DataValue(
                lastValue.getValue(),
                quality,
                DateTime.now(),
                DateTime.now());

            setValue(value);
        }
    }

    @Override
    public boolean isSamplingEnabled() {
        return getMonitoringMode() != MonitoringMode.Disabled;
    }

    @Override
    public synchronized void setMonitoringMode(MonitoringMode monitoringMode) {
        if (monitoringMode == MonitoringMode.Disabled) {
            lastValue = null;
        }

        super.setMonitoringMode(monitoringMode);
    }

    public synchronized void clearLastValue() {
        lastValue = null;
    }

    @Override
    protected void installFilter(ExtensionObject filterXo) throws UaException {
        if (filterXo == null || filterXo.decode() == null) {
            this.filter = DefaultFilter;
        } else {
            Object filterObject = filterXo.decode();

            if (filterObject instanceof MonitoringFilter) {
                if (filterObject instanceof DataChangeFilter) {
                    this.filter = ((DataChangeFilter) filterObject);

                    DeadbandType deadbandType = DeadbandType.from(filter.getDeadbandType().intValue());

                    if (deadbandType == null) {
                        throw new UaException(StatusCodes.Bad_DeadbandFilterInvalid);
                    }

                    if (deadbandType != DeadbandType.None &&
                        AttributeId.Value.isEqual(getReadValueId().getAttributeId())) {
                        throw new UaException(StatusCodes.Bad_FilterNotAllowed);
                    }
                } else if (filterObject instanceof AggregateFilter) {
                    throw new UaException(StatusCodes.Bad_MonitoredItemFilterUnsupported);
                } else if (filterObject instanceof EventFilter) {
                    throw new UaException(StatusCodes.Bad_FilterNotAllowed);
                }
            } else {
                throw new UaException(StatusCodes.Bad_MonitoredItemFilterInvalid);
            }
        }
    }

    @Override
    public ExtensionObject getFilterResult() {
        return filterResult;
    }

    @Override
    protected MonitoredItemNotification wrapQueueValue(DataValue value) {
        value = DataValue.derivedValue(value, timestamps);

        return new MonitoredItemNotification(uint(getClientHandle()), value);
    }

}
