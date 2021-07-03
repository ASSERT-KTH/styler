/*
 * Portions copyright (c) 1998-1999, James Clark : see copyingjc.txt for
 * license details
 * Portions copyright (c) 2002, Bill Lindsey : see copying.txt for license
 * details
 * 
 * Portions copyright (c) 2009-2011 TIBCO Software Inc.
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
package org.genxdm.processor.xpath.v10.patterns;

import java.lang.reflect.Array;

import org.genxdm.Model;
import org.genxdm.xpath.v10.ExprContextDynamic;
import org.genxdm.xpath.v10.ExprException;

/**
 * represents an "OR" (union) of match patterns
 */
class AlternativesPattern
    implements TopLevelPattern
{
	private final TopLevelPattern pattern1;
	private final PathPattern pattern2;

	/**
	 * construct with a head pattern1 and tail pattern2
	 */
	AlternativesPattern(final TopLevelPattern pattern1, final PathPattern pattern2)
	{
		this.pattern1 = pattern1;
		this.pattern2 = pattern2;
	}

	/**
	 * evaluate to a boolean
	 */
	public <N> boolean matches(Model<N> model, final N node, final ExprContextDynamic<N> dynEnv) throws ExprException
	{
		return pattern1.matches(model, node, dynEnv) || pattern2.matches(model, node, dynEnv);
	}

	/**
	 * @return an array of all the alternative PathPatterns
	 */
	public PathPattern[] getAlternatives()
	{
		// we decompose a backwards sort of lisp-like list
		final PathPattern[] tem = pattern1.getAlternatives();
		final PathPattern[] result = (PathPattern[])Array.newInstance(PathPattern.class, tem.length + 1);
		System.arraycopy(tem, 0, result, 0, tem.length);
		result[result.length - 1] = pattern2;
		return result;
	}
}
