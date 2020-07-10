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

import org.genxdm.xs.components.ParticleTerm;

/**
 * This interface represents the term that we will be using to construct the regular expression patterns. The validation
 * model is expected to provide pre-compiled regular expression patterns from which regular expression finite state
 * machines are built.
 */
interface ValidationExpr
{
	boolean isGroup();

	boolean isSequence();

	boolean isChoice();

	boolean isInterleave();

	Iterable<ValidationExpr> getSubTerms();

	ParticleTerm getParticleTerm();

	boolean matches(final QName token);

	boolean intersects(final ValidationExpr other);

	int minOccurs();

	int maxOccurs();

	boolean isMaxOccursUnbounded();
}
