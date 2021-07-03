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


import java.util.List;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Types</b></em>'.
 * @since 1.0
 * @ignore
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * This class represents a WSDL types element. The types element encloses data type definitions that are relevant for the exchanged messages.
 * <!-- end-model-doc -->
 *
 *
 * @see org.eclipse.wst.wsdl.WSDLPackage#getTypes()
 * @model superTypes="org.eclipse.wst.wsdl.ExtensibleElement org.eclipse.wst.wsdl.ITypes"
 * @generated
 */
public interface Types extends ExtensibleElement, javax.wsdl.Types
{
  /**
   * <!-- begin-user-doc -->
   * Returns a List of inline XSDSchema instances in this Types. 
   * @return List of XSDSchema instances.
   * @see org.eclipse.xsd.XSDSchema
   * @ignore
   * <!-- end-user-doc -->
   * @model kind="operation" type="org.eclipse.wst.wsdl.IList" many="false"
   * @generated
   */
  List getSchemas();

  /**
   * <!-- begin-user-doc -->
   * Returns a List of inline XSDSchema instances in this Types. The targetNamespace of
   * the schemas are the same as namespaceURI argument value.
   * @param namespaceURI targetNamespace of the schemas to be searched for.
   * @return List of XSDSchema instances.
   * @see org.eclipse.xsd.XSDSchema
   * @ignore
   * <!-- end-user-doc -->
   * @model type="org.eclipse.wst.wsdl.IList" many="false"
   * @generated
   */
  List getSchemas(String namespaceURI);

} // Types
