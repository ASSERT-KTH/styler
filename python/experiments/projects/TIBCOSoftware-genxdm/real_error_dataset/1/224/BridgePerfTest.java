package org.genxdm.samples.performance.bridges;

import org.genxdm.samples.performance.PerfTest;

public interface BridgePerfTest <N, A> extends PerfTest {
	Iterable<String> getRequiredFeatures();
}
