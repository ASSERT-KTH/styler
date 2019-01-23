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

import org.eclipse.milo.opcua.sdk.core.model.BasicProperty;
import org.eclipse.milo.opcua.sdk.core.model.Property;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.structured.AxisInformation;


public interface CubeItemType extends ArrayItemType {

    Property<AxisInformation> X_AXIS_DEFINITION = new BasicProperty<>(
        QualifiedName.parse("0:XAxisDefinition"),
        NodeId.parse("ns=0;i=12079"),
        -1,
        AxisInformation.class
    );

    Property<AxisInformation> Y_AXIS_DEFINITION = new BasicProperty<>(
        QualifiedName.parse("0:YAxisDefinition"),
        NodeId.parse("ns=0;i=12079"),
        -1,
        AxisInformation.class
    );

    Property<AxisInformation> Z_AXIS_DEFINITION = new BasicProperty<>(
        QualifiedName.parse("0:ZAxisDefinition"),
        NodeId.parse("ns=0;i=12079"),
        -1,
        AxisInformation.class
    );


    CompletableFuture<? extends PropertyType> xAxisDefinition();

    CompletableFuture<AxisInformation> getXAxisDefinition();

    CompletableFuture<StatusCode> setXAxisDefinition(AxisInformation value);

    CompletableFuture<? extends PropertyType> yAxisDefinition();

    CompletableFuture<AxisInformation> getYAxisDefinition();

    CompletableFuture<StatusCode> setYAxisDefinition(AxisInformation value);

    CompletableFuture<? extends PropertyType> zAxisDefinition();

    CompletableFuture<AxisInformation> getZAxisDefinition();

    CompletableFuture<StatusCode> setZAxisDefinition(AxisInformation value);


}