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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

abstract class Graph<D, V extends Vertex<D, V>>
{
	protected final V m_initialState; // of the state machine

	public Graph(final V initialState)
	{
		m_initialState = initialState;
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();

		final HashMap<V, Integer> index = new HashMap<V, Integer>();
		final Stack<Edge<D, V>> stack = new Stack<Edge<D, V>>();

		int i = 0;
		stack.push(new Edge<D, V>(null, m_initialState));
		index.put(m_initialState, i++);

		while (!stack.isEmpty())
		{
			final V k = stack.pop().to;

			sb.append("#").append(index.get(k)).append(" ");
			if (k.getTerm() != null)
			{
				sb.append(k.getTerm().toString());
			}
			sb.append(" ==> ");

			for (final V t : k.nextStates())
			{
				if (!index.containsKey(t))
				{
					stack.push(new Edge<D, V>(k, t));
					sb.append(" ").append(i);
					index.put(t, i++);
				}
				else
				{
					sb.append(" ").append(index.get(t));
				}
			}
			sb.append('\n');
		}

		for (final Edge<D, V> edge : deadEdges(m_initialState))
		{
			sb.append("dead(").append(index.get(edge.from)).append(",").append(index.get(edge.to)).append(")").append('\n');
		}

		return sb.toString();
	}

	private void cleanup()
	{
		final HashMap<V, Boolean> visited = new HashMap<V, Boolean>();
		final Stack<Edge<D, V>> stack = new Stack<Edge<D, V>>();

		stack.push(new Edge<D, V>(null, m_initialState));
		visited.put(m_initialState, Boolean.TRUE);

		while (!stack.isEmpty())
		{
			final Edge<D, V> edge = stack.pop();
			final V vertex = edge.to;

			boolean more = true;
			if (edge.from == null)
			{
				// Must be the start vertex.
			}
			else if (vertex.isEnd())
			{
				// Must be the end vertex!
				more = false;
			}
			else
			{
				final V target = collapseOut(vertex);
				if (null != target)
				{
					if (!target.isEnd())
					{
						vertex.nextStates().clear();
						vertex.nextStates().addAll(target.nextStates());
						// Push this edge
						stack.push(edge);
						more = false;
					}
				}
			}
			if (more)
			{
				for (final V to : vertex.nextStates())
				{
					if (!visited.containsKey(to))
					{
						stack.push(new Edge<D, V>(vertex, to));
						visited.put(to, Boolean.TRUE);
					}
				}
			}
		}
	}

	private V collapseOut(final V vertex)
	{
		V target = null;
		int unconditional = 0;
		int conditional = 0;
		for (final V next : vertex.nextStates())
		{
			if (null != next.getTerm())
			{
				conditional++;
			}
			else
			{
				unconditional++;
				target = next;
			}
		}

		if (1 == unconditional)
		{
			if (0 == conditional)
			{
				return target;
			}
			else
			{
				throw new IllegalStateException("Must run dead path removal first.");
			}
		}
		else
		{
			return null;
		}
	}

	/**
	 * Remove unreachable transitions and short-circuit unconditional transitions.
	 */
	// TODO:("Can't use this yet. e.g. a*b doesn't work.")
	public void optimize()
	{
		for (final Edge<D, V> edge : deadEdges(m_initialState))
		{
			edge.from.nextStates().remove(edge.to);
		}

		cleanup();
	}

	private Iterable<Edge<D, V>> deadEdges(final V initialState)
	{
		final List<Edge<D, V>> deadCode = new LinkedList<Edge<D, V>>();

		final HashMap<V, Boolean> visited = new HashMap<V, Boolean>();
		final Stack<Edge<D, V>> stack = new Stack<Edge<D, V>>();

		stack.push(new Edge<D, V>(null, initialState));
		visited.put(initialState, Boolean.TRUE);

		while (!stack.isEmpty())
		{
			final Edge<D, V> edge = stack.pop();
			final V from = edge.to;

			for (final V to : from.nextStates())
			{
				if (!visited.containsKey(to))
				{
					stack.push(new Edge<D, V>(from, to));
					visited.put(to, Boolean.TRUE);
				}

				if (deadEdge(from, to))
				{
					deadCode.add(new Edge<D, V>(from, to));
				}
			}
		}

		return deadCode;
	}

	public static boolean deadEdge(final Vertex<?, ?> from, final Vertex<?, ?> to)
	{
		if (null == to.getTerm())
		{
			return false;
		}

		for (final Vertex<?, ?> t : from.nextStates())
		{
			if (null == t.getTerm())
			{
				return true;
			}
		}

		return false;
	}
}
