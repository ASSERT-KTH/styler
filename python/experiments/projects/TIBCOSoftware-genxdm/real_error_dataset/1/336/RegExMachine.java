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

import java.util.List;

/**
 * A stepper over a Finite State Representation.
 * 
 * @author David Holmes
 * 
 * @param <E>
 *            The expression handle.
 * @param <T>
 *            The token handle.
 */
public interface RegExMachine<E, T>
{
	/**
	 * Processes the given token with the state machine. if token is null, then we are at the end of input, and so make
	 * sure the state machine is in an end state
	 * 
	 * @param token
	 *            an input token or null
	 * @param matchers
	 *            output {@link List} containing Expressions that matched the input
	 * @return true if the token can be consumed by the pattern or if input is null and state machine is at an end state
	 */
	boolean step(T token, List<? super E> matchers);
}
