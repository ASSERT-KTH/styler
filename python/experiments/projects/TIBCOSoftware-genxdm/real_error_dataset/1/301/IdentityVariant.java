/**
 * Copyright (c) 2009-2010 TIBCO Software Inc.
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
package org.genxdm.processor.w3c.xs.validation.impl;

import java.util.ArrayList;

import org.genxdm.exceptions.PreCondition;

/**
 * A variant that can store one of two things, dangling references or a value.
 */
final class IdentityVariant
{
	private final ArrayList<IdentityDanglingReference> m_refs;
	private final Boolean m_value;

	public IdentityVariant(final ArrayList<IdentityDanglingReference> refs)
	{
		m_refs = PreCondition.assertArgumentNotNull(refs, "refs");
		m_value = null;
	}

	public IdentityVariant(final Boolean value)
	{
		m_refs = null;
		m_value = PreCondition.assertArgumentNotNull(value, "value");
	}

	public boolean isDanglingRefs()
	{
		return (null != m_refs);
	}

	public ArrayList<IdentityDanglingReference> getDanglingRefs()
	{
		PreCondition.assertTrue(isDanglingRefs(), "isDanglingRefs()");
		return m_refs;
	}

	public boolean isValue()
	{
		return (null != m_value);
	}

	public Boolean getValue()
	{
		PreCondition.assertTrue(isValue(), "isValue()");
		return m_value;
	}
}
