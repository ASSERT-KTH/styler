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
 * A representation of the model object '<em><b>Binding Input</b></em>'.
 * @since 1.0
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * This class represents a WSDL input element within a operation within a binding. An input element within an operation within a binding specifies binding information for the input of the operation. 
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.wst.wsdl.BindingInput#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.wst.wsdl.BindingInput#getEInput <em>EInput</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.wst.wsdl.WSDLPackage#getBindingInput()
 * @model superTypes="org.eclipse.wst.wsdl.ExtensibleElement org.eclipse.wst.wsdl.IBindingInput"
 * @generated
 */
public interface BindingInput extends ExtensibleElement, javax.wsdl.BindingInput
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
   * @see org.eclipse.wst.wsdl.WSDLPackage#getBindingInput_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.wst.wsdl.BindingInput#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>EInput</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>EInput</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>EInput</em>' reference.
   * @see #setEInput(Input)
   * @see org.eclipse.wst.wsdl.WSDLPackage#getBindingInput_EInput()
   * @model required="true"
   * @generated
   */
  Input getEInput();

  /**
   * Sets the value of the '{@link org.eclipse.wst.wsdl.BindingInput#getEInput <em>EInput</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>EInput</em>' reference.
   * @see #getEInput()
   * @generated
   */
  void setEInput(Input value);

  /**
   * <!-- begin-user-doc -->
   * This method is same as getEInput() and compatible with the JWSDL API.
   * @see #getEInput()
   * @return the value of the '<em>EInput</em>' reference.
   * @ignore
   * <!-- end-user-doc -->
   * @model kind="operation" type="org.eclipse.wst.wsdl.IInput"
   * @generated
   */
  javax.wsdl.Input getInput();

  /**
   * <!-- begin-user-doc -->
   * This method is same as setEInput(Input) and compatible with the JWSDL API.
   * @see #setEInput(Input)
   * @param input the new value of the '<em>EInput</em>' reference.
   * @ignore
   * <!-- end-user-doc -->
   * @model inputType="org.eclipse.wst.wsdl.IInput"
   * @generated
   */
  void setInput(javax.wsdl.Input input);

} // BindingInput
