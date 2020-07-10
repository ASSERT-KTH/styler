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

/**
 * The status of a streaming XPath.
 */
final class IdentityXPathStatus
{
	public int currentStep = 0;

	/**
	 * Refers to whether the type of the element is simple. This determines whether the text can be used as a key.
	 */
	public boolean isSimple = false;

	/**
	 * This is synonymous with the status not being the first in the list. All status entries after the first were
	 * dynamically added as a result of evaluating the XPath expression in a streaming fashion with a relocation (//).
	 */
	public final boolean removable;

	public IdentityXPathStatus(final boolean removable)
	{
		this.removable = removable;
	}
}
