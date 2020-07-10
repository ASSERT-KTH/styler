package org.genxdm.samples.performance.dom;

import java.util.ArrayList;

import org.w3c.dom.Node;

public class TestNavigate extends BaseBareDomPerfTest {

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
		return getDocFile();
	}
	
	@Override
	public void iterativeSetup() {
		if(m_testNode == null)
		{
			setupTestNode();
		}
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
		Node node = m_testNode;
        while (node!=null) {
/*        	
	        switch(node.getNodeType())
	        {
			case Node.ATTRIBUTE_NODE:
				if(isNamespace(node))
					mi_namespaces++;
				else
					mi_attributes++;
				break;
			case Node.COMMENT_NODE:
				mi_comments++;
				break;
			case Node.DOCUMENT_NODE:
				mi_documents++;
				break;
			case Node.ELEMENT_NODE:
				mi_elements++;
				break;
			case Node.PROCESSING_INSTRUCTION_NODE:
				mi_processingInstructions++;
				break;
			case Node.TEXT_NODE:
				mi_texts++;
				break;
	        default:
	        	mi_other++;
	        	break;
	        }
*/	        
	        m_all++;
	        if(node.hasChildNodes())
	        {
		        node = node.getFirstChild();
	        } 
	        else {
	        	while (node.getNextSibling() == null && node != m_testNode)
	        	{
	        		node = node.getParentNode();
	        	}
		        node = node.getNextSibling();
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
	
	@Override
	public void finalTeardown() {};
}
