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

import java.lang.reflect.Array;

import org.genxdm.Model;
import org.genxdm.processor.xpath.v10.iterators.MergeNodeIterator;
import org.genxdm.processor.xpath.v10.iterators.NullNodeIterator;
import org.genxdm.processor.xpath.v10.iterators.UnionNodeIterator;
import org.genxdm.xpath.v10.ExprContextDynamic;
import org.genxdm.xpath.v10.ExprException;
import org.genxdm.xpath.v10.NodeIterator;
import org.genxdm.xpath.v10.extend.ConvertibleNodeSetExpr;

/**
 * an expression which composes two sub-expressions (for each node in expr1, evaluate expr2)
 */
final class ComposeExpr
    extends ConvertibleNodeSetExprImpl
{
	private final ConvertibleNodeSetExpr expr1;
	private final ConvertibleNodeSetExpr expr2;

	/**
	 * construct with two sub-expressions
	 */
	ComposeExpr(final ConvertibleNodeSetExpr expr1, final ConvertibleNodeSetExpr expr2)
	{
		super();
		this.expr1 = expr1;
		this.expr2 = expr2;
	}

	/**
	 * evaluate with a context node and an expression context
	 */
	@SuppressWarnings("unchecked")
	public <N> NodeIterator<N> nodeIterator(Model<N> model, final N contextNode, final ExprContextDynamic<N> dynEnv) throws ExprException
	{
		NodeIterator<N> iter = expr1.nodeIterator(model, contextNode, dynEnv);
		NodeIterator<N>[] iters = (NodeIterator<N>[])Array.newInstance(NodeIterator.class, 10);
		int length = 0;
		for (;;)
		{
			// for each node in the first expression
			// we build a NodeIterator for the second expression
			N tem = iter.next();
			if (tem == null)
			{
				// we've exhausted our supply of nodes in the
				// first expression
				break;
			}
			if (length == iters.length)
			{
				// we need a bigger array
				NodeIterator<N>[] oldIters = iters;
				iters = (NodeIterator<N>[])Array.newInstance(NodeIterator.class, oldIters.length * 2);
				System.arraycopy(oldIters, 0, iters, 0, oldIters.length);
			}
			iters[length++] = expr2.nodeIterator(model, tem, dynEnv);
		}

		// so, how many iterators did we build?
		switch (length)
		{
			case 0:
			{
				return new NullNodeIterator<N>();
			}
			case 1:
			{
				return iters[0];
			}
			case 2:
			{
				return new UnionNodeIterator<N>(iters[0], iters[1], model);
			}
		}
		return new MergeNodeIterator<N>(iters, length, model);
	}

	/**
     *
     */
	@Override
	public int getOptimizeFlags()
	{
		return expr1.getOptimizeFlags() & expr2.getOptimizeFlags();
	}
}
