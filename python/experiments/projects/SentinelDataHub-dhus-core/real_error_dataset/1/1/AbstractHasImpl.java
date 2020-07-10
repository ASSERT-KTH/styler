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

public abstract class AbstractHasImpl implements HasImpl
{
   /**
    * Implementer shall return a list of the classes he supports.
    * This method is automatically called by {@link #hasImpl(Class)}.
    * It the implemented requires a different behavior, this latter method shall be overwritten.
    *
    * @return the list of supported classes by {@link HasImpl#hasImpl(Class)}
    */
   protected Class<?>[] implsTypes()
   {
      throw new UnsupportedOperationException("Not implemented");
   }

   @Override
   public boolean hasImpl(Class<?> cl)
   {
      for (Class<?> impl: implsTypes())
      {
         if (cl.isAssignableFrom(impl))
         {
            return true;
         }
      }
      return false;
   }
}
