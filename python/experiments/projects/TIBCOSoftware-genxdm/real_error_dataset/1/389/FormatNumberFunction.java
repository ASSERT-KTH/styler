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

import java.text.DecimalFormat;

import org.genxdm.Model;
import org.genxdm.nodes.TraversingInformer;
import org.genxdm.processor.xpath.v10.expressions.ConvertibleExprImpl;
import org.genxdm.processor.xpath.v10.expressions.ConvertibleStringExpr;
import org.genxdm.xpath.v10.TraverserDynamicContext;
import org.genxdm.xpath.v10.NodeDynamicContext;
import org.genxdm.xpath.v10.StaticContext;
import org.genxdm.xpath.v10.NumberExpr;
import org.genxdm.xpath.v10.StringExpr;
import org.genxdm.xpath.v10.extend.ConvertibleExpr;

public final class FormatNumberFunction 
    extends Function2
{

	ConvertibleExprImpl makeCallExpr(final ConvertibleExpr e1, final ConvertibleExpr e2, StaticContext statEnv)
	{
		final NumberExpr ne = e1.makeNumberExpr(statEnv);
		final StringExpr se = e2.makeStringExpr(statEnv);

		return new ConvertibleStringExpr()
		{
            @Override
			public <N> String stringFunction(Model<N> model, final N node, final NodeDynamicContext<N> context) {
				try {
					return new DecimalFormat(se.stringFunction(model, node, context)).format(ne.numberFunction(model, node, context));
				}
				catch (final IllegalArgumentException e) {
					throw new RuntimeException("invalid format pattern");
				}
			}

            @Override
            public String stringFunction(TraversingInformer contextNode, TraverserDynamicContext dynEnv) {
                try {
                    return new DecimalFormat(se.stringFunction(contextNode, dynEnv)).format(ne.numberFunction(contextNode, dynEnv));
                }
                catch (final IllegalArgumentException e) {
                    throw new RuntimeException("invalid format pattern", e);
                }
            }
		};
	}
}
