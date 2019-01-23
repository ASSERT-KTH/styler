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

import org.eclipse.milo.opcua.sdk.client.api.model.types.variables.PropertyType;
import org.eclipse.milo.opcua.sdk.core.model.BasicProperty;
import org.eclipse.milo.opcua.sdk.core.model.Property;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;


public interface AuditHistoryAtTimeDeleteEventType extends AuditHistoryDeleteEventType {

    Property<DateTime[]> REQ_TIMES = new BasicProperty<>(
        QualifiedName.parse("0:ReqTimes"),
        NodeId.parse("ns=0;i=294"),
        1,
        DateTime[].class
    );

    Property<DataValue[]> OLD_VALUES = new BasicProperty<>(
        QualifiedName.parse("0:OldValues"),
        NodeId.parse("ns=0;i=23"),
        1,
        DataValue[].class
    );


    CompletableFuture<? extends PropertyType> reqTimes();

    CompletableFuture<DateTime[]> getReqTimes();

    CompletableFuture<StatusCode> setReqTimes(DateTime[] value);

    CompletableFuture<? extends PropertyType> oldValues();

    CompletableFuture<DataValue[]> getOldValues();

    CompletableFuture<StatusCode> setOldValues(DataValue[] value);


}