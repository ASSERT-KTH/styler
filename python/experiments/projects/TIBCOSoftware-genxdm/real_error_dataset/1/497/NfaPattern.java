/*
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

import org.genxdm.exceptions.PreCondition;
import org.genxdm.processor.w3c.xs.regex.api.RegExBridge;
import org.genxdm.processor.w3c.xs.regex.api.RegExMachine;
import org.genxdm.processor.w3c.xs.regex.api.RegExPattern;
import org.genxdm.processor.w3c.xs.regex.api.RegExPatternInput;


/**
 * A Pattern for representing and matching against regular expressions. Stores the pattern as a nondeterministic state
 * machine. Can be used for XML content models or textual regular expressions.
 */
final class NfaPattern<E, T> extends Graph<E, NfaMatchState<E>> implements RegExPattern<E, T>
{
	private final RegExBridge<E, T> m_bridge;

	private NfaPattern(final NfaMatchState<E> initialState, final RegExBridge<E, T> bridge)
	{
		super(initialState);
		m_bridge = PreCondition.assertArgumentNotNull(bridge, "bridge");
	}

	/**
	 * Creates a pattern corresponding to the given term, which is a tree of terms.
	 * 
	 * @param term
	 *            the head term of the term model. We allow null which represents the lambda set.
	 */
	static <E, T> NfaPattern<E, T> newPattern(final E term, final RegExBridge<E, T> bridge)
	{
		final NfaMatchState<E> initialState = NfaCompiler.compileNFA(term, bridge);

		return new NfaPattern<E, T>(initialState, bridge);

		// Can't use this yet till a*b bug is fixed.
		// graph.optimize();
	}

	/**
	 * Executes the state machine against the given input token stream.
	 * <p/>
	 * In case of failure, the input stream is left at the token that failed to match. The caller can use that to
	 * determine where the mismatch lies for error reporting.
	 * 
	 * @param input
	 *            the input, which is a sequence of tokens
	 * @return true if the input matches the pattern
	 */
	public boolean matches(final RegExPatternInput<E, T> input)
	{
		return matches(input, null);
	}

	/**
	 * Executes the state machine against the given input token stream. Returns true if the input matches the pattern.
	 * If the followers parameter is not null, it will be returned with possible leaf PatternTerms that could follow the
	 * last matched-token.
	 * <p>
	 * In case of failure, the input stream is left at the token that failed to match. The caller can use that to
	 * determine where the mismatch lies for error reporting.
	 * 
	 * @param input
	 *            a PatternInput, advanced as needed.
	 * @param followers
	 *            an output List of PatternTerm.
	 * @return true if the input matches the pattern
	 */
	public boolean matches(final RegExPatternInput<E, T> input, final List<E> followers)
	{
		final NfaStepper<E, T> stepper = new NfaStepper<E, T>(m_initialState, followers, m_bridge);
		final List<E> matchTerms = new ArrayList<E>(23);
		while (input.hasNext())
		{
			matchTerms.clear();
			final T token = input.peek();
			if (stepper.step(token, matchTerms))
			{
				input.matchedPeek(matchTerms); // tell input who matched
				input.next(); // consume the term and continue
			}
			else
			{
				return false;
			}
		}
		return stepper.step(null, null);
	}

	public RegExMachine<E, T> createRegExMachine(final List<E> followers)
	{
		return new NfaStepper<E, T>(m_initialState, followers, m_bridge);
	}

	/**
	 * Returns terms that could follow the given token. if token if null, returns possible first terms.
	 * 
	 * @param token
	 *            token or null
	 * @return List of PatternTerm
	 */
	public List<E> getFollowers(T token)
	{
		final List<E> followers = new ArrayList<E>(23);
		if (token == null)
		{
			addFollowers(m_initialState, followers, new ArrayList<NfaMatchState<E>>(23));
		}
		else
		{
			final List<NfaMatchState<E>> states = new ArrayList<NfaMatchState<E>>(23);
			addStatesToVector(m_initialState, states);
			for (final NfaMatchState<E> s : states)
			{
				if (s.getTerm() != null && m_bridge.matches(s.getTerm(), token))
				{
					final List<NfaMatchState<E>> matchStates = s.nextStates();
					final int count = matchStates.size();
					for (int index = 0; index < count; index++)
					{
						addFollowers(matchStates.get(index), followers, new ArrayList<NfaMatchState<E>>(23));
					}
				}
			}
		}
		return followers;
	}

	/**
	 * add all leaf terms that could directly follow the given state.
	 * 
	 * @param followers
	 *            a List of PatternTerm.
	 */
	protected void addFollowers(NfaMatchState<E> curState, List<E> followers, List<NfaMatchState<E>> visited)
	{
		if (curState.getTerm() == null)
		{
			// avoid infinite loop in case of (a?)*
			if (visited.indexOf(curState) == -1)
			{
				visited.add(curState);
				final List<NfaMatchState<E>> matchStates = curState.nextStates();
				final int count = matchStates.size();
				for (int index = 0; index < count; index++)
				{
					addFollowers(matchStates.get(index), followers, visited);
				}
			}
		}
		else if (followers.indexOf(curState.getTerm()) == -1 && curState.getTerm().toString() != null)
		{
			followers.add(curState.getTerm());
		}
	}

	/**
	 * returns whether this pattern is deterministic or not, based on the XML/SGML definition. Non-determinism is
	 * allowed in the state machine, but only if alternate paths are based on the same PatternTerm. See Unique Particle
	 * Attribute Constraint in XML Schema Structures.
	 * 
	 * @return true if deterministic
	 */
	/*
	 * public boolean isDeterministic() { return (getPairOffendingDeterministic() == null); }
	 */

	/*
	 * public RegExPair<E, E> getPairOffendingDeterministic() { // TODO: This doesn't seem right. Shouldn't we consider
	 * states that have an exit condition too? // Create a list containing all the states in the NFA. final
	 * List<NfaMatchState<E>> states = new ArrayList<NfaMatchState<E>>(23); addStatesToVector(m_initialState, states);
	 * 
	 * final List<NfaMatchState<E>> visited = new ArrayList<NfaMatchState<E>>(23); for (final NfaMatchState<E> s :
	 * states) { // If the state has no exit condition... if (s.getTerm() == null) { final List<E> followers = new
	 * ArrayList<E>(23); addFollowers(s, followers, visited); // check for duplicates for (int j = 0; j <
	 * followers.size() - 1; j++) { final E a = followers.get(j); for (int k = j + 1; k < followers.size(); k++) { final
	 * E b = followers.get(k); // don't count as duplicate if terms are the same. Happens for // patterns like
	 * ((a|b*)+), which get converted to ((a|b*),(a|b*)*) // since + is not handled by the state machine. if (a != b &&
	 * m_bridge.intersects(a, b)) { return new RegExPair<E, E>(a, b); // has duplicate followers, so it's not
	 * deterministic } } } } } return null; }
	 */

	/**
	 * Prints a text representation of the state machine for debugging purposes. For each state, the output is: state
	 * number, term accepted at that state, and a list of follower states. For example, below is a state machine for
	 * (a*,(b|c)):
	 * 
	 * <PRE>
	 * 0  ==&gt;  1
	 * 1  ==&gt;  2 3
	 * 2 a ==&gt;  1
	 * 3  ==&gt;  4 6
	 * 4 b ==&gt;  5
	 * 5  ==&gt;
	 * 6 c ==&gt;  5
	 * </PRE>
	 * 
	 * 0 is the start state, and 5 is the end state.
	 * <p/>
	 * A state can be entered if the current token matches the state's term.
	 * 
	 * @return multi-line String
	 */
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		final List<NfaMatchState<E>> states = new ArrayList<NfaMatchState<E>>(23);
		addStatesToVector(m_initialState, states);
		for (int i = 0; i < states.size(); i++)
		{
			sb.append("#").append(i).append(" ");
			final NfaMatchState<E> s = states.get(i);
			if (s.getTerm() != null)
			{
				sb.append(s.getTerm().toString());
			}
			sb.append(" ==> ");
			List<NfaMatchState<E>> matchStates = s.nextStates();
			int count = matchStates.size();
			for (int index = 0; index < count; index++)
			{
				NfaMatchState<E> next = matchStates.get(index);
				sb.append(" ").append(states.indexOf(next));
			}
			sb.append('\n');
		}
		return sb.toString();
	}

	/**
	 * For debugging.
	 * 
	 * @param name
	 *            label to write out
	 */
	protected void printStateMachine(final String name)
	{
		System.out.print("Name = " + name + toString());
	}

	/**
	 * add new states starting at 'from' to the given List
	 */
	private void addStatesToVector(final NfaMatchState<E> from, final List<NfaMatchState<E>> states)
	{
		if (states.indexOf(from) >= 0)
		{
			return; // already added
		}
		states.add(from);
		for (final NfaMatchState<E> matchState : from.nextStates())
		{
			addStatesToVector(matchState, states);
		}
	}
}
