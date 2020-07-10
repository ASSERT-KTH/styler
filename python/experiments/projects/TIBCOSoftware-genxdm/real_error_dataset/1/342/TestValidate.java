package org.genxdm.samples.performance.dom;


public class TestValidate extends BaseBareDomPerfTest {

	@Override
	public String getTestName() {
		return "Validate test not implemented for bare DOM; ignore results";
	}
	
	@Override
	public void iterativeSetup() {
	}
	@Override
	public void execute() {
		//throw new UnsupportedOperationException("Validate test not implemented for bare DOM.");
	}
	@Override
	public Iterable<String> iterativeTeardown() {
		return null;
	}
	
	@Override
	public void finalTeardown() {};
}
