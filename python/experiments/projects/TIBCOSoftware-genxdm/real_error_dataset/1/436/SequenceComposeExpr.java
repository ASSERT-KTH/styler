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
import org.genxdm.processor.xpath.v10.iterators.SequenceComposeNodeIterator;
import org.genxdm.processor.xpath.v10.iterators.SequenceComposeTraverser;
import org.genxdm.xpath.v10.TraverserDynamicContext;
import org.genxdm.xpath.v10.ExprContextDynamic;
import org.genxdm.xpath.v10.NodeIterator;
import org.genxdm.xpath.v10.extend.ConvertibleNodeSetExpr;

/**
 * Composition when expr1 is SINGLE_LEVEL and expr2 is STAYS_IN_SUBTREE.
 */
final class SequenceComposeExpr 
    extends ConvertibleNodeSetExprImpl
{
	private final ConvertibleNodeSetExpr expr1;
	private final ConvertibleNodeSetExpr expr2;

	SequenceComposeExpr(final ConvertibleNodeSetExpr expr1, final ConvertibleNodeSetExpr expr2)
	{
		super();
		this.expr1 = expr1;
		this.expr2 = expr2;
	}

    @Override
	public <N> NodeIterator<N> nodeIterator(Model<N> model, final N contextNode, final ExprContextDynamic<N> dynEnv) {
		return new SequenceComposeNodeIterator<N>(model, expr1.nodeIterator(model, contextNode, dynEnv), expr2, dynEnv);
	}

    @Override
    public Traverser traverseNodes(TraversingInformer contextNode, TraverserDynamicContext dynEnv) {
        return new SequenceComposeTraverser(expr1.traverseNodes(contextNode, dynEnv), expr2, dynEnv);
    }

    @Override
	public int getOptimizeFlags()
	{
		// if expr2 is SINGLE_LEVEL then this will be too
		return expr2.getOptimizeFlags();
	}

}
