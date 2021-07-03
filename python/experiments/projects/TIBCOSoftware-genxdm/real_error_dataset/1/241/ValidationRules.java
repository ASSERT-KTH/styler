/**
 * Copyright (c) 2009-2010 TIBCO Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.genxdm.processor.w3c.xs.validation.impl;

import javax.xml.namespace.QName;

import org.genxdm.processor.w3c.xs.exception.cvc.CvcAbstractComplexTypeException;
import org.genxdm.processor.w3c.xs.exception.cvc.CvcElementChildElementWithFixedException;
import org.genxdm.processor.w3c.xs.exception.cvc.CvcElementFixedValueOverriddenMixedException;
import org.genxdm.processor.w3c.xs.exception.src.SrcFrozenLocation;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.constraints.ValueConstraint;
import org.genxdm.xs.exceptions.AbortException;
import org.genxdm.xs.exceptions.SchemaExceptionHandler;
import org.genxdm.xs.types.ComplexType;


final class ValidationRules
{
	/**
	 * If there is a fixed {value constraint} the element information item must have no element information item
	 * children.
	 */
	public static  void checkValueConstraintAllowsElementChild(final ElementDefinition elementDeclaration, final QName childName, final Locatable childLocatable, final SchemaExceptionHandler errors) throws AbortException
	{
		final ValueConstraint valueConstraint = elementDeclaration.getValueConstraint();
		if (null != valueConstraint)
		{
			switch (valueConstraint.getVariety())
			{
				case Fixed:
				{
					errors.error(new CvcElementChildElementWithFixedException(elementDeclaration, childName, childLocatable.getLocation()));
				}
				break;
				case Default:
				{
					// No problem.
				}
				break;
				default:
				{
					throw new AssertionError(valueConstraint.getVariety().name());
				}
			}
		}
	}

	/**
	 * If the data component has a "fixed" attribute, reports an error if there is a conflict with the validated value.
	 */
	public static <A> void checkValueConstraintForMixedContent(final ElementDefinition elementDeclaration, final String initialValue, final Locatable locatable, final SchemaExceptionHandler errors, final AtomBridge<A> atomBridge) throws AbortException
	{
		final ValueConstraint valueConstraint = elementDeclaration.getValueConstraint();
		if (null != valueConstraint)
		{
			switch (valueConstraint.getVariety())
			{
				case Fixed:
				{
					final String expectValue = atomBridge.getC14NString(valueConstraint.getValue(atomBridge));

					if (!expectValue.equals(initialValue))
					{
						errors.error(new CvcElementFixedValueOverriddenMixedException(elementDeclaration, expectValue, initialValue, locatable.getLocation()));
					}
				}
				break;
				case Default:
				{
					// No problem.
				}
				break;
				default:
				{
					throw new AssertionError(valueConstraint.getVariety());
				}
			}
		}
	}

	/**
	 * Determines whether the element {type} is {abstract}. <br/>
	 * The error is only raised if the element {type} exists, is a complex type, and is abstract.
	 * 
	 * @param elementType
	 *            The type of the element, may be <code>null</code>.
	 * @param elementName
	 *            The name of the element information item.
	 * @param errors
	 *            The exception handler.
	 */
	public static void checkComplexTypeNotAbstract(final ComplexType elementType, final QName elementName, final SchemaExceptionHandler errors) throws AbortException
	{
		if (null != elementType && elementType.isAbstract())
		{
			errors.error(new CvcAbstractComplexTypeException(elementName, elementType, new SrcFrozenLocation(-1, -1, -1, null, null)));
		}
	}
}