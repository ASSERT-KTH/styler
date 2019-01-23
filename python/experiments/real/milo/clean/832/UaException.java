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

package org.eclipse.milo.opcua.stack.core;

import java.util.Optional;

import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;

public class UaException extends Exception {

    private final StatusCode statusCode;

    public UaException(long statusCode) {
        this.statusCode = new StatusCode(statusCode);
    }

    public UaException(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public UaException(Throwable cause) {
        super(cause);

        this.statusCode = new StatusCode(StatusCodes.Bad_InternalError);
    }

    public UaException(long statusCode, Throwable cause) {
        super(cause);

        this.statusCode = new StatusCode(statusCode);
    }

    public UaException(long statusCode, String message) {
        super(message);

        this.statusCode = new StatusCode(statusCode);
    }

    public UaException(StatusCode statusCode, String message) {
        super(message);

        this.statusCode = statusCode;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();

        if (message == null || message.isEmpty()) {
            Optional<String[]> lookup = StatusCodes.lookup(statusCode.getValue());

            String status = lookup.map(nd -> nd[0]).orElse(statusCode.toString());
            String description = lookup.map(nd -> nd[1]).orElse("");

            if (description.isEmpty()) {
                message = String.format("status=%s", status);
            } else {
                message = String.format("status=%s, description=%s", status, description);
            }
        }

        return message;
    }

    @Override
    public String toString() {
        String clazzName = getClass().getSimpleName();

        Optional<String[]> lookup = StatusCodes.lookup(statusCode.getValue());

        String status = lookup.map(nd -> nd[0]).orElse(statusCode.toString());

        String message = super.getMessage();
        if (message == null) {
            message = lookup.map(nd -> nd[1]).orElse("");
        }

        if (message.isEmpty()) {
            return String.format("%s: status=%s", clazzName, status);
        } else {
            return String.format("%s: status=%s, message=%s", clazzName, status, message);
        }
    }

    /**
     * If {@code ex} is a {@link UaException}, or if a {@link UaException} can be found by walking up the exception
     * cause chain, return it.
     *
     * @param ex the {@link Throwable} to extract from.
     * @return a {@link UaException} if one was present in the exception chain.
     */
    public static Optional<UaException> extract(Throwable ex) {
        if (ex instanceof UaException) {
            return Optional.of((UaException) ex);
        } else {
            Throwable cause = ex.getCause();
            return cause != null ?
                extract(cause) : Optional.empty();
        }
    }

}
