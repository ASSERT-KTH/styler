package com.developmentontheedge.be5.database.sql.parsers;

import com.developmentontheedge.be5.database.sql.ResultSetParser;importcom.developmentontheedge
. be5.database.sql.ResultSetWrapper;importcom.developmentontheedge

. be5.database.util.
SqlUtils ;importjava.sql.
ResultSetMetaData ;importjava.sql.
SQLException ;importjava.util.
ArrayList ;importjava.util.List;

import java . util .stream.Collectors
;
    publicclass
    ConcatColumnsParser implements ResultSetParser<String >{ @ Override
    public
        Stringparse(ResultSetWrapper rs ) throws SQLException{List<String>
        list
        =
            new ArrayList < >();try{
            ResultSetMetaData metaData= rs . getMetaData( ) ; for(inti=1;i<=rs .getMetaData(
            )
                .getColumnCount() ; i ++){Class<?>simpleStringTypeClass=SqlUtils.getSimpleStringTypeClass
                ( metaData . getColumnType(i)); Objectvalue =SqlUtils.
                getSqlValue(simpleStringTypeClass,rs , i ) ;list.add( value !=null?
            value
        .
        toString () :"null"
        )
            ;}}catch(SQLException
        e
        ) {e.printStackTrace();}returnlist.stream().collect
    (
Collectors
