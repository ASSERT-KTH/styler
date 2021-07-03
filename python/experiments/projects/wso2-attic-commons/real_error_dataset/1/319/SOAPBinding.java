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
 * A representation of the model object '<em><b>Binding</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.wst.wsdl.binding.soap.SOAPBinding#getTransportURI <em>Transport URI</em>}</li>
 *   <li>{@link org.eclipse.wst.wsdl.binding.soap.SOAPBinding#getStyle <em>Style</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.wst.wsdl.binding.soap.SOAPPackage#getSOAPBinding()
 * @model superTypes="org.eclipse.wst.wsdl.ExtensibilityElement org.eclipse.wst.wsdl.binding.soap.ISOAPBinding"
 * @generated
 */
public interface SOAPBinding extends ExtensibilityElement, javax.wsdl.extensions.soap.SOAPBinding
{

  /**
   * Returns the value of the '<em><b>Transport URI</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Transport URI</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Transport URI</em>' attribute.
   * @see #setTransportURI(String)
   * @see org.eclipse.wst.wsdl.binding.soap.SOAPPackage#getSOAPBinding_TransportURI()
   * @model
   * @generated
   */
  String getTransportURI();

  /**
   * Sets the value of the '{@link org.eclipse.wst.wsdl.binding.soap.SOAPBinding#getTransportURI <em>Transport URI</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Transport URI</em>' attribute.
   * @see #getTransportURI()
   * @generated
   */
  void setTransportURI(String value);

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
   * @see org.eclipse.wst.wsdl.binding.soap.SOAPPackage#getSOAPBinding_Style()
   * @model
   * @generated
   */
  String getStyle();

  /**
   * Sets the value of the '{@link org.eclipse.wst.wsdl.binding.soap.SOAPBinding#getStyle <em>Style</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Style</em>' attribute.
   * @see #getStyle()
   * @generated
   */
  void setStyle(String value);
} // SOAPBinding
