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
import org.genxdm.Precursor;
import org.genxdm.nodes.Traverser;
import org.genxdm.nodes.TraversingInformer;
import org.genxdm.processor.xpath.v10.expressions.ConvertibleExprImpl;
import org.genxdm.processor.xpath.v10.expressions.ConvertibleNodeSetExprImpl;
import org.genxdm.processor.xpath.v10.iterators.SingleNodeIterator;
import org.genxdm.processor.xpath.v10.iterators.SingleTraverser;
import org.genxdm.xpath.v10.TraverserDynamicContext;
import org.genxdm.xpath.v10.ExprContextDynamic;
import org.genxdm.xpath.v10.ExprContextStatic;
import org.genxdm.xpath.v10.ExprParseException;
import org.genxdm.xpath.v10.NodeIterator;
import org.genxdm.xpath.v10.NodeSetExpr;
import org.genxdm.xpath.v10.StringExpr;
import org.genxdm.xpath.v10.TraverserVariant;
import org.genxdm.xpath.v10.Variant;
import org.genxdm.xpath.v10.VariantExpr;
import org.genxdm.xpath.v10.extend.ConvertibleExpr;

/**
 * the XPath function id(x)
 */
public final class IdFunction
    extends Function1
{

	private final <N> NodeIterator<N> id(final N node, final NodeIterator<N> iter)
	{
	    // TODO:
	    // for each node in the set, call: id(node, StringFunction(iter.next()).stringFunction())
	    // (or something like that: turn the node into a string representing an id)
	    // collect the results of each call, return the iterator over the entire collection.
		throw new UnsupportedOperationException("TODO: id()");
	}

    private final Traverser id(final TraversingInformer node, final Traverser iter)
    {
        // TODO:
        // for each node in the set, call: id(node, StringFunction(iter.next()).stringFunction())
        // (or something like that: turn the node into a string representing an id)
        // collect the results of each call, return the iterator over the entire collection.
        throw new UnsupportedOperationException("TODO: id()");
    }

	private final <N> NodeIterator<N> id(Model<N> model, final N node, final String str)
	{
		// TODO - review:
		// The following seems a little to simplistic - it just always returns the node with the given ID? 
		N result = model.getElementById(node, str);
		return new SingleNodeIterator<N>(result);
	}

    private final Traverser id(TraversingInformer node, final String str)
    {
        // TODO - review:
        // The following seems a little to simplistic - it just always returns the node with the given ID?
        Precursor result = node.newPrecursor();
        result.moveToElementById(str);
        return new SingleTraverser(result);
    }

	ConvertibleExprImpl makeCallExpr(final ConvertibleExpr e, final ExprContextStatic statEnv) throws ExprParseException
	{
		if (e instanceof NodeSetExpr)
		{
			final NodeSetExpr nse = (NodeSetExpr)e;
			return new ConvertibleNodeSetExprImpl()
			{
                @Override
				public <N> NodeIterator<N> nodeIterator(Model<N> model, final N node, final ExprContextDynamic<N> dynEnv) {
					return id(node, nse.nodeIterator(model, node, dynEnv));
				}

                @Override
                public Traverser traverseNodes(TraversingInformer contextNode, TraverserDynamicContext dynEnv) {
                    return id(contextNode, nse.traverseNodes(contextNode, dynEnv));
                }
			};
		}
		else if (e instanceof VariantExpr)
		{
			final VariantExpr ve = (VariantExpr)e;
			return new ConvertibleNodeSetExprImpl() {
                @Override
				public <N> NodeIterator<N> nodeIterator(Model<N> model, final N node, final ExprContextDynamic<N> dynEnv) {
					Variant<N> v = ve.evaluateAsVariant(model, node, dynEnv);
					if (v.isNodeSet())
					{
						return id(node, v.convertToNodeSet());
					}
					else
					{
						return id(model, node, v.convertToString());
					}
				}

                @Override
                public Traverser traverseNodes(TraversingInformer contextNode,
                        TraverserDynamicContext dynEnv) {
                    TraverserVariant v = ve.evaluateAsVariant(contextNode, dynEnv);
                    if (v.isNodeSet())
                    {
                        return id(contextNode, v.convertToTraverser());
                    }
                    else
                    {
                        return id(contextNode, v.convertToString());
                    }
                }
			};
		}
		else
		{
			final StringExpr se = e.makeStringExpr(statEnv);
			return new ConvertibleNodeSetExprImpl()
			{
                @Override
				public <N> NodeIterator<N> nodeIterator(Model<N> model, final N contextNode, final ExprContextDynamic<N> dynEnv) {
					return id(model, contextNode, se.stringFunction(model, contextNode, dynEnv));
				}

                @Override
                public Traverser traverseNodes(TraversingInformer contextNode, TraverserDynamicContext dynEnv) {
                    return id(contextNode, se.stringFunction(contextNode, dynEnv));
                }
			};
		}
	}

}
