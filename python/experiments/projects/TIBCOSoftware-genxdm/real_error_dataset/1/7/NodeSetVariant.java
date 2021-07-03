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
import org.genxdm.xpath.v10.Converter;
import org.genxdm.xpath.v10.ExprException;
import org.genxdm.xpath.v10.NodeIterator;
import org.genxdm.xpath.v10.Variant;

public final class NodeSetVariant<N> extends VariantBase<N>
{
	private final NodeIterator<N> iter;
	private final Model<N> model;

	public NodeSetVariant(final NodeIterator<N> iter, final Model<N> model)
	{
		this.iter = iter;
		this.model = model;
	}

	public Model<N> getModel() {
	    return model;
	}
	
	public NodeIterator<N> convertToNodeSet()
	{
		return iter;
	}

	public String convertToString()
	{
		return Converter.toString(iter, model);
	}

	public boolean convertToBoolean()
	{
		return Converter.toBoolean(iter);
	}

	@Override
	public double convertToNumber()
	{
		return Converter.toNumber(iter, model);
	}

	@Override
	public Variant<N> makePermanent() throws ExprException
	{
		return new PermanentNodeSetVariant<N>(iter, model);
	}

	public boolean isNodeSet()
	{
		return true;
	}
}
