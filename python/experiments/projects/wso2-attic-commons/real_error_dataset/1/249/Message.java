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
 * A representation of the model object '<em><b>Message</b></em>'.
 * @since 1.0
 * @ignore
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * This class represents a WSDL message element. A WSDL message is an abstract, typed definition of the data being communicated.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.wst.wsdl.Message#getQName <em>QName</em>}</li>
 *   <li>{@link org.eclipse.wst.wsdl.Message#isUndefined <em>Undefined</em>}</li>
 *   <li>{@link org.eclipse.wst.wsdl.Message#getEParts <em>EParts</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.wst.wsdl.WSDLPackage#getMessage()
 * @model superTypes="org.eclipse.wst.wsdl.ExtensibleElement org.eclipse.wst.wsdl.IMessage"
 * @generated
 */
public interface Message extends ExtensibleElement, javax.wsdl.Message
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
   * @see org.eclipse.wst.wsdl.WSDLPackage#getMessage_QName()
   * @model dataType="org.eclipse.wst.wsdl.QName"
   * @generated
   */
  QName getQName();

  /**
   * Sets the value of the '{@link org.eclipse.wst.wsdl.Message#getQName <em>QName</em>}' attribute.
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
   * @see org.eclipse.wst.wsdl.WSDLPackage#getMessage_Undefined()
   * @model
   * @generated
   */
  boolean isUndefined();

  /**
   * Sets the value of the '{@link org.eclipse.wst.wsdl.Message#isUndefined <em>Undefined</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Undefined</em>' attribute.
   * @see #isUndefined()
   * @generated
   */
  void setUndefined(boolean value);

  /**
   * Returns the value of the '<em><b>EParts</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.wst.wsdl.Part}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>EParts</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>EParts</em>' containment reference list.
   * @see org.eclipse.wst.wsdl.WSDLPackage#getMessage_EParts()
   * @model type="org.eclipse.wst.wsdl.Part" containment="true"
   * @generated
   */
  EList getEParts();

} // Message
