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

import org.genxdm.exceptions.IllegalNullArgumentException;
import org.genxdm.xpath.v10.Converter;

public final class StringVariant<N> extends VariantBase<N>
{
	private final String str;

	public StringVariant(final String str)
	{
		this.str = IllegalNullArgumentException.check(str, "str");
	}

	public String convertToString()
	{
		return str;
	}

	public boolean convertToBoolean()
	{
		return Converter.toBoolean(str);
	}

	@Override
	public double convertToNumber()
	{
		return Converter.toNumber(str);
	}

	@Override
	public boolean isString()
	{
		return true;
	}
}
