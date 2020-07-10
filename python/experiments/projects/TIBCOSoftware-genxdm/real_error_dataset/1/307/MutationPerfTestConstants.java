package org.genxdm.samples.performance.bridges;

public interface MutationPerfTestConstants {
	static final String DOC_CREATE_DEPTH_NAME = "depth";
	static final int DOC_CREATE_DEPTH_DEFAULT = 3;
	static final String DOC_CREATE_WIDTH_NAME = "width";
	static final int DOC_CREATE_WIDTH_DEFAULT = 10;
	
	static final String DOC_CREATE_NS = "http://com.tibco.com/test";
	static final String DOC_CREATE_PREFIX = "ns";
	static final String DOC_CREATE_ROOT_NAME = "root";
	static final String DOC_CREATE_CHILD_NAME = "child";
	static final String[][] DOC_CREATE_ATTS = {
		{"att0","0.0"}, {"att1","1.0"}, {"att2","2.0"}, {"att3","string3"},
			{"att4","string4"}, {"att5","2010-12-10T14:43:55+04:00"}
	};
	static final String[] DOC_CREATE_TEXT_VALUES = {
		"Hello"," ", "world!", "\n" 
	};
}
