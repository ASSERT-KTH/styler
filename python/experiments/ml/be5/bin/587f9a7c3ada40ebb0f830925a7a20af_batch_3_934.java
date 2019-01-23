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
    public intgetFetchDirection
    ( ) throwsSQLException{ return_res . getFetchDirection
    (
        );}@Overridepublicvoid
    setFetchSize

    (int
    rows ) throwsSQLException{ _res .
    setFetchSize
        ( rows);}@Override
    public

    intgetFetchSize
    ( ) throwsSQLException{ return _res
    .
        getFetchSize ();}@Override
    public

    intgetType
    ( ) throwsSQLException{ return _res
    .
        getType ();}@Override
    public

    intgetConcurrency
    ( ) throwsSQLException{ return _res
    .
        getConcurrency ();}@Override
    public

    booleanrowUpdated
    ( ) throwsSQLException{ return _res
    .
        rowUpdated ();}@Override
    public

    booleanrowInserted
    ( ) throwsSQLException{ return _res
    .
        rowInserted ();}@Override
    public

    booleanrowDeleted
    ( ) throwsSQLException{ return_res . rowDeleted
    (
        );}@Overridepublicvoid
    updateNull

    (int
    columnIndex ) throwsSQLException{ _res. updateNull (columnIndex ) ;
    }
        @OverridepublicvoidupdateBoolean( intcolumnIndex,
    boolean

    x)
    throws SQLException {_res. updateBoolean( columnIndex ,x ) ;
    }
        @OverridepublicvoidupdateByte( intcolumnIndex,
    byte

    x)
    throws SQLException {_res. updateByte( columnIndex ,x ) ;
    }
        @OverridepublicvoidupdateShort( intcolumnIndex,
    short

    x)
    throws SQLException {_res. updateShort( columnIndex ,x ) ;
    }
        @OverridepublicvoidupdateInt( intcolumnIndex,
    int

    x)
    throws SQLException {_res. updateInt( columnIndex ,x ) ;
    }
        @OverridepublicvoidupdateLong( intcolumnIndex,
    long

    x)
    throws SQLException {_res. updateLong( columnIndex ,x ) ;
    }
        @OverridepublicvoidupdateFloat( intcolumnIndex,
    float

    x)
    throws SQLException {_res. updateFloat( columnIndex ,x ) ;
    }
        @OverridepublicvoidupdateDouble( intcolumnIndex,
    double

    x)
    throws SQLException {_res. updateDouble( columnIndex ,x ) ;
    }
        @OverridepublicvoidupdateBigDecimal( intcolumnIndex,
    BigDecimal

    x)
    throws SQLException {_res. updateBigDecimal( columnIndex ,x ) ;
    }
        @OverridepublicvoidupdateString( intcolumnIndex,
    String

    x)
    throws SQLException {_res. updateString( columnIndex,x ); } @
    Override
        publicvoidupdateBytes(intcolumnIndex ,byte[
    ]

    x)
    throws SQLException {_res. updateBytes( columnIndex ,x ) ;
    }
        @OverridepublicvoidupdateDate( intcolumnIndex,
    Date

    x)
    throws SQLException {_res. updateDate( columnIndex ,x ) ;
    }
        @OverridepublicvoidupdateTime( intcolumnIndex,
    Time

    x)
    throws SQLException {_res. updateTime( columnIndex ,x ) ;
    }
        @OverridepublicvoidupdateTimestamp( intcolumnIndex,
    Timestamp

    x)
    throws SQLException {_res. updateTimestamp( columnIndex ,x ) ;} @ Override
    public
        voidupdateAsciiStream(intcolumnIndex, InputStreamx ,intlength
    )

    throwsSQLException
    { _res .updateAsciiStream( columnIndex, x ,length ) ;} @ Override
    public
        voidupdateBinaryStream(intcolumnIndex, InputStreamx ,intlength
    )

    throwsSQLException
    { _res .updateBinaryStream( columnIndex, x ,length ) ;} @ Override
    public
        voidupdateCharacterStream(intcolumnIndex, Readerx ,intlength
    )

    throwsSQLException
    { _res .updateCharacterStream( columnIndex, x ,length ) ;} @ Override
    public
        voidupdateObject(intcolumnIndex, Objectx ,intscaleOrLength
    )

    throwsSQLException
    { _res .updateObject( columnIndex, x ,scaleOrLength ) ;
    }
        @OverridepublicvoidupdateObject( intcolumnIndex,
    Object

    x)
    throws SQLException {_res. updateObject( columnIndex ,
    x
        );}@Overridepublicvoid
    updateNull

    (String
    columnLabel ) throwsSQLException{ _res. updateNull (columnLabel ) ;
    }
        @OverridepublicvoidupdateBoolean( StringcolumnLabel,
    boolean

    x)
    throws SQLException {_res. updateBoolean( columnLabel ,x ) ;
    }
        @OverridepublicvoidupdateByte( StringcolumnLabel,
    byte

    x)
    throws SQLException {_res. updateByte( columnLabel ,x ) ;
    }
        @OverridepublicvoidupdateShort( StringcolumnLabel,
    short

    x)
    throws SQLException {_res. updateShort( columnLabel ,x ) ;
    }
        @OverridepublicvoidupdateInt( StringcolumnLabel,
    int

    x)
    throws SQLException {_res. updateInt( columnLabel ,x ) ;
    }
        @OverridepublicvoidupdateLong( StringcolumnLabel,
    long

    x)
    throws SQLException {_res. updateLong( columnLabel ,x ) ;
    }
        @OverridepublicvoidupdateFloat( StringcolumnLabel,
    float

    x)
    throws SQLException {_res. updateFloat( columnLabel ,x ) ;
    }
        @OverridepublicvoidupdateDouble( StringcolumnLabel,
    double

    x)
    throws SQLException {_res. updateDouble( columnLabel ,x ) ;
    }
        @OverridepublicvoidupdateBigDecimal( StringcolumnLabel,
    BigDecimal

    x)
    throws SQLException {_res. updateBigDecimal( columnLabel ,x ) ;
    }
        @OverridepublicvoidupdateString( StringcolumnLabel,
    String

    x)
    throws SQLException {_res. updateString( columnLabel,x ); } @
    Override
        publicvoidupdateBytes(StringcolumnLabel ,byte[
    ]

    x)
    throws SQLException {_res. updateBytes( columnLabel ,x ) ;
    }
        @OverridepublicvoidupdateDate( StringcolumnLabel,
    Date

    x)
    throws SQLException {_res. updateDate( columnLabel ,x ) ;
    }
        @OverridepublicvoidupdateTime( StringcolumnLabel,
    Time

    x)
    throws SQLException {_res. updateTime( columnLabel ,x ) ;
    }
        @OverridepublicvoidupdateTimestamp( StringcolumnLabel,
    Timestamp

    x)
    throws SQLException {_res. updateTimestamp( columnLabel ,x ) ;} @ Override
    public
        voidupdateAsciiStream(StringcolumnLabel, InputStreamx ,intlength
    )

    throwsSQLException
    { _res .updateAsciiStream( columnLabel, x ,length ) ;} @ Override
    public
        voidupdateBinaryStream(StringcolumnLabel, InputStreamx ,intlength
    )

    throwsSQLException
    { _res .updateBinaryStream( columnLabel, x ,length ) ;} @ Override
    public
        voidupdateCharacterStream(StringcolumnLabel, Readerreader ,intlength
    )

    throwsSQLException
    { _res .updateCharacterStream( columnLabel, reader ,length ) ;} @ Override
    public
        voidupdateObject(StringcolumnLabel, Objectx ,intscaleOrLength
    )

    throwsSQLException
    { _res .updateObject( columnLabel, x ,scaleOrLength ) ;
    }
        @OverridepublicvoidupdateObject( StringcolumnLabel,
    Object

    x)
    throws SQLException {_res. updateObject (
    columnLabel
        ,x);}@
    Override

    publicvoid
    insertRow ( )throwsSQLException { _res
    .
        insertRow();}@
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
        moveToCurrentRow ();}@Override
    public

    StatementgetStatement
    ( ) throwsSQLException{ return_res .getStatement() ;}@Overridepublic ObjectgetObject ( int
    columnIndex
        , Map<String,Class< ?>>
    map

    )throws
    SQLException { return_res. getObject( columnIndex ,
    map
        ) ;}@OverridepublicRefgetRef
    (

    intcolumnIndex
    ) throws SQLException{return _res. getRef (
    columnIndex
        ) ;}@OverridepublicBlobgetBlob
    (

    intcolumnIndex
    ) throws SQLException{return _res. getBlob (
    columnIndex
        ) ;}@OverridepublicClobgetClob
    (

    intcolumnIndex
    ) throws SQLException{return _res. getClob (
    columnIndex
        ) ;}@OverridepublicArraygetArray
    (

    intcolumnIndex
    ) throws SQLException{return _res. getArray(columnIndex) ;}@Overridepublic ObjectgetObject ( String
    columnLabel
        , Map<String,Class< ?>>
    map

    )throws
    SQLException { return_res. getObject( columnLabel ,
    map
        ) ;}@OverridepublicRefgetRef
    (

    StringcolumnLabel
    ) throws SQLException{return _res. getRef (
    columnLabel
        ) ;}@OverridepublicBlobgetBlob
    (

    StringcolumnLabel
    ) throws SQLException{return _res. getBlob (
    columnLabel
        ) ;}@OverridepublicClobgetClob
    (

    StringcolumnLabel
    ) throws SQLException{return _res. getClob (
    columnLabel
        ) ;}@OverridepublicArraygetArray
    (

    StringcolumnLabel
    ) throws SQLException{return _res. getArray (columnLabel ) ;
    }
        @ OverridepublicDategetDate(int columnIndex,Calendar
    cal

    )throws
    SQLException { return_res. getDate( columnIndex ,cal ) ;
    }
        @ OverridepublicDategetDate(String columnLabel,Calendar
    cal

    )throws
    SQLException { return_res. getDate( columnLabel ,cal ) ;
    }
        @ OverridepublicTimegetTime(int columnIndex,Calendar
    cal

    )throws
    SQLException { return_res. getTime( columnIndex ,cal ) ;
    }
        @ OverridepublicTimegetTime(String columnLabel,Calendar
    cal

    )throws
    SQLException { return_res. getTime( columnLabel ,cal ) ;
    }
        @ OverridepublicTimestampgetTimestamp(int columnIndex,Calendar
    cal

    )throws
    SQLException { return_res. getTimestamp( columnIndex ,cal ) ;
    }
        @ OverridepublicTimestampgetTimestamp(String columnLabel,Calendar
    cal

    )throws
    SQLException { return_res. getTimestamp( columnLabel ,
    cal
        ) ;}@OverridepublicURLgetURL
    (

    intcolumnIndex
    ) throws SQLException{return _res. getURL (
    columnIndex
        ) ;}@OverridepublicURLgetURL
    (

    StringcolumnLabel
    ) throws SQLException{return _res. getURL (columnLabel ) ;
    }
        @OverridepublicvoidupdateRef( intcolumnIndex,
    Ref

    x)
    throws SQLException {_res. updateRef( columnIndex ,x ) ;
    }
        @OverridepublicvoidupdateRef( StringcolumnLabel,
    Ref

    x)
    throws SQLException {_res. updateRef( columnLabel ,x ) ;
    }
        @OverridepublicvoidupdateBlob( intcolumnIndex,
    Blob

    x)
    throws SQLException {_res. updateBlob( columnIndex ,x ) ;
    }
        @OverridepublicvoidupdateBlob( StringcolumnLabel,
    Blob

    x)
    throws SQLException {_res. updateBlob( columnLabel ,x ) ;
    }
        @OverridepublicvoidupdateClob( intcolumnIndex,
    Clob

    x)
    throws SQLException {_res. updateClob( columnIndex ,x ) ;
    }
        @OverridepublicvoidupdateClob( StringcolumnLabel,
    Clob

    x)
    throws SQLException {_res. updateClob( columnLabel ,x ) ;
    }
        @OverridepublicvoidupdateArray( intcolumnIndex,
    Array

    x)
    throws SQLException {_res. updateArray( columnIndex ,x ) ;
    }
        @OverridepublicvoidupdateArray( StringcolumnLabel,
    Array

    x)
    throws SQLException {_res. updateArray( columnLabel ,
    x
        ) ;}@OverridepublicRowIdgetRowId
    (

    intcolumnIndex
    ) throws SQLException{return _res. getRowId (
    columnIndex
        ) ;}@OverridepublicRowIdgetRowId
    (

    StringcolumnLabel
    ) throws SQLException{return _res. getRowId (columnLabel ) ;
    }
        @OverridepublicvoidupdateRowId( intcolumnIndex,
    RowId

    x)
    throws SQLException {_res. updateRowId( columnIndex ,x ) ;
    }
        @OverridepublicvoidupdateRowId( StringcolumnLabel,
    RowId

    x)
    throws SQLException {_res. updateRowId (
    columnLabel
        , x);}@Override
    public

    intgetHoldability
    ( ) throwsSQLException{ return _res
    .
        getHoldability ();}@Override
    public

    booleanisClosed
    ( ) throwsSQLException{ return_res . isClosed( ) ;
    }
        @OverridepublicvoidupdateNString( intcolumnIndex,
    String

    nString)
    throws SQLException {_res. updateNString( columnIndex ,nString ) ;
    }
        @OverridepublicvoidupdateNString( StringcolumnLabel,
    String

    nString)
    throws SQLException {_res. updateNString( columnLabel ,nString ) ;
    }
        @OverridepublicvoidupdateNClob( intcolumnIndex,
    NClob

    nClob)
    throws SQLException {_res. updateNClob( columnIndex ,nClob ) ;
    }
        @OverridepublicvoidupdateNClob( StringcolumnLabel,
    NClob

    nClob)
    throws SQLException {_res. updateNClob( columnLabel ,
    nClob
        ) ;}@OverridepublicNClobgetNClob
    (

    intcolumnIndex
    ) throws SQLException{return _res. getNClob (
    columnIndex
        ) ;}@OverridepublicNClobgetNClob
    (

    StringcolumnLabel
    ) throws SQLException{return _res. getNClob (
    columnLabel
        ) ;}@OverridepublicSQLXMLgetSQLXML
    (

    intcolumnIndex
    ) throws SQLException{return _res. getSQLXML (
    columnIndex
        ) ;}@OverridepublicSQLXMLgetSQLXML
    (

    StringcolumnLabel
    ) throws SQLException{return _res. getSQLXML (columnLabel ) ;
    }
        @OverridepublicvoidupdateSQLXML( intcolumnIndex,
    SQLXML

    xmlObject)
    throws SQLException {_res. updateSQLXML( columnIndex ,xmlObject ) ;
    }
        @OverridepublicvoidupdateSQLXML( StringcolumnLabel,
    SQLXML

    xmlObject)
    throws SQLException {_res. updateSQLXML( columnLabel ,
    xmlObject
        ) ;}@OverridepublicStringgetNString
    (

    intcolumnIndex
    ) throws SQLException{return _res. getNString (
    columnIndex
        ) ;}@OverridepublicStringgetNString
    (

    StringcolumnLabel
    ) throws SQLException{return _res. getNString (
    columnLabel
        ) ;}@OverridepublicReadergetNCharacterStream
    (

    intcolumnIndex
    ) throws SQLException{return _res. getNCharacterStream (
    columnIndex
        ) ;}@OverridepublicReadergetNCharacterStream
    (

    StringcolumnLabel
    ) throws SQLException{return _res. getNCharacterStream (columnLabel ) ;} @ Override
    public
        voidupdateNCharacterStream(intcolumnIndex, Readerx ,longlength
    )

    throwsSQLException
    { _res .updateNCharacterStream( columnIndex, x ,length ) ;} @ Override
    public
        voidupdateNCharacterStream(StringcolumnLabel, Readerreader ,longlength
    )

    throwsSQLException
    { _res .updateNCharacterStream( columnLabel, reader ,length ) ;} @ Override
    public
        voidupdateAsciiStream(intcolumnIndex, InputStreamx ,longlength
    )

    throwsSQLException
    { _res .updateAsciiStream( columnIndex, x ,length ) ;} @ Override
    public
        voidupdateBinaryStream(intcolumnIndex, InputStreamx ,longlength
    )

    throwsSQLException
    { _res .updateBinaryStream( columnIndex, x ,length ) ;} @ Override
    public
        voidupdateCharacterStream(intcolumnIndex, Readerx ,longlength
    )

    throwsSQLException
    { _res .updateCharacterStream( columnIndex, x ,length ) ;} @ Override
    public
        voidupdateAsciiStream(StringcolumnLabel, InputStreamx ,longlength
    )

    throwsSQLException
    { _res .updateAsciiStream( columnLabel, x ,length ) ;} @ Override
    public
        voidupdateBinaryStream(StringcolumnLabel, InputStreamx ,longlength
    )

    throwsSQLException
    { _res .updateBinaryStream( columnLabel, x ,length ) ;} @ Override
    public
        voidupdateCharacterStream(StringcolumnLabel, Readerreader ,longlength
    )

    throwsSQLException
    { _res .updateCharacterStream( columnLabel, reader ,length ) ;} @ Override
    public
        voidupdateBlob(intcolumnIndex, InputStreaminputStream ,longlength
    )

    throwsSQLException
    { _res .updateBlob( columnIndex, inputStream ,length ) ;} @ Override
    public
        voidupdateBlob(StringcolumnLabel, InputStreaminputStream ,longlength
    )

    throwsSQLException
    { _res .updateBlob( columnLabel, inputStream ,length ) ;} @ Override
    public
        voidupdateClob(intcolumnIndex, Readerreader ,longlength
    )

    throwsSQLException
    { _res .updateClob( columnIndex, reader ,length ) ;} @ Override
    public
        voidupdateClob(StringcolumnLabel, Readerreader ,longlength
    )

    throwsSQLException
    { _res .updateClob( columnLabel, reader ,length ) ;} @ Override
    public
        voidupdateNClob(intcolumnIndex, Readerreader ,longlength
    )

    throwsSQLException
    { _res .updateNClob( columnIndex, reader ,length ) ;} @ Override
    public
        voidupdateNClob(StringcolumnLabel, Readerreader ,longlength
    )

    throwsSQLException
    { _res .updateNClob( columnLabel, reader ,length ) ;
    }
        @OverridepublicvoidupdateNCharacterStream( intcolumnIndex,
    Reader

    x)
    throws SQLException {_res. updateNCharacterStream( columnIndex ,x ) ;
    }
        @OverridepublicvoidupdateNCharacterStream( StringcolumnLabel,
    Reader

    reader)
    throws SQLException {_res. updateNCharacterStream( columnLabel ,reader ) ;
    }
        @OverridepublicvoidupdateAsciiStream( intcolumnIndex,
    InputStream

    x)
    throws SQLException {_res. updateAsciiStream( columnIndex ,x ) ;
    }
        @OverridepublicvoidupdateBinaryStream( intcolumnIndex,
    InputStream

    x)
    throws SQLException {_res. updateBinaryStream( columnIndex ,x ) ;
    }
        @OverridepublicvoidupdateCharacterStream( intcolumnIndex,
    Reader

    x)
    throws SQLException {_res. updateCharacterStream( columnIndex ,x ) ;
    }
        @OverridepublicvoidupdateAsciiStream( StringcolumnLabel,
    InputStream

    x)
    throws SQLException {_res. updateAsciiStream( columnLabel ,x ) ;
    }
        @OverridepublicvoidupdateBinaryStream( StringcolumnLabel,
    InputStream

    x)
    throws SQLException {_res. updateBinaryStream( columnLabel ,x ) ;
    }
        @OverridepublicvoidupdateCharacterStream( StringcolumnLabel,
    Reader

    reader)
    throws SQLException {_res. updateCharacterStream( columnLabel ,reader ) ;
    }
        @OverridepublicvoidupdateBlob( intcolumnIndex,
    InputStream

    inputStream)
    throws SQLException {_res. updateBlob( columnIndex ,inputStream ) ;
    }
        @OverridepublicvoidupdateBlob( StringcolumnLabel,
    InputStream

    inputStream)
    throws SQLException {_res. updateBlob( columnLabel ,inputStream ) ;
    }
        @OverridepublicvoidupdateClob( intcolumnIndex,
    Reader

    reader)
    throws SQLException {_res. updateClob( columnIndex ,reader ) ;
    }
        @OverridepublicvoidupdateClob( StringcolumnLabel,
    Reader

    reader)
    throws SQLException {_res. updateClob( columnLabel ,reader ) ;
    }
        @OverridepublicvoidupdateNClob( intcolumnIndex,
    Reader

    reader)
    throws SQLException {_res. updateNClob( columnIndex ,reader ) ;
    }
        @OverridepublicvoidupdateNClob( StringcolumnLabel,
    Reader

    reader)
    throws SQLException{_res . updateNClob(columnLabel ,reader );}@ Overridepublic < T
    >
        T getObject(intcolumnIndex,Class <T>
    type

    )throws
    SQLException {return_res . getObject(columnIndex ,type );}@ Overridepublic < T
    >
        T getObject(StringcolumnLabel,Class <T>
    type

    )throws
    SQLException { return_res. getObject( columnLabel ,type ) ;} @ Overridepublic void updateObject
    (
        intcolumnIndex,Objectx, SQLTypetargetSqlType ,int scaleOrLength)throws
    SQLException

    {_res
    . updateObject (columnIndex, x, targetSqlType ,scaleOrLength ) ;} @ Overridepublic void updateObject
    (
        StringcolumnLabel,Objectx, SQLTypetargetSqlType ,int scaleOrLength)throws
    SQLException

    {_res
    . updateObject (columnLabel, x, targetSqlType ,scaleOrLength ) ;} @ Override
    public
        voidupdateObject(intcolumnIndex, Objectx ,SQLTypetargetSqlType
    )

    throwsSQLException
    { _res .updateObject( columnIndex, x ,targetSqlType ) ;} @ Override
    public
        voidupdateObject(StringcolumnLabel, Objectx ,SQLTypetargetSqlType
    )

    throwsSQLException
    { _res.updateObject ( columnLabel,x,targetSqlType) ;} @ Override
    public
        < T>Tunwrap(Class<
    T

    >iface
    ) throws SQLException{return_res.unwrap (iface ) ;
    }
        @ OverridepublicbooleanisWrapperFor(Class<
    ?

>
