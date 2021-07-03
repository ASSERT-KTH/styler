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


import org.eclipse.emf.common.util.EList;
import org.eclipse.wst.wsdl.ExtensibilityElement;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Body</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.wst.wsdl.binding.soap.SOAPBody#getUse <em>Use</em>}</li>
 *   <li>{@link org.eclipse.wst.wsdl.binding.soap.SOAPBody#getNamespaceURI <em>Namespace URI</em>}</li>
 *   <li>{@link org.eclipse.wst.wsdl.binding.soap.SOAPBody#getEEncodingStyles <em>EEncoding Styles</em>}</li>
 *   <li>{@link org.eclipse.wst.wsdl.binding.soap.SOAPBody#getEParts <em>EParts</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.wst.wsdl.binding.soap.SOAPPackage#getSOAPBody()
 * @model superTypes="org.eclipse.wst.wsdl.ExtensibilityElement org.eclipse.wst.wsdl.binding.soap.ISOAPBody"
 * @generated
 */
public interface SOAPBody extends ExtensibilityElement, javax.wsdl.extensions.soap.SOAPBody
{

  /**
   * Returns the value of the '<em><b>Use</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Use</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Use</em>' attribute.
   * @see #setUse(String)
   * @see org.eclipse.wst.wsdl.binding.soap.SOAPPackage#getSOAPBody_Use()
   * @model
   * @generated
   */
  String getUse();

  /**
   * Sets the value of the '{@link org.eclipse.wst.wsdl.binding.soap.SOAPBody#getUse <em>Use</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Use</em>' attribute.
   * @see #getUse()
   * @generated
   */
  void setUse(String value);

  /**
   * Returns the value of the '<em><b>Namespace URI</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Namespace URI</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Namespace URI</em>' attribute.
   * @see #setNamespaceURI(String)
   * @see org.eclipse.wst.wsdl.binding.soap.SOAPPackage#getSOAPBody_NamespaceURI()
   * @model
   * @generated
   */
  String getNamespaceURI();

  /**
   * Sets the value of the '{@link org.eclipse.wst.wsdl.binding.soap.SOAPBody#getNamespaceURI <em>Namespace URI</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Namespace URI</em>' attribute.
   * @see #getNamespaceURI()
   * @generated
   */
  void setNamespaceURI(String value);

  /**
   * Returns the value of the '<em><b>EEncoding Styles</b></em>' attribute list.
   * The list contents are of type {@link java.lang.String}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>EEncoding Styles</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>EEncoding Styles</em>' attribute list.
   * @see org.eclipse.wst.wsdl.binding.soap.SOAPPackage#getSOAPBody_EEncodingStyles()
   * @model dataType="org.eclipse.wst.wsdl.binding.soap.IString"
   * @generated
   */
  EList getEEncodingStyles();

  /**
   * Returns the value of the '<em><b>EParts</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.wst.wsdl.Part}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>EParts</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>EParts</em>' reference list.
   * @see org.eclipse.wst.wsdl.binding.soap.SOAPPackage#getSOAPBody_EParts()
   * @model type="org.eclipse.wst.wsdl.Part"
   * @generated
   */
  EList getEParts();
} // SOAPBody
