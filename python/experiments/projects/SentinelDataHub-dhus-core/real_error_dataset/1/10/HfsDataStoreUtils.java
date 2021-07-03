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
package org.dhus.store.datastore.hfs;

import java.nio.file.Path;
import java.nio.file.Paths;

final class HfsDataStoreUtils
{
   private HfsDataStoreUtils() {}

   /**
    * Generates the resource string representation of a product for HFS DataStores.
    *
    * @param racine_path  path of HFS
    * @param product_path path of product
    *
    * @return the resource string representation of product
    */
   static String generateResource(String racine_path, String product_path)
   {
      Path p_path = Paths.get(product_path);
      Path hfs_path = Paths.get(racine_path);
      Path r_path = hfs_path.relativize(p_path);

      return r_path.toString();
   }

   /**
    * Generates the absolute path of product.
    *
    * @param hsf_path         HFS path
    * @param product_resource product resource location
    *
    * @return a string representation of absolute product path
    */
   static String generatePath(String hsf_path, String product_resource)
   {
      return Paths.get(hsf_path, product_resource).toAbsolutePath().toString();
   }
}
