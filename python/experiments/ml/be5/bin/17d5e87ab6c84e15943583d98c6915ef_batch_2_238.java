package com.developmentontheedge.be5.database;

import com.developmentontheedge .sql.format.dbms.Dbms;importjavax.sql.DataSource;publicinterfaceDataSourceService{

DataSourcegetDataSource ()
    DataSource getDataSource();

    Dbms getDbms();

    String getConnectionUrl();
}

