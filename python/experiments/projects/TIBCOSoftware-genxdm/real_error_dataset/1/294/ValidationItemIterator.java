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

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.genxdm.exceptions.PreCondition;

/**
 * An {@link Iterator} for walking up the stack of {@link ValidationItem}s.
 */
final class ValidationItemIterator implements Iterator<ValidationItem>
{
	private ValidationItem m_pendingItem;

	public ValidationItemIterator(final ValidationItem pendingItem)
	{
		m_pendingItem = PreCondition.assertArgumentNotNull(pendingItem);
	}

	public boolean hasNext()
	{
		return (null != m_pendingItem);
	}

	public ValidationItem next() throws NoSuchElementException
	{
		if (null != m_pendingItem)
		{
			final ValidationItem nextItem = m_pendingItem;
			m_pendingItem = m_pendingItem.getParentItem();
			return nextItem;
		}
		else
		{
			throw new NoSuchElementException();
		}
	}

	public void remove() throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException();
	}
}
