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
import org.genxdm.nodes.TraversingInformer;
import org.genxdm.processor.xpath.v10.variants.TraverserVariantImpl;
import org.genxdm.processor.xpath.v10.variants.NodeSetVariant;
import org.genxdm.xpath.v10.BooleanExpr;
import org.genxdm.xpath.v10.Converter;
import org.genxdm.xpath.v10.TraverserDynamicContext;
import org.genxdm.xpath.v10.TraverserVariant;
import org.genxdm.xpath.v10.ExprContextDynamic;
import org.genxdm.xpath.v10.ExprContextStatic;
import org.genxdm.xpath.v10.StringExpr;
import org.genxdm.xpath.v10.Variant;
import org.genxdm.xpath.v10.VariantExpr;
import org.genxdm.xpath.v10.extend.ConvertibleNodeSetExpr;

/**
 * A compiled XPath pattern component which returns a Node set, but is convertible (castable) to a String expression, boolean expression or VariantExpression convertible (castable) to a String expression, boolean expression or VariantExpression convertible (castable) to
 * a String expression, boolean expression or VariantExpression convertible (castable) to a String expression, boolean expression or VariantExpression
 */
public abstract class ConvertibleNodeSetExprImpl 
    extends ConvertibleExprImpl
    implements ConvertibleNodeSetExpr
{

	public StringExpr makeStringExpr(ExprContextStatic statEnv)
	{
		return new ConvertibleStringExpr()
		{
            @Override
			public <N> String stringFunction(Model<N> model, final N node, final ExprContextDynamic<N> dynEnv) {
				return Converter.toString(ConvertibleNodeSetExprImpl.this.nodeIterator(model, node, dynEnv), model);
			}

            @Override
            public String stringFunction(TraversingInformer contextNode, TraverserDynamicContext dynEnv) {
                return Converter.toStringFromTraverser(ConvertibleNodeSetExprImpl.this.traverseNodes(contextNode, dynEnv));
            }
		};
	}

	/**
     *
     */
	public BooleanExpr makeBooleanExpr(ExprContextStatic statEnv)
	{
		return new ConvertibleBooleanExpr( )
		{
            @Override
			public <N> boolean booleanFunction(Model<N> model, final N node, final ExprContextDynamic<N> dynEnv) {
				return Converter.toBoolean(ConvertibleNodeSetExprImpl.this.nodeIterator(model, node, dynEnv));
			}

            @Override
            public boolean booleanFunction(TraversingInformer contextNode, TraverserDynamicContext dynEnv) {
                return Converter.toBooleanFromTraverser(ConvertibleNodeSetExprImpl.this.traverseNodes(contextNode, dynEnv));
            }
		};
	}

	/**
     *
     */
	@Override
	public ConvertibleNodeSetExprImpl makeNodeSetExpr(ExprContextStatic statEnv)
	{
		return this;
	}

	/**
     *
     */
	public VariantExpr makeVariantExpr(final ExprContextStatic statEnv)
	{
		return new ConvertibleVariantExpr()
		{
            @Override
			public <N> Variant<N> evaluateAsVariant(Model<N> model, final N contextNode, final ExprContextDynamic<N> dynEnv) {
				return new NodeSetVariant<N>(ConvertibleNodeSetExprImpl.this.nodeIterator(model, contextNode, dynEnv), model);
			}

            @Override
            public TraverserVariant evaluateAsVariant(TraversingInformer contextNode, TraverserDynamicContext dynEnv) {
                return new TraverserVariantImpl(ConvertibleNodeSetExprImpl.this.traverseNodes(contextNode, dynEnv));
            }
		};
	}

	public int getOptimizeFlags()
	{
		return 0;
	}

	/**
	 * Return an expression for this/expr
	 */
	public ConvertibleNodeSetExpr compose(final ConvertibleNodeSetExpr expr)
	{
		final int opt1 = this.getOptimizeFlags();
		final int opt2 = expr.getOptimizeFlags();
		if ((opt1 & SINGLE_LEVEL) != 0 && (opt2 & STAYS_IN_SUBTREE) != 0)
		{
			return new SequenceComposeExpr(this, expr);
		}
		return new ComposeExpr(this, expr);
	}

}
