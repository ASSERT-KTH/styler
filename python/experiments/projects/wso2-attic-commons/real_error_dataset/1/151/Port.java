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
 * A representation of the model object '<em><b>Port</b></em>'.
 * @since 1.0
 * @ignore
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * This class represents a WSDL port element. A port defines an individual endpoint by specifying a single address for a binding
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.wst.wsdl.Port#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.wst.wsdl.Port#getEBinding <em>EBinding</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.wst.wsdl.WSDLPackage#getPort()
 * @model superTypes="org.eclipse.wst.wsdl.ExtensibleElement org.eclipse.wst.wsdl.IPort"
 * @generated
 */
public interface Port extends ExtensibleElement, javax.wsdl.Port
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
   * @see org.eclipse.wst.wsdl.WSDLPackage#getPort_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.wst.wsdl.Port#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>EBinding</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>EBinding</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>EBinding</em>' reference.
   * @see #setEBinding(Binding)
   * @see org.eclipse.wst.wsdl.WSDLPackage#getPort_EBinding()
   * @model required="true"
   * @generated
   */
  Binding getEBinding();

  /**
   * Sets the value of the '{@link org.eclipse.wst.wsdl.Port#getEBinding <em>EBinding</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>EBinding</em>' reference.
   * @see #getEBinding()
   * @generated
   */
  void setEBinding(Binding value);

} // Port
