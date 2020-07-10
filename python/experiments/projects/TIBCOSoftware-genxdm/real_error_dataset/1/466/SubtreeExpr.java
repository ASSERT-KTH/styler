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
import org.genxdm.nodes.Traverser;
import org.genxdm.nodes.TraversingInformer;
import org.genxdm.processor.xpath.v10.iterators.NodeIteratorOnIterator;
import org.genxdm.processor.xpath.v10.iterators.SequenceComposeNodeIterator;
import org.genxdm.processor.xpath.v10.iterators.SequenceComposeTraverser;
import org.genxdm.processor.xpath.v10.iterators.UnionNodeIterator;
import org.genxdm.processor.xpath.v10.iterators.UnionNodeTraverser;
import org.genxdm.xpath.v10.TraverserDynamicContext;
import org.genxdm.xpath.v10.ExprContextDynamic;
import org.genxdm.xpath.v10.NodeIterator;

/**
 * descendants-or-self(node())/E when E has STAYS_IN_SUBTREE
 */

final class SubtreeExpr
    extends ConvertibleNodeSetExprImpl
{
	private final ConvertibleNodeSetExprImpl expr;

	SubtreeExpr(final ConvertibleNodeSetExprImpl expr)
	{
		super();
		this.expr = expr;
	}

	@Override
	public int getOptimizeFlags()
	{
		return STAYS_IN_SUBTREE;
	}

    @Override
	public <N> NodeIterator<N> nodeIterator(Model<N> model, final N contextNode, ExprContextDynamic<N> dynEnv)
	{
		return new UnionNodeIterator<N>(expr.nodeIterator(model, contextNode, dynEnv),
				new SequenceComposeNodeIterator<N>(
						model, new NodeIteratorOnIterator<N>(
										model.getChildAxis(contextNode).iterator()), this, dynEnv), model);
	}

    @Override
    public Traverser traverseNodes(TraversingInformer contextNode, TraverserDynamicContext dynEnv) {
        return new UnionNodeTraverser(expr.traverseNodes(contextNode, dynEnv),
                new SequenceComposeTraverser(contextNode.traverseChildAxis(), this, dynEnv));
    }
}
