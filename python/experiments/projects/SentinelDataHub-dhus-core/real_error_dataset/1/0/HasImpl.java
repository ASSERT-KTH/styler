/*
 * Data Hub Service (DHuS) - For Space data distribution.
 * Copyright (C) 2016 GAEL Systems
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
package org.dhus.store;

public interface HasImpl
{
   /**
    * Is this class able to provide the implementation.
    * Checks if this is able to provide a class-compatible implementation of the passed class.
    *
    * @param cl class of implementation to be supported
    * @return true if supported, false otherwise
    */
   public boolean hasImpl(Class<?> cl);

   /**
    * Returns an instance of the class if supported.
    *
    * @param <T> the type of class
    * @param cl the class instance
    * @return the instance of asked class
    */
   public <T> T getImpl(Class<? extends T> cl);
}
