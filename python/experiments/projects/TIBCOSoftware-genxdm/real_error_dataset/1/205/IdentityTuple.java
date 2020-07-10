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
import java.util.Iterator;

import org.genxdm.exceptions.PreCondition;

/**
 * A tuple is an ordered list of keys values.
 */
final class IdentityTuple
{
	private final ArrayList<IdentityKey> m_keys;

	public IdentityTuple(final ArrayList<IdentityKey> keys)
	{
		// This invariant may not hold up to the test of time.
		this.m_keys = PreCondition.assertArgumentNotNull(keys, "keys");
	}

	public ArrayList<IdentityKey> getKeys()
	{
		return m_keys;
	}

	@Override
	public int hashCode()
	{
		return m_keys.size();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(final Object obj)
	{
		if (obj instanceof IdentityTuple)
		{
			final IdentityTuple other = (IdentityTuple)obj;
			return equalKeys(m_keys, other.m_keys);
		}
		else
		{
			return false;
		}
	}

	private static <A> boolean equalKeys(final ArrayList<IdentityKey> expect, final ArrayList<IdentityKey> actual)
	{
		final Iterator<IdentityKey> lhs = expect.iterator();
		final Iterator<IdentityKey> rhs = actual.iterator();
		while (lhs.hasNext())
		{
			final IdentityKey lhsAtom = lhs.next();
			if (rhs.hasNext())
			{
				final IdentityKey rhsAtom = rhs.next();
				if (!lhsAtom.equals(rhsAtom))
				{
					return false;
				}
			}
			else
			{
				return false;
			}
		}
		return !rhs.hasNext();
	}
}
