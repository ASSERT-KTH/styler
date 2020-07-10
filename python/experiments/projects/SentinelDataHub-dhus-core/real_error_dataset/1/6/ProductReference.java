/*
 * Data Hub Service (DHuS) - For Space data distribution.
 * Copyright (C) 2016,2017 GAEL Systems
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
package org.dhus.store.datastore;

import java.util.Objects;

import org.dhus.AbstractProduct;

/**
 * An instance of product knowing just its resource location.
 */
public class ProductReference extends AbstractProduct implements DataStoreProduct
{
   /** Resource location referencing a product. */
   private final String productResourceLocation;

   /**
    * Creates an instance of product with the given resource location.
    *
    * @param product_resource_location reference to a product
    */
   public ProductReference(String product_resource_location)
   {
      Objects.requireNonNull(product_resource_location);
      this.productResourceLocation = product_resource_location;
   }

   @Override
   public Long getContentLength()
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public String getResourceLocation()
   {
      return productResourceLocation;
   }

   @Override
   public boolean hasImpl(Class<?> cl)
   {
      return cl.isAssignableFrom(DataStoreProduct.class);
   }

   @Override
   public <T> T getImpl(Class<? extends T> cl)
   {
      if (hasImpl(cl))
      {
         return cl.cast(this);
      }
      return null;
   }
}
