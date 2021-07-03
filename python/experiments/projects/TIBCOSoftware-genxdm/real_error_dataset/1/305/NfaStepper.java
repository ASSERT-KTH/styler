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
package org.genxdm.processor.w3c.xs.regex.impl.nfa;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.processor.w3c.xs.regex.api.RegExBridge;
import org.genxdm.processor.w3c.xs.regex.api.RegExMachine;


/**
 * Pattern internal class for matching input against a state machine one input token at a time. <br/>
 * This class simulates the NFA.
 */
final class NfaStepper<E, T> implements RegExMachine<E, T>
{
	// The current list of states that the NFA is in...
	private LinkedList<NfaMatchState<E>> m_clist = new LinkedList<NfaMatchState<E>>();

	private List<E> m_followers;
	private RegExBridge<E, T> m_bridge;
	private NfaMatchAllState<E> allState; // can only be one of these active at a time
	private List<E> remainingOptional; // used for "all" processing
	private List<E> remainingRequired; // used for "all" processing

	/**
	 * Creates a stepper and supplies a place to put follower terms.
	 * 
	 * @param start
	 *            the initial state of a state machine to step through
	 * @param followers
	 *            output list of PatternTerms, to be filled during operation
	 */
	public NfaStepper(final NfaMatchState<E> start, final List<E> followers, final RegExBridge<E, T> bridge)
	{
		// Initialize the current list of states to the starting state.
		m_clist.addFirst(start);
		m_followers = followers;
		m_bridge = PreCondition.assertArgumentNotNull(bridge, "bridge");
	}

	/**
	 * Processes the given token with the state machine. if token is null, then we are at the end of input, and so make
	 * sure the state machine is in an end state
	 * 
	 * @param token
	 *            an input token or null
	 * @param matchers
	 *            output List containing PatternTerms that matched the input
	 * @return true if the token can be consumed by the pattern or if input is null and state machine is at an end state
	 */
	public boolean step(final T token, final List<? super E> matchers)
	{
		if (m_followers != null)
		{
			m_followers.clear();
		}

		if (allState != null)
		{
			return allStep(token, matchers);
		}

		// TODO: Find a more efficient way to do this? Some solutions use a generation number but that
		// TODO: would mean labelling the states diectly.
		List<NfaMatchState<E>> visitedThisTurn = new ArrayList<NfaMatchState<E>>(23);
		// Map visitedThisTurn = new HashMap();

		// Append a marker (null) to separate the current states from the next states
		m_clist.add(null); // null separates steps in the deque

		while (true)
		{
			final NfaMatchState<E> curState = m_clist.removeFirst();
			if (curState == null)
			{
				// we're done with this step
				if (m_clist.size() == 0)
				{
					// This is the error condition. We no states for next time
					// so we definitely can't find ourselves in an accept (end) state.
					// The follower expressions are already synchronized (empty).
					return false;
				}
				else
				{
					if (token == null)
					{
						// We should have returned by now with the accept condition
						// so the fact that we have not says that we didn't satisfy
						// the accept condition.
						NfaHelper.updateFollowers(m_clist, m_followers);
						return false;
					}
					else
					{
						// TODO: Why don't we update the followers - for design time tools that is.
						return true; // we added work for the next step
					}
				}
			}
			else if (curState.isEnd())
			{
				if (token == null)
				{
					// TODO: We're bailing out early so the follower list as actually incomplete.
					NfaHelper.updateFollowers(m_clist, m_followers);
					return true; // This is the accept condition.
				}
			}
			else if (curState.isAll())
			{
				// TODO: Don't understand this yet.
				if (allState == null)
				{
					allState = (NfaMatchAllState<E>)curState;
					remainingOptional = new ArrayList<E>(allState.getOptionalTerms());
					remainingRequired = new ArrayList<E>(allState.getRequiredTerms());
					return allStep(token, matchers);
				}
			}
			else if (curState.getTerm() == null)
			{
				// There is no exit condition so we need to process the next states.
				// Furthermore, these states need to be processed with the incoming token parameter,
				// so we are going to push them onto the deque and handle them immediately.
				for (final NfaMatchState<E> next : curState.nextStates())
				{
					/*
					 * if (!visitedThisTurn.containsKey(next)) { visitedThisTurn.put(next, next); work.addHead(next); }
					 */
					if (visitedThisTurn.indexOf(next) == -1)
					{
						visitedThisTurn.add(next);
						m_clist.addFirst(next); // push onto deque so as not to be behind the marker
					}
				}
			}
			else if (token != null && m_bridge.matches(curState.getTerm(), token))
			{
				if (matchers != null)
				{
					matchers.add(curState.getTerm());
				}
				for (final NfaMatchState<E> e : curState.nextStates())
				{
					m_clist.add(e);
				}
			}
			else if (token == null && m_bridge.matches(curState.getTerm(), null))
			{
				// There is no exit condition so we need to process the next states.
				// Furthermore, these states need to be processed with the incoming token parameter,
				// so we are going to push them onto the deque and handle them immediately.
				for (final NfaMatchState<E> next : curState.nextStates())
				{
					/*
					 * if (!visitedThisTurn.containsKey(next)) { visitedThisTurn.put(next, next); work.addHead(next); }
					 */
					if (visitedThisTurn.indexOf(next) == -1)
					{
						visitedThisTurn.add(next);
						m_clist.addFirst(next); // push onto deque so as not to be behind the marker
					}
				}
			}
			else
			{
				// TODO: This just looks broken. What is it for?
				// nothing consumed, remember for possible error reporting
				if (m_followers != null)
				{
					m_followers.add(curState.getTerm());
				}
			}
		}
	}

	/**
	 * Processes the given token with the state machine. if token is null, then we are at the end of input, and so make
	 * sure the state machine is in an end state
	 * <p/>
	 * Special case for handling the "all" content model type.
	 * 
	 * @param token
	 *            an input token or null
	 * @param matchers
	 *            output List containing PatternTerms that matched the input
	 * @return true if the token can be consumed by the pattern or if input is null and state machine is at an end state
	 */
	public boolean allStep(T token, List<? super E> matchers)
	{
		if (token == null)
		{
			// end of input, should have consumed all required items
			if (m_followers != null)
			{
				// update followers
				for (final E e : remainingRequired)
				{
					m_followers.add(e);
				}
				for (final E e : remainingOptional)
				{
					m_followers.add(e);
				}
			}
			return remainingRequired.size() == 0;
		}
		else
		{
			for (int i = 0; i < remainingRequired.size(); i++)
			{
				E t = remainingRequired.get(i);
				if (m_bridge.matches(t, token))
				{
					if (matchers != null)
						matchers.add(t);
					remainingRequired.remove(i);
					return true; // got a match for token
				}
			}
			for (int i = 0; i < remainingOptional.size(); i++)
			{
				E t = remainingOptional.get(i);
				if (m_bridge.matches(t, token))
				{
					if (matchers != null)
					{
						matchers.add(t);
					}
					remainingOptional.remove(i);
					return true; // got a match for token
				}
			}
			if (m_followers != null)
			{
				// update followers
				for (final E e : remainingRequired)
				{
					m_followers.add(e);
				}
				for (final E e : remainingOptional)
				{
					m_followers.add(e);
				}
			}
			return false;
		}
	}
}
