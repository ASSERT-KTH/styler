/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.wsdl.binding.soap.internal.impl;


import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.wst.wsdl.binding.soap.SOAPFactory;
import org.eclipse.wst.wsdl.binding.soap.SOAPHeader;
import org.eclipse.wst.wsdl.binding.soap.SOAPHeaderFault;
import org.eclipse.wst.wsdl.binding.soap.SOAPPackage;
import org.eclipse.wst.wsdl.binding.soap.internal.util.SOAPConstants;
import org.w3c.dom.Element;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Header</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.wst.wsdl.binding.soap.internal.impl.SOAPHeaderImpl#getHeaderFaults <em>Header Faults</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SOAPHeaderImpl extends SOAPHeaderBaseImpl implements SOAPHeader
{
  /**
   * This class is not intended to be serialized.
   * serialVersionUID is assigned with 1L to avoid
   * compiler warning messages.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The cached value of the '{@link #getHeaderFaults() <em>Header Faults</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getHeaderFaults()
   * @generated
   * @ordered
   */
  protected EList headerFaults;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected SOAPHeaderImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected EClass eStaticClass()
  {
    return SOAPPackage.Literals.SOAP_HEADER;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getHeaderFaults()
  {
    if (headerFaults == null)
    {
      headerFaults = new EObjectContainmentEList(SOAPHeaderFault.class, this, SOAPPackage.SOAP_HEADER__HEADER_FAULTS);
    }
    return headerFaults;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public List getSOAPHeaderFaults()
  {
    return getHeaderFaults();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public void addSOAPHeaderFault(javax.wsdl.extensions.soap.SOAPHeaderFault soapHeaderFault)
  {
    EList headerFaults = getHeaderFaults();
    headerFaults.add(soapHeaderFault);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case SOAPPackage.SOAP_HEADER__HEADER_FAULTS:
      return ((InternalEList)getHeaderFaults()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case SOAPPackage.SOAP_HEADER__HEADER_FAULTS:
      return getHeaderFaults();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case SOAPPackage.SOAP_HEADER__HEADER_FAULTS:
      getHeaderFaults().clear();
      getHeaderFaults().addAll((Collection)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case SOAPPackage.SOAP_HEADER__HEADER_FAULTS:
      getHeaderFaults().clear();
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case SOAPPackage.SOAP_HEADER__HEADER_FAULTS:
      return headerFaults != null && !headerFaults.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  public void handleUnreconciledElement(Element child, Collection remainingModelObjects)
  {
    if (SOAPConstants.HEADER_FAULT_ELEMENT_TAG.equals(child.getLocalName()))
    {
      SOAPHeaderFault fault = SOAPFactory.eINSTANCE.createSOAPHeaderFault();
      fault.setEnclosingDefinition(getEnclosingDefinition());
      fault.setElement(child);
      getHeaderFaults().add(fault);
    }
  }

  protected void handleReconciliation(Collection remainingModelObjects)
  {
    for (Iterator i = remainingModelObjects.iterator(); i.hasNext();)
    {
      remove(this, i.next());
    }
  }

  protected void remove(Object component, Object modelObject)
  {
    SOAPHeader header = (SOAPHeader)component;
    if (modelObject instanceof SOAPHeaderFault)
    {
      header.getHeaderFaults().remove(modelObject);
    }
  }

  public QName getElementType()
  {
    if (elementType == null)
      elementType = new QName(SOAPConstants.SOAP_NAMESPACE_URI, SOAPConstants.HEADER_ELEMENT_TAG);
    return elementType;
  }
} //SOAPHeaderImpl
