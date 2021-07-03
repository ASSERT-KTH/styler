/*
 * Copyright (c) 2019 the Eclipse Milo Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.milo.opcua.sdk.server.events.conversions;

import org.eclipse.milo.opcua.stack.core.BuiltinDataType;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class LocalizedTextConversions {

    private LocalizedTextConversions() {}

    @Nullable
    static String localizedTextToString(@NotNull LocalizedText text) {
        return text.getText();
    }

    @Nullable
    static Object convert(@NotNull Object o, BuiltinDataType targetType, boolean implicit) {
        if (o instanceof LocalizedText) {
            LocalizedText text = (LocalizedText) o;

            return implicit ?
                implicitConversion(text, targetType) :
                explicitConversion(text, targetType);
        } else {
            return null;
        }
    }

    @Nullable
    static Object explicitConversion(@NotNull LocalizedText text, BuiltinDataType targetType) {
        return implicitConversion(text, targetType);
    }

    @Nullable
    static Object implicitConversion(@NotNull LocalizedText text, BuiltinDataType targetType) {
        //@formatter:off
        switch (targetType) {
            case String:    return localizedTextToString(text);
            default:        return null;
        }
        //@formatter:on
    }

}
