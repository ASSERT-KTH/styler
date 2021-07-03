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
package org.eclipse.wst.wsdl;


import javax.xml.namespace.QName;

import org.eclipse.emf.common.util.EList;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Binding</b></em>'.
 * @since 1.0
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * This class represents a WSDL binding element. A binding defines message format and protocol details for operations and messages defined by a particular portType. There may be any number of bindings for a given portType.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.wst.wsdl.Binding#getQName <em>QName</em>}</li>
 *   <li>{@link org.eclipse.wst.wsdl.Binding#isUndefined <em>Undefined</em>}</li>
 *   <li>{@link org.eclipse.wst.wsdl.Binding#getEPortType <em>EPort Type</em>}</li>
 *   <li>{@link org.eclipse.wst.wsdl.Binding#getEBindingOperations <em>EBinding Operations</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.wst.wsdl.WSDLPackage#getBinding()
 * @model superTypes="org.eclipse.wst.wsdl.ExtensibleElement org.eclipse.wst.wsdl.IBinding"
 * @generated
 */
public interface Binding extends ExtensibleElement, javax.wsdl.Binding
{
  /**
   * Returns the value of the '<em><b>QName</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>QName</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>QName</em>' attribute.
   * @see #setQName(QName)
   * @see org.eclipse.wst.wsdl.WSDLPackage#getBinding_QName()
   * @model dataType="org.eclipse.wst.wsdl.QName"
   * @generated
   */
  QName getQName();

  /**
   * Sets the value of the '{@link org.eclipse.wst.wsdl.Binding#getQName <em>QName</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>QName</em>' attribute.
   * @see #getQName()
   * @generated
   */
  void setQName(QName value);

  /**
   * Returns the value of the '<em><b>Undefined</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Undefined</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Undefined</em>' attribute.
   * @see #setUndefined(boolean)
   * @see org.eclipse.wst.wsdl.WSDLPackage#getBinding_Undefined()
   * @model
   * @generated
   */
  boolean isUndefined();

  /**
   * Sets the value of the '{@link org.eclipse.wst.wsdl.Binding#isUndefined <em>Undefined</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Undefined</em>' attribute.
   * @see #isUndefined()
   * @generated
   */
  void setUndefined(boolean value);

  /**
   * Returns the value of the '<em><b>EPort Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>EPort Type</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>EPort Type</em>' reference.
   * @see #setEPortType(PortType)
   * @see org.eclipse.wst.wsdl.WSDLPackage#getBinding_EPortType()
   * @model required="true"
   * @generated
   */
  PortType getEPortType();

  /**
   * Sets the value of the '{@link org.eclipse.wst.wsdl.Binding#getEPortType <em>EPort Type</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>EPort Type</em>' reference.
   * @see #getEPortType()
   * @generated
   */
  void setEPortType(PortType value);

  /**
   * Returns the value of the '<em><b>EBinding Operations</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.wst.wsdl.BindingOperation}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>EBinding Operations</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>EBinding Operations</em>' containment reference list.
   * @see org.eclipse.wst.wsdl.WSDLPackage#getBinding_EBindingOperations()
   * @model type="org.eclipse.wst.wsdl.BindingOperation" containment="true"
   * @generated
   */
  EList getEBindingOperations();

} // Binding
