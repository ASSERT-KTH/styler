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
import org.genxdm.nodes.TraversingInformer;
import org.genxdm.xpath.v10.TraverserDynamicContext;
import org.genxdm.xpath.v10.NodeDynamicContext;
import org.genxdm.xpath.v10.StringExpr;
import org.genxdm.xpath.v10.extend.ConvertibleExpr;

public class WrappedStringExpr extends ConvertibleStringExpr {

	public static ConvertibleExpr wrap(StringExpr expr) {
		
		if (expr instanceof ConvertibleExpr) {
			return (ConvertibleExpr) expr;
		}
		
		return new WrappedStringExpr(expr);
	}
	
	public WrappedStringExpr(StringExpr expr) {
		m_wrappedExpr = expr;
	}
	
	@Override
	public <N> String stringFunction(Model<N> model, N contextNode,
			NodeDynamicContext<N> dynEnv) {
		return m_wrappedExpr.stringFunction(model, contextNode, dynEnv);
	}

    @Override
    public String stringFunction(TraversingInformer contextNode, TraverserDynamicContext dynEnv) {
        return m_wrappedExpr.stringFunction(contextNode, dynEnv);
    }

    private StringExpr m_wrappedExpr;
}
