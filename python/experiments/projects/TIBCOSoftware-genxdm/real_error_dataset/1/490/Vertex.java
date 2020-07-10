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
package org.genxdm.processor.w3c.xs.regex.nfa;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation for graphs that are not dense. <br/>
 * All the vertices connected to each vertex are listed on an adjacency list for that vertex. <br/>
 * In this case though, we only have the following vertices. <br/>
 * As this stands, some simple operations are not easily supported. For example, vertex deletion.
 */
abstract class Vertex<D, V extends Vertex<D, V>>
{
	private D m_term; // must be a leaf term or null
	private List<V> m_next; // possible next states (zero-length if end state)

	public Vertex(final D term)
	{
		m_term = term;
		m_next = new ArrayList<V>(23);
	}

	public final D getTerm()
	{
		return m_term;
	}

	public final List<V> nextStates()
	{
		return m_next;
	}

	void addNext(final V s)
	{
		m_next.add(s);
	}

	boolean isEnd()
	{
		return m_next.isEmpty();
	}
}
