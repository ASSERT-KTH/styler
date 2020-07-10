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
import java.util.HashSet;
import java.util.Stack;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.components.AttributeDefinition;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.components.ModelGroup;
import org.genxdm.xs.components.ParticleTerm;
import org.genxdm.xs.components.SchemaParticle;
import org.genxdm.xs.components.SchemaWildcard;

final class ContentModelExpression implements ValidationExpr
{
	private static  void compile(final SchemaParticle particle, final ArrayList<ValidationExpr> subTerms)
	{
		final ParticleTerm term = particle.getTerm();

		if (term instanceof ElementDefinition)
		{
			final ElementDefinition element = (ElementDefinition)term;
			if (element.hasSubstitutionGroupMembers())
			{
				// Build a set of substitution element choices (which includes the original element).
				final Stack<ElementDefinition> stack = new Stack<ElementDefinition>();
				stack.push(element);
				final HashSet<ElementDefinition> choices = new HashSet<ElementDefinition>();
				while (!stack.isEmpty())
				{
					final ElementDefinition popped = stack.pop();

					if (!popped.isAbstract())
					{
						choices.add(popped);
					}

					for (final ElementDefinition substitution : popped.getSubstitutionGroupMembers())
					{
						stack.push(substitution);
					}
				}
				if (particle.isMaxOccursUnbounded())
				{
					subTerms.add(new SubstitutionGroupExpression(particle.getMinOccurs(), choices));
				}
				else
				{
					subTerms.add(new SubstitutionGroupExpression(particle.getMinOccurs(), particle.getMaxOccurs(), choices));
				}
			}
			else
			{
				if (particle.isMaxOccursUnbounded())
				{
					subTerms.add(new ParticleElementExpression(particle.getMinOccurs(), element));
				}
				else
				{
					subTerms.add(new ParticleElementExpression(particle.getMinOccurs(), particle.getMaxOccurs(), element));
				}
			}
		}
		else if (term instanceof AttributeDefinition)
		{
			throw new AssertionError(term);
		}
		else if (term instanceof ModelGroup)
		{
			throw new RuntimeException();
			// subTerms.add(new ModelGroupExpression(particle, (ModelGroup)term));
		}
		else if (term instanceof SchemaWildcard)
		{
			throw new RuntimeException();
			// subTerms.add(new ParticleWildcardExpression(particle, (SchemaWildcard)term));
		}
		else
		{
			throw new RuntimeException();
		}
	}

	private static  Iterable<ValidationExpr> compileSubTerms(final SchemaParticle particle)
	{
		final ArrayList<ValidationExpr> subTerms = new ArrayList<ValidationExpr>();
		compile(particle, subTerms);
		return subTerms;
	}

	private final SchemaParticle m_particle;

	private final Iterable<ValidationExpr> m_subTerms;

	public ContentModelExpression(final SchemaParticle particle)
	{
		m_particle = PreCondition.assertArgumentNotNull(particle, "particle");
		m_subTerms = compileSubTerms(particle);
	}

	public ParticleTerm getParticleTerm()
	{
		return m_particle.getTerm();
	}

	public Iterable<ValidationExpr> getSubTerms()
	{
		return m_subTerms;
	}

	public boolean intersects(final ValidationExpr other)
	{
		throw new RuntimeException();
	}

	public boolean isChoice()
	{
		return false;
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
		return m_particle.isMaxOccursUnbounded();
	}

	public boolean isSequence()
	{
		return true;
	}

	public boolean matches(final QName token)
	{
		throw new RuntimeException();
	}

	public int maxOccurs()
	{
		return m_particle.getMaxOccurs();
	}

	public int minOccurs()
	{
		return m_particle.getMinOccurs();
	}
}
