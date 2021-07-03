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
package org.genxdm.processor.xpath.v10.patterns;

import org.genxdm.Model;
import org.genxdm.NodeKind;
import org.genxdm.processor.xpath.v10.expressions.DelegateExprContext;
import org.genxdm.processor.xpath.v10.iterators.NodeIteratorOnIterator;
import org.genxdm.xpath.v10.BooleanExpr;
import org.genxdm.xpath.v10.ExprContextDynamic;
import org.genxdm.xpath.v10.ExprException;
import org.genxdm.xpath.v10.NodeIterator;

/**
 * a pattern that has a predicate to eliminate some nodes
 */
final class FilterPattern
    extends PathPatternBase
{
	private final PathPatternBase pattern;
	private final BooleanExpr predicate;
	private final boolean inheritAttributes;
	private final boolean inheritNamespaces;

	FilterPattern(final PathPatternBase pattern, final BooleanExpr predicate, final boolean inheritAttributes, final boolean inheritNamespaces)
	{
		this.pattern = pattern;
		this.predicate = predicate;
		this.inheritAttributes = inheritAttributes;
		this.inheritNamespaces = inheritNamespaces;
	}

	public <N> boolean matches(Model<N> model, final N node, final ExprContextDynamic<N> dynEnv) throws ExprException
	{
		if (!pattern.matches(model, node, dynEnv))
		{
			return false;
		}
		return predicate.booleanFunction(model, node, new Context<N>(model, node, dynEnv));
	}

	public int getDefaultPriority()
	{
		return 1;
	}

	public String getMatchNamespaceURI()
	{
		return pattern.getMatchNamespaceURI();
	}

	public String getMatchLocalName()
	{
		return pattern.getMatchLocalName();
	}

	public NodeKind getMatchNodeType()
	{
		return pattern.getMatchNodeType();
	}

	// ////////////////////////////////////////
	//  
	// the context changes a bit from our caller's context to reflect a
	// different way of tracking position()
	//
	class Context<N> extends DelegateExprContext<N>
	{
		N node;
		final Model<N> model;
		int position = 0;
		int lastPosition = 0;

		Context(Model<N> model, final N node, final ExprContextDynamic<N> context)
		{
			super(context);
			this.node = node;
			this.model = model;
		}

		public int getContextPosition() throws ExprException
		{
			if (position != 0)
			{
				return position;
			}
			NodeIterator<N> iter;
			switch (model.getNodeKind(node))
			{
				case DOCUMENT:
					position = 1;
					return 1;
				case ATTRIBUTE:
					iter = new NodeIteratorOnIterator<N>(model.getAttributeAxis(model.getParent(node), inheritAttributes).iterator());
				break;
				case NAMESPACE:
					iter = new NodeIteratorOnIterator<N>(model.getNamespaceAxis(model.getParent(node), inheritNamespaces).iterator());
				break;
				default:
					iter = new NodeIteratorOnIterator<N>(model.getChildAxis(model.getParent(node)).iterator());
				break;
			}
			position = 1;
			for (;;)
			{
				N tem = iter.next();
				if (tem.equals(node))
				{
					break;
				}
				if (pattern.matches(model, tem, origContext))
				{
					position++;
				}
			}
			return position;
		}

		public int getContextSize() throws ExprException
		{
			if (lastPosition != 0)
			{
				return lastPosition;
			}
			NodeIterator<N> iter;
			switch (model.getNodeKind(node))
			{
				case DOCUMENT:
					lastPosition = 1;
					return 1;
				case ATTRIBUTE:
					iter = new NodeIteratorOnIterator<N>(model.getAttributeAxis(model.getParent(node), inheritAttributes).iterator());
					lastPosition = 0;
				break;
				case NAMESPACE:
					iter = new NodeIteratorOnIterator<N>(model.getNamespaceAxis(model.getParent(node), inheritNamespaces).iterator());
					lastPosition = 0;
				break;
				default:
					iter = new NodeIteratorOnIterator<N>(model.getFollowingSiblingAxis(node).iterator());
					lastPosition = position;
				break;
			}
			for (;;)
			{
				N tem = iter.next();
				if (tem == null)
				{
					break;
				}
				if (pattern.matches(model, tem, origContext))
				{
					lastPosition++;
				}
			}
			return lastPosition;
		}
	}
}
