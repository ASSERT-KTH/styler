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

    @OverridepublicTimestamp
    getTimestamp( StringcolumnLabel )throwsSQLException{return_res .getTimestamp(columnLabel ); }@Overridepublic
        InputStreamgetAsciiStream (StringcolumnLabel)throwsSQLException{return_res.getAsciiStream(
    columnLabel

    );
    } @ Override@Deprecated publicInputStream getUnicodeStream (
    String
        columnLabel )throwsSQLException{return_res.
    getUnicodeStream

    (columnLabel
    );
    } @ OverridepublicInputStream getBinaryStream( String columnLabel
    )
        throws SQLException{return_res.getBinaryStream(
    columnLabel

    );
    } @ OverridepublicSQLWarning getWarnings( ) throws
    SQLException
        { return_res.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException
    {
        _res .clearWarnings();}
    @

    Overridepublic
    String getCursorName ()throws SQLException {
    return
        _res.getCursorName();
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException
    {
        return _res.getMetaData();
    }

    @Override
    public Object getObject(int columnIndex )
    throws
        SQLException {return_res.getObject(
    columnIndex

    );
    } @ OverridepublicObject getObject( String columnLabel
    )
        throws SQLException{return_res.getObject(
    columnLabel

    );
    } @ Overridepublicint findColumn( String columnLabel
    )
        throws SQLException{return_res.findColumn(
    columnLabel

    );
    } @ OverridepublicReader getCharacterStream( int columnIndex
    )
        throws SQLException{return_res.getCharacterStream(
    columnIndex

    );
    } @ OverridepublicReader getCharacterStream( String columnLabel
    )
        throws SQLException{return_res.getCharacterStream(
    columnLabel

    );
    } @ OverridepublicBigDecimal getBigDecimal( int columnIndex
    )
        throws SQLException{return_res.getBigDecimal(
    columnIndex

    );
    } @ OverridepublicBigDecimal getBigDecimal( String columnLabel
    )
        throws SQLException{return_res.getBigDecimal(
    columnLabel

    );
    } @ Overridepublicboolean isBeforeFirst( ) throws
    SQLException
        { return_res.isBeforeFirst();
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
        _res .beforeFirst();}
    @

    Overridepublic
    void afterLast ()throws SQLException {
    _res
        .afterLast();}
    @

    Overridepublic
    boolean first ()throws SQLException {
    return
        _res.first();
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
    public boolean absolute(int row )
    throws
        SQLException {return_res.absolute(
    row

    );
    } @ Overridepublicboolean relative( int rows
    )
        throws SQLException{return_res.relative(
    rows

    );
    } @ Overridepublicboolean previous( ) throws
    SQLException
        { return_res.previous();
    }

    @Override
    public void setFetchDirection(int direction )
    throws
        SQLException {_res.setFetchDirection(direction
    )

    ;}
    @ Override publicintgetFetchDirection () throws SQLException
    {
        return_res.getFetchDirection();
    }

    @Override
    public void setFetchSize(int rows )
    throws
        SQLException {_res.setFetchSize(rows
    )

    ;}
    @ Override publicintgetFetchSize () throws SQLException
    {
        return_res.getFetchSize();
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
    public void updateNull(int columnIndex )
    throws
        SQLException {_res.updateNull(columnIndex
    )

    ;}
    @ Override publicvoidupdateBoolean (int columnIndex ,
    boolean
        x)throwsSQLException{_res.
    updateBoolean

    (columnIndex
    , x );} @Override public voidupdateByte ( int
    columnIndex
        ,bytex)throwsSQLException {_res.
    updateByte

    (columnIndex
    , x );} @Override public voidupdateShort ( int
    columnIndex
        ,shortx)throwsSQLException {_res.
    updateShort

    (columnIndex
    , x );} @Override public voidupdateInt ( int
    columnIndex
        ,intx)throwsSQLException {_res.
    updateInt

    (columnIndex
    , x );} @Override public voidupdateLong ( int
    columnIndex
        ,longx)throwsSQLException {_res.
    updateLong

    (columnIndex
    , x );} @Override public voidupdateFloat ( int
    columnIndex
        ,floatx)throwsSQLException {_res.
    updateFloat

    (columnIndex
    , x );} @Override public voidupdateDouble ( int
    columnIndex
        ,doublex)throwsSQLException {_res.
    updateDouble

    (columnIndex
    , x );} @Override public voidupdateBigDecimal ( int
    columnIndex
        ,BigDecimalx)throwsSQLException {_res.
    updateBigDecimal

    (columnIndex
    , x );} @Override public voidupdateString ( int
    columnIndex
        ,Stringx)throwsSQLException {_res.
    updateString

    (columnIndex
    , x );} @Override public voidupdateBytes ( int
    columnIndex
        ,byte[]x) throwsSQLException{
    _res

    .updateBytes
    ( columnIndex ,x) ;} @Overridepublic voidupdateDate ( int
    columnIndex
        ,Datex)throwsSQLException {_res.
    updateDate

    (columnIndex
    , x );} @Override public voidupdateTime ( int
    columnIndex
        ,Timex)throwsSQLException {_res.
    updateTime

    (columnIndex
    , x );} @Override public voidupdateTimestamp ( int
    columnIndex
        ,Timestampx)throwsSQLException {_res.
    updateTimestamp

    (columnIndex
    , x );} @Override public voidupdateAsciiStream ( int
    columnIndex
        ,InputStreamx,intlength )throwsSQLException
    {

    _res.
    updateAsciiStream ( columnIndex,x ,length ) ;} @ Overridepublic void updateBinaryStream
    (
        intcolumnIndex,InputStreamx, intlength )throwsSQLException
    {

    _res.
    updateBinaryStream ( columnIndex,x ,length ) ;} @ Overridepublic void updateCharacterStream
    (
        intcolumnIndex,Readerx, intlength )throwsSQLException
    {

    _res.
    updateCharacterStream ( columnIndex,x ,length ) ;} @ Overridepublic void updateObject
    (
        intcolumnIndex,Objectx, intscaleOrLength )throwsSQLException
    {

    _res.
    updateObject ( columnIndex,x ,scaleOrLength ) ;} @ Overridepublic void updateObject
    (
        intcolumnIndex,Objectx) throwsSQLException {_res.
    updateObject

    (columnIndex
    , x );} @Override public voidupdateNull ( String
    columnLabel
        )throwsSQLException{_res. updateNull(columnLabel
    )

    ;}
    @ Override publicvoidupdateBoolean (String columnLabel ,
    boolean
        x)throwsSQLException{_res.
    updateBoolean

    (columnLabel
    , x );} @Override public voidupdateByte ( String
    columnLabel
        ,bytex)throwsSQLException {_res.
    updateByte

    (columnLabel
    , x );} @Override public voidupdateShort ( String
    columnLabel
        ,shortx)throwsSQLException {_res.
    updateShort

    (columnLabel
    , x );} @Override public voidupdateInt ( String
    columnLabel
        ,intx)throwsSQLException {_res.
    updateInt

    (columnLabel
    , x );} @Override public voidupdateLong ( String
    columnLabel
        ,longx)throwsSQLException {_res.
    updateLong

    (columnLabel
    , x );} @Override public voidupdateFloat ( String
    columnLabel
        ,floatx)throwsSQLException {_res.
    updateFloat

    (columnLabel
    , x );} @Override public voidupdateDouble ( String
    columnLabel
        ,doublex)throwsSQLException {_res.
    updateDouble

    (columnLabel
    , x );} @Override public voidupdateBigDecimal ( String
    columnLabel
        ,BigDecimalx)throwsSQLException {_res.
    updateBigDecimal

    (columnLabel
    , x );} @Override public voidupdateString ( String
    columnLabel
        ,Stringx)throwsSQLException {_res.
    updateString

    (columnLabel
    , x );} @Override public voidupdateBytes ( String
    columnLabel
        ,byte[]x) throwsSQLException{
    _res

    .updateBytes
    ( columnLabel ,x) ;} @Overridepublic voidupdateDate ( String
    columnLabel
        ,Datex)throwsSQLException {_res.
    updateDate

    (columnLabel
    , x );} @Override public voidupdateTime ( String
    columnLabel
        ,Timex)throwsSQLException {_res.
    updateTime

    (columnLabel
    , x );} @Override public voidupdateTimestamp ( String
    columnLabel
        ,Timestampx)throwsSQLException {_res.
    updateTimestamp

    (columnLabel
    , x );} @Override public voidupdateAsciiStream ( String
    columnLabel
        ,InputStreamx,intlength )throwsSQLException
    {

    _res.
    updateAsciiStream ( columnLabel,x ,length ) ;} @ Overridepublic void updateBinaryStream
    (
        StringcolumnLabel,InputStreamx, intlength )throwsSQLException
    {

    _res.
    updateBinaryStream ( columnLabel,x ,length ) ;} @ Overridepublic void updateCharacterStream
    (
        StringcolumnLabel,Readerreader, intlength )throwsSQLException
    {

    _res.
    updateCharacterStream ( columnLabel,reader ,length ) ;} @ Overridepublic void updateObject
    (
        StringcolumnLabel,Objectx, intscaleOrLength )throwsSQLException
    {

    _res.
    updateObject ( columnLabel,x ,scaleOrLength ) ;} @ Overridepublic void updateObject
    (
        StringcolumnLabel,Objectx) throwsSQLException {_res.
    updateObject

    (columnLabel
    , x );} @Override public voidinsertRow ( )
    throws
        SQLException{_res.insertRow( );}
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
        _res.getStatement();
    }

    @Override
    public Object getObject(int columnIndex ,
    Map
        < String,Class<?>
    >

    map)
    throws SQLException {return_res .getObject (columnIndex,map );}@Override publicRef getRef (
    int
        columnIndex )throwsSQLException{return_res .getRef(
    columnIndex

    );
    } @ OverridepublicBlob getBlob( int columnIndex
    )
        throws SQLException{return_res.getBlob(
    columnIndex

    );
    } @ OverridepublicClob getClob( int columnIndex
    )
        throws SQLException{return_res.getClob(
    columnIndex

    );
    } @ OverridepublicArray getArray( int columnIndex
    )
        throws SQLException{return_res.getArray(
    columnIndex

    );
    } @ OverridepublicObject getObject( String columnLabel
    ,
        Map <String,Class<?>
    >

    map)
    throws SQLException {return_res .getObject (columnLabel,map );}@Override publicRef getRef (
    String
        columnLabel )throwsSQLException{return_res .getRef(
    columnLabel

    );
    } @ OverridepublicBlob getBlob( String columnLabel
    )
        throws SQLException{return_res.getBlob(
    columnLabel

    );
    } @ OverridepublicClob getClob( String columnLabel
    )
        throws SQLException{return_res.getClob(
    columnLabel

    );
    } @ OverridepublicArray getArray( String columnLabel
    )
        throws SQLException{return_res.getArray(
    columnLabel

    );
    } @ OverridepublicDate getDate( int columnIndex
    ,
        Calendar cal)throwsSQLException{return_res
    .

    getDate(
    columnIndex , cal); }@ Override publicDate getDate (
    String
        columnLabel ,Calendarcal)throwsSQLException {return_res
    .

    getDate(
    columnLabel , cal); }@ Override publicTime getTime (
    int
        columnIndex ,Calendarcal)throwsSQLException {return_res
    .

    getTime(
    columnIndex , cal); }@ Override publicTime getTime (
    String
        columnLabel ,Calendarcal)throwsSQLException {return_res
    .

    getTime(
    columnLabel , cal); }@ Override publicTimestamp getTimestamp (
    int
        columnIndex ,Calendarcal)throwsSQLException {return_res
    .

    getTimestamp(
    columnIndex , cal); }@ Override publicTimestamp getTimestamp (
    String
        columnLabel ,Calendarcal)throwsSQLException {return_res
    .

    getTimestamp(
    columnLabel , cal); }@ Override publicURL getURL (
    int
        columnIndex )throwsSQLException{return_res .getURL(
    columnIndex

    );
    } @ OverridepublicURL getURL( String columnLabel
    )
        throws SQLException{return_res.getURL(
    columnLabel

    );
    } @ Overridepublicvoid updateRef( int columnIndex
    ,
        Ref x)throwsSQLException{_res.
    updateRef

    (columnIndex
    , x );} @Override public voidupdateRef ( String
    columnLabel
        ,Refx)throwsSQLException {_res.
    updateRef

    (columnLabel
    , x );} @Override public voidupdateBlob ( int
    columnIndex
        ,Blobx)throwsSQLException {_res.
    updateBlob

    (columnIndex
    , x );} @Override public voidupdateBlob ( String
    columnLabel
        ,Blobx)throwsSQLException {_res.
    updateBlob

    (columnLabel
    , x );} @Override public voidupdateClob ( int
    columnIndex
        ,Clobx)throwsSQLException {_res.
    updateClob

    (columnIndex
    , x );} @Override public voidupdateClob ( String
    columnLabel
        ,Clobx)throwsSQLException {_res.
    updateClob

    (columnLabel
    , x );} @Override public voidupdateArray ( int
    columnIndex
        ,Arrayx)throwsSQLException {_res.
    updateArray

    (columnIndex
    , x );} @Override public voidupdateArray ( String
    columnLabel
        ,Arrayx)throwsSQLException {_res.
    updateArray

    (columnLabel
    , x );} @Override public RowIdgetRowId ( int
    columnIndex
        )throwsSQLException{return_res .getRowId(
    columnIndex

    );
    } @ OverridepublicRowId getRowId( String columnLabel
    )
        throws SQLException{return_res.getRowId(
    columnLabel

    );
    } @ Overridepublicvoid updateRowId( int columnIndex
    ,
        RowId x)throwsSQLException{_res.
    updateRowId

    (columnIndex
    , x );} @Override public voidupdateRowId ( String
    columnLabel
        ,RowIdx)throwsSQLException {_res.
    updateRowId

    (columnLabel
    , x );} @Override public intgetHoldability ( )
    throws
        SQLException{return_res.getHoldability ();
    }

    @Override
    public boolean isClosed() throws SQLException
    {
        return _res.isClosed();
    }

    @Override
    public void updateNString(int columnIndex ,
    String
        nString )throwsSQLException{_res.
    updateNString

    (columnIndex
    , nString );} @Override public voidupdateNString ( String
    columnLabel
        ,StringnString)throwsSQLException {_res.
    updateNString

    (columnLabel
    , nString );} @Override public voidupdateNClob ( int
    columnIndex
        ,NClobnClob)throwsSQLException {_res.
    updateNClob

    (columnIndex
    , nClob );} @Override public voidupdateNClob ( String
    columnLabel
        ,NClobnClob)throwsSQLException {_res.
    updateNClob

    (columnLabel
    , nClob );} @Override public NClobgetNClob ( int
    columnIndex
        )throwsSQLException{return_res .getNClob(
    columnIndex

    );
    } @ OverridepublicNClob getNClob( String columnLabel
    )
        throws SQLException{return_res.getNClob(
    columnLabel

    );
    } @ OverridepublicSQLXML getSQLXML( int columnIndex
    )
        throws SQLException{return_res.getSQLXML(
    columnIndex

    );
    } @ OverridepublicSQLXML getSQLXML( String columnLabel
    )
        throws SQLException{return_res.getSQLXML(
    columnLabel

    );
    } @ Overridepublicvoid updateSQLXML( int columnIndex
    ,
        SQLXML xmlObject)throwsSQLException{_res.
    updateSQLXML

    (columnIndex
    , xmlObject );} @Override public voidupdateSQLXML ( String
    columnLabel
        ,SQLXMLxmlObject)throwsSQLException {_res.
    updateSQLXML

    (columnLabel
    , xmlObject );} @Override public StringgetNString ( int
    columnIndex
        )throwsSQLException{return_res .getNString(
    columnIndex

    );
    } @ OverridepublicString getNString( String columnLabel
    )
        throws SQLException{return_res.getNString(
    columnLabel

    );
    } @ OverridepublicReader getNCharacterStream( int columnIndex
    )
        throws SQLException{return_res.getNCharacterStream(
    columnIndex

    );
    } @ OverridepublicReader getNCharacterStream( String columnLabel
    )
        throws SQLException{return_res.getNCharacterStream(
    columnLabel

    );
    } @ Overridepublicvoid updateNCharacterStream( int columnIndex
    ,
        Reader x,longlength)throwsSQLException
    {

    _res.
    updateNCharacterStream ( columnIndex,x ,length ) ;} @ Overridepublic void updateNCharacterStream
    (
        StringcolumnLabel,Readerreader, longlength )throwsSQLException
    {

    _res.
    updateNCharacterStream ( columnLabel,reader ,length ) ;} @ Overridepublic void updateAsciiStream
    (
        intcolumnIndex,InputStreamx, longlength )throwsSQLException
    {

    _res.
    updateAsciiStream ( columnIndex,x ,length ) ;} @ Overridepublic void updateBinaryStream
    (
        intcolumnIndex,InputStreamx, longlength )throwsSQLException
    {

    _res.
    updateBinaryStream ( columnIndex,x ,length ) ;} @ Overridepublic void updateCharacterStream
    (
        intcolumnIndex,Readerx, longlength )throwsSQLException
    {

    _res.
    updateCharacterStream ( columnIndex,x ,length ) ;} @ Overridepublic void updateAsciiStream
    (
        StringcolumnLabel,InputStreamx, longlength )throwsSQLException
    {

    _res.
    updateAsciiStream ( columnLabel,x ,length ) ;} @ Overridepublic void updateBinaryStream
    (
        StringcolumnLabel,InputStreamx, longlength )throwsSQLException
    {

    _res.
    updateBinaryStream ( columnLabel,x ,length ) ;} @ Overridepublic void updateCharacterStream
    (
        StringcolumnLabel,Readerreader, longlength )throwsSQLException
    {

    _res.
    updateCharacterStream ( columnLabel,reader ,length ) ;} @ Overridepublic void updateBlob
    (
        intcolumnIndex,InputStreaminputStream, longlength )throwsSQLException
    {

    _res.
    updateBlob ( columnIndex,inputStream ,length ) ;} @ Overridepublic void updateBlob
    (
        StringcolumnLabel,InputStreaminputStream, longlength )throwsSQLException
    {

    _res.
    updateBlob ( columnLabel,inputStream ,length ) ;} @ Overridepublic void updateClob
    (
        intcolumnIndex,Readerreader, longlength )throwsSQLException
    {

    _res.
    updateClob ( columnIndex,reader ,length ) ;} @ Overridepublic void updateClob
    (
        StringcolumnLabel,Readerreader, longlength )throwsSQLException
    {

    _res.
    updateClob ( columnLabel,reader ,length ) ;} @ Overridepublic void updateNClob
    (
        intcolumnIndex,Readerreader, longlength )throwsSQLException
    {

    _res.
    updateNClob ( columnIndex,reader ,length ) ;} @ Overridepublic void updateNClob
    (
        StringcolumnLabel,Readerreader, longlength )throwsSQLException
    {

    _res.
    updateNClob ( columnLabel,reader ,length ) ;} @ Overridepublic void updateNCharacterStream
    (
        intcolumnIndex,Readerx) throwsSQLException {_res.
    updateNCharacterStream

    (columnIndex
    , x );} @Override public voidupdateNCharacterStream ( String
    columnLabel
        ,Readerreader)throwsSQLException {_res.
    updateNCharacterStream

    (columnLabel
    , reader );} @Override public voidupdateAsciiStream ( int
    columnIndex
        ,InputStreamx)throwsSQLException {_res.
    updateAsciiStream

    (columnIndex
    , x );} @Override public voidupdateBinaryStream ( int
    columnIndex
        ,InputStreamx)throwsSQLException {_res.
    updateBinaryStream

    (columnIndex
    , x );} @Override public voidupdateCharacterStream ( int
    columnIndex
        ,Readerx)throwsSQLException {_res.
    updateCharacterStream

    (columnIndex
    , x );} @Override public voidupdateAsciiStream ( String
    columnLabel
        ,InputStreamx)throwsSQLException {_res.
    updateAsciiStream

    (columnLabel
    , x );} @Override public voidupdateBinaryStream ( String
    columnLabel
        ,InputStreamx)throwsSQLException {_res.
    updateBinaryStream

    (columnLabel
    , x );} @Override public voidupdateCharacterStream ( String
    columnLabel
        ,Readerreader)throwsSQLException {_res.
    updateCharacterStream

    (columnLabel
    , reader );} @Override public voidupdateBlob ( int
    columnIndex
        ,InputStreaminputStream)throwsSQLException {_res.
    updateBlob

    (columnIndex
    , inputStream );} @Override public voidupdateBlob ( String
    columnLabel
        ,InputStreaminputStream)throwsSQLException {_res.
    updateBlob

    (columnLabel
    , inputStream );} @Override public voidupdateClob ( int
    columnIndex
        ,Readerreader)throwsSQLException {_res.
    updateClob

    (columnIndex
    , reader );} @Override public voidupdateClob ( String
    columnLabel
        ,Readerreader)throwsSQLException {_res.
    updateClob

    (columnLabel
    , reader );} @Override public voidupdateNClob ( int
    columnIndex
        ,Readerreader)throwsSQLException {_res.
    updateNClob

    (columnIndex
    , reader );} @Override public voidupdateNClob ( String
    columnLabel
        ,Readerreader)throwsSQLException {_res.
    updateNClob

    (columnLabel
    , reader );} @Override public <T > T
    getObject
        (intcolumnIndex,Class< T>type
    )

    throwsSQLException
    { return_res. getObject (columnIndex, type) ;}@Override public< T >
    T
        getObject (StringcolumnLabel,Class< T>type
    )

    throwsSQLException
    { return_res. getObject (columnLabel, type) ;}@Override publicvoid updateObject (
    int
        columnIndex ,Objectx,SQLTypetargetSqlType ,intscaleOrLength
    )

    throwsSQLException
    { _res .updateObject( columnIndex, x ,targetSqlType , scaleOrLength) ; }@ Override public
    void
        updateObject(StringcolumnLabel,Object x, SQLTypetargetSqlType ,intscaleOrLength
    )

    throwsSQLException
    { _res .updateObject( columnLabel, x ,targetSqlType , scaleOrLength) ; }@ Override public
    void
        updateObject(intcolumnIndex,Object x, SQLTypetargetSqlType )throwsSQLException
    {

    _res.
    updateObject ( columnIndex,x ,targetSqlType ) ;} @ Overridepublic void updateObject
    (
        StringcolumnLabel,Objectx, SQLTypetargetSqlType )throwsSQLException
    {

    _res.
    updateObject ( columnLabel,x ,targetSqlType ) ;} @ Overridepublic < T
    >
        Tunwrap(Class<T >iface )throwsSQLException
    {

    return_res
    . unwrap(iface ) ;}@Overridepublicboolean isWrapperFor( Class <
    ?
        > iface)throwsSQLException{return_res
    .

    isWrapperFor(
    iface ) ;}}