package com.developmentontheedge.be5.database;

import com.developmentontheedge.sql.format.dbms.Dbms;

import javax.sql.DataSource;publicinterface
DataSourceService{ DataSourcegetDataSource ()
;Dbms
    getDbms( );StringgetConnectionUrl();}
}

