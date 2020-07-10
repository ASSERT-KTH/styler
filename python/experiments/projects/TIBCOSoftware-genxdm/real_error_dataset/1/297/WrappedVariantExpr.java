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
import org.genxdm.xpath.v10.ExprContextDynamic;
import org.genxdm.xpath.v10.ExprException;
import org.genxdm.xpath.v10.Variant;
import org.genxdm.xpath.v10.VariantExpr;
import org.genxdm.xpath.v10.extend.ConvertibleExpr;

/**
 * Wrapper around a {@link VariantExpr} that turns it into an IConvertibleExpr
 */
public class WrappedVariantExpr extends ConvertibleVariantExpr {

	public static ConvertibleExpr wrap(VariantExpr expr) {
		if (expr instanceof ConvertibleExpr) {
			return (ConvertibleExpr) expr;
		}
		
		return new WrappedVariantExpr(expr);
	}
	
	public WrappedVariantExpr(VariantExpr expr) {
		m_expr = expr;
	}
	
	@Override
	public <N> Variant<N> evaluateAsVariant(Model<N> model, N contextNode,
			ExprContextDynamic<N> dynEnv) throws ExprException {
		return m_expr.evaluateAsVariant(model, contextNode, dynEnv);
	}

	private VariantExpr m_expr;
}
