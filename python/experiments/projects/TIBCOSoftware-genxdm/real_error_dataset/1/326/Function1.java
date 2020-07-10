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

import org.genxdm.xpath.v10.ExprContextStatic;
import org.genxdm.xpath.v10.ExprParseException;
import org.genxdm.xpath.v10.extend.Function;
import org.genxdm.xpath.v10.extend.ConvertibleExpr;

/**
 * base class for all functions taking one arguments
 */
abstract class Function1 
    implements Function
{

	abstract ConvertibleExpr makeCallExpr(ConvertibleExpr e, ExprContextStatic statEnv) throws ExprParseException;

	public ConvertibleExpr makeCallExpr(final ConvertibleExpr[] e, final ExprContextStatic statEnv) throws ExprParseException
	{
		if (e.length != 1)
		{
			throw new ExprParseException("expected one argument");
		}
		return makeCallExpr(e[0], statEnv);
	}
}
