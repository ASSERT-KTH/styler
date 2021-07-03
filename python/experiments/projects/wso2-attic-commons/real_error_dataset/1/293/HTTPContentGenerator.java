/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.wsdl.binding.http.internal.generator;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.wst.wsdl.Binding;
import org.eclipse.wst.wsdl.BindingFault;
import org.eclipse.wst.wsdl.BindingInput;
import org.eclipse.wst.wsdl.BindingOperation;
import org.eclipse.wst.wsdl.BindingOutput;
import org.eclipse.wst.wsdl.ExtensibilityElement;
import org.eclipse.wst.wsdl.Fault;
import org.eclipse.wst.wsdl.Input;
import org.eclipse.wst.wsdl.Operation;
import org.eclipse.wst.wsdl.Output;
import org.eclipse.wst.wsdl.Port;
import org.eclipse.wst.wsdl.PortType;
import org.eclipse.wst.wsdl.binding.http.HTTPAddress;
import org.eclipse.wst.wsdl.binding.http.HTTPBinding;
import org.eclipse.wst.wsdl.binding.http.HTTPFactory;
import org.eclipse.wst.wsdl.binding.http.HTTPOperation;
import org.eclipse.wst.wsdl.binding.http.HTTPUrlEncoded;
import org.eclipse.wst.wsdl.internal.generator.ContentGenerator;
import org.w3c.dom.Element;


public class HTTPContentGenerator implements ContentGenerator
{
  public static final int VERB_POST = 0;

  public static final int VERB_GET = 1;

  public static final int VERB_NOT_SET = -1;

  private int verbOption = VERB_NOT_SET;

  protected String addressLocation = ContentGenerator.ADDRESS_LOCATION;

  protected final static String[] requiredNamespaces = { "http://schemas.xmlsoap.org/wsdl/mime/", "http://schemas.xmlsoap.org/wsdl/http/" };

  public void setVerb(int verb)
  {
    verbOption = verb;
  }

  public void setAddressLocation(String addressLocation)
  {
    this.addressLocation = addressLocation;
  }

  public String[] getRequiredNamespaces()
  {
    return requiredNamespaces;
  }

  public String getPreferredNamespacePrefix(String namespace)
  {
    if (namespace.equals("http://schemas.xmlsoap.org/wsdl/mime/"))
    {
      return "mime";
    }
    else if (namespace.equals("http://schemas.xmlsoap.org/wsdl/http/"))
    {
      return "http";
    }

    return "";
  }

  public void generatePortContent(Port port)
  {
    // We blow away any existing ExtensibilityElements/content before we generate it
    // Is it worth being smarter?  Look for matching content first and create those which aren't found????
    List removeList = new ArrayList(port.getEExtensibilityElements());
    removeExtensebilityElements(port.getEExtensibilityElements(), removeList);

    HTTPAddress httpAddress = HTTPFactory.eINSTANCE.createHTTPAddress();
    httpAddress.setLocationURI(addressLocation);
    port.addExtensibilityElement(httpAddress);
  }

  public void generateBindingContent(Binding binding, PortType portType)
  {
    // We blow away any existing ExtensibilityElements/content before we generate it
    // Is it worth being smarter?  Look for matching content first and create those which aren't found????
    List removeList = new ArrayList(binding.getEExtensibilityElements());
    removeExtensebilityElements(binding.getEExtensibilityElements(), removeList);

    HTTPBinding httpBinding = HTTPFactory.eINSTANCE.createHTTPBinding();
    httpBinding.setVerb(getVerbOption(binding) == VERB_POST ? "POST" : "GET");

    binding.addExtensibilityElement(httpBinding);
  }

  public void generateBindingOperationContent(BindingOperation bindingOperation, Operation operation)
  {
    // We blow away any existing ExtensibilityElements/content before we generate it
    // Is it worth being smarter?  Look for matching content first and create those which aren't found????
    List removeList = new ArrayList(bindingOperation.getEExtensibilityElements());
    removeExtensebilityElements(bindingOperation.getEExtensibilityElements(), removeList);

    HTTPOperation httpOperation = HTTPFactory.eINSTANCE.createHTTPOperation();
    httpOperation.setLocationURI("/" + operation.getName());
    bindingOperation.addExtensibilityElement(httpOperation);
  }

  public void generateBindingInputContent(BindingInput bindingInput, Input input)
  {
    // We blow away any existing ExtensibilityElements/content before we generate it
    // Is it worth being smarter?  Look for matching content first and create those which aren't found????
    List removeList = new ArrayList(bindingInput.getEExtensibilityElements());

    // hack: we're being sneaky here. Set the 'verb' options now.
    int option = getVerbOption(bindingInput);
    removeExtensebilityElements(bindingInput.getEExtensibilityElements(), removeList);

    if (option == VERB_POST)
    {
      // TODO: Is there an equivalent HTTP Model Object for this?....
      Element element = createElement(bindingInput.getElement(), "mime", "content");
      element.setAttribute("type", "application/x-www-form-urlencoded");
    }
    else
    {
      HTTPUrlEncoded urlEncoded = HTTPFactory.eINSTANCE.createHTTPUrlEncoded();
      bindingInput.addExtensibilityElement(urlEncoded);
    }
  }

  public void generateBindingOutputContent(BindingOutput bindingOutput, Output output)
  {
    // We blow away any existing ExtensibilityElements/content before we generate it
    // Is it worth being smarter?  Look for matching content first and create those which aren't found????
    List removeList = new ArrayList(bindingOutput.getEExtensibilityElements());

    // hack: we're being sneaky here. Set the 'verb' options now.
    getVerbOption(bindingOutput);
    removeExtensebilityElements(bindingOutput.getEExtensibilityElements(), removeList);

    // TODO: Is there an equivalent HTTP Model Object for this?....
    Element bindingOutputElement = bindingOutput.getElement();
    Element element = createElement(bindingOutputElement, "mime", "content");
    element.setAttribute("type", "text/xml");
  }

  public void generateBindingFaultContent(BindingFault bindingFault, Fault fault)
  {
    //TODO!!
  }

  protected Element createElement(Element parentElement, String prefix, String elementName)
  {
    String name = prefix != null ? (prefix + ":" + elementName) : elementName;
    Element result = parentElement.getOwnerDocument().createElement(name);
    parentElement.insertBefore(result, parentElement.getFirstChild());
    //    parentElement.appendChild(result);
    return result;
  }

  /////////////////////  
  private int getVerbOption(Object genericBindingObject)
  {
    if (verbOption == VERB_NOT_SET && genericBindingObject != null)
    {
      // init() was never called, try to determine the 'verb' based on what we have/know
      Binding binding = getBindingObject(genericBindingObject);

      // Try to determine the verb from the Binding Object
      if (binding != null)
      {
        List list = binding.getEExtensibilityElements();
        Iterator valuesIt = getExtensibilityElementAttributeValue(list, "verb").iterator();

        while (valuesIt.hasNext())
        {
          String verb = (String)valuesIt.next();

          if (verb.equals("POST"))
          {
            verbOption = VERB_POST;
          }
          else if (verb.equals("GET"))
          {
            verbOption = VERB_GET;
          }

          if (verbOption != VERB_NOT_SET)
          {
            break;
          }
        }
      }
    }

    if (verbOption == VERB_NOT_SET)
    {
      verbOption = VERB_GET;
    }

    return verbOption;
  }

  private List getExtensibilityElementAttributeValue(List eeList, String attributeKey)
  {
    List values = new ArrayList();
    Iterator eeElementsIt = eeList.iterator();

    while (eeElementsIt.hasNext())
    {
      ExtensibilityElement eeElement = (ExtensibilityElement)eeElementsIt.next();
      String attributeValue = eeElement.getElement().getAttribute(attributeKey);
      if (attributeValue != null && !attributeValue.equals(""))
      {
        values.add(attributeValue);
      }
    }

    return values;
  }

  private Binding getBindingObject(Object genericBindingObject)
  {
    Object parent = genericBindingObject;

    int index = 0;
    while (parent != null && index < 5)
    {
      parent = getGenericBindingObjectParent(parent);
      if (parent instanceof Binding)
      {
        break;
      }
      index++;
    }

    return (parent instanceof Binding) ? (Binding)parent : null;
  }

  private Object getGenericBindingObjectParent(Object genericBindingObject)
  {
    Object parent = null;

    if (genericBindingObject != null)
    {
      if (genericBindingObject instanceof BindingOperation)
      {
        parent = ((BindingOperation)genericBindingObject).getContainer();
      }
      else if (genericBindingObject instanceof BindingInput)
      {
        parent = ((BindingInput)genericBindingObject).getContainer();
      }
      else if (genericBindingObject instanceof BindingOutput)
      {
        parent = ((BindingOutput)genericBindingObject).getContainer();
      }
      else if (genericBindingObject instanceof BindingFault)
      {
        parent = ((BindingFault)genericBindingObject).getContainer();
      }
    }

    return parent;
  }

  private void removeExtensebilityElements(List originalList, List removeList)
  {
    Iterator removeIt = removeList.iterator();
    while (removeIt.hasNext())
    {
      originalList.remove(removeIt.next());
    }
  }

  public String getProtocol()
  {
    return "HTTP";
  }
}
