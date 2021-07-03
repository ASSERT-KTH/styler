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
package org.dhus.olingo.v2.entity;

import org.apache.olingo.commons.api.data.Entity;

/**
 * Translates from type I to an OData entity.
 *
 * @param <I> the type of the input to the method
 */
@FunctionalInterface
public interface EntityProducer<I>
{
   /**
    * To implement.
    *
    * @param i the method argument
    * @return an Entity or {@code null}
    */
   public Entity transform(I i);

   /**
    * Returns {@code null} if {@code i == null}, then calls {@link #transform(Object)}.
    *
    * @param i the method argument
    * @return an Entity or {@code null}
    */
   default public Entity toOlingoEntity(I i)
   {
      if (i == null)
      {
         return null;
      }
      return transform(i);
   }
}
