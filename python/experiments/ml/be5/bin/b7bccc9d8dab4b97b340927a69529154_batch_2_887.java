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
    publicvoid updateLong (int columnIndex ,
    long
        x)throwsSQLException{_res .updateLong(
    columnIndex

    ,x
    ) ; }@Override publicvoid updateFloat (int columnIndex ,
    float
        x)throwsSQLException{_res .updateFloat(
    columnIndex

    ,x
    ) ; }@Override publicvoid updateDouble (int columnIndex ,
    double
        x)throwsSQLException{_res .updateDouble(
    columnIndex

    ,x
    ) ; }@Override publicvoid updateBigDecimal (int columnIndex ,
    BigDecimal
        x)throwsSQLException{_res .updateBigDecimal(
    columnIndex

    ,x
    ) ; }@Override publicvoid updateString (int columnIndex ,
    String
        x)throwsSQLException{_res .updateString(
    columnIndex

    ,x
    ) ; }@Override publicvoid updateBytes(int columnIndex, byte [
    ]
        x)throwsSQLException{_res .updateBytes(
    columnIndex

    ,x
    ) ; }@Override publicvoid updateDate (int columnIndex ,
    Date
        x)throwsSQLException{_res .updateDate(
    columnIndex

    ,x
    ) ; }@Override publicvoid updateTime (int columnIndex ,
    Time
        x)throwsSQLException{_res .updateTime(
    columnIndex

    ,x
    ) ; }@Override publicvoid updateTimestamp (int columnIndex ,
    Timestamp
        x)throwsSQLException{_res .updateTimestamp(
    columnIndex

    ,x
    ) ; }@Override publicvoid updateAsciiStream (int columnIndex ,InputStream x ,
    int
        length)throwsSQLException{_res .updateAsciiStream (columnIndex,
    x

    ,length
    ) ; }@Override publicvoid updateBinaryStream (int columnIndex ,InputStream x ,
    int
        length)throwsSQLException{_res .updateBinaryStream (columnIndex,
    x

    ,length
    ) ; }@Override publicvoid updateCharacterStream (int columnIndex ,Reader x ,
    int
        length)throwsSQLException{_res .updateCharacterStream (columnIndex,
    x

    ,length
    ) ; }@Override publicvoid updateObject (int columnIndex ,Object x ,
    int
        scaleOrLength)throwsSQLException{_res .updateObject (columnIndex,
    x

    ,scaleOrLength
    ) ; }@Override publicvoid updateObject (int columnIndex ,
    Object
        x)throwsSQLException{_res .updateObject(
    columnIndex

    ,x
    ) ; }@Override publicvoid updateNull (
    String
        columnLabel)throwsSQLException{_res.
    updateNull

    (columnLabel
    ) ; }@Override publicvoid updateBoolean (String columnLabel ,
    boolean
        x)throwsSQLException{_res .updateBoolean(
    columnLabel

    ,x
    ) ; }@Override publicvoid updateByte (String columnLabel ,
    byte
        x)throwsSQLException{_res .updateByte(
    columnLabel

    ,x
    ) ; }@Override publicvoid updateShort (String columnLabel ,
    short
        x)throwsSQLException{_res .updateShort(
    columnLabel

    ,x
    ) ; }@Override publicvoid updateInt (String columnLabel ,
    int
        x)throwsSQLException{_res .updateInt(
    columnLabel

    ,x
    ) ; }@Override publicvoid updateLong (String columnLabel ,
    long
        x)throwsSQLException{_res .updateLong(
    columnLabel

    ,x
    ) ; }@Override publicvoid updateFloat (String columnLabel ,
    float
        x)throwsSQLException{_res .updateFloat(
    columnLabel

    ,x
    ) ; }@Override publicvoid updateDouble (String columnLabel ,
    double
        x)throwsSQLException{_res .updateDouble(
    columnLabel

    ,x
    ) ; }@Override publicvoid updateBigDecimal (String columnLabel ,
    BigDecimal
        x)throwsSQLException{_res .updateBigDecimal(
    columnLabel

    ,x
    ) ; }@Override publicvoid updateString (String columnLabel ,
    String
        x)throwsSQLException{_res .updateString(
    columnLabel

    ,x
    ) ; }@Override publicvoid updateBytes(String columnLabel, byte [
    ]
        x)throwsSQLException{_res .updateBytes(
    columnLabel

    ,x
    ) ; }@Override publicvoid updateDate (String columnLabel ,
    Date
        x)throwsSQLException{_res .updateDate(
    columnLabel

    ,x
    ) ; }@Override publicvoid updateTime (String columnLabel ,
    Time
        x)throwsSQLException{_res .updateTime(
    columnLabel

    ,x
    ) ; }@Override publicvoid updateTimestamp (String columnLabel ,
    Timestamp
        x)throwsSQLException{_res .updateTimestamp(
    columnLabel

    ,x
    ) ; }@Override publicvoid updateAsciiStream (String columnLabel ,InputStream x ,
    int
        length)throwsSQLException{_res .updateAsciiStream (columnLabel,
    x

    ,length
    ) ; }@Override publicvoid updateBinaryStream (String columnLabel ,InputStream x ,
    int
        length)throwsSQLException{_res .updateBinaryStream (columnLabel,
    x

    ,length
    ) ; }@Override publicvoid updateCharacterStream (String columnLabel ,Reader reader ,
    int
        length)throwsSQLException{_res .updateCharacterStream (columnLabel,
    reader

    ,length
    ) ; }@Override publicvoid updateObject (String columnLabel ,Object x ,
    int
        scaleOrLength)throwsSQLException{_res .updateObject (columnLabel,
    x

    ,scaleOrLength
    ) ; }@Override publicvoid updateObject (String columnLabel ,
    Object
        x)throwsSQLException{_res .updateObject(
    columnLabel

    ,x
    ) ; }@Override public void
    insertRow
        ()throwsSQLException{_res
    .

    insertRow(
    ) ; }@Override public void
    updateRow
        ()throwsSQLException{_res
    .

    updateRow(
    ) ; }@Override public void
    deleteRow
        ()throwsSQLException{_res
    .

    deleteRow(
    ) ; }@Override public void
    refreshRow
        ()throwsSQLException{_res
    .

    refreshRow(
    ) ; }@Override public void
    cancelRowUpdates
        ()throwsSQLException{_res
    .

    cancelRowUpdates(
    ) ; }@Override public void
    moveToInsertRow
        ()throwsSQLException{_res
    .

    moveToInsertRow(
    ) ; }@Override public void
    moveToCurrentRow
        ()throwsSQLException{_res
    .

    moveToCurrentRow(
    ) ; }@Override public Statement
    getStatement
        ( )throwsSQLException{return_res
    .

    getStatement(
    ) ; }@Override publicObject getObject(intcolumnIndex ,Map<String, Class< ? >
    >
        map )throwsSQLException{return_res .getObject(
    columnIndex

    ,map
    ) ; }@Override publicRef getRef (
    int
        columnIndex )throwsSQLException{return_res.
    getRef

    (columnIndex
    ) ; }@Override publicBlob getBlob (
    int
        columnIndex )throwsSQLException{return_res.
    getBlob

    (columnIndex
    ) ; }@Override publicClob getClob (
    int
        columnIndex )throwsSQLException{return_res.
    getClob

    (columnIndex
    ) ; }@Override publicArray getArray (
    int
        columnIndex )throwsSQLException{return_res.
    getArray

    (columnIndex
    ) ; }@Override publicObject getObject(StringcolumnLabel ,Map<String, Class< ? >
    >
        map )throwsSQLException{return_res .getObject(
    columnLabel

    ,map
    ) ; }@Override publicRef getRef (
    String
        columnLabel )throwsSQLException{return_res.
    getRef

    (columnLabel
    ) ; }@Override publicBlob getBlob (
    String
        columnLabel )throwsSQLException{return_res.
    getBlob

    (columnLabel
    ) ; }@Override publicClob getClob (
    String
        columnLabel )throwsSQLException{return_res.
    getClob

    (columnLabel
    ) ; }@Override publicArray getArray (
    String
        columnLabel )throwsSQLException{return_res.
    getArray

    (columnLabel
    ) ; }@Override publicDate getDate (int columnIndex ,
    Calendar
        cal )throwsSQLException{return_res .getDate(
    columnIndex

    ,cal
    ) ; }@Override publicDate getDate (String columnLabel ,
    Calendar
        cal )throwsSQLException{return_res .getDate(
    columnLabel

    ,cal
    ) ; }@Override publicTime getTime (int columnIndex ,
    Calendar
        cal )throwsSQLException{return_res .getTime(
    columnIndex

    ,cal
    ) ; }@Override publicTime getTime (String columnLabel ,
    Calendar
        cal )throwsSQLException{return_res .getTime(
    columnLabel

    ,cal
    ) ; }@Override publicTimestamp getTimestamp (int columnIndex ,
    Calendar
        cal )throwsSQLException{return_res .getTimestamp(
    columnIndex

    ,cal
    ) ; }@Override publicTimestamp getTimestamp (String columnLabel ,
    Calendar
        cal )throwsSQLException{return_res .getTimestamp(
    columnLabel

    ,cal
    ) ; }@Override publicURL getURL (
    int
        columnIndex )throwsSQLException{return_res.
    getURL

    (columnIndex
    ) ; }@Override publicURL getURL (
    String
        columnLabel )throwsSQLException{return_res.
    getURL

    (columnLabel
    ) ; }@Override publicvoid updateRef (int columnIndex ,
    Ref
        x)throwsSQLException{_res .updateRef(
    columnIndex

    ,x
    ) ; }@Override publicvoid updateRef (String columnLabel ,
    Ref
        x)throwsSQLException{_res .updateRef(
    columnLabel

    ,x
    ) ; }@Override publicvoid updateBlob (int columnIndex ,
    Blob
        x)throwsSQLException{_res .updateBlob(
    columnIndex

    ,x
    ) ; }@Override publicvoid updateBlob (String columnLabel ,
    Blob
        x)throwsSQLException{_res .updateBlob(
    columnLabel

    ,x
    ) ; }@Override publicvoid updateClob (int columnIndex ,
    Clob
        x)throwsSQLException{_res .updateClob(
    columnIndex

    ,x
    ) ; }@Override publicvoid updateClob (String columnLabel ,
    Clob
        x)throwsSQLException{_res .updateClob(
    columnLabel

    ,x
    ) ; }@Override publicvoid updateArray (int columnIndex ,
    Array
        x)throwsSQLException{_res .updateArray(
    columnIndex

    ,x
    ) ; }@Override publicvoid updateArray (String columnLabel ,
    Array
        x)throwsSQLException{_res .updateArray(
    columnLabel

    ,x
    ) ; }@Override publicRowId getRowId (
    int
        columnIndex )throwsSQLException{return_res.
    getRowId

    (columnIndex
    ) ; }@Override publicRowId getRowId (
    String
        columnLabel )throwsSQLException{return_res.
    getRowId

    (columnLabel
    ) ; }@Override publicvoid updateRowId (int columnIndex ,
    RowId
        x)throwsSQLException{_res .updateRowId(
    columnIndex

    ,x
    ) ; }@Override publicvoid updateRowId (String columnLabel ,
    RowId
        x)throwsSQLException{_res .updateRowId(
    columnLabel

    ,x
    ) ; }@Override public int
    getHoldability
        ( )throwsSQLException{return_res
    .

    getHoldability(
    ) ; }@Override public boolean
    isClosed
        ( )throwsSQLException{return_res
    .

    isClosed(
    ) ; }@Override publicvoid updateNString (int columnIndex ,
    String
        nString)throwsSQLException{_res .updateNString(
    columnIndex

    ,nString
    ) ; }@Override publicvoid updateNString (String columnLabel ,
    String
        nString)throwsSQLException{_res .updateNString(
    columnLabel

    ,nString
    ) ; }@Override publicvoid updateNClob (int columnIndex ,
    NClob
        nClob)throwsSQLException{_res .updateNClob(
    columnIndex

    ,nClob
    ) ; }@Override publicvoid updateNClob (String columnLabel ,
    NClob
        nClob)throwsSQLException{_res .updateNClob(
    columnLabel

    ,nClob
    ) ; }@Override publicNClob getNClob (
    int
        columnIndex )throwsSQLException{return_res.
    getNClob

    (columnIndex
    ) ; }@Override publicNClob getNClob (
    String
        columnLabel )throwsSQLException{return_res.
    getNClob

    (columnLabel
    ) ; }@Override publicSQLXML getSQLXML (
    int
        columnIndex )throwsSQLException{return_res.
    getSQLXML

    (columnIndex
    ) ; }@Override publicSQLXML getSQLXML (
    String
        columnLabel )throwsSQLException{return_res.
    getSQLXML

    (columnLabel
    ) ; }@Override publicvoid updateSQLXML (int columnIndex ,
    SQLXML
        xmlObject)throwsSQLException{_res .updateSQLXML(
    columnIndex

    ,xmlObject
    ) ; }@Override publicvoid updateSQLXML (String columnLabel ,
    SQLXML
        xmlObject)throwsSQLException{_res .updateSQLXML(
    columnLabel

    ,xmlObject
    ) ; }@Override publicString getNString (
    int
        columnIndex )throwsSQLException{return_res.
    getNString

    (columnIndex
    ) ; }@Override publicString getNString (
    String
        columnLabel )throwsSQLException{return_res.
    getNString

    (columnLabel
    ) ; }@Override publicReader getNCharacterStream (
    int
        columnIndex )throwsSQLException{return_res.
    getNCharacterStream

    (columnIndex
    ) ; }@Override publicReader getNCharacterStream (
    String
        columnLabel )throwsSQLException{return_res.
    getNCharacterStream

    (columnLabel
    ) ; }@Override publicvoid updateNCharacterStream (int columnIndex ,Reader x ,
    long
        length)throwsSQLException{_res .updateNCharacterStream (columnIndex,
    x

    ,length
    ) ; }@Override publicvoid updateNCharacterStream (String columnLabel ,Reader reader ,
    long
        length)throwsSQLException{_res .updateNCharacterStream (columnLabel,
    reader

    ,length
    ) ; }@Override publicvoid updateAsciiStream (int columnIndex ,InputStream x ,
    long
        length)throwsSQLException{_res .updateAsciiStream (columnIndex,
    x

    ,length
    ) ; }@Override publicvoid updateBinaryStream (int columnIndex ,InputStream x ,
    long
        length)throwsSQLException{_res .updateBinaryStream (columnIndex,
    x

    ,length
    ) ; }@Override publicvoid updateCharacterStream (int columnIndex ,Reader x ,
    long
        length)throwsSQLException{_res .updateCharacterStream (columnIndex,
    x

    ,length
    ) ; }@Override publicvoid updateAsciiStream (String columnLabel ,InputStream x ,
    long
        length)throwsSQLException{_res .updateAsciiStream (columnLabel,
    x

    ,length
    ) ; }@Override publicvoid updateBinaryStream (String columnLabel ,InputStream x ,
    long
        length)throwsSQLException{_res .updateBinaryStream (columnLabel,
    x

    ,length
    ) ; }@Override publicvoid updateCharacterStream (String columnLabel ,Reader reader ,
    long
        length)throwsSQLException{_res .updateCharacterStream (columnLabel,
    reader

    ,length
    ) ; }@Override publicvoid updateBlob (int columnIndex ,InputStream inputStream ,
    long
        length)throwsSQLException{_res .updateBlob (columnIndex,
    inputStream

    ,length
    ) ; }@Override publicvoid updateBlob (String columnLabel ,InputStream inputStream ,
    long
        length)throwsSQLException{_res .updateBlob (columnLabel,
    inputStream

    ,length
    ) ; }@Override publicvoid updateClob (int columnIndex ,Reader reader ,
    long
        length)throwsSQLException{_res .updateClob (columnIndex,
    reader

    ,length
    ) ; }@Override publicvoid updateClob (String columnLabel ,Reader reader ,
    long
        length)throwsSQLException{_res .updateClob (columnLabel,
    reader

    ,length
    ) ; }@Override publicvoid updateNClob (int columnIndex ,Reader reader ,
    long
        length)throwsSQLException{_res .updateNClob (columnIndex,
    reader

    ,length
    ) ; }@Override publicvoid updateNClob (String columnLabel ,Reader reader ,
    long
        length)throwsSQLException{_res .updateNClob (columnLabel,
    reader

    ,length
    ) ; }@Override publicvoid updateNCharacterStream (int columnIndex ,
    Reader
        x)throwsSQLException{_res .updateNCharacterStream(
    columnIndex

    ,x
    ) ; }@Override publicvoid updateNCharacterStream (String columnLabel ,
    Reader
        reader)throwsSQLException{_res .updateNCharacterStream(
    columnLabel

    ,reader
    ) ; }@Override publicvoid updateAsciiStream (int columnIndex ,
    InputStream
        x)throwsSQLException{_res .updateAsciiStream(
    columnIndex

    ,x
    ) ; }@Override publicvoid updateBinaryStream (int columnIndex ,
    InputStream
        x)throwsSQLException{_res .updateBinaryStream(
    columnIndex

    ,x
    ) ; }@Override publicvoid updateCharacterStream (int columnIndex ,
    Reader
        x)throwsSQLException{_res .updateCharacterStream(
    columnIndex

    ,x
    ) ; }@Override publicvoid updateAsciiStream (String columnLabel ,
    InputStream
        x)throwsSQLException{_res .updateAsciiStream(
    columnLabel

    ,x
    ) ; }@Override publicvoid updateBinaryStream (String columnLabel ,
    InputStream
        x)throwsSQLException{_res .updateBinaryStream(
    columnLabel

    ,x
    ) ; }@Override publicvoid updateCharacterStream (String columnLabel ,
    Reader
        reader)throwsSQLException{_res .updateCharacterStream(
    columnLabel

    ,reader
    ) ; }@Override publicvoid updateBlob (int columnIndex ,
    InputStream
        inputStream)throwsSQLException{_res .updateBlob(
    columnIndex

    ,inputStream
    ) ; }@Override publicvoid updateBlob (String columnLabel ,
    InputStream
        inputStream)throwsSQLException{_res .updateBlob(
    columnLabel

    ,inputStream
    ) ; }@Override publicvoid updateClob (int columnIndex ,
    Reader
        reader)throwsSQLException{_res .updateClob(
    columnIndex

    ,reader
    ) ; }@Override publicvoid updateClob (String columnLabel ,
    Reader
        reader)throwsSQLException{_res .updateClob(
    columnLabel

    ,reader
    ) ; }@Override publicvoid updateNClob (int columnIndex ,
    Reader
        reader)throwsSQLException{_res .updateNClob(
    columnIndex

    ,reader
    ) ; }@Override publicvoid updateNClob (String columnLabel ,
    Reader
        reader)throwsSQLException{_res .updateNClob(
    columnLabel

    ,reader
    ) ;}@ Override public<T >T getObject(intcolumnIndex ,Class < T
    >
        type )throwsSQLException{return_res .getObject(
    columnIndex

    ,type
    ) ;}@ Override public<T >T getObject(StringcolumnLabel ,Class < T
    >
        type )throwsSQLException{return_res .getObject(
    columnLabel

    ,type
    ) ; }@Override publicvoid updateObject (int columnIndex ,Object x ,SQLType targetSqlType ,
    int
        scaleOrLength)throwsSQLException{_res .updateObject (columnIndex ,x,
    targetSqlType

    ,scaleOrLength
    ) ; }@Override publicvoid updateObject (String columnLabel ,Object x ,SQLType targetSqlType ,
    int
        scaleOrLength)throwsSQLException{_res .updateObject (columnLabel ,x,
    targetSqlType

    ,scaleOrLength
    ) ; }@Override publicvoid updateObject (int columnIndex ,Object x ,
    SQLType
        targetSqlType)throwsSQLException{_res .updateObject (columnIndex,
    x

    ,targetSqlType
    ) ; }@Override publicvoid updateObject (String columnLabel ,Object x ,
    SQLType
        targetSqlType)throwsSQLException{_res .updateObject (columnLabel,
    x

    ,targetSqlType
    ) ;}@ Override public<T>Tunwrap (Class < T
    >
        iface )throwsSQLException{return_res.
    unwrap

    (iface
    ) ; }@OverridepublicbooleanisWrapperFor (Class < ?
    >
        iface )throwsSQLException{return_res.
    isWrapperFor

(
