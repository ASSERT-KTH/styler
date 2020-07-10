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

import org.genxdm.processor.w3c.xs.regex.api.RegExBridge;

enum StringRegExBridge implements RegExBridge<StringRegExPatternTerm, String>
{
	SINGLETON;

	public boolean isSequence(final StringRegExPatternTerm expression)
	{
		return expression.isSequence();
	}

	public boolean isChoice(final StringRegExPatternTerm expression)
	{
		return expression.isChoice();
	}

	public boolean isInterleave(final StringRegExPatternTerm expression)
	{
		return expression.isInterleave();
	}

	public Iterable<StringRegExPatternTerm> getSubTerms(final StringRegExPatternTerm expression)
	{
		return expression.getSubTerms();
	}

	public boolean matches(final StringRegExPatternTerm expression, final String token)
	{
		return expression.matches(token);
	}

	public boolean intersects(final StringRegExPatternTerm e1, final StringRegExPatternTerm e2)
	{
		return e1.intersects(e2);
	}

	public int minOccurs(final StringRegExPatternTerm expression)
	{
		return expression.minOccurs();
	}

	public int maxOccurs(final StringRegExPatternTerm expression)
	{
		return expression.maxOccurs();
	}

	public StringRegExPatternTerm prime(final StringRegExPatternTerm expression)
	{
		return expression;
	}
}
