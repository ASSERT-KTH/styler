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
import org.genxdm.xpath.v10.BooleanExpr;
import org.genxdm.xpath.v10.ExprContextDynamic;
import org.genxdm.xpath.v10.ExprException;
import org.genxdm.xpath.v10.extend.ConvertibleExpr;

public class WrappedBooleanExpr extends ConvertibleBooleanExpr {

	public static ConvertibleExpr wrap(BooleanExpr expr) {
		
		if (expr instanceof ConvertibleExpr) {
			return (ConvertibleExpr) expr;
		}
		
		return new WrappedBooleanExpr(expr);
	}
	
	public WrappedBooleanExpr(BooleanExpr expr) {
		m_wrappedExpr = expr;
	}
	
	@Override
	public <N> boolean booleanFunction(Model<N> model, N contextNode,
			ExprContextDynamic<N> dynEnv) throws ExprException {
		// TODO Auto-generated method stub
		return m_wrappedExpr.booleanFunction(model, contextNode, dynEnv);
	}

	private final BooleanExpr m_wrappedExpr;
}
