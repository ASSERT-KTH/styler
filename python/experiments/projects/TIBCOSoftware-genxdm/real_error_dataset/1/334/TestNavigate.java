package org.genxdm.samples.performance.bridges;

import java.util.ArrayList;

public class TestNavigate<N,A> extends BaseBridgePerfTest<N,A> 
{
	long m_attributes = 0;
	long m_comments = 0;
	long m_documents = 0;
	long m_elements = 0;
	long m_namespaces = 0;
	long m_processingInstructions = 0;
	long m_texts = 0;
	long m_other = 0;
	long m_all = 0;
	
	@Override
	public String getTestName() {
		return getDocFilename();
	}
	
	@Override
	public void iterativeSetup() {
		m_attributes = 0;
		m_comments = 0;
		m_documents = 0;
		m_elements = 0;
		m_namespaces = 0;
		m_processingInstructions = 0;
		m_texts = 0;
		m_other = 0;
		m_all = 0;
	}
	@Override
	public void execute() {
		N node = getTestNode();
	    while (node!=null) {
/*	    	
	        switch(getModel().getNodeKind(node))
	        {
			case ATTRIBUTE:
				m_attributes++;
				break;
			case COMMENT:
				m_comments++;
				break;
			case DOCUMENT:
				m_documents++;
				break;
			case ELEMENT:
				m_elements++;
				break;
			case NAMESPACE:
				m_namespaces++;
				break;
			case PROCESSING_INSTRUCTION:
				m_processingInstructions++;
				break;
			case TEXT:
				m_texts++;
				break;
	        default:
	        	m_other++;
	        	break;
	        }
*/
	    	m_all++;
	        if(getModel().hasChildren(node))
	        {
		        node = getModel().getFirstChild(node);
	        } 
	        else {
	        	while (getModel().getNextSibling(node) == null && node != getTestNode())
	        	{
	        		node = getModel().getParent(node);
	        	}
		        node = getModel().getNextSibling(node);
		      } 
	    } 
	}
	@Override
	public Iterable<String> iterativeTeardown() {
		ArrayList<String> retval = new ArrayList<String>();
/*		
       	if(m_attributes > 0)             retval.add("att      count = " + Long.toString(m_attributes));
       	if(m_comments > 0)               retval.add("comment  count = " + Long.toString(m_comments));
       	if(m_documents > 0)              retval.add("document count = " + Long.toString(m_documents));
       	if(m_elements > 0)               retval.add("element  count = " + Long.toString(m_elements));
       	if(m_namespaces > 0)             retval.add("ns       count = " + Long.toString(m_namespaces));
       	if(m_processingInstructions > 0) retval.add("pi       count = " + Long.toString(m_processingInstructions));
       	if(m_texts > 0)                  retval.add("text     count = " + Long.toString(m_texts));
       	if(m_other > 0)                  retval.add("other    count = " + Long.toString(m_other));
*/       	
       	if(m_all > 0)                  retval.add("node count = " + Long.toString(m_all));
		return retval; 
	}
}
