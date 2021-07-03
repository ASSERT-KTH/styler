/*
 * Data Hub Service (DHuS) - For Space data distribution.
 * Copyright (C) 2017 GAEL Systems
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
package org.dhus.store.datastore.async;

import org.dhus.AbstractProduct;
import org.dhus.store.datastore.DataStoreException;
import org.dhus.store.datastore.DataStoreProduct;

/**
 * A product whose data may not be accessible at that moment.
 */
public class AsyncProduct extends AbstractProduct implements DataStoreProduct
{
   /** Back link to store for {@link #asyncFetchData()}. */
   private final AsyncDataSource source;

   /**
    * Data or not data ... that is the question!
    * @param source Source store
    */
   public AsyncProduct(AsyncDataSource source)
   {
      this.source = source;
   }

   /**
    * Please fetch that product for me.
    * @throws DataStoreException could not perform fetch
    * @see AsyncDataSource#fetch(AsyncProduct)
    */
   public void asyncFetchData() throws DataStoreException
   {
      this.source.fetch(this);
   }

   @Override
   protected Class<?>[] implsTypes()
   {
      Class<?>[] poo = new Class<?>[1];
      poo[0] = DataStoreProduct.class;
      return poo;
   }

   @Override
   public <T> T getImpl(Class<? extends T> cl)
   {
      if (DataStoreProduct.class.isAssignableFrom(cl))
      {
         return (T)this;
      }
      throw new UnsupportedOperationException("Unsupported impl " + cl.getName());
   }

   @Override
   public Long getContentLength()
   {
      return 0L;
   }

   @Override
   public String getResourceLocation()
   {
      return null;
   }

}
