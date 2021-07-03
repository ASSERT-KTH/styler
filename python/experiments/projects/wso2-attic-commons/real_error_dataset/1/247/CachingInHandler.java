/*
 * Copyright (c) 2006, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.caching.handlers;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.OperationContext;
import org.apache.axis2.description.AxisModule;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.Parameter;
import org.wso2.caching.*;
import org.wso2.caching.digest.DigestGenerator;
import org.wso2.caching.util.FixedByteArrayOutputStream;

import javax.xml.stream.XMLStreamException;

public class CachingInHandler extends CachingHandler {

    /**
     * This method will be invoked in the inflow to do the caching related in flow handling over
     * the message
     *
     * @param msgContext - MessageContext including the message to be processed
     * @return InvocationResponse.CONTINUE in order to continue after the handling
     * @throws AxisFault if any errors occured during the processing
     */
    public InvocationResponse invoke(MessageContext msgContext) throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug("Starting the execution of the CachingInHandler");
        }
        if (!msgContext.isServerSide()) {
            throw new UnsupportedOperationException("Client side caching is not supported");
        }
        OperationContext opCtx = msgContext.getOperationContext();
        if (opCtx == null) {
            handleException("Unable to perform caching : OperationContext not found to store " +
                                    "cache details");
        }
        ConfigurationContext cfgCtx = msgContext.getConfigurationContext();
        if (cfgCtx == null) {
            handleException("Unable to perform caching : ConfigurationContext not " +
                                    "found to process cache");
        }

        CacheConfiguration cacheCfg = null;
        Parameter ccfgParam
                = msgContext.getAxisMessage().getParameter(CachingConstants.CACHE_CONFIGURATION);

        if (ccfgParam != null && ccfgParam.getValue() instanceof CacheConfiguration) {
            cacheCfg = (CacheConfiguration) ccfgParam.getValue();
        } else {
            handleException("Unable to perform " +
                                    "caching : Could not find the CacheConfiguration");
        }

        // even though we found a cache config, if the timeout is <= 0, caching is disabled
        if (cacheCfg.getTimeout() <= 0) {
            return InvocationResponse.CONTINUE;
        }

        // if maxMessageSize is specified check for the message size before processing
        FixedByteArrayOutputStream fbaos;
        if (cacheCfg.getMaxMessageSize() > 0) {
            fbaos = new FixedByteArrayOutputStream(cacheCfg.getMaxMessageSize());
            try {
                msgContext.getEnvelope().serialize(fbaos);
            } catch (XMLStreamException e) {
                handleException("Error in checking the message size", e);
            } catch (CachingException che) {
                if (log.isDebugEnabled()) {
                    log.debug("Message size exceeds the upper bound for caching, " +
                                      "request will not be cached");
                    return InvocationResponse.CONTINUE;
                }
            }
        }

        String requestHash = getRequestHash(msgContext, cacheCfg);

        CacheManager cacheManager =
                (CacheManager) cfgCtx.getPropertyNonReplicable(CachingConstants.CACHE_MANAGER);
        String serviceName = msgContext.getAxisService().getName();

        opCtx.setNonReplicableProperty(CachingConstants.REQUEST_HASH, requestHash);
        ServiceName service = new ServiceName(serviceName);
        RequestHash hash = new RequestHash(requestHash);
        CachableResponse cachedResponse =
                cacheManager.getCachedResponse(service, hash);
        CacheReplicationCommand cacheReplicationCommand = new CacheReplicationCommand();
        if (cachedResponse != null) { // Response is available in the cache
            if (!cachedResponse.isExpired()) { // Cache hit & fresh. No state replication needed
                if (log.isDebugEnabled()) {
                    log.debug("Cache-hit for message-ID : " + msgContext.getMessageID());
                }
                //mark CachedObject as in use to prevent cleanup
                cachedResponse.setInUse(true);
                opCtx.setNonReplicableProperty(CachingConstants.CACHED_OBJECT, cachedResponse);
                // Forcefully dispatch the request to the CachedOperation,
                // bypassing the dispatching to the requested operation.
                // So, the CacheMessageReceiver will be invoked once we CONTINUE
                AxisService axisService = msgContext.getAxisService();
                if (axisService != null) {
                    AxisOperation cachedOperation =
                            axisService.getOperation(CachingConstants.CACHED_OPERATION_QNAME);
                    if (cachedOperation != null) {
                        cachedOperation.setControlOperation(true);
                        msgContext.setAxisOperation(cachedOperation);
                    } else {
                        handleException("Unable to perform " +
                                                "caching : Could not find the cached operation");
                    }
                }
                return InvocationResponse.CONTINUE;
            } else {  // Cache hit but stale. Need to replicate state
                cachedResponse.reincarnate(cacheCfg.getTimeout());
                if (log.isDebugEnabled()) {
                    log.debug("Existing cached response has expired. Reset cache element");
                }
                cacheManager.cacheResponse(service, hash, cachedResponse, cacheReplicationCommand);
                opCtx.setNonReplicableProperty(CachingConstants.CACHED_OBJECT, cachedResponse);
                opCtx.setNonReplicableProperty(CachingConstants.STATE_REPLICATION_OBJECT,
                                               cacheReplicationCommand);
                // Request needs to continue to reach the service since the response envelope
                // needs to be recreated by the service
                return InvocationResponse.CONTINUE;
            }
        } else { // Brand new request. No cached response found
            if (log.isDebugEnabled()) {
                log.debug("There is no cached response for the request. Trying to cache...");
            }

            if (cacheManager.getCacheSize(service) >= cacheCfg.getMaxCacheSize()) { // If cache is full
                cacheManager.removeExpiredResponses(service, cacheReplicationCommand); // try to remove expired responses
                if (cacheManager.getCacheSize(service) >= cacheCfg.getMaxCacheSize()) { // recheck if there is space
                    if (log.isDebugEnabled()) {
                        log.debug("In-memory cache is full. Unable to cache");
                    }
                } else { // if we managed to free up some space in the cache. Need state replication
                    cacheNewResponse(msgContext, service, hash, cacheManager, cacheCfg,
                                     cacheReplicationCommand);
                }
            } else { // if there is more space in the cache. Need state replication
                cacheNewResponse(msgContext, service, hash, cacheManager, cacheCfg,
                                 cacheReplicationCommand);
            }
            return InvocationResponse.CONTINUE;
        }
    }

    private String getRequestHash(MessageContext msgContext,
                                  CacheConfiguration cacheCfg) throws AxisFault {
        String requestHash = null;
        try {
            DigestGenerator digestGenerator = cacheCfg.getDigestGenerator();
            if (digestGenerator != null) {
                requestHash = digestGenerator.getDigest(msgContext);
            } else {
                handleException("Unable to retrieve the DigestGenerator from the CacheManager");
            }
        } catch (CachingException ce) {
            handleException("Unable to perform " +
                                    "caching : Error in generating the request hash");
        }
        return requestHash;
    }

    private void cacheNewResponse(MessageContext msgContext,
                                  ServiceName serviceName, RequestHash requestHash,
                                  CacheManager cacheManager, CacheConfiguration chCfg,
                                  CacheReplicationCommand cacheReplicationCommand) {
        CachableResponse response = new CachableResponse();
        response.setRequestHash(requestHash.getRequestHash());
        response.setTimeout(chCfg.getTimeout());
        cacheManager.cacheResponse(serviceName, requestHash, response, cacheReplicationCommand);
        OperationContext opCtx = msgContext.getOperationContext();
        opCtx.setNonReplicableProperty(CachingConstants.CACHED_OBJECT, response);
        opCtx.setNonReplicableProperty(CachingConstants.STATE_REPLICATION_OBJECT,
                                       cacheReplicationCommand);
    }
}
