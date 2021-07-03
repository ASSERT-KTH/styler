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

import org.genxdm.Model;
import org.genxdm.processor.xpath.v10.iterators.SingleNodeIterator;
import org.genxdm.xpath.v10.ExprContextDynamic;
import org.genxdm.xpath.v10.NodeIterator;

final class NodeConstantExpr<CN>
    extends ConvertibleNodeSetExprImpl
{
	private final CN node;

	NodeConstantExpr(final CN node)
	{
		super();
		this.node = node;
	}

	@SuppressWarnings("unchecked")
	public <N> NodeIterator<N> nodeIterator(Model<N> model, final N contextNode, final ExprContextDynamic<N> dynEnv)
	{
		// TODO - this appears to be untested - There doesn't seem to be a way
		// that one could compile to a constant node, and then guarantee via type
		// safety that it is the same node as when you started.
		return new SingleNodeIterator<N>( (N) node);
	}
}
