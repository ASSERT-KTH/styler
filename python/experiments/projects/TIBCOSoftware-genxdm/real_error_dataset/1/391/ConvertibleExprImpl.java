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

import org.genxdm.xpath.v10.BooleanExpr;
import org.genxdm.xpath.v10.StaticContext;
import org.genxdm.xpath.v10.ExprParseException;
import org.genxdm.xpath.v10.NumberExpr;
import org.genxdm.xpath.v10.extend.ConvertibleExpr;

/**
 * An XPath expression (component) which can be cast to any of several types as needed
 */
public abstract class ConvertibleExprImpl implements ConvertibleExpr 
{
	/* (non-Javadoc)
	 * @see org.genxdm.xpath.v10.expressions.IConvertibleExpr#makeNodeSetExpr(org.genxdm.xpath.v10.expressions.ExprContextStatic)
	 */
	@Override
	public ConvertibleNodeSetExprImpl makeNodeSetExpr(final StaticContext statEnv) throws ExprParseException
	{
		throw new ExprParseException("value of expression cannot be converted to a node-set");
	}

	/* (non-Javadoc)
	 * @see org.genxdm.xpath.v10.expressions.IConvertibleExpr#makeNumberExpr(org.genxdm.xpath.v10.expressions.ExprContextStatic)
	 */
	@Override
	public NumberExpr makeNumberExpr(final StaticContext statEnv)
	{
		return WrappedStringExpr.wrap(makeStringExpr(statEnv)).makeNumberExpr(statEnv);
	}

	/* (non-Javadoc)
	 * @see org.genxdm.xpath.v10.expressions.IConvertibleExpr#makePredicateExpr(org.genxdm.xpath.v10.expressions.ExprContextStatic)
	 */
	@Override
	public BooleanExpr makePredicateExpr(final StaticContext statEnv)
	{
		return makeBooleanExpr(statEnv);
	}
}
