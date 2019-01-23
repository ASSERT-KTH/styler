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

    @

    Overridepublic
    byte []getBytes (StringcolumnLabel )throws SQLException {
    return
        _res .getBytes(columnLabel);}
    @

    Overridepublic
    Date getDate (StringcolumnLabel )throws SQLException {
    return
        _res .getDate(columnLabel);}
    @

    Overridepublic
    Time getTime (StringcolumnLabel )throws SQLException {
    return
        _res .getTime(columnLabel);}
    @

    Overridepublic
    Timestamp getTimestamp (StringcolumnLabel )throws SQLException {
    return
        _res .getTimestamp(columnLabel);}
    @

    Overridepublic
    InputStream getAsciiStream (StringcolumnLabel )throws SQLException {
    return
        _res .getAsciiStream(columnLabel);}
    @

    Override@
    Deprecatedpublic
    InputStream getUnicodeStream (StringcolumnLabel )throws SQLException {
    return
        _res .getUnicodeStream(columnLabel);}
    @

    Overridepublic
    InputStream getBinaryStream (StringcolumnLabel )throws SQLException {
    return
        _res .getBinaryStream(columnLabel);}
    @

    Overridepublic
    SQLWarning getWarnings ()throws SQLException {
    return
        _res .getWarnings();}
    @

    Overridepublic
    void clearWarnings ()throws SQLException {
    _res
        .clearWarnings();}
    @

    Overridepublic
    String getCursorName ()throws SQLException {
    return
        _res .getCursorName();}
    @

    Overridepublic
    ResultSetMetaData getMetaData ()throws SQLException {
    return
        _res .getMetaData();}
    @

    Overridepublic
    Object getObject (intcolumnIndex )throws SQLException {
    return
        _res .getObject(columnIndex);}
    @

    Overridepublic
    Object getObject (StringcolumnLabel )throws SQLException {
    return
        _res .getObject(columnLabel);}
    @

    Overridepublic
    int findColumn (StringcolumnLabel )throws SQLException {
    return
        _res .findColumn(columnLabel);}
    @

    Overridepublic
    Reader getCharacterStream (intcolumnIndex )throws SQLException {
    return
        _res .getCharacterStream(columnIndex);}
    @

    Overridepublic
    Reader getCharacterStream (StringcolumnLabel )throws SQLException {
    return
        _res .getCharacterStream(columnLabel);}
    @

    Overridepublic
    BigDecimal getBigDecimal (intcolumnIndex )throws SQLException {
    return
        _res .getBigDecimal(columnIndex);}
    @

    Overridepublic
    BigDecimal getBigDecimal (StringcolumnLabel )throws SQLException {
    return
        _res .getBigDecimal(columnLabel);}
    @

    Overridepublic
    boolean isBeforeFirst ()throws SQLException {
    return
        _res .isBeforeFirst();}
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
        .beforeFirst();}
    @

    Overridepublic
    void afterLast ()throws SQLException {
    _res
        .afterLast();}
    @

    Overridepublic
    boolean first ()throws SQLException {
    return
        _res .first();}
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
    boolean absolute (introw )throws SQLException {
    return
        _res .absolute(row);}
    @

    Overridepublic
    boolean relative (introws )throws SQLException {
    return
        _res .relative(rows);}
    @

    Overridepublic
    boolean previous ()throws SQLException {
    return
        _res .previous();}
    @

    Overridepublic
    void setFetchDirection (intdirection )throws SQLException {
    _res
        .setFetchDirection(direction);}
    @

    Overridepublic
    int getFetchDirection ()throws SQLException {
    return
        _res .getFetchDirection();}
    @

    Overridepublic
    void setFetchSize (introws )throws SQLException {
    _res
        .setFetchSize(rows);}
    @

    Overridepublic
    int getFetchSize ()throws SQLException {
    return
        _res .getFetchSize();}
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
    void updateNull (intcolumnIndex )throws SQLException {
    _res
        .updateNull(columnIndex);}
    @

    Overridepublic
    void updateBoolean (intcolumnIndex ,boolean x )throws SQLException {
    _res
        .updateBoolean(columnIndex,x );}
    @

    Overridepublic
    void updateByte (intcolumnIndex ,byte x )throws SQLException {
    _res
        .updateByte(columnIndex,x );}
    @

    Overridepublic
    void updateShort (intcolumnIndex ,short x )throws SQLException {
    _res
        .updateShort(columnIndex,x );}
    @

    Overridepublic
    void updateInt (intcolumnIndex ,int x )throws SQLException {
    _res
        .updateInt(columnIndex,x );}
    @

    Overridepublic
    void updateLong (intcolumnIndex ,long x )throws SQLException {
    _res
        .updateLong(columnIndex,x );}
    @

    Overridepublic
    void updateFloat (intcolumnIndex ,float x )throws SQLException {
    _res
        .updateFloat(columnIndex,x );}
    @

    Overridepublic
    void updateDouble (intcolumnIndex ,double x )throws SQLException {
    _res
        .updateDouble(columnIndex,x );}
    @

    Overridepublic
    void updateBigDecimal (intcolumnIndex ,BigDecimal x )throws SQLException {
    _res
        .updateBigDecimal(columnIndex,x );}
    @

    Overridepublic
    void updateString (intcolumnIndex ,String x )throws SQLException {
    _res
        .updateString(columnIndex,x );}
    @

    Overridepublic
    void updateBytes (intcolumnIndex ,byte []x )throws SQLException {
    _res
        .updateBytes(columnIndex,x );}
    @

    Overridepublic
    void updateDate (intcolumnIndex ,Date x )throws SQLException {
    _res
        .updateDate(columnIndex,x );}
    @

    Overridepublic
    void updateTime (intcolumnIndex ,Time x )throws SQLException {
    _res
        .updateTime(columnIndex,x );}
    @

    Overridepublic
    void updateTimestamp (intcolumnIndex ,Timestamp x )throws SQLException {
    _res
        .updateTimestamp(columnIndex,x );}
    @

    Overridepublic
    void updateAsciiStream (intcolumnIndex ,InputStream x ,int length )throws SQLException {
    _res
        .updateAsciiStream(columnIndex,x ,length );}
    @

    Overridepublic
    void updateBinaryStream (intcolumnIndex ,InputStream x ,int length )throws SQLException {
    _res
        .updateBinaryStream(columnIndex,x ,length );}
    @

    Overridepublic
    void updateCharacterStream (intcolumnIndex ,Reader x ,int length )throws SQLException {
    _res
        .updateCharacterStream(columnIndex,x ,length );}
    @

    Overridepublic
    void updateObject (intcolumnIndex ,Object x ,int scaleOrLength )throws SQLException {
    _res
        .updateObject(columnIndex,x ,scaleOrLength );}
    @

    Overridepublic
    void updateObject (intcolumnIndex ,Object x )throws SQLException {
    _res
        .updateObject(columnIndex,x );}
    @

    Overridepublic
    void updateNull (StringcolumnLabel )throws SQLException {
    _res
        .updateNull(columnLabel);}
    @

    Overridepublic
    void updateBoolean (StringcolumnLabel ,boolean x )throws SQLException {
    _res
        .updateBoolean(columnLabel,x );}
    @

    Overridepublic
    void updateByte (StringcolumnLabel ,byte x )throws SQLException {
    _res
        .updateByte(columnLabel,x );}
    @

    Overridepublic
    void updateShort (StringcolumnLabel ,short x )throws SQLException {
    _res
        .updateShort(columnLabel,x );}
    @

    Overridepublic
    void updateInt (StringcolumnLabel ,int x )throws SQLException {
    _res
        .updateInt(columnLabel,x );}
    @

    Overridepublic
    void updateLong (StringcolumnLabel ,long x )throws SQLException {
    _res
        .updateLong(columnLabel,x );}
    @

    Overridepublic
    void updateFloat (StringcolumnLabel ,float x )throws SQLException {
    _res
        .updateFloat(columnLabel,x );}
    @

    Overridepublic
    void updateDouble (StringcolumnLabel ,double x )throws SQLException {
    _res
        .updateDouble(columnLabel,x );}
    @

    Overridepublic
    void updateBigDecimal (StringcolumnLabel ,BigDecimal x )throws SQLException {
    _res
        .updateBigDecimal(columnLabel,x );}
    @

    Overridepublic
    void updateString (StringcolumnLabel ,String x )throws SQLException {
    _res
        .updateString(columnLabel,x );}
    @

    Overridepublic
    void updateBytes (StringcolumnLabel ,byte []x )throws SQLException {
    _res
        .updateBytes(columnLabel,x );}
    @

    Overridepublic
    void updateDate (StringcolumnLabel ,Date x )throws SQLException {
    _res
        .updateDate(columnLabel,x );}
    @

    Overridepublic
    void updateTime (StringcolumnLabel ,Time x )throws SQLException {
    _res
        .updateTime(columnLabel,x );}
    @

    Overridepublic
    void updateTimestamp (StringcolumnLabel ,Timestamp x )throws SQLException {
    _res
        .updateTimestamp(columnLabel,x );}
    @

    Overridepublic
    void updateAsciiStream (StringcolumnLabel ,InputStream x ,int length )throws SQLException {
    _res
        .updateAsciiStream(columnLabel,x ,length );}
    @

    Overridepublic
    void updateBinaryStream (StringcolumnLabel ,InputStream x ,int length )throws SQLException {
    _res
        .updateBinaryStream(columnLabel,x ,length );}
    @

    Overridepublic
    void updateCharacterStream (StringcolumnLabel ,Reader reader ,int length )throws SQLException {
    _res
        .updateCharacterStream(columnLabel,reader ,length );}
    @

    Overridepublic
    void updateObject (StringcolumnLabel ,Object x ,int scaleOrLength )throws SQLException {
    _res
        .updateObject(columnLabel,x ,scaleOrLength );}
    @

    Overridepublic
    void updateObject (StringcolumnLabel ,Object x )throws SQLException {
    _res
        .updateObject(columnLabel,x );}
    @

    Overridepublic
    void insertRow ()throws SQLException {
    _res
        .insertRow();}
    @

    Overridepublic
    void updateRow ()throws SQLException {
    _res
        .updateRow();}
    @

    Overridepublic
    void deleteRow ()throws SQLException {
    _res
        .deleteRow();}
    @

    Overridepublic
    void refreshRow ()throws SQLException {
    _res
        .refreshRow();}
    @

    Overridepublic
    void cancelRowUpdates ()throws SQLException {
    _res
        .cancelRowUpdates();}
    @

    Overridepublic
    void moveToInsertRow ()throws SQLException {
    _res
        .moveToInsertRow();}
    @

    Overridepublic
    void moveToCurrentRow ()throws SQLException {
    _res
        .moveToCurrentRow();}
    @

    Overridepublic
    Statement getStatement ()throws SQLException {
    return
        _res .getStatement();}
    @

    Overridepublic
    Object getObject (intcolumnIndex ,Map <String,Class <?>>map )throws SQLException {
    return
        _res .getObject(columnIndex,map );}
    @

    Overridepublic
    Ref getRef (intcolumnIndex )throws SQLException {
    return
        _res .getRef(columnIndex);}
    @

    Overridepublic
    Blob getBlob (intcolumnIndex )throws SQLException {
    return
        _res .getBlob(columnIndex);}
    @

    Overridepublic
    Clob getClob (intcolumnIndex )throws SQLException {
    return
        _res .getClob(columnIndex);}
    @

    Overridepublic
    Array getArray (intcolumnIndex )throws SQLException {
    return
        _res .getArray(columnIndex);}
    @

    Overridepublic
    Object getObject (StringcolumnLabel ,Map <String,Class <?>>map )throws SQLException {
    return
        _res .getObject(columnLabel,map );}
    @

    Overridepublic
    Ref getRef (StringcolumnLabel )throws SQLException {
    return
        _res .getRef(columnLabel);}
    @

    Overridepublic
    Blob getBlob (StringcolumnLabel )throws SQLException {
    return
        _res .getBlob(columnLabel);}
    @

    Overridepublic
    Clob getClob (StringcolumnLabel )throws SQLException {
    return
        _res .getClob(columnLabel);}
    @

    Overridepublic
    Array getArray (StringcolumnLabel )throws SQLException {
    return
        _res .getArray(columnLabel);}
    @

    Overridepublic
    Date getDate (intcolumnIndex ,Calendar cal )throws SQLException {
    return
        _res .getDate(columnIndex,cal );}
    @

    Overridepublic
    Date getDate (StringcolumnLabel ,Calendar cal )throws SQLException {
    return
        _res .getDate(columnLabel,cal );}
    @

    Overridepublic
    Time getTime (intcolumnIndex ,Calendar cal )throws SQLException {
    return
        _res .getTime(columnIndex,cal );}
    @

    Overridepublic
    Time getTime (StringcolumnLabel ,Calendar cal )throws SQLException {
    return
        _res .getTime(columnLabel,cal );}
    @

    Overridepublic
    Timestamp getTimestamp (intcolumnIndex ,Calendar cal )throws SQLException {
    return
        _res .getTimestamp(columnIndex,cal );}
    @

    Overridepublic
    Timestamp getTimestamp (StringcolumnLabel ,Calendar cal )throws SQLException {
    return
        _res .getTimestamp(columnLabel,cal );}
    @

    Overridepublic
    URL getURL (intcolumnIndex )throws SQLException {
    return
        _res .getURL(columnIndex);}
    @

    Overridepublic
    URL getURL (StringcolumnLabel )throws SQLException {
    return
        _res .getURL(columnLabel);}
    @

    Overridepublic
    void updateRef (intcolumnIndex ,Ref x )throws SQLException {
    _res
        .updateRef(columnIndex,x );}
    @

    Overridepublic
    void updateRef (StringcolumnLabel ,Ref x )throws SQLException {
    _res
        .updateRef(columnLabel,x );}
    @

    Overridepublic
    void updateBlob (intcolumnIndex ,Blob x )throws SQLException {
    _res
        .updateBlob(columnIndex,x );}
    @

    Overridepublic
    void updateBlob (StringcolumnLabel ,Blob x )throws SQLException {
    _res
        .updateBlob(columnLabel,x );}
    @

    Overridepublic
    void updateClob (intcolumnIndex ,Clob x )throws SQLException {
    _res
        .updateClob(columnIndex,x );}
    @

    Overridepublic
    void updateClob (StringcolumnLabel ,Clob x )throws SQLException {
    _res
        .updateClob(columnLabel,x );}
    @

    Overridepublic
    void updateArray (intcolumnIndex ,Array x )throws SQLException {
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

