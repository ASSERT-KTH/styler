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

import java.util.List;

final class ValidationSupport
{
	private ValidationSupport()
	{
	}

	public static <A> boolean equalValues(final List<? extends A> expect, final List<? extends A> actual)
	{
		final int size = expect.size();
		if (size == actual.size())
		{
			for (int index = 0; index < size; index++)
			{
				if (!expect.get(index).equals(actual.get(index)))
				{
					return false;
				}
			}
			return true;
		}
		else
		{
			return false;
		}
	}
}
