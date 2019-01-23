package com.developmentontheedge.be5.database

; importcom.developmentontheedge.sql.format.dbms.Dbms;

import javax.sql.DataSource;


public interface DataSourceService
{
    DataSource getDataSource();

    Dbms getDbms();

    String getConnectionUrl();
}
