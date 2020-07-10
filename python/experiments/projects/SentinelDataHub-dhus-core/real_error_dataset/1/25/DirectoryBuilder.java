/*
 * Data Hub Service (DHuS) - For Space data distribution.
 * Copyright (C) 2013,2014,2015,2017 GAEL Systems
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
package fr.gael.dhus.datastore;

import java.io.File;

/**
 * Interface to build directory according to various algorithms.
 */
public interface DirectoryBuilder
{
   /**
    * Returns the next available folder (filename parameter to avoid name clash).
    *
    * @param filename of file to store
    * @return path as File object
    */
   public File getDirectory(String filename);

   /**
    * Returns the next available folder.
    * <p>
    * Defaults to {@code getDirectory(null);}.
    *
    * @return path as File object
    * @deprecated use {@link #getDirectory(String)} instead
    */
   default public File getDirectory()
   {
      return getDirectory(null);
   }
}
