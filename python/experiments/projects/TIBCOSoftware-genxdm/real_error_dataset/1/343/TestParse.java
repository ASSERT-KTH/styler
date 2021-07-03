package org.genxdm.samples.performance.dom;

import org.xml.sax.SAXException;
import java.io.IOException;

public class TestParse extends BaseBareDomPerfTest {

	String mi_docFilePath;
	@Override
	public String getTestName() {
		return getDocFile();
	}
	
	@Override
	public void iterativeSetup() {
	    mi_docFilePath = getBaseURI() + "/" + getDocFile();
	}
	@Override
	public void execute() {
		try {
			getDocBuilder().parse(mi_docFilePath);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public Iterable<String> iterativeTeardown() { return null; }
	
	@Override
	public void finalTeardown() {}

}
