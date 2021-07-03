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
import org.genxdm.nodes.TraversingInformer;
import org.genxdm.processor.xpath.v10.expressions.ConvertibleExprImpl;
import org.genxdm.processor.xpath.v10.expressions.ConvertibleStringExpr;
import org.genxdm.xpath.v10.TraverserDynamicContext;
import org.genxdm.xpath.v10.NodeDynamicContext;
import org.genxdm.xpath.v10.StaticContext;
import org.genxdm.xpath.v10.StringExpr;
import org.genxdm.xpath.v10.extend.ConvertibleExpr;

public final class TranslateFunction 
    extends Function3
{
	private static String translate(final String s1, final String s2, final String s3)
	{
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < s1.length(); i++)
		{
			char c = s1.charAt(i);
			// FIXME deal with surrogates properly
			int j = s2.indexOf(c);
			if (j < s3.length())
				buf.append(j < 0 ? c : s3.charAt(j));
		}
		return buf.toString();
	}

	ConvertibleExprImpl makeCallExpr(final ConvertibleExpr e1, final ConvertibleExpr e2, final ConvertibleExpr e3, final StaticContext statEnv)
	{
		final StringExpr se1 = e1.makeStringExpr(statEnv);
		final StringExpr se2 = e2.makeStringExpr(statEnv);
		final StringExpr se3 = e3.makeStringExpr(statEnv);

		return new ConvertibleStringExpr()
		{
			public <N> String stringFunction(Model<N> model, final N node, final NodeDynamicContext<N> dynEnv) {
				return translate(se1.stringFunction(model, node, dynEnv),
						se2.stringFunction(model, node, dynEnv),
						se3.stringFunction(model, node, dynEnv));
			}

            @Override
            public String stringFunction(TraversingInformer contextNode, TraverserDynamicContext dynEnv) {
                return translate(se1.stringFunction(contextNode, dynEnv),
                        se2.stringFunction(contextNode, dynEnv),
                        se3.stringFunction(contextNode, dynEnv));
            }
		};
	}
}
