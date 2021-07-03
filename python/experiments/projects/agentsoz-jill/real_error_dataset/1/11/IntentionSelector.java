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
  private final HashSet<Integer> activeAgents;
  HashSet<Integer> extToRemove;
  HashSet<Integer> extToAdd;

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
    HashSet<Integer> toRemove = new HashSet<Integer>();
    do {
      boolean idle = true;
      synchronized (extToRemove) {
        if (!extToRemove.isEmpty()) {
          for (int i : extToRemove) {
            activeAgents.remove(i);
          }
          extToRemove.clear();
        }
      }
      synchronized (extToAdd) {
        if (!extToAdd.isEmpty()) {
          for (int i : extToAdd) {
            activeAgents.add(i);
          }
          extToAdd.clear();
        }
      }

      for (Integer i : activeAgents) {

        Agent agent = (Agent) GlobalState.agents.get(i);
        Stack255 agentExecutionStack = (Stack255) (agent).getExecutionStack();
        int esSize = agentExecutionStack.size();
        logger
            .trace(Log.logPrefix(agent.getId()) + "'s execution stack is " + esSize + "/255 full");
        if (agentExecutionStack == null || esSize == 0) {
          // Mark this agent as idle
          // Main.setAgentIdle(i, true);
          toRemove.add(i);
          continue;
        }
        if (esSize >= 255) {
          logger.error(Log.logPrefix(agent.getId())
              + "'s execution stack has reached the maximum size of 255. Cannot continue.");
          continue;
        }

        // At least one agent is active
        idle = false;

        // Get the item at the top of the stack
        Object node = (Object) agentExecutionStack.get((byte) (esSize - 1));

        // If it is a plan then execute it
        if (node instanceof Plan) {
          // If done then pop this plan/goal
          if (((Plan) node).hasfinished()) {
            logger.debug(Log.logPrefix(agent.getId()) + " finished executing plan "
                + node.getClass().getSimpleName());
            synchronized (agentExecutionStack) {
              // Pop the plan off the stack
              agentExecutionStack.pop();
              // Pop the goal off the stack
              agentExecutionStack.pop();
              if (agentExecutionStack.isEmpty()) {
                // Mark this agent as idle
                // Main.setAgentIdle(i, true);
                toRemove.add(i);
              }
            }
          } else {
            logger.debug(Log.logPrefix(agent.getId()) + " is executing a step of plan "
                + node.getClass().getSimpleName());
            ((Plan) node).step();
          }

          continue;
        }

        // If it is a goal then find a plan for it and put it on the stack
        if (node instanceof Goal) {
          // Get the goal type for this goal
          GoalType gtype = (GoalType) GlobalState.goalTypes.find(node.getClass().getName());
          byte[] ptypes = gtype.getChildren();
          assert (ptypes != null);
          // Clear any previous plan bindings before adding any new ones
          bindings.clear();
          for (int p = 0; p < ptypes.length; p++) {
            PlanType ptype = (PlanType) GlobalState.planTypes.get(ptypes[p]);

            try {
              // Create an object on this Plan type, so we can
              // access its context condition
              Plan planInstance =
                  (Plan) (ptype.getPlanClass().getConstructor(Agent.class, Goal.class, String.class)
                      .newInstance(GlobalState.agents.get(i), node, "p"));
              // Clear previously buffered context results if any
              agent.clearLastResults();
              // Evaluate the context condition
              if (planInstance.context()) {
                // Get the results of context query just performed
                Set<Belief> results = agent.getLastResults();
                // Add the results to the bindings
                bindings.add(planInstance,
                    (results == null) ? null : new LinkedHashSet<Belief>(results));
              }
            } catch (NoSuchMethodException | SecurityException | InstantiationException
                | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
              logger.error("Could not create plan object of type " + ptype.getClass().getName(), e);
            }
          }
          int numBindings = bindings.size();
          if (numBindings == 0) {
            // No plan options for this goal at this point in time, so move to the next agent
            logger.debug(Log.logPrefix(agent.getId()) + " has no applicable plans for goal "
                + gtype + " and will continue to wait indefinitely");
            continue;
          }
          // Call the meta-level planning prior to plan selection
          agent.notifyAgentPrePlanSelection(bindings);
          // Pick a plan option using specified policy
          Plan planInstance = bindings.get(GlobalConstant.PLAN_SELECTION_POLICY);
          // Now push the plan on to the intention stack
          synchronized (agentExecutionStack) {
            logger.debug(Log.logPrefix(agent.getId()) + " choose an instance of plan "
                + planInstance.getClass().getSimpleName() + " to handle goal "
                + gtype.getClass().getSimpleName());
            agentExecutionStack.push(planInstance);
          }
        }
      }

      if (!toRemove.isEmpty()) {
        for (int i : toRemove) {
          activeAgents.remove(i);
        }
        toRemove.clear();
      }

      if (idle) {
        synchronized (lock) {
          while (idle && !hasMessage) {
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
        if (shutdown) {
          break;
        }
      }
    } while (true);
    logger.debug("Pool {} is exiting", poolid);
  }

  /**
   * Flags to this intention selection thread that an external message, to an agent managed by this
   * thread, is waiting to be processed.
   */
  public void flagMessage() {
    synchronized (lock) {
      logger.debug("Pool {} received a new message", poolid);
      hasMessage = true;
      lock.notify();
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
      lock.notify();
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
