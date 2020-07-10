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

import org.genxdm.xpath.v10.DynamicContextBuilderBase;

public abstract class DynamicContextBuilderBaseImpl implements DynamicContextBuilderBase {

    protected int position;
    protected int size;
    protected boolean m_inheritAttributes = false;
    protected boolean m_inheritNamespaces = true;

    public DynamicContextBuilderBaseImpl() {
        super();
    }

    @Override
    public void setContextPosition(final int position) {
    	this.position = position;
    }

    @Override
    public void setContextSize(final int size) {
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