package com.developmentontheedge.be5.database.sql;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;import
java. sql.SQLException;importjava.sql.SQLType;import
java. sql.SQLWarning;importjava.sql.SQLXML
;import java.sql.Statement;importjava.sql.
Time ;importjava.sql.
Timestamp ;importjava.util.
Calendar ;importjava.util.
Map ;publicclassResultSetWrapperimplementsResultSet
{ privateResultSet_res;ResultSetWrapper(
ResultSet resultSet){this._res

= resultSet ; } void
setResultSet
    ( ResultSet _res)

    {this. _res=
    _res
        ;}@ Override publicboolean
    next

    ( )throwsSQLException {throw
    new
        UnsupportedOperationException("Method next() run automatically. Do not call this." ) ;}
    @

    Overridepublic
    void close ()throws SQLException {
    _res
        . close ();}@
    Override

    publicboolean
    wasNull ( )throwsSQLException { return
    _res
        .wasNull();}
    @

    Overridepublic
    String getString (intcolumnIndex ) throws
    SQLException
        { return_res.getString(columnIndex
    )

    ;}
    @ Override publicbooleangetBoolean (int columnIndex )
    throws
        SQLException {return_res.getBoolean(columnIndex
    )

    ;}
    @ Override publicbytegetByte (int columnIndex )
    throws
        SQLException {return_res.getByte(columnIndex
    )

    ;}
    @ Override publicshortgetShort (int columnIndex )
    throws
        SQLException {return_res.getShort(columnIndex
    )

    ;}
    @ Override publicintgetInt (int columnIndex )
    throws
        SQLException {return_res.getInt(columnIndex
    )

    ;}
    @ Override publiclonggetLong (int columnIndex )
    throws
        SQLException {return_res.getLong(columnIndex
    )

    ;}
    @ Override publicfloatgetFloat (int columnIndex )
    throws
        SQLException {return_res.getFloat(columnIndex
    )

    ;}
    @ Override publicdoublegetDouble (int columnIndex )
    throws
        SQLException {return_res.getDouble(columnIndex
    )

    ;}
    @ Override @Deprecatedpublic BigDecimalgetBigDecimal ( int
    columnIndex
        , intscale)throwsSQLException{return
    _res

    .getBigDecimal
    (columnIndex
    , scale );} @Override public byte[ ] getBytes
    (
        int columnIndex)throwsSQLException{return _res.getBytes
    (

    columnIndex)
    ; }@Override publicDategetDate (int columnIndex )
    throws
        SQLException {return_res.getDate(columnIndex
    )

    ;}
    @ Override publicTimegetTime (int columnIndex )
    throws
        SQLException {return_res.getTime(columnIndex
    )

    ;}
    @ Override publicTimestampgetTimestamp (int columnIndex )
    throws
        SQLException {return_res.getTimestamp(columnIndex
    )

    ;}
    @ Override publicInputStreamgetAsciiStream (int columnIndex )
    throws
        SQLException {return_res.getAsciiStream(columnIndex
    )

    ;}
    @ Override @Deprecatedpublic InputStreamgetUnicodeStream ( int
    columnIndex
        ) throwsSQLException{return_res.getUnicodeStream
    (

    columnIndex)
    ;}
    @ Override publicInputStreamgetBinaryStream (int columnIndex )
    throws
        SQLException {return_res.getBinaryStream(columnIndex
    )

    ;}
    @ Override publicStringgetString (String columnLabel )
    throws
        SQLException {return_res.getString(columnLabel
    )

    ;}
    @ Override publicbooleangetBoolean (String columnLabel )
    throws
        SQLException {return_res.getBoolean(columnLabel
    )

    ;}
    @ Override publicbytegetByte (String columnLabel )
    throws
        SQLException {return_res.getByte(columnLabel
    )

    ;}
    @ Override publicshortgetShort (String columnLabel )
    throws
        SQLException {return_res.getShort(columnLabel
    )

    ;}
    @ Override publicintgetInt (String columnLabel )
    throws
        SQLException {return_res.getInt(columnLabel
    )

    ;}
    @ Override publiclonggetLong (String columnLabel )
    throws
        SQLException {return_res.getLong(columnLabel
    )

    ;}
    @ Override publicfloatgetFloat (String columnLabel )
    throws
        SQLException {return_res.getFloat(columnLabel
    )

    ;}
    @ Override publicdoublegetDouble (String columnLabel )
    throws
        SQLException {return_res.getDouble(columnLabel
    )

    ;}
    @ Override @Deprecatedpublic BigDecimalgetBigDecimal ( String
    columnLabel
        , intscale)throwsSQLException{return
    _res

    .getBigDecimal
    (columnLabel
    , scale );} @Override public byte[ ] getBytes
    (
        String columnLabel)throwsSQLException{return _res.getBytes
    (

    columnLabel)
    ; }@Override publicDategetDate (String columnLabel )
    throws
        SQLException {return_res.getDate(columnLabel
    )

    ;}
    @ Override publicTimegetTime (String columnLabel )
    throws
        SQLException {return_res.getTime(columnLabel
    )

    ;}
    @ Override publicTimestampgetTimestamp (String columnLabel )
    throws
        SQLException {return_res.getTimestamp(columnLabel
    )

    ;}
    @ Override publicInputStreamgetAsciiStream (String columnLabel )
    throws
        SQLException {return_res.getAsciiStream(columnLabel
    )

    ;}
    @ Override @Deprecatedpublic InputStreamgetUnicodeStream ( String
    columnLabel
        ) throwsSQLException{return_res.getUnicodeStream
    (

    columnLabel)
    ;}
    @ Override publicInputStreamgetBinaryStream (String columnLabel )
    throws
        SQLException {return_res.getBinaryStream(columnLabel
    )

    ;}
    @ Override publicSQLWarninggetWarnings () throws SQLException
    {
        return _res.getWarnings();}
    @

    Overridepublic
    void clearWarnings ()throws SQLException {
    _res
        . clearWarnings();}@
    Override

    publicString
    getCursorName ( )throwsSQLException { return
    _res
        .getCursorName();}
    @

    Overridepublic
    ResultSetMetaData getMetaData ()throws SQLException {
    return
        _res .getMetaData();}
    @

    Overridepublic
    Object getObject (intcolumnIndex ) throws
    SQLException
        { return_res.getObject(columnIndex
    )

    ;}
    @ Override publicObjectgetObject (String columnLabel )
    throws
        SQLException {return_res.getObject(columnLabel
    )

    ;}
    @ Override publicintfindColumn (String columnLabel )
    throws
        SQLException {return_res.findColumn(columnLabel
    )

    ;}
    @ Override publicReadergetCharacterStream (int columnIndex )
    throws
        SQLException {return_res.getCharacterStream(columnIndex
    )

    ;}
    @ Override publicReadergetCharacterStream (String columnLabel )
    throws
        SQLException {return_res.getCharacterStream(columnLabel
    )

    ;}
    @ Override publicBigDecimalgetBigDecimal (int columnIndex )
    throws
        SQLException {return_res.getBigDecimal(columnIndex
    )

    ;}
    @ Override publicBigDecimalgetBigDecimal (String columnLabel )
    throws
        SQLException {return_res.getBigDecimal(columnLabel
    )

    ;}
    @ Override publicbooleanisBeforeFirst () throws SQLException
    {
        return _res.isBeforeFirst();}
    @

    Overridepublic
    boolean isAfterLast ()throws SQLException {
    return
        _res .isAfterLast();}
    @

    Overridepublic
    boolean isFirst ()throws SQLException {
    return
        _res .isFirst();}
    @

    Overridepublic
    boolean isLast ()throws SQLException {
    return
        _res .isLast();}
    @

    Overridepublic
    void beforeFirst ()throws SQLException {
    _res
        . beforeFirst();}@
    Override

    publicvoid
    afterLast ( )throwsSQLException { _res
    .
        afterLast();}@
    Override

    publicboolean
    first ( )throwsSQLException { return
    _res
        .first();}
    @

    Overridepublic
    boolean last ()throws SQLException {
    return
        _res .last();}
    @

    Overridepublic
    int getRow ()throws SQLException {
    return
        _res .getRow();}
    @

    Overridepublic
    boolean absolute (introw ) throws
    SQLException
        { return_res.absolute(row
    )

    ;}
    @ Override publicbooleanrelative (int rows )
    throws
        SQLException {return_res.relative(rows
    )

    ;}
    @ Override publicbooleanprevious () throws SQLException
    {
        return _res.previous();}
    @

    Overridepublic
    void setFetchDirection (intdirection ) throws
    SQLException
        { _res.setFetchDirection(direction)
    ;

    }@
    Override public intgetFetchDirection( )throws SQLException {
    return
        _res.getFetchDirection();}
    @

    Overridepublic
    void setFetchSize (introws ) throws
    SQLException
        { _res.setFetchSize(rows)
    ;

    }@
    Override public intgetFetchSize( )throws SQLException {
    return
        _res.getFetchSize();}
    @

    Overridepublic
    int getType ()throws SQLException {
    return
        _res .getType();}
    @

    Overridepublic
    int getConcurrency ()throws SQLException {
    return
        _res .getConcurrency();}
    @

    Overridepublic
    boolean rowUpdated ()throws SQLException {
    return
        _res .rowUpdated();}
    @

    Overridepublic
    boolean rowInserted ()throws SQLException {
    return
        _res .rowInserted();}
    @

    Overridepublic
    boolean rowDeleted ()throws SQLException {
    return
        _res .rowDeleted();}
    @

    Overridepublic
    void updateNull (intcolumnIndex ) throws
    SQLException
        { _res.updateNull(columnIndex)
    ;

    }@
    Override public voidupdateBoolean( intcolumnIndex , boolean
    x
        )throwsSQLException{_res.updateBoolean
    (

    columnIndex,
    x ) ;}@ Overridepublic void updateByte( int columnIndex
    ,
        bytex)throwsSQLException{ _res.updateByte
    (

    columnIndex,
    x ) ;}@ Overridepublic void updateShort( int columnIndex
    ,
        shortx)throwsSQLException{ _res.updateShort
    (

    columnIndex,
    x ) ;}@ Overridepublic void updateInt( int columnIndex
    ,
        intx)throwsSQLException{ _res.updateInt
    (

    columnIndex,
    x ) ;}@ Overridepublic void updateLong( int columnIndex
    ,
        longx)throwsSQLException{ _res.updateLong
    (

    columnIndex,
    x ) ;}@ Overridepublic void updateFloat( int columnIndex
    ,
        floatx)throwsSQLException{ _res.updateFloat
    (

    columnIndex,
    x ) ;}@ Overridepublic void updateDouble( int columnIndex
    ,
        doublex)throwsSQLException{ _res.updateDouble
    (

    columnIndex,
    x ) ;}@ Overridepublic void updateBigDecimal( int columnIndex
    ,
        BigDecimalx)throwsSQLException{ _res.updateBigDecimal
    (

    columnIndex,
    x ) ;}@ Overridepublic void updateString( int columnIndex
    ,
        Stringx)throwsSQLException{ _res.updateString
    (

    columnIndex,
    x ) ;}@ Overridepublic void updateBytes( int columnIndex
    ,
        byte[]x)throws SQLException{_res
    .

    updateBytes(
    columnIndex , x); }@ Overridepublicvoid updateDate( int columnIndex
    ,
        Datex)throwsSQLException{ _res.updateDate
    (

    columnIndex,
    x ) ;}@ Overridepublic void updateTime( int columnIndex
    ,
        Timex)throwsSQLException{ _res.updateTime
    (

    columnIndex,
    x ) ;}@ Overridepublic void updateTimestamp( int columnIndex
    ,
        Timestampx)throwsSQLException{ _res.updateTimestamp
    (

    columnIndex,
    x ) ;}@ Overridepublic void updateAsciiStream( int columnIndex
    ,
        InputStreamx,intlength) throwsSQLException{
    _res

    .updateAsciiStream
    ( columnIndex ,x, length) ; }@ Override publicvoid updateBinaryStream (
    int
        columnIndex,InputStreamx,int length) throwsSQLException{
    _res

    .updateBinaryStream
    ( columnIndex ,x, length) ; }@ Override publicvoid updateCharacterStream (
    int
        columnIndex,Readerx,int length) throwsSQLException{
    _res

    .updateCharacterStream
    ( columnIndex ,x, length) ; }@ Override publicvoid updateObject (
    int
        columnIndex,Objectx,int scaleOrLength) throwsSQLException{
    _res

    .updateObject
    ( columnIndex ,x, scaleOrLength) ; }@ Override publicvoid updateObject (
    int
        columnIndex,Objectx)throws SQLException{ _res.updateObject
    (

    columnIndex,
    x ) ;}@ Overridepublic void updateNull( String columnLabel
    )
        throwsSQLException{_res.updateNull (columnLabel)
    ;

    }@
    Override public voidupdateBoolean( StringcolumnLabel , boolean
    x
        )throwsSQLException{_res.updateBoolean
    (

    columnLabel,
    x ) ;}@ Overridepublic void updateByte( String columnLabel
    ,
        bytex)throwsSQLException{ _res.updateByte
    (

    columnLabel,
    x ) ;}@ Overridepublic void updateShort( String columnLabel
    ,
        shortx)throwsSQLException{ _res.updateShort
    (

    columnLabel,
    x ) ;}@ Overridepublic void updateInt( String columnLabel
    ,
        intx)throwsSQLException{ _res.updateInt
    (

    columnLabel,
    x ) ;}@ Overridepublic void updateLong( String columnLabel
    ,
        longx)throwsSQLException{ _res.updateLong
    (

    columnLabel,
    x ) ;}@ Overridepublic void updateFloat( String columnLabel
    ,
        floatx)throwsSQLException{ _res.updateFloat
    (

    columnLabel,
    x ) ;}@ Overridepublic void updateDouble( String columnLabel
    ,
        doublex)throwsSQLException{ _res.updateDouble
    (

    columnLabel,
    x ) ;}@ Overridepublic void updateBigDecimal( String columnLabel
    ,
        BigDecimalx)throwsSQLException{ _res.updateBigDecimal
    (

    columnLabel,
    x ) ;}@ Overridepublic void updateString( String columnLabel
    ,
        Stringx)throwsSQLException{ _res.updateString
    (

    columnLabel,
    x ) ;}@ Overridepublic void updateBytes( String columnLabel
    ,
        byte[]x)throws SQLException{_res
    .

    updateBytes(
    columnLabel , x); }@ Overridepublicvoid updateDate( String columnLabel
    ,
        Datex)throwsSQLException{ _res.updateDate
    (

    columnLabel,
    x ) ;}@ Overridepublic void updateTime( String columnLabel
    ,
        Timex)throwsSQLException{ _res.updateTime
    (

    columnLabel,
    x ) ;}@ Overridepublic void updateTimestamp( String columnLabel
    ,
        Timestampx)throwsSQLException{ _res.updateTimestamp
    (

    columnLabel,
    x ) ;}@ Overridepublic void updateAsciiStream( String columnLabel
    ,
        InputStreamx,intlength) throwsSQLException{
    _res

    .updateAsciiStream
    ( columnLabel ,x, length) ; }@ Override publicvoid updateBinaryStream (
    String
        columnLabel,InputStreamx,int length) throwsSQLException{
    _res

    .updateBinaryStream
    ( columnLabel ,x, length) ; }@ Override publicvoid updateCharacterStream (
    String
        columnLabel,Readerreader,int length) throwsSQLException{
    _res

    .updateCharacterStream
    ( columnLabel ,reader, length) ; }@ Override publicvoid updateObject (
    String
        columnLabel,Objectx,int scaleOrLength) throwsSQLException{
    _res

    .updateObject
    ( columnLabel ,x, scaleOrLength) ; }@ Override publicvoid updateObject (
    String
        columnLabel,Objectx)throws SQLException{ _res.updateObject
    (

    columnLabel,
    x ) ;}@ Overridepublic void insertRow( ) throws
    SQLException
        {_res.insertRow() ;}@
    Override

    publicvoid
    updateRow ( )throwsSQLException { _res
    .
        updateRow();}@
    Override

    publicvoid
    deleteRow ( )throwsSQLException { _res
    .
        deleteRow();}@
    Override

    publicvoid
    refreshRow ( )throwsSQLException { _res
    .
        refreshRow();}@
    Override

    publicvoid
    cancelRowUpdates ( )throwsSQLException { _res
    .
        cancelRowUpdates();}@
    Override

    publicvoid
    moveToInsertRow ( )throwsSQLException { _res
    .
        moveToInsertRow();}@
    Override

    publicvoid
    moveToCurrentRow ( )throwsSQLException { _res
    .
        moveToCurrentRow();}@
    Override

    publicStatement
    getStatement ( )throwsSQLException { return
    _res
        .getStatement();}
    @

    Overridepublic
    Object getObject (intcolumnIndex , Map
    <
        String ,Class<?>>
    map

    )throws
    SQLException { return_res. getObject( columnIndex,map) ;}@Overridepublic RefgetRef ( int
    columnIndex
        ) throwsSQLException{return_res. getRef(columnIndex
    )

    ;}
    @ Override publicBlobgetBlob (int columnIndex )
    throws
        SQLException {return_res.getBlob(columnIndex
    )

    ;}
    @ Override publicClobgetClob (int columnIndex )
    throws
        SQLException {return_res.getClob(columnIndex
    )

    ;}
    @ Override publicArraygetArray (int columnIndex )
    throws
        SQLException {return_res.getArray(columnIndex
    )

    ;}
    @ Override publicObjectgetObject (String columnLabel ,
    Map
        < String,Class<?>>
    map

    )throws
    SQLException { return_res. getObject( columnLabel,map) ;}@Overridepublic RefgetRef ( String
    columnLabel
        ) throwsSQLException{return_res. getRef(columnLabel
    )

    ;}
    @ Override publicBlobgetBlob (String columnLabel )
    throws
        SQLException {return_res.getBlob(columnLabel
    )

    ;}
    @ Override publicClobgetClob (String columnLabel )
    throws
        SQLException {return_res.getClob(columnLabel
    )

    ;}
    @ Override publicArraygetArray (String columnLabel )
    throws
        SQLException {return_res.getArray(columnLabel
    )

    ;}
    @ Override publicDategetDate (int columnIndex ,
    Calendar
        cal )throwsSQLException{return_res.
    getDate

    (columnIndex
    , cal );} @Override public DategetDate ( String
    columnLabel
        , Calendarcal)throwsSQLException{ return_res.
    getDate

    (columnLabel
    , cal );} @Override public TimegetTime ( int
    columnIndex
        , Calendarcal)throwsSQLException{ return_res.
    getTime

    (columnIndex
    , cal );} @Override public TimegetTime ( String
    columnLabel
        , Calendarcal)throwsSQLException{ return_res.
    getTime

    (columnLabel
    , cal );} @Override public TimestampgetTimestamp ( int
    columnIndex
        , Calendarcal)throwsSQLException{ return_res.
    getTimestamp

    (columnIndex
    , cal );} @Override public TimestampgetTimestamp ( String
    columnLabel
        , Calendarcal)throwsSQLException{ return_res.
    getTimestamp

    (columnLabel
    , cal );} @Override public URLgetURL ( int
    columnIndex
        ) throwsSQLException{return_res. getURL(columnIndex
    )

    ;}
    @ Override publicURLgetURL (String columnLabel )
    throws
        SQLException {return_res.getURL(columnLabel
    )

    ;}
    @ Override publicvoidupdateRef (int columnIndex ,
    Ref
        x )throwsSQLException{_res.updateRef
    (

    columnIndex,
    x ) ;}@ Overridepublic void updateRef( String columnLabel
    ,
        Refx)throwsSQLException{ _res.updateRef
    (

    columnLabel,
    x ) ;}@ Overridepublic void updateBlob( int columnIndex
    ,
        Blobx)throwsSQLException{ _res.updateBlob
    (

    columnIndex,
    x ) ;}@ Overridepublic void updateBlob( String columnLabel
    ,
        Blobx)throwsSQLException{ _res.updateBlob
    (

    columnLabel,
    x ) ;}@ Overridepublic void updateClob( int columnIndex
    ,
        Clobx)throwsSQLException{ _res.updateClob
    (

    columnIndex,
    x ) ;}@ Overridepublic void updateClob( String columnLabel
    ,
        Clobx)throwsSQLException{ _res.updateClob
    (

    columnLabel,
    x ) ;}@ Overridepublic void updateArray( int columnIndex
    ,
        Arrayx)throwsSQLException{ _res.updateArray
    (

    columnIndex,
    x ) ;}@ Overridepublic void updateArray( String columnLabel
    ,
        Arrayx)throwsSQLException{ _res.updateArray
    (

    columnLabel,
    x ) ;}@ Overridepublic RowId getRowId( int columnIndex
    )
        throwsSQLException{return_res. getRowId(columnIndex
    )

    ;}
    @ Override publicRowIdgetRowId (String columnLabel )
    throws
        SQLException {return_res.getRowId(columnLabel
    )

    ;}
    @ Override publicvoidupdateRowId (int columnIndex ,
    RowId
        x )throwsSQLException{_res.updateRowId
    (

    columnIndex,
    x ) ;}@ Overridepublic void updateRowId( String columnLabel
    ,
        RowIdx)throwsSQLException{ _res.updateRowId
    (

    columnLabel,
    x ) ;}@ Overridepublic int getHoldability( ) throws
    SQLException
        {return_res.getHoldability( );}
    @

    Overridepublic
    boolean isClosed ()throws SQLException {
    return
        _res .isClosed();}
    @

    Overridepublic
    void updateNString (intcolumnIndex , String
    nString
        ) throwsSQLException{_res.updateNString
    (

    columnIndex,
    nString ) ;}@ Overridepublic void updateNString( String columnLabel
    ,
        StringnString)throwsSQLException{ _res.updateNString
    (

    columnLabel,
    nString ) ;}@ Overridepublic void updateNClob( int columnIndex
    ,
        NClobnClob)throwsSQLException{ _res.updateNClob
    (

    columnIndex,
    nClob ) ;}@ Overridepublic void updateNClob( String columnLabel
    ,
        NClobnClob)throwsSQLException{ _res.updateNClob
    (

    columnLabel,
    nClob ) ;}@ Overridepublic NClob getNClob( int columnIndex
    )
        throwsSQLException{return_res. getNClob(columnIndex
    )

    ;}
    @ Override publicNClobgetNClob (String columnLabel )
    throws
        SQLException {return_res.getNClob(columnLabel
    )

    ;}
    @ Override publicSQLXMLgetSQLXML (int columnIndex )
    throws
        SQLException {return_res.getSQLXML(columnIndex
    )

    ;}
    @ Override publicSQLXMLgetSQLXML (String columnLabel )
    throws
        SQLException {return_res.getSQLXML(columnLabel
    )

    ;}
    @ Override publicvoidupdateSQLXML (int columnIndex ,
    SQLXML
        xmlObject )throwsSQLException{_res.updateSQLXML
    (

    columnIndex,
    xmlObject ) ;}@ Overridepublic void updateSQLXML( String columnLabel
    ,
        SQLXMLxmlObject)throwsSQLException{ _res.updateSQLXML
    (

    columnLabel,
    xmlObject ) ;}@ Overridepublic String getNString( int columnIndex
    )
        throwsSQLException{return_res. getNString(columnIndex
    )

    ;}
    @ Override publicStringgetNString (String columnLabel )
    throws
        SQLException {return_res.getNString(columnLabel
    )

    ;}
    @ Override publicReadergetNCharacterStream (int columnIndex )
    throws
        SQLException {return_res.getNCharacterStream(columnIndex
    )

    ;}
    @ Override publicReadergetNCharacterStream (String columnLabel )
    throws
        SQLException {return_res.getNCharacterStream(columnLabel
    )

    ;}
    @ Override publicvoidupdateNCharacterStream (int columnIndex ,
    Reader
        x ,longlength)throwsSQLException{
    _res

    .updateNCharacterStream
    ( columnIndex ,x, length) ; }@ Override publicvoid updateNCharacterStream (
    String
        columnLabel,Readerreader,long length) throwsSQLException{
    _res

    .updateNCharacterStream
    ( columnLabel ,reader, length) ; }@ Override publicvoid updateAsciiStream (
    int
        columnIndex,InputStreamx,long length) throwsSQLException{
    _res

    .updateAsciiStream
    ( columnIndex ,x, length) ; }@ Override publicvoid updateBinaryStream (
    int
        columnIndex,InputStreamx,long length) throwsSQLException{
    _res

    .updateBinaryStream
    ( columnIndex ,x, length) ; }@ Override publicvoid updateCharacterStream (
    int
        columnIndex,Readerx,long length) throwsSQLException{
    _res

    .updateCharacterStream
    ( columnIndex ,x, length) ; }@ Override publicvoid updateAsciiStream (
    String
        columnLabel,InputStreamx,long length) throwsSQLException{
    _res

    .updateAsciiStream
    ( columnLabel ,x, length) ; }@ Override publicvoid updateBinaryStream (
    String
        columnLabel,InputStreamx,long length) throwsSQLException{
    _res

    .updateBinaryStream
    ( columnLabel ,x, length) ; }@ Override publicvoid updateCharacterStream (
    String
        columnLabel,Readerreader,long length) throwsSQLException{
    _res

    .updateCharacterStream
    ( columnLabel ,reader, length) ; }@ Override publicvoid updateBlob (
    int
        columnIndex,InputStreaminputStream,long length) throwsSQLException{
    _res

    .updateBlob
    ( columnIndex ,inputStream, length) ; }@ Override publicvoid updateBlob (
    String
        columnLabel,InputStreaminputStream,long length) throwsSQLException{
    _res

    .updateBlob
    ( columnLabel ,inputStream, length) ; }@ Override publicvoid updateClob (
    int
        columnIndex,Readerreader,long length) throwsSQLException{
    _res

    .updateClob
    ( columnIndex ,reader, length) ; }@ Override publicvoid updateClob (
    String
        columnLabel,Readerreader,long length) throwsSQLException{
    _res

    .updateClob
    ( columnLabel ,reader, length) ; }@ Override publicvoid updateNClob (
    int
        columnIndex,Readerreader,long length) throwsSQLException{
    _res

    .updateNClob
    ( columnIndex ,reader, length) ; }@ Override publicvoid updateNClob (
    String
        columnLabel,Readerreader,long length) throwsSQLException{
    _res

    .updateNClob
    ( columnLabel ,reader, length) ; }@ Override publicvoid updateNCharacterStream (
    int
        columnIndex,Readerx)throws SQLException{ _res.updateNCharacterStream
    (

    columnIndex,
    x ) ;}@ Overridepublic void updateNCharacterStream( String columnLabel
    ,
        Readerreader)throwsSQLException{ _res.updateNCharacterStream
    (

    columnLabel,
    reader ) ;}@ Overridepublic void updateAsciiStream( int columnIndex
    ,
        InputStreamx)throwsSQLException{ _res.updateAsciiStream
    (

    columnIndex,
    x ) ;}@ Overridepublic void updateBinaryStream( int columnIndex
    ,
        InputStreamx)throwsSQLException{ _res.updateBinaryStream
    (

    columnIndex,
    x ) ;}@ Overridepublic void updateCharacterStream( int columnIndex
    ,
        Readerx)throwsSQLException{ _res.updateCharacterStream
    (

    columnIndex,
    x ) ;}@ Overridepublic void updateAsciiStream( String columnLabel
    ,
        InputStreamx)throwsSQLException{ _res.updateAsciiStream
    (

    columnLabel,
    x ) ;}@ Overridepublic void updateBinaryStream( String columnLabel
    ,
        InputStreamx)throwsSQLException{ _res.updateBinaryStream
    (

    columnLabel,
    x ) ;}@ Overridepublic void updateCharacterStream( String columnLabel
    ,
        Readerreader)throwsSQLException{ _res.updateCharacterStream
    (

    columnLabel,
    reader ) ;}@ Overridepublic void updateBlob( int columnIndex
    ,
        InputStreaminputStream)throwsSQLException{ _res.updateBlob
    (

    columnIndex,
    inputStream ) ;}@ Overridepublic void updateBlob( String columnLabel
    ,
        InputStreaminputStream)throwsSQLException{ _res.updateBlob
    (

    columnLabel,
    inputStream ) ;}@ Overridepublic void updateClob( int columnIndex
    ,
        Readerreader)throwsSQLException{ _res.updateClob
    (

    columnIndex,
    reader ) ;}@ Overridepublic void updateClob( String columnLabel
    ,
        Readerreader)throwsSQLException{ _res.updateClob
    (

    columnLabel,
    reader ) ;}@ Overridepublic void updateNClob( int columnIndex
    ,
        Readerreader)throwsSQLException{ _res.updateNClob
    (

    columnIndex,
    reader ) ;}@ Overridepublic void updateNClob( String columnLabel
    ,
        Readerreader)throwsSQLException{ _res.updateNClob
    (

    columnLabel,
    reader ) ;}@ Overridepublic < T> T getObject
    (
        intcolumnIndex,Class<T >type)
    throws

    SQLException{
    return _res.getObject ( columnIndex,type ); }@Overridepublic <T > T
    getObject
        ( StringcolumnLabel,Class<T >type)
    throws

    SQLException{
    return _res.getObject ( columnLabel,type ); }@Overridepublic voidupdateObject ( int
    columnIndex
        , Objectx,SQLTypetargetSqlType, intscaleOrLength)
    throws

    SQLException{
    _res . updateObject(columnIndex ,x , targetSqlType, scaleOrLength ); } @Override public void
    updateObject
        (StringcolumnLabel,Objectx ,SQLType targetSqlType, intscaleOrLength)
    throws

    SQLException{
    _res . updateObject(columnLabel ,x , targetSqlType, scaleOrLength ); } @Override public void
    updateObject
        (intcolumnIndex,Objectx ,SQLType targetSqlType) throwsSQLException{
    _res

    .updateObject
    ( columnIndex ,x, targetSqlType) ; }@ Override publicvoid updateObject (
    String
        columnLabel,Objectx,SQLType targetSqlType) throwsSQLException{
    _res

    .updateObject
    ( columnLabel ,x, targetSqlType) ; }@ Override public< T >
    T
        unwrap(Class<T> iface) throwsSQLException{
    return

    _res.
    unwrap (iface) ; }@OverridepublicbooleanisWrapperFor (Class < ?
    >
        iface )throwsSQLException{return_res.
    isWrapperFor

    (iface
    ) ; }}