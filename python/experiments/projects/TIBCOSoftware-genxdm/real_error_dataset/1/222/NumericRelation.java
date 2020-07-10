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

import org.genxdm.xpath.v10.Converter;
import org.genxdm.xpath.v10.ExprException;
import org.genxdm.xpath.v10.Variant;

public abstract class NumericRelation 
    extends Relation
{

	public boolean relate(final boolean b1, final boolean b2)
	{
		return relate(b1 ? 1.0 : 0.0, b2 ? 1.0 : 0.0);
	}

	public boolean relate(final String s1, final String s2)
	{
		return relate(Converter.toNumber(s1), Converter.toNumber(s2));
	}

	@Override
	<N> boolean relateAtomic(final Variant<N> obj1, final Variant<N> obj2) throws ExprException
	{
		return relate(obj1.convertToNumber(), obj2.convertToNumber());
	}
}
