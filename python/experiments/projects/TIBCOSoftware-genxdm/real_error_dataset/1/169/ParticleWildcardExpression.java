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
import org.genxdm.xs.components.SchemaParticle;
import org.genxdm.xs.components.SchemaWildcard;

final class ParticleWildcardExpression implements ValidationExpr
{
	private final SchemaParticle m_particle;
	private final SchemaWildcard m_wildcard;

	public ParticleWildcardExpression(final SchemaParticle particle, final SchemaWildcard wildcard)
	{
		m_particle = PreCondition.assertArgumentNotNull(particle, "particle");
		m_wildcard = PreCondition.assertArgumentNotNull(wildcard, "wildcard");
	}

	public ParticleTerm getParticleTerm()
	{
		return m_wildcard;
	}

	public Iterable<ValidationExpr> getSubTerms()
	{
		// We are not building a deep finite state machine.
		return Collections.emptyList();
	}

	public boolean intersects(final ValidationExpr other)
	{
		if (other.isGroup())
		{
			return false;
		}

		final ParticleTerm term = other.getParticleTerm();

		if (term instanceof ElementDefinition)
		{
			final ElementDefinition element = (ElementDefinition)term;
			return m_wildcard.getNamespaceConstraint().allowsNamespaceName(element.getName().getNamespaceURI());
		}
		else if (term instanceof SchemaWildcard)
		{
			throw new UnsupportedOperationException("TODO");
			// return m_wildcard.intersects((SchemaWildcard)term);
		}
		else
		{
			System.out.println("Don't know how to deal with nameless " + other.getClass().getName());
			return false;
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
		return m_particle.isMaxOccursUnbounded();
	}

	public boolean isSequence()
	{
		return false;
	}

	public boolean matches(final QName token)
	{
		if (null != token)
		{
			return m_wildcard.getNamespaceConstraint().allowsNamespaceName(token.getNamespaceURI());
		}
		else
		{
			return false;
		}
	}

	public int maxOccurs()
	{
		return m_particle.getMaxOccurs();
	}

	public int minOccurs()
	{
		return m_particle.getMinOccurs();
	}

	@Override
	public String toString()
	{
		return m_wildcard.toString();
	}
}
