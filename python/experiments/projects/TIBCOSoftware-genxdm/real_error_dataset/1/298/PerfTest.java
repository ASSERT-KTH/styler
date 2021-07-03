package org.genxdm.samples.performance;

import java.util.Map;

public interface PerfTest {
	String getBridgeName();
	String getBridgeVersion();
	String getTestName();
	void execute();
	void initialSetup(Map<String,Object> props);
	void iterativeSetup();
	Iterable<String> iterativeTeardown();
	void finalTeardown();
}
