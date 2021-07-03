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

/**
 * A type-safe pair of objects.
 */
final class Pair<X, Y>
{
	private final X m_x;
	private final Y m_y;

	public Pair(final X x, final Y y)
	{
		m_x = x;
		m_y = y;
	}

	public X getX()
	{
		return m_x;
	}

	public X getFirst()
	{
		return m_x;
	}

	public X getLeft()
	{
		return m_x;
	}

	public Y getY()
	{
		return m_y;
	}

	public Y getSecond()
	{
		return m_y;
	}

	public Y getRight()
	{
		return m_y;
	}
}
