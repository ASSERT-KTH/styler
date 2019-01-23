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
        _res.updateShort(columnIndex, x);}@
    Overridepublic

    voidupdateInt(int
    columnIndex, intx )throwsSQLException{_res. updateInt(columnIndex, x) ;}@Override publicvoid updateLong( intcolumnIndex
    ,long
        x)throwsSQLException{_res.updateLong( columnIndex,x
    )

    ;}
    @ Override publicvoidupdateFloat (int columnIndex ,float x )
    throws
        SQLException{_res.updateFloat( columnIndex,x
    )

    ;}
    @ Override publicvoidupdateDouble (int columnIndex ,double x )
    throws
        SQLException{_res.updateDouble( columnIndex,x
    )

    ;}
    @ Override publicvoidupdateBigDecimal (int columnIndex ,BigDecimal x )
    throws
        SQLException{_res.updateBigDecimal( columnIndex,x
    )

    ;}
    @ Override publicvoidupdateString (int columnIndex ,String x )
    throws
        SQLException{_res.updateString( columnIndex,x
    )

    ;}
    @ Override publicvoidupdateBytes (int columnIndex ,byte [ ]
    x
        )throwsSQLException{_res. updateBytes(columnIndex
    ,

    x)
    ; } @Overridepublic voidupdateDate (intcolumnIndex ,Date x )
    throws
        SQLException{_res.updateDate( columnIndex,x
    )

    ;}
    @ Override publicvoidupdateTime (int columnIndex ,Time x )
    throws
        SQLException{_res.updateTime( columnIndex,x
    )

    ;}
    @ Override publicvoidupdateTimestamp (int columnIndex ,Timestamp x )
    throws
        SQLException{_res.updateTimestamp( columnIndex,x
    )

    ;}
    @ Override publicvoidupdateAsciiStream (int columnIndex ,InputStream x ,
    int
        length)throwsSQLException{_res .updateAsciiStream(
    columnIndex

    ,x
    , length );} @Override public voidupdateBinaryStream ( intcolumnIndex , InputStream
    x
        ,intlength)throwsSQLException {_res .updateBinaryStream(
    columnIndex

    ,x
    , length );} @Override public voidupdateCharacterStream ( intcolumnIndex , Reader
    x
        ,intlength)throwsSQLException {_res .updateCharacterStream(
    columnIndex

    ,x
    , length );} @Override public voidupdateObject ( intcolumnIndex , Object
    x
        ,intscaleOrLength)throwsSQLException {_res .updateObject(
    columnIndex

    ,x
    , scaleOrLength );} @Override public voidupdateObject ( intcolumnIndex , Object
    x
        )throwsSQLException{_res. updateObject( columnIndex,x
    )

    ;}
    @ Override publicvoidupdateNull (String columnLabel )throws SQLException {
    _res
        .updateNull(columnLabel); }@Override
    public

    voidupdateBoolean
    ( String columnLabel,boolean x) throws SQLException
    {
        _res.updateBoolean(columnLabel,x
    )

    ;}
    @ Override publicvoidupdateByte (String columnLabel ,byte x )
    throws
        SQLException{_res.updateByte( columnLabel,x
    )

    ;}
    @ Override publicvoidupdateShort (String columnLabel ,short x )
    throws
        SQLException{_res.updateShort( columnLabel,x
    )

    ;}
    @ Override publicvoidupdateInt (String columnLabel ,int x )
    throws
        SQLException{_res.updateInt( columnLabel,x
    )

    ;}
    @ Override publicvoidupdateLong (String columnLabel ,long x )
    throws
        SQLException{_res.updateLong( columnLabel,x
    )

    ;}
    @ Override publicvoidupdateFloat (String columnLabel ,float x )
    throws
        SQLException{_res.updateFloat( columnLabel,x
    )

    ;}
    @ Override publicvoidupdateDouble (String columnLabel ,double x )
    throws
        SQLException{_res.updateDouble( columnLabel,x
    )

    ;}
    @ Override publicvoidupdateBigDecimal (String columnLabel ,BigDecimal x )
    throws
        SQLException{_res.updateBigDecimal( columnLabel,x
    )

    ;}
    @ Override publicvoidupdateString (String columnLabel ,String x )
    throws
        SQLException{_res.updateString( columnLabel,x
    )

    ;}
    @ Override publicvoidupdateBytes (String columnLabel ,byte [ ]
    x
        )throwsSQLException{_res. updateBytes(columnLabel
    ,

    x)
    ; } @Overridepublic voidupdateDate (StringcolumnLabel ,Date x )
    throws
        SQLException{_res.updateDate( columnLabel,x
    )

    ;}
    @ Override publicvoidupdateTime (String columnLabel ,Time x )
    throws
        SQLException{_res.updateTime( columnLabel,x
    )

    ;}
    @ Override publicvoidupdateTimestamp (String columnLabel ,Timestamp x )
    throws
        SQLException{_res.updateTimestamp( columnLabel,x
    )

    ;}
    @ Override publicvoidupdateAsciiStream (String columnLabel ,InputStream x ,
    int
        length)throwsSQLException{_res .updateAsciiStream(
    columnLabel

    ,x
    , length );} @Override public voidupdateBinaryStream ( StringcolumnLabel , InputStream
    x
        ,intlength)throwsSQLException {_res .updateBinaryStream(
    columnLabel

    ,x
    , length );} @Override public voidupdateCharacterStream ( StringcolumnLabel , Reader
    reader
        ,intlength)throwsSQLException {_res .updateCharacterStream(
    columnLabel

    ,reader
    , length );} @Override public voidupdateObject ( StringcolumnLabel , Object
    x
        ,intscaleOrLength)throwsSQLException {_res .updateObject(
    columnLabel

    ,x
    , scaleOrLength );} @Override public voidupdateObject ( StringcolumnLabel , Object
    x
        )throwsSQLException{_res. updateObject( columnLabel,x
    )

    ;}
    @ Override publicvoidinsertRow () throws SQLException{ _res .
    insertRow
        ();}@Override publicvoidupdateRow
    (

    )throws
    SQLException { _res.updateRow ( )
    ;
        }@OverridepublicvoiddeleteRow
    (

    )throws
    SQLException { _res.deleteRow ( )
    ;
        }@OverridepublicvoidrefreshRow
    (

    )throws
    SQLException { _res.refreshRow ( )
    ;
        }@OverridepublicvoidcancelRowUpdates
    (

    )throws
    SQLException { _res.cancelRowUpdates ( )
    ;
        }@OverridepublicvoidmoveToInsertRow
    (

    )throws
    SQLException { _res.moveToInsertRow ( )
    ;
        }@OverridepublicvoidmoveToCurrentRow
    (

    )throws
    SQLException { _res.moveToCurrentRow ( )
    ;
        }@OverridepublicStatementgetStatement
    (

    )throws
    SQLException { return_res. getStatement (
    )
        ;}@OverridepublicObject
    getObject

    (int
    columnIndex , Map<String , Class
    <
        ? >>map)throwsSQLException
    {

    return_res
    . getObject (columnIndex, map) ;}@Override publicRefgetRef(int columnIndex) throws SQLException
    {
        return _res.getRef(columnIndex) ;}@
    Override

    publicBlob
    getBlob ( intcolumnIndex) throwsSQLException { return
    _res
        . getBlob(columnIndex);}@
    Override

    publicClob
    getClob ( intcolumnIndex) throwsSQLException { return
    _res
        . getClob(columnIndex);}@
    Override

    publicArray
    getArray ( intcolumnIndex) throwsSQLException { return
    _res
        . getArray(columnIndex);}@
    Override

    publicObject
    getObject ( StringcolumnLabel, Map< String ,
    Class
        < ?>>map)throwsSQLException
    {

    return_res
    . getObject (columnLabel, map) ;}@Override publicRefgetRef(String columnLabel) throws SQLException
    {
        return _res.getRef(columnLabel) ;}@
    Override

    publicBlob
    getBlob ( StringcolumnLabel) throwsSQLException { return
    _res
        . getBlob(columnLabel);}@
    Override

    publicClob
    getClob ( StringcolumnLabel) throwsSQLException { return
    _res
        . getClob(columnLabel);}@
    Override

    publicArray
    getArray ( StringcolumnLabel) throwsSQLException { return
    _res
        . getArray(columnLabel);}@
    Override

    publicDate
    getDate ( intcolumnIndex, Calendarcal ) throws
    SQLException
        { return_res.getDate(columnIndex,
    cal

    );
    } @ OverridepublicDate getDate( String columnLabel, Calendar cal
    )
        throws SQLException{return_res.getDate (columnLabel,
    cal

    );
    } @ OverridepublicTime getTime( int columnIndex, Calendar cal
    )
        throws SQLException{return_res.getTime (columnIndex,
    cal

    );
    } @ OverridepublicTime getTime( String columnLabel, Calendar cal
    )
        throws SQLException{return_res.getTime (columnLabel,
    cal

    );
    } @ OverridepublicTimestamp getTimestamp( int columnIndex, Calendar cal
    )
        throws SQLException{return_res.getTimestamp (columnIndex,
    cal

    );
    } @ OverridepublicTimestamp getTimestamp( String columnLabel, Calendar cal
    )
        throws SQLException{return_res.getTimestamp (columnLabel,
    cal

    );
    } @ OverridepublicURL getURL( int columnIndex) throws SQLException
    {
        return _res.getURL(columnIndex) ;}@
    Override

    publicURL
    getURL ( StringcolumnLabel) throwsSQLException { return
    _res
        . getURL(columnLabel);}@
    Override

    publicvoid
    updateRef ( intcolumnIndex, Refx ) throws
    SQLException
        { _res.updateRef(columnIndex,x
    )

    ;}
    @ Override publicvoidupdateRef (String columnLabel ,Ref x )
    throws
        SQLException{_res.updateRef( columnLabel,x
    )

    ;}
    @ Override publicvoidupdateBlob (int columnIndex ,Blob x )
    throws
        SQLException{_res.updateBlob( columnIndex,x
    )

    ;}
    @ Override publicvoidupdateBlob (String columnLabel ,Blob x )
    throws
        SQLException{_res.updateBlob( columnLabel,x
    )

    ;}
    @ Override publicvoidupdateClob (int columnIndex ,Clob x )
    throws
        SQLException{_res.updateClob( columnIndex,x
    )

    ;}
    @ Override publicvoidupdateClob (String columnLabel ,Clob x )
    throws
        SQLException{_res.updateClob( columnLabel,x
    )

    ;}
    @ Override publicvoidupdateArray (int columnIndex ,Array x )
    throws
        SQLException{_res.updateArray( columnIndex,x
    )

    ;}
    @ Override publicvoidupdateArray (String columnLabel ,Array x )
    throws
        SQLException{_res.updateArray( columnLabel,x
    )

    ;}
    @ Override publicRowIdgetRowId (int columnIndex )throws SQLException {
    return
        _res.getRowId(columnIndex) ;}@
    Override

    publicRowId
    getRowId ( StringcolumnLabel) throwsSQLException { return
    _res
        . getRowId(columnLabel);}@
    Override

    publicvoid
    updateRowId ( intcolumnIndex, RowIdx ) throws
    SQLException
        { _res.updateRowId(columnIndex,x
    )

    ;}
    @ Override publicvoidupdateRowId (String columnLabel ,RowId x )
    throws
        SQLException{_res.updateRowId( columnLabel,x
    )

    ;}
    @ Override publicintgetHoldability () throws SQLException{ return _res
    .
        getHoldability();}@ Overridepublicboolean
    isClosed

    ()
    throws SQLException {return_res . isClosed
    (
        ) ;}@Overridepublicvoid
    updateNString

    (int
    columnIndex , StringnString) throws SQLException
    {
        _res .updateNString(columnIndex,nString
    )

    ;}
    @ Override publicvoidupdateNString (String columnLabel ,String nString )
    throws
        SQLException{_res.updateNString( columnLabel,nString
    )

    ;}
    @ Override publicvoidupdateNClob (int columnIndex ,NClob nClob )
    throws
        SQLException{_res.updateNClob( columnIndex,nClob
    )

    ;}
    @ Override publicvoidupdateNClob (String columnLabel ,NClob nClob )
    throws
        SQLException{_res.updateNClob( columnLabel,nClob
    )

    ;}
    @ Override publicNClobgetNClob (int columnIndex )throws SQLException {
    return
        _res.getNClob(columnIndex) ;}@
    Override

    publicNClob
    getNClob ( StringcolumnLabel) throwsSQLException { return
    _res
        . getNClob(columnLabel);}@
    Override

    publicSQLXML
    getSQLXML ( intcolumnIndex) throwsSQLException { return
    _res
        . getSQLXML(columnIndex);}@
    Override

    publicSQLXML
    getSQLXML ( StringcolumnLabel) throwsSQLException { return
    _res
        . getSQLXML(columnLabel);}@
    Override

    publicvoid
    updateSQLXML ( intcolumnIndex, SQLXMLxmlObject ) throws
    SQLException
        { _res.updateSQLXML(columnIndex,xmlObject
    )

    ;}
    @ Override publicvoidupdateSQLXML (String columnLabel ,SQLXML xmlObject )
    throws
        SQLException{_res.updateSQLXML( columnLabel,xmlObject
    )

    ;}
    @ Override publicStringgetNString (int columnIndex )throws SQLException {
    return
        _res.getNString(columnIndex) ;}@
    Override

    publicString
    getNString ( StringcolumnLabel) throwsSQLException { return
    _res
        . getNString(columnLabel);}@
    Override

    publicReader
    getNCharacterStream ( intcolumnIndex) throwsSQLException { return
    _res
        . getNCharacterStream(columnIndex);}@
    Override

    publicReader
    getNCharacterStream ( StringcolumnLabel) throwsSQLException { return
    _res
        . getNCharacterStream(columnLabel);}@
    Override

    publicvoid
    updateNCharacterStream ( intcolumnIndex, Readerx , long
    length
        ) throwsSQLException{_res.updateNCharacterStream(
    columnIndex

    ,x
    , length );} @Override public voidupdateNCharacterStream ( StringcolumnLabel , Reader
    reader
        ,longlength)throwsSQLException {_res .updateNCharacterStream(
    columnLabel

    ,reader
    , length );} @Override public voidupdateAsciiStream ( intcolumnIndex , InputStream
    x
        ,longlength)throwsSQLException {_res .updateAsciiStream(
    columnIndex

    ,x
    , length );} @Override public voidupdateBinaryStream ( intcolumnIndex , InputStream
    x
        ,longlength)throwsSQLException {_res .updateBinaryStream(
    columnIndex

    ,x
    , length );} @Override public voidupdateCharacterStream ( intcolumnIndex , Reader
    x
        ,longlength)throwsSQLException {_res .updateCharacterStream(
    columnIndex

    ,x
    , length );} @Override public voidupdateAsciiStream ( StringcolumnLabel , InputStream
    x
        ,longlength)throwsSQLException {_res .updateAsciiStream(
    columnLabel

    ,x
    , length );} @Override public voidupdateBinaryStream ( StringcolumnLabel , InputStream
    x
        ,longlength)throwsSQLException {_res .updateBinaryStream(
    columnLabel

    ,x
    , length );} @Override public voidupdateCharacterStream ( StringcolumnLabel , Reader
    reader
        ,longlength)throwsSQLException {_res .updateCharacterStream(
    columnLabel

    ,reader
    , length );} @Override public voidupdateBlob ( intcolumnIndex , InputStream
    inputStream
        ,longlength)throwsSQLException {_res .updateBlob(
    columnIndex

    ,inputStream
    , length );} @Override public voidupdateBlob ( StringcolumnLabel , InputStream
    inputStream
        ,longlength)throwsSQLException {_res .updateBlob(
    columnLabel

    ,inputStream
    , length );} @Override public voidupdateClob ( intcolumnIndex , Reader
    reader
        ,longlength)throwsSQLException {_res .updateClob(
    columnIndex

    ,reader
    , length );} @Override public voidupdateClob ( StringcolumnLabel , Reader
    reader
        ,longlength)throwsSQLException {_res .updateClob(
    columnLabel

    ,reader
    , length );} @Override public voidupdateNClob ( intcolumnIndex , Reader
    reader
        ,longlength)throwsSQLException {_res .updateNClob(
    columnIndex

    ,reader
    , length );} @Override public voidupdateNClob ( StringcolumnLabel , Reader
    reader
        ,longlength)throwsSQLException {_res .updateNClob(
    columnLabel

    ,reader
    , length );} @Override public voidupdateNCharacterStream ( intcolumnIndex , Reader
    x
        )throwsSQLException{_res. updateNCharacterStream( columnIndex,x
    )

    ;}
    @ Override publicvoidupdateNCharacterStream (String columnLabel ,Reader reader )
    throws
        SQLException{_res.updateNCharacterStream( columnLabel,reader
    )

    ;}
    @ Override publicvoidupdateAsciiStream (int columnIndex ,InputStream x )
    throws
        SQLException{_res.updateAsciiStream( columnIndex,x
    )

    ;}
    @ Override publicvoidupdateBinaryStream (int columnIndex ,InputStream x )
    throws
        SQLException{_res.updateBinaryStream( columnIndex,x
    )

    ;}
    @ Override publicvoidupdateCharacterStream (int columnIndex ,Reader x )
    throws
        SQLException{_res.updateCharacterStream( columnIndex,x
    )

    ;}
    @ Override publicvoidupdateAsciiStream (String columnLabel ,InputStream x )
    throws
        SQLException{_res.updateAsciiStream( columnLabel,x
    )

    ;}
    @ Override publicvoidupdateBinaryStream (String columnLabel ,InputStream x )
    throws
        SQLException{_res.updateBinaryStream( columnLabel,x
    )

    ;}
    @ Override publicvoidupdateCharacterStream (String columnLabel ,Reader reader )
    throws
        SQLException{_res.updateCharacterStream( columnLabel,reader
    )

    ;}
    @ Override publicvoidupdateBlob (int columnIndex ,InputStream inputStream )
    throws
        SQLException{_res.updateBlob( columnIndex,inputStream
    )

    ;}
    @ Override publicvoidupdateBlob (String columnLabel ,InputStream inputStream )
    throws
        SQLException{_res.updateBlob( columnLabel,inputStream
    )

    ;}
    @ Override publicvoidupdateClob (int columnIndex ,Reader reader )
    throws
        SQLException{_res.updateClob( columnIndex,reader
    )

    ;}
    @ Override publicvoidupdateClob (String columnLabel ,Reader reader )
    throws
        SQLException{_res.updateClob( columnLabel,reader
    )

    ;}
    @ Override publicvoidupdateNClob (int columnIndex ,Reader reader )
    throws
        SQLException{_res.updateNClob( columnIndex,reader
    )

    ;}
    @ Override publicvoidupdateNClob (String columnLabel ,Reader reader )
    throws
        SQLException{_res.updateNClob( columnLabel,reader
    )

    ;}
    @ Override public<T >T getObject (int columnIndex ,
    Class
        <T>type)throws SQLException{return
    _res

    .getObject
    ( columnIndex,type ) ;}@ Overridepublic <T>T getObject( String columnLabel
    ,
        Class <T>type)throws SQLException{return
    _res

    .getObject
    ( columnLabel,type ) ;}@ Overridepublic voidupdateObject(int columnIndex, Object x
    ,
        SQLType targetSqlType,intscaleOrLength)throws SQLException{_res
    .

    updateObject(
    columnIndex , x,targetSqlType ,scaleOrLength ) ;} @ Overridepublic void updateObject( String columnLabel
    ,
        Objectx,SQLTypetargetSqlType, intscaleOrLength )throws SQLException{_res
    .

    updateObject(
    columnLabel , x,targetSqlType ,scaleOrLength ) ;} @ Overridepublic void updateObject( int columnIndex
    ,
        Objectx,SQLTypetargetSqlType) throwsSQLException {_res .updateObject(
    columnIndex

    ,x
    , targetSqlType );} @Override public voidupdateObject ( StringcolumnLabel , Object
    x
        ,SQLTypetargetSqlType)throwsSQLException {_res .updateObject(
    columnLabel

    ,x
    , targetSqlType );} @Override public <T > Tunwrap ( Class
    <
        T>iface)throwsSQLException {return _res.unwrap
    (

    iface)
    ; }@Override public booleanisWrapperFor(Class<? >iface ) throws
    SQLException
        { return_res.isWrapperFor(iface)
    ;

    }}
    