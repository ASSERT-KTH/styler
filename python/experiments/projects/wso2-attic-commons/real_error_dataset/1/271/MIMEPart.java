/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.wsdl.binding.mime;


import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.wst.wsdl.ExtensibilityElement;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Part</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.wst.wsdl.binding.mime.MIMEPart#getEExtensibilityElements <em>EExtensibility Elements</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.wst.wsdl.binding.mime.MIMEPackage#getMIMEPart()
 * @model superTypes="org.eclipse.wst.wsdl.ExtensibilityElement org.eclipse.wst.wsdl.binding.mime.IMIMEPart"
 * @generated
 */
public interface MIMEPart extends ExtensibilityElement, javax.wsdl.extensions.mime.MIMEPart
{
  /**
   * Returns the value of the '<em><b>EExtensibility Elements</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.wst.wsdl.ExtensibilityElement}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>EExtensibility Elements</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>EExtensibility Elements</em>' containment reference list.
   * @see org.eclipse.wst.wsdl.binding.mime.MIMEPackage#getMIMEPart_EExtensibilityElements()
   * @model type="org.eclipse.wst.wsdl.ExtensibilityElement" containment="true"
   * @generated
   */
  EList getEExtensibilityElements();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model extensibilityElementType="org.eclipse.wst.wsdl.IExtensibilityElement"
   * @generated
   */
  void addExtensibilityElement(javax.wsdl.extensions.ExtensibilityElement extensibilityElement);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation" dataType="org.eclipse.wst.wsdl.binding.mime.List" many="false"
   * @generated
   */
  List getExtensibilityElements();

} // MIMEPart
