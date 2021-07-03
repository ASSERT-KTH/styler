/*
 * $HeadURL: http://svn.apache.org/repos/asf/httpcomponents/httpcore/trunk/contrib/src/main/java/org/apache/http/contrib/benchmark/Stats.java $
 * $Revision: 534594 $
 * $Date: 2007-05-02 12:14:03 -0700 (Wed, 02 May 2007) $
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.apache.http.contrib.benchmark;

/**
 * Helper to gather statistics for an {@link HttpBenchmark HttpBenchmark}.
 * @author <a href="mailto:oleg at ural.ru">Oleg Kalnichevski</a>
 *
 *
 * <!-- empty lines above to avoid 'svn diff' context problems -->
 * @version $Revision: 534594 $
 * 
 * @since 4.0
 */
public class Stats {

    private long startTime = -1;    // nano seconds - does not represent an actual time
    private long finishTime = -1;   // nano seconds - does not represent an actual time
    private int successCount = 0;
    private int failureCount = 0;
    private int writeErrors = 0;
    private String serverName = null;
    private long totalBytesRecv = 0;
    private long contentLength = -1;

    public Stats() {
        super();
    }

    public void start() {
        this.startTime = System.nanoTime();
    }

    public void finish() {
        this.finishTime = System.nanoTime();
    }

    public long getFinishTime() {
        return this.finishTime;
    }

    public long getStartTime() {
        return this.startTime;
    }

    /**
     * Total execution time measured in nano seconds
     *
     * @return
     */
    public long getDuration() {
        // we are using System.nanoTime() and the return values could be negative
        // but its only the difference that we are concerned about
        return this.finishTime - this.startTime;
    }

    public void incSuccessCount() {
        this.successCount++;
    }

    public void incFailureCount() {
        this.failureCount++;
    }

    public int getFailureCount() {
        return this.failureCount;
    }

    public void incWriteErrors() {
        this.writeErrors++;
    }

    public int getWriteErrors() {
        return this.writeErrors;
    }

    public int getSuccessCount() {
        return this.successCount;
    }

    public long getTotalBytesRecv() {
        return this.totalBytesRecv;
    }

    public void incTotalBytesRecv(int n) {
        this.totalBytesRecv += n;
    }

    public long getContentLength() {
        return this.contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public String getServerName() {
        return this.serverName;
    }

    public void setServerName(final String serverName) {
        this.serverName = serverName;
    }

}
