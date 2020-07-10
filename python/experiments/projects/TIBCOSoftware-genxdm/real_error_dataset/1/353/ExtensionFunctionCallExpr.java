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

import java.lang.reflect.Array;

import javax.xml.namespace.QName;

import org.genxdm.Model;
import org.genxdm.xpath.v10.ExprContextDynamic;
import org.genxdm.xpath.v10.ExprException;
import org.genxdm.xpath.v10.Variant;
import org.genxdm.xpath.v10.VariantExpr;

final class ExtensionFunctionCallExpr 
    extends ConvertibleVariantExpr
{
	private final QName name;
	private final VariantExpr[] args;

	ExtensionFunctionCallExpr(final QName name, final VariantExpr[] args)
	{
		super();
		this.name = name;
		this.args = args;
	}

	public <N> Variant<N> evaluateAsVariant(Model<N> model, final N contextNode, final ExprContextDynamic<N> dynEnv) throws ExprException
	{
		@SuppressWarnings("unchecked")
		final Variant<N>[] argValues = (Variant<N>[])Array.newInstance(Variant.class, args.length);
		for (int i = 0; i < args.length; i++)
		{
			argValues[i] = args[i].evaluateAsVariant(model, contextNode, dynEnv);
		}
		return dynEnv.getExtensionContext(name.getNamespaceURI()).call(name.getLocalPart(), contextNode, argValues);
	}
}
