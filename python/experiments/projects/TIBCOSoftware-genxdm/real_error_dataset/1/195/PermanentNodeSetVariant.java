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
package org.genxdm.processor.xpath.v10.variants;

import org.genxdm.Model;
import org.genxdm.processor.xpath.v10.iterators.CloneableNodeIterator;
import org.genxdm.processor.xpath.v10.iterators.CloneableNodeIteratorImpl;
import org.genxdm.xpath.v10.Converter;
import org.genxdm.xpath.v10.ExprException;
import org.genxdm.xpath.v10.NodeIterator;

final class PermanentNodeSetVariant<N> extends VariantBase<N>
{
	private final CloneableNodeIterator<N> iter;
	private final Model<N> model;

	PermanentNodeSetVariant(NodeIterator<N> iter, final Model<N> model) throws ExprException
	{
		this.model = model;
		if (iter instanceof CloneableNodeIterator<?>)
		{
			this.iter = (CloneableNodeIterator<N>)iter;
		}
		else
		{
			this.iter = new CloneableNodeIteratorImpl<N>(iter, model);
		}
		this.iter.bind();
	}

	@SuppressWarnings("unchecked")
	@Override
	public NodeIterator<N> convertToNodeSet()
	{
		return (NodeIterator<N>)iter.clone();
	}

	public String convertToString() throws ExprException
	{
		return Converter.toString(convertToNodeSet(), model);
	}

	public boolean convertToBoolean() throws ExprException
	{
		return Converter.toBoolean(convertToNodeSet());
	}

	@Override
	public double convertToNumber() throws ExprException
	{
		return Converter.toNumber(convertToNodeSet(), model);
	}

	@Override
	public boolean isNodeSet()
	{
		return true;
	}
}
