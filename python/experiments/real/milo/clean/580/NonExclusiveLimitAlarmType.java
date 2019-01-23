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

import org.eclipse.milo.opcua.sdk.server.model.types.variables.TwoStateVariableType;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;

public interface NonExclusiveLimitAlarmType extends LimitAlarmType {

    LocalizedText getActiveState();

    TwoStateVariableType getActiveStateNode();

    void setActiveState(LocalizedText value);

    LocalizedText getHighHighState();

    TwoStateVariableType getHighHighStateNode();

    void setHighHighState(LocalizedText value);

    LocalizedText getHighState();

    TwoStateVariableType getHighStateNode();

    void setHighState(LocalizedText value);

    LocalizedText getLowState();

    TwoStateVariableType getLowStateNode();

    void setLowState(LocalizedText value);

    LocalizedText getLowLowState();

    TwoStateVariableType getLowLowStateNode();

    void setLowLowState(LocalizedText value);
}
