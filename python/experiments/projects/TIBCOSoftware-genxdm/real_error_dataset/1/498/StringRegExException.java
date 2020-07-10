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
package org.genxdm.processor.w3c.xs.regex.string;

import org.genxdm.exceptions.PreCondition;

@SuppressWarnings("serial")
public final class StringRegExException extends Exception
{
	public enum Kind
	{
		blockBeginWIs, expectedBraceAfter, invalidAtomNoClosure, invalidChar, invalidCharRange, invalidCharRangeEx, invalidCommaQuantifier, invalidEmptyAtom, invalidExprMissing, invalidPattern, invalidPatternEmptyAtom, invalidPatternEmptyBranch, invalidQualifierNoClosure, invalidRangeQualifier, invalidSubtractionNoClosure, invalidUnicodeBlockName, invalidUnicodeCategory, unrecognizedEscapeChar
	}

	private final Object[] m_args;

	private final Kind m_key;

	public StringRegExException(final Kind key, final Object argument)
	{
		this(key, new Object[] { argument });
	}

	public StringRegExException(final Kind key, final Object[] argument)
	{
		m_key = PreCondition.assertArgumentNotNull(key, "key");
		m_args = argument;
	}

	/**
	 * Returns an argument array that can be passed to the format() method of a Java MessageFormat.
	 * 
	 * @return Argument parameters.
	 */
	public Object[] getArguments()
	{
		return m_args;
	}

	/**
	 * Returns the key that will be used to uniquely identify the message. This will be used as the key into a resource
	 * bundle.
	 * 
	 * @return The resouce bundle pattern key.
	 */
	public String getPatternKey()
	{
		return m_key.name();
	}

	/**
	 * The base name of the file that contains the message resources.
	 */
	public String getResourceBundleBaseName()
	{
		final String name = getClass().getName();
		return name.substring(0, name.lastIndexOf(".")) + ".exceptions";
	}
}
