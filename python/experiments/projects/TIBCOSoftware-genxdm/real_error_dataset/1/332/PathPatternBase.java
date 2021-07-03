/**
 * Portions copyright (c) 1998-1999, James Clark : see copyingjc.txt for
 * license details
 * Portions copyright (c) 2002, Bill Lindsey : see copying.txt for license
 * details
 * 
 * Portions copyright (c) 2009-2010 TIBCO Software Inc.
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

import org.genxdm.NodeKind;

/**
 * a (component of) a compiled XPath pattern expression
 */
abstract public class PathPatternBase
    implements PathPattern, TopLevelPattern
{
	/**
	 * by default, only return a list of length one (itself)
	 */
	public PathPattern[] getAlternatives()
	{
		final PathPattern[] result = (PathPattern[])Array.newInstance(PathPattern.class, 1);
		result[0] = this;
		return result;
	}

	abstract public String getMatchNamespaceURI();

	abstract public String getMatchLocalName();

	abstract public NodeKind getMatchNodeType();
}
