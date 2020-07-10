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
import org.genxdm.xpath.v10.NodeIterator;

/**
 * does some sort of sort/merge on NodeIterators, I think
 */
final public class MergeNodeIterator<N> implements NodeIterator<N>
{
	private NodeIterator<N>[] iters;
	private List<N> nodes;
	private int length;
	private final Model<N> model;

	/**
	 * construct with an array of iterators
	 * 
	 * @param length
	 *            the number of slots in the array which really have NodeIterators for us
	 */
	public MergeNodeIterator(NodeIterator<N>[] iters, int length, final Model<N> model)
	{
		this.length = length;
		this.iters = iters;
		nodes = new ArrayList<N>(length);
		this.model = model;
		int j = 0;
		for (int i = 0; i < length; i++)
		{
			// we squeeze out NodeIterators with no nodes
			// and put the first node from each iterator
			// in our "nodes" array
			if (i != j)
			{
				iters[j] = iters[i];
			}
			N tem = iters[j].next();
			if (tem != null)
			{
				nodes.add(tem);
				j++;
			}
		}
		this.length = j; // reset the length to reflect squeezing
		buildHeap();
	}

	/**
	 * Make the heap rooted at i a heap, assuming its children are heaps.
	 */
	private final void heapify(int i)
	{
		// i starts out around (length / 2) - 1
		for (;;)
		{
			int left = (i << 1) | 1; // (i*2) + 1 ??
			int right = left + 1; // (i*2) + 2 ??

			if (right < length)
			{

				if (compare(left, right) <= 0)
				{
					// left <= right

					if (compare(left, i) > 0)
					{
						break;
					}
					exchange(left, i);
					i = left;
				}
				else
				{
					// right >= left
					if (compare(right, i) > 0)
					{
						break;
					}
					exchange(right, i);
					i = right;
				}
			}
			else if (left < length)
			{
				if (compare(left, i) > 0)
				{
					break;
				}
				exchange(left, i);
				i = left;
			}
			else
			{
				break;
			}
		}
	}

	/**
	 * swaps the items with the given indices
	 */
	private final void exchange(int i, int j)
	{
		{
			N tem = nodes.get(i);
			nodes.set(i, nodes.get(j));
			nodes.set(j, tem);
		}
		{
			NodeIterator<N> tem = iters[i];
			iters[i] = iters[j];
			iters[j] = tem;
		}
	}

	private final int compare(final int i, final int j)
	{
		return model.compare(nodes.get(i), nodes.get(j));
	}

	private void buildHeap()
	{
		for (int i = length / 2 - 1; i >= 0; --i)
		{
			heapify(i);
		}
	}

	/**
	 * finds and returns the next node (in document(s) order?)
	 */
	public N next() {
		if (length == 0)
		{
			return null;
		}
		N max = nodes.get(0);
		do
		{
			N tem = iters[0].next();
			if (tem == null)
			{
				if (--length == 0)
					break;
				nodes.set(0, nodes.get(length));
				iters[0] = iters[length];
			}
			else
			{
				nodes.set(0, tem);
			}
			heapify(0);
		}
		while (max.equals(nodes.get(0)));
		return max;
	}
}
