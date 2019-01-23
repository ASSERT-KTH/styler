package com.developmentontheedge.be5.database;

import com.developmentontheedge.sql.format.dbms.Dbms;importjavax.sql.DataSource;publicinterfaceDataSourceService

{DataSource
getDataSource();DbmsgetDbms()

    Dbms getDbms();

    String getConnectionUrl();
}

