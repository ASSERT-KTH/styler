package org.genxdm.samples.performance.bridges;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.genxdm.io.DtdAttributeKind;
import org.genxdm.io.FragmentBuilder;

public class TestDocumentCreationWithBuilder <N,A> extends BaseBridgePerfTest<N,A> implements MutationPerfTestConstants
{
	private final String DOC_TYPE_DECL = null;
	private final DtdAttributeKind DTD_ATT_KIND = null;
	
    FragmentBuilder<N> m_fragBuilder;
	int m_depth;
	int m_width;
	
	@Override
	public String getTestName() {
		long size = 1;
		for(int icnt = 1; icnt <= m_depth; icnt++)
		{
			size += Math.pow(m_width, icnt);
		}
		return "Document creation via builder: " + size + " elements";
	}
	
	@Override
	public void initialSetup(Map<String,Object> props)
	{
		super.initialSetup(props);
		String depth = (String)props.get(DOC_CREATE_DEPTH_NAME);
		if(depth == null)
			m_depth = DOC_CREATE_DEPTH_DEFAULT;
		else
			m_depth = Integer.parseInt(depth);

		String width = (String)props.get(DOC_CREATE_WIDTH_NAME);
		if(width == null)
			m_width = DOC_CREATE_WIDTH_DEFAULT;
		else
			m_width = Integer.parseInt(width);
	}
	@Override
	public void iterativeSetup() {
		
		m_fragBuilder = getPcx().newFragmentBuilder();
	}
	@Override
	public void finalTeardown()	{
		m_fragBuilder = null;
		super.finalTeardown();
	}
	@Override
	public void execute() {
		/* // Create a new document. */
		try {
			m_fragBuilder.startDocument(new URI(DOC_CREATE_NS), DOC_TYPE_DECL);
			{
				m_fragBuilder.startElement(DOC_CREATE_NS, DOC_CREATE_ROOT_NAME, DOC_CREATE_PREFIX);
				{
					appendChildren(DOC_CREATE_NS, DOC_CREATE_PREFIX, DOC_CREATE_CHILD_NAME, DOC_CREATE_ATTS, DOC_CREATE_TEXT_VALUES, m_depth, m_width);
				}
				m_fragBuilder.endElement();
			}
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		m_fragBuilder.endDocument();
	}
	private void appendChildren(String ns, String prefix, String childName, String[][] atts, String textValues[], int depth, int width)
	{
		// Add children
		for(int icnt = 0; icnt < width; icnt++)
		{
			m_fragBuilder.startElement(ns, childName, prefix);
			{
				// Add attributes
				for(String[] att : atts)
				{
					m_fragBuilder.attribute(ns, att[0], prefix, att[1], DTD_ATT_KIND);
				}
				// If not leaf node, add children
				if(depth > 1)
				{
					depth--;
					appendChildren(ns, prefix, childName, atts, textValues, depth, width);
				}
				// Else if leaf node, add text values
				else
				{
					for(String textValue : textValues)
					{
						m_fragBuilder.text(textValue);
					}
				}
			}
			m_fragBuilder.endElement();
		}
	}
	@Override
	public Iterable<String> iterativeTeardown() { return null; }
}
