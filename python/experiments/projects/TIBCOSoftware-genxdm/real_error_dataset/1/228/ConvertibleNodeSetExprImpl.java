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
import org.genxdm.processor.xpath.v10.variants.NodeSetVariant;
import org.genxdm.xpath.v10.BooleanExpr;
import org.genxdm.xpath.v10.Converter;
import org.genxdm.xpath.v10.ExprContextDynamic;
import org.genxdm.xpath.v10.ExprContextStatic;
import org.genxdm.xpath.v10.ExprException;
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
			public <N> String stringFunction(Model<N> model, final N node, final ExprContextDynamic<N> dynEnv) throws ExprException
			{
				return Converter.toString(ConvertibleNodeSetExprImpl.this.nodeIterator(model, node, dynEnv), model);
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
			public <N> boolean booleanFunction(Model<N> model, final N node, final ExprContextDynamic<N> dynEnv) throws ExprException
			{
				return Converter.toBoolean(ConvertibleNodeSetExprImpl.this.nodeIterator(model, node, dynEnv));
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
			public <N> Variant<N> evaluateAsVariant(Model<N> model, final N contextNode, final ExprContextDynamic<N> dynEnv) throws ExprException
			{
				return new NodeSetVariant<N>(ConvertibleNodeSetExprImpl.this.nodeIterator(model, contextNode, dynEnv), model);
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
