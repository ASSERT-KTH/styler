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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.primitives.Ints;
import org.eclipse.milo.opcua.sdk.server.api.MonitoredItem;
import org.eclipse.milo.opcua.sdk.server.util.RingBuffer;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.serialization.UaStructure;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExtensionObject;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;

public abstract class BaseMonitoredItem<T> implements MonitoredItem {

    private static final int MAX_QUEUE_SIZE = 0xFFFF;

    protected volatile Map<UInteger, BaseMonitoredItem<?>> triggeredItems;
    protected volatile boolean triggered = false;

    protected volatile RingBuffer<T> queue;

    protected volatile long clientHandle;
    protected volatile int queueSize;
    protected volatile double samplingInterval;
    protected volatile boolean discardOldest;

    protected final UInteger id;
    protected final UInteger subscriptionId;
    protected final ReadValueId readValueId;
    protected volatile MonitoringMode monitoringMode;
    protected volatile TimestampsToReturn timestamps;

    protected BaseMonitoredItem(
        UInteger id,
        UInteger subscriptionId,
        ReadValueId readValueId,
        MonitoringMode monitoringMode,
        TimestampsToReturn timestamps,
        UInteger clientHandle,
        double samplingInterval,
        UInteger queueSize,
        boolean discardOldest) {

        this.id = id;
        this.subscriptionId = subscriptionId;
        this.readValueId = readValueId;
        this.monitoringMode = monitoringMode;
        this.timestamps = timestamps;
        this.clientHandle = clientHandle.longValue();
        this.samplingInterval = samplingInterval;
        this.discardOldest = discardOldest;

        setQueueSize(queueSize);

        queue = new RingBuffer<>(this.queueSize);
    }

    protected void setQueueSize(UInteger queueSize) {
        int qs = Ints.saturatedCast(queueSize.longValue());
        qs = Math.min(qs, MAX_QUEUE_SIZE);
        qs = Math.max(qs, 1);
        this.queueSize = qs;
    }

    public synchronized boolean getNotifications(List<UaStructure> notifications, int max) {
        int queueSize = queue.size();
        int count = Math.min(queueSize, max);

        for (int i = 0; i < count; i++) {
            notifications.add(wrapQueueValue(queue.remove()));
        }

        boolean queueIsEmpty = queue.isEmpty();

        if (queueIsEmpty && triggered) {
            triggered = false;
        }

        return queueIsEmpty;
    }

    public synchronized boolean hasNotifications() {
        return (queue.size() > 0 && monitoringMode == MonitoringMode.Reporting);
    }

    public synchronized void modify(TimestampsToReturn timestamps,
                                    UInteger clientHandle,
                                    double samplingInterval,
                                    ExtensionObject filter,
                                    UInteger queueSize,
                                    boolean discardOldest) throws UaException {

        installFilter(filter);

        this.timestamps = timestamps;
        this.clientHandle = clientHandle.longValue();
        this.samplingInterval = samplingInterval;
        this.discardOldest = discardOldest;

        if (queueSize.intValue() != this.queueSize) {
            setQueueSize(queueSize);

            RingBuffer<T> oldQueue = queue;
            queue = new RingBuffer<>(this.queueSize);

            while (oldQueue.size() > 0) {
                enqueue(oldQueue.remove());
            }
        }
    }

    protected abstract void enqueue(T value);

    public void setMonitoringMode(MonitoringMode monitoringMode) {
        this.monitoringMode = monitoringMode;

        if (monitoringMode == MonitoringMode.Disabled) {
            queue.clear();
        }
    }

    @Override
    public UInteger getId() {
        return id;
    }

    @Override
    public UInteger getSubscriptionId() {
        return subscriptionId;
    }

    @Override
    public ReadValueId getReadValueId() {
        return readValueId;
    }

    @Override
    public TimestampsToReturn getTimestampsToReturn() {
        return timestamps;
    }

    public long getClientHandle() {
        return clientHandle;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public double getSamplingInterval() {
        return samplingInterval;
    }

    public boolean isDiscardOldest() {
        return discardOldest;
    }

    public MonitoringMode getMonitoringMode() {
        return monitoringMode;
    }

    public synchronized Map<UInteger, BaseMonitoredItem<?>> getTriggeredItems() {
        if (triggeredItems == null) triggeredItems = new HashMap<>();

        return triggeredItems;
    }

    public synchronized boolean isTriggered() {
        return triggered;
    }

    public abstract ExtensionObject getFilterResult();

    protected abstract void installFilter(ExtensionObject filterXo) throws UaException;

    protected abstract UaStructure wrapQueueValue(T value);

}
