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

import javax.xml.namespace.QName;

import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.components.SchemaWildcard;

/**
 * State-machine abstraction used for XML content model validation of a child axis.
 */
interface SmContentFiniteStateMachine
{
	/**
	 * Steps the state machine upon completion of all elements.
	 * 
	 * @return <code>true</code> if no more elements are expected.
	 */
	boolean end();

	/**
	 * Returns an element declaration if an element is matched.
	 */
	ElementDefinition getElement();

	/**
	 * Returns a wildcard if a wildcard is matched.
	 */
	SchemaWildcard getWildcard();

	/**
	 * Determines whether the last step matched an element declaration.
	 */
	boolean isElementMatch();

	/**
	 * Determines whether the last step matched a wildcard.
	 */
	boolean isWildcardMatch();

	/**
	 * Steps the state machine upon arrival of an element with the specified name.
	 * 
	 * @param name
	 *            The name of the element.
	 * @return <code>true</code> if a transition exists for the element.
	 */
	boolean step(QName name);
}
