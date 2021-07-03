package io.github.agentsoz.jill.core;

/*
 * #%L Jill Cognitive Agents Platform %% Copyright (C) 2014 - 2017 by its authors. See AUTHORS file.
 * %% This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>. #L%
 */

import io.github.agentsoz.jill.Main;
import io.github.agentsoz.jill.config.GlobalConstant;
import io.github.agentsoz.jill.core.beliefbase.Belief;
import io.github.agentsoz.jill.lang.Agent;
import io.github.agentsoz.jill.lang.Goal;
import io.github.agentsoz.jill.lang.Plan;
import io.github.agentsoz.jill.lang.PlanBindings;
import io.github.agentsoz.jill.lang.PlanStep;
import io.github.agentsoz.jill.struct.GoalType;
import io.github.agentsoz.jill.struct.PlanType;
import io.github.agentsoz.jill.util.Log;
import io.github.agentsoz.jill.util.Stack255;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

public class IntentionSelector implements Runnable {

  private final Logger logger = LoggerFactory.getLogger(Main.LOGGER_NAME);

  private final int poolid;
  private final Set<Integer> activeAgents;
  Set<Integer> extToRemove;
  Set<Integer> extToAdd;

  private final Object lock;
  private boolean hasMessage;
  private boolean isIdle;
  private boolean shutdown;
  private final PlanBindings bindings; // plan bindings

  /**
   * Constructs a new intention selector to manage a set of agents.
   * 
   * @param poolid ID of this pool (must follow the sequence 0,1,2,3,...).
   * @param seed to initialise the random number generator
   */
  public IntentionSelector(int poolid, long seed) {
    this.poolid = poolid;
    this.lock = new Object();
    this.hasMessage = false;
    this.isIdle = false;
    this.shutdown = false;
    activeAgents = new HashSet<Integer>();
    extToRemove = new HashSet<Integer>();
    extToAdd = new HashSet<Integer>();
    bindings = new PlanBindings(new Random(seed));
  }

  /**
   * Runs this intentions selction thread.
   */
  public void run() {
    Set<Integer> toRemove = new HashSet<Integer>();
    do {
      boolean idle = true;
      // Remove agents that have have become idle due to an external event
      removeInactiveAgents();
      // Add agents that have have become active due to an external event
      addActiveAgents();
      for (Integer i : activeAgents) {
        Agent agent = (Agent) GlobalState.agents.get(i);
        Stack255 agentExecutionStack = (Stack255) (agent).getExecutionStack();
        if (!isStackValid(agent, agentExecutionStack)) {
          // Mark this agent for removal
          toRemove.add(i);
          continue;
        }
        // At least one agent is active
        idle = false;
        // Get the item at the top of the stack
        Object node = (Object) agentExecutionStack.get((byte) (agentExecutionStack.size() - 1));
        if (node instanceof Plan) {
          // If it is a plan then execute a plan step; and if it finished then remove it
          managePlan(i, agentExecutionStack, (Plan) node, toRemove);
        } else if (node instanceof Goal) {
          // If it is a goal then find a plan for it and put it on the stack
          manageGoal(i, agent, agentExecutionStack, (Goal) node);
        }
      }
      // remove agents that have finished executing plans and have gone idle in this cycle
      removeFinishedAgents(toRemove);
      if (idle) {
        waitOnExternalMessage();
        if (shutdown) {
          break;
        }
      }
    } while (true);
    logger.debug("Pool {} is exiting", poolid);
  }

  /**
   * Checks if this agent's execution stack is valid. The stack is valid if it is not null or empty
   * and has not exceeded the maximum size limit of 255.
   * 
   * @param agent the agent whose stack is to be checked
   * @param agentExecutionStack that agent's execution stack
   * @return true if stack is valid, false otherwise
   */
  private boolean isStackValid(Agent agent, Stack255 agentExecutionStack) {
    if (agentExecutionStack == null) {
      return false;
    }
    final int esSize = agentExecutionStack.size();
    logger.trace("{} execution stack is {}/255 full", Log.logPrefix(agent.getId()), esSize);
    if (esSize == 0) {
      return false;
    }
    if (esSize >= 255) {
      logger.error("{} execution stack reached size limit of 255. Cannot continue.",
          Log.logPrefix(agent.getId()));
      return false;
    }
    return true;
  }

  /**
   * Removes the given list of agents from the list of active agents.
   * 
   * @param toRemove the list of agent IDs to remove
   */
  private void removeFinishedAgents(Set<Integer> toRemove) {
    if (!toRemove.isEmpty()) {
      for (int i : toRemove) {
        activeAgents.remove(i);
      }
      toRemove.clear();
    }
  }

  /**
   * Manages the goal at the top of the execution stack of an agent. All relevant plans are
   * evaluated to see if their context conditions hold. Plans deemed applicable are then added to
   * the list of bindings from which a plan instane will be eventually selected.
   * 
   * @param agentIndex the agent's index
   * @param agent the agent in question
   * @param agentExecutionStack the agent's execution stack
   * @param node the goal at the top of the xecution stack
   * @return false if something went wrong, true otherwise
   */
  private boolean manageGoal(int agentIndex, Agent agent, Stack255 agentExecutionStack, Goal node) {
    // Get the goal type for this goal
    GoalType gtype = (GoalType) GlobalState.goalTypes.find(node.getClass().getName());
    byte[] ptypes = gtype.getChildren();
    assert (ptypes != null);
    // Clear any previous plan bindings before adding any new ones
    bindings.clear();
    for (int p = 0; p < ptypes.length; p++) {
      PlanType ptype = (PlanType) GlobalState.planTypes.get(ptypes[p]);

      try {
        // Create an object of this Plan type, so we can
        // access its context condition
        Plan planInstance =
            (Plan) (ptype.getPlanClass().getConstructor(Agent.class, Goal.class, String.class)
                .newInstance(GlobalState.agents.get(agentIndex), node, "p"));
        // Clear previously buffered context results if any
        agent.clearLastResults();
        // Evaluate the context condition
        if (planInstance.context()) {
          // Get the results of context query just performed
          Set<Belief> results = agent.getLastResults();
          // Add the results to the bindings
          bindings.add(planInstance, (results == null) ? null : new LinkedHashSet<Belief>(results));
        }
      } catch (NoSuchMethodException | SecurityException | InstantiationException
          | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        logger.error("Could not create plan object of type " + ptype.getClass().getName(), e);
      }
    }
    int numBindings = bindings.size();
    if (numBindings == 0) {
      // No plan options for this goal at this point in time, so move to the next agent
      logger.debug(Log.logPrefix(agent.getId()) + " has no applicable plans for goal " + gtype
          + " and will continue to wait indefinitely");
      return false;
    }
    // Call the meta-level planning prior to plan selection
    agent.notifyAgentPrePlanSelection(bindings);
    // Pick a plan option using specified policy
    Plan planInstance = bindings.selectPlan(GlobalConstant.PLAN_SELECTION_POLICY);
    // Now push the plan on to the intention stack
    synchronized (agentExecutionStack) {
      logger.debug(Log.logPrefix(agent.getId()) + " choose an instance of plan "
          + planInstance.getClass().getSimpleName() + " to handle goal "
          + gtype.getClass().getSimpleName());
      agentExecutionStack.push(planInstance);
    }
    return true;
  }

  /**
   * Manages the plan at the top of this agent's execution stack. If the plan has finished it is
   * removed, else it is progresses by a single {@link PlanStep}.
   * 
   * @param agentIndex the agent in question
   * @param agentExecutionStack this agent's execution stack
   * @param node the plan at the top of the execution stack
   * @param toRemove the remove list to which this agent should be added if the plan has finished
   */
  private void managePlan(int agentIndex, Stack255 agentExecutionStack, Plan node,
      Set<Integer> toRemove) {
    // If done then pop this plan/goal
    if (node.hasfinished()) {
      logger.debug(Log.logPrefix(agentIndex) + " finished executing plan "
          + node.getClass().getSimpleName());
      synchronized (agentExecutionStack) {
        // Pop the plan off the stack
        agentExecutionStack.pop();
        // Pop the goal off the stack
        agentExecutionStack.pop();
        if (agentExecutionStack.isEmpty()) {
          // Mark this agent as idle
          // Main.setAgentIdle(i, true);
          toRemove.add(agentIndex);
        }
      }
    } else {
      logger.debug(Log.logPrefix(agentIndex) + " is executing a step of plan "
          + node.getClass().getSimpleName());
      node.step();
    }
  }

  /**
   * Removes from {@link #activeAgents} all agents flagged by external events as having become
   * inactive.
   */
  private void removeInactiveAgents() {
    synchronized (extToRemove) {
      if (!extToRemove.isEmpty()) {
        for (int i : extToRemove) {
          activeAgents.remove(i);
        }
        extToRemove.clear();
      }
    }
  }

  /**
   * Adds to {@link #activeAgents} all agents flagged by external events as having become active.
   */
  private void addActiveAgents() {
    synchronized (extToAdd) {
      if (!extToAdd.isEmpty()) {
        for (int i : extToAdd) {
          activeAgents.add(i);
        }
        extToAdd.clear();
      }
    }
  }

  /**
   * Waits on {@link IntentionSelector#lock} until woken up by an external message.
   */
  private void waitOnExternalMessage() {
    synchronized (lock) {
      while (!hasMessage) {
        try {
          logger.debug("Pool {} is idle; will wait on external message", poolid);
          // Main.incrementPoolsIdle();
          isIdle = true;
          Main.flagPoolIdle();
          lock.wait();
          isIdle = false;
          // Main.decrementPoolsIdle();
          logger.debug("Pool {} just woke up on external message", poolid);
        } catch (InterruptedException e) {
          logger.error("Pool " + poolid + " failed to wait on external message: ", e);
        }
      }
      hasMessage = false;
    }
  }

  /**
   * Flags to this intention selection thread that an external message, to an agent managed by this
   * thread, is waiting to be processed.
   */
  public void flagMessage() {
    synchronized (lock) {
      logger.debug("Pool {} received a new message", poolid);
      hasMessage = true;
      lock.notify(); // NOPMD - ignore notifyall() warning
    }
  }

  /**
   * Checks if this intention selector is idle.
   * 
   * @return true if idle, false otherwise
   */
  public boolean isIdle() {
    return isIdle && !hasMessage;
  }

  /**
   * Terminates this intention selector thread.
   */
  public void shutdown() {
    synchronized (lock) {
      logger.debug("Pool {} received shutdown message", poolid);
      shutdown = true;
      hasMessage = true;
      lock.notify(); // NOPMD - ignore notifyall() warning
    }
  }

  /**
   * Sets the idle status of the given agent managed by this intention selector.
   * 
   * 
   * @param agentId ID of the agent whose idle status is being set
   * @param idle the new idle status of this agent
   */
  // FIXME: Threading issue when external threads changes activeagents
  // and this thread is still iterating over activeagents
  public void setAgentIdle(int agentId, boolean idle) {
    // If agent is becoming active, and not already active
    if (!idle /* && !activeAgents.contains(agentId) */) {
      synchronized (extToAdd) {
        extToAdd.add(agentId);
      }
    }
    // If agent is becoming idle, and not already idle
    if (idle /* && activeAgents.contains(agentId) */) {
      synchronized (extToRemove) {
        extToRemove.add(agentId);
      }
    }
  }
}
