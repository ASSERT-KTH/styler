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
package org.genxdm.processor.w3c.xs.regex.api;

import java.util.Collection;

/**
 * Provides the aspects of a set that are required for lazily matching tokens.
 */
public interface RegExDerivative<E, T>
{
	/**
	 * Determines whether the specified expression is the empty set.
	 * 
	 * @param expression
	 *            The expression describing a set of sequences of tokens.
	 */
	boolean empty(E expression);

	/**
	 * Determines whether the specified expression contains the zero length token sequence.
	 * 
	 * @param expression
	 *            The expression describing a set of sequences of tokens.
	 */
	boolean delta(E expression);

	/**
	 * Computes the derivative (residual) of the expression wrt the specified token.
	 * 
	 * @param expression
	 *            The expression describing a set of sequences of tokens.
	 * @param token
	 *            A token.
	 * @param matchers
	 *            The expressions that matched the token.
	 * @return <code>null</code> for an expression that does not allow any sequence (empty set).
	 */
	E residual(E expression, T token, Collection<? super E> matchers);
}
