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
import org.genxdm.xpath.v10.ExprException;
import org.genxdm.xpath.v10.ExtensionContext;
import org.genxdm.xpath.v10.Variant;

final class ExprContextDynamicImpl<N> 
    implements ExprContextDynamic<N>
{
	private boolean m_inheritAttributes;
	
	private boolean m_inheritNamespaces;
	
	private final int position;
	private final int size;
	private final Map<QName, Variant<N>> variables;

	public ExprContextDynamicImpl(final int position, final int size,
			final Map<? extends QName, ? extends Variant<N>> variables,
					boolean inheritAttributes, boolean inheritNamespaces)
	{
		this.position = position;
		this.size = size;
		this.variables = new HashMap<QName, Variant<N>>(variables);
		this.m_inheritAttributes = inheritAttributes;
		this.m_inheritNamespaces = inheritNamespaces;
	}

	public int getContextPosition()
	{
		return position;
	}

	public int getContextSize() throws ExprException
	{
		return size;
	}

	public ExtensionContext<N> getExtensionContext(final String namespace) throws ExprException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Variant<N> getVariableValue(final QName name) throws ExprException
	{
		return variables.get(name);
	}

	@Override
	public boolean getInheritAttributes() {
		return m_inheritAttributes;
	}

	@Override
	public boolean getInheritNamespaces() {
		return m_inheritNamespaces;
	}

}
