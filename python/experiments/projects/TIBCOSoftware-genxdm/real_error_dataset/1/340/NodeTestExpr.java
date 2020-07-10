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
package org.genxdm.processor.xpath.v10.expressions;

import org.genxdm.Model;
import org.genxdm.processor.xpath.v10.patterns.Pattern;
import org.genxdm.xpath.v10.ExprContextDynamic;
import org.genxdm.xpath.v10.ExprException;
import org.genxdm.xpath.v10.NodeIterator;

/**
 *
 */
final class NodeTestExpr
    extends ConvertibleNodeSetExprImpl
{
	private final Pattern nodeTest;
	private final ConvertibleNodeSetExprImpl expr;

	NodeTestExpr(final ConvertibleNodeSetExprImpl expr, final Pattern nodeTest)
	{
		super();
		this.expr = expr;
		this.nodeTest = nodeTest;
	}

	public <N> NodeIterator<N> nodeIterator(final Model<N> model, final N node, final ExprContextDynamic<N> dynEnv) throws ExprException
	{
		final NodeIterator<N> iter = expr.nodeIterator(model, node, dynEnv);
		return new NodeIterator<N>()
		{
			public N next() throws ExprException
			{
				for (;;)
				{
					N temp = iter.next();
					if (temp == null)
					{
						break;
					}
					if (nodeTest.matches(model, temp, dynEnv))
					{
						return temp;
					}
				}
				return null;
			}
		};
	}

	@Override
	public int getOptimizeFlags()
	{
		return expr.getOptimizeFlags();
	}

	Pattern getChildrenNodePattern()
	{
		if (expr.getClass() == ChildAxisExpr.class)
		{
			return nodeTest;
		}
		return null;
	}
}
