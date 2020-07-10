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

import java.lang.reflect.Array;

import org.genxdm.Model;
import org.genxdm.processor.xpath.v10.expressions.ConvertibleStringExpr;
import org.genxdm.xpath.v10.ExprContextDynamic;
import org.genxdm.xpath.v10.ExprContextStatic;
import org.genxdm.xpath.v10.ExprException;
import org.genxdm.xpath.v10.ExprParseException;
import org.genxdm.xpath.v10.StringExpr;
import org.genxdm.xpath.v10.extend.Function;
import org.genxdm.xpath.v10.extend.ConvertibleExpr;

/**
 * represents the XPath Function: string concat(string, string, string*)
 * 
 * has the method makeCallExpr which will construct a String expression representing the concat function
 */
public final class ConcatFunction 
    implements Function
{

	public ConvertibleExpr makeCallExpr(final ConvertibleExpr[] args, final ExprContextStatic statEnv) throws ExprParseException
	{
		final StringExpr[] se = (StringExpr[])Array.newInstance(StringExpr.class, args.length);
		for (int i = 0; i < se.length; i++)
		{
			se[i] = args[i].makeStringExpr(statEnv);
		}

		return new ConvertibleStringExpr()
		{
			public <N> String stringFunction(Model<N> model, final N node, final ExprContextDynamic<N> dynEnv) throws ExprException
			{
				final StringBuilder buf = new StringBuilder();
				for (int i = 0; i < se.length; i++)
				{
					buf.append(se[i].stringFunction(model, node, dynEnv));
				}
				return buf.toString();
			}
		};
	}
}
