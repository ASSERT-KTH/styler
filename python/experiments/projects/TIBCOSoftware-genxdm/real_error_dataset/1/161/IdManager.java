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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.processor.w3c.xs.exception.cvc.CvcDanglingIDReferenceException;
import org.genxdm.processor.w3c.xs.exception.sm.SmDuplicateIDException;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.exceptions.AbortException;
import org.genxdm.xs.exceptions.SchemaExceptionHandler;
import org.genxdm.xs.resolve.LocationInSchema;
import org.genxdm.xs.types.SimpleType;


/**
 * Responsible for implementing ID/IDREF functionality.
 */
final class IdManager
{
	private final HashSet<String> m_definedIds = new HashSet<String>();
	private final HashMap<String, List<LocationInSchema>> m_danglingIDREFLocations = new HashMap<String, List<LocationInSchema>>();

	public <A> void text(final List<? extends A> actualValue, final SimpleType actualType, final Locatable locatable, final SchemaExceptionHandler errors, final AtomBridge<A> atomBridge) throws AbortException
	{
		value(actualValue, actualType, locatable, errors, atomBridge);
	}

	public <A> void attribute(final List<? extends A> actualValue, final SimpleType actualType, final Locatable locatable, final SchemaExceptionHandler errors, final AtomBridge<A> atomBridge) throws AbortException
	{
		value(actualValue, actualType, locatable, errors, atomBridge);
	}

	private <A> void value(final List<? extends A> actualValue, final SimpleType actualType, final Locatable locatable, final SchemaExceptionHandler errors, final AtomBridge<A> atomBridge) throws AbortException
	{
		if (actualType.isID())
		{
			processId(castAsString(actualValue.iterator().next(), atomBridge), locatable, atomBridge, errors);
		}
		else if (actualType.isIDREF())
		{
			processIdRef(castAsString(actualValue.iterator().next(), atomBridge), locatable);
		}
		else if (actualType.isIDREFS())
		{
			for (final A idref : actualValue)
			{
				processIdRef(castAsString(idref, atomBridge), locatable);
			}
		}
	}

	private <A> String castAsString(final A atom, final AtomBridge<A> atomBridge)
	{
		PreCondition.assertArgumentNotNull(atom, "atom");
		PreCondition.assertArgumentNotNull(atomBridge, "atomBridge");
		return atomBridge.getC14NForm(atom);
	}

	public <A> void processId(final String id, final Locatable locatable, final AtomBridge<A> atomBridge, final SchemaExceptionHandler errors) throws AbortException
	{
		if (!m_definedIds.add(id))
		{
			errors.error(new SmDuplicateIDException(id, locatable.getLocation()));
		}

		m_danglingIDREFLocations.remove(id);
	}

	private void processIdRef(final String idref, final Locatable locatable)
	{
		if (m_definedIds.contains(idref))
		{
			m_danglingIDREFLocations.remove(idref);
		}
		else
		{
			if (m_danglingIDREFLocations.containsKey(idref))
			{
				m_danglingIDREFLocations.get(idref).add(locatable.getLocation());
			}
			else
			{
				final LinkedList<LocationInSchema> locations = new LinkedList<LocationInSchema>();
				locations.add(locatable.getLocation());
				m_danglingIDREFLocations.put(idref, locations);
			}
		}
	}

	public void reportDanglingIdRefs(final SchemaExceptionHandler errors) throws AbortException
	{
		if (m_danglingIDREFLocations.size() != 0)
		{
			for (final String idref : m_danglingIDREFLocations.keySet())
			{
				final List<LocationInSchema> locations = m_danglingIDREFLocations.get(idref);

				for (final LocationInSchema location : locations)
				{
					errors.error(new CvcDanglingIDReferenceException(idref, location));
				}
			}
		}

		reset();
	}

	public void reset()
	{
		m_definedIds.clear();
		m_danglingIDREFLocations.clear();
	}
}
