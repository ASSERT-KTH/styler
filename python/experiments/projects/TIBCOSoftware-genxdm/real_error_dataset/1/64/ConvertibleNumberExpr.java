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
import org.genxdm.processor.xpath.v10.variants.NumberVariant;
import org.genxdm.xpath.v10.BooleanExpr;
import org.genxdm.xpath.v10.Converter;
import org.genxdm.xpath.v10.TraverserDynamicContext;
import org.genxdm.xpath.v10.TraverserVariant;
import org.genxdm.xpath.v10.ExprContextDynamic;
import org.genxdm.xpath.v10.ExprContextStatic;
import org.genxdm.xpath.v10.NumberExpr;
import org.genxdm.xpath.v10.StringExpr;
import org.genxdm.xpath.v10.Variant;
import org.genxdm.xpath.v10.VariantExpr;

public abstract class ConvertibleNumberExpr 
    extends ConvertibleExprImpl
    implements NumberExpr
{

	@Override
	public NumberExpr makeNumberExpr(final ExprContextStatic statEnv)
	{
		return this;
	}

	@Override
	public BooleanExpr makePredicateExpr(final ExprContextStatic statEnv)
	{
		return new ConvertibleBooleanExpr()
		{
            @Override
			public <N> boolean booleanFunction(Model<N> model, final N node, final ExprContextDynamic<N> context) {
				return Converter.positionToBoolean(ConvertibleNumberExpr.this.numberFunction(model, node, context), context);
			}

            @Override
            public boolean booleanFunction(TraversingInformer contextNode, TraverserDynamicContext dynEnv) {
                return Converter.positionToBoolean(ConvertibleNumberExpr.this.numberFunction(contextNode, dynEnv), dynEnv);
            }
		};
	}

	public BooleanExpr makeBooleanExpr(final ExprContextStatic statEnv)
	{
		return new ConvertibleBooleanExpr()
		{
            @Override
			public <N> boolean booleanFunction(Model<N> model, final N node, final ExprContextDynamic<N> dynEnv) {
				return Converter.toBoolean(ConvertibleNumberExpr.this.numberFunction(model, node, dynEnv));
			}

            @Override
            public boolean booleanFunction(TraversingInformer contextNode, TraverserDynamicContext dynEnv) {
                return Converter.toBoolean(ConvertibleNumberExpr.this.numberFunction(contextNode, dynEnv));
            }
		};
	}

	public VariantExpr makeVariantExpr(final ExprContextStatic statEnv)
	{
		return new ConvertibleVariantExpr()
		{
            @Override
			public <N> Variant<N> evaluateAsVariant(Model<N> model, final N contextNode, final ExprContextDynamic<N> dynEnv) {
				return new NumberVariant<N>(ConvertibleNumberExpr.this.numberFunction(model, contextNode, dynEnv));
			}

            @Override
            public TraverserVariant evaluateAsVariant(TraversingInformer contextNode, TraverserDynamicContext dynEnv) {
                return new NumberVariant<Object>(ConvertibleNumberExpr.this.numberFunction(contextNode, dynEnv));
            }
		};
	}

	public StringExpr makeStringExpr(final ExprContextStatic statEnv)
	{
		return new ConvertibleStringExpr()
		{
            @Override
			public <N> String stringFunction(Model<N> model, final N node, final ExprContextDynamic<N> context) {
				return Converter.toString(ConvertibleNumberExpr.this.numberFunction(model, node, context));
			}

            @Override
            public String stringFunction(TraversingInformer contextNode, TraverserDynamicContext dynEnv) {
                return Converter.toString(ConvertibleNumberExpr.this.numberFunction(contextNode, dynEnv));
            }
		};
	}
}
