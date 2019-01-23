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
    public BigDecimal getBigDecimal(StringcolumnLabel,int scale)throwsSQLException {return _res.getBigDecimal( columnLabel, scale)
    ;}
        @Override publicbyte[]getBytes(StringcolumnLabel)throwsSQLException{ return_res.getBytes(columnLabel
    )

    ;}
    @ OverridepublicDate getDate(String columnLabel) throws SQLException
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
    @ Deprecated publicInputStreamgetUnicodeStream (String columnLabel )
    throws
        SQLException {return_res.getUnicodeStream(columnLabel
    )

    ;}
    @Override
    public InputStream getBinaryStream(String columnLabel) throws SQLException
    {
        return _res.getBinaryStream(columnLabel);
    }

    @Override
    public SQLWarning getWarnings() throwsSQLException { return
    _res
        . getWarnings();}@Override
    public

    voidclearWarnings
    ( ) throwsSQLException{ _res .
    clearWarnings
        ( );}@Overridepublic
    String

    getCursorName(
    ) throws SQLException{return _res .
    getCursorName
        ();}@Override
    public

    ResultSetMetaDatagetMetaData
    ( ) throwsSQLException{ return _res
    .
        getMetaData ();}@Override
    public

    ObjectgetObject
    ( int columnIndex)throws SQLException {
    return
        _res .getObject(columnIndex);
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
    public boolean isBeforeFirst() throwsSQLException { return
    _res
        . isBeforeFirst();}@Override
    public

    booleanisAfterLast
    ( ) throwsSQLException{ return _res
    .
        isAfterLast ();}@Override
    public

    booleanisFirst
    ( ) throwsSQLException{ return _res
    .
        isFirst ();}@Override
    public

    booleanisLast
    ( ) throwsSQLException{ return _res
    .
        isLast ();}@Override
    public

    voidbeforeFirst
    ( ) throwsSQLException{ _res .
    beforeFirst
        ( );}@Overridepublic
    void

    afterLast(
    ) throws SQLException{_res . afterLast
    (
        );}@Overridepublic
    boolean

    first(
    ) throws SQLException{return _res .
    first
        ();}@Override
    public

    booleanlast
    ( ) throwsSQLException{ return _res
    .
        last ();}@Override
    public

    intgetRow
    ( ) throwsSQLException{ return _res
    .
        getRow ();}@Override
    public

    booleanabsolute
    ( int row)throws SQLException {
    return
        _res .absolute(row);
    }

    @Override
    public boolean relative(int rows) throws SQLException
    {
        return _res.relative(rows);
    }

    @Override
    public boolean previous() throwsSQLException { return
    _res
        . previous();}@Override
    public

    voidsetFetchDirection
    ( int direction)throws SQLException {
    _res
        . setFetchDirection(direction);}
    @

    Overridepublic
    int getFetchDirection ()throws SQLException{ return _res
    .
        getFetchDirection();}@Override
    public

    voidsetFetchSize
    ( int rows)throws SQLException {
    _res
        . setFetchSize(rows);}
    @

    Overridepublic
    int getFetchSize ()throws SQLException{ return _res
    .
        getFetchSize();}@Override
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
    ( ) throwsSQLException{ return _res
    .
        rowDeleted ();}@Override
    public

    voidupdateNull
    ( int columnIndex)throws SQLException {
    _res
        . updateNull(columnIndex);}
    @

    Overridepublic
    void updateBoolean (intcolumnIndex ,boolean x )
    throws
        SQLException{_res.updateBoolean(columnIndex
    ,

    x)
    ; } @Overridepublic voidupdateByte ( intcolumnIndex , byte
    x
        )throwsSQLException{_res. updateByte(columnIndex
    ,

    x)
    ; } @Overridepublic voidupdateShort ( intcolumnIndex , short
    x
        )throwsSQLException{_res. updateShort(columnIndex
    ,

    x)
    ; } @Overridepublic voidupdateInt ( intcolumnIndex , int
    x
        )throwsSQLException{_res. updateInt(columnIndex
    ,

    x)
    ; } @Overridepublic voidupdateLong ( intcolumnIndex , long
    x
        )throwsSQLException{_res. updateLong(columnIndex
    ,

    x)
    ; } @Overridepublic voidupdateFloat ( intcolumnIndex , float
    x
        )throwsSQLException{_res. updateFloat(columnIndex
    ,

    x)
    ; } @Overridepublic voidupdateDouble ( intcolumnIndex , double
    x
        )throwsSQLException{_res. updateDouble(columnIndex
    ,

    x)
    ; } @Overridepublic voidupdateBigDecimal ( intcolumnIndex , BigDecimal
    x
        )throwsSQLException{_res. updateBigDecimal(columnIndex
    ,

    x)
    ; } @Overridepublic voidupdateString ( intcolumnIndex , String
    x
        )throwsSQLException{_res. updateString(columnIndex
    ,

    x)
    ; } @Overridepublic voidupdateBytes ( intcolumnIndex , byte
    [
        ]x)throwsSQLException{ _res.updateBytes
    (

    columnIndex,
    x ) ;}@ Overridepublic voidupdateDate( intcolumnIndex , Date
    x
        )throwsSQLException{_res. updateDate(columnIndex
    ,

    x)
    ; } @Overridepublic voidupdateTime ( intcolumnIndex , Time
    x
        )throwsSQLException{_res. updateTime(columnIndex
    ,

    x)
    ; } @Overridepublic voidupdateTimestamp ( intcolumnIndex , Timestamp
    x
        )throwsSQLException{_res. updateTimestamp(columnIndex
    ,

    x)
    ; } @Overridepublic voidupdateAsciiStream ( intcolumnIndex , InputStream
    x
        ,intlength)throwsSQLException {_res.
    updateAsciiStream

    (columnIndex
    , x ,length) ;} @ Overridepublic void updateBinaryStream( int columnIndex
    ,
        InputStreamx,intlength) throwsSQLException {_res.
    updateBinaryStream

    (columnIndex
    , x ,length) ;} @ Overridepublic void updateCharacterStream( int columnIndex
    ,
        Readerx,intlength) throwsSQLException {_res.
    updateCharacterStream

    (columnIndex
    , x ,length) ;} @ Overridepublic void updateObject( int columnIndex
    ,
        Objectx,intscaleOrLength) throwsSQLException {_res.
    updateObject

    (columnIndex
    , x ,scaleOrLength) ;} @ Overridepublic void updateObject( int columnIndex
    ,
        Objectx)throwsSQLException{ _res. updateObject(columnIndex
    ,

    x)
    ; } @Overridepublic voidupdateNull ( StringcolumnLabel ) throws
    SQLException
        {_res.updateNull(columnLabel );}
    @

    Overridepublic
    void updateBoolean (StringcolumnLabel ,boolean x )
    throws
        SQLException{_res.updateBoolean(columnLabel
    ,

    x)
    ; } @Overridepublic voidupdateByte ( StringcolumnLabel , byte
    x
        )throwsSQLException{_res. updateByte(columnLabel
    ,

    x)
    ; } @Overridepublic voidupdateShort ( StringcolumnLabel , short
    x
        )throwsSQLException{_res. updateShort(columnLabel
    ,

    x)
    ; } @Overridepublic voidupdateInt ( StringcolumnLabel , int
    x
        )throwsSQLException{_res. updateInt(columnLabel
    ,

    x)
    ; } @Overridepublic voidupdateLong ( StringcolumnLabel , long
    x
        )throwsSQLException{_res. updateLong(columnLabel
    ,

    x)
    ; } @Overridepublic voidupdateFloat ( StringcolumnLabel , float
    x
        )throwsSQLException{_res. updateFloat(columnLabel
    ,

    x)
    ; } @Overridepublic voidupdateDouble ( StringcolumnLabel , double
    x
        )throwsSQLException{_res. updateDouble(columnLabel
    ,

    x)
    ; } @Overridepublic voidupdateBigDecimal ( StringcolumnLabel , BigDecimal
    x
        )throwsSQLException{_res. updateBigDecimal(columnLabel
    ,

    x)
    ; } @Overridepublic voidupdateString ( StringcolumnLabel , String
    x
        )throwsSQLException{_res. updateString(columnLabel
    ,

    x)
    ; } @Overridepublic voidupdateBytes ( StringcolumnLabel , byte
    [
        ]x)throwsSQLException{ _res.updateBytes
    (

    columnLabel,
    x ) ;}@ Overridepublic voidupdateDate( StringcolumnLabel , Date
    x
        )throwsSQLException{_res. updateDate(columnLabel
    ,

    x)
    ; } @Overridepublic voidupdateTime ( StringcolumnLabel , Time
    x
        )throwsSQLException{_res. updateTime(columnLabel
    ,

    x)
    ; } @Overridepublic voidupdateTimestamp ( StringcolumnLabel , Timestamp
    x
        )throwsSQLException{_res. updateTimestamp(columnLabel
    ,

    x)
    ; } @Overridepublic voidupdateAsciiStream ( StringcolumnLabel , InputStream
    x
        ,intlength)throwsSQLException {_res.
    updateAsciiStream

    (columnLabel
    , x ,length) ;} @ Overridepublic void updateBinaryStream( String columnLabel
    ,
        InputStreamx,intlength) throwsSQLException {_res.
    updateBinaryStream

    (columnLabel
    , x ,length) ;} @ Overridepublic void updateCharacterStream( String columnLabel
    ,
        Readerreader,intlength) throwsSQLException {_res.
    updateCharacterStream

    (columnLabel
    , reader ,length) ;} @ Overridepublic void updateObject( String columnLabel
    ,
        Objectx,intscaleOrLength) throwsSQLException {_res.
    updateObject

    (columnLabel
    , x ,scaleOrLength) ;} @ Overridepublic void updateObject( String columnLabel
    ,
        Objectx)throwsSQLException{ _res. updateObject(columnLabel
    ,

    x)
    ; } @Overridepublic voidinsertRow ( )throws SQLException {
    _res
        .insertRow();} @Overridepublic
    void

    updateRow(
    ) throws SQLException{_res . updateRow
    (
        );}@Overridepublic
    void

    deleteRow(
    ) throws SQLException{_res . deleteRow
    (
        );}@Overridepublic
    void

    refreshRow(
    ) throws SQLException{_res . refreshRow
    (
        );}@Overridepublic
    void

    cancelRowUpdates(
    ) throws SQLException{_res . cancelRowUpdates
    (
        );}@Overridepublic
    void

    moveToInsertRow(
    ) throws SQLException{_res . moveToInsertRow
    (
        );}@Overridepublic
    void

    moveToCurrentRow(
    ) throws SQLException{_res . moveToCurrentRow
    (
        );}@Overridepublic
    Statement

    getStatement(
    ) throws SQLException{return _res .
    getStatement
        ();}@Override
    public

    ObjectgetObject
    ( int columnIndex,Map < String
    ,
        Class <?>>map)
    throws

    SQLException{
    return _res .getObject( columnIndex, map);} @OverridepublicRefgetRef (int columnIndex )
    throws
        SQLException {return_res.getRef( columnIndex);
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
    public Object getObject(String columnLabel, Map <
    String
        , Class<?>>map)
    throws

    SQLException{
    return _res .getObject( columnLabel, map);} @OverridepublicRefgetRef (String columnLabel )
    throws
        SQLException {return_res.getRef( columnLabel);
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
    public Date getDate(int columnIndex, Calendar cal
    )
        throws SQLException{return_res.getDate(
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
    ) ; }@Override publicURL getURL (int columnIndex )
    throws
        SQLException {return_res.getURL( columnIndex);
    }

    @Override
    public URL getURL(String columnLabel) throws SQLException
    {
        return _res.getURL(columnLabel);
    }

    @Override
    public void updateRef(int columnIndex, Ref x
    )
        throws SQLException{_res.updateRef(columnIndex
    ,

    x)
    ; } @Overridepublic voidupdateRef ( StringcolumnLabel , Ref
    x
        )throwsSQLException{_res. updateRef(columnLabel
    ,

    x)
    ; } @Overridepublic voidupdateBlob ( intcolumnIndex , Blob
    x
        )throwsSQLException{_res. updateBlob(columnIndex
    ,

    x)
    ; } @Overridepublic voidupdateBlob ( StringcolumnLabel , Blob
    x
        )throwsSQLException{_res. updateBlob(columnLabel
    ,

    x)
    ; } @Overridepublic voidupdateClob ( intcolumnIndex , Clob
    x
        )throwsSQLException{_res. updateClob(columnIndex
    ,

    x)
    ; } @Overridepublic voidupdateClob ( StringcolumnLabel , Clob
    x
        )throwsSQLException{_res. updateClob(columnLabel
    ,

    x)
    ; } @Overridepublic voidupdateArray ( intcolumnIndex , Array
    x
        )throwsSQLException{_res. updateArray(columnIndex
    ,

    x)
    ; } @Overridepublic voidupdateArray ( StringcolumnLabel , Array
    x
        )throwsSQLException{_res. updateArray(columnLabel
    ,

    x)
    ; } @Overridepublic RowIdgetRowId ( intcolumnIndex ) throws
    SQLException
        {return_res.getRowId( columnIndex);
    }

    @Override
    public RowId getRowId(String columnLabel) throws SQLException
    {
        return _res.getRowId(columnLabel);
    }

    @Override
    public void updateRowId(int columnIndex, RowId x
    )
        throws SQLException{_res.updateRowId(columnIndex
    ,

    x)
    ; } @Overridepublic voidupdateRowId ( StringcolumnLabel , RowId
    x
        )throwsSQLException{_res. updateRowId(columnLabel
    ,

    x)
    ; } @Overridepublic intgetHoldability ( )throws SQLException {
    return
        _res.getHoldability(); }@Override
    public

    booleanisClosed
    ( ) throwsSQLException{ return _res
    .
        isClosed ();}@Override
    public

    voidupdateNString
    ( int columnIndex,String nString )
    throws
        SQLException {_res.updateNString(columnIndex
    ,

    nString)
    ; } @Overridepublic voidupdateNString ( StringcolumnLabel , String
    nString
        )throwsSQLException{_res. updateNString(columnLabel
    ,

    nString)
    ; } @Overridepublic voidupdateNClob ( intcolumnIndex , NClob
    nClob
        )throwsSQLException{_res. updateNClob(columnIndex
    ,

    nClob)
    ; } @Overridepublic voidupdateNClob ( StringcolumnLabel , NClob
    nClob
        )throwsSQLException{_res. updateNClob(columnLabel
    ,

    nClob)
    ; } @Overridepublic NClobgetNClob ( intcolumnIndex ) throws
    SQLException
        {return_res.getNClob( columnIndex);
    }

    @Override
    public NClob getNClob(String columnLabel) throws SQLException
    {
        return _res.getNClob(columnLabel);
    }

    @Override
    public SQLXML getSQLXML(int columnIndex) throws SQLException
    {
        return _res.getSQLXML(columnIndex);
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException
    {
        return _res.getSQLXML(columnLabel);
    }

    @Override
    public void updateSQLXML(int columnIndex, SQLXML xmlObject
    )
        throws SQLException{_res.updateSQLXML(columnIndex
    ,

    xmlObject)
    ; } @Overridepublic voidupdateSQLXML ( StringcolumnLabel , SQLXML
    xmlObject
        )throwsSQLException{_res. updateSQLXML(columnLabel
    ,

    xmlObject)
    ; } @Overridepublic StringgetNString ( intcolumnIndex ) throws
    SQLException
        {return_res.getNString( columnIndex);
    }

    @Override
    public String getNString(String columnLabel) throws SQLException
    {
        return _res.getNString(columnLabel);
    }

    @Override
    public Reader getNCharacterStream(int columnIndex) throws SQLException
    {
        return _res.getNCharacterStream(columnIndex);
    }

    @Override
    public Reader getNCharacterStream(String columnLabel) throws SQLException
    {
        return _res.getNCharacterStream(columnLabel);
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x
    ,
        long length)throwsSQLException{_res.
    updateNCharacterStream

    (columnIndex
    , x ,length) ;} @ Overridepublic void updateNCharacterStream( String columnLabel
    ,
        Readerreader,longlength) throwsSQLException {_res.
    updateNCharacterStream

    (columnLabel
    , reader ,length) ;} @ Overridepublic void updateAsciiStream( int columnIndex
    ,
        InputStreamx,longlength) throwsSQLException {_res.
    updateAsciiStream

    (columnIndex
    , x ,length) ;} @ Overridepublic void updateBinaryStream( int columnIndex
    ,
        InputStreamx,longlength) throwsSQLException {_res.
    updateBinaryStream

    (columnIndex
    , x ,length) ;} @ Overridepublic void updateCharacterStream( int columnIndex
    ,
        Readerx,longlength) throwsSQLException {_res.
    updateCharacterStream

    (columnIndex
    , x ,length) ;} @ Overridepublic void updateAsciiStream( String columnLabel
    ,
        InputStreamx,longlength) throwsSQLException {_res.
    updateAsciiStream

    (columnLabel
    , x ,length) ;} @ Overridepublic void updateBinaryStream( String columnLabel
    ,
        InputStreamx,longlength) throwsSQLException {_res.
    updateBinaryStream

    (columnLabel
    , x ,length) ;} @ Overridepublic void updateCharacterStream( String columnLabel
    ,
        Readerreader,longlength) throwsSQLException {_res.
    updateCharacterStream

    (columnLabel
    , reader ,length) ;} @ Overridepublic void updateBlob( int columnIndex
    ,
        InputStreaminputStream,longlength) throwsSQLException {_res.
    updateBlob

    (columnIndex
    , inputStream ,length) ;} @ Overridepublic void updateBlob( String columnLabel
    ,
        InputStreaminputStream,longlength) throwsSQLException {_res.
    updateBlob

    (columnLabel
    , inputStream ,length) ;} @ Overridepublic void updateClob( int columnIndex
    ,
        Readerreader,longlength) throwsSQLException {_res.
    updateClob

    (columnIndex
    , reader ,length) ;} @ Overridepublic void updateClob( String columnLabel
    ,
        Readerreader,longlength) throwsSQLException {_res.
    updateClob

    (columnLabel
    , reader ,length) ;} @ Overridepublic void updateNClob( int columnIndex
    ,
        Readerreader,longlength) throwsSQLException {_res.
    updateNClob

    (columnIndex
    , reader ,length) ;} @ Overridepublic void updateNClob( String columnLabel
    ,
        Readerreader,longlength) throwsSQLException {_res.
    updateNClob

    (columnLabel
    , reader ,length) ;} @ Overridepublic void updateNCharacterStream( int columnIndex
    ,
        Readerx)throwsSQLException{ _res. updateNCharacterStream(columnIndex
    ,

    x)
    ; } @Overridepublic voidupdateNCharacterStream ( StringcolumnLabel , Reader
    reader
        )throwsSQLException{_res. updateNCharacterStream(columnLabel
    ,

    reader)
    ; } @Overridepublic voidupdateAsciiStream ( intcolumnIndex , InputStream
    x
        )throwsSQLException{_res. updateAsciiStream(columnIndex
    ,

    x)
    ; } @Overridepublic voidupdateBinaryStream ( intcolumnIndex , InputStream
    x
        )throwsSQLException{_res. updateBinaryStream(columnIndex
    ,

    x)
    ; } @Overridepublic voidupdateCharacterStream ( intcolumnIndex , Reader
    x
        )throwsSQLException{_res. updateCharacterStream(columnIndex
    ,

    x)
    ; } @Overridepublic voidupdateAsciiStream ( StringcolumnLabel , InputStream
    x
        )throwsSQLException{_res. updateAsciiStream(columnLabel
    ,

    x)
    ; } @Overridepublic voidupdateBinaryStream ( StringcolumnLabel , InputStream
    x
        )throwsSQLException{_res. updateBinaryStream(columnLabel
    ,

    x)
    ; } @Overridepublic voidupdateCharacterStream ( StringcolumnLabel , Reader
    reader
        )throwsSQLException{_res. updateCharacterStream(columnLabel
    ,

    reader)
    ; } @Overridepublic voidupdateBlob ( intcolumnIndex , InputStream
    inputStream
        )throwsSQLException{_res. updateBlob(columnIndex
    ,

    inputStream)
    ; } @Overridepublic voidupdateBlob ( StringcolumnLabel , InputStream
    inputStream
        )throwsSQLException{_res. updateBlob(columnLabel
    ,

    inputStream)
    ; } @Overridepublic voidupdateClob ( intcolumnIndex , Reader
    reader
        )throwsSQLException{_res. updateClob(columnIndex
    ,

    reader)
    ; } @Overridepublic voidupdateClob ( StringcolumnLabel , Reader
    reader
        )throwsSQLException{_res. updateClob(columnLabel
    ,

    reader)
    ; } @Overridepublic voidupdateNClob ( intcolumnIndex , Reader
    reader
        )throwsSQLException{_res. updateNClob(columnIndex
    ,

    reader)
    ; } @Overridepublic voidupdateNClob ( StringcolumnLabel , Reader
    reader
        )throwsSQLException{_res. updateNClob(columnLabel
    ,

    reader)
    ; } @Overridepublic <T > TgetObject ( int
    columnIndex
        ,Class<T>type )throwsSQLException
    {

    return_res
    . getObject(columnIndex , type); }@ Overridepublic<T >T getObject (
    String
        columnLabel ,Class<T>type )throwsSQLException
    {

    return_res
    . getObject(columnLabel , type); }@ OverridepublicvoidupdateObject (int columnIndex ,
    Object
        x ,SQLTypetargetSqlType,intscaleOrLength )throwsSQLException
    {

    _res.
    updateObject ( columnIndex,x ,targetSqlType , scaleOrLength) ; }@ Override publicvoid updateObject (
    String
        columnLabel,Objectx,SQLType targetSqlType, intscaleOrLength )throwsSQLException
    {

    _res.
    updateObject ( columnLabel,x ,targetSqlType , scaleOrLength) ; }@ Override publicvoid updateObject (
    int
        columnIndex,Objectx,SQLType targetSqlType) throwsSQLException {_res.
    updateObject

    (columnIndex
    , x ,targetSqlType) ;} @ Overridepublic void updateObject( String columnLabel
    ,
        Objectx,SQLTypetargetSqlType) throwsSQLException {_res.
    updateObject

    (columnLabel
    , x ,targetSqlType) ;} @ Overridepublic < T> T unwrap
    (
        Class<T>iface) throwsSQLException {return_res
    .

    unwrap(
    iface );} @ OverridepublicbooleanisWrapperFor(Class <? > iface
    )
        throws SQLException{return_res.isWrapperFor(
    iface

    );
    } } 