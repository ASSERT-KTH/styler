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

    @Override
    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException
    {
        _res.updateBigDecimal(columnLabel, x);
    }

    @Override
    public void updateString(String columnLabel, String x) throws SQLException
    {
        _res.updateString(columnLabel, x);
    }

    @Override
    public void updateBytes(String columnLabel, byte[] x) throws SQLException
    {
        _res.updateBytes(columnLabel, x);
    }

    @Override
    public void updateDate(String columnLabel, Date x) throws SQLException
    {
        _res.updateDate(columnLabel, x);
    }

    @Override
    public void updateTime(String columnLabel, Time x) throws SQLException
    {
        _res.updateTime(columnLabel, x);
    }

    @Override
    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException
    {
        _res.updateTimestamp(columnLabel, x);
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException
    {
        _res.updateAsciiStream(columnLabel, x, length);
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException
    {
        _res.updateBinaryStream(columnLabel, x, length);
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException
    {
        _res.updateCharacterStream(columnLabel, reader, length);
    }

    @Override
    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException
    {
        _res.updateObject(columnLabel, x, scaleOrLength);
    }

    @Override
    public void updateObject(String columnLabel, Object x) throws SQLException
    {
        _res.updateObject(columnLabel, x);
    }

    @Override
    public void insertRow() throws SQLException
    {
        _res.insertRow();
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
        return _res.getStatement();
    }

    @Override
    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException
    {
        return _res.getObject(columnIndex, map);
    }

    @Override
    public Ref getRef(int columnIndex) throws SQLException
    {
        return _res.getRef(columnIndex);
    }

    @Override
    public Blob getBlob(int columnIndex) throws SQLException
    {
        return _res.getBlob(columnIndex);
    }

    @Override
    public Clob getClob(int columnIndex) throws SQLException
    {
        return _res.getClob(columnIndex);
    }

    @Override
    public Array getArray(int columnIndex) throws SQLException
    {
        return _res.getArray(columnIndex);
    }

    @Override
    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException
    {
        return _res.getObject(columnLabel, map);
    }

    @Override
    public Ref getRef(String columnLabel) throws SQLException
    {
        return _res.getRef(columnLabel);
    }

    @Override
    public Blob getBlob(String columnLabel) throws SQLException
    {
        return _res.getBlob(columnLabel);
    }

    @Override
    public Clob getClob(String columnLabel) throws SQLException
    {
        return _res.getClob(columnLabel);
    }

    @Override
    public Array getArray(String columnLabel) throws SQLException
    {
        return _res.getArray(columnLabel);
    }

    @Override
    public Date getDate(int columnIndex, Calendar cal) throws SQLException
    {
        return _res.getDate(columnIndex, cal);
    }

    @Override
    public Date getDate(String columnLabel, Calendar cal) throws SQLException
    {
        return _res.getDate(columnLabel, cal);
    }

    @Override
    public Time getTime(int columnIndex, Calendar cal) throws SQLException
    {
        return _res.getTime(columnIndex, cal);
    }

    @Override
    public Time getTime(String columnLabel, Calendar cal) throws SQLException
    {
        return _res.getTime(columnLabel, cal);
    }

    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException
    {
        return _res.getTimestamp(columnIndex, cal);
    }

    @Override
    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException
    {
        return _res.getTimestamp(columnLabel, cal);
    }

    @Override
    public URL getURL(int columnIndex) throws SQLException
    {
        return _res.getURL(columnIndex);
    }

    @Override
    public URL getURL(String columnLabel) throws SQLException
    {
        return _res.getURL(columnLabel);
    }

    @Override
    public void updateRef(int columnIndex, Ref x) throws SQLException
    {
        _res.updateRef(columnIndex, x);
    }

    @Override
    public void updateRef(String columnLabel, Ref x) throws SQLException
    {
        _res.updateRef(columnLabel, x);
    }

    @Override
    public void updateBlob(int columnIndex, Blob x) throws SQLException
    {
        _res.updateBlob(columnIndex, x);
    }

    @Override
    public void updateBlob(String columnLabel, Blob x) throws SQLException
    {
        _res.updateBlob(columnLabel, x);
    }

    @Override
    public void updateClob(int columnIndex, Clob x) throws SQLException
    {
        _res.updateClob(columnIndex, x);
    }

    @Override
    public void updateClob(String columnLabel, Clob x) throws SQLException
    {
        _res.updateClob(columnLabel, x);
    }

    @Override
    public void updateArray(int columnIndex, Array x) throws SQLException{
    _res
        .updateArray(columnIndex,x );}
    @

    Overridepublic
    void updateArray (StringcolumnLabel ,Array x )throws SQLException {
    _res
        .updateArray(columnLabel,x );}
    @

    Overridepublic
    RowId getRowId (intcolumnIndex )throws SQLException {
    return
        _res .getRowId(columnIndex);}
    @

    Overridepublic
    RowId getRowId (StringcolumnLabel )throws SQLException {
    return
        _res .getRowId(columnLabel);}
    @

    Overridepublic
    void updateRowId (intcolumnIndex ,RowId x )throws SQLException {
    _res
        .updateRowId(columnIndex,x );}
    @

    Overridepublic
    void updateRowId (StringcolumnLabel ,RowId x )throws SQLException {
    _res
        .updateRowId(columnLabel,x );}
    @

    Overridepublic
    int getHoldability ()throws SQLException {
    return
        _res .getHoldability();}
    @

    Overridepublic
    boolean isClosed ()throws SQLException {
    return
        _res .isClosed();}
    @

    Overridepublic
    void updateNString (intcolumnIndex ,String nString )throws SQLException {
    _res
        .updateNString(columnIndex,nString );}
    @

    Overridepublic
    void updateNString (StringcolumnLabel ,String nString )throws SQLException {
    _res
        .updateNString(columnLabel,nString );}
    @

    Overridepublic
    void updateNClob (intcolumnIndex ,NClob nClob )throws SQLException {
    _res
        .updateNClob(columnIndex,nClob );}
    @

    Overridepublic
    void updateNClob (StringcolumnLabel ,NClob nClob )throws SQLException {
    _res
        .updateNClob(columnLabel,nClob );}
    @

    Overridepublic
    NClob getNClob (intcolumnIndex )throws SQLException {
    return
        _res .getNClob(columnIndex);}
    @

    Overridepublic
    NClob getNClob (StringcolumnLabel )throws SQLException {
    return
        _res .getNClob(columnLabel);}
    @

    Overridepublic
    SQLXML getSQLXML (intcolumnIndex )throws SQLException {
    return
        _res .getSQLXML(columnIndex);}
    @

    Overridepublic
    SQLXML getSQLXML (StringcolumnLabel )throws SQLException {
    return
        _res .getSQLXML(columnLabel);}
    @

    Overridepublic
    void updateSQLXML (intcolumnIndex ,SQLXML xmlObject )throws SQLException {
    _res
        .updateSQLXML(columnIndex,xmlObject );}
    @

    Overridepublic
    void updateSQLXML (StringcolumnLabel ,SQLXML xmlObject )throws SQLException {
    _res
        .updateSQLXML(columnLabel,xmlObject );}
    @

    Overridepublic
    String getNString (intcolumnIndex )throws SQLException {
    return
        _res .getNString(columnIndex);}
    @

    Overridepublic
    String getNString (StringcolumnLabel )throws SQLException {
    return
        _res .getNString(columnLabel);}
    @

    Overridepublic
    Reader getNCharacterStream (intcolumnIndex )throws SQLException {
    return
        _res .getNCharacterStream(columnIndex);}
    @

    Overridepublic
    Reader getNCharacterStream (StringcolumnLabel )throws SQLException {
    return
        _res .getNCharacterStream(columnLabel);}
    @

    Overridepublic
    void updateNCharacterStream (intcolumnIndex ,Reader x ,long length )throws SQLException {
    _res
        .updateNCharacterStream(columnIndex,x ,length );}
    @

    Overridepublic
    void updateNCharacterStream (StringcolumnLabel ,Reader reader ,long length )throws SQLException {
    _res
        .updateNCharacterStream(columnLabel,reader ,length );}
    @

    Overridepublic
    void updateAsciiStream (intcolumnIndex ,InputStream x ,long length )throws SQLException {
    _res
        .updateAsciiStream(columnIndex,x ,length );}
    @

    Overridepublic
    void updateBinaryStream (intcolumnIndex ,InputStream x ,long length )throws SQLException {
    _res
        .updateBinaryStream(columnIndex,x ,length );}
    @

    Overridepublic
    void updateCharacterStream (intcolumnIndex ,Reader x ,long length )throws SQLException {
    _res
        .updateCharacterStream(columnIndex,x ,length );}
    @

    Overridepublic
    void updateAsciiStream (StringcolumnLabel ,InputStream x ,long length )throws SQLException {
    _res
        .updateAsciiStream(columnLabel,x ,length );}
    @

    Overridepublic
    void updateBinaryStream (StringcolumnLabel ,InputStream x ,long length )throws SQLException {
    _res
        .updateBinaryStream(columnLabel,x ,length );}
    @

    Overridepublic
    void updateCharacterStream (StringcolumnLabel ,Reader reader ,long length )throws SQLException {
    _res
        .updateCharacterStream(columnLabel,reader ,length );}
    @

    Overridepublic
    void updateBlob (intcolumnIndex ,InputStream inputStream ,long length )throws SQLException {
    _res
        .updateBlob(columnIndex,inputStream ,length );}
    @

    Overridepublic
    void updateBlob (StringcolumnLabel ,InputStream inputStream ,long length )throws SQLException {
    _res
        .updateBlob(columnLabel,inputStream ,length );}
    @

    Overridepublic
    void updateClob (intcolumnIndex ,Reader reader ,long length )throws SQLException {
    _res
        .updateClob(columnIndex,reader ,length );}
    @

    Overridepublic
    void updateClob (StringcolumnLabel ,Reader reader ,long length )throws SQLException {
    _res
        .updateClob(columnLabel,reader ,length );}
    @

    Overridepublic
    void updateNClob (intcolumnIndex ,Reader reader ,long length )throws SQLException {
    _res
        .updateNClob(columnIndex,reader ,length );}
    @

    Overridepublic
    void updateNClob (StringcolumnLabel ,Reader reader ,long length )throws SQLException {
    _res
        .updateNClob(columnLabel,reader ,length );}
    @

    Overridepublic
    void updateNCharacterStream (intcolumnIndex ,Reader x )throws SQLException {
    _res
        .updateNCharacterStream(columnIndex,x );}
    @

    Overridepublic
    void updateNCharacterStream (StringcolumnLabel ,Reader reader )throws SQLException {
    _res
        .updateNCharacterStream(columnLabel,reader );}
    @

    Overridepublic
    void updateAsciiStream (intcolumnIndex ,InputStream x )throws SQLException {
    _res
        .updateAsciiStream(columnIndex,x );}
    @

    Overridepublic
    void updateBinaryStream (intcolumnIndex ,InputStream x )throws SQLException {
    _res
        .updateBinaryStream(columnIndex,x );}
    @

    Overridepublic
    void updateCharacterStream (intcolumnIndex ,Reader x )throws SQLException {
    _res
        .updateCharacterStream(columnIndex,x );}
    @

    Overridepublic
    void updateAsciiStream (StringcolumnLabel ,InputStream x )throws SQLException {
    _res
        .updateAsciiStream(columnLabel,x );}
    @

    Overridepublic
    void updateBinaryStream (StringcolumnLabel ,InputStream x )throws SQLException {
    _res
        .updateBinaryStream(columnLabel,x );}
    @

    Overridepublic
    void updateCharacterStream (StringcolumnLabel ,Reader reader )throws SQLException {
    _res
        .updateCharacterStream(columnLabel,reader );}
    @

    Overridepublic
    void updateBlob (intcolumnIndex ,InputStream inputStream )throws SQLException {
    _res
        .updateBlob(columnIndex,inputStream );}
    @

    Overridepublic
    void updateBlob (StringcolumnLabel ,InputStream inputStream )throws SQLException {
    _res
        .updateBlob(columnLabel,inputStream );}
    @

    Overridepublic
    void updateClob (intcolumnIndex ,Reader reader )throws SQLException {
    _res
        .updateClob(columnIndex,reader );}
    @

    Overridepublic
    void updateClob (StringcolumnLabel ,Reader reader )throws SQLException {
    _res
        .updateClob(columnLabel,reader );}
    @

    Overridepublic
    void updateNClob (intcolumnIndex ,Reader reader )throws SQLException {
    _res
        .updateNClob(columnIndex,reader );}
    @

    Overridepublic
    void updateNClob (StringcolumnLabel ,Reader reader )throws SQLException {
    _res
        .updateNClob(columnLabel,reader );}
    @

    Overridepublic
    < T>T getObject (intcolumnIndex ,Class <T>type )throws SQLException {
    return
        _res .getObject(columnIndex,type );}
    @

    Overridepublic
    < T>T getObject (StringcolumnLabel ,Class <T>type )throws SQLException {
    return
        _res .getObject(columnLabel,type );}
    @

    Overridepublic
    void updateObject (intcolumnIndex ,Object x ,SQLType targetSqlType ,int scaleOrLength )throws SQLException {
    _res
        .updateObject(columnIndex,x ,targetSqlType ,scaleOrLength );}
    @

    Overridepublic
    void updateObject (StringcolumnLabel ,Object x ,SQLType targetSqlType ,int scaleOrLength )throws SQLException {
    _res
        .updateObject(columnLabel,x ,targetSqlType ,scaleOrLength );}
    @

    Overridepublic
    void updateObject (intcolumnIndex ,Object x ,SQLType targetSqlType )throws SQLException {
    _res
        .updateObject(columnIndex,x ,targetSqlType );}
    @

    Overridepublic
    void updateObject (StringcolumnLabel ,Object x ,SQLType targetSqlType )throws SQLException {
    _res
        .updateObject(columnLabel,x ,targetSqlType );}
    @

    Overridepublic
    < T>T unwrap (Class<T>iface )throws SQLException {
    return
        _res .unwrap(iface);}
    @

    Overridepublic
    boolean isWrapperFor (Class<?>iface )throws SQLException {
    return
        _res .isWrapperFor(iface);}
    }

