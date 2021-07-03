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
package org.genxdm.processor.xpath.v10.functions;

import org.genxdm.processor.xpath.v10.expressions.WrappedBooleanExpr;
import org.genxdm.xpath.v10.ExprContextStatic;
import org.genxdm.xpath.v10.ExprParseException;
import org.genxdm.xpath.v10.extend.ConvertibleExpr;

/**
 * a single argument XPath function which casts its argument to a boolean Function: boolean boolean(object)
 * 
 * <p>
 * The boolean function converts its argument to a boolean as follows:
 * </p>
 *<ul>
 * <li>a number is true if and only if it is neither positive or negative zero nor NaN</li>
 * 
 * <li>a node-set is true if and only if it is non-empty</li>
 * 
 * <li>a string is true if and only if its length is non-zero</li>
 * 
 * <li>an object of a type other than the four basic types is converted to a boolean in a way that is dependent on that type</li>
 *</ul>
 */
public class BooleanFunction 
    extends Function1
{

	ConvertibleExpr makeCallExpr(final ConvertibleExpr e, final ExprContextStatic statEnv) throws ExprParseException
	{
		// ConvertibleExprs know how to cast themselves to booleans
		return WrappedBooleanExpr.wrap(e.makeBooleanExpr(statEnv) );
	}
}
