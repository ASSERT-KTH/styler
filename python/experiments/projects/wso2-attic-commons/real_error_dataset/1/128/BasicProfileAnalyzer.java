/*******************************************************************************
 * Copyright (c) 2002, 2009 IBM Corporation, Parasoft, Beacon Information Technology Inc. and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM      - Initial API and implementation
 *   Parasoft - Initial API and implementation
 *   BeaconIT - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.wsi.internal.core.analyzer;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import javax.wsdl.Binding;
import javax.wsdl.WSDLException;

import org.eclipse.wst.wsi.internal.WSITestToolsProperties;
import org.eclipse.wst.wsi.internal.core.ToolInfo;
import org.eclipse.wst.wsi.internal.core.WSIConstants;
import org.eclipse.wst.wsi.internal.core.WSIException;
import org.eclipse.wst.wsi.internal.core.WSIFileNotFoundException;
import org.eclipse.wst.wsi.internal.core.analyzer.config.UDDIReference;
import org.eclipse.wst.wsi.internal.core.analyzer.config.WSDLElement;
import org.eclipse.wst.wsi.internal.core.analyzer.config.impl.WSDLElementImpl;
import org.eclipse.wst.wsi.internal.core.profile.validator.BaseValidator;
import org.eclipse.wst.wsi.internal.core.profile.validator.WSDLValidator;
import org.eclipse.wst.wsi.internal.core.profile.validator.impl.wsdl.WSDLValidatorImpl;
import org.eclipse.wst.wsi.internal.core.report.AssertionResult;
import org.eclipse.wst.wsi.internal.core.report.Report;
import org.eclipse.wst.wsi.internal.core.report.ReportArtifact;
import org.eclipse.wst.wsi.internal.core.report.ReportContext;
import org.eclipse.wst.wsi.internal.core.report.ReportWriter;
import org.eclipse.wst.wsi.internal.core.report.impl.DefaultReporter;
import org.eclipse.wst.wsi.internal.core.util.ArtifactType;
import org.eclipse.wst.wsi.internal.core.util.UDDIUtils;
import org.eclipse.wst.wsi.internal.core.util.WSIProperties;
import org.eclipse.wst.wsi.internal.core.wsdl.WSDLDocument;
import org.eclipse.wst.wsi.internal.core.wsdl.WSDLUtils;
import org.eclipse.wst.wsi.internal.core.xml.XMLDocumentCache;
import org.uddi4j.client.UDDIProxy;
import org.uddi4j.datatype.binding.BindingTemplate;
import org.uddi4j.datatype.tmodel.TModel;
import org.uddi4j.response.BindingDetail;
import org.uddi4j.response.TModelDetail;
import org.uddi4j.transport.TransportFactory;


/**
 * Analyzes log files to confirm conformance to a profile.
 *
 * @version 1.0.1
 * @author Jim Clune
 * @author Peter Brittenham
 * @author Graham Turrell
 */
public class BasicProfileAnalyzer extends Analyzer
{
  /**
   * Tool information.
   */
  public static final String TOOL_NAME = "Analyzer";

  /**
   * WSDL document to analyze.
   */
  protected WSDLDocument wsdlDocument = null;
  // private WSDLValidator wsdlValidator;

  /**
   * Basic profile analyzer.
   * @param args command line arguments.
   * @throws WSIException if unable to create a Basic profile analyzer.
   */
  public BasicProfileAnalyzer(String[] args) throws WSIException
  {
    super(args, new ToolInfo(TOOL_NAME));
    new XMLDocumentCache();
  }

  /**
   * Basic profile analyzer.
   * @param args command line arguments.
   * @param validate flag for command line argument validation.
   * @throws WSIException if unable to create a Basic profile analyzer.
   */
  public BasicProfileAnalyzer(String[] args, boolean validate) throws WSIException
  {
    super(args, new ToolInfo(TOOL_NAME), validate);
    new XMLDocumentCache();
  }

  /**
   * Basic profile analyzer.
   * @param analyzerConfigList a list of configurations for the analyzer.
   * @throws WSIException if unable to create a Basic profile analyzer.
   */
  public BasicProfileAnalyzer(List analyzerConfigList) throws WSIException
  {
    super(analyzerConfigList, new ToolInfo(TOOL_NAME));
    new XMLDocumentCache();
  }

  /**
   * Basic profile analyzer.
   * @param analyzerConfigList a list of configurations for the analyzer.
   * @param wsdlURI a wsdl document location.
   * @throws WSIException if unable to create a Basic profile analyzer.
   */
  public BasicProfileAnalyzer(List analyzerConfigList, String wsdlURI)
    throws WSIException
  {
    super(analyzerConfigList, new ToolInfo(TOOL_NAME));
    new XMLDocumentCache();
    
    try
    {
      // Get the WSDL document
      if (wsdlURI != null)
        this.wsdlDocument = new WSDLDocument(wsdlURI);
    }

    catch (WSDLException we)
    {
      throw new WSIException(we.getMessage(), we);
    }

  }
  
  private WSDLDocument getWsdlFromUddi() {
      WSDLDocument wsdlDoc = null;
      try {
          UDDIProxy uddiProxy = new UDDIProxy();
          UDDIReference uddiReference = getAnalyzerConfig().getUDDIReference();
          uddiProxy.setInquiryURL(uddiReference.getInquiryURL());
          TModel tModel = null;

          if (uddiReference.getKeyType().equals(UDDIReference.BINDING_KEY)) {
              BindingDetail bindingDetail = uddiProxy.get_bindingDetail(
                      uddiReference.getKey());
              BindingTemplate bindingTemplate = (BindingTemplate) bindingDetail.
                      getBindingTemplateVector().elementAt(0);
              tModel = UDDIUtils.findTModel(uddiProxy, bindingTemplate, false);
          } else { // UDDIReference.TMODEL_KEY
              TModelDetail tModelDetail = uddiProxy.get_tModelDetail(
                          uddiReference.getKey());
              tModel = (TModel) tModelDetail.getTModelVector().elementAt(0);
          }

          String overviewURL = UDDIUtils.getOverviewURL(tModel);
          String wsdlURI = UDDIUtils.getWSDLLocation(overviewURL);
          wsdlDoc = new WSDLDocument(wsdlURI);

          /* TODO:  I refactored this code from BSP3001 but I'm not sure that
           * it's correct.  This overrides the <wsdlElement> child of
           * <uddiReference> from the config file, which never gets used.
           */
          // If the wsdlElement does not exist, then set it
          if (analyzerContext.getServiceReference().getWSDLElement() == null)
          {
              Binding binding = UDDIUtils.getBinding(overviewURL, wsdlDoc);
              String bindingName =
                  binding == null ? null : binding.getQName().getLocalPart();
                String namespace =
                  binding == null ? null : binding.getQName().getNamespaceURI();

            // Get WSDL binding from the overviewURL and set in analyzerContext
            WSDLElement wsdlElement = new WSDLElementImpl();
            wsdlElement.setName(bindingName);
            wsdlElement.setNamespace(namespace);
            wsdlElement.setType(WSDLValidator.TYPE_DESCRIPTION_BINDING);
            analyzerContext.getServiceReference().setWSDLElement(wsdlElement);
          }
          return wsdlDoc;

      } catch (Exception e) {
        /* If the wsdlDoc is set at the point of the exception, return it;
         * otherwise return null. */
        return wsdlDoc;
    }
  }
  
  
  /**
   * Process all conformance validation functions.
   * @return status code.
   * @throws WSIException if conformance validation process failed.
   */
  public int validateConformance() throws WSIException
  {
    int statusCode = 0;

    Report report = null;
    ReportArtifact reportArtifact = null;

    // Set up initial analyzer context based on entries in the config file
    this.analyzerContext =
      new AnalyzerContext(new ServiceReference(getAnalyzerConfig()));

    ReportWriter reportWriter = null;
    try
    {
      this.profileAssertions = WSITestToolsProperties.getProfileAssertions(
                getAnalyzerConfig().getTestAssertionsDocumentLocation());

      if (this.profileAssertions == null)
      {
        throw new WSIException(messageList.getMessage("config20",
       		   "The WS-I Test Assertion Document (TAD)document was either not found or could not be processed."));  
      }	

      // Create report from document factory
      report = documentFactory.newReport();
      report.setLocation(getAnalyzerConfig().getReportLocation());

      // Create report context
      ReportContext reportContext =
        new ReportContext(
          WSIConstants.DEFAULT_REPORT_TITLE,
          profileAssertions,
          this);
      report.setReportContext(reportContext);

      // Create report writer
      reportWriter = documentFactory.newReportWriter();
      // I18N: 2003.02.26 modified by K.Nakagome@BeaconIT 
      //reportWriter.setWriter(new FileWriter(analyzerConfig.getReportLocation()));
      reportWriter.setWriter(getAnalyzerConfig().getReportLocation());

      // Create reporter
      this.reporter = new DefaultReporter(report, reportWriter);

      // fetch WSDL if not set in constructor
      // First, attempt to get the WSDL URI from a UDDI tModel
      if ((wsdlDocument == null) && (getAnalyzerConfig().isUDDIReferenceSet()))
              wsdlDocument = getWsdlFromUddi();

      /* Next, try to use the WSDL settings from the config file.  If we can't
       * fetch that WSDL, and the config file is set up to test a WSDL, then the
       * validator fails here. */
      if ((wsdlDocument == null) && (getAnalyzerConfig().isWSDLReferenceSet()))
      {
          wsdlDocument = new WSDLDocument(getAnalyzerConfig().
                  getWSDLLocation());

          if (wsdlDocument == null)
              throw new WSIException(messageList.getMessage("config05",
                      "WSDL document was either not found or could not be " + 
                      "processed."));
      }
      
      /*
       * Only validate messages against a wsdl document if the wsdl document
       * does not contain soap 1.2 bindings. 
       */
	  if (WSDLUtils.isSOAP12WSDL(wsdlDocument) && getAnalyzerConfig().getLogLocation() != null)
		  getAnalyzerConfig().setWSDLReference(null);
	  else
		  analyzerContext.setWsdlDocument(wsdlDocument);  

      // Start writing report
      this.reporter.startReport();

      // Walk through the artifact elements from the TAD, validating each one
      profileAssertions.getArtifactList().keySet().iterator();
      for (Iterator i = profileAssertions.getArtifactList().keySet().iterator();
              i.hasNext(); ) {
          String artifactType = (String) i.next();
          // Set current artifact
          reportArtifact = setCurrentArtifact(ArtifactType.getArtifactType(
                  artifactType));
          validate(reportArtifact, factory.getValidatorForArtifact(
                  artifactType));
          this.reporter.endCurrentArtifact();
      }

      // Finish the conformance report
      reporter.finishReport();
    }
    catch (Exception e)
    {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);

      String message =
        messageList.getMessage(
          "error03",
          "The conformance validation process failed.");

      // Finish report
      if (reporter != null)
      {
        pw.println(message);
        pw.println(
          messageList.getMessage("exception01", "Exception: ")
            + e.getClass().getName());
        e.printStackTrace(pw);
        reporter.finishReportWithError(sw.toString());
      }

      if (e instanceof WSIException)
        throw (WSIException) e;
      else
        throw new WSIException(message, e);
    }
    finally 
    {
      if (reportWriter != null)
      {
        reportWriter.close();
      }
    }

    if (report != null)
    {
      statusCode =
        (report.getSummaryResult().equals(AssertionResult.RESULT_PASSED)
          ? 0
          : 1);
    }

    return statusCode;
  }

  /**
   * Run test assertions.
   */
  private void validate(ReportArtifact reportArtifact, BaseValidator validator)
          throws WSIException 
  {
    if (validator instanceof WSDLValidatorImpl)
	{
    	((WSDLValidatorImpl)validator).init(analyzerContext, profileAssertions, reportArtifact, getAnalyzerConfig(), reporter,
    			getAnalyzerConfigIndex() == 0);
	}
    else
    {
      validator.init(analyzerContext, profileAssertions, reportArtifact, getAnalyzerConfig(), reporter);
    }

    if (validator.runTests()) {
        validator.validateArtifact();
        validator.cleanup();
    } 
    else 
    {
      validator.setAllMissingInput();
    }
  }

  /**
   * Command line interface for the analyzer tool.
   * @param args command line arguments.
   * @throws IOException if IO problems occur.
   */
  public static void main(String[] args) throws IOException
  {
    int statusCode = 0;
    Analyzer analyzer = null;

    try
    {
      // Set document builder factory class
      System.setProperty(
        WSIProperties.PROP_JAXP_DOCUMENT_FACTORY,
        WSIProperties.getProperty(WSIProperties.PROP_JAXP_DOCUMENT_FACTORY));

      // Set the system property for UDDI4J transport
      System.setProperty(
        TransportFactory.PROPERTY_NAME,
        WSIProperties.getProperty(TransportFactory.PROPERTY_NAME));

      // Create the analyzer object
      analyzer = new BasicProfileAnalyzer(args);

      // Have it process the conformance validation functions
      statusCode = analyzer.validateConformance();

      // Display message
      analyzer.printMessage(
        "created01",
        null,
        "Conformance report has been created.");
    }

    catch (Exception e)
    {
      statusCode = 1;

      String messageID;
      String defaultMessage;
      String messageData;

      if ((e instanceof WSIFileNotFoundException)
        || (e instanceof IllegalArgumentException))
      {
        //printStackTrace = false;
        messageID = "error01";
        defaultMessage = "Analyzer Error:";
        messageData = e.getMessage();
      }

      else
      {
        //printStackTrace = true;
        messageID = "error02";
        defaultMessage = "Analyzer Stopped By Exception:";
        messageData = e.toString();
      }

      if (analyzer != null)
        analyzer.printMessage(messageID, messageData, defaultMessage);
      else
        Analyzer.staticPrintMessage(messageID, messageData, defaultMessage);

      if (analyzer != null
        && analyzer.getAnalyzerConfig() != null
        && analyzer.getAnalyzerConfig().getVerboseOption())
        dump(e);
    }

    // Exit
    System.exit(statusCode);
  }

  /**
   * Set current artifact.
   * @param artifactType an ArtifactType object.
   * @return a ReportArtifact object.
   * @throws WSIException if problems creating report artifact.
   */
  protected ReportArtifact setCurrentArtifact(ArtifactType artifactType)
    throws WSIException
  {
    // Create artifact
    ReportArtifact reportArtifact = reporter.createArtifact();
    reportArtifact.setType(artifactType);

    // Add artifact to report
    this.reporter.setCurrentArtifact(reportArtifact);

    return reportArtifact;
  }

  /**
   * Print exception.
   * @param t a Throwable object.
   */
  public static void dump(Throwable t)
  {
    while (t instanceof WSIException)
    {
      Throwable nested = ((WSIException) t).getTargetException();
      if (nested == null)
        break;
      else
        t = nested;
    }
    t.printStackTrace();
  }
}
