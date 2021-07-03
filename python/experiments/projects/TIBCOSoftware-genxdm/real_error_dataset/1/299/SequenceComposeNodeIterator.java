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
package org.genxdm.processor.xpath.v10.iterators;

import org.genxdm.Model;
import org.genxdm.xpath.v10.ExprContextDynamic;
import org.genxdm.xpath.v10.ExprException;
import org.genxdm.xpath.v10.NodeIterator;
import org.genxdm.xpath.v10.NodeSetExpr;

public final class SequenceComposeNodeIterator<N> implements NodeIterator<N>
{
	private final NodeIterator<N> iter1;
	private NodeIterator<N> iter2;
	private final NodeSetExpr expr;
	private Model<N> m_model;
	private final ExprContextDynamic<N> context;

	public SequenceComposeNodeIterator(Model<N> model, final NodeIterator<N> iter, final NodeSetExpr expr, final ExprContextDynamic<N> context)
	{
		this.iter1 = iter;
		this.m_model = model;
		this.expr = expr;
		this.context = context;
		this.iter2 = new NullNodeIterator<N>();
	}

	public N next() throws ExprException
	{
		for (;;)
		{
			N node = iter2.next();
			if (node != null)
				return node;
			node = iter1.next();
			if (node == null)
				break;
			iter2 = expr.nodeIterator(m_model, node, context);
		}
		return null;
	}
}
