/*
 * Copyright (c) 2009-2011 TIBCO Software Inc.
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

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.genxdm.xpath.v10.ExprContextDynamic;
import org.genxdm.xpath.v10.ExprContextDynamicArgs;
import org.genxdm.xpath.v10.Variant;

public final class ExprContextDynamicArgsImpl<N> 
    implements ExprContextDynamicArgs<N>
{
	private int position;
	private int size;
	private final Map<QName, Variant<N>> variables = new HashMap<QName, Variant<N>>();
	private boolean m_inheritAttributes = false;
	
	// TODO not sure about this default...
	private boolean m_inheritNamespaces = true;

	public void bindVariableValue(final QName name, final Variant<N> value)
	{
		variables.put(name, value);
	}

	public ExprContextDynamic<N> build()
	{
		return new ExprContextDynamicImpl<N>(position, size, variables, m_inheritAttributes, m_inheritNamespaces);
	}

	public void reset()
	{
		variables.clear();
	}

	public void setContextPosition(final int position)
	{
		this.position = position;
	}

	public void setContextSize(final int size)
	{
		this.size = size;
	}

	@Override
	public void setInheritAttributes(boolean inheritAttributes) {
		m_inheritAttributes = inheritAttributes;
	}

	@Override
	public void setInheritNamespaces(boolean inheritNamespaces) {
		m_inheritNamespaces = inheritNamespaces;
	}
}
