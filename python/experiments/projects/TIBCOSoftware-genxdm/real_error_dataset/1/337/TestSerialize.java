package org.genxdm.samples.performance.dom;

import java.util.Map;

import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

public class TestSerialize extends BaseBareDomPerfTest {

	LSSerializer mi_writer;
	
	@Override
	public String getTestName() {
		return getDocFile();
	}
	@Override
	public void initialSetup(Map<String,Object> props)	{
		super.initialSetup(props);
        System.setProperty(DOMImplementationRegistry.PROPERTY, "org.apache.xerces.dom.DOMImplementationSourceImpl");
		DOMImplementationRegistry registry;
		try {
			registry = DOMImplementationRegistry.newInstance();
		} catch (ClassCastException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		DOMImplementationLS impl = (DOMImplementationLS)registry.getDOMImplementation("LS");
		mi_writer = impl.createLSSerializer();
	}
	
	@Override
	public void iterativeSetup() {
		if(m_testNode == null)
		{
			setupTestNode();
		}
	}
	@Override
	public void execute() {
        mi_writer.writeToString(m_testNode);
	}
	@Override
	public Iterable<String> iterativeTeardown() { return null; }
	
	@Override
	public void finalTeardown() {};
}
