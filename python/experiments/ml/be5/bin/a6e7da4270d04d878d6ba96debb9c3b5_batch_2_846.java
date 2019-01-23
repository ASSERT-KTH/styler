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
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

public class ResultSetWrapper implements ResultSet
{
    private ResultSet _res;

    ResultSetWrapper(ResultSet resultSet)
    {
        this._res = resultSet;
    }

    void setResultSet(ResultSet _res)
    {
        this._res = _res;
    }

    @Override
    public boolean next() throws SQLException
    {
        throw new UnsupportedOperationException("Method next() run automatically. Do not call this.");
    }

    @Override
    public void close() throws SQLException
    {
        _res.close();
    }

    @Override
    public boolean wasNull() throws SQLException
    {
        return _res.wasNull();
    }

    @Override
    public String getString(int columnIndex) throws SQLException
    {
        return _res.getString(columnIndex);
    }

    @Override
    public boolean getBoolean(int columnIndex) throws SQLException
    {
        return _res.getBoolean(columnIndex);
    }

    @Override
    public byte getByte(int columnIndex) throws SQLException
    {
        return _res.getByte(columnIndex);
    }

    @Override
    public short getShort(int columnIndex) throws SQLException
    {
        return _res.getShort(columnIndex);
    }

    @Override
    public int getInt(int columnIndex) throws SQLException
    {
        return _res.getInt(columnIndex);
    }

    @Override
    public long getLong(int columnIndex) throws SQLException
    {
        return _res.getLong(columnIndex);
    }

    @Override
    public float getFloat(int columnIndex) throws SQLException
    {
        return _res.getFloat(columnIndex);
    }

    @Override
    public double getDouble(int columnIndex) throws SQLException
    {
        return _res.getDouble(columnIndex);
    }

    @Override
    @Deprecated
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException
    {
        return _res.getBigDecimal(columnIndex, scale);
    }

    @Override
    public byte[] getBytes(int columnIndex) throws SQLException
    {
        return _res.getBytes(columnIndex);
    }

    @Override
    public Date getDate(int columnIndex) throws SQLException
    {
        return _res.getDate(columnIndex);
    }

    @Override
    public Time getTime(int columnIndex) throws SQLException
    {
        return _res.getTime(columnIndex);
    }

    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException
    {
        return _res.getTimestamp(columnIndex);
    }

    @Override
    public InputStream getAsciiStream(int columnIndex) throws SQLException
    {
        return _res.getAsciiStream(columnIndex);
    }

    @Override
    @Deprecated
    public InputStream getUnicodeStream(int columnIndex) throws SQLException
    {
        return _res.getUnicodeStream(columnIndex);
    }

    @Override
    public InputStream getBinaryStream(int columnIndex) throws SQLException
    {
        return _res.getBinaryStream(columnIndex);
    }

    @Override
    public String getString(String columnLabel) throws SQLException
    {
        return _res.getString(columnLabel);
    }

    @Override
    public boolean getBoolean(String columnLabel) throws SQLException
    {
        return _res.getBoolean(columnLabel);
    }

    @Override
    public byte getByte(String columnLabel) throws SQLException
    {
        return _res.getByte(columnLabel);
    }

    @Override
    public short getShort(String columnLabel) throws SQLException
    {
        return _res.getShort(columnLabel);
    }

    @Override
    public int getInt(String columnLabel) throws SQLException
    {
        return _res.getInt(columnLabel);
    }

    @Override
    public long getLong(String columnLabel) throws SQLException
    {
        return _res.getLong(columnLabel);
    }

    @Override
    public float getFloat(String columnLabel) throws SQLException
    {
        return _res.getFloat(columnLabel);
    }

    @Override
    public double getDouble(String columnLabel) throws SQLException
    {
        return _res.getDouble(columnLabel);
    }

    @Override
    @Deprecated
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException
    {
        return _res.getBigDecimal(columnLabel, scale);
    }

    @Override
    public byte[] getBytes(String columnLabel) throws SQLException
    {
        return _res.getBytes(columnLabel);
    }

    @Override
    public Date getDate(String columnLabel) throws SQLException
    {
        return _res.getDate(columnLabel);
    }

    @Override
    public Time getTime(String columnLabel) throws SQLException
    {
        return _res.getTime(columnLabel);
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) throws SQLException
    {
        return _res.getTimestamp(columnLabel);
    }

    @Override
    public InputStream getAsciiStream(String columnLabel) throws SQLException
    {
        return _res.getAsciiStream(columnLabel);
    }

    @Override
    @Deprecated
    public InputStream getUnicodeStream(String columnLabel) throws SQLException
    {
        return _res.getUnicodeStream(columnLabel);
    }

    @Override
    public InputStream getBinaryStream(String columnLabel) throws SQLException
    {
        return _res.getBinaryStream(columnLabel);
    }

    @Override
    public SQLWarning getWarnings() throws SQLException
    {
        return _res.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException
    {
        _res.clearWarnings();
    }

    @Override
    public String getCursorName() throws SQLException
    {
        return _res.getCursorName();
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException
    {
        return _res.getMetaData();
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException
    {
        return _res.getObject(columnIndex);
    }

    @Override
    public Object getObject(String columnLabel) throws SQLException
    {
        return _res.getObject(columnLabel);
    }

    @Override
    public int findColumn(String columnLabel) throws SQLException
    {
        return _res.findColumn(columnLabel);
    }

    @Override
    public Reader getCharacterStream(int columnIndex) throws SQLException
    {
        return _res.getCharacterStream(columnIndex);
    }

    @Override
    public Reader getCharacterStream(String columnLabel) throws SQLException
    {
        return _res.getCharacterStream(columnLabel);
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException
    {
        return _res.getBigDecimal(columnIndex);
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel) throws SQLException
    {
        return _res.getBigDecimal(columnLabel);
    }

    @Override
    public boolean isBeforeFirst() throws SQLException
    {
        return _res.isBeforeFirst();
    }

    @Override
    public boolean isAfterLast() throws SQLException
    {
        return _res.isAfterLast();
    }

    @Override
    public boolean isFirst() throws SQLException
    {
        return _res.isFirst();
    }

    @Override
    public boolean isLast() throws SQLException
    {
        return _res.isLast();
    }

    @Override
    public void beforeFirst() throws SQLException
    {
        _res.beforeFirst();
    }

    @Override
    public void afterLast() throws SQLException
    {
        _res.afterLast();
    }

    @Override
    public boolean first() throws SQLException
    {
        return _res.first();
    }

    @Override
    public boolean last() throws SQLException
    {
        return _res.last();
    }

    @Override
    public int getRow() throws SQLException
    {
        return _res.getRow();
    }

    @Override
    public boolean absolute(int row) throws SQLException
    {
        return _res.absolute(row);
    }

    @Override
    public boolean relative(int rows) throws SQLException
    {
        return _res.relative(rows);
    }

    @Override
    public boolean previous() throws SQLException
    {
        return _res.previous();
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException
    {
        _res.setFetchDirection(direction);
    }

    @Override
    public int getFetchDirection() throws SQLException
    {
        return _res.getFetchDirection();
    }

    @Override
    public void setFetchSize(int rows) throws SQLException
    {
        _res.setFetchSize(rows);
    }

    @Override
    public int getFetchSize() throws SQLException
    {
        return _res.getFetchSize();
    }

    @Override
    public int getType() throws SQLException
    {
        return _res.getType();
    }

    @Override
    public int getConcurrency() throws SQLException
    {
        return _res.getConcurrency();
    }

    @Override
    public boolean rowUpdated() throws SQLException
    {
        return _res.rowUpdated();
    }

    @Override
    public boolean rowInserted() throws SQLException
    {
        return _res.rowInserted();
    }

    @Override
    public boolean rowDeleted() throws SQLException
    {
        return _res.rowDeleted();
    }

    @Override
    public void updateNull(int columnIndex) throws SQLException
    {
        _res.updateNull(columnIndex);
    }

    @Override
    public void updateBoolean(int columnIndex, boolean x) throws SQLException
    {
        _res.updateBoolean(columnIndex, x);
    }

    @Override
    public void updateByte(int columnIndex, byte x) throws SQLException
    {
        _res.updateByte(columnIndex, x);
    }

    @Override
    public void updateShort(int columnIndex, short x) throws SQLException
    {
        _res.updateShort(columnIndex, x);
    }

    @Override
    public void updateInt(int columnIndex, int x) throws SQLException
    {
        _res.updateInt(columnIndex, x);
    }

    @Override
    public void updateLong(int columnIndex, long x) throws SQLException
    {
        _res.updateLong(columnIndex, x);
    }

    @Override
    public void updateFloat(int columnIndex, float x) throws SQLException
    {
        _res.updateFloat(columnIndex, x);
    }

    @Override
    public void updateDouble(int columnIndex, double x) throws SQLException
    {
        _res.updateDouble(columnIndex, x);
    }

    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException
    {
        _res.updateBigDecimal(columnIndex, x);
    }

    @Override
    public void updateString(int columnIndex, String x) throws SQLException
    {
        _res.updateString(columnIndex, x);
    }

    @Override
    public void updateBytes(int columnIndex, byte[] x) throws SQLException
    {
        _res.updateBytes(columnIndex, x);
    }

    @Override
    public void updateDate(int columnIndex, Date x) throws SQLException
    {
        _res.updateDate(columnIndex, x);
    }

    @Override
    public void updateTime(int columnIndex, Time x) throws SQLException
    {
        _res.updateTime(columnIndex, x);
    }

    @Override
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException
    {
        _res.updateTimestamp(columnIndex, x);
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException
    {
        _res.updateAsciiStream(columnIndex, x, length);
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException
    {
        _res.updateBinaryStream(columnIndex, x, length);
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException
    {
        _res.updateCharacterStream(columnIndex, x, length);
    }

    @Override
    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException
    {
        _res.updateObject(columnIndex, x, scaleOrLength);
    }

    @Override
    public void updateObject(int columnIndex, Object x) throws SQLException
    {
        _res.updateObject(columnIndex, x);
    }

    @Override
    public void updateNull(String columnLabel) throws SQLException
    {
        _res.updateNull(columnLabel);
    }

    @Override
    public void updateBoolean(String columnLabel, boolean x) throws SQLException
    {
        _res.updateBoolean(columnLabel, x);
    }

    @Override
    public void updateByte(String columnLabel, byte x) throws SQLException
    {
        _res.updateByte(columnLabel, x);
    }

    @Override
    public void updateShort(String columnLabel, short x) throws SQLException
    {
        _res.updateShort(columnLabel, x);
    }

    @Override
    public void updateInt(String columnLabel, int x) throws SQLException
    {
        _res.updateInt(columnLabel, x);
    }

    @Override
    public void updateLong(String columnLabel, long x) throws SQLException
    {
        _res.updateLong(columnLabel, x);
    }

    @Override
    public void updateFloat(String columnLabel, float x) throws SQLException
    {
        _res.updateFloat(columnLabel, x);
    }

    @Override
    public void updateDouble(String columnLabel, double x) throws SQLException
    {
        _res.updateDouble(columnLabel, x);
    }

    @Overridepublicvoid
    updateBigDecimal( StringcolumnLabel ,BigDecimalx)throwsSQLException {_res.updateBigDecimal (columnLabel ,x); }@ Overridepublic
    voidupdateString
        (StringcolumnLabel,Stringx)throws SQLException{_res
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