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
    getTimestamp( StringcolumnLabel )throwsSQLException{return_res .getTimestamp(columnLabel ); }@
    Overridepublic
        InputStreamgetAsciiStream(StringcolumnLabel)throwsSQLException{return_res.
    getAsciiStream

    (columnLabel
    ) ; }@Override @Deprecated public InputStream
    getUnicodeStream
        ( StringcolumnLabel)throwsSQLException{return
    _res

    .getUnicodeStream
    (columnLabel
    ) ; }@Override publicInputStream getBinaryStream (
    String
        columnLabel )throwsSQLException{return_res.
    getBinaryStream

    (columnLabel
    ) ; }@Override publicSQLWarning getWarnings (
    )
        throws SQLException{return_res.getWarnings(
    )

    ;}
    @ Override publicvoidclearWarnings ( )
    throws
        SQLException {_res.clearWarnings()
    ;

    }@
    Override public StringgetCursorName( ) throws
    SQLException
        {return_res.getCursorName(
    )

    ;}
    @ Override publicResultSetMetaDatagetMetaData ( )
    throws
        SQLException {return_res.getMetaData(
    )

    ;}
    @ Override publicObjectgetObject ( int
    columnIndex
        ) throwsSQLException{return_res.
    getObject

    (columnIndex
    ) ; }@Override publicObject getObject (
    String
        columnLabel )throwsSQLException{return_res.
    getObject

    (columnLabel
    ) ; }@Override publicint findColumn (
    String
        columnLabel )throwsSQLException{return_res.
    findColumn

    (columnLabel
    ) ; }@Override publicReader getCharacterStream (
    int
        columnIndex )throwsSQLException{return_res.
    getCharacterStream

    (columnIndex
    ) ; }@Override publicReader getCharacterStream (
    String
        columnLabel )throwsSQLException{return_res.
    getCharacterStream

    (columnLabel
    ) ; }@Override publicBigDecimal getBigDecimal (
    int
        columnIndex )throwsSQLException{return_res.
    getBigDecimal

    (columnIndex
    ) ; }@Override publicBigDecimal getBigDecimal (
    String
        columnLabel )throwsSQLException{return_res.
    getBigDecimal

    (columnLabel
    ) ; }@Override publicboolean isBeforeFirst (
    )
        throws SQLException{return_res.isBeforeFirst(
    )

    ;}
    @ Override publicbooleanisAfterLast ( )
    throws
        SQLException {return_res.isAfterLast(
    )

    ;}
    @ Override publicbooleanisFirst ( )
    throws
        SQLException {return_res.isFirst(
    )

    ;}
    @ Override publicbooleanisLast ( )
    throws
        SQLException {return_res.isLast(
    )

    ;}
    @ Override publicvoidbeforeFirst ( )
    throws
        SQLException {_res.beforeFirst()
    ;

    }@
    Override public voidafterLast( ) throws
    SQLException
        {_res.afterLast()
    ;

    }@
    Override public booleanfirst( ) throws
    SQLException
        {return_res.first(
    )

    ;}
    @ Override publicbooleanlast ( )
    throws
        SQLException {return_res.last(
    )

    ;}
    @ Override publicintgetRow ( )
    throws
        SQLException {return_res.getRow(
    )

    ;}
    @ Override publicbooleanabsolute ( int
    row
        ) throwsSQLException{return_res.
    absolute

    (row
    ) ; }@Override publicboolean relative (
    int
        rows )throwsSQLException{return_res.
    relative

    (rows
    ) ; }@Override publicboolean previous (
    )
        throws SQLException{return_res.previous(
    )

    ;}
    @ Override publicvoidsetFetchDirection ( int
    direction
        ) throwsSQLException{_res.setFetchDirection
    (

    direction)
    ; } @Overridepublic intgetFetchDirection ( )
    throws
        SQLException{return_res.getFetchDirection(
    )

    ;}
    @ Override publicvoidsetFetchSize ( int
    rows
        ) throwsSQLException{_res.setFetchSize
    (

    rows)
    ; } @Overridepublic intgetFetchSize ( )
    throws
        SQLException{return_res.getFetchSize(
    )

    ;}
    @ Override publicintgetType ( )
    throws
        SQLException {return_res.getType(
    )

    ;}
    @ Override publicintgetConcurrency ( )
    throws
        SQLException {return_res.getConcurrency(
    )

    ;}
    @ Override publicbooleanrowUpdated ( )
    throws
        SQLException {return_res.rowUpdated(
    )

    ;}
    @ Override publicbooleanrowInserted ( )
    throws
        SQLException {return_res.rowInserted(
    )

    ;}
    @ Override publicbooleanrowDeleted ( )
    throws
        SQLException {return_res.rowDeleted(
    )

    ;}
    @ Override publicvoidupdateNull ( int
    columnIndex
        ) throwsSQLException{_res.updateNull
    (

    columnIndex)
    ; } @Overridepublic voidupdateBoolean ( int
    columnIndex
        ,booleanx)throwsSQLException{
    _res

    .updateBoolean
    ( columnIndex ,x) ;} @ Overridepublic void updateByte
    (
        intcolumnIndex,bytex) throwsSQLException{
    _res

    .updateByte
    ( columnIndex ,x) ;} @ Overridepublic void updateShort
    (
        intcolumnIndex,shortx) throwsSQLException{
    _res

    .updateShort
    ( columnIndex ,x) ;} @ Overridepublic void updateInt
    (
        intcolumnIndex,intx) throwsSQLException{
    _res

    .updateInt
    ( columnIndex ,x) ;} @ Overridepublic void updateLong
    (
        intcolumnIndex,longx) throwsSQLException{
    _res

    .updateLong
    ( columnIndex ,x) ;} @ Overridepublic void updateFloat
    (
        intcolumnIndex,floatx) throwsSQLException{
    _res

    .updateFloat
    ( columnIndex ,x) ;} @ Overridepublic void updateDouble
    (
        intcolumnIndex,doublex) throwsSQLException{
    _res

    .updateDouble
    ( columnIndex ,x) ;} @ Overridepublic void updateBigDecimal
    (
        intcolumnIndex,BigDecimalx) throwsSQLException{
    _res

    .updateBigDecimal
    ( columnIndex ,x) ;} @ Overridepublic void updateString
    (
        intcolumnIndex,Stringx) throwsSQLException{
    _res

    .updateString
    ( columnIndex ,x) ;} @ Overridepublic void updateBytes
    (
        intcolumnIndex,byte[] x)throws
    SQLException

    {_res
    . updateBytes (columnIndex, x) ;}@ Overridepublic void updateDate
    (
        intcolumnIndex,Datex) throwsSQLException{
    _res

    .updateDate
    ( columnIndex ,x) ;} @ Overridepublic void updateTime
    (
        intcolumnIndex,Timex) throwsSQLException{
    _res

    .updateTime
    ( columnIndex ,x) ;} @ Overridepublic void updateTimestamp
    (
        intcolumnIndex,Timestampx) throwsSQLException{
    _res

    .updateTimestamp
    ( columnIndex ,x) ;} @ Overridepublic void updateAsciiStream
    (
        intcolumnIndex,InputStreamx, intlength)
    throws

    SQLException{
    _res . updateAsciiStream(columnIndex ,x , length) ; }@ Override public
    void
        updateBinaryStream(intcolumnIndex,InputStream x, intlength)
    throws

    SQLException{
    _res . updateBinaryStream(columnIndex ,x , length) ; }@ Override public
    void
        updateCharacterStream(intcolumnIndex,Reader x, intlength)
    throws

    SQLException{
    _res . updateCharacterStream(columnIndex ,x , length) ; }@ Override public
    void
        updateObject(intcolumnIndex,Object x, intscaleOrLength)
    throws

    SQLException{
    _res . updateObject(columnIndex ,x , scaleOrLength) ; }@ Override public
    void
        updateObject(intcolumnIndex,Object x) throwsSQLException{
    _res

    .updateObject
    ( columnIndex ,x) ;} @ Overridepublic void updateNull
    (
        StringcolumnLabel)throwsSQLException{ _res.updateNull
    (

    columnLabel)
    ; } @Overridepublic voidupdateBoolean ( String
    columnLabel
        ,booleanx)throwsSQLException{
    _res

    .updateBoolean
    ( columnLabel ,x) ;} @ Overridepublic void updateByte
    (
        StringcolumnLabel,bytex) throwsSQLException{
    _res

    .updateByte
    ( columnLabel ,x) ;} @ Overridepublic void updateShort
    (
        StringcolumnLabel,shortx) throwsSQLException{
    _res

    .updateShort
    ( columnLabel ,x) ;} @ Overridepublic void updateInt
    (
        StringcolumnLabel,intx) throwsSQLException{
    _res

    .updateInt
    ( columnLabel ,x) ;} @ Overridepublic void updateLong
    (
        StringcolumnLabel,longx) throwsSQLException{
    _res

    .updateLong
    ( columnLabel ,x) ;} @ Overridepublic void updateFloat
    (
        StringcolumnLabel,floatx) throwsSQLException{
    _res

    .updateFloat
    ( columnLabel ,x) ;} @ Overridepublic void updateDouble
    (
        StringcolumnLabel,doublex) throwsSQLException{
    _res

    .updateDouble
    ( columnLabel ,x) ;} @ Overridepublic void updateBigDecimal
    (
        StringcolumnLabel,BigDecimalx) throwsSQLException{
    _res

    .updateBigDecimal
    ( columnLabel ,x) ;} @ Overridepublic void updateString
    (
        StringcolumnLabel,Stringx) throwsSQLException{
    _res

    .updateString
    ( columnLabel ,x) ;} @ Overridepublic void updateBytes
    (
        StringcolumnLabel,byte[] x)throws
    SQLException

    {_res
    . updateBytes (columnLabel, x) ;}@ Overridepublic void updateDate
    (
        StringcolumnLabel,Datex) throwsSQLException{
    _res

    .updateDate
    ( columnLabel ,x) ;} @ Overridepublic void updateTime
    (
        StringcolumnLabel,Timex) throwsSQLException{
    _res

    .updateTime
    ( columnLabel ,x) ;} @ Overridepublic void updateTimestamp
    (
        StringcolumnLabel,Timestampx) throwsSQLException{
    _res

    .updateTimestamp
    ( columnLabel ,x) ;} @ Overridepublic void updateAsciiStream
    (
        StringcolumnLabel,InputStreamx, intlength)
    throws

    SQLException{
    _res . updateAsciiStream(columnLabel ,x , length) ; }@ Override public
    void
        updateBinaryStream(StringcolumnLabel,InputStream x, intlength)
    throws

    SQLException{
    _res . updateBinaryStream(columnLabel ,x , length) ; }@ Override public
    void
        updateCharacterStream(StringcolumnLabel,Reader reader, intlength)
    throws

    SQLException{
    _res . updateCharacterStream(columnLabel ,reader , length) ; }@ Override public
    void
        updateObject(StringcolumnLabel,Object x, intscaleOrLength)
    throws

    SQLException{
    _res . updateObject(columnLabel ,x , scaleOrLength) ; }@ Override public
    void
        updateObject(StringcolumnLabel,Object x) throwsSQLException{
    _res

    .updateObject
    ( columnLabel ,x) ;} @ Overridepublic void insertRow
    (
        )throwsSQLException{_res. insertRow()
    ;

    }@
    Override public voidupdateRow( ) throws
    SQLException
        {_res.updateRow()
    ;

    }@
    Override public voiddeleteRow( ) throws
    SQLException
        {_res.deleteRow()
    ;

    }@
    Override public voidrefreshRow( ) throws
    SQLException
        {_res.refreshRow()
    ;

    }@
    Override public voidcancelRowUpdates( ) throws
    SQLException
        {_res.cancelRowUpdates()
    ;

    }@
    Override public voidmoveToInsertRow( ) throws
    SQLException
        {_res.moveToInsertRow()
    ;

    }@
    Override public voidmoveToCurrentRow( ) throws
    SQLException
        {_res.moveToCurrentRow()
    ;

    }@
    Override public StatementgetStatement( ) throws
    SQLException
        {return_res.getStatement(
    )

    ;}
    @ Override publicObjectgetObject ( int
    columnIndex
        , Map<String,Class<
    ?

    >>
    map ) throwsSQLException{ return_res .getObject(columnIndex ,map);} @Override public Ref
    getRef
        ( intcolumnIndex)throwsSQLException{ return_res.
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
    ) ; }@Override publicObject getObject (
    String
        columnLabel ,Map<String,Class<
    ?

    >>
    map ) throwsSQLException{ return_res .getObject(columnLabel ,map);} @Override public Ref
    getRef
        ( StringcolumnLabel)throwsSQLException{ return_res.
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
    ) ; }@Override publicDate getDate (
    int
        columnIndex ,Calendarcal)throwsSQLException{
    return

    _res.
    getDate ( columnIndex,cal ); } @Override public Date
    getDate
        ( StringcolumnLabel,Calendarcal) throwsSQLException{
    return

    _res.
    getDate ( columnLabel,cal ); } @Override public Time
    getTime
        ( intcolumnIndex,Calendarcal) throwsSQLException{
    return

    _res.
    getTime ( columnIndex,cal ); } @Override public Time
    getTime
        ( StringcolumnLabel,Calendarcal) throwsSQLException{
    return

    _res.
    getTime ( columnLabel,cal ); } @Override public Timestamp
    getTimestamp
        ( intcolumnIndex,Calendarcal) throwsSQLException{
    return

    _res.
    getTimestamp ( columnIndex,cal ); } @Override public Timestamp
    getTimestamp
        ( StringcolumnLabel,Calendarcal) throwsSQLException{
    return

    _res.
    getTimestamp ( columnLabel,cal ); } @Override public URL
    getURL
        ( intcolumnIndex)throwsSQLException{ return_res.
    getURL

    (columnIndex
    ) ; }@Override publicURL getURL (
    String
        columnLabel )throwsSQLException{return_res.
    getURL

    (columnLabel
    ) ; }@Override publicvoid updateRef (
    int
        columnIndex ,Refx)throwsSQLException{
    _res

    .updateRef
    ( columnIndex ,x) ;} @ Overridepublic void updateRef
    (
        StringcolumnLabel,Refx) throwsSQLException{
    _res

    .updateRef
    ( columnLabel ,x) ;} @ Overridepublic void updateBlob
    (
        intcolumnIndex,Blobx) throwsSQLException{
    _res

    .updateBlob
    ( columnIndex ,x) ;} @ Overridepublic void updateBlob
    (
        StringcolumnLabel,Blobx) throwsSQLException{
    _res

    .updateBlob
    ( columnLabel ,x) ;} @ Overridepublic void updateClob
    (
        intcolumnIndex,Clobx) throwsSQLException{
    _res

    .updateClob
    ( columnIndex ,x) ;} @ Overridepublic void updateClob
    (
        StringcolumnLabel,Clobx) throwsSQLException{
    _res

    .updateClob
    ( columnLabel ,x) ;} @ Overridepublic void updateArray
    (
        intcolumnIndex,Arrayx) throwsSQLException{
    _res

    .updateArray
    ( columnIndex ,x) ;} @ Overridepublic void updateArray
    (
        StringcolumnLabel,Arrayx) throwsSQLException{
    _res

    .updateArray
    ( columnLabel ,x) ;} @ Overridepublic RowId getRowId
    (
        intcolumnIndex)throwsSQLException{ return_res.
    getRowId

    (columnIndex
    ) ; }@Override publicRowId getRowId (
    String
        columnLabel )throwsSQLException{return_res.
    getRowId

    (columnLabel
    ) ; }@Override publicvoid updateRowId (
    int
        columnIndex ,RowIdx)throwsSQLException{
    _res

    .updateRowId
    ( columnIndex ,x) ;} @ Overridepublic void updateRowId
    (
        StringcolumnLabel,RowIdx) throwsSQLException{
    _res

    .updateRowId
    ( columnLabel ,x) ;} @ Overridepublic int getHoldability
    (
        )throwsSQLException{return_res .getHoldability(
    )

    ;}
    @ Override publicbooleanisClosed ( )
    throws
        SQLException {return_res.isClosed(
    )

    ;}
    @ Override publicvoidupdateNString ( int
    columnIndex
        , StringnString)throwsSQLException{
    _res

    .updateNString
    ( columnIndex ,nString) ;} @ Overridepublic void updateNString
    (
        StringcolumnLabel,StringnString) throwsSQLException{
    _res

    .updateNString
    ( columnLabel ,nString) ;} @ Overridepublic void updateNClob
    (
        intcolumnIndex,NClobnClob) throwsSQLException{
    _res

    .updateNClob
    ( columnIndex ,nClob) ;} @ Overridepublic void updateNClob
    (
        StringcolumnLabel,NClobnClob) throwsSQLException{
    _res

    .updateNClob
    ( columnLabel ,nClob) ;} @ Overridepublic NClob getNClob
    (
        intcolumnIndex)throwsSQLException{ return_res.
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
    ) ; }@Override publicvoid updateSQLXML (
    int
        columnIndex ,SQLXMLxmlObject)throwsSQLException{
    _res

    .updateSQLXML
    ( columnIndex ,xmlObject) ;} @ Overridepublic void updateSQLXML
    (
        StringcolumnLabel,SQLXMLxmlObject) throwsSQLException{
    _res

    .updateSQLXML
    ( columnLabel ,xmlObject) ;} @ Overridepublic String getNString
    (
        intcolumnIndex)throwsSQLException{ return_res.
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
    ) ; }@Override publicvoid updateNCharacterStream (
    int
        columnIndex ,Readerx,longlength)
    throws

    SQLException{
    _res . updateNCharacterStream(columnIndex ,x , length) ; }@ Override public
    void
        updateNCharacterStream(StringcolumnLabel,Reader reader, longlength)
    throws

    SQLException{
    _res . updateNCharacterStream(columnLabel ,reader , length) ; }@ Override public
    void
        updateAsciiStream(intcolumnIndex,InputStream x, longlength)
    throws

    SQLException{
    _res . updateAsciiStream(columnIndex ,x , length) ; }@ Override public
    void
        updateBinaryStream(intcolumnIndex,InputStream x, longlength)
    throws

    SQLException{
    _res . updateBinaryStream(columnIndex ,x , length) ; }@ Override public
    void
        updateCharacterStream(intcolumnIndex,Reader x, longlength)
    throws

    SQLException{
    _res . updateCharacterStream(columnIndex ,x , length) ; }@ Override public
    void
        updateAsciiStream(StringcolumnLabel,InputStream x, longlength)
    throws

    SQLException{
    _res . updateAsciiStream(columnLabel ,x , length) ; }@ Override public
    void
        updateBinaryStream(StringcolumnLabel,InputStream x, longlength)
    throws

    SQLException{
    _res . updateBinaryStream(columnLabel ,x , length) ; }@ Override public
    void
        updateCharacterStream(StringcolumnLabel,Reader reader, longlength)
    throws

    SQLException{
    _res . updateCharacterStream(columnLabel ,reader , length) ; }@ Override public
    void
        updateBlob(intcolumnIndex,InputStream inputStream, longlength)
    throws

    SQLException{
    _res . updateBlob(columnIndex ,inputStream , length) ; }@ Override public
    void
        updateBlob(StringcolumnLabel,InputStream inputStream, longlength)
    throws

    SQLException{
    _res . updateBlob(columnLabel ,inputStream , length) ; }@ Override public
    void
        updateClob(intcolumnIndex,Reader reader, longlength)
    throws

    SQLException{
    _res . updateClob(columnIndex ,reader , length) ; }@ Override public
    void
        updateClob(StringcolumnLabel,Reader reader, longlength)
    throws

    SQLException{
    _res . updateClob(columnLabel ,reader , length) ; }@ Override public
    void
        updateNClob(intcolumnIndex,Reader reader, longlength)
    throws

    SQLException{
    _res . updateNClob(columnIndex ,reader , length) ; }@ Override public
    void
        updateNClob(StringcolumnLabel,Reader reader, longlength)
    throws

    SQLException{
    _res . updateNClob(columnLabel ,reader , length) ; }@ Override public
    void
        updateNCharacterStream(intcolumnIndex,Reader x) throwsSQLException{
    _res

    .updateNCharacterStream
    ( columnIndex ,x) ;} @ Overridepublic void updateNCharacterStream
    (
        StringcolumnLabel,Readerreader) throwsSQLException{
    _res

    .updateNCharacterStream
    ( columnLabel ,reader) ;} @ Overridepublic void updateAsciiStream
    (
        intcolumnIndex,InputStreamx) throwsSQLException{
    _res

    .updateAsciiStream
    ( columnIndex ,x) ;} @ Overridepublic void updateBinaryStream
    (
        intcolumnIndex,InputStreamx) throwsSQLException{
    _res

    .updateBinaryStream
    ( columnIndex ,x) ;} @ Overridepublic void updateCharacterStream
    (
        intcolumnIndex,Readerx) throwsSQLException{
    _res

    .updateCharacterStream
    ( columnIndex ,x) ;} @ Overridepublic void updateAsciiStream
    (
        StringcolumnLabel,InputStreamx) throwsSQLException{
    _res

    .updateAsciiStream
    ( columnLabel ,x) ;} @ Overridepublic void updateBinaryStream
    (
        StringcolumnLabel,InputStreamx) throwsSQLException{
    _res

    .updateBinaryStream
    ( columnLabel ,x) ;} @ Overridepublic void updateCharacterStream
    (
        StringcolumnLabel,Readerreader) throwsSQLException{
    _res

    .updateCharacterStream
    ( columnLabel ,reader) ;} @ Overridepublic void updateBlob
    (
        intcolumnIndex,InputStreaminputStream) throwsSQLException{
    _res

    .updateBlob
    ( columnIndex ,inputStream) ;} @ Overridepublic void updateBlob
    (
        StringcolumnLabel,InputStreaminputStream) throwsSQLException{
    _res

    .updateBlob
    ( columnLabel ,inputStream) ;} @ Overridepublic void updateClob
    (
        intcolumnIndex,Readerreader) throwsSQLException{
    _res

    .updateClob
    ( columnIndex ,reader) ;} @ Overridepublic void updateClob
    (
        StringcolumnLabel,Readerreader) throwsSQLException{
    _res

    .updateClob
    ( columnLabel ,reader) ;} @ Overridepublic void updateNClob
    (
        intcolumnIndex,Readerreader) throwsSQLException{
    _res

    .updateNClob
    ( columnIndex ,reader) ;} @ Overridepublic void updateNClob
    (
        StringcolumnLabel,Readerreader) throwsSQLException{
    _res

    .updateNClob
    ( columnLabel ,reader) ;} @ Overridepublic < T
    >
        TgetObject(intcolumnIndex, Class<T
    >

    type)
    throws SQLException{return _res .getObject( columnIndex, type);} @Override public <
    T
        > TgetObject(StringcolumnLabel, Class<T
    >

    type)
    throws SQLException{return _res .getObject( columnLabel, type);} @Override public void
    updateObject
        ( intcolumnIndex,Objectx, SQLTypetargetSqlType,
    int

    scaleOrLength)
    throws SQLException {_res. updateObject( columnIndex ,x , targetSqlType, scaleOrLength ); } @
    Override
        publicvoidupdateObject(StringcolumnLabel ,Object x, SQLTypetargetSqlType,
    int

    scaleOrLength)
    throws SQLException {_res. updateObject( columnLabel ,x , targetSqlType, scaleOrLength ); } @
    Override
        publicvoidupdateObject(intcolumnIndex ,Object x, SQLTypetargetSqlType)
    throws

    SQLException{
    _res . updateObject(columnIndex ,x , targetSqlType) ; }@ Override public
    void
        updateObject(StringcolumnLabel,Object x, SQLTypetargetSqlType)
    throws

    SQLException{
    _res . updateObject(columnLabel ,x , targetSqlType) ; }@ Override public
    <
        T>Tunwrap(Class <T >iface)
    throws

    SQLException{
    return _res.unwrap ( iface);}@Override publicboolean isWrapperFor (
    Class
        < ?>iface)throwsSQLException{
    return

    _res.
    isWrapperFor ( iface);}}