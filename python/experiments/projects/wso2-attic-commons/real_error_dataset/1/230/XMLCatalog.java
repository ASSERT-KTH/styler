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


package org.eclipse.wst.wsdl.validation.internal.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.xerces.jaxp.SAXParserFactoryImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * XMLCatalog This class can be used to register, obtain and delete an instance
 * of an XML catalog. Method definitions are provided for the catalog to set a
 * location in the catalog and resolve an entity from the catalog.
 */
public class XMLCatalog implements IXMLCatalog
{
  //private final static String _APACHE_FEATURE_CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";

  private final static String _APACHE_FEATURE_NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";

  private final static String _APACHE_FEATURE_NAMESPACES = "http://xml.org/sax/features/namespaces";

  //private final static String _APACHE_FEATURE_VALIDATION = "http://xml.org/sax/features/validation";

  //private final static String _APACHE_FEATURE_VALIDATION_SCHEMA = "http://apache.org/xml/features/validation/schema";
  
  private final static String FILE_PROTOCOL = "file:///";

  private static IXMLCatalog instance = null;

  private static String extxmlcatalogclass = null;

  private static ClassLoader extclassLoader = null;

  private static IXMLCatalog extXMLCatalogInstance = null;

  private static List schemadirs = new ArrayList();

  private static List entities = new ArrayList();

  /**
   * A hashtable to hold the XML catalog entries.
   */
  protected Map catalog = new Hashtable();

  /**
   * Return an instance of the XML catalog. Assigns all registered schemas to
   * the XML catalog.
   * 
   * @return The instance of the XML catalog.
   */
  public static IXMLCatalog getInstance()
  {
    if (instance == null)
    {
      instance = new XMLCatalog();

      // Add the registered entities to the catalog.
      Iterator entityIter = entities.iterator();
      while (entityIter.hasNext())
      {
        XMLCatalogEntityHolder entity = (XMLCatalogEntityHolder) entityIter.next();
        instance.addEntryToCatalog(entity.getPublicId(), entity.getSystemId());
      }

      // Add the schemas in the schema directories to the catalog.
      if (schemadirs.size() > 0)
      {
        SAXParser saxParser = null;
        try
        {
          SAXParserFactory parserfactory = new SAXParserFactoryImpl();
          parserfactory.setFeature(_APACHE_FEATURE_NAMESPACE_PREFIXES, true);
          parserfactory.setFeature(_APACHE_FEATURE_NAMESPACES, true);
          saxParser = parserfactory.newSAXParser();
        }
        catch (FactoryConfigurationError e)
        {
        }
        catch (SAXNotRecognizedException e)
        {
        }
        catch (ParserConfigurationException e)
        {
        }
        catch (SAXNotSupportedException e)
        {
        }
        catch (SAXException e)
        {
        }
        Iterator schemadirIter = schemadirs.iterator();
        SchemaNamespaceHandler handler = ((XMLCatalog) instance).new SchemaNamespaceHandler();
        while (schemadirIter.hasNext())
        {
          String schemadir = (String) schemadirIter.next();
          registerSchemasForDir(instance, schemadir, saxParser, handler);
        }
      }
    }
    return instance;
  }

  /**
   * Register the schemas in the given directory and all subdirectories with the
   * XML catalog.
   * 
   * @param catalog
   *          The catalog to register the schemas with.
   * @param schemadir
   *          The schema directory to search for schema files.
   * @param parser
   *          The SAXParser to use to parse the schemas for their
   *          targetNamespace.
   * @param handler
   *          The handler to use to get the targetNamespace.
   */
  private static void registerSchemasForDir(IXMLCatalog catalog, String schemadir, SAXParser parser,
      SchemaNamespaceHandler handler)
  {
    // Remove file: and file:/ from beginning of file location if they are present.
    if(schemadir.startsWith("file:"))
    {
      schemadir = schemadir.substring(5);
    }
    while(schemadir.startsWith("//"))
    {
      schemadir = schemadir.substring(1);
    }
    
    File dir = new File(schemadir);
    if (dir.isDirectory())
    {
      File[] files = dir.listFiles();
      int numfiles = files.length;
      for (int i = 0; i < numfiles; i++)
      {
        File tempfile = files[i];
        String tempfilepath = tempfile.getAbsolutePath();
        tempfilepath = tempfilepath.replace('\\','/');
        while(tempfilepath.startsWith("/"))
        {
          tempfilepath = tempfilepath.substring(1);
        }
        tempfilepath = FILE_PROTOCOL + tempfilepath;
        if (tempfile.isDirectory())
        {
          registerSchemasForDir(catalog, tempfilepath, parser, handler);
        } else
        {
          handler.reset();
          try
          {
            parser.parse(tempfilepath, handler);
          }
          catch (Exception e)
          {
            // TODO: log error.
          }
          String targetNamespace = handler.getTargetNamespace();
          if (targetNamespace != null)
          {
            catalog.addEntryToCatalog(targetNamespace, tempfilepath);
          }
        }
      }
    }
  }

  /**
   * Get the instance of the extension XML catalog. Returns the instance if one
   * is registered and can be created, null otherwise.
   * 
   * @return The instance of the extension XML catalog if one is registered,
   *         null otherwise.
   */
  public static IXMLCatalog getExtensionCatalogInstance()
  {
    if (extXMLCatalogInstance == null)
    {
      if (extxmlcatalogclass != null && extclassLoader != null)
      {
        try
        {
          Class catalogClass = extclassLoader != null ? extclassLoader.loadClass(extxmlcatalogclass) : Class
              .forName(extxmlcatalogclass);
          extXMLCatalogInstance = (IXMLCatalog) catalogClass.newInstance();
        }
        catch (Exception e)
        {
          //TODO: Log error
        }
      }
    }
    return extXMLCatalogInstance;
  }

  /**
   * Set the class of the XML catalog to be used.
   * 
   * @param xmlcatalog
   *          The class of the XML catalog to be used.
   * @param classloader
   *          The classloader to use to load the catalog.
   */
  public static void setExtensionXMLCatalog(String xmlcatalog, ClassLoader classloader)
  {
    extxmlcatalogclass = xmlcatalog;
    extclassLoader = classloader;
  }

  /**
   * Resets the instance of the XML catalog to null. For deleting the catalog if
   * necessary.
   */
  public static void reset()
  {
    instance = null;
    extxmlcatalogclass = null;
    extclassLoader = null;
    extXMLCatalogInstance = null;
    entities = new ArrayList();
    schemadirs = new ArrayList();
  }

  /**
   * Add a schema directory to be checked for schemas to register in the
   * catalog.
   * 
   * @param schemadir
   *          The directory to check for schemas.
   */
  public static void addSchemaDir(String schemadir)
  {
    schemadirs.add(schemadir);
  }

  /**
   * Add an entity to the catalog.
   * 
   * @param entity
   *          The entity to add to the catalog.
   */
  public static void addEntity(XMLCatalogEntityHolder entity)
  {
    entities.add(entity);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.wsdl.validate.internal.xml.IXMLCatalog#addEntryToCatalog(java.lang.String,
   *      java.lang.String)
   */
  public void addEntryToCatalog(String publicId, String systemId)
  {
    catalog.put(publicId, systemId);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.wsdl.validate.internal.xml.IXMLCatalog#resolveEntityLocation(java.lang.String,
   *      java.lang.String)
   */
  public String resolveEntityLocation(String publicId, String systemId)
  {
    String resolvedlocation = null;
    // First try to resolve using the ext catalog.
    IXMLCatalog extcatalog = getExtensionCatalogInstance();
    if (extcatalog != null)
    {
      resolvedlocation = extcatalog.resolveEntityLocation(publicId, systemId);
    }
    if (resolvedlocation == null)
    {
      // if there's no system id use the public id
      if (systemId == null || systemId.equals(""))
      {
        systemId = publicId;
      }
      resolvedlocation = (String) catalog.get(systemId);
    }
    return resolvedlocation;
  }

  /**
   * A handler used in parsing to get the targetNamespace string of a schema.
   */
  protected class SchemaNamespaceHandler extends DefaultHandler
  {
    private final String TARGET_NAMESPACE = "targetNamespace";

    private final String SCHEMA = "schema";

    private String targetNamespace = null;

    /**
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
     *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localname, String arg2, Attributes attributes) throws SAXException
    {
      if (localname.equals(SCHEMA))
      {

        int numAtts = attributes.getLength();
        for (int i = 0; i < numAtts; i++)
        {

          String attname = attributes.getQName(i);
          if (attname.equals(TARGET_NAMESPACE))
          {
            targetNamespace = attributes.getValue(i);
          }
        }

      }
      super.startElement(uri, localname, arg2, attributes);
    }

    /**
     * Return the targetNamespace found by parsing the file.
     * 
     * @return The targetNamespace found by parsing the file.
     */
    public String getTargetNamespace()
    {
      return targetNamespace;
    }

    /**
     * Reset the state of the handler so it can be reused.
     */
    public void reset()
    {
      targetNamespace = null;
    }
  }
}
