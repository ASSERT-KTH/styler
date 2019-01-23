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

package org.eclipse.milo.opcua.sdk.server.util;

import java.util.Objects;

import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.enumerated.DataChangeTrigger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.DeadbandType;
import org.eclipse.milo.opcua.stack.core.types.structured.DataChangeFilter;

public class DataChangeMonitoringFilter {

    public static boolean filter(DataValue lastValue, DataValue currentValue, DataChangeFilter filter) {
        return triggerFilter(lastValue, currentValue, filter) && deadbandFilter(lastValue, currentValue, filter);
    }

    private static boolean triggerFilter(DataValue lastValue, DataValue currentValue, DataChangeFilter filter) {
        if (lastValue == null) return true;

        DataChangeTrigger trigger = filter.getTrigger();

        if (trigger == DataChangeTrigger.Status) {
            return statusChanged(lastValue, currentValue);
        } else if (trigger == DataChangeTrigger.StatusValue) {
            return valueChanged(lastValue, currentValue) || statusChanged(lastValue, currentValue);
        } else {
            // DataChangeTrigger.StatusValueTimestamp
            return timestampChanged(lastValue, currentValue) ||
                valueChanged(lastValue, currentValue) ||
                statusChanged(lastValue, currentValue);
        }
    }

    private static boolean deadbandFilter(DataValue lastValue, DataValue currentValue, DataChangeFilter filter) {
        if (lastValue == null) return true;

        int index = filter.getDeadbandType().intValue();
        if (index < 0 || index >= DeadbandType.values().length) return true;
        DeadbandType deadbandType = DeadbandType.values()[index];

        if (deadbandType != DeadbandType.Absolute) return true;

        Object last = lastValue.getValue().getValue();
        Object current = currentValue.getValue().getValue();

        if (last == null || current == null) {
            return true;
        } else if (last.getClass().isArray() && current.getClass().isArray()) {
            return compareArrayDeadband(last, current, filter.getDeadbandValue());
        } else {
            return compareScalarDeadband(last, current, filter.getDeadbandValue());
        }
    }

    private static boolean compareArrayDeadband(Object last, Object current, double deadband) {
        Object[] lastA = Object[].class.cast(last);
        Object[] currentA = Object[].class.cast(current);

        if (lastA.length != currentA.length) {
            return true;
        } else {
            boolean exceeds = false;

            for (int i = 0; i < lastA.length; i++) {
                exceeds = exceeds || exceedsDeadband(lastA[i], currentA[i], deadband);
            }

            return exceeds;
        }
    }

    private static boolean compareScalarDeadband(Object last, Object current, double deadband) {
        return exceedsDeadband(last, current, deadband);
    }

    private static boolean exceedsDeadband(Object last, Object current, double deadband) {
        try {
            double lastD = Number.class.cast(last).doubleValue();
            double currentD = Number.class.cast(current).doubleValue();

            return Math.abs(lastD - currentD) > deadband;
        } catch (Throwable t) {
            return true;
        }
    }

    private static boolean statusChanged(DataValue lastValue, DataValue currentValue) {
        return !Objects.equals(lastValue.getStatusCode(), currentValue.getStatusCode());
    }

    private static boolean valueChanged(DataValue lastValue, DataValue currentValue) {
        return !Objects.equals(lastValue.getValue(), currentValue.getValue());
    }

    private static boolean timestampChanged(DataValue lastValue, DataValue currentValue) {
        return !Objects.equals(lastValue.getSourceTime(), currentValue.getSourceTime());
    }

}
