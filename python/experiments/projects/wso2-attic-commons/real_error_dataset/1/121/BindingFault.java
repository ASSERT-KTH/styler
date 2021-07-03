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


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Binding Fault</b></em>'.
 * @since 1.0
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * This class represents a WSDL fault element within a operation within a binding. A fault element within an operation within a binding specifies binding information for the fault with the same name. 
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.wst.wsdl.BindingFault#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.wst.wsdl.BindingFault#getEFault <em>EFault</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.wst.wsdl.WSDLPackage#getBindingFault()
 * @model superTypes="org.eclipse.wst.wsdl.ExtensibleElement org.eclipse.wst.wsdl.IBindingFault"
 * @generated
 */
public interface BindingFault extends ExtensibleElement, javax.wsdl.BindingFault
{
  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.eclipse.wst.wsdl.WSDLPackage#getBindingFault_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.wst.wsdl.BindingFault#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>EFault</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>EFault</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>EFault</em>' reference.
   * @see #setEFault(Fault)
   * @see org.eclipse.wst.wsdl.WSDLPackage#getBindingFault_EFault()
   * @model required="true"
   * @generated
   */
  Fault getEFault();

  /**
   * Sets the value of the '{@link org.eclipse.wst.wsdl.BindingFault#getEFault <em>EFault</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>EFault</em>' reference.
   * @see #getEFault()
   * @generated
   */
  void setEFault(Fault value);

  /**
   * <!-- begin-user-doc -->
   * This method is same as getEFault() and compatible with the JWSDL API.
   * @see #getEFault()
   * @return the value of the '<em>EFault</em>' reference.
   * @ignore
   * <!-- end-user-doc -->
   * @model kind="operation" type="org.eclipse.wst.wsdl.IFault"
   * @generated
   */
  javax.wsdl.Fault getFault();

  /**
   * <!-- begin-user-doc -->
   * This method is same as setEFault(Fault) and compatible with the JWSDL API.
   * @see #setEFault(Fault)
   * @param fault the new value of the '<em>EFault</em>' reference.
   * @ignore
   * <!-- end-user-doc -->
   * @model faultType="org.eclipse.wst.wsdl.IFault"
   * @generated
   */
  void setFault(javax.wsdl.Fault fault);

} // BindingFault
