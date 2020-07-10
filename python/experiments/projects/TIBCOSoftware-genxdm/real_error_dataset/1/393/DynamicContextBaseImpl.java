/*
 * Copyright (c) 2012 TIBCO Software Inc.
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

import org.genxdm.xpath.v10.DynamicContextBase;
import org.genxdm.xpath.v10.ExtensionContext;

public class DynamicContextBaseImpl implements DynamicContextBase {

    protected boolean m_inheritAttributes;
    protected boolean m_inheritNamespaces;
    protected final int position;
    protected final int size;

    public DynamicContextBaseImpl(final int position, final int size,
            boolean inheritAttributes, boolean inheritNamespaces) {
        this.position = position;
        this.size = size;
        this.m_inheritAttributes = inheritAttributes;
        this.m_inheritNamespaces = inheritNamespaces;
    }

    @Override
    public int getContextPosition() {
    	return position;
    }

    @Override
    public int getContextSize() {
    	return size;
    }

    @Override
    public ExtensionContext getExtensionContext(final String namespace) {
    	// TODO Auto-generated method stub
    	return null;
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