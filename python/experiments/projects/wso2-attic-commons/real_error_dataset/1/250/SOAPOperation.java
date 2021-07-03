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
package org.eclipse.wst.wsdl.binding.soap;


import org.eclipse.wst.wsdl.ExtensibilityElement;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Operation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.wst.wsdl.binding.soap.SOAPOperation#getSoapActionURI <em>Soap Action URI</em>}</li>
 *   <li>{@link org.eclipse.wst.wsdl.binding.soap.SOAPOperation#getStyle <em>Style</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.wst.wsdl.binding.soap.SOAPPackage#getSOAPOperation()
 * @model superTypes="org.eclipse.wst.wsdl.ExtensibilityElement org.eclipse.wst.wsdl.binding.soap.ISOAPOperation"
 * @generated
 */
public interface SOAPOperation extends ExtensibilityElement, javax.wsdl.extensions.soap.SOAPOperation
{

  /**
   * Returns the value of the '<em><b>Soap Action URI</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Soap Action URI</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Soap Action URI</em>' attribute.
   * @see #setSoapActionURI(String)
   * @see org.eclipse.wst.wsdl.binding.soap.SOAPPackage#getSOAPOperation_SoapActionURI()
   * @model
   * @generated
   */
  String getSoapActionURI();

  /**
   * Sets the value of the '{@link org.eclipse.wst.wsdl.binding.soap.SOAPOperation#getSoapActionURI <em>Soap Action URI</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Soap Action URI</em>' attribute.
   * @see #getSoapActionURI()
   * @generated
   */
  void setSoapActionURI(String value);

  /**
   * Returns the value of the '<em><b>Style</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Style</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Style</em>' attribute.
   * @see #setStyle(String)
   * @see org.eclipse.wst.wsdl.binding.soap.SOAPPackage#getSOAPOperation_Style()
   * @model
   * @generated
   */
  String getStyle();

  /**
   * Sets the value of the '{@link org.eclipse.wst.wsdl.binding.soap.SOAPOperation#getStyle <em>Style</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Style</em>' attribute.
   * @see #getStyle()
   * @generated
   */
  void setStyle(String value);
} // SOAPOperation
