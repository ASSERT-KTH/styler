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
import org.genxdm.bridgekit.tree.Ordering;
import org.genxdm.xpath.v10.NodeIterator;

/*
 * Remove duplicates. iteration is assumed to be in document order. 
 */
final class UniqueNodeIterator<N> implements NodeIterator<N>
{
	private final NodeIterator<N> iter;
	private final Model<N> model;
	private N lastNode = null;

	UniqueNodeIterator(final NodeIterator<N> iter, final Model<N> model)
	{
		this.iter = iter;
		this.model = model;
	}

	public N next() {
		// loop till we find a node that isn't the same as the last one
		for (;;)
		{
			N tem = iter.next();
			if (tem == null)
			{
				break;
			}
			if (!Ordering.isSameNode(tem, lastNode, model))
			{
				return lastNode = tem;
			}
		}
		return null;
	}
}
