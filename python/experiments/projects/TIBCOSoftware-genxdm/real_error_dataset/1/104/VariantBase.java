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

import org.genxdm.xpath.v10.NodeDynamicContext;
import org.genxdm.xpath.v10.ExprException;
import org.genxdm.xpath.v10.NodeIterator;
import org.genxdm.xpath.v10.NodeVariant;

public abstract class VariantBase<N> extends VariantCoreBase
    implements NodeVariant<N>
{
	public NodeVariant<N> makePermanent() throws ExprException
	{
		return this;
	}

	public NodeIterator<N> convertToNodeSet()
	{
		throw new RuntimeException("cannot convert to node-set");
	}

	public boolean convertToPredicate(final NodeDynamicContext<N> context)
	{
		return convertToBoolean();
	}

}
