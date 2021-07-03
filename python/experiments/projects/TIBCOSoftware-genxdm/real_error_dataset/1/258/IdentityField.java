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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.processor.w3c.xs.exception.cvc.CvcIdentityConstraintFieldNodeNotSimpleTypeException;
import org.genxdm.processor.w3c.xs.exception.sm.SmDuplicateKeyFieldException;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.constraints.RestrictedXPath;
import org.genxdm.xs.exceptions.AbortException;
import org.genxdm.xs.exceptions.SchemaExceptionHandler;
import org.genxdm.xs.resolve.LocationInSchema;
import org.genxdm.xs.types.ComplexType;
import org.genxdm.xs.types.SimpleType;
import org.genxdm.xs.types.Type;


/**
 * Provides a streaming evaluation of a restricted XPath expression. <br/>
 * When certain conditions are met, a match handler is called. The matched fields gets reported back to its scope.
 */
final class IdentityField
{
	/**
	 * The index of the element that is the context for the xs:field evaluation.
	 */
	private final int m_contextIndex;
	private final IdentityScope m_scope;
	private final SchemaExceptionHandler m_errorHandler;

	// TODO: temporary
//	private List<? extends A> m_value = null;
	String m_value;
	private int m_nodeIndex = -1;
	private LocationInSchema m_location = null;

	// a List of branches, representing a parsed identity (selector or field)
	// XPath expression
	private RestrictedXPath m_branches[];

	private final ArrayList<LinkedList<IdentityXPathStatus>> m_active;

	// true if that branch started with ".//"
	private boolean[] m_relocatable;

	private int m_depth = -1;

	/**
	 * @param xpath
	 *            representing the parsed attribute xpath
	 */
	public IdentityField(final RestrictedXPath xpath, final int contextIndex, final IdentityScope scope, final SchemaExceptionHandler errorHandler)
	{
		m_contextIndex = contextIndex;
		m_scope = PreCondition.assertArgumentNotNull(scope, "scope");
		m_errorHandler = PreCondition.assertArgumentNotNull(errorHandler, "errorHandler");

		// Count the number of branches, that is, the number of XPath
		// expressions separated by "|".
		int size = 0;
		for (RestrictedXPath branch = xpath; branch != null; branch = branch.getAlternate())
		{
			// System.out.println("IdentityPathEvaluation.<init> Branch = " +
			// branch);
			size++;
		}

		m_relocatable = new boolean[size];
		m_branches = (RestrictedXPath[])Array.newInstance(RestrictedXPath.class, size);
		m_active = new ArrayList<LinkedList<IdentityXPathStatus>>(size);
		RestrictedXPath branch = xpath;
		for (int i = size - 1; i >= 0; i--)
		{
			// put them backwards
			if (null == branch)
			{
				throw new RuntimeException();
			}
			m_branches[i] = branch;
			m_relocatable[i] = branch.isRelocatable();
			branch = branch.getAlternate();
		}
		for (int i = 0; i < size; i++)
		{
			// The XPath expression is already running for each branch.
			final LinkedList<IdentityXPathStatus> e = new LinkedList<IdentityXPathStatus>();
			final IdentityXPathStatus xps = new IdentityXPathStatus(false);
			xps.currentStep = -1;
			e.add(xps);
			m_active.add(e);
		}
	}

	public <A> List<? extends A> getTypedValue(AtomBridge<A> bridge)
	{
	    // TODO: so, now fix it.
	    return null;
//		return m_value;
	}

	public LocationInSchema getLocation()
	{
		return m_location;
	}

	private int getMaximumStep(final int branchIdx)
	{
		return m_branches[branchIdx].getStepLength() - 1;
	}

	public void startElement(final QName elementName, final int elementIndex, final Type elementType, final Locatable locatable) throws AbortException
	{
		m_depth++;

		for (int branchIdx = m_branches.length - 1; branchIdx >= 0; branchIdx--)
		{
			// Note: Use an Iterator here so that we can remove (through the
			// iterator) without
			// getting a concurrent modification exception (would happen through
			// the list).
			final Iterator<IdentityXPathStatus> active = m_active.get(branchIdx).iterator();
			while (active.hasNext())
			{
				final IdentityXPathStatus xps = active.next();
				xps.currentStep += 1;
				if (xps.currentStep <= getMaximumStep(branchIdx))
				{
					if (matches(m_branches[branchIdx], xps.currentStep, elementName))
					{
						if (xps.currentStep == getMaximumStep(branchIdx))
						{
							// TODO: Should this take place at design time?
							if (isSimpleTypeOrSimpleContent(elementType))
							{
								xps.isSimple = true;
							}
							else
							{
								m_errorHandler.error(new CvcIdentityConstraintFieldNodeNotSimpleTypeException(m_scope.getConstraint().getName(), locatable.getLocation()));
							}
						}
					}
					else
					{
						if (xps.removable)
						{
							active.remove();
						}
					}
				}
			}

			if (m_depth > 0 && m_relocatable[branchIdx])
			{
				if (matches(m_branches[branchIdx], 0, elementName))
				{
					final IdentityXPathStatus xps = new IdentityXPathStatus(true);
					if (xps.currentStep == getMaximumStep(branchIdx))
					{
						if (isSimpleTypeOrSimpleContent(elementType))
						{
							xps.isSimple = true;
						}
						else
						{
							m_errorHandler.error(new CvcIdentityConstraintFieldNodeNotSimpleTypeException(m_scope.getConstraint().getName(), locatable.getLocation()));
						}
					}
					m_active.get(branchIdx).add(xps);
				}
			}
		}
	}

	public void endElement(final QName elementName, final int elementIndex, final Locatable location)
	{
		// System.out.println(StripQualifiers.strip(getClass().getName()) +
		// ".endElement(elementName=" + elementName + ", elementIndex=" +
		// elementIndex + ")");

		m_depth--;
		for (int branchIdx = m_branches.length - 1; branchIdx >= 0; branchIdx--)
		{
			// Note: Use an Iterator here so that we can remove (through the
			// iterator) without
			// getting a concurrent modification exception (would happen through
			// the list).
			final Iterator<IdentityXPathStatus> it = m_active.get(branchIdx).iterator();
			while (it.hasNext())
			{
				final IdentityXPathStatus xps = it.next();
				if (xps.currentStep == getMaximumStep(branchIdx))
				{
					xps.isSimple = false;
					if (xps.removable)
					{
						it.remove();
					}
				}
				xps.currentStep -= 1;
			}

			// if (m_depth == 0)
			// {
			// PreCondition.assertTrue(m_active.get(branchIdx).isEmpty());
			// }
		}
	}

	public <A> void attribute(final QName name, final List<? extends A> actualValue, final int attributeIndex, final SimpleType attributeType, final Locatable locatable, final AtomBridge<A> atomBridge) throws AbortException
	{
		// System.out.println(StripQualifiers.strip(getClass().getName()) + "["
		// + hashCode() + "].attribute(name=" + name + ", value=" +
		// GxSupport.atomsToString(actualValue) + ")");

		for (int branchIdx = m_branches.length - 1; branchIdx >= 0; branchIdx--)
		{
			// Note: Use an Iterator here so that we can remove (through the
			// iterator) without
			// getting a concurrent modification exception (would happen through
			// the list).
			final Iterator<IdentityXPathStatus> active = m_active.get(branchIdx).iterator();
			while (active.hasNext())
			{
				final IdentityXPathStatus xps = active.next();
				final int currentStep = xps.currentStep + 1;
				final int maximumStep = getMaximumStep(branchIdx);
				if (currentStep <= maximumStep)
				{
					if (matchesAttribute(m_branches[branchIdx], currentStep, name))
					{
						if (currentStep == getMaximumStep(branchIdx))
						{
							attributeMatched(name, actualValue, attributeIndex, locatable, atomBridge);
						}
					}
					else
					{
						if (xps.removable)
						{
							active.remove();
						}
					}
				}
			}
		}
	}

	public <A> void text(final List<? extends A> actualValue, final SimpleType actualType, final int textIndex, final Locatable locatable, final AtomBridge<A> atomBridge) throws AbortException
	{
		for (int branchIdx = m_branches.length - 1; branchIdx >= 0; branchIdx--)
		{
			final Iterator<IdentityXPathStatus> it = m_active.get(branchIdx).iterator();
			while (it.hasNext())
			{
				final IdentityXPathStatus xps = it.next();
				if (xps.isSimple)
				{
					textMatched(actualValue, textIndex, locatable, atomBridge);
				}
				else
				{
					final int currentStep = xps.currentStep + 1;
					final int maximumStep = getMaximumStep(branchIdx);
					if (currentStep <= maximumStep)
					{
						if (matchesText(m_branches[branchIdx], currentStep))
						{
							if (currentStep == maximumStep)
							{
								textMatched(actualValue, textIndex, locatable, atomBridge);
							}
						}
						else
						{
							if (xps.removable)
							{
								it.remove();
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Determine whether the (QName,isAttribute) combination matches the XPath expression at the specified step index.
	 */
	private boolean matches(final RestrictedXPath xpath, final int idxStep, final QName name)
	{
		if (xpath.isAttribute())
		{
			return false;
		}

		if (xpath.isContextNode(idxStep))
		{
			return true;
		}
		else
		{
			final String stepLN = xpath.getStepLocalName(idxStep);
			final String stepNS = xpath.getStepNamespace(idxStep);

			if (xpath.isWildcardLocalName(idxStep) && (xpath.isWildcardNamespace(idxStep) || stepNS.equals(name.getNamespaceURI())))
			{
				return true;
			}
			else
			{
				return name.getLocalPart().equals(stepLN) && name.getNamespaceURI().equals(stepNS);
			}
		}
	}

	private boolean matchesText(final RestrictedXPath xpath, final int idxStep)
	{
		// System.out.println(StripQualifiers.strip(getClass().getName()) + "["
		// + hashCode() + "].matchesText(idxStep=" + idxStep + ")");
		if (xpath.isAttribute())
		{
			return false;
		}

		return xpath.isContextNode(idxStep);
	}

	/**
	 * Determine whether the (QName,isAttribute) combination matches the XPath expression at the specified step index.
	 */
	private boolean matchesAttribute(final RestrictedXPath xpath, final int idxStep, final QName name)
	{
		// System.out.println(StripQualifiers.strip(getClass().getName()) + "["
		// + hashCode() + "].matchesAttribute(" + xpath + ", idxStep=" + idxStep
		// + ", name=" + name + ")");
		if (!xpath.isAttribute())
		{
			return false;
		}

		if (xpath.isContextNode(idxStep))
		{
			return true;
		}
		else
		{
			final String stepNS = xpath.getStepNamespace(idxStep);

			if (xpath.isWildcardLocalName(idxStep) && (xpath.isWildcardNamespace(idxStep) || stepNS.equals(name.getNamespaceURI())))
			{
				return true;
			}
			else
			{
				final String stepLN = xpath.getStepLocalName(idxStep);
				return name.getLocalPart().equals(stepLN) && name.getNamespaceURI().equals(stepNS);
			}
		}
	}

	private <A> void attributeMatched(final QName name, final List<? extends A> actualValue, final int attributeIndex, final Locatable locatable, final AtomBridge<A> atomBridge) throws AbortException
	{
		if (m_value == null)
		{
			m_value = atomBridge.getC14NString(actualValue);
			m_nodeIndex = attributeIndex;
			m_location = locatable.getLocation();
			m_scope.onFieldValueSet(this, m_contextIndex, atomBridge);
		}
		else
		{
			if (m_nodeIndex != attributeIndex)
			{
				m_errorHandler.error(new SmDuplicateKeyFieldException(m_scope.getConstraint().getName(), locatable.getLocation()));
			}
		}
	}

	private <A> void textMatched(final List<? extends A> actualValue, final int textIndex, final Locatable locatable, final AtomBridge<A> atomBridge) throws AbortException
	{
		if (m_value == null)
		{
			m_value = atomBridge.getC14NString(actualValue);
			m_nodeIndex = textIndex;
			m_location = locatable.getLocation();
			m_scope.onFieldValueSet(this, m_contextIndex, atomBridge);
		}
		else
		{
			if (m_nodeIndex != textIndex)
			{
				m_errorHandler.error(new SmDuplicateKeyFieldException(m_scope.getConstraint().getName(), locatable.getLocation()));
			}
		}
	}

	/**
	 * A safe test of whether a type is simple or complex with simple content that is resilient to null.
	 */
	private static <A> boolean isSimpleTypeOrSimpleContent(final Type type)
	{
		if (null != type)
		{
			if (type instanceof SimpleType)
			{
				return true;
			}
			else if (type instanceof ComplexType)
			{
				final ComplexType complexType = (ComplexType)type;
				return complexType.getContentType().isSimple();
			}
			else
			{
				throw new AssertionError();
			}
		}
		else
		{
			return false;
		}
	}
}
