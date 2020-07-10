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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

final class UnaryIterable<E> implements List<E>
{
	private final E m_thing;

	public UnaryIterable(final E thing)
	{
		m_thing = thing;
	}

	public Iterator<E> iterator()
	{
		return new UnaryIterator<E>(m_thing);
	}

	public int size()
	{
		return (null != m_thing) ? 1 : 0;
	}

	public boolean isEmpty()
	{
		return (null == m_thing);
	}

	public E get(final int index)
	{
		if (null != m_thing)
		{
			if (0 == index)
			{
				return m_thing;
			}
			else
			{
				throw new IndexOutOfBoundsException();
			}
		}
		else
		{
			throw new IndexOutOfBoundsException();
		}
	}

	public boolean contains(Object o)
	{
		throw new UnsupportedOperationException("contains");
	}

	public Object[] toArray()
	{
		if (null != m_thing)
		{
			return new Object[] { m_thing };
		}
		else
		{
			return new Object[] {};
		}
	}

	public <T> T[] toArray(T[] a)
	{
		throw new UnsupportedOperationException("toArray");
	}

	public boolean add(E e)
	{
		throw new UnsupportedOperationException("add");
	}

	public boolean remove(Object o)
	{
		throw new UnsupportedOperationException("remove");
	}

	public boolean containsAll(Collection<?> c)
	{
		throw new UnsupportedOperationException("containsAll");
	}

	public boolean addAll(Collection<? extends E> c)
	{
		throw new UnsupportedOperationException("addAll");
	}

	public boolean addAll(int index, Collection<? extends E> c)
	{
		throw new UnsupportedOperationException("addAll");
	}

	public boolean removeAll(Collection<?> c)
	{
		throw new UnsupportedOperationException("removeAll");
	}

	public boolean retainAll(Collection<?> c)
	{
		throw new UnsupportedOperationException("retainAll");
	}

	public void clear()
	{
		throw new UnsupportedOperationException("clear");
	}

	public E set(int index, E element)
	{
		throw new UnsupportedOperationException("set");
	}

	public void add(int index, E element)
	{
		throw new UnsupportedOperationException("add");
	}

	public E remove(int index)
	{
		throw new UnsupportedOperationException("remove");
	}

	public int indexOf(Object o)
	{
		throw new UnsupportedOperationException("indexOf");
	}

	public int lastIndexOf(Object o)
	{
		throw new UnsupportedOperationException("lastIndexOf");
	}

	public ListIterator<E> listIterator()
	{
		throw new UnsupportedOperationException("listIterator");
	}

	public ListIterator<E> listIterator(int index)
	{
		throw new UnsupportedOperationException("listIterator");
	}

	public List<E> subList(int fromIndex, int toIndex)
	{
		throw new UnsupportedOperationException("subList");
	}

	@Override
	public String toString()
	{
		if (null != m_thing)
		{
			return m_thing.toString();
		}
		else
		{
			return "()";
		}
	}
}
