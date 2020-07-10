package org.genxdm.samples.performance.dom;


public class TestDocumentCreationWithBuilder extends BaseBareDomPerfTest {

	@Override
	public String getTestName() {
		return "Document creation via builder test not implemented for bare DOM; ignore results";
	}
	
	@Override
	public void iterativeSetup() {}
	
	@Override
	public void execute() {
		//throw new UnsupportedOperationException("Document creation via builder test not implemented for bare DOM.");
	}
	@Override
	public Iterable<String> iterativeTeardown() { return null; }
	
	@Override
	public void finalTeardown() {};
}
