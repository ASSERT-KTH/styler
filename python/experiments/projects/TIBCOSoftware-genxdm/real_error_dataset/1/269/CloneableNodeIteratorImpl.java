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

import java.util.ArrayList;
import java.util.List;

import org.genxdm.Model;
import org.genxdm.xpath.v10.ExprException;
import org.genxdm.xpath.v10.NodeIterator;

public final class CloneableNodeIteratorImpl<N> implements CloneableNodeIterator<N>
{
	private final NodeList<N> list;
	private int i;

	@Override
	public Object clone()
	{
		return new CloneableNodeIteratorImpl<N>(list, i);
	}

	public CloneableNodeIteratorImpl(NodeIterator<N> iter, final Model<N> model)
	{
		list = new NodeList<N>(iter, model);
		i = 0;
	}

	private CloneableNodeIteratorImpl(NodeList<N> list, int i)
	{
		this.list = list;
		this.i = i;
	}

	public N next() throws ExprException
	{
		N tem = list.nodeAt(i);
		if (tem != null)
			i++;
		return tem;
	}

	public void bind() throws ExprException
	{
		for (int i = 0; list.nodeAt(i) != null; i++)
			;
	}

	static class NodeList<N>
	{
		final NodeIterator<N> iter;
		final Model<N> model;
		List<N> nodes = null;
		int len = 0;

		NodeList(final NodeIterator<N> iter, final Model<N> model)
		{
			this.iter = iter;
			this.model = model;
		}

		N nodeAt(int i) throws ExprException
		{
			if (i >= len)
			{
				if (nodes == null)
				{
					nodes = new ArrayList<N>(i + 4);
				}
				// Have i < nodes.length
				for (; len <= i; len++)
				{
				    N node = iter.next();
				    if (node == null)
				        return null;
				    nodes.add(node);
				}
				// Have i < length
			}
			return nodes.get(i);
		}
	}
}
