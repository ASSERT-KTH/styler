/*
 * Copyright (c) 2011 TIBCO Software Inc.
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
import org.genxdm.xpath.v10.NodeIterator;
import org.genxdm.xpath.v10.NodeSetExpr;
import org.genxdm.xpath.v10.StringExpr;
import org.genxdm.xpath.v10.Variant;
import org.genxdm.xpath.v10.VariantExpr;
import org.genxdm.xpath.v10.extend.ConvertibleNodeSetExpr;

public class WrappedNodeSetExpr extends ConvertibleNodeSetExprImpl {

	public static ConvertibleNodeSetExpr wrap(NodeSetExpr expr, int optimizeFlags) {
		if (expr instanceof ConvertibleNodeSetExpr) {
			return (ConvertibleNodeSetExpr) expr;
		}
		
		return new WrappedNodeSetExpr(expr, optimizeFlags);
	}
	
	public WrappedNodeSetExpr(NodeSetExpr expr, int optimizeFlags) {
		m_nodeSetExpr = expr;
		m_optimizeFlags = optimizeFlags;
	}
	
	@Override
	public <N> NodeIterator<N> nodeIterator(Model<N> model, N contextNode,
			ExprContextDynamic<N> dynEnv) throws ExprException {
		return m_nodeSetExpr.nodeIterator(model, contextNode, dynEnv);
	}

	@Override
	public StringExpr makeStringExpr(ExprContextStatic statEnv) {
		return new ConvertibleStringExpr() {
			public <N> String stringFunction(Model<N> model, final N node, final ExprContextDynamic<N> dynEnv)
			throws ExprException {
				return Converter.toString(m_nodeSetExpr.nodeIterator(model, node, dynEnv), model);
			}
		};
	}

	@Override
	public BooleanExpr makeBooleanExpr(ExprContextStatic statEnv) {
		return new ConvertibleBooleanExpr( ) {
			public <N> boolean booleanFunction(Model<N> model, final N node, final ExprContextDynamic<N> dynEnv) throws ExprException {
				return Converter.toBoolean(m_nodeSetExpr.nodeIterator(model, node, dynEnv));
			}
		};
	}

	@Override
	public VariantExpr makeVariantExpr(ExprContextStatic statEnv) {
		return new ConvertibleVariantExpr() {
			public <N> Variant<N> evaluateAsVariant(Model<N> model, final N contextNode, final ExprContextDynamic<N> dynEnv)
			throws ExprException {
				return new NodeSetVariant<N>(m_nodeSetExpr.nodeIterator(model, contextNode, dynEnv), model);
			}
		};
	}

	@Override
	public int getOptimizeFlags() {
		return m_optimizeFlags;
	}

	private NodeSetExpr m_nodeSetExpr;

	private int m_optimizeFlags;
}
