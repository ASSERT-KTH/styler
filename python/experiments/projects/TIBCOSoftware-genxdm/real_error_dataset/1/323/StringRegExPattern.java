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
package org.genxdm.processor.w3c.xs.regex.impl.string;

import java.util.List;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.processor.w3c.xs.regex.api.RegExFactory;
import org.genxdm.processor.w3c.xs.regex.api.RegExPattern;
import org.genxdm.processor.w3c.xs.regex.impl.nfa.NfaFactory;


public final class StringRegExPattern
{
	private final RegExPattern<StringRegExPatternTerm, String> pattern;
	// private static final RegExFactory<StringRegExPatternTerm, String> factory = new
	// DefaultRegExFactory<StringRegExPatternTerm, String>().getInstance();
	private static final RegExFactory<StringRegExPatternTerm, String> factory = new NfaFactory<StringRegExPatternTerm, String>();

	StringRegExPattern(final RegExPattern<StringRegExPatternTerm, String> pattern)
	{
		this.pattern = pattern;
	}

	/**
	 * Convenience for creating a pattern directly from a string representation.
	 */
	public static StringRegExPattern compile(final String regexp) throws StringRegExException
	{
		PreCondition.assertArgumentNotNull(regexp, "regexp");
		final StringRegExPatternTerm pt = StringRegExParser.parse(regexp);
		PreCondition.assertArgumentNotNull(pt, "pt");
		return new StringRegExPattern(factory.newPattern(pt, StringRegExBridge.SINGLETON));
	}

	public boolean matches(final String strval, final List<StringRegExPatternTerm> followers)
	{
		return pattern.matches(new StringRegExPatternInput(strval), followers);
	}

	@Override
	public String toString()
	{
		return pattern.toString();
	}
}
