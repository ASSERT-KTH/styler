/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.wsdl.validation.internal.wsdl11;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.wsdl.Import;
import javax.wsdl.WSDLException;

import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xerces.parsers.StandardParserConfiguration;
import org.apache.xerces.xni.XNIException;
import org.eclipse.wst.wsdl.validation.internal.util.MessageGenerator;
import org.eclipse.wst.wsdl.validation.internal.xml.LineNumberDOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.ibm.wsdl.DefinitionImpl;
import com.ibm.wsdl.util.StringUtils;

/**
 * A WSDL reader that supports cyclic WSDL imports, schema imports and inline schemas.
 * This reader is based on the WSDLReaderImpl from WSDL4J. 
 */
public class WSDLReaderImpl
{
  protected MessageGenerator messagegenerator;
  protected IWSDL11ValidationInfo wsdlvalinfo;
  
  /**
   * Constructor.
   * 
   * @param wsdlvalinfo The WSDL 1.1 validation info object to use.
   */
  public WSDLReaderImpl(IWSDL11ValidationInfo wsdlvalinfo)
  {
    this.wsdlvalinfo = wsdlvalinfo;
  }
  
  /**
   * Parse the root document. This method will find all imports and parse them as 
   * well creating a WSDLDocument for each unique WSDL file. This method supports
   * cyclic WSDL import statements such that file A can import file B and file B
   * can import file A.
   * 
   * @param documentBaseURI The base URI of the root document.
   * @param defEl The definition element of the root document.
   * @return An array of WSDLDocuments containing all of the unique files in the description.
   * @throws WSDLException
   */
  protected WSDLDocument[] parseDocument(String documentBaseURI, Element defEl) throws WSDLException
  {
    int initialImportArraySize = 20;
    List[] filesAtDepth = new ArrayList[initialImportArraySize];
    Map filesImporting = new Hashtable();
    SortedSet parsedImports = new TreeSet();
    SortedSet importsToParse = new TreeSet();
    int maxdepth = 0;

    WSDLDocument rootdoc = new WSDLDocument(documentBaseURI, defEl, 0, messagegenerator, wsdlvalinfo);
    String targetNamespace = rootdoc.getDefinition().getTargetNamespace();
    ImportHolder rootImport = new ImportHolder(targetNamespace, documentBaseURI, documentBaseURI, rootdoc, 0, null, messagegenerator, wsdlvalinfo);
    rootImport.createWSDLImport(rootdoc);
    parsedImports.add(rootImport);
    List rootList = new ArrayList();
    filesImporting.put(rootImport.getLocation(), new ArrayList());
    rootList.add(rootdoc);
    filesAtDepth[0] = rootList;
    importsToParse.addAll(rootdoc.getImports());
    Set imps = rootdoc.getImports();
    Iterator impIter = imps.iterator();
    while(impIter.hasNext())
    {
      ImportHolder imp = (ImportHolder)impIter.next();
      List tempList = new ArrayList();
      tempList.add(imp.getImportingDocument());
      filesImporting.put(imp.getLocation(), tempList);
    }
    
    while(!importsToParse.isEmpty())
    {
      ImportHolder imp = (ImportHolder)importsToParse.first();
      // It's important to initialize the import here so each import
      // is only created once. In the case of reciprical imports this
      // avoids an infinite loop.
      imp.initialize();
      WSDLDocument impDoc = imp.getWSDLDocument();
      
      importsToParse.remove(imp);
      
      parsedImports.add(imp);
      
      // Add new imports to the list of imports to parse.
      // Remove all the imports that have already been parsed.
      if(impDoc != null)
      {
        // Increate import array if necessary.
        if(imp.getDepth() >= initialImportArraySize)
        {
          List[] tempArray = new List[filesAtDepth.length + initialImportArraySize];
          System.arraycopy(filesAtDepth, 0, tempArray, 0, filesAtDepth.length);
          filesAtDepth = tempArray;
        }
        // Create the list for the depth if necessary.
        int impDepth = imp.getDepth();
        if(filesAtDepth[impDepth] == null)
        {
          if(maxdepth < impDepth)
          {
            maxdepth = impDepth;
          }
          filesAtDepth[impDepth] = new ArrayList();
        }
        filesAtDepth[imp.getDepth()].add(impDoc);
        
        Set imports = impDoc.getImports();
        ImportHolder[] importsArray = (ImportHolder[])imports.toArray(new ImportHolder[imports.size()]);
        for(int i = 0; i < importsArray.length; i++)
        {
          ImportHolder ih = importsArray[i];
          // If already parsed, add the definition importing this file to the list.
          if(filesImporting.containsKey(ih.getLocation()))
          {
            ((List)filesImporting.get(ih.getLocation())).add(ih.getImportingDocument());
          }
          // Otherwise add it to the list to parse.
          else
          {
            // Add this import to the list of files importing list.
            List tempList = new ArrayList();
            tempList.add(ih.getImportingDocument());
            filesImporting.put(ih.getLocation(), tempList);
            importsToParse.add(ih);
          }
        }
      }
    }
    
    // Add all of the imports to the respective documents.
    Iterator importElementsIter = parsedImports.iterator();
    while(importElementsIter.hasNext())
    {
      ImportHolder imp = (ImportHolder)importElementsIter.next();
      List files = (List)filesImporting.get(imp.getLocation());
      Iterator filesIter = files.iterator();
      while(filesIter.hasNext())
      {
        WSDLDocument doc = (WSDLDocument)filesIter.next();
        
        DefinitionImpl def = (DefinitionImpl)doc.getDefinition();
        Import impElem = imp.getImport();
        if(impElem != null)
        {
          def.addImport(impElem);
          if(!imp.isWSDLFileImport())
          {
            doc.addSchemas(imp.getSchemas());
          }
        }
        
      }
    }
    
    // Parse the WSDL documents.
    // Parse the Messages.
    for(int i = maxdepth; i >=0; i--)
    {
      List docs = filesAtDepth[i];
      Iterator docsIter = docs.iterator();
      while(docsIter.hasNext())
      {
        WSDLDocument doc = (WSDLDocument)docsIter.next();
        doc.parseMessages();
      }
    }
    // Parse the Porttypes.
    for(int i = maxdepth; i >=0; i--)
    {
      List docs = filesAtDepth[i];
      Iterator docsIter = docs.iterator();
      while(docsIter.hasNext())
      {
        WSDLDocument doc = (WSDLDocument)docsIter.next();
        doc.parsePorttypes();
      }
    }
    // Parse the Bindings.
    for(int i = maxdepth; i >=0; i--)
    {
      List docs = filesAtDepth[i];
      Iterator docsIter = docs.iterator();
      while(docsIter.hasNext())
      {
        WSDLDocument doc = (WSDLDocument)docsIter.next();
        doc.parseBindings();
      }
    }
    // Parse the Services.
    for(int i = maxdepth; i >=0; i--)
    {
      List docs = filesAtDepth[i];
      Iterator docsIter = docs.iterator();
      while(docsIter.hasNext())
      {
        WSDLDocument doc = (WSDLDocument)docsIter.next();
        doc.parseServices();
      }
    }
    // Parse the Extensibility Elements.
    for(int i = maxdepth; i >=0; i--)
    {
      List docs = filesAtDepth[i];
      Iterator docsIter = docs.iterator();
      while(docsIter.hasNext())
      {
        WSDLDocument doc = (WSDLDocument)docsIter.next();
        doc.parseExtensibilityElements();
      }
    }
    
    List wsdlDocs = new ArrayList();
    for(int i = maxdepth; i >=0; i--)
    {
      List docs = filesAtDepth[i];
      Iterator docsIter = docs.iterator();
      while(docsIter.hasNext())
      {
        WSDLDocument doc = (WSDLDocument)docsIter.next();
        wsdlDocs.add(doc);
      }
    }
    
    return (WSDLDocument[])wsdlDocs.toArray(new WSDLDocument[wsdlDocs.size()]);
  }
  
  /**
   * Get the WSDL document.
   * 
   * @param inputSource The source of the document being retrieved.
   * @param desc The description of the document being retrieved.
   * @return The WSDL document.
   * @throws WSDLException
   */
  public static Document getDocument(InputSource inputSource, String desc) throws WSDLException
  {
    try
    {
      StandardParserConfiguration configuration = new StandardParserConfiguration()
      {
        protected XMLErrorReporter createErrorReporter()
        {
          return new XMLErrorReporter()
          {
            public void reportError(String domain, String key, Object[] arguments, short severity) throws XNIException
            {
              boolean reportError = true;
              if (key.equals("PrematureEOF"))
               {         
                reportError = false;
              }

              if (reportError)
               {
                super.reportError(domain, key, arguments, severity);
              }
            }
          };
        }
      };
      
      ErrorHandler errorHandler = new ErrorHandler()
      {
            /* (non-Javadoc)
           * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
           */
          public void error(SAXParseException exception) throws SAXException
          {
            // TODO Auto-generated method stub

          }
          /* (non-Javadoc)
           * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
           */
          public void fatalError(SAXParseException exception) throws SAXException
          {
            // TODO Auto-generated method stub

          }
          /* (non-Javadoc)
           * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
           */
          public void warning(SAXParseException exception) throws SAXException
          {
            // TODO Auto-generated method stub

          }
      };

      DOMParser builder = new LineNumberDOMParser(configuration);
      builder.setErrorHandler(errorHandler);
      builder.parse(inputSource);
      Document doc = builder.getDocument();

      return doc;
    }
    catch (Throwable t)
    {
      throw new WSDLException(WSDLException.PARSER_ERROR, "Problem parsing '" + desc + "'.", t);
    }
  }

  /**
   * Read a WSDL document using a context URI and file URI.
   * 
   * @param contextURI The context URI to use.
   * @param wsdlURI The WSDL URI to use.
   * @return An array of WSDLDocuments.
   * @throws WSDLException
   */
  public WSDLDocument[] readWSDL(String contextURI, String wsdlURI) throws WSDLException
  {
    try
    {
      URL contextURL = (contextURI != null) ? StringUtils.getURL(null, contextURI) : null;
      URL url = StringUtils.getURL(contextURL, wsdlURI);
      InputStream reader = StringUtils.getContentAsInputStream(url);
      InputSource inputSource = new InputSource(reader);
      Document doc = getDocument(inputSource, wsdlURI);
      reader.close();
      WSDLDocument[] wsdlDocs = null;
      // only parse the document if it isn't empty
      if(doc.getDocumentElement() != null)
       {      
        wsdlDocs = readWSDL(url.toString(), doc);
      }
      return wsdlDocs;
    }
    catch (WSDLException e)
    {
      throw e;
    }
    catch (Throwable t)
    {
      throw new WSDLException(
        WSDLException.OTHER_ERROR,
        "Unable to resolve imported document at '" + wsdlURI + "'.",
        t);
    }
  }

  /**
   * Set the messagegenerator for the reader.
   * 
   * @param mg The message generator to set.
   */
  public void setMessageGenerator(MessageGenerator mg)
  {
    messagegenerator = mg;
  }
  
  /**
   * Read the WSDL document accessible via the specified
   * URI into a WSDL definition.
   *
   * @param wsdlURI A URI pointing to a WSDL file.
   * @return An array of WSDLDocuments.
   */
  public WSDLDocument[] readWSDL(String wsdlURI) throws WSDLException
  {
    return readWSDL(null, wsdlURI);
  }

  /**
   * Read the WSDL document described by a URI and its definitions element.
   * 
   * @param documentBaseURI The URI of the WSDL document.
   * @param definitionsElement The definitions element for the WSDL document.
   * @return An array of WSDLDocuments.
   * @throws WSDLException
   */
  protected WSDLDocument[] readWSDL(String documentBaseURI,
                                Element definitionsElement)
                                  throws WSDLException
  {
    return parseDocument(documentBaseURI, definitionsElement);
  }

  /**
   * Read the specified WSDL document.
   *
   * @param documentBaseURI The document base URI.
   * @param wsdlDocument The WSDL document.
   * @return An array of WSDLDocuments.
   */
  public WSDLDocument[] readWSDL(String documentBaseURI, Document wsdlDocument)
    throws WSDLException
  {
    return readWSDL(documentBaseURI, wsdlDocument.getDocumentElement());
  }
  
  
  
}

