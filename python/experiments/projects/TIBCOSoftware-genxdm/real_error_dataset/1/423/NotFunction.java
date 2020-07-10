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
import org.genxdm.processor.xpath.v10.expressions.ConvertibleBooleanExpr;
import org.genxdm.processor.xpath.v10.expressions.ConvertibleExprImpl;
import org.genxdm.xpath.v10.BooleanExpr;
import org.genxdm.xpath.v10.TraverserDynamicContext;
import org.genxdm.xpath.v10.ExprContextDynamic;
import org.genxdm.xpath.v10.ExprContextStatic;
import org.genxdm.xpath.v10.extend.ConvertibleExpr;

public final class NotFunction 
    extends Function1
{

	ConvertibleExprImpl makeCallExpr(final ConvertibleExpr e, final ExprContextStatic statEnv)
	{
		final BooleanExpr be = e.makeBooleanExpr(statEnv);

		return new ConvertibleBooleanExpr()
		{
            @Override
			public <N> boolean booleanFunction(Model<N> model, final N node, final ExprContextDynamic<N> dynEnv) {
				return !be.booleanFunction(model, node, dynEnv);
			}

            @Override
            public boolean booleanFunction(TraversingInformer contextNode, TraverserDynamicContext dynEnv) {
                return !be.booleanFunction(contextNode, dynEnv);
            }
		};
	}
}
