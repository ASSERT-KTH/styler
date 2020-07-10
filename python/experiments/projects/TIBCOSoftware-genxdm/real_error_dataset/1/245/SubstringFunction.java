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

import org.genxdm.Model;
import org.genxdm.processor.xpath.v10.expressions.ConvertibleStringExpr;
import org.genxdm.processor.xpath.v10.expressions.NumberConstantExpr;
import org.genxdm.xpath.v10.ExprContextDynamic;
import org.genxdm.xpath.v10.ExprContextStatic;
import org.genxdm.xpath.v10.ExprException;
import org.genxdm.xpath.v10.ExprParseException;
import org.genxdm.xpath.v10.NumberExpr;
import org.genxdm.xpath.v10.StringExpr;
import org.genxdm.xpath.v10.extend.Function;
import org.genxdm.xpath.v10.extend.ConvertibleExpr;

public final class SubstringFunction 
    implements Function
{
	private final static boolean isLowSurrogate(char c)
	{
		return (c & 0xFC00) == 0xD800;
	}

	static final private String substring(final String str, double start, double len)
	{
		start = Math.floor(start + 0.5);
		double end = start + Math.floor(len + 0.5);
		int strLen = str.length();
		int pos = 1;
		int firstIndex = -1;
		int lastIndex = -1;
		for (int i = 0; i < strLen; i++, pos++)
		{
			if (pos >= start && pos < end)
			{
				if (firstIndex < 0)
					firstIndex = i;
				if (isLowSurrogate(str.charAt(i)))
					++i;
				lastIndex = i;
			}
			else if (isLowSurrogate(str.charAt(i)))
				++i;
		}
		if (firstIndex >= 0)
		{
			return str.substring(firstIndex, lastIndex + 1);
		}
		return "";
	}

	public ConvertibleExpr makeCallExpr(final ConvertibleExpr[] args, final ExprContextStatic statEnv) throws ExprParseException
	{
		if (args.length < 2 || args.length > 3)
		{
			throw new ExprParseException("expected 2 or 3 arguments");
		}
		final StringExpr se = args[0].makeStringExpr(statEnv);
		final NumberExpr ne1 = args[1].makeNumberExpr(statEnv);
		final NumberExpr ne2 = (args.length == 2 ? new NumberConstantExpr(1.0 / 0.0) : args[2].makeNumberExpr(statEnv));

		return new ConvertibleStringExpr()
		{
			public <N> String stringFunction(Model<N> model, final N node, final ExprContextDynamic<N> dynEnv) throws ExprException
			{
				return substring(se.stringFunction(model, node, dynEnv),
						ne1.numberFunction(model, node, dynEnv),
						ne2.numberFunction(model, node, dynEnv));
			}
		};
	}

}
