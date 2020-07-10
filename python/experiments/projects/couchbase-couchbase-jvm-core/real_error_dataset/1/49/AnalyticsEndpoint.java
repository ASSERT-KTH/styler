/*
 * Copyright (c) 2017 Couchbase, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.couchbase.client.core.endpoint.analytics;

import com.couchbase.client.core.ResponseEvent;
import com.couchbase.client.core.endpoint.AbstractEndpoint;
import com.couchbase.client.core.env.CoreEnvironment;
import com.lmax.disruptor.RingBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * This endpoint defines the pipeline for analytics requests and responses.
 *
 * @author Michael Nitschinger
 * @since 1.4.3
 */
public class AnalyticsEndpoint extends AbstractEndpoint {

    public AnalyticsEndpoint(String hostname, String bucket, String password, int port, CoreEnvironment environment,
                             RingBuffer<ResponseEvent> responseBuffer) {
        this(hostname, bucket, bucket, password, port, environment, responseBuffer);
    }

    public AnalyticsEndpoint(String hostname, String bucket, String username, String password, int port, CoreEnvironment environment,
        RingBuffer<ResponseEvent> responseBuffer) {
        super(hostname, bucket, username, password, port, environment, responseBuffer, false,
                environment.queryIoPool() == null ? environment.ioPool() : environment.queryIoPool(), false);
    }

    @Override
    protected void customEndpointHandlers(final ChannelPipeline pipeline) {
        if (environment().keepAliveInterval() > 0) {
            pipeline.addLast(
                new IdleStateHandler(environment().keepAliveInterval(), 0, 0, TimeUnit.MILLISECONDS)
            );
        }

        pipeline
            .addLast(new HttpClientCodec())
            .addLast(new AnalyticsHandler(this, responseBuffer(), false, false));
    }
}
