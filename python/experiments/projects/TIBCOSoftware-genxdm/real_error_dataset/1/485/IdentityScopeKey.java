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
import java.util.HashMap;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.processor.w3c.xs.exception.cvc.CvcMissingKeyFieldException;
import org.genxdm.processor.w3c.xs.exception.src.SrcDuplicateKeyTargetException;
import org.genxdm.xs.constraints.IdentityConstraint;
import org.genxdm.xs.constraints.IdentityConstraintKind;
import org.genxdm.xs.exceptions.AbortException;
import org.genxdm.xs.exceptions.SchemaExceptionHandler;
import org.genxdm.xs.resolve.LocationInSchema;


/**
 * Specialization of a scope for xs:key and xs:unique.
 * 
 * Note: According to the XML Schema Part 1 Specification, both xs:key and xs:unique can be referenced by xs:keyref.
 * This is why this class is used to represent both xs:key and xs:unique. However, there are some differences. Both key
 * and unique assert uniqueness, with respect to the content identified by the selector, of the tuples resulting from
 * the fields. Only key further requires that all selected content has such tuples.
 */
final class IdentityScopeKey extends IdentityScope
{
	public final HashMap<IdentityTuple, IdentityVariant> m_qualifiedTargets = new HashMap<IdentityTuple, IdentityVariant>();

	public IdentityScopeKey(final int elementIndex, final IdentityConstraint constraint, final SchemaExceptionHandler errorHandler, final LocationInSchema location)
	{
		super(elementIndex, constraint, errorHandler, location);
	}

	@Override
	protected void onKeysComplete(final ArrayList<IdentityKey> keyValues, final int elementIndex) throws AbortException
	{
	    final IdentityTuple key = new IdentityTuple(keyValues);

		final IdentityVariant mapped = m_qualifiedTargets.get(key);

		if (mapped == null || mapped.isDanglingRefs())
		{
			// List was the undeclared refs
			m_qualifiedTargets.put(key, new IdentityVariant(Boolean.TRUE));
		}
		else if (mapped.isValue())
		{
			m_errorHandler.error(new SrcDuplicateKeyTargetException(getConstraint().getName(), keyValues, m_location));
		}
		else
		{
			// Unexpected.
			throw new AssertionError();
		}
	}

	@Override
	protected void onScopeEnd(final int elementIndex, final Locatable locatable) throws AbortException
	{
		final IdentityConstraint constraint = getConstraint();
		final IdentityConstraintKind category = constraint.getCategory();
		// xs:key must have bound values while xs:unique need not exist.
		if (category.isKey())
		{
			final ArrayList<IdentityField> elementHandlers = m_fieldEvals.get(elementIndex);
			PreCondition.assertArgumentNotNull(elementHandlers, "elementHandlers");
			if (m_boundFields.get(elementIndex) < elementHandlers.size())
			{
				final LocationInSchema frozenLocation = locatable.getLocation();
				for (int i = 0; i < elementHandlers.size(); i++)
				{
					m_errorHandler.error(new CvcMissingKeyFieldException(constraint.getName(), i + 1, frozenLocation));
				}
			}
		}
	}
}
