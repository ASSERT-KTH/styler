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
package org.eclipse.wst.wsdl.internal.impl;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.wst.wsdl.Binding;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.ExtensibilityElement;
import org.eclipse.wst.wsdl.Port;
import org.eclipse.wst.wsdl.WSDLPackage;
import org.eclipse.wst.wsdl.util.WSDLConstants;
import org.w3c.dom.Element;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Port</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.wst.wsdl.internal.impl.PortImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.wst.wsdl.internal.impl.PortImpl#getEBinding <em>EBinding</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class PortImpl extends ExtensibleElementImpl implements Port
{
  /**
   * This class is not intended to be serialized.
   * serialVersionUID is assigned with 1L to avoid
   * compiler warning messages.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * The cached value of the '{@link #getEBinding() <em>EBinding</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEBinding()
   * @generated
   * @ordered
   */
  protected Binding eBinding;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected PortImpl()
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
    return WSDLPackage.Literals.PORT;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setName(String newName)
  {
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, WSDLPackage.PORT__NAME, oldName, name));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Binding getEBinding()
  {
    if (eBinding != null && eBinding.eIsProxy())
    {
      InternalEObject oldEBinding = (InternalEObject)eBinding;
      eBinding = (Binding)eResolveProxy(oldEBinding);
      if (eBinding != oldEBinding)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, WSDLPackage.PORT__EBINDING, oldEBinding, eBinding));
      }
    }
    return eBinding;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Binding basicGetEBinding()
  {
    return eBinding;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setEBinding(Binding newEBinding)
  {
    Binding oldEBinding = eBinding;
    eBinding = newEBinding;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, WSDLPackage.PORT__EBINDING, oldEBinding, eBinding));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public javax.wsdl.Binding getBinding()
  {
    return getEBinding();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public void setBinding(javax.wsdl.Binding binding)
  {
    setEBinding((Binding)binding);
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
      case WSDLPackage.PORT__NAME:
      return getName();
      case WSDLPackage.PORT__EBINDING:
      if (resolve)
        return getEBinding();
      return basicGetEBinding();
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
      case WSDLPackage.PORT__NAME:
      setName((String)newValue);
      return;
      case WSDLPackage.PORT__EBINDING:
      setEBinding((Binding)newValue);
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
      case WSDLPackage.PORT__NAME:
      setName(NAME_EDEFAULT);
      return;
      case WSDLPackage.PORT__EBINDING:
      setEBinding((Binding)null);
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
      case WSDLPackage.PORT__NAME:
      return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case WSDLPackage.PORT__EBINDING:
      return eBinding != null;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String toString()
  {
    if (eIsProxy())
      return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (name: "); //$NON-NLS-1$
    result.append(name);
    result.append(')');
    return result.toString();
  }

  //
  // Reconcile methods
  //

  public void reconcileAttributes(Element changedElement)
  {
    setName(changedElement.getAttribute("name"));
    reconcileReferences(false);
  }

  public void handleUnreconciledElement(Element child, Collection remainingModelObjects)
  {
    super.handleUnreconciledElement(child, remainingModelObjects);
  }

  public Collection getModelObjects(Object component)
  {
    Port port = (Port)component;
    List list = new ArrayList();
    list.addAll(port.getEExtensibilityElements());
    return list;
  }

  //
  // For reconciliation: Model -> DOM
  //

  protected void changeAttribute(EAttribute eAttribute)
  {
    if (isReconciling)
      return;

    super.changeAttribute(eAttribute);
    Element theElement = getElement();
    if (theElement != null)
    {
      if (eAttribute == null || eAttribute == WSDLPackage.Literals.PORT__NAME)
        niceSetAttribute(theElement, WSDLConstants.NAME_ATTRIBUTE, getName());
    }
  }

  protected void changeReference(EReference eReference)
  {
    if (isReconciling)
      return;

    super.changeReference(eReference);
    Element theElement = getElement();
    if (theElement != null)
    {
      if (eReference == null || eReference == WSDLPackage.Literals.PORT__EBINDING)
      {
        Binding binding = getEBinding();
        if (binding != null)
        {
          QName qName = binding.getQName();
          niceSetAttributeURIValue(theElement, WSDLConstants.BINDING_ATTRIBUTE, qName.getNamespaceURI() + "#" + qName.getLocalPart());
        }
      }
    }
  }

  protected Element createElement()
  {
    Element newElement = createElement(WSDLConstants.PORT);
    setElement(newElement);

    Iterator iterator = getExtensibilityElements().iterator();
    while (iterator.hasNext())
    {
      ExtensibilityElement extensibilityElement = (ExtensibilityElement)iterator.next();
      Element child = ((ExtensibilityElementImpl)extensibilityElement).createElement();
      newElement.appendChild(child);
    }
    return newElement;
  }

  public void reconcileReferences(boolean deep)
  {
    if (element != null)
    {
      Definition definition = getEnclosingDefinition();
      QName bindingQName = createQName(definition, element.getAttribute(WSDLConstants.BINDING_ATTRIBUTE), element);
      Binding newBinding = (bindingQName != null) ? (Binding)definition.getBinding(bindingQName) : null;
      if (newBinding != getEBinding())
      {
        setEBinding(newBinding);
      }
    }
    super.reconcileReferences(deep);
  }

} //PortImpl
