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

import javax.xml.namespace.QName;

import org.genxdm.Model;
import org.genxdm.exceptions.IllegalNullArgumentException;
import org.genxdm.nodes.TraversingInformer;
import org.genxdm.xpath.v10.TraverserDynamicContext;
import org.genxdm.xpath.v10.TraverserVariant;
import org.genxdm.xpath.v10.NodeDynamicContext;
import org.genxdm.xpath.v10.NodeVariant;

final class VariableRefExpr
    extends ConvertibleVariantExpr
{
	private final QName name;

	VariableRefExpr(final QName name)
	{
		super();
		this.name = IllegalNullArgumentException.check(name, "name");
	}

    @Override
	public <N> NodeVariant<N> evaluateAsVariant(Model<N> model, final N contextNode, final NodeDynamicContext<N> dynEnv) {
		final NodeVariant<N> value = dynEnv.getVariableValue(name);
		if (null != value)
		{
			return value;
		}
		else
		{
			throw new RuntimeException("dynEnv |- " + this + " => nothing");
		}
	}

    @Override
    public TraverserVariant evaluateAsVariant(TraversingInformer contextNode,
            TraverserDynamicContext dynEnv) {
        final TraverserVariant value = dynEnv.getVariableValue(name);
        if (null != value)
        {
            return value;
        }
        else
        {
            throw new RuntimeException("dynEnv |- " + this + " => nothing");
        }
    }

    @Override
	public String toString()
	{
		return "$" + name;
	}

}
