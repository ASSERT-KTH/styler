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
import org.genxdm.xpath.v10.BooleanExpr;
import org.genxdm.xpath.v10.TraverserDynamicContext;
import org.genxdm.xpath.v10.TraverserVariant;
import org.genxdm.xpath.v10.NodeDynamicContext;
import org.genxdm.xpath.v10.StaticContext;
import org.genxdm.xpath.v10.NodeIterator;
import org.genxdm.xpath.v10.StringExpr;
import org.genxdm.xpath.v10.NodeVariant;
import org.genxdm.xpath.v10.VariantExpr;

/**
 *
 */
public abstract class ConvertibleVariantExpr 
    extends ConvertibleExprImpl implements VariantExpr
{

	public VariantExpr makeVariantExpr(final StaticContext statEnv)
	{
		return this;
	}

	@Override
	public BooleanExpr makePredicateExpr(final StaticContext statEnv)
	{
		return new ConvertibleBooleanExpr( )
		{
		    @Override
			public <N> boolean booleanFunction(Model<N> model, final N node, NodeDynamicContext<N> context) {
				return ConvertibleVariantExpr.this.evaluateAsVariant(model, node, context).convertToPredicate(context);
			}

            @Override
            public boolean booleanFunction(TraversingInformer contextNode, TraverserDynamicContext dynEnv) {
                return ConvertibleVariantExpr.this.evaluateAsVariant(contextNode, dynEnv).convertToPredicate(dynEnv);
            }
		};
	}

	public BooleanExpr makeBooleanExpr(final StaticContext statEnv)
	{
		return new ConvertibleBooleanExpr()
		{
            @Override
			public <N> boolean booleanFunction(Model<N> model, final N node, NodeDynamicContext<N> dynEnv) {
				return ConvertibleVariantExpr.this.evaluateAsVariant(model, node, dynEnv).convertToBoolean();
			}

            @Override
            public boolean booleanFunction(TraversingInformer contextNode, TraverserDynamicContext dynEnv) {
                return ConvertibleVariantExpr.this.evaluateAsVariant(contextNode, dynEnv).convertToBoolean();
            }
		};
	}

	@Override
	public ConvertibleNumberExpr makeNumberExpr(final StaticContext statEnv)
	{
		return new ConvertibleNumberExpr()
		{
			public <N> double numberFunction(Model<N> model, N contextNode, NodeDynamicContext<N> context) {
				return ConvertibleVariantExpr.this.evaluateAsVariant(model, contextNode, context).convertToNumber();
			}

            @Override
            public double numberFunction(TraversingInformer contextNode, TraverserDynamicContext dynEnv) {
                return ConvertibleVariantExpr.this.evaluateAsVariant(contextNode, dynEnv).convertToNumber();
            }
		};
	}

	public StringExpr makeStringExpr(final StaticContext statEnv)
	{
		return new ConvertibleStringExpr()
		{
			public <N> String stringFunction(Model<N> model, final N node, final NodeDynamicContext<N> context) {
				final NodeVariant<N> variant = ConvertibleVariantExpr.this.evaluateAsVariant(model, node, context);
				if (null != variant)
				{
					return variant.convertToString();
				}
				else
				{
					throw new AssertionError(ConvertibleVariantExpr.this + " => " + variant);
				}
			}

            @Override
            public String stringFunction(TraversingInformer contextNode, TraverserDynamicContext dynEnv) {
                final TraverserVariant variant = ConvertibleVariantExpr.this.evaluateAsVariant(contextNode, dynEnv);
                if (null != variant)
                {
                    return variant.convertToString();
                }
                else
                {
                    throw new AssertionError(ConvertibleVariantExpr.this + " => " + variant);
                }
            }
		};
	}

	@Override
	public ConvertibleNodeSetExprImpl makeNodeSetExpr(final StaticContext statEnv)
	{
		return new ConvertibleNodeSetExprImpl()
		{
			public <N> NodeIterator<N> nodeIterator(Model<N> model, final N node, NodeDynamicContext<N> context) {
				return ConvertibleVariantExpr.this.evaluateAsVariant(model, node, context).convertToNodeSet();
			}

            @Override
            public Traverser traverseNodes(TraversingInformer contextNode, TraverserDynamicContext dynEnv) {
                return ConvertibleVariantExpr.this.evaluateAsVariant(contextNode, dynEnv).convertToTraverser();
            }
		};
	}
}
