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

package org.eclipse.milo.opcua.sdk.server.model.types.objects;

import org.eclipse.milo.opcua.sdk.core.model.BasicProperty;
import org.eclipse.milo.opcua.sdk.core.model.Property;
import org.eclipse.milo.opcua.sdk.server.model.types.variables.PropertyType;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;

public interface ProgressEventType extends BaseEventType {

    Property<Object> CONTEXT = new BasicProperty<>(
        QualifiedName.parse("0:Context"),
        NodeId.parse("ns=0;i=24"),
        -1,
        Object.class
    );

    Property<UShort> PROGRESS = new BasicProperty<>(
        QualifiedName.parse("0:Progress"),
        NodeId.parse("ns=0;i=5"),
        -1,
        UShort.class
    );

    Object getContext();

    PropertyType getContextNode();

    void setContext(Object value);

    UShort getProgress();

    PropertyType getProgressNode();

    void setProgress(UShort value);
}
