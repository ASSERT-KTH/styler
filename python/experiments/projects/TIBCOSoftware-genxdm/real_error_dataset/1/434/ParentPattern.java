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
import org.genxdm.Precursor;
import org.genxdm.nodes.TraversingInformer;
import org.genxdm.xpath.v10.TraverserDynamicContext;
import org.genxdm.xpath.v10.ExprContextDynamic;

/**
 * represents the concatenation of step patterns, right to left in a LocationPathPattern
 */
class ParentPattern
    extends PathPatternBase
{
	private final PathPatternBase childPattern;
	private final Pattern parentPattern;

	/**
	 * construct with a new stepPattern: childPattern and a previous parentPattern
	 */
	ParentPattern(final PathPatternBase childPattern, final Pattern parentPattern)
	{
		// the right hand (child or attribute axis) StepPattern
		this.childPattern = childPattern;

		// whatever came before
		this.parentPattern = parentPattern;
	}

	/**
	 * if the rightmost step matches, and our parentPattern's matches() returns true for this node's parent then we have a winner!
	 */
	@Override
	public <N> boolean matches(Model<N> model, final N contextNode, final ExprContextDynamic<N> dynEnv) {
		if (!childPattern.matches(model, contextNode, dynEnv))
		{
			return false;
		}
		final N node = model.getParent(contextNode);
		if (node == null)
		{
			// we ran out of ancestors before we ran out of StepPatterns
			return false;
		}
		return parentPattern.matches(model, node, dynEnv);
	}

    @Override
    public boolean matches(TraversingInformer node, TraverserDynamicContext dynEnv) {
        if (!childPattern.matches(node, dynEnv))
        {
            return false;
        }
        Precursor parent = node.newPrecursor();
        if (!parent.moveToParent())
        {
            // we ran out of ancestors before we ran out of StepPatterns
            return false;
        }
        return parentPattern.matches(parent, dynEnv);
    }

    @Override
	public int getDefaultPriority()
	{
		return 1;
	}

	/**
	 * gets the rightmost (final) step's matchNodeType
	 */
    @Override
	public String getMatchNamespaceURI()
	{
		return childPattern.getMatchNamespaceURI();
	}

    @Override
	public String getMatchLocalName()
	{
		return childPattern.getMatchLocalName();
	}

	/**
	 * gets the rightmost (final) step's matchNodeType
	 */
    @Override
	public NodeKind getMatchNodeType()
	{
		return childPattern.getMatchNodeType();
	}

}
