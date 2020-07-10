package org.genxdm.samples.performance.dom;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class TestDocumentCreationWithModel extends BaseBareDomPerfTest {
	static final String DOC_CREATE_DEPTH_NAME = "depth";
	static final int DOC_CREATE_DEPTH_DEFAULT = 3;
	static final String DOC_CREATE_WIDTH_NAME = "width";
	static final int DOC_CREATE_WIDTH_DEFAULT = 10;
	private static final String DOCUMENT_CREATE_NS = "http://com.tibco.com/test";
	private static final String DOCUMENT_CREATE_PREFIX = "ns";
	private static final String DOCUMENT_CREATE_ROOT_NAME = "root";
	private static final String DOCUMENT_CREATE_CHILD_NAME = "child";
	private static final String[][] DOCUMENT_CREATE_ATTS = {
		{"att0","0.0"}, {"att1","1.0"}, {"att2","2.0"}, {"att3","string3"},
			{"att4","string4"}, {"att5","2010-12-10T14:43:55+04:00"}
	};
	private static final String[] DOCUMENT_CREATE_TEXT_VALUES = {
		"Hello"," ", "world!", "\n" 
	};

	int m_depth;
	int m_width;
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
	public String getTestName() {
		long size = 1;
		for(int icnt = 1; icnt <= m_depth; icnt++)
		{
			size += Math.pow(m_width, icnt);
		}
		return "Document creation via model: " + size + " elements";
	}
	
	@Override
	public void iterativeSetup() {}
	
	@Override
	public void execute() {
		/* // Create a new document. */
		Document document = m_builder.newDocument();

		Element root = document.createElementNS(DOCUMENT_CREATE_NS, DOCUMENT_CREATE_PREFIX + ":" + DOCUMENT_CREATE_ROOT_NAME);

		// Append the document element to the documentNode.
		document.appendChild(root);

		appendChildren(document, root, DOCUMENT_CREATE_NS, DOCUMENT_CREATE_PREFIX, DOCUMENT_CREATE_CHILD_NAME, DOCUMENT_CREATE_ATTS, DOCUMENT_CREATE_TEXT_VALUES, m_depth, m_width);
	}
	private void appendChildren(Document doc, Node parent, String ns, String prefix, String childName, 
			String[][] atts, String textValues[], int depth, int width)
	{
		// Add children
		for(int icnt = 0; icnt < width; icnt++)
		{
			final Element childElement = doc.createElementNS(ns, prefix+":"+childName + "_" + depth + "_" + icnt);
			parent.appendChild(childElement);
			
			// Add attributes
			for(String[] att : atts)
			{
				childElement.setAttributeNS(ns, prefix+":"+att[0], att[1]);
			}
			// If not leaf node, add children
			if(depth > 1)
			{
				appendChildren(doc, childElement, ns, prefix, childName, atts, textValues, depth - 1 , width);
			}
			// Else if leaf node, add text values
			else
			{
				for(String textValue : textValues)
				{
					Text text = doc.createTextNode(textValue);
					childElement.appendChild(text);
				}
			}
		}
	}
	@Override
	public Iterable<String> iterativeTeardown() { return null; }
	
	@Override
	public void finalTeardown() {};
}
