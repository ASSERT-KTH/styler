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

import java.util.Collections;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.components.ParticleTerm;

final class ParticleElementExpression implements ValidationExpr
{
	private final boolean isMaxOccursUnbounded;
	private final ElementDefinition m_element;
	private final int m_maxOccurs;
	private final int m_minOccurs;

	public ParticleElementExpression(final int minOccurs, final int maxOccurs, final ElementDefinition element)
	{
		this.m_minOccurs = minOccurs;
		this.m_maxOccurs = maxOccurs;
		this.isMaxOccursUnbounded = false;
		this.m_element = PreCondition.assertArgumentNotNull(element, "element");
	}

	public ParticleElementExpression(final int minOccurs, final ElementDefinition element)
	{
		this.m_minOccurs = minOccurs;
		this.m_maxOccurs = -1;
		this.isMaxOccursUnbounded = true;
		this.m_element = PreCondition.assertArgumentNotNull(element, "element");
	}

	public ElementDefinition getParticleTerm()
	{
		return m_element;
	}

	public Iterable<ValidationExpr> getSubTerms()
	{
		return Collections.emptyList();
	}

	public boolean intersects(final ValidationExpr other)
	{
		if (other.isGroup())
		{
			return false;
		}
		else
		{
			final ParticleTerm term = other.getParticleTerm();
			if (term instanceof ElementDefinition)
			{
				final ElementDefinition element = (ElementDefinition)term;
				return matches(element.getName());
			}
			else
			{
				// It must be a wildcard.
				return other.intersects(this);
			}
		}
	}

	public boolean isChoice()
	{
		return false;
	}

	public boolean isGroup()
	{
		return false;
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

	public boolean matches(final QName name)
	{
		if (null != name)
		{
			return m_element.getName().equals(name);
		}
		else
		{
			return false;
		}
	}

	public int maxOccurs()
	{
		return m_maxOccurs;
	}

	public int minOccurs()
	{
		return m_minOccurs;
	}

	@Override
	public String toString()
	{
		return "element " + m_element.getName();
	}
}
