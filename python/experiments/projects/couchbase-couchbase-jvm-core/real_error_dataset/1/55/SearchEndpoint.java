/*
 * Copyright (c) 2016 Couchbase, Inc.
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
package com.couchbase.client.core.endpoint.search;

import com.couchbase.client.core.CoreContext;
import com.couchbase.client.core.ResponseEvent;
import com.couchbase.client.core.endpoint.AbstractEndpoint;
import com.couchbase.client.core.env.CoreEnvironment;
import com.lmax.disruptor.RingBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * This endpoint defines the pipeline for search requests and responses (CBFT).
 *
 * @author Sergey Avseyev
 * @since 1.2
 */
public class SearchEndpoint extends AbstractEndpoint {

    @Deprecated
    public SearchEndpoint(String hostname, String bucket, String password, int port, CoreContext ctx) {
        this(hostname, bucket, bucket, password, port, ctx);
    }

    public SearchEndpoint(String hostname, String bucket, String username, String password, int port, CoreContext ctx) {
        super(hostname, bucket, username, password, port, ctx, false,
                ctx.environment().searchIoPool() == null ? ctx.environment().ioPool() : ctx.environment().searchIoPool(), false);
    }

    @Override
    protected void customEndpointHandlers(final ChannelPipeline pipeline) {
        if (environment().keepAliveInterval() > 0) {
            pipeline.addLast(new IdleStateHandler(environment().keepAliveInterval(), 0, 0, TimeUnit.MILLISECONDS));
        }
        pipeline.addLast(new HttpClientCodec())
                .addLast(new SearchHandler(this, responseBuffer(), false, false));
    }
}
