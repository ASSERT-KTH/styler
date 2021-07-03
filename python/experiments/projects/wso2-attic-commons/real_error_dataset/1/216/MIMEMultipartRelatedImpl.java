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
package org.eclipse.wst.wsdl.binding.mime.internal.impl;


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
import org.eclipse.wst.wsdl.binding.mime.MIMEFactory;
import org.eclipse.wst.wsdl.binding.mime.MIMEMultipartRelated;
import org.eclipse.wst.wsdl.binding.mime.MIMEPackage;
import org.eclipse.wst.wsdl.binding.mime.MIMEPart;
import org.eclipse.wst.wsdl.binding.mime.internal.util.MIMEConstants;
import org.eclipse.wst.wsdl.internal.impl.ExtensibilityElementImpl;
import org.w3c.dom.Element;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Multipart Related</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.wst.wsdl.binding.mime.internal.impl.MIMEMultipartRelatedImpl#getEMIMEPart <em>EMIME Part</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MIMEMultipartRelatedImpl extends ExtensibilityElementImpl implements MIMEMultipartRelated
{
  /**
   * This class is not intended to be serialized.
   * serialVersionUID is assigned with 1L to avoid
   * compiler warning messages.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The cached value of the '{@link #getEMIMEPart() <em>EMIME Part</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEMIMEPart()
   * @generated
   * @ordered
   */
  protected EList eMIMEPart;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected MIMEMultipartRelatedImpl()
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
    return MIMEPackage.Literals.MIME_MULTIPART_RELATED;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getEMIMEPart()
  {
    if (eMIMEPart == null)
    {
      eMIMEPart = new EObjectContainmentEList(MIMEPart.class, this, MIMEPackage.MIME_MULTIPART_RELATED__EMIME_PART);
    }
    return eMIMEPart;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public void addMIMEPart(javax.wsdl.extensions.mime.MIMEPart mimePart)
  {
    getEMIMEPart().add(mimePart);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public List getMIMEParts()
  {
    return getEMIMEPart();
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
      case MIMEPackage.MIME_MULTIPART_RELATED__EMIME_PART:
      return ((InternalEList)getEMIMEPart()).basicRemove(otherEnd, msgs);
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
      case MIMEPackage.MIME_MULTIPART_RELATED__EMIME_PART:
      return getEMIMEPart();
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
      case MIMEPackage.MIME_MULTIPART_RELATED__EMIME_PART:
      getEMIMEPart().clear();
      getEMIMEPart().addAll((Collection)newValue);
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
      case MIMEPackage.MIME_MULTIPART_RELATED__EMIME_PART:
      getEMIMEPart().clear();
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
      case MIMEPackage.MIME_MULTIPART_RELATED__EMIME_PART:
      return eMIMEPart != null && !eMIMEPart.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  public void handleUnreconciledElement(Element child, Collection remainingModelObjects)
  {
    if (MIMEConstants.PART_ELEMENT_TAG.equals(child.getLocalName()))
    {
      MIMEPart mimePart = MIMEFactory.eINSTANCE.createMIMEPart();
      mimePart.setEnclosingDefinition(getEnclosingDefinition());
      mimePart.setElement(child);
      addMIMEPart(mimePart);
    }
  }

  public QName getElementType()
  {
    if (elementType == null)
      elementType = new QName(MIMEConstants.MIME_NAMESPACE_URI, MIMEConstants.MULTIPART_RELATED_ELEMENT_TAG);
    return elementType;
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.wsdl.internal.impl.ExtensibilityElementImpl#createElement()
   */
  public Element createElement()
  {
    Element newElement = super.createElement();

    Iterator iterator = getMIMEParts().iterator();
    while (iterator.hasNext())
    {
      Object obj = iterator.next();
      if (obj instanceof MIMEPart)
      {
        MIMEPart mimePart = (MIMEPart)obj;
        Element child = ((MIMEPartImpl)mimePart).createElement();
        newElement.appendChild(child);
      }
    }
    return newElement;
  }
} //MIMEMultipartRelatedImpl
