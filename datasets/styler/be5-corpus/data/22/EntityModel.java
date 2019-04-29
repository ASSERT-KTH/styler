package com.developmentontheedge.be5.databasemodel;

import com.developmentontheedge.be5.metadata.model.Entity;
import com.developmentontheedge.beans.DynamicPropertySet;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * All methods, which not generate RecordModel, not use caches!<br>
 * All methods throws EntityModelException if the method have internal error
 * EntityModel are generally not synchronized. It is recommended to create separate
 * instances for each thread. If multiple threads access a format concurrently,
 * it must be synchronized externally.
 *
 * @author ruslan
 */
public interface EntityModel<T>
{
    /**
     * Returns the number of records a table.
     * This method never use cache.
     *
     * @return number of records
     */
    long count();

    /**
     * Returns the number of records a table.
     * This method never use cache.
     *
     * @return number of records
     */
    long count(Map<String, ?> values);

    /**
     * Returns <tt>true</tt> if this table contains no records.
     * This method never use caches.
     *
     * @return <tt>true</tt> if this table contains no records
     */
    boolean isEmpty();

    /**
     * Returns <tt>true</tt> if entity contains record consistent with the
     * specified condition.
     * This method never use caches.
     *
     * @param conditions condition values
     * @return <tt>true</tt> if entity contains record consistent with
     * conditions, otherwise false
     */
    boolean contains(Map<String, ?> conditions);

    /**
     * Adds record into database from map, where key is the column name
     * and key value is the column value.<br>
     * The method can check the values on consistency and threw exceptions<br>
     * in order to avoid compromising the integrity of the database.
     * This method calls {@link #add(DynamicPropertySet)}
     *
     * @param values map with column names and values
     * @return generated record identify number
     */
    <R> R add(Map<String, ?> values);

    /**
     * Adds record into database from map, where key is the column name
     * and key value is the column value.<br>
     * This method may not contain any checks, it's just the method implementation.
     *
     * @param dps DynamicPropertySet
     * @return generated record identify number
     */
    <R> R add(DynamicPropertySet dps);

    /**
     * Returns <tt>true</tt> if entity contains record consistent with the
     * all specified condition in collection otherwise <tt>false</tt>
     *
     * @param c collection of conditions
     * @return <tt>true</tt> if entity contains record consistent with the
     * all specified condition
     */
    boolean containsAll(Collection<Map<String, ?>> c);

    /**
     * Adds all records from collection into database.
     *
     * @param c collection with column names and values
     * @return list with record identify numbers
     */
    <R> List<R> addAll(Collection<Map<String, ?>> c);

    /**
     * Returns the record object with the specified id
     *
     * @param id value of primary key
     * @return the record object with the specified id otherwise null
     */
    RecordModel<T> get(T id);

    /**
     * Returns the record object consistent with the specified condition,
     * where key is the column name with the value equals map key value
     *
     * @param conditions condition values
     * @return the record object with the specified id otherwise null
     */
    RecordModel<T> getBy(Map<String, ?> conditions);

    RecordModel<T> getColumns(List<String> columns, T id);

    RecordModel<T> getColumnsBy(List<String> columns, Map<String, ?> conditions);

    /**
     * Returns a list of records of current entity.
     *
     * @return list of records
     */
    List<RecordModel<T>> toList();

//    List<RecordModel> collect();

    /**
     * Returns a array of records of current entity.
     *
     * @return array of records
     */
    RecordModel<T>[] toArray();

    /**
     * Returns a list of records of current entity filtered by the specified parameters.
     *
     * @param conditions the filter parameters
     * @return array of records
     */
    List<RecordModel<T>> toList(Map<String, ?> conditions);

    /**
     * Returns a array of records of current entity filtered by the specified parameters.
     *
     * @param conditions the filter parameters
     * @return array of records
     */
    RecordModel<T>[] toArray(Map<String, ?> conditions);

    /**
     * Sets value to property with a specified name.<br>
     * The method can check the values on consistency and threw exceptions<br>
     * in order to avoid compromising the integrity of the database.
     * This method calls {@link #set(T, Map)}
     *
     * @param id           identify number of record
     * @param propertyName column name
     * @param value        new value
     * @return number of affected rows
     */
    int set(T id, String propertyName, Object value);

    /**
     * Sets value to property with a specified name.<br>
     * The method can check the values on consistency and threw exceptions<br>
     * in order to avoid compromising the integrity of the database.
     * This method calls {@link #set(T, DynamicPropertySet)}
     *
     * @param id     identify number of record
     * @param values column names and values
     * @return number of affected rows
     */
    int set(T id, Map<String, ?> values);

//    void setMany( Map<String, ?> values, Map<String, ?> conditions);

    /**
     * Sets value to property with a specified name.<br>
     * This method may not contain any checks, it's just the method implementation.
     *
     * @param id     identify number of record
     * @param values new column names and values
     * @return number of affected rows
     */
    int set(T id, DynamicPropertySet values);

    //void setForceMany(String propertyName, String value, Map<String, String> conditions);

//    void setForceMany(Map<String, String> values, Map<String, String> conditions);

//    /**
//     * Operation removes all the records consistent with any of conditions in collection.
//     * The method can check the values on consistency and threw exceptions<br>
//     * in order to avoid compromising the integrity of the database.
//     * @param c collection of conditions
//     * @return number of affected rows
//     */
//    int removeAll(Collection<Map<String, ?>> c);

    int removeWhereColumnIn(String columnName, T[] ids);

    /**
     * Operation removes all the records
     *
     * @return number of affected rows
     */
    int removeAll();

    /**
     * Operation removes all the records, consistent with conditions.
     * The method can check the values on consistency and threw exceptions<br>
     * in order to avoid compromising the integrity of the database.
     *
     * @param conditions conditions
     * @return number of affected rows
     */
    int removeBy(Map<String, ?> conditions);

    /**
     * Deletes the record with the specified identifiers.
     * The method can check the values on consistency and threw exceptions<br>
     * in order to avoid compromising the integrity of the database.
     * This method calls {@link #remove(T[])}
     *
     * @param id first identify number of record
     * @return number of affected rows
     */
    int remove(T id);

    /**
     * Deletes the record with the specified identifiers.<br>
     * This method may not contain any checks, it's just the method implementation.
     *
     * @param ids numbers of record
     * @return number of affected rows
     */
    int remove(T[] ids);

//    /**
//     * Spreads collection and collect elements from function to list.<br>
//     * For example:<br>
//     * <code>List<DynamicPropertySet> list =
//     *      entity.<DynamicPropertySet>collect( ( bean, row ) -> row % 2 == 0 ? bean : null, Collections.<String, Object>.emptyMap() );
//     * </code>
//     * @param conditions condition values
//     * @param lambda handler
//     * @return list with the function results
//     */
//  <T> List<T> collect(Map<String, ?> conditions, BiFunction<R, Integer, T> lambda);

    /**
     * Returns entity name.
     *
     * @return entity name
     */
    String getEntityName();

    /**
     * Returns primary key of entity table.
     *
     * @return primary key
     */
    String getPrimaryKeyName();

    Entity getEntity();
}
