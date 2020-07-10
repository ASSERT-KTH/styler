package org.genxdm.samples.performance.bridges;

import java.io.IOException;
import java.io.StringWriter;

import org.genxdm.exceptions.XdmMarshalException;

public class TestSerialize<N,A> extends BaseBridgePerfTest<N,A> {

	StringWriter m_writer;
	@Override
	public String getTestName() {
		return getDocFilename();
	}
	
	@Override
	public void iterativeSetup() {
		m_writer = new StringWriter();
	}
	
	@Override
	public void execute() {
        try {
			getDocHandler().write(m_writer, getTestNode());
		} 
        catch (XdmMarshalException e) 
		{
			throw new RuntimeException(e);
		} 
        catch (IOException e) 
		{
			throw new RuntimeException(e);
		}
	}
	@Override
	public Iterable<String> iterativeTeardown() {
		m_writer = null;
		return null; 
	}
	@Override
	public void finalTeardown()	{
		m_writer = null;
		super.finalTeardown();
	}
}
