package org.genxdm.samples.performance;

import org.genxdm.bridgekit.ProcessingContextFactory;

public interface VersionedProcessingContextFactory<N> extends ProcessingContextFactory<N>{
	String getBridgeName();
	String getBridgeVersion();
}
