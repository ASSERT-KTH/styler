/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.apache.kylin.rest.response;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.kylin.common.QueryContext;
import org.apache.kylin.metadata.querymeta.SelectedColumnMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;

public class SQLResponse implements Serializable {
    protected static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(SQLResponse.class);

    // the data type for each column
    protected List<SelectedColumnMeta> columnMetas;

    // the results rows, each row contains several columns
    protected List<List<String>> results;

    /**
     * for historical reasons it is named "cube", however it might also refer to any realizations like hybrid, II or etc.
     */
    protected String cube;

    // if not select query, only return affected row count
    protected int affectedRowCount;

    // flag indicating whether an exception occurred
    protected boolean isException;

    // if isException, the detailed exception message
    protected String exceptionMessage;

    // if isException, the related Exception
    protected Throwable throwable;

    protected long duration;

    protected boolean isPartial = false;

    protected long totalScanCount;

    protected long totalScanBytes;

    protected boolean hitExceptionCache = false;

    protected boolean storageCacheUsed = false;

    protected boolean queryPushDown = false;

    protected byte[] queryStatistics;
    
    protected String traceUrl = null;

    // it's sql response signature for cache checking, no need to return and should be JsonIgnore
    protected String signature;

    // it's a temporary flag, no need to return and should be JsonIgnore
    // indicating the lazy query start time, -1 indicating not enabled
    protected long lazyQueryStartTime = -1L;

    public SQLResponse() {
    }

    public SQLResponse(List<SelectedColumnMeta> columnMetas, List<List<String>> results, int affectedRowCount,
            boolean isException, String exceptionMessage) {
        this.columnMetas = columnMetas;
        this.results = results;
        this.affectedRowCount = affectedRowCount;
        this.isException = isException;
        this.exceptionMessage = exceptionMessage;
    }

    public SQLResponse(List<SelectedColumnMeta> columnMetas, List<List<String>> results, String cube,
            int affectedRowCount, boolean isException, String exceptionMessage, boolean isPartial, boolean isPushDown) {
        this.columnMetas = columnMetas;
        this.results = results;
        this.cube = cube;
        this.affectedRowCount = affectedRowCount;
        this.isException = isException;
        this.exceptionMessage = exceptionMessage;
        this.isPartial = isPartial;
        this.queryPushDown = isPushDown;
    }

    public List<SelectedColumnMeta> getColumnMetas() {
        return columnMetas;
    }

    public List<List<String>> getResults() {
        return results;
    }

    public void setResults(List<List<String>> results) {
        this.results = results;
    }

    public String getCube() {
        return cube;
    }

    public void setCube(String cube) {
        this.cube = cube;
    }

    public int getAffectedRowCount() {
        return affectedRowCount;
    }

    public boolean getIsException() {
        return isException;
    }

    public void setIsException(boolean v) {
        isException = v;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String msg) {
        exceptionMessage = msg;
    }

    @JsonIgnore
    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isPartial() {

        return isPartial;
    }

    public boolean isPushDown() {
        return queryPushDown;
    }

    public long getTotalScanCount() {
        return totalScanCount;
    }

    public void setTotalScanCount(long totalScanCount) {
        this.totalScanCount = totalScanCount;
    }

    public long getTotalScanBytes() {
        return totalScanBytes;
    }

    public void setTotalScanBytes(long totalScanBytes) {
        this.totalScanBytes = totalScanBytes;
    }

    public boolean isHitExceptionCache() {
        return hitExceptionCache;
    }

    public void setHitExceptionCache(boolean hitExceptionCache) {
        this.hitExceptionCache = hitExceptionCache;
    }

    public boolean isStorageCacheUsed() {
        return storageCacheUsed;
    }

    public void setStorageCacheUsed(boolean storageCacheUsed) {
        this.storageCacheUsed = storageCacheUsed;
    }

    public String getTraceUrl() {
        return traceUrl;
    }

    public void setTraceUrl(String traceUrl) {
        this.traceUrl = traceUrl;
    }

    @JsonIgnore
    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @JsonIgnore
    public boolean isRunning() {
        return this.lazyQueryStartTime >= 0;
    }

    @JsonIgnore
    public long getLazyQueryStartTime() {
        return lazyQueryStartTime;
    }

    public void setLazyQueryStartTime(long lazyQueryStartTime) {
        this.lazyQueryStartTime = lazyQueryStartTime;
    }

    @JsonIgnore
    public List<QueryContext.CubeSegmentStatisticsResult> getCubeSegmentStatisticsList() {
        try {
            return queryStatistics == null ? Lists.<QueryContext.CubeSegmentStatisticsResult> newArrayList()
                    : (List<QueryContext.CubeSegmentStatisticsResult>) SerializationUtils.deserialize(queryStatistics);
        } catch (Exception e) { // deserialize exception should not block query
            logger.warn("Error while deserialize queryStatistics due to " + e);
            return Lists.newArrayList();
        }
    }

    public void setCubeSegmentStatisticsList(
            List<QueryContext.CubeSegmentStatisticsResult> cubeSegmentStatisticsList) {
        try {
            this.queryStatistics = cubeSegmentStatisticsList == null ? null
                    : SerializationUtils.serialize((Serializable) cubeSegmentStatisticsList);
        } catch (Exception e) { // serialize exception should not block query
            logger.warn("Error while serialize queryStatistics due to " + e);
            this.queryStatistics = null;
        }
    }
}
