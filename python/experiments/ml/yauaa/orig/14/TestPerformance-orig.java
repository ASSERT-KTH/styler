/*
 * Yet Another UserAgent Analyzer
 * Copyright (C) 2013-2018 Niels Basjes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.basjes.parse.useragent.profile;

import nl.basjes.parse.useragent.UserAgentAnalyzer;
import nl.basjes.parse.useragent.debug.UserAgentAnalyzerTester;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;

public class TestPerformance {
    private static final Logger LOG = LoggerFactory.getLogger(TestPerformance.class);

    @Ignore
    @Test
    public void validateAllPredefinedBrowsersPerformance() { //NOSONAR: Do not complain about ignored performance test
        UserAgentAnalyzerTester uaa =
            UserAgentAnalyzerTester.newBuilder()
            .showMatcherLoadStats()
            .immediateInitialization()
            .build();
        Assert.assertTrue(uaa.runTests(false, false, null, true, true));
    }

    @Test
    public void checkAllPossibleFieldsFastSpeed() {
        LOG.info("Create analyzer");
        long start = System.nanoTime();
        UserAgentAnalyzer uaa = UserAgentAnalyzer
            .newBuilder()
            .keepTests()
            .delayInitialization()
            .build();
        long stop = System.nanoTime();
        long constructMsecs = (stop - start) / 1000000;
        LOG.info("-- Construction time: {}ms", constructMsecs);

        LOG.info("List fieldnames");
        start = System.nanoTime();
        uaa.getAllPossibleFieldNamesSorted()
            .forEach(LOG::info);
        stop = System.nanoTime();
        long listFieldNamesMsecs = (stop - start) / 1000000;
        LOG.info("-- List fieldnames: {}ms", listFieldNamesMsecs);
        assertTrue("Just listing the field names should only take a few ms", listFieldNamesMsecs < 500);

        LOG.info("Initializing the datastructures");
        start = System.nanoTime();
        uaa.initializeMatchers();
        stop = System.nanoTime();
        long initializeMsecs = (stop - start) / 1000000;
        LOG.info("-- Initialization: {}ms", initializeMsecs);
        assertTrue("The initialization went too fast, this should take several seconds", initializeMsecs > 1000);

        LOG.info("Preheat");
        start = System.nanoTime();
        uaa.preHeat();
        stop = System.nanoTime();
        long preheatMsecs = (stop - start) / 1000000;
        LOG.info("-- Preheat : {}ms", preheatMsecs);
    }

}
