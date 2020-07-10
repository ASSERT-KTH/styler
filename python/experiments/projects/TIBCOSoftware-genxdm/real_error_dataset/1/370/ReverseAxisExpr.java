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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.genxdm.Model;
import org.genxdm.Cursor;
import org.genxdm.nodes.Traverser;
import org.genxdm.nodes.TraversingInformer;
import org.genxdm.processor.xpath.v10.iterators.ListNodeIterator;
import org.genxdm.processor.xpath.v10.iterators.ListTraverser;
import org.genxdm.xpath.v10.TraverserDynamicContext;
import org.genxdm.xpath.v10.ExprContextDynamic;
import org.genxdm.xpath.v10.NodeIterator;

/**
 * A reverse axis (XPath) expression represents a Node set which may need to be seen in document order (thus reversed)
 */
abstract class ReverseAxisExpr 
    extends AxisExpr
{

	/**
	 * @return a version of this which, when evaluated, returns a Node iterator in document order
	 */
	@Override
	ConvertibleNodeSetExprImpl makeDocumentOrderExpr(final ConvertibleNodeSetExprImpl expr)
	{
		return new ConvertibleNodeSetExprImpl()
		{
            @Override
			public <N> NodeIterator<N> nodeIterator(Model<N> model, final N node, final ExprContextDynamic<N> dynEnv) {
				return reverse(expr.nodeIterator(model, node, dynEnv), model);
			}

            @Override
            public Traverser traverseNodes(TraversingInformer contextNode, TraverserDynamicContext dynEnv) {
                return reverse(expr.traverseNodes(contextNode, dynEnv));
            }
		};
	}

	private static <N> NodeIterator<N> reverse(final NodeIterator<N> iter, final Model<N> model)
	{
	    List<N> list = new ArrayList<N>(10);
		for (;;)
		{
			N node = iter.next();
			if (node == null)
				break;
			list.add(node);
		}
		Collections.reverse(list);
		return new ListNodeIterator<N>(list);
	}

    private static Traverser reverse(final Traverser iter)
    {
        List<Cursor> list = new ArrayList<Cursor>();
        for (;iter.moveToNext();) {
            Cursor cursor = iter.newPrecursor();
            list.add(cursor);
        }
        Collections.reverse(list);
        return new ListTraverser(list);
    }
}
