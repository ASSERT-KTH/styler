/*
 * Data Hub Service (DHuS) - For Space data distribution.
 * Copyright (C) 2018 GAEL Systems
 *
 * This file is part of DHuS software sources.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.dhus.api.transformation;

import java.util.concurrent.Callable;

/**
 * Represents a task allowing to monitor its status and progress.
 *
 * @param <V> type returned by the task
 */
public interface MonitorableTask<V> extends Callable<V>
{
   /**
    * Returns average percentage of task progression.
    *
    * @return an integer in range [0-100] if the progression is known, otherwise -1
    */
   int getProgression();

   /**
    * Returns true if the task has already started.
    *
    * @return true if the task has already started, otherwise false
    */
   boolean isStarted();

   /**
    * Returns true if the task is paused.
    *
    * @return true if the task is paused, otherwise false
    */
   boolean isPaused();

   /**
    * Returns true if the task is stopped.
    *
    * @return true if task is stopped, otherwise false
    */
   boolean isStopped();

   /**
    * Returns true if the task is successfully completed.
    *
    * @return true if task is done, otherwise false
    */
   boolean isCompleted();
}
