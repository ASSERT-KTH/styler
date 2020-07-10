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
package org.genxdm.processor.xpath.v10.tests;

import org.genxdm.Model;
import org.genxdm.NodeKind;
import org.genxdm.processor.xpath.v10.patterns.PathPatternBase;
import org.genxdm.xpath.v10.ExprContextDynamic;

/**
 * Simply tests if a node is of a given type e.g. "text()"
 */
public class NodeTypeTest 
    extends PathPatternBase
{
	private final NodeKind type;

	/**
	 * construct with one of the constants
	 */
	public NodeTypeTest(final NodeKind type)
	{
		this.type = type;
	}

	/**
	 * return true if this represents the "node()" test, else return true if the node's type matches the test
	 */
	public <N> boolean matches(Model<N> model, final N node, final ExprContextDynamic<N> dynEnv)
	{
		return type == null ? true : model.getNodeKind(node) == type;
	}

	public String getMatchLocalName()
	{
		return null;
	}

	public String getMatchNamespaceURI()
	{
		return null;
	}

	public NodeKind getMatchNodeType()
	{
		return type;
	}

	public int getDefaultPriority()
	{
		return -2;
	}
}
