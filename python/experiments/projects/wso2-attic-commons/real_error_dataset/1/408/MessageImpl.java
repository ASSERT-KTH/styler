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


import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.ExtensibilityElement;
import org.eclipse.wst.wsdl.Message;
import org.eclipse.wst.wsdl.Part;
import org.eclipse.wst.wsdl.WSDLPackage;
import org.eclipse.wst.wsdl.internal.util.WSDLUtil;
import org.eclipse.wst.wsdl.util.WSDLConstants;
import org.w3c.dom.Element;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Message</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.wst.wsdl.internal.impl.MessageImpl#getQName <em>QName</em>}</li>
 *   <li>{@link org.eclipse.wst.wsdl.internal.impl.MessageImpl#isUndefined <em>Undefined</em>}</li>
 *   <li>{@link org.eclipse.wst.wsdl.internal.impl.MessageImpl#getEParts <em>EParts</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MessageImpl extends ExtensibleElementImpl implements Message
{
  /**
   * This class is not intended to be serialized.
   * serialVersionUID is assigned with 1L to avoid
   * compiler warning messages.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The default value of the '{@link #getQName() <em>QName</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getQName()
   * @generated
   * @ordered
   */
  protected static final QName QNAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getQName() <em>QName</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getQName()
   * @generated
   * @ordered
   */
  protected QName qName = QNAME_EDEFAULT;

  /**
   * The default value of the '{@link #isUndefined() <em>Undefined</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isUndefined()
   * @generated
   * @ordered
   */
  protected static final boolean UNDEFINED_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isUndefined() <em>Undefined</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isUndefined()
   * @generated
   * @ordered
   */
  protected boolean undefined = UNDEFINED_EDEFAULT;

  /**
   * The cached value of the '{@link #getEParts() <em>EParts</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEParts()
   * @generated
   * @ordered
   */
  protected EList eParts;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected MessageImpl()
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
    return WSDLPackage.Literals.MESSAGE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public QName getQName()
  {
    return qName;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setQName(QName newQName)
  {
    QName oldQName = qName;
    qName = newQName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, WSDLPackage.MESSAGE__QNAME, oldQName, qName));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isUndefined()
  {
    return undefined;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setUndefined(boolean newUndefined)
  {
    boolean oldUndefined = undefined;
    undefined = newUndefined;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, WSDLPackage.MESSAGE__UNDEFINED, oldUndefined, undefined));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getEParts()
  {
    if (eParts == null)
    {
      eParts = new EObjectContainmentEList(Part.class, this, WSDLPackage.MESSAGE__EPARTS);
    }
    return eParts;
  }

  /**
   * <!-- begin-user-doc -->
   * Add a part to this message.
   * @param part the part to be added
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public void addPart(javax.wsdl.Part part)
  {
    getEParts().add((Part)part);
  }

  /**
   * <!-- begin-user-doc -->
   * Get the specified part.
   * @param name the name of the desired part.
   * @return the corresponding part, or null if there wasn't
   * any matching part
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public javax.wsdl.Part getPart(String name)
  {
    Part result = null;
    for (Iterator i = getEParts().iterator(); i.hasNext();)
    {
      Part part = (Part)i.next();
      if (name.equals(part.getName()))
      {
        result = part;
        break;
      }
    }
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * Get all the parts defined here.
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Map getParts()
  {
    HashMap hashMap = new HashMap();
    for (Iterator i = getEParts().iterator(); i.hasNext();)
    {
      Part part = (Part)i.next();
      hashMap.put(part.getName(), part);
    }
    return hashMap;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public List getOrderedParts(List partOrder)
  {
    List orderedParts = new Vector();
    if (partOrder == null)
    {
      orderedParts.addAll(getEParts());
      return orderedParts;
    }

    Iterator partNameIterator = partOrder.iterator();
    while (partNameIterator.hasNext())
    {
      String partName = (String)partNameIterator.next();
      javax.wsdl.Part part = getPart(partName);
      if (part != null)
        orderedParts.add(part);
    }

    return orderedParts;
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
      case WSDLPackage.MESSAGE__EPARTS:
      return ((InternalEList)getEParts()).basicRemove(otherEnd, msgs);
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
      case WSDLPackage.MESSAGE__QNAME:
      return getQName();
      case WSDLPackage.MESSAGE__UNDEFINED:
      return isUndefined() ? Boolean.TRUE : Boolean.FALSE;
      case WSDLPackage.MESSAGE__EPARTS:
      return getEParts();
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
      case WSDLPackage.MESSAGE__QNAME:
      setQName((QName)newValue);
      return;
      case WSDLPackage.MESSAGE__UNDEFINED:
      setUndefined(((Boolean)newValue).booleanValue());
      return;
      case WSDLPackage.MESSAGE__EPARTS:
      getEParts().clear();
      getEParts().addAll((Collection)newValue);
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
      case WSDLPackage.MESSAGE__QNAME:
      setQName(QNAME_EDEFAULT);
      return;
      case WSDLPackage.MESSAGE__UNDEFINED:
      setUndefined(UNDEFINED_EDEFAULT);
      return;
      case WSDLPackage.MESSAGE__EPARTS:
      getEParts().clear();
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
      case WSDLPackage.MESSAGE__QNAME:
      return QNAME_EDEFAULT == null ? qName != null : !QNAME_EDEFAULT.equals(qName);
      case WSDLPackage.MESSAGE__UNDEFINED:
      return undefined != UNDEFINED_EDEFAULT;
      case WSDLPackage.MESSAGE__EPARTS:
      return eParts != null && !eParts.isEmpty();
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
    result.append(" (qName: "); //$NON-NLS-1$
    result.append(qName);
    result.append(", undefined: "); //$NON-NLS-1$
    result.append(undefined);
    result.append(')');
    return result.toString();
  }

  //
  // Reconcile methods
  //

  public void reconcileAttributes(Element changedElement)
  {
    Definition definition = getEnclosingDefinition();
    String name = changedElement.getAttribute(WSDLConstants.NAME_ATTRIBUTE);
    QName qname = new QName(definition.getTargetNamespace(), name == null ? "" : name); //$NON-NLS-1$
    setQName(qname);
  }

  public void handleUnreconciledElement(Element child, Collection remainingModelObjects)
  {
    switch (WSDLUtil.getInstance().getWSDLType(child))
    {
      case WSDLConstants.PART:
      {
        Part part = ((WSDLPackage)EPackage.Registry.INSTANCE.getEPackage(WSDLPackage.eNS_URI)).getWSDLFactory().createPart();
        part.setEnclosingDefinition(getEnclosingDefinition());
        part.setElement(child);
        getEParts().add(part);
        break;
      }
      default:
      {
        super.handleUnreconciledElement(child, remainingModelObjects);
        break;
      }
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
    List list = getList(component, modelObject);
    if (list != null)
    {
      list.remove(modelObject);
    }    
  }
  
  private List getList(Object component, Object modelObject)
  {
    List result = null;
    Message message = (Message)component;
    if (modelObject instanceof Part)
    {
      result = message.getEParts();
    }
    else if (modelObject instanceof ExtensibilityElement)
    {
      result = getExtensibilityElements();
    }
    
    return result;
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
      if (eAttribute == null || eAttribute == WSDLPackage.Literals.MESSAGE__QNAME)
        niceSetAttribute(theElement, WSDLConstants.NAME_ATTRIBUTE, getQName().getLocalPart());
    }
  }

  public Element createElement()
  {
    Element newElement = createElement(WSDLConstants.MESSAGE);
    setElement(newElement);

    Iterator iterator = getEParts().iterator();
    while (iterator.hasNext())
    {
      Object obj = iterator.next();
      if (obj instanceof Part)
      {
        Part part = (Part)obj;
        Element child = ((PartImpl)part).createElement();
        newElement.appendChild(child);
      }
    }
    return newElement;
  }
} //MessageImpl
