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
import org.genxdm.nodes.Traverser;
import org.genxdm.nodes.TraversingInformer;
import org.genxdm.processor.xpath.v10.expressions.ConvertibleExprImpl;
import org.genxdm.processor.xpath.v10.expressions.ConvertibleNumberExpr;
import org.genxdm.xpath.v10.Converter;
import org.genxdm.xpath.v10.TraverserDynamicContext;
import org.genxdm.xpath.v10.NodeDynamicContext;
import org.genxdm.xpath.v10.StaticContext;
import org.genxdm.xpath.v10.ExprParseException;
import org.genxdm.xpath.v10.NodeIterator;
import org.genxdm.xpath.v10.NodeSetExpr;
import org.genxdm.xpath.v10.extend.ConvertibleExpr;

public final class SumFunction 
    extends Function1
{
	static private final <N> double sum(final NodeIterator<N> iter, final Model<N> model) {
		double n = 0.0;
		for (;;)
		{
			N node = iter.next();
			if (node == null)
			{
				break;
			}
			n += Converter.toNumber(Converter.toString(node, model));
		}
		return n;
	}

    static private final <N> double sum(final Traverser iter)
    {
        double n = 0.0;
        while(iter.moveToNext())
        {
            n += Converter.toNumber(Converter.toString(iter));
        }
        return n;
    }

	ConvertibleExprImpl makeCallExpr(final ConvertibleExpr e, final StaticContext statEnv) throws ExprParseException
	{
		final NodeSetExpr nse = e.makeNodeSetExpr(statEnv);

		return new ConvertibleNumberExpr()
		{
			public <N> double numberFunction(Model<N> model, final N contextNode, final NodeDynamicContext<N> dynEnv) {
				return sum(nse.nodeIterator(model, contextNode, dynEnv), model);
			}

            @Override
            public double numberFunction(TraversingInformer contextNode,
                    TraverserDynamicContext dynEnv) {
                return sum(nse.traverseNodes(contextNode, dynEnv));
            }
		};
	}
}
