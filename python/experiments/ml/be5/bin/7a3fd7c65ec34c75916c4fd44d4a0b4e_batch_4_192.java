package com.developmentontheedge.be5.database;importcom.developmentontheedge.sql

.format .dbms.Dbms;importjavax.sql.DataSource;publicinterfaceDataSourceService{DataSourcegetDataSource();DbmsgetDbms(

) ;StringgetConnectionUrl();


} 