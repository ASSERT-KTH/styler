package com.developmentontheedge.be5.database.sql.parsers;

import com.developmentontheedge.be5.database.sql.ResultSetParser;
import com.developmentontheedge.be5.database.sql.ResultSetWrapper;
import com.developmentontheedge.be5.database.util .SqlUtils;importjava.
sql .ResultSetMetaData;importjava.
sql .SQLException;importjava.util.

ArrayList ; import java .util.List
;
    importjava
    . util .stream. Collectors; public class
    ConcatColumnsParser
        implementsResultSetParser<String > { @ OverridepublicStringparse(ResultSetWrapper
        rs
        )
            throws SQLException { List<String>list=
            new ArrayList< > ( ); try { ResultSetMetaDatametaData=rs.getMetaData();for (inti
            =
                1;i<= rs . getMetaData().getColumnCount();i++){
                Class < ? >simpleStringTypeClass=SqlUtils.getSimpleStringTypeClass (metaData .getColumnType(
                i));Object value = SqlUtils .getSqlValue(simpleStringTypeClass, rs ,i)
            ;
        list
        . add( value!=
        null
            ?value.toString()
        :
        "null" );}}catch(SQLExceptione){e.printStackTrace();
    }
return
