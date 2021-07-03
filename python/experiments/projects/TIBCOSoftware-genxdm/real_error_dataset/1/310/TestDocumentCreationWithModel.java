package org.genxdm.samples.performance.bridges;

import java.util.ArrayList;
import java.util.Map;

import org.genxdm.Feature;
import org.genxdm.mutable.MutableContext;
import org.genxdm.mutable.MutableModel;
import org.genxdm.mutable.NodeFactory;

public class TestDocumentCreationWithModel <N,A> extends BaseBridgePerfTest<N,A>
implements MutationPerfTestConstants
{
	private int m_depth;
	private int m_width;
	private MutableContext<N> m_mutablePcx;
	private MutableModel<N> m_mutableModel;
	private NodeFactory<N> m_nodeFactory;
	private static final ArrayList<String> REQUIRED_FEATURES = new ArrayList<String>();
	static
	{
		REQUIRED_FEATURES.add(Feature.MUTABILITY);
	}
	
    @Override
    public Iterable<String> getRequiredFeatures()
    {
    	return REQUIRED_FEATURES;
    }
	@Override
	public String getTestName() {
		long size = 1;
		for(int icnt = 1; icnt <= m_depth; icnt++)
		{
			size += Math.pow(m_width, icnt);
		}
		return "Document creation via model: " + size + " elements";
	}
	@Override
	public void initialSetup(Map<String,Object> props)	{
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
		m_mutablePcx = getPcx().getMutableContext();
		m_mutableModel = m_mutablePcx.getModel();
		m_nodeFactory = m_mutablePcx.getNodeFactory();
	}
	
	@Override
	public void iterativeSetup() {}
	
	@Override
	public void execute() {
//System.out.println("depth x width = " + m_depth + " x " + m_width);		
		/* // Create a new document. */
		final N documentNode = m_nodeFactory.createDocument(null, null);

		final N documentElement = m_nodeFactory.createElement(DOC_CREATE_NS, DOC_CREATE_ROOT_NAME, DOC_CREATE_PREFIX);

		// Append the document element to the documentNode.
		m_mutableModel.appendChild(documentNode, documentElement);
		appendChildren(documentElement, DOC_CREATE_NS, DOC_CREATE_PREFIX, DOC_CREATE_CHILD_NAME, DOC_CREATE_ATTS, DOC_CREATE_TEXT_VALUES, m_depth, m_width);
	}
	private void appendChildren(N parent, String ns, String prefix, String childName, String[][] atts, String textValues[], int depth, int width)
	{
		// Add children
		for(int icnt = 0; icnt < width; icnt++)
		{
			final N childElement = m_nodeFactory.createElement(ns, childName, prefix);
			m_mutableModel.appendChild(parent, childElement);
			m_mutableModel.insertNamespace(childElement, DOC_CREATE_PREFIX, ns);
			
			// Add attributes
			for(String[] att : atts)
			{
				N attribute = m_nodeFactory.createAttribute(ns, att[0], prefix, att[1]);
				m_mutableModel.insertAttribute(childElement, attribute);
			}
			// If not leaf node, add children
			if(depth > 1)
			{
				appendChildren(childElement, ns, prefix, childName, atts, textValues, depth - 1, width);
			}
			// Else if leaf node, add text values
			else
			{
				for(String textValue : textValues)
				{
					m_mutableModel.appendChild(childElement, m_nodeFactory.createText(textValue));
				}
			}
		}
	}
	@Override
	public Iterable<String> iterativeTeardown() { return null; }
	
	@Override
	public void finalTeardown() {
		super.finalTeardown();
		m_mutableModel = null;
		m_mutablePcx = null;
		m_nodeFactory = null;
	}
	
}
