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

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.constraints.RestrictedXPath;
import org.genxdm.xs.exceptions.AbortException;

/**
 * Provides a streaming evaluation of a restricted XPath expression. <br/>
 * When certain conditions are met, a match handler is called
 */
final class IdentitySelector
{
	public static final boolean DEBUG = false;

	// We will callback the scope to indicate that the selector has matched.
	// We issue one event for the start and one event for the end in a pair.
	private final IdentityScope m_scope;

	// a List of branches, representing a parsed identity (selector or field)
	// XPath expression
	private final RestrictedXPath m_xpath[];

	/**
	 * The possibility of having "//" in the XPath expression means that a single XPath expression may have many
	 * simultaneous evaluations. We keep track of these with a list of status objects. The outer index is again for
	 * alternate XPath expressions.
	 */
	private final ArrayList<LinkedList<IdentitySelectorEvaluation>> m_evals;

	// true if that branch started with ".//"
	private final boolean[] m_relocatable;

	private int m_depth = -1;

	/**
	 * @param xpath
	 *            representing the parsed attribute xpath
	 */
	public IdentitySelector(final IdentityScope scope, final RestrictedXPath xpath)
	{
		m_scope = PreCondition.assertArgumentNotNull(scope, "scope");

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
		m_xpath = (RestrictedXPath[])Array.newInstance(RestrictedXPath.class, size);
		m_evals = new ArrayList<LinkedList<IdentitySelectorEvaluation>>(size);
		RestrictedXPath branch = xpath;
		for (int i = size - 1; i >= 0; i--)
		{
			// put them backwards
			if (null == branch)
			{
				throw new RuntimeException();
			}
			m_xpath[i] = branch;
			m_relocatable[i] = branch.isRelocatable();
			branch = branch.getAlternate();
		}
		for (int i = 0; i < size; i++)
		{
			final LinkedList<IdentitySelectorEvaluation> e = new LinkedList<IdentitySelectorEvaluation>();
			e.add(new IdentitySelectorEvaluation(m_xpath[i], false));
			m_evals.add(e);
		}
	}

	public void startElement(final QName elementName, final int elementIndex)
	{
		m_depth++;

		for (int unionIdx = m_xpath.length - 1; unionIdx >= 0; unionIdx--)
		{
			// Note: Use an Iterator here so that we can remove (through the
			// iterator) without getting a concurrent modification exception
			// (would happen through the list).
			final Iterator<IdentitySelectorEvaluation> it = m_evals.get(unionIdx).iterator();
			while (it.hasNext())
			{
				final IdentitySelectorEvaluation xps = it.next();

				xps.advance();

				if (xps.inBounds())
				{
					if (xps.matchesElement(elementName))
					{
						if (xps.onLastStep())
						{
							m_scope.startSelectorElement(elementName, elementIndex);
							xps.setSelecting(true);
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

			// TODO: The following code pertains to relocation.
			if (m_depth > 0 && m_relocatable[unionIdx])
			{
				if (matchesElement(m_xpath[unionIdx], 0, elementName))
				{
					final IdentitySelectorEvaluation xps = new IdentitySelectorEvaluation(m_xpath[unionIdx], true);
					if (xps.onLastStep())
					{
						m_scope.startSelectorElement(elementName, elementIndex);
						xps.setSelecting(true);
					}
					m_evals.get(unionIdx).add(xps);
				}
			}
		}
	}

	public void endElement(final QName elementName, final int elementIndex, final Locatable location) throws AbortException
	{
		m_depth--;
		for (int unionIdx = m_xpath.length - 1; unionIdx >= 0; unionIdx--)
		{
			// Note: Use an Iterator here so that we can remove (through the
			// iterator) without
			// getting a concurrent modification exception (would happen through
			// the list).
			final Iterator<IdentitySelectorEvaluation> it = m_evals.get(unionIdx).iterator();
			while (it.hasNext())
			{
				final IdentitySelectorEvaluation xps = it.next();
				if (xps.inBounds())
				{
					// Use the flag to avoid checking for a match and last step.
					if (xps.isSelecting())
					{
						try
						{
							m_scope.endSelectorElement(elementName, elementIndex, location);
						}
						finally
						{
							xps.setSelecting(false);
						}
					}
					if (xps.removable)
					{
						it.remove();
					}
				}
				xps.retreat();
			}
		}
	}

	private boolean matchesElement(final RestrictedXPath xpath, final int idxStep, final QName elementName)
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

			if (xpath.isWildcardLocalName(idxStep) && (xpath.isWildcardNamespace(idxStep) || stepNS.equals(elementName.getNamespaceURI())))
			{
				return true;
			}
			else
			{
				return elementName.getLocalPart().equals(stepLN) && elementName.getNamespaceURI().equals(stepNS);
			}
		}
	}
}
