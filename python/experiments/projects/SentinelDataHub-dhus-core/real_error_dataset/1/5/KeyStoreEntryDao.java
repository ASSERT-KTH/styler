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
package fr.gael.dhus.database.dao;

import fr.gael.dhus.database.dao.interfaces.HibernateDao;
import fr.gael.dhus.database.object.KeyStoreEntry;
import fr.gael.dhus.database.object.KeyStoreEntry.Key;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;

@Repository
public class KeyStoreEntryDao extends HibernateDao<KeyStoreEntry, Key>
{
   /**
    * Retrieves a key from a KeyStore by a substring of its associated value.
    * @param sub_value substring of associate value
    * @return the key of searched value
    */
   public String getKeyBySubValue(final String sub_value)
   {
      return getHibernateTemplate().execute(new HibernateCallback<String>()
      {
         @Override
         public String doInHibernate(Session session) throws HibernateException, SQLException
         {
            Criteria criteria = session.createCriteria(entityClass);
            criteria.add(Restrictions.like("value", "%" + sub_value + "%"));
            List results = criteria.list();
            if (results.isEmpty())
            {
               return null;
            }
            return ((KeyStoreEntry) results.get(0)).getEntryKey();
         }
      });
   }
}