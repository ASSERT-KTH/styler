package org.genxdm.samples.performance.bridges;

import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.genxdm.Model;
import org.genxdm.ProcessingContext;
import org.genxdm.io.DocumentHandler;
import org.genxdm.io.FragmentBuilder;
import org.genxdm.io.Resolver;
import org.genxdm.samples.performance.VersionedProcessingContextFactory;

abstract public class BaseBridgePerfTest<N, A> implements BridgePerfTest<N, A> {

	public static final String DOC_FILE_PROP_NAME = "bridge.document";
	public static final String BASE_URI_PROP_NAME = "bridge.baseURI";
	public static final String BRIDGE_FACTORY_CLASS = "bridgeFactoryClass";
	private N m_testNode;
	private String m_baseURI;
	private Resolver m_resolver;
	private ProcessingContext<N> m_pcx;
	private VersionedProcessingContextFactory<N> m_versionedPcxFactory;
	private FragmentBuilder<N> m_docBuilder;
    private DocumentHandler<N> m_docHandler;
	private Model<N> m_model;
	private String m_docFilename;

	public BaseBridgePerfTest() {
	}
	
	@Override
	public Iterable<String> getRequiredFeatures() {
		return null;
	}
/*
	@Override
	public void setContext(ProcessingContext<N> pcx) {
		// Ensure that context supports the features we need.
		m_pcx = pcx;
		m_docBuilder = m_pcx.newFragmentBuilder();
		m_model = m_pcx.getModel();
		m_docHandler = m_pcx.newDocumentHandler();
	}
*/
	@SuppressWarnings("unchecked") // for cast of pcxFactory
    @Override
	public void initialSetup(Map<String,Object> props)	{
    	
		// Bridge injection.
		String pcxFactoryClassName = (String)props.get(BRIDGE_FACTORY_CLASS);
		if(pcxFactoryClassName != null)
		{
			try {
				Class<?> pcxFactoryClass = this.getClass().getClassLoader().loadClass(pcxFactoryClassName);
				m_versionedPcxFactory = (VersionedProcessingContextFactory<N>) pcxFactoryClass.newInstance();
				m_pcx = m_versionedPcxFactory.newProcessingContext();
				m_docBuilder = m_pcx.newFragmentBuilder();
				m_model = m_pcx.getModel();
				m_docHandler = m_pcx.newDocumentHandler();
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		else
		{
			throw new IllegalStateException("No mutable processing context factory specified.");
		}
		
		m_docFilename = (String)props.get(DOC_FILE_PROP_NAME);
		if(m_docFilename == null)
		{
			throw new IllegalStateException("Input document must be specified.");
		}
		
    	// Ensure that bridge has capabilities needed to run test.
		Iterable<String> reqFeatures = getRequiredFeatures();
		if(reqFeatures != null)
		{
			for(String feature : reqFeatures)
			{
				if(!getPcx().isSupported(feature))
				{
					throw new UnsupportedOperationException(getBridgeName() + " bridge does not support the " + feature + " feature which is required by the " + this.getClass().getName() + " test.");
				}
			}
		}
		
		m_baseURI = (String)props.get(BASE_URI_PROP_NAME);
		if(m_baseURI == null)
		{
			throw new IllegalStateException("Base URI must be specified.");
		}
		try {
			m_testNode = getDocHandler().parse(new FileReader(m_baseURI + "/" + m_docFilename), null);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public void finalTeardown()	{
		m_testNode = null;
		m_baseURI = null;
		m_resolver = null;
		m_pcx = null;
		m_versionedPcxFactory = null;
		m_docBuilder = null;
	    m_docHandler = null;
		m_model = null;
		m_docFilename = null;
	};
	

	public N getTestNode() 
	{
		return m_testNode;
	}
	public ProcessingContext<N> getPcx() 
	{
		return m_pcx;
	}
	public FragmentBuilder<N> getDocBuilder() 
	{
		return m_docBuilder;
	}
	public DocumentHandler<N> getDocHandler() 
	{
		return m_docHandler;
	}
	public Model<N> getModel() 
	{
		return m_model;
	}
	public String getBaseURI()
	{
		return m_baseURI;
	}
	public String getDocFilename()
	{
		return m_docFilename;
	}
	@Override
	public String getBridgeName() {
		if(m_versionedPcxFactory != null)
		{
			return m_versionedPcxFactory.getBridgeName();
		}
		return getPcx().toString();
	}
	@Override
	public String getBridgeVersion() {
		if(m_versionedPcxFactory != null)
		{
			return m_versionedPcxFactory.getBridgeVersion();
		}
		return "unknown";
	}
	protected Resolver getResolver()
	{
		if(m_resolver == null)
		{
			try {
				m_resolver = new SampleResolver(new URI(getBaseURI()));
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
		}
		return m_resolver;
	}
}