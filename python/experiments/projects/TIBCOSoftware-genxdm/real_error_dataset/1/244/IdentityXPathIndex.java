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
 * An (experimental) abstraction to simplify the handling of an index into an XPath expression that consists of an array
 * of steps.
 */
final class IdentityXPathIndex
{
	/**
	 * The current index.
	 */
	private int m_index;
	/**
	 * The upper bound index.
	 */
	private final int m_upper;

	public IdentityXPathIndex(final int initial, final int upperBound)
	{
		m_index = initial;
		m_upper = upperBound;
	}

	public int value()
	{
		return m_index;
	}

	public boolean canAdvance()
	{
		return m_index < m_upper;
	}

	/**
	 * Advances the index.
	 */
	public void advance()
	{
		m_index++;
	}

	public boolean canDecrement()
	{
		return m_index > 0;
	}

	/**
	 * Decrements the index.
	 */
	public void decrement()
	{
		m_index--;
	}

	/**
	 * Determines whether the index is below the lower bound for the array.
	 */
	public boolean isBelow()
	{
		return m_index < 0;
	}

	/**
	 * Determines whether the index is above the upper bound for the array.
	 */
	public boolean isAbove()
	{
		return m_index > m_upper;
	}

	/**
	 * Determines whether the index is equal to the upper bound for the array.
	 */
	public boolean isUpperBound()
	{
		return m_index == m_upper;
	}

	/**
	 * Determines whether the index is within the upper and lower bounds.
	 */
	public boolean inBounds()
	{
		return m_index >= 0 && m_index <= m_upper;
	}
}
