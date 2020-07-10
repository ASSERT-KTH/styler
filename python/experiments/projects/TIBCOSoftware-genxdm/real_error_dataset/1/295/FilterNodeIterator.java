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
import org.genxdm.processor.xpath.v10.expressions.DelegateExprContext;
import org.genxdm.xpath.v10.BooleanExpr;
import org.genxdm.xpath.v10.ExprContextDynamic;
import org.genxdm.xpath.v10.ExprException;
import org.genxdm.xpath.v10.NodeIterator;

public final class FilterNodeIterator<N> 
    extends DelegateExprContext<N> 
    implements NodeIterator<N>
{
	private int pos = 0;
	private int lastPos = 0;
	private NodeIterator<N> iter;
	private final Model<N> m_model;
	private final BooleanExpr predicate;

	public FilterNodeIterator(Model<N> model, final NodeIterator<N> iter, final ExprContextDynamic<N> dynEnv, final BooleanExpr predicate)
	{
		super(dynEnv);
		this.iter = iter;
		this.m_model = model;
		this.predicate = predicate;
	}

	public N next() throws ExprException
	{
		for (;;)
		{
			N tem = iter.next();
			if (tem == null)
			{
				break;
			}
			++pos;
			if (predicate.booleanFunction(this.m_model, tem, this))
			{
				return tem;
			}
		}
		return null;
	}

	@Override
	public int getContextPosition()
	{
		return pos;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int getContextSize() throws ExprException
	{
		if (lastPos == 0)
		{
			CloneableNodeIterator<N> cloneIter;
			if (iter instanceof CloneableNodeIterator)
			{
				cloneIter = (CloneableNodeIterator<N>)iter;
			}
			else
			{
				cloneIter = new CloneableNodeIteratorImpl<N>(iter, m_model);
			}
			iter = (NodeIterator<N>)cloneIter.clone();
			int savePosition = pos;
			try
			{
				while (next() != null)
					;
				lastPos = pos;
			}
			finally
			{
				pos = savePosition;
				iter = cloneIter;
			}
		}
		return lastPos;
	}
}
