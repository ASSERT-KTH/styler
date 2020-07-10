/**
 * Copyright (c) 2010 TIBCO Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.genxdm.samples.performance;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.samples.performance.bridges.BaseBridgePerfTest;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class Performance {
	private static final String CSV_OUTPUT = "-csv";
	
	public static void main(String[] args)
	{
		if(args.length < 1)
		{
			System.out.println("No performance input file specified.");
			System.exit(0);
		}
		// Let each test variation execute setup, so that we don't have a failure
		// on the last run & waste time.
		ArrayList<Performance> pList = new ArrayList<Performance>();
		
		boolean csvOut = false;
		try
		{
			for(String arg : args)
			{
				if(CSV_OUTPUT.equalsIgnoreCase(arg))
				{
					csvOut = true;
				}
				else
				{
					pList.add(new Performance(arg));
				}
			}
			for(Performance p : pList)
			{
				p.runTests(csvOut);
			}
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static final Boolean DEBUG = false;
	private String m_name;
	private int m_reportSubtaskLevels = 2;
	private boolean m_reportMemoryUsage = false;
	private final String m_inputFilename;
	private final Iterable<Module> m_modules;
	private final ArrayList<String> m_bridges = new ArrayList<String>();

	public Performance(String inputFile) throws SAXException, IOException {
		m_inputFilename = inputFile;
		m_modules = configure(inputFile, m_bridges);
	}
	/**
	 * Performs the tests.
	 * 
	 * @throws Exception because any uncaught exception should stop the test
	 */
	public final void runTests(boolean csvOut) throws Exception {
		
	    TaskTimer ttTotal = new TaskTimer(m_name, m_reportMemoryUsage);
	    ttTotal.addNote("property file = " + m_inputFilename);
	    ttTotal.addNote("timestamp = " + new Timestamp(System.currentTimeMillis()).toString());
	    
	    for(String bridgeFactoryClassName : m_bridges)
	    {
			for(Module module : m_modules)
			{
				//System.out.println("Module: " + module.getName() + ": cnt = " + module.getTestConfig().mi_cnt);
			    //ttTotal.addNote("module: " + module.getName());
			    TaskTimer ttModule = ttTotal.newChild(module.getName());
			    
				for(PerfTestWrapper testWrapper : module.getTestWrappers())
				{
					PerfTest test = testWrapper.getTest();
					//System.out.println("   test: " + test.getName() + ": input = " + testWrapper.mi_props.get("document"));
					
					try 
					{
						// Provide the specific bridge factory here.
						testWrapper.getProperties().put(BaseBridgePerfTest.BRIDGE_FACTORY_CLASS, bridgeFactoryClassName);
					    test.initialSetup(testWrapper.getProperties());
					    
					    TaskTimer ttBridge = ttModule.newChild(test.getBridgeName() + "." + test.getBridgeVersion());
					    TaskTimer ttTest = ttBridge.newChild(test.getTestName());
					    if(testWrapper.getTestConfig().mi_excludeOneRun)
					    {
					    	test.iterativeSetup();
					    	test.execute();
					    	test.iterativeTeardown();
					    }
					    for(int icnt = 0; icnt < module.getTestConfig().mi_cnt; icnt++)
					    {
					    	TaskTimer ttRun = ttTest.newChild("run[" + icnt + "]");
					    	test.iterativeSetup();
					        ttRun.startTimer();
					        test.execute();
					        ttRun.stopTimer();
					    	Iterable<String> notes = test.iterativeTeardown();
					    	if(notes != null)
					    	{
					    		for(String note : notes)
					    		{
					    			ttRun.addNote(note);
					    		}
					    	}
					    }
					}
					catch (UnsupportedOperationException ex)
					{
						System.out.println(ex.getMessage());
					}
					finally
					{
					    test.finalTeardown();
					}
				}
			}
	    }
		if(csvOut)
		{
			ttTotal.setPrintTimeUnits(false);
			System.out.println(ttTotal.toCsvMillis(m_reportSubtaskLevels));
		}
		else
		{
			System.out.println("----------------------------------------------------");
			System.out.println(ttTotal.toPrettyStringMillis("", m_reportSubtaskLevels));
			System.out.println("----------------------------------------------------");
		}
	}
	protected Iterable<Module> configure(String inputFile, List<String> bridges) throws SAXException, IOException
	{
		XMLReader parser = XMLReaderFactory.createXMLReader();
		ConfigParser cp = new ConfigParser(bridges);
		parser.setContentHandler(cp);
		parser.parse(new InputSource(inputFile));
		return cp.getModules();
	}
	class ConfigParser implements ContentHandler
	{
		public static final String PERFORMANCE_ELEM_NAME = "Performance";
		public static final String COUNT_ATT_NAME = "cnt";
		public static final String EXCLUDE_ONE_RUN_ATT_NAME = "excludeOneRun";
		public static final String REPORT_SUBTASK_LEVELS_ATT_NAME = "reportSubtaskLevels";
		public static final String REPORT_MEM_USAGE_ATT_NAME = "reportMemoryUsage";
		
		public static final String MODULE_ELEM_NAME = "Module";
		public static final String BRIDGES_ELEM_NAME = "Bridges";
		public static final String BRIDGE_ELEM_NAME = "Bridge";
		public static final String BRIDGE_FACTORY_ATT_NAME = "factory";
		public static final String TEST_ELEM_NAME = "Test";
		public static final String PROP_ELEM_NAME = "Prop";
		public static final String CLASS_NAME_ATT_NAME = "className";
		public static final String NAME_ATT_NAME = "name";
		public static final String VALUE_ATT_NAME = "value";
		
		final ArrayList<Module> mi_modules = new ArrayList<Module>();
		private boolean isPerf = false;
		private boolean isModule = false;
		private boolean isBridges = false;
		private boolean isTest = false;
		private TestConfig mi_perfTestConfig;
		private final List<String> mi_bridges;
		
		public ConfigParser(List<String> bridges) {
			mi_bridges = bridges;
		}
		
		public Iterable<Module> getModules() 
		{
			return mi_modules;
		}
		private TestConfig createTestConfig(TestConfig backer, Attributes atts)
		{
			TestConfig tc = new TestConfig();
			
			String value = atts.getValue(COUNT_ATT_NAME);
			tc.mi_cnt = value != null ? Integer.parseInt(value) : backer.mi_cnt;

			value = atts.getValue(EXCLUDE_ONE_RUN_ATT_NAME);
			tc.mi_excludeOneRun = value != null ? Boolean.parseBoolean(value) : backer.mi_excludeOneRun;
			
			return tc;
		}
		@SuppressWarnings("unchecked") // for cast of object to List<Object>
		@Override
		public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
			if(isPerf)
			{
				if(isBridges)
				{
					if(BRIDGE_ELEM_NAME.equals(localName))
					{
						String factoryName = atts.getValue(BRIDGE_FACTORY_ATT_NAME);
						if(factoryName == null)
						{
							throw new SAXException("Invalid input: missing " + BRIDGE_FACTORY_ATT_NAME + " attribute.");
						}
						mi_bridges.add(factoryName);
						
					}
					else
					{
						throw new SAXException("Invalid input: missing " + BRIDGE_ELEM_NAME + " element.");
					}
				}
				else if(isModule)
				{
					if(isTest)
					{
						if(PROP_ELEM_NAME.equals(localName))
						{
							String propName = atts.getValue(NAME_ATT_NAME);
							String propValue = atts.getValue(VALUE_ATT_NAME);
							if(propName != null && propValue != null)
							{
								Module module = mi_modules.get(mi_modules.size() - 1);
								HashMap<String, Object> props = module.getLastPerfTestWrapper().getProperties();
								
								if(props.get(propName) instanceof String)
								{
									ArrayList<Object> list = new ArrayList<Object>();
									list.add(props.get(propName));
									list.add(propValue);
									props.put(propName, list);
								}
								else if(props.get(propName) instanceof List)
								{
									((List<Object>)props.get(propName)).add(propValue);
								}
								else
								{
									props.put(propName, propValue);
								}
							}
							else
							{
								throw new SAXException("Prop specification requires both name and value.");
							}
						}
						else
						{
							throw new SAXException("Invalid element: " + localName);
						}
					}
					else if(PROP_ELEM_NAME.equals(localName))
					{
						String propName = atts.getValue(NAME_ATT_NAME);
						String propValue = atts.getValue(VALUE_ATT_NAME);
						if(propName != null && propValue != null)
						{
							HashMap<String, Object> props = mi_modules.get(mi_modules.size() - 1).getProperties();
							if(props.get(propName) instanceof String)
							{
								ArrayList<Object> list = new ArrayList<Object>();
								list.add(props.get(propName));
								list.add(propValue);
								props.put(propName, list);
							}
							else if(props.get(propName) instanceof List)
							{
								((List<Object>)props.get(propName)).add(propValue);
							}
							else
							{
								props.put(propName, propValue);
							}
						}
						else
						{
							throw new SAXException("Prop specification requires both name and value.");
						}
					}
					else
					{
						if(TEST_ELEM_NAME.equals(localName))
						{
							isTest = true;
							Module mod = mi_modules.get(mi_modules.size() - 1);
							
							String testClassName = atts.getValue(CLASS_NAME_ATT_NAME);
							TestConfig tc = createTestConfig(mod.getTestConfig(), atts);
							
							try {
								Class<?> clazz = Performance.class.getClassLoader().loadClass(testClassName);
								PerfTest perfTest = (PerfTest)clazz.newInstance();
								PerfTestWrapper wrapper = new PerfTestWrapper(perfTest, tc);
								
								// Add the properties from the module to the PerfTestWrapper...
								HashMap<String,Object> modProps = mod.getProperties();
								HashMap<String,Object> props = wrapper.getProperties();
								
								for(String key : modProps.keySet())
								{
									props.put(key, modProps.get(key));
								}
								
								mod.addTestWrapper(wrapper);
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
							throw new SAXException("Invalid input: missing " + TEST_ELEM_NAME + " element.");
						}
					}
				}
				else
				{
					if(BRIDGES_ELEM_NAME.equals(localName))
					{
						isBridges = true;
					}
					else if(MODULE_ELEM_NAME.equals(localName))
					{
						isModule = true;
						isBridges = false;
						String modName = atts.getValue(NAME_ATT_NAME);
						TestConfig tc = createTestConfig(mi_perfTestConfig, atts);
						Module mod = new Module(modName, tc);
						mi_modules.add(mod);
					}
					else
					{
						throw new SAXException("Invalid input: missing " + MODULE_ELEM_NAME + " element.");
					}
				}
			}
			else
			{
				if(PERFORMANCE_ELEM_NAME.equals(localName))
				{
					isPerf = true;
					m_name = atts.getValue(NAME_ATT_NAME);
					
					String value = atts.getValue(REPORT_SUBTASK_LEVELS_ATT_NAME);
					if(value != null)
					{
						m_reportSubtaskLevels = Integer.parseInt(value);
					}

					value = atts.getValue(REPORT_MEM_USAGE_ATT_NAME);
					if(value != null)
					{
						m_reportMemoryUsage = Boolean.parseBoolean(value);
					}
					
					
					mi_perfTestConfig = createTestConfig(new TestConfig(), atts);
				}
				else
				{
					throw new SAXException("Invalid input: missing " + PERFORMANCE_ELEM_NAME + " root element.");
				}
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if(PERFORMANCE_ELEM_NAME.equals(localName))
			{
				isPerf = false;
			}
			else if(BRIDGES_ELEM_NAME.equals(localName))
			{
				isBridges = false;
			}
			else if(MODULE_ELEM_NAME.equals(localName))
			{
				isModule = false;
			}
			else if(TEST_ELEM_NAME.equals(localName))
			{
				isTest = false;
			}
		}
		
		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {}
		@Override
		public void setDocumentLocator(Locator locator) {}
		@Override
		public void startDocument() throws SAXException {}
		@Override
		public void endDocument() throws SAXException {}
		@Override
		public void startPrefixMapping(String prefix, String uri) throws SAXException {}
		@Override
		public void endPrefixMapping(String prefix) throws SAXException {}
		@Override
		public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {}
		@Override
		public void processingInstruction(String target, String data) throws SAXException {}
		@Override
		public void skippedEntity(String name) throws SAXException {}
	}
	class Module 
	{
		private final String mi_name;
		private final TestConfig mi_config;
		private final HashMap<String, Object> mi_props = new HashMap<String, Object>();
		private final ArrayList<PerfTestWrapper> mi_tests = new ArrayList<PerfTestWrapper>();
		public Module(String name, TestConfig tc) 
		{
			mi_name = PreCondition.assertArgumentNotNull(name);
			mi_config = PreCondition.assertArgumentNotNull(tc);
		}
		public String getName()
		{
			return mi_name;
		}
		public TestConfig getTestConfig()
		{
			return mi_config;
		}
		public void addTestWrapper(PerfTestWrapper testWrapper)
		{
			mi_tests.add(PreCondition.assertArgumentNotNull(testWrapper));
		}
		public PerfTestWrapper getLastPerfTestWrapper()
		{
			return mi_tests.get(mi_tests.size() - 1);
		}
		public Iterable<PerfTestWrapper> getTestWrappers()
		{
			return mi_tests;
		}
		public HashMap<String, Object> getProperties()
		{
			return mi_props;
		}
	}
	class PerfTestWrapper
	{
		private final PerfTest mi_test;
		private final TestConfig mi_config;
		private final HashMap<String, Object> mi_props = new HashMap<String, Object>();
		public PerfTestWrapper(PerfTest perfTest, TestConfig tc)
		{
			mi_test = PreCondition.assertArgumentNotNull(perfTest);
			mi_config = PreCondition.assertArgumentNotNull(tc);
		}
		public PerfTest getTest()
		{
			return mi_test;
		}
		public TestConfig getTestConfig()
		{
			return mi_config;
		}
		public HashMap<String, Object> getProperties()
		{
			return mi_props;
		}
		public PerfTestWrapper copy()
		{
			PerfTestWrapper retval = new PerfTestWrapper(mi_test, mi_config);
			return retval;
		}
	}
	class TestConfig
	{
		public int mi_cnt = 1;
		public boolean mi_excludeOneRun = true;
	}
}