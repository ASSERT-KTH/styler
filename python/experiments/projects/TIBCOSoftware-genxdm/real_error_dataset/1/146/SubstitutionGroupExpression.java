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

import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.components.ParticleTerm;

/**
 * A Substitution Group is implemented as a choice of elements.
 */
final class SubstitutionGroupExpression implements ValidationExpr
{
	private static  Iterable<ValidationExpr> compileSubTerms(final Iterable<ElementDefinition> choices)
	{
		final ArrayList<ValidationExpr> subTerms = new ArrayList<ValidationExpr>();

		for (final ElementDefinition element : choices)
		{
			subTerms.add(new ParticleElementExpression(1, 1, element));
		}
		return subTerms;
	}

	private final boolean isMaxOccursUnbounded;
	private final int m_maxOccurs;
	private final int m_minOccurs;

	private final Iterable<ValidationExpr> m_subTerms;

	public SubstitutionGroupExpression(final int minOccurs, final int maxOccurs, final Iterable<ElementDefinition> choices)
	{
		this.m_minOccurs = minOccurs;
		this.m_maxOccurs = maxOccurs;
		this.isMaxOccursUnbounded = false;
		this.m_subTerms = compileSubTerms(choices);
	}

	public SubstitutionGroupExpression(final int minOccurs, final Iterable<ElementDefinition> choices)
	{
		this.m_minOccurs = minOccurs;
		this.m_maxOccurs = -1;
		this.isMaxOccursUnbounded = true;
		this.m_subTerms = compileSubTerms(choices);
	}

	public ParticleTerm getParticleTerm()
	{
		throw new UnsupportedOperationException();
	}

	public Iterable<ValidationExpr> getSubTerms()
	{
		return m_subTerms;
	}

	public boolean intersects(final ValidationExpr other)
	{
		return false;
	}

	public boolean isChoice()
	{
		return true;
	}

	public boolean isGroup()
	{
		return true;
	}

	public boolean isInterleave()
	{
		return false;
	}

	public boolean isMaxOccursUnbounded()
	{
		return isMaxOccursUnbounded;
	}

	public boolean isSequence()
	{
		return false;
	}

	public boolean matches(final QName token)
	{
		return false;
	}

	public int maxOccurs()
	{
		return m_maxOccurs;
	}

	public int minOccurs()
	{
		return m_minOccurs;
	}
}
