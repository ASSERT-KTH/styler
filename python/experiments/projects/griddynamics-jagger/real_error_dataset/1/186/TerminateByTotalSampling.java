/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the Apache License; either
 * version 2.0 of the License, or any later version.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.griddynamics.jagger.engine.e1.scenario;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;

@Deprecated
// TODO: Should be removed with xml configuration JFG-906
public class TerminateByTotalSampling implements TerminateStrategyConfiguration {
    private int samples;

    @Override
    public TerminationStrategy getTerminateStrategy() {
        return new TerminateByTotalSamplingStrategy(samples);
    }

    @Required
    public void setSamples(int samples) {
        Preconditions.checkArgument(samples > 0, "Samples should be > 0");

        this.samples = samples;
    }

    private static class TerminateByTotalSamplingStrategy implements TerminationStrategy {
        private final int samples;

        public TerminateByTotalSamplingStrategy(int samples) {
            this.samples = samples;
        }

        @Override
        public boolean isTerminationRequired(WorkloadExecutionStatus status) {
            int samples = status.getTotalSamples();
            return samples >= this.samples;
        }
    }

    @Override
    public String toString() {
        return "Terminate after " + samples +" samples";
    }
}
