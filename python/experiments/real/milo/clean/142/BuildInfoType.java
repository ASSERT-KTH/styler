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

import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;


public interface BuildInfoType extends BaseDataVariableType {


    CompletableFuture<? extends BaseDataVariableType> productUri();

    CompletableFuture<String> getProductUri();

    CompletableFuture<StatusCode> setProductUri(String value);

    CompletableFuture<? extends BaseDataVariableType> manufacturerName();

    CompletableFuture<String> getManufacturerName();

    CompletableFuture<StatusCode> setManufacturerName(String value);

    CompletableFuture<? extends BaseDataVariableType> productName();

    CompletableFuture<String> getProductName();

    CompletableFuture<StatusCode> setProductName(String value);

    CompletableFuture<? extends BaseDataVariableType> softwareVersion();

    CompletableFuture<String> getSoftwareVersion();

    CompletableFuture<StatusCode> setSoftwareVersion(String value);

    CompletableFuture<? extends BaseDataVariableType> buildNumber();

    CompletableFuture<String> getBuildNumber();

    CompletableFuture<StatusCode> setBuildNumber(String value);

    CompletableFuture<? extends BaseDataVariableType> buildDate();

    CompletableFuture<DateTime> getBuildDate();

    CompletableFuture<StatusCode> setBuildDate(DateTime value);


}