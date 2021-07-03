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

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.wst.wsdl.BindingOperation;
import org.eclipse.wst.wsdl.BindingOutput;
import org.eclipse.wst.wsdl.ExtensibilityElement;
import org.eclipse.wst.wsdl.Operation;
import org.eclipse.wst.wsdl.Output;
import org.eclipse.wst.wsdl.WSDLPackage;
import org.eclipse.wst.wsdl.util.WSDLConstants;
import org.w3c.dom.Element;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Binding Output</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.wst.wsdl.internal.impl.BindingOutputImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.wst.wsdl.internal.impl.BindingOutputImpl#getEOutput <em>EOutput</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class BindingOutputImpl extends ExtensibleElementImpl implements BindingOutput
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
   * The cached value of the '{@link #getEOutput() <em>EOutput</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEOutput()
   * @generated
   * @ordered
   */
  protected Output eOutput;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected BindingOutputImpl()
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
    return WSDLPackage.Literals.BINDING_OUTPUT;
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
      eNotify(new ENotificationImpl(this, Notification.SET, WSDLPackage.BINDING_OUTPUT__NAME, oldName, name));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Output getEOutput()
  {
    if (eOutput != null && eOutput.eIsProxy())
    {
      InternalEObject oldEOutput = (InternalEObject)eOutput;
      eOutput = (Output)eResolveProxy(oldEOutput);
      if (eOutput != oldEOutput)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, WSDLPackage.BINDING_OUTPUT__EOUTPUT, oldEOutput, eOutput));
      }
    }
    return eOutput;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Output basicGetEOutput()
  {
    return eOutput;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setEOutput(Output newEOutput)
  {
    Output oldEOutput = eOutput;
    eOutput = newEOutput;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, WSDLPackage.BINDING_OUTPUT__EOUTPUT, oldEOutput, eOutput));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public javax.wsdl.Output getOutput()
  {
    return getEOutput();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public void setOutput(javax.wsdl.Output output)
  {
    setEOutput((Output)output);
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
      case WSDLPackage.BINDING_OUTPUT__NAME:
      return getName();
      case WSDLPackage.BINDING_OUTPUT__EOUTPUT:
      if (resolve)
        return getEOutput();
      return basicGetEOutput();
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
      case WSDLPackage.BINDING_OUTPUT__NAME:
      setName((String)newValue);
      return;
      case WSDLPackage.BINDING_OUTPUT__EOUTPUT:
      setEOutput((Output)newValue);
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
      case WSDLPackage.BINDING_OUTPUT__NAME:
      setName(NAME_EDEFAULT);
      return;
      case WSDLPackage.BINDING_OUTPUT__EOUTPUT:
      setEOutput((Output)null);
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
      case WSDLPackage.BINDING_OUTPUT__NAME:
      return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case WSDLPackage.BINDING_OUTPUT__EOUTPUT:
      return eOutput != null;
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
    if (changedElement.hasAttribute("name"))
    {
      String name = changedElement.getAttribute("name");
      if (name != null)
      {
        setName(name);
      }
    }
  }

  public void handleUnreconciledElement(Element child, Collection remainingModelObjects)
  {
    super.handleUnreconciledElement(child, remainingModelObjects);
  }

  public Collection getModelObjects(Object component)
  {
    BindingOutput bindingOutput = (BindingOutput)component;
    List list = new ArrayList();
    list.addAll(bindingOutput.getEExtensibilityElements());
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
      if (eAttribute == null || eAttribute == WSDLPackage.Literals.BINDING_OUTPUT__NAME)
        niceSetAttribute(theElement, WSDLConstants.NAME_ATTRIBUTE, getName());
    }
  }

  public Element createElement()
  {
    Element newElement = createElement(WSDLConstants.OUTPUT);
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

  // Resolve the reference to Output
  public void reconcileReferences(boolean deep)
  {
    if (element != null)
    {
      BindingOperation bop = (BindingOperation)getContainer();
      Operation op = bop.getEOperation();
      if (op != null)
      {
        setOutput(op.getEOutput());
      }
    }
    super.reconcileReferences(deep);
  }
} //BindingOutputImpl
