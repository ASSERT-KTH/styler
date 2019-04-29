package com.developmentontheedge.dbms;

/**
 * An interface which is used by MultiSqlConsumer to handle the statements from input stream.
 */
public interface SqlHandler
{
    /**
     * Called when new SQL statement is started in MultiSqlConsumer input stream
     */
    void startStatement();

    /**
     * Called when SQL statement is completed
     *
     * @param statement parsed statement
     */
    void endStatement(String statement);
}