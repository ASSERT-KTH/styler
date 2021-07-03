/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.wsdl.validation.internal.eclipse;

import java.io.IOException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.wst.wsdl.validation.internal.Constants;
import org.eclipse.wst.wsdl.validation.internal.logging.LoggerFactory;
import org.eclipse.wst.wsdl.validation.internal.wsdl11.WSDL11ValidatorDelegate;
import org.eclipse.wst.wsdl.validation.internal.xml.XMLCatalog;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class required for eclipse.
 */
public class ValidateWSDLPlugin extends Plugin
{
  protected final String PROPERTIES_FILE = "validatewsdlui";
  protected static ValidateWSDLPlugin instance;
  protected ResourceBundle resourcebundle = null;
  protected ResourceBundle wsdlValidatorResourceBundle = null;

  /**
   * Constructor.
   */
  public ValidateWSDLPlugin()
  {
    super();
  }

  /* (non-Javadoc)
   * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
   */
  public void start(BundleContext context) throws Exception
  {
    super.start(context);
    instance = this;
    wsdlValidatorResourceBundle = ResourceBundle.getBundle(Constants.WSDL_VALIDATOR_PROPERTIES_FILE);
    resourcebundle = ResourceBundle.getBundle(PROPERTIES_FILE);
    
    LoggerFactory.getInstance().setLogger(new EclipseLogger());

    new WSDLValidatorPluginRegistryReader(
      "extvalidator",
      "extvalidator",
      WSDLValidatorPluginRegistryReader.EXT_VALIDATOR)
      .readRegistry();

    // register any WSDL 1.1 validators defined
    new WSDL11ValidatorPluginRegistryReader("wsdl11validator", "validator").readRegistry();
  }
  /* (non-Javadoc)
   * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
   */
  public void stop(BundleContext context) throws Exception
  {
    super.stop(context);
    XMLCatalog.reset();
  }
  
  /**
   * Return the instance of this plugin object.
   * 
   * @return the instance of this plugin object
   */
  public static ValidateWSDLPlugin getInstance()
  {
    return instance;
  }

  /**
   * Get the install URL of this plugin.
   * 
   * @return the install url of this plugin
   */
  public String getInstallURL()
  {
    try
    {
      return FileLocator.resolve(getBundle().getEntry("/")).getFile();
    }
    catch (IOException e)
    {
      return null;
    }
  }

  /*************************************************************
   * ResourceBundle helper methods
   * 
   *************************************************************/
  /**
   * Returns the resource bundle for this plugin.
   * 
   * @return the resource bundle for this plugin
   */
  public ResourceBundle getResourceBundle()
  {
    return resourcebundle;
  }

  /**
   * Returns the resource bundle for the WSDL validator.
   * 
   * @return the resource bundle for the WSDL validator
   */
  public ResourceBundle getWSDLValidatorResourceBundle()
  {
    return wsdlValidatorResourceBundle;
  }

  /**
   * Returns the string for the given id.
   * 
   * @param stringID - the id for the string
   * @return the string for the given id
   */
  public String getString(String stringID)
  {
    return getResourceBundle().getString(stringID);
  }
}

/**
 * This class reads the plugin manifests and registers each WSDLExtensionValidator
 */
class WSDLValidatorPluginRegistryReader
{
  protected static final String PLUGIN_ID = "org.eclipse.wst.wsdl.validation";
  protected static final String ATT_CLASS = "class";
  protected static final String ATT_NAMESPACE = "namespace";
  protected static final int WSDL_VALIDATOR = 0;
  protected static final int EXT_VALIDATOR = 1;
  protected String extensionPointId;
  protected String tagName;
  protected int validatorType;

  /**
   * 
   */
  public WSDLValidatorPluginRegistryReader(String extensionPointId, String tagName, int validatorType)
  {
    this.extensionPointId = extensionPointId;
    this.tagName = tagName;
    this.validatorType = validatorType;
  }

  /**
   * read from plugin registry and parse it.
   */
  public void readRegistry()
  {
    IExtensionRegistry pluginRegistry = Platform.getExtensionRegistry();
    IExtensionPoint point = pluginRegistry.getExtensionPoint(PLUGIN_ID, extensionPointId);
    if (point != null)
    {
      IConfigurationElement[] elements = point.getConfigurationElements();
      for (int i = 0; i < elements.length; i++)
      {
        readElement(elements[i]);
      }
    }
  }

  /**
   * readElement() - parse and deal with an extension like:
   *
   * <extension point="org.eclipse.validate.wsdl.WSDLExtensionValidator"
   *            id="soapValidator"
   *            name="SOAP Validator">>
   *   <validator>
   *        <run class=" org.eclipse.validate.wsdl.soap.SOAPValidator"/>
   *   </validator>
   *   <attribute name="namespace" value="http://schemas.xmlsoap.org/wsdl/soap/"/>
   * </extension>
   */
  protected void readElement(IConfigurationElement element)
  {
    if (element.getName().equals(tagName))
    {
      String validatorClass = element.getAttribute(ATT_CLASS);
      String namespace = element.getAttribute(ATT_NAMESPACE);

      if (validatorClass != null)
      {
        try
        {
          //          ClassLoader pluginLoader =
          //            element.getDeclaringExtension().getDeclaringPluginDescriptor().getPlugin().getClass().getClassLoader();
          //				modified to resolve certain situations where the plugin has not been initialized

          Bundle pluginBundle = Platform.getBundle(element.getDeclaringExtension().getContributor().getName());
          
          if (validatorType == EXT_VALIDATOR)
           {
            EclipseWSDLValidatorDelegate delegate = new EclipseWSDLValidatorDelegate(validatorClass, pluginBundle);
            WSDLValidator.getInstance().registerWSDLExtensionValidator(namespace, delegate);
          }
        }
        catch (Exception e)
        {
        }
      }
    }
  }
}

/**
 * Read WSDl 1.1 extension validators.
 * 
 *  <extension
 *     point="com.ibm.etools.validation.validator"
 *     id="wsdlValidator"
 *     name="%_UI_WSDL_VALIDATOR">
 *    <wsdl11validator
 *       namespace="http://schemas.xmlsoap.org/wsdl/soap/"
 *       class="org.eclipse.wsdl.validate.soap.wsdl11.SOAPValidator"
 *       resourcebundle="validatewsdlsoap"/>
 *   </extension>
 *  
 */
class WSDL11ValidatorPluginRegistryReader
{
  protected static final String PLUGIN_ID = "org.eclipse.wst.wsdl.validation";
  protected static final String ATT_CLASS = "class";
  protected static final String ATT_NAMESPACE = "namespace";
  protected String extensionPointId;
  protected String tagName;

  /**
   * Constructor.
   * 
   * @param extensionPointId - the id of the extension point
   * @param tagName - the tag name of the extension point
   */
  public WSDL11ValidatorPluginRegistryReader(String extensionPointId, String tagName)
  {
    this.extensionPointId = extensionPointId;
    this.tagName = tagName;
  }

  /**
   * Read from plugin registry and handle the configuration elements that match
   * the spedified elements.
   */
  public void readRegistry()
  {
    IExtensionRegistry pluginRegistry = Platform.getExtensionRegistry();
    IExtensionPoint point = pluginRegistry.getExtensionPoint(PLUGIN_ID, extensionPointId);
    if (point != null)
    {
      IConfigurationElement[] elements = point.getConfigurationElements();
      for (int i = 0; i < elements.length; i++)
      {
        readElement(elements[i]);
      }
    }
  }

  /**
   * Parse and deal with the extension points.
   * 
   * @param element The extension point element.
   */
  protected void readElement(IConfigurationElement element)
  {
    if (element.getName().equals(tagName))
    {
      String validatorClass = element.getAttribute(ATT_CLASS);
      String namespace = element.getAttribute(ATT_NAMESPACE);

      if (validatorClass != null && namespace != null)
      {
        try
        {
          Bundle pluginBundle = Platform.getBundle(element.getDeclaringExtension().getContributor().getName());
          WSDL11ValidatorDelegate delegate = new EclipseWSDL11ValidatorDelegate(validatorClass, pluginBundle);
          WSDLValidator.getInstance().registerWSDL11Validator(namespace, delegate);
        }
        catch (Exception e)
        {
        }
      }
    }
  }
}




