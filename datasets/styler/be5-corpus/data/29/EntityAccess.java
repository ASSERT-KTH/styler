package com.developmentontheedge.be5.databasemodel;


/**
 * Interface to access object-wrapped representation of the entity.
 *
 * @author ruslan
 */
public interface EntityAccess
{

    /**
     * @param entityName entity name
     * @return EntityModel
     */
    <T> EntityModel<T> getEntity(String entityName);

//    /**
//     * Returns database connector
//     * @return database connector
//     */
//    DatabaseConnector getConnector();
//
//    /**
//     * Returns database analyzer that corresponds to the connector.
//     * @return database analyzer
//     */
//    DatabaseAnalyzer getAnalyzer();

    //DatabaseService getDatabaseService();

//    EntityAccess<E> getCache();
//
//    String getTcloneId();

//    EntityAccess<E> getCloned(String tcloneId);
}
