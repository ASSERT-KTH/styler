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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.genxdm.xpath.v10.StaticContext;

public final class ExprContextStaticImpl 
    implements StaticContext
{
	private final Set<QName> variables = new HashSet<QName>();
	private final Map<String, String> namespaces = new HashMap<String, String>();

	public ExprContextStaticImpl() {
	}

	public boolean containsVariable(final QName name)
	{
		return variables.contains(name);
	}

	public String getNamespace(final String prefix)
	{
		return namespaces.get(prefix);
	}

	public void declareNamespace(final String prefix, final String namespace)
	{
		namespaces.put(prefix, namespace);
	}

	public void declareVariable(final QName name)
	{
		variables.add(name);
	}

	public void reset()
	{
		variables.clear();
		namespaces.clear();
	}
}
