/*******************************************************************************
 * Copyright (c) 2002-2005 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.wsi.internal.core.log;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.wsi.internal.core.ToolInfo;
import org.eclipse.wst.wsi.internal.core.WSIConstants;
import org.eclipse.wst.wsi.internal.core.common.AddStyleSheet;
import org.eclipse.wst.wsi.internal.core.common.impl.AddStyleSheetImpl;
import org.eclipse.wst.wsi.internal.core.document.DocumentFactory;
import org.eclipse.wst.wsi.internal.core.log.impl.LogImpl;
import org.eclipse.wst.wsi.internal.core.log.impl.LogWriterImpl;
import org.eclipse.wst.wsi.internal.core.log.impl.MessageEntryImpl;
import org.eclipse.wst.wsi.internal.core.monitor.config.Comment;
import org.eclipse.wst.wsi.internal.core.monitor.config.ManInTheMiddle;
import org.eclipse.wst.wsi.internal.core.monitor.config.MonitorConfig;
import org.eclipse.wst.wsi.internal.core.monitor.config.impl.CommentImpl;
import org.eclipse.wst.wsi.internal.core.monitor.config.impl.ManInTheMiddleImpl;
import org.eclipse.wst.wsi.internal.core.util.Utils;

import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.TimeZone;

/**
 * Given a list of RequestHandlers, this class builds a 
 * WS-I compliant Message Log file.
 * 
 * @author David Lauzon, IBM
 */
public class LogBuilder
{
  public static final String copyright = "(c) Copyright IBM Corporation 2000, 2002.";

  /**
   * IDs to identify requests and corresponding responses.
   */
  protected int conversationId = 1;

  /**
   * IDs to uniquely identify each and every message within the log file.
   */
  protected int id = 1;

  /**
   * The actual log object.
   */
  protected Log log = null;

  /**
   * The log file.
   */
  protected IFile ifile;

  /**
   * Tool information property values.
   */
  private static final String TOOL_NAME = "Monitor";
  private static final String TOOL_VERSION = "1.0";
  private static final String TOOL_RELEASE_DATE = "2003-03-20";
  private static final String TOOL_IMPLEMENTER = "IBM";
  private static final String TOOL_LOCATION = "";

  /**
   * Common timestamp format.
   */
  public static final SimpleDateFormat timestampFormat = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

  /**
   * Constructor.
   * @param ifile: the file handler for the log.
   */
  public LogBuilder(IFile ifile)
  {
    this.ifile = ifile;
  }

  /**
   * This builds and then returns a log based on the list of request-response pairs.
   * @param requestResponses: a list of messages in the form of request-response pairs.
   * @return a log based on a list of request-response pairs.
   */
  public Log buildLog(List requestResponses)
  {
    log = new LogImpl();
    logMonitorInformation();

    // log the messages
    for (Iterator i = requestResponses.iterator(); i.hasNext();)
    {
      RequestHandler rr = (RequestHandler)i.next();
      if (rr != null) {
          try {
              logRequestResponse(rr);
          } catch (Exception e) {
              /* Empty catch block.  This request/response pair is not a valid SOAP
               * message, so skip it. */
          }
      }
    }
    return log;
  }

  /**
   * Currently a no-op.
   */
  public void logMonitorInformation()
  {
  }

  /**
   * Log the request-response pair.
   *@param rr: a request-response pair.
   */
  protected void logRequestResponse(RequestHandler rr)
  {
    if (rr != null)
    {
      byte[] requestHeaderBytes = rr.getRequestHeader();
      byte[] responseHeaderBytes = rr.getResponseHeader();
 
      if ((requestHeaderBytes != null) || (responseHeaderBytes != null))
      {
        long timestamp = rr.getDate().getTime();
        String localHostAndPort = "localhost:" + rr.getLocalPort();
        String remoteHostAndPort = rr.getRemoteHost() + ":" + rr.getRemotePort();

        int conversationId = getNextAvailableConversationId();
        try
        {
          if (requestHeaderBytes != null)
          {
            String requestHeader = new String(requestHeaderBytes);
            byte[] unchunkedRequestBody = rr.getRequestContent();
            int requestId  = getNextAvailableId();
            
            MessageEntry messageEntryRequest = createMessageEntry(requestId, conversationId, 
                MessageEntry.TYPE_REQUEST, timestamp, localHostAndPort,
                remoteHostAndPort, unchunkedRequestBody, requestHeader);

            if (messageEntryRequest != null)
            {
              log.addLogEntry(messageEntryRequest);
            }
          }
          if (responseHeaderBytes != null)
          {
            String responseHeader = new String(responseHeaderBytes);
            byte[] unchunkedResponseBody = rr.getResponseContent();
            int responseId = getNextAvailableId();
            
            MessageEntry messageEntryResponse = createMessageEntry(responseId, conversationId, 
                MessageEntry.TYPE_RESPONSE, timestamp + rr.getResponseTime(), remoteHostAndPort,
                localHostAndPort, unchunkedResponseBody, responseHeader);
 
            if (messageEntryResponse != null)
            {
              log.addLogEntry(messageEntryResponse);
            }
          }
        }
        catch (Exception e)
        {
          // ignore the request response pair
        }
      }
    }
  }

  /**
   * Returns true if the content of the message has at least
   * one left and one right bracket.
   * @param message: a message content.
   * @return true if the content of the message has at least
   *         one left and one right bracket.
   */
  public boolean isMessageWithBrackets(String message)
  {
    return ((message != null) && 
            (message.indexOf("<")!= -1) && 
            (message.indexOf(">") != -1));
  }
  /**
   * Returns the header of a message.
   * @param requestOrResponse: a message.
   * @param headerLength: the length of the header in the message.
   * @return the header of a message.
   */ 
  protected String getHeader(byte[] requestOrResponse,  int headerLength)
  {
    String result = null;
    
    if ((requestOrResponse != null) && 
        (headerLength > 0) && 
        (requestOrResponse.length >= headerLength))
    {
      byte[] header = new byte[headerLength];
      System.arraycopy(requestOrResponse, 0, header, 0, headerLength);
      result = new String(header);
    }
    return result;
  }

  /**
   * Returns the body of a message.
   * @param requestOrResponse: a message.
   * @param headerLength: the length of the header in the message.
   * @return the body of a message.
   */ 
  protected String getBody(byte[] requestOrResponse,  int headerLength)
  {
    String result = null;
    
    if ((requestOrResponse != null) && 
        (headerLength > 0) && 
        (requestOrResponse.length > headerLength))
    {
      int bodyLength = requestOrResponse.length - headerLength;
      byte[] body = new byte[bodyLength];
      System.arraycopy(requestOrResponse, headerLength, body, 0, bodyLength);
      result = new String(body);
    }
    return result;
  }
      
  /**
   * Create a log entry.
   * @param id: unique message id within the log.
   * @param conversationId: conversation id to identify request-response pairs.
   * @param type: type indicating a request or response.
   * @param timestamp: the date and time of the message.
   * @param senderHostAndPort: the host and port of the sender.
   * @param receiverHostAndPort: the host and port of the receiver.
   * @param messageContent: the content or body of the message.
   * @param header: the header of the message.
   * @return a log entry.
   */
  protected MessageEntry createMessageEntry(int id, int conversationId, String type, long timestamp, 
        String senderHostAndPort, String receiverHostAndPort, byte[] messageContent, String header) 
  {
 	// Create log entry
    MessageEntry messageEntry = new MessageEntryImpl();
    messageEntry.setId(String.valueOf(id));
    messageEntry.setConversationId(String.valueOf(conversationId));
    messageEntry.setType(type);
    messageEntry.setTimestamp(getTimestamp(new Date(timestamp)));
    messageEntry.setSenderHostAndPort(senderHostAndPort);
    messageEntry.setReceiverHostAndPort(receiverHostAndPort);
    messageEntry.setEncoding(WSIConstants.DEFAULT_XML_ENCODING);
    
    messageEntry.setHTTPHeaders(header);

    if (Utils.isMultipartRelatedMessage(header))
    {
    	MimeParts mimeParts = Utils.parseMultipartRelatedMessage(messageContent, header, WSIConstants.DEFAULT_XML_ENCODING);
    	if (mimeParts == null)
    	{
    	  // problem creating Mimeparts -- treat it as simple SOAP message
    	  if (messageContent != null)
    	  {
    	    messageEntry.setMessage(new String(messageContent));
    	  }
    	  else
    	  {
    	    messageEntry.setMessage("");
    	  }
    	  messageEntry.setMimeContent(false);
    	}
    	else
    	{
          messageEntry.setMimeParts(mimeParts);
          messageEntry.setMimeContent(true);
    	}
    }
    else
    {
      // Get the message content
      if (messageContent != null)
      {
        messageEntry.setMessage(new String(messageContent));
      }
      else
      {
        messageEntry.setMessage("");
      }
      messageEntry.setMimeContent(false);
    }
    return messageEntry;
  }

  /**
   * Returns the next available id, then increments the id value.
   * @return the next available id.
   */
  protected int getNextAvailableId()
  {
    return id++;
  }

  /**
   * Returns the next available conversation id, then increments the conversation id value.
   * A conversation id identfies a request and its corresponding response.
   * @return the next available converasationid.
   */
  protected int getNextAvailableConversationId()
  {
    return conversationId++;
  }

  public int getHeaderLength(Integer obj)
  {
    int result = 0;
    if (obj != null)
    {
      result = obj.intValue();
    }
    return result;
  }

  /**
   * Write log out to file.
   * @param log: log to be written to file.
   */

  public void writeLog(Log log)
  {
    try
    {
      // Get log writer
      LogWriter logWriter = new LogWriterImpl();
    
      logWriter.setWriter(ifile.getLocation().toString());
      
      // Write start of log file
      logWriter.write(new StringReader(log.getStartXMLString("")));
    
      // Write monitor tool information
      String monitorInfo = generateMonitorToolInfo();
      logWriter.write(new StringReader(monitorInfo));

      for (int i=0; i<log.getEntryCount(); i++)
      {
        MessageEntry me = log.getLogEntry(i);
        logWriter.write(new StringReader(me.toXMLString("")));
      }
      logWriter.write(new StringReader(log.getEndXMLString("")));
      logWriter.close();
    }
    catch (Exception e)
    {
      System.out.println("Exception thrown when printing log file.");
    }
  }


  /**
   * Returns XML string representation of the Monitor tool.
   * @return XML string representation of the Monitor tool.
   */
  protected String generateMonitorToolInfo()
  {

    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw); 

    try
    {
      // Tool information
      ToolInfo toolInfo = new ToolInfo(TOOL_NAME, TOOL_VERSION, TOOL_RELEASE_DATE, TOOL_IMPLEMENTER, TOOL_LOCATION);

      DocumentFactory documentFactory = DocumentFactory.newInstance();
      MonitorConfig monitorConfig = documentFactory.newMonitorConfig();
    
      Comment comment = new CommentImpl();
      comment.setText("Comment");
      monitorConfig.setComment(comment);
      monitorConfig.setLogLocation("URL");
      monitorConfig.setReplaceLog(true);

      AddStyleSheet addStyleSheet = new AddStyleSheetImpl();
      monitorConfig.setAddStyleSheet(addStyleSheet);

      monitorConfig.setLogDuration(600);
      monitorConfig.setTimeout(3);
     
      ManInTheMiddle manInTheMiddle = new ManInTheMiddleImpl();
      monitorConfig.setManInTheMiddle(manInTheMiddle);

      monitorConfig.setLocation("documentURI");
      monitorConfig.setVerboseOption(false);

      // Start     
      pw.print(toolInfo.getStartXMLString(""));
    
      // Config
      pw.print(monitorConfig.toXMLString(WSIConstants.NS_NAME_WSI_MONITOR_CONFIG));

      // End
      pw.println(toolInfo.getEndXMLString(""));
    }
    catch (Exception e)
    {
    }
    return sw.toString();
  }

  /**
   * Get the given date and time as a timestamp.
   * @param date: a date object.
   * @return the given date and time as a timestamp.
   */
  public static String getTimestamp(Date date)
  {
    timestampFormat.setTimeZone(TimeZone.getDefault());
    return timestampFormat.format(date);
  }
}
