package com.developmentontheedge.be5.database.sql;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;importjava
.sql .Clob;importjava.sql.Date;importjava
.sql .NClob;importjava.sql.Ref;importjava
.sql .ResultSet;importjava.
sql .ResultSetMetaData;importjava.
sql .RowId;importjava.
sql .SQLException;importjava.
sql .SQLType;importjava.
sql .SQLWarning;importjava.
sql .SQLXML;importjava.
sql .Statement;importjava.
sql .Time;importjava.
sql .Timestamp;importjava.
util .Calendar;importjava.
util .Map;publicclassResultSetWrapper
implements ResultSet{privateResultSet_res;
ResultSetWrapper (ResultSetresultSet){this

. _res = resultSet ;
}
    void setResultSet (ResultSet

    _res){ this.
    _res
        =_res; } @Override
    public

    boolean next() throwsSQLException
    {
        thrownewUnsupportedOperationException ( "Method next() run automatically. Do not call this.")
    ;

    }@
    Override public voidclose( ) throws
    SQLException
        { _res .close();
    }

    @Override
    public boolean wasNull() throws SQLException
    {
        return_res.wasNull()
    ;

    }@
    Override public StringgetString( int columnIndex
    )
        throws SQLException{return_res.getString
    (

    columnIndex)
    ; } @Overridepublic booleangetBoolean ( int
    columnIndex
        ) throwsSQLException{return_res.getBoolean
    (

    columnIndex)
    ; } @Overridepublic bytegetByte ( int
    columnIndex
        ) throwsSQLException{return_res.getByte
    (

    columnIndex)
    ; } @Overridepublic shortgetShort ( int
    columnIndex
        ) throwsSQLException{return_res.getShort
    (

    columnIndex)
    ; } @Overridepublic intgetInt ( int
    columnIndex
        ) throwsSQLException{return_res.getInt
    (

    columnIndex)
    ; } @Overridepublic longgetLong ( int
    columnIndex
        ) throwsSQLException{return_res.getLong
    (

    columnIndex)
    ; } @Overridepublic floatgetFloat ( int
    columnIndex
        ) throwsSQLException{return_res.getFloat
    (

    columnIndex)
    ; } @Overridepublic doublegetDouble ( int
    columnIndex
        ) throwsSQLException{return_res.getDouble
    (

    columnIndex)
    ; } @Override@ Deprecatedpublic BigDecimal getBigDecimal
    (
        int columnIndex,intscale)throwsSQLException
    {

    return_res
    .getBigDecimal
    ( columnIndex ,scale) ;} @ Overridepublic byte [
    ]
        getBytes (intcolumnIndex)throwsSQLException {return_res
    .

    getBytes(
    columnIndex );} @Overridepublic DategetDate ( int
    columnIndex
        ) throwsSQLException{return_res.getDate
    (

    columnIndex)
    ; } @Overridepublic TimegetTime ( int
    columnIndex
        ) throwsSQLException{return_res.getTime
    (

    columnIndex)
    ; } @Overridepublic TimestampgetTimestamp ( int
    columnIndex
        ) throwsSQLException{return_res.getTimestamp
    (

    columnIndex)
    ; } @Overridepublic InputStreamgetAsciiStream ( int
    columnIndex
        ) throwsSQLException{return_res.getAsciiStream
    (

    columnIndex)
    ; } @Override@ Deprecatedpublic InputStream getUnicodeStream
    (
        int columnIndex)throwsSQLException{return_res
    .

    getUnicodeStream(
    columnIndex)
    ; } @Overridepublic InputStreamgetBinaryStream ( int
    columnIndex
        ) throwsSQLException{return_res.getBinaryStream
    (

    columnIndex)
    ; } @Overridepublic StringgetString ( String
    columnLabel
        ) throwsSQLException{return_res.getString
    (

    columnLabel)
    ; } @Overridepublic booleangetBoolean ( String
    columnLabel
        ) throwsSQLException{return_res.getBoolean
    (

    columnLabel)
    ; } @Overridepublic bytegetByte ( String
    columnLabel
        ) throwsSQLException{return_res.getByte
    (

    columnLabel)
    ; } @Overridepublic shortgetShort ( String
    columnLabel
        ) throwsSQLException{return_res.getShort
    (

    columnLabel)
    ; } @Overridepublic intgetInt ( String
    columnLabel
        ) throwsSQLException{return_res.getInt
    (

    columnLabel)
    ; } @Overridepublic longgetLong ( String
    columnLabel
        ) throwsSQLException{return_res.getLong
    (

    columnLabel)
    ; } @Overridepublic floatgetFloat ( String
    columnLabel
        ) throwsSQLException{return_res.getFloat
    (

    columnLabel)
    ; } @Overridepublic doublegetDouble ( String
    columnLabel
        ) throwsSQLException{return_res.getDouble
    (

    columnLabel)
    ; } @Override@ Deprecatedpublic BigDecimal getBigDecimal
    (
        String columnLabel,intscale)throwsSQLException
    {

    return_res
    .getBigDecimal
    ( columnLabel ,scale) ;} @ Overridepublic byte [
    ]
        getBytes (StringcolumnLabel)throwsSQLException {return_res
    .

    getBytes(
    columnLabel );} @Overridepublic DategetDate ( String
    columnLabel
        ) throwsSQLException{return_res.getDate
    (

    columnLabel)
    ; } @Overridepublic TimegetTime ( String
    columnLabel
        ) throwsSQLException{return_res.getTime
    (

    columnLabel)
    ; } @Overridepublic TimestampgetTimestamp ( String
    columnLabel
        ) throwsSQLException{return_res.getTimestamp
    (

    columnLabel)
    ; } @Overridepublic InputStreamgetAsciiStream ( String
    columnLabel
        ) throwsSQLException{return_res.getAsciiStream
    (

    columnLabel)
    ; } @Override@ Deprecatedpublic InputStream getUnicodeStream
    (
        String columnLabel)throwsSQLException{return_res
    .

    getUnicodeStream(
    columnLabel)
    ; } @Overridepublic InputStreamgetBinaryStream ( String
    columnLabel
        ) throwsSQLException{return_res.getBinaryStream
    (

    columnLabel)
    ; } @Overridepublic SQLWarninggetWarnings ( )
    throws
        SQLException {return_res.getWarnings()
    ;

    }@
    Override public voidclearWarnings( ) throws
    SQLException
        { _res.clearWarnings();
    }

    @Override
    public String getCursorName() throws SQLException
    {
        return_res.getCursorName()
    ;

    }@
    Override public ResultSetMetaDatagetMetaData( ) throws
    SQLException
        { return_res.getMetaData()
    ;

    }@
    Override public ObjectgetObject( int columnIndex
    )
        throws SQLException{return_res.getObject
    (

    columnIndex)
    ; } @Overridepublic ObjectgetObject ( String
    columnLabel
        ) throwsSQLException{return_res.getObject
    (

    columnLabel)
    ; } @Overridepublic intfindColumn ( String
    columnLabel
        ) throwsSQLException{return_res.findColumn
    (

    columnLabel)
    ; } @Overridepublic ReadergetCharacterStream ( int
    columnIndex
        ) throwsSQLException{return_res.getCharacterStream
    (

    columnIndex)
    ; } @Overridepublic ReadergetCharacterStream ( String
    columnLabel
        ) throwsSQLException{return_res.getCharacterStream
    (

    columnLabel)
    ; } @Overridepublic BigDecimalgetBigDecimal ( int
    columnIndex
        ) throwsSQLException{return_res.getBigDecimal
    (

    columnIndex)
    ; } @Overridepublic BigDecimalgetBigDecimal ( String
    columnLabel
        ) throwsSQLException{return_res.getBigDecimal
    (

    columnLabel)
    ; } @Overridepublic booleanisBeforeFirst ( )
    throws
        SQLException {return_res.isBeforeFirst()
    ;

    }@
    Override public booleanisAfterLast( ) throws
    SQLException
        { return_res.isAfterLast()
    ;

    }@
    Override public booleanisFirst( ) throws
    SQLException
        { return_res.isFirst()
    ;

    }@
    Override public booleanisLast( ) throws
    SQLException
        { return_res.isLast()
    ;

    }@
    Override public voidbeforeFirst( ) throws
    SQLException
        { _res.beforeFirst();
    }

    @Override
    public void afterLast() throws SQLException
    {
        _res.afterLast();
    }

    @Override
    public boolean first() throws SQLException
    {
        return_res.first()
    ;

    }@
    Override public booleanlast( ) throws
    SQLException
        { return_res.last()
    ;

    }@
    Override public intgetRow( ) throws
    SQLException
        { return_res.getRow()
    ;

    }@
    Override public booleanabsolute( int row
    )
        throws SQLException{return_res.absolute
    (

    row)
    ; } @Overridepublic booleanrelative ( int
    rows
        ) throwsSQLException{return_res.relative
    (

    rows)
    ; } @Overridepublic booleanprevious ( )
    throws
        SQLException {return_res.previous()
    ;

    }@
    Override public voidsetFetchDirection( int direction
    )
        throws SQLException{_res.setFetchDirection(
    direction

    );
    } @ Overridepublicint getFetchDirection( ) throws
    SQLException
        {return_res.getFetchDirection()
    ;

    }@
    Override public voidsetFetchSize( int rows
    )
        throws SQLException{_res.setFetchSize(
    rows

    );
    } @ Overridepublicint getFetchSize( ) throws
    SQLException
        {return_res.getFetchSize()
    ;

    }@
    Override public intgetType( ) throws
    SQLException
        { return_res.getType()
    ;

    }@
    Override public intgetConcurrency( ) throws
    SQLException
        { return_res.getConcurrency()
    ;

    }@
    Override public booleanrowUpdated( ) throws
    SQLException
        { return_res.rowUpdated()
    ;

    }@
    Override public booleanrowInserted( ) throws
    SQLException
        { return_res.rowInserted()
    ;

    }@
    Override public booleanrowDeleted( ) throws
    SQLException
        { return_res.rowDeleted()
    ;

    }@
    Override public voidupdateNull( int columnIndex
    )
        throws SQLException{_res.updateNull(
    columnIndex

    );
    } @ Overridepublicvoid updateBoolean( int columnIndex
    ,
        booleanx)throwsSQLException{_res
    .

    updateBoolean(
    columnIndex , x); }@ Override publicvoid updateByte (
    int
        columnIndex,bytex)throws SQLException{_res
    .

    updateByte(
    columnIndex , x); }@ Override publicvoid updateShort (
    int
        columnIndex,shortx)throws SQLException{_res
    .

    updateShort(
    columnIndex , x); }@ Override publicvoid updateInt (
    int
        columnIndex,intx)throws SQLException{_res
    .

    updateInt(
    columnIndex , x); }@ Override publicvoid updateLong (
    int
        columnIndex,longx)throws SQLException{_res
    .

    updateLong(
    columnIndex , x); }@ Override publicvoid updateFloat (
    int
        columnIndex,floatx)throws SQLException{_res
    .

    updateFloat(
    columnIndex , x); }@ Override publicvoid updateDouble (
    int
        columnIndex,doublex)throws SQLException{_res
    .

    updateDouble(
    columnIndex , x); }@ Override publicvoid updateBigDecimal (
    int
        columnIndex,BigDecimalx)throws SQLException{_res
    .

    updateBigDecimal(
    columnIndex , x); }@ Override publicvoid updateString (
    int
        columnIndex,Stringx)throws SQLException{_res
    .

    updateString(
    columnIndex , x); }@ Override publicvoid updateBytes (
    int
        columnIndex,byte[]x )throwsSQLException
    {

    _res.
    updateBytes ( columnIndex,x ); }@Override publicvoid updateDate (
    int
        columnIndex,Datex)throws SQLException{_res
    .

    updateDate(
    columnIndex , x); }@ Override publicvoid updateTime (
    int
        columnIndex,Timex)throws SQLException{_res
    .

    updateTime(
    columnIndex , x); }@ Override publicvoid updateTimestamp (
    int
        columnIndex,Timestampx)throws SQLException{_res
    .

    updateTimestamp(
    columnIndex , x); }@ Override publicvoid updateAsciiStream (
    int
        columnIndex,InputStreamx,int length)throws
    SQLException

    {_res
    . updateAsciiStream (columnIndex, x, length ); } @Override public void
    updateBinaryStream
        (intcolumnIndex,InputStreamx ,int length)throws
    SQLException

    {_res
    . updateBinaryStream (columnIndex, x, length ); } @Override public void
    updateCharacterStream
        (intcolumnIndex,Readerx ,int length)throws
    SQLException

    {_res
    . updateCharacterStream (columnIndex, x, length ); } @Override public void
    updateObject
        (intcolumnIndex,Objectx ,int scaleOrLength)throws
    SQLException

    {_res
    . updateObject (columnIndex, x, scaleOrLength ); } @Override public void
    updateObject
        (intcolumnIndex,Objectx )throws SQLException{_res
    .

    updateObject(
    columnIndex , x); }@ Override publicvoid updateNull (
    String
        columnLabel)throwsSQLException{_res .updateNull(
    columnLabel

    );
    } @ Overridepublicvoid updateBoolean( String columnLabel
    ,
        booleanx)throwsSQLException{_res
    .

    updateBoolean(
    columnLabel , x); }@ Override publicvoid updateByte (
    String
        columnLabel,bytex)throws SQLException{_res
    .

    updateByte(
    columnLabel , x); }@ Override publicvoid updateShort (
    String
        columnLabel,shortx)throws SQLException{_res
    .

    updateShort(
    columnLabel , x); }@ Override publicvoid updateInt (
    String
        columnLabel,intx)throws SQLException{_res
    .

    updateInt(
    columnLabel , x); }@ Override publicvoid updateLong (
    String
        columnLabel,longx)throws SQLException{_res
    .

    updateLong(
    columnLabel , x); }@ Override publicvoid updateFloat (
    String
        columnLabel,floatx)throws SQLException{_res
    .

    updateFloat(
    columnLabel , x); }@ Override publicvoid updateDouble (
    String
        columnLabel,doublex)throws SQLException{_res
    .

    updateDouble(
    columnLabel , x); }@ Override publicvoid updateBigDecimal (
    String
        columnLabel,BigDecimalx)throws SQLException{_res
    .

    updateBigDecimal(
    columnLabel , x); }@ Override publicvoid updateString (
    String
        columnLabel,Stringx)throws SQLException{_res
    .

    updateString(
    columnLabel , x); }@ Override publicvoid updateBytes (
    String
        columnLabel,byte[]x )throwsSQLException
    {

    _res.
    updateBytes ( columnLabel,x ); }@Override publicvoid updateDate (
    String
        columnLabel,Datex)throws SQLException{_res
    .

    updateDate(
    columnLabel , x); }@ Override publicvoid updateTime (
    String
        columnLabel,Timex)throws SQLException{_res
    .

    updateTime(
    columnLabel , x); }@ Override publicvoid updateTimestamp (
    String
        columnLabel,Timestampx)throws SQLException{_res
    .

    updateTimestamp(
    columnLabel , x); }@ Override publicvoid updateAsciiStream (
    String
        columnLabel,InputStreamx,int length)throws
    SQLException

    {_res
    . updateAsciiStream (columnLabel, x, length ); } @Override public void
    updateBinaryStream
        (StringcolumnLabel,InputStreamx ,int length)throws
    SQLException

    {_res
    . updateBinaryStream (columnLabel, x, length ); } @Override public void
    updateCharacterStream
        (StringcolumnLabel,Readerreader ,int length)throws
    SQLException

    {_res
    . updateCharacterStream (columnLabel, reader, length ); } @Override public void
    updateObject
        (StringcolumnLabel,Objectx ,int scaleOrLength)throws
    SQLException

    {_res
    . updateObject (columnLabel, x, scaleOrLength ); } @Override public void
    updateObject
        (StringcolumnLabel,Objectx )throws SQLException{_res
    .

    updateObject(
    columnLabel , x); }@ Override publicvoid insertRow (
    )
        throwsSQLException{_res.insertRow ();
    }

    @Override
    public void updateRow() throws SQLException
    {
        _res.updateRow();
    }

    @Override
    public void deleteRow() throws SQLException
    {
        _res.deleteRow();
    }

    @Override
    public void refreshRow() throws SQLException
    {
        _res.refreshRow();
    }

    @Override
    public void cancelRowUpdates() throws SQLException
    {
        _res.cancelRowUpdates();
    }

    @Override
    public void moveToInsertRow() throws SQLException
    {
        _res.moveToInsertRow();
    }

    @Override
    public void moveToCurrentRow() throws SQLException
    {
        _res.moveToCurrentRow();
    }

    @Override
    public Statement getStatement() throws SQLException
    {
        return_res.getStatement()
    ;

    }@
    Override public ObjectgetObject( int columnIndex
    ,
        Map <String,Class<?
    >

    >map
    ) throws SQLException{return _res. getObject(columnIndex, map);}@ Overridepublic Ref getRef
    (
        int columnIndex)throwsSQLException{return _res.getRef
    (

    columnIndex)
    ; } @Overridepublic BlobgetBlob ( int
    columnIndex
        ) throwsSQLException{return_res.getBlob
    (

    columnIndex)
    ; } @Overridepublic ClobgetClob ( int
    columnIndex
        ) throwsSQLException{return_res.getClob
    (

    columnIndex)
    ; } @Overridepublic ArraygetArray ( int
    columnIndex
        ) throwsSQLException{return_res.getArray
    (

    columnIndex)
    ; } @Overridepublic ObjectgetObject ( String
    columnLabel
        , Map<String,Class<?
    >

    >map
    ) throws SQLException{return _res. getObject(columnLabel, map);}@ Overridepublic Ref getRef
    (
        String columnLabel)throwsSQLException{return _res.getRef
    (

    columnLabel)
    ; } @Overridepublic BlobgetBlob ( String
    columnLabel
        ) throwsSQLException{return_res.getBlob
    (

    columnLabel)
    ; } @Overridepublic ClobgetClob ( String
    columnLabel
        ) throwsSQLException{return_res.getClob
    (

    columnLabel)
    ; } @Overridepublic ArraygetArray ( String
    columnLabel
        ) throwsSQLException{return_res.getArray
    (

    columnLabel)
    ; } @Overridepublic DategetDate ( int
    columnIndex
        , Calendarcal)throwsSQLException{return
    _res

    .getDate
    ( columnIndex ,cal) ;} @ Overridepublic Date getDate
    (
        String columnLabel,Calendarcal)throws SQLException{return
    _res

    .getDate
    ( columnLabel ,cal) ;} @ Overridepublic Time getTime
    (
        int columnIndex,Calendarcal)throws SQLException{return
    _res

    .getTime
    ( columnIndex ,cal) ;} @ Overridepublic Time getTime
    (
        String columnLabel,Calendarcal)throws SQLException{return
    _res

    .getTime
    ( columnLabel ,cal) ;} @ Overridepublic Timestamp getTimestamp
    (
        int columnIndex,Calendarcal)throws SQLException{return
    _res

    .getTimestamp
    ( columnIndex ,cal) ;} @ Overridepublic Timestamp getTimestamp
    (
        String columnLabel,Calendarcal)throws SQLException{return
    _res

    .getTimestamp
    ( columnLabel ,cal) ;} @ Overridepublic URL getURL
    (
        int columnIndex)throwsSQLException{return _res.getURL
    (

    columnIndex)
    ; } @Overridepublic URLgetURL ( String
    columnLabel
        ) throwsSQLException{return_res.getURL
    (

    columnLabel)
    ; } @Overridepublic voidupdateRef ( int
    columnIndex
        , Refx)throwsSQLException{_res
    .

    updateRef(
    columnIndex , x); }@ Override publicvoid updateRef (
    String
        columnLabel,Refx)throws SQLException{_res
    .

    updateRef(
    columnLabel , x); }@ Override publicvoid updateBlob (
    int
        columnIndex,Blobx)throws SQLException{_res
    .

    updateBlob(
    columnIndex , x); }@ Override publicvoid updateBlob (
    String
        columnLabel,Blobx)throws SQLException{_res
    .

    updateBlob(
    columnLabel , x); }@ Override publicvoid updateClob (
    int
        columnIndex,Clobx)throws SQLException{_res
    .

    updateClob(
    columnIndex , x); }@ Override publicvoid updateClob (
    String
        columnLabel,Clobx)throws SQLException{_res
    .

    updateClob(
    columnLabel , x); }@ Override publicvoid updateArray (
    int
        columnIndex,Arrayx)throws SQLException{_res
    .

    updateArray(
    columnIndex , x); }@ Override publicvoid updateArray (
    String
        columnLabel,Arrayx)throws SQLException{_res
    .

    updateArray(
    columnLabel , x); }@ Override publicRowId getRowId (
    int
        columnIndex)throwsSQLException{return _res.getRowId
    (

    columnIndex)
    ; } @Overridepublic RowIdgetRowId ( String
    columnLabel
        ) throwsSQLException{return_res.getRowId
    (

    columnLabel)
    ; } @Overridepublic voidupdateRowId ( int
    columnIndex
        , RowIdx)throwsSQLException{_res
    .

    updateRowId(
    columnIndex , x); }@ Override publicvoid updateRowId (
    String
        columnLabel,RowIdx)throws SQLException{_res
    .

    updateRowId(
    columnLabel , x); }@ Override publicint getHoldability (
    )
        throwsSQLException{return_res. getHoldability()
    ;

    }@
    Override public booleanisClosed( ) throws
    SQLException
        { return_res.isClosed()
    ;

    }@
    Override public voidupdateNString( int columnIndex
    ,
        String nString)throwsSQLException{_res
    .

    updateNString(
    columnIndex , nString); }@ Override publicvoid updateNString (
    String
        columnLabel,StringnString)throws SQLException{_res
    .

    updateNString(
    columnLabel , nString); }@ Override publicvoid updateNClob (
    int
        columnIndex,NClobnClob)throws SQLException{_res
    .

    updateNClob(
    columnIndex , nClob); }@ Override publicvoid updateNClob (
    String
        columnLabel,NClobnClob)throws SQLException{_res
    .

    updateNClob(
    columnLabel , nClob); }@ Override publicNClob getNClob (
    int
        columnIndex)throwsSQLException{return _res.getNClob
    (

    columnIndex)
    ; } @Overridepublic NClobgetNClob ( String
    columnLabel
        ) throwsSQLException{return_res.getNClob
    (

    columnLabel)
    ; } @Overridepublic SQLXMLgetSQLXML ( int
    columnIndex
        ) throwsSQLException{return_res.getSQLXML
    (

    columnIndex)
    ; } @Overridepublic SQLXMLgetSQLXML ( String
    columnLabel
        ) throwsSQLException{return_res.getSQLXML
    (

    columnLabel)
    ; } @Overridepublic voidupdateSQLXML ( int
    columnIndex
        , SQLXMLxmlObject)throwsSQLException{_res
    .

    updateSQLXML(
    columnIndex , xmlObject); }@ Override publicvoid updateSQLXML (
    String
        columnLabel,SQLXMLxmlObject)throws SQLException{_res
    .

    updateSQLXML(
    columnLabel , xmlObject); }@ Override publicString getNString (
    int
        columnIndex)throwsSQLException{return _res.getNString
    (

    columnIndex)
    ; } @Overridepublic StringgetNString ( String
    columnLabel
        ) throwsSQLException{return_res.getNString
    (

    columnLabel)
    ; } @Overridepublic ReadergetNCharacterStream ( int
    columnIndex
        ) throwsSQLException{return_res.getNCharacterStream
    (

    columnIndex)
    ; } @Overridepublic ReadergetNCharacterStream ( String
    columnLabel
        ) throwsSQLException{return_res.getNCharacterStream
    (

    columnLabel)
    ; } @Overridepublic voidupdateNCharacterStream ( int
    columnIndex
        , Readerx,longlength)throws
    SQLException

    {_res
    . updateNCharacterStream (columnIndex, x, length ); } @Override public void
    updateNCharacterStream
        (StringcolumnLabel,Readerreader ,long length)throws
    SQLException

    {_res
    . updateNCharacterStream (columnLabel, reader, length ); } @Override public void
    updateAsciiStream
        (intcolumnIndex,InputStreamx ,long length)throws
    SQLException

    {_res
    . updateAsciiStream (columnIndex, x, length ); } @Override public void
    updateBinaryStream
        (intcolumnIndex,InputStreamx ,long length)throws
    SQLException

    {_res
    . updateBinaryStream (columnIndex, x, length ); } @Override public void
    updateCharacterStream
        (intcolumnIndex,Readerx ,long length)throws
    SQLException

    {_res
    . updateCharacterStream (columnIndex, x, length ); } @Override public void
    updateAsciiStream
        (StringcolumnLabel,InputStreamx ,long length)throws
    SQLException

    {_res
    . updateAsciiStream (columnLabel, x, length ); } @Override public void
    updateBinaryStream
        (StringcolumnLabel,InputStreamx ,long length)throws
    SQLException

    {_res
    . updateBinaryStream (columnLabel, x, length ); } @Override public void
    updateCharacterStream
        (StringcolumnLabel,Readerreader ,long length)throws
    SQLException

    {_res
    . updateCharacterStream (columnLabel, reader, length ); } @Override public void
    updateBlob
        (intcolumnIndex,InputStreaminputStream ,long length)throws
    SQLException

    {_res
    . updateBlob (columnIndex, inputStream, length ); } @Override public void
    updateBlob
        (StringcolumnLabel,InputStreaminputStream ,long length)throws
    SQLException

    {_res
    . updateBlob (columnLabel, inputStream, length ); } @Override public void
    updateClob
        (intcolumnIndex,Readerreader ,long length)throws
    SQLException

    {_res
    . updateClob (columnIndex, reader, length ); } @Override public void
    updateClob
        (StringcolumnLabel,Readerreader ,long length)throws
    SQLException

    {_res
    . updateClob (columnLabel, reader, length ); } @Override public void
    updateNClob
        (intcolumnIndex,Readerreader ,long length)throws
    SQLException

    {_res
    . updateNClob (columnIndex, reader, length ); } @Override public void
    updateNClob
        (StringcolumnLabel,Readerreader ,long length)throws
    SQLException

    {_res
    . updateNClob (columnLabel, reader, length ); } @Override public void
    updateNCharacterStream
        (intcolumnIndex,Readerx )throws SQLException{_res
    .

    updateNCharacterStream(
    columnIndex , x); }@ Override publicvoid updateNCharacterStream (
    String
        columnLabel,Readerreader)throws SQLException{_res
    .

    updateNCharacterStream(
    columnLabel , reader); }@ Override publicvoid updateAsciiStream (
    int
        columnIndex,InputStreamx)throws SQLException{_res
    .

    updateAsciiStream(
    columnIndex , x); }@ Override publicvoid updateBinaryStream (
    int
        columnIndex,InputStreamx)throws SQLException{_res
    .

    updateBinaryStream(
    columnIndex , x); }@ Override publicvoid updateCharacterStream (
    int
        columnIndex,Readerx)throws SQLException{_res
    .

    updateCharacterStream(
    columnIndex , x); }@ Override publicvoid updateAsciiStream (
    String
        columnLabel,InputStreamx)throws SQLException{_res
    .

    updateAsciiStream(
    columnLabel , x); }@ Override publicvoid updateBinaryStream (
    String
        columnLabel,InputStreamx)throws SQLException{_res
    .

    updateBinaryStream(
    columnLabel , x); }@ Override publicvoid updateCharacterStream (
    String
        columnLabel,Readerreader)throws SQLException{_res
    .

    updateCharacterStream(
    columnLabel , reader); }@ Override publicvoid updateBlob (
    int
        columnIndex,InputStreaminputStream)throws SQLException{_res
    .

    updateBlob(
    columnIndex , inputStream); }@ Override publicvoid updateBlob (
    String
        columnLabel,InputStreaminputStream)throws SQLException{_res
    .

    updateBlob(
    columnLabel , inputStream); }@ Override publicvoid updateClob (
    int
        columnIndex,Readerreader)throws SQLException{_res
    .

    updateClob(
    columnIndex , reader); }@ Override publicvoid updateClob (
    String
        columnLabel,Readerreader)throws SQLException{_res
    .

    updateClob(
    columnLabel , reader); }@ Override publicvoid updateNClob (
    int
        columnIndex,Readerreader)throws SQLException{_res
    .

    updateNClob(
    columnIndex , reader); }@ Override publicvoid updateNClob (
    String
        columnLabel,Readerreader)throws SQLException{_res
    .

    updateNClob(
    columnLabel , reader); }@ Override public< T >
    T
        getObject(intcolumnIndex,Class <T>
    type

    )throws
    SQLException {return_res . getObject(columnIndex ,type );}@ Overridepublic < T
    >
        T getObject(StringcolumnLabel,Class <T>
    type

    )throws
    SQLException {return_res . getObject(columnLabel ,type );}@ Overridepublic void updateObject
    (
        int columnIndex,Objectx,SQLType targetSqlType,int
    scaleOrLength

    )throws
    SQLException { _res.updateObject (columnIndex , x, targetSqlType ,scaleOrLength ) ;} @ Override
    public
        voidupdateObject(StringcolumnLabel, Objectx ,SQLType targetSqlType,int
    scaleOrLength

    )throws
    SQLException { _res.updateObject (columnLabel , x, targetSqlType ,scaleOrLength ) ;} @ Override
    public
        voidupdateObject(intcolumnIndex, Objectx ,SQLType targetSqlType)throws
    SQLException

    {_res
    . updateObject (columnIndex, x, targetSqlType ); } @Override public void
    updateObject
        (StringcolumnLabel,Objectx ,SQLType targetSqlType)throws
    SQLException

    {_res
    . updateObject (columnLabel, x, targetSqlType ); } @Override public <
    T
        >Tunwrap(Class< T> iface)throws
    SQLException

    {return
    _res .unwrap( iface );}@Overridepublic booleanisWrapperFor ( Class
    <
        ? >iface)throwsSQLException{return
    _res

    .isWrapperFor
    ( iface );}}