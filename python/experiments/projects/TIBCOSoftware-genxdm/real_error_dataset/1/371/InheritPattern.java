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
import org.genxdm.Cursor;
import org.genxdm.nodes.TraversingInformer;
import org.genxdm.xpath.v10.TraverserDynamicContext;
import org.genxdm.xpath.v10.ExprContextDynamic;

final class InheritPattern 
    implements Pattern
{
	private Pattern p;

	InheritPattern(final Pattern p)
	{
		this.p = p;
	}

	@Override
	public <N> boolean matches(Model<N> model, final N contextNode, final ExprContextDynamic<N> dynEnv) {
		N node = contextNode;
		do
		{
			if (p.matches(model, node, dynEnv))
			{
				return true;
			}
			node = model.getParent(node);
		}
		while (node != null);
		return false;
	}

    @Override
    public boolean matches(TraversingInformer node, TraverserDynamicContext dynEnv) {
        Cursor someAncestor = node.newPrecursor();
        do
        {
            if (p.matches(someAncestor, dynEnv))
            {
                return true;
            }
            if (!someAncestor.moveToParent()) {
                someAncestor = null;
            }
        }
        while (someAncestor != null);
        return false;
    }
}
