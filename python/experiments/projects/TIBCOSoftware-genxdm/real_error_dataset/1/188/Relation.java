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
package org.genxdm.processor.xpath.v10.relations;

import org.genxdm.Model;
import org.genxdm.processor.xpath.v10.iterators.CloneableNodeIterator;
import org.genxdm.processor.xpath.v10.iterators.CloneableNodeIteratorImpl;
import org.genxdm.xpath.v10.Converter;
import org.genxdm.xpath.v10.ExprException;
import org.genxdm.xpath.v10.NodeIterator;
import org.genxdm.xpath.v10.Variant;

public abstract class Relation 
{

	public abstract boolean relate(boolean b1, boolean b2);

	<N> boolean relate(boolean b, NodeIterator<N> iter) throws ExprException
	{
		return relate(b, iter.next() != null);
	}

	public abstract boolean relate(double d1, double d2);

	<N> boolean relate(double d, NodeIterator<N> iter, final Model<N> model) throws ExprException
	{
		for (;;)
		{
			N node = iter.next();
			if (node == null)
				break;
			if (relate(d, Converter.toNumber(Converter.toString(node, model))))
				return true;
		}
		return false;
	}

	<N> boolean relate(NodeIterator<N> iter, boolean b) throws ExprException
	{
		return relate(iter.next() != null, b);
	}

	<N> boolean relate(NodeIterator<N> iter, double d, final Model<N> model) throws ExprException
	{
		for (;;)
		{
			N node = iter.next();
			if (node == null)
				break;
			if (relate(Converter.toNumber(Converter.toString(node, model)), d))
				return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	<N> boolean relate(final NodeIterator<N> iter1, NodeIterator<N> iter2, final Model<N> model) throws ExprException
	{
		if (!(iter2 instanceof CloneableNodeIterator))
			iter2 = new CloneableNodeIteratorImpl<N>(iter2, model);
		for (;;)
		{
			N node1 = iter1.next();
			if (node1 == null)
				break;
			String s1 = Converter.toString(node1, model);
			NodeIterator<N> tem = (NodeIterator<N>)((CloneableNodeIterator<N>)iter2).clone();
			for (;;)
			{
				N node2 = tem.next();
				if (node2 == null)
					break;
				if (relate(s1, Converter.toString(node2, model)))
					return true;
			}
		}
		return false;
	}

	<N> boolean relate(NodeIterator<N> iter, String s, final Model<N> model) throws ExprException
	{
		for (;;)
		{
			N node = iter.next();
			if (node == null)
				break;
			if (relate(Converter.toString(node, model), s))
				return true;
		}
		return false;
	}

	<N> boolean relate(String s, NodeIterator<N> iter, final Model<N> model) throws ExprException
	{
		for (;;)
		{
			N node = iter.next();
			if (node == null)
				break;
			if (relate(s, Converter.toString(node, model)))
				return true;
		}
		return false;
	}

	public abstract boolean relate(String s1, String s2);

	public <N> boolean relate(final Variant<N> obj1, final Variant<N> obj2, final Model<N> model) throws ExprException
	{
		if (obj1.isNodeSet())
		{
			if (obj2.isNodeSet())
				return relate(obj1.convertToNodeSet(), obj2.convertToNodeSet(), model);
			if (obj2.isNumber())
				return relate(obj1.convertToNodeSet(), obj2.convertToNumber(), model);
			if (obj2.isBoolean())
				return relate(obj1.convertToNodeSet(), obj2.convertToBoolean());
			return relate(obj1.convertToNodeSet(), obj2.convertToString(), model);
		}
		if (obj2.isNodeSet())
		{
			if (obj1.isNumber())
			{
				return relate(obj1.convertToNumber(), obj2.convertToNodeSet(), model);
			}
			if (obj1.isBoolean())
			{
				return relate(obj1.convertToBoolean(), obj2.convertToNodeSet());
			}
			return relate(obj1.convertToString(), obj2.convertToNodeSet(), model);
		}
		return relateAtomic(obj1, obj2);
	}

	<N> boolean relateAtomic(final Variant<N> obj1, final Variant<N> obj2) throws ExprException
	{
		if (obj1.isBoolean() || obj2.isBoolean())
		{
			return relate(obj1.convertToBoolean(), obj2.convertToBoolean());
		}
		if (obj1.isNumber() || obj2.isNumber())
		{
			return relate(obj1.convertToNumber(), obj2.convertToNumber());
		}
		return relate(obj1.convertToString(), obj2.convertToString());
	}
}
