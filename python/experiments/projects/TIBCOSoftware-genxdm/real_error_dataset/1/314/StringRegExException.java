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
package org.genxdm.processor.w3c.xs.regex.api;

@SuppressWarnings("serial")
public final class StringRegExException extends Exception
{
	private final String m_pattern;
	private final int m_index;

	public StringRegExException(final Throwable cause, final String pattern, final int index)
	{
		super(cause);
		m_pattern = PreCondition.assertArgumentNotNull(pattern, "pattern");
		m_index = index;
	}

	public String getPattern()
	{
		return m_pattern;
	}

	public int getIndex()
	{
		return m_index;
	}
}
