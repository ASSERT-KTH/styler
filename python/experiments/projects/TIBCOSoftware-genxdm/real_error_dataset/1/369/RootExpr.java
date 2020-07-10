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
import org.genxdm.Cursor;
import org.genxdm.nodes.Traverser;
import org.genxdm.nodes.TraversingInformer;
import org.genxdm.xpath.v10.TraverserDynamicContext;
import org.genxdm.xpath.v10.ExprContextDynamic;
import org.genxdm.xpath.v10.NodeIterator;
import org.genxdm.xpath.v10.NodeSetExpr;

/**
 * an XPath expression (component) that evaluates a sub-expression with a document root Node as the contextNode
 */
final class RootExpr
    extends ConvertibleNodeSetExprImpl 
{
	private final NodeSetExpr expr;

	/**
	 * construct with a sub-expression
	 */
	RootExpr(final NodeSetExpr expr)
	{
		super();
		this.expr = expr;
	}

	/**
	 * evaluate the sub-expression with the context node's root node
	 */
    @Override
	public <N> NodeIterator<N> nodeIterator(Model<N> model, final N contextNode, final ExprContextDynamic<N> dynEnv) {
		return expr.nodeIterator(model, model.getRoot(contextNode), dynEnv);
	}

    @Override
    public Traverser traverseNodes(TraversingInformer contextNode, TraverserDynamicContext dynEnv) {
        Cursor pc = contextNode.newPrecursor();
        pc.moveToRoot();
        return expr.traverseNodes(pc, dynEnv);
    }
}
