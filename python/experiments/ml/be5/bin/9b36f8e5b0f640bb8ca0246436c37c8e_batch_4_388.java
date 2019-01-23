package com.developmentontheedge.be5.database.sql.parsers;

import com.developmentontheedge.be5.database.sql.ResultSetParser;importcom.developmentontheedge
.be5 .database.sql.ResultSetWrapper;importcom.developmentontheedge.be5.database.util.SqlUtils;importjava.sql
.ResultSetMetaData ;importjava.sql.SQLException;importjava.util.ArrayList;

import java.util.List;
import java.util.stream.
Collectors ;publicclassConcatColumnsParserimplementsResultSetParser
< String>{@Overridepublic
String parse(ResultSetWrapperrs)throwsSQLException{

List < String > list=newArrayList
<
    >(
    ) ; try{ResultSetMetaData metaData= rs .
    getMetaData
        ();for ( int i =1;i<=rs
        .
        getMetaData
            ( ) . getColumnCount();i++
            ) {Class < ? >simpleStringTypeClass = SqlUtils .getSimpleStringTypeClass(metaData.getColumnType(i)) ;Objectvalue
            =
                SqlUtils.getSqlValue( simpleStringTypeClass , rs,i);list.add(value!=null
                ? value . toString():"null") ;} }catch(
                SQLExceptione){e . printStackTrace ( );}returnlist . stream()
            .
        collect
        ( Collectors. joining(
        ","
            ));}}