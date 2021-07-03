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
package org.genxdm.processor.w3c.xs.validation.api;

import org.genxdm.exceptions.PreCondition;

/**
 * A mapping from a key to a value. <br/>
 * The key cannot be null. The value may be null.
 */
public final class VxMapping<K, V>
{
	private final K key;
	private final V value;

	public VxMapping(final K key, final V value)
	{
		this.key = PreCondition.assertArgumentNotNull(key);
		this.value = value;
	}

	/**
	 * The key part of the mapping.
	 */
	public K getKey()
	{
		return key;
	}

	/**
	 * The value part of the mapping.
	 */
	public V getValue()
	{
		return value;
	}

	@Override
	public String toString()
	{
		return key + " => " + value;
	}
}