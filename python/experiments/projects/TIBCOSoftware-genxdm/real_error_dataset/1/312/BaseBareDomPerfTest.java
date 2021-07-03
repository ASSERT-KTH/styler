package org.genxdm.samples.performance.dom;

import java.io.IOException;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.genxdm.samples.performance.PerfTest;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * This sample illustrates a simple, serialization.  
 * 
 * @param <N> the node type
 * @author jbaysdon
 */
abstract public class BaseBareDomPerfTest implements PerfTest
{
	public static final Boolean DEBUG = false;
	public static final String DOC_FILE_PROP_NAME = "bridge.document";
	public static final String SCHEMA_FILE_PROP_NAME = "schema";
	public static final String BASE_URI_PROP_NAME = "bridge.baseURI";

	protected String m_docFile;
	protected String m_baseURI;

	public static final Boolean COPY_TYPE_ANNOTATIONS = true;

	Node m_testNode;
	DocumentBuilderFactory m_dbf;
	DocumentBuilder m_builder;
    
	public BaseBareDomPerfTest()	{}

	@Override
	public String getBridgeName() {
		return "DOM.xerces";
	}
	@Override
	public String getBridgeVersion() {
		return "2.9.102";
	}
	
	@Override
	public void initialSetup(Map<String,Object> props) {
		m_docFile = (String)props.get(DOC_FILE_PROP_NAME);
//		if(m_docFile == null)
//		{
//			throw new IllegalStateException("Input document must be specified.");
//		}
		m_baseURI = (String)props.get(BASE_URI_PROP_NAME);
//		if(m_baseURI == null)
//		{
//			throw new IllegalStateException("Base URI must be specified.");
//		}
		// General setup
	    m_dbf = DocumentBuilderFactory.newInstance();
    	m_dbf.setNamespaceAware(true);
        try {
			m_builder = m_dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
	protected String getDocFile()
	{
		return m_docFile;
	}
	protected String getBaseURI()
	{
		return m_baseURI;
	}
	protected DocumentBuilder getDocBuilder()
	{
		return m_builder;
	}
	//-------------------------------------------------------------------------
	// PerfTest implementation classes.
	//-------------------------------------------------------------------------
	protected void setupTestNode()
	{
		if(m_docFile != null && m_docFile.length() > 0)
		{
			try {
				m_testNode = m_builder.parse(m_baseURI + "/" + m_docFile);
			} catch (IOException e) {
				throw new RuntimeException(e);
			} catch (SAXException e) {
				throw new RuntimeException(e);
			}
		}
	}
	//-------------------------------------------------------------------------
	// convenience methods
	//-------------------------------------------------------------------------
    public static boolean isNamespace(final Node node)
    {
        final short nodeType = node.getNodeType();
        if (nodeType == Node.ATTRIBUTE_NODE)
        {
            final String namespaceURI = node.getNamespaceURI();
            return XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI) ? true : false;
        }
        else
        {
            return false;
        }
    }
}
