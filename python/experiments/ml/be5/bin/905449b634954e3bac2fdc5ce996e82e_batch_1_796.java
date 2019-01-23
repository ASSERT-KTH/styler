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
    public
    String
    getString ( intcolumnIndex) throwsSQLException { return
    _res
        . getString(columnIndex);}@
    Override

    publicboolean
    getBoolean ( intcolumnIndex) throwsSQLException { return
    _res
        . getBoolean(columnIndex);}@
    Override

    publicbyte
    getByte ( intcolumnIndex) throwsSQLException { return
    _res
        . getByte(columnIndex);}@
    Override

    publicshort
    getShort ( intcolumnIndex) throwsSQLException { return
    _res
        . getShort(columnIndex);}@
    Override

    publicint
    getInt ( intcolumnIndex) throwsSQLException { return
    _res
        . getInt(columnIndex);}@
    Override

    publiclong
    getLong ( intcolumnIndex) throwsSQLException { return
    _res
        . getLong(columnIndex);}@
    Override

    publicfloat
    getFloat ( intcolumnIndex) throwsSQLException { return
    _res
        . getFloat(columnIndex);}@
    Override

    publicdouble
    getDouble ( intcolumnIndex) throwsSQLException { return
    _res
        . getDouble(columnIndex);}@
    Override

    @Deprecated
    publicBigDecimal
    getBigDecimal ( intcolumnIndex, intscale ) throwsSQLException { return
    _res
        . getBigDecimal(columnIndex,scale) ;}@
    Override

    publicbyte
    [ ]getBytes( intcolumnIndex) throwsSQLException { return
    _res
        . getBytes(columnIndex);}@
    Override

    publicDate
    getDate ( intcolumnIndex) throwsSQLException { return
    _res
        . getDate(columnIndex);}@
    Override

    publicTime
    getTime ( intcolumnIndex) throwsSQLException { return
    _res
        . getTime(columnIndex);}@
    Override

    publicTimestamp
    getTimestamp ( intcolumnIndex) throwsSQLException { return
    _res
        . getTimestamp(columnIndex);}@
    Override

    publicInputStream
    getAsciiStream ( intcolumnIndex) throwsSQLException { return
    _res
        . getAsciiStream(columnIndex);}@
    Override

    @Deprecated
    publicInputStream
    getUnicodeStream ( intcolumnIndex) throwsSQLException { return
    _res
        . getUnicodeStream(columnIndex);}@
    Override

    publicInputStream
    getBinaryStream ( intcolumnIndex) throwsSQLException { return
    _res
        . getBinaryStream(columnIndex);}@
    Override

    publicString
    getString ( StringcolumnLabel) throwsSQLException { return
    _res
        . getString(columnLabel);}@
    Override

    publicboolean
    getBoolean ( StringcolumnLabel) throwsSQLException { return
    _res
        . getBoolean(columnLabel);}@
    Override

    publicbyte
    getByte ( StringcolumnLabel) throwsSQLException { return
    _res
        . getByte(columnLabel);}@
    Override

    publicshort
    getShort ( StringcolumnLabel) throwsSQLException { return
    _res
        . getShort(columnLabel);}@
    Override

    publicint
    getInt ( StringcolumnLabel) throwsSQLException { return
    _res
        . getInt(columnLabel);}@
    Override

    publiclong
    getLong ( StringcolumnLabel) throwsSQLException { return
    _res
        . getLong(columnLabel);}@
    Override

    publicfloat
    getFloat ( StringcolumnLabel) throwsSQLException { return
    _res
        . getFloat(columnLabel);}@
    Override

    publicdouble
    getDouble ( StringcolumnLabel) throwsSQLException { return
    _res
        . getDouble(columnLabel);}@
    Override

    @Deprecated
    publicBigDecimal
    getBigDecimal ( StringcolumnLabel, intscale ) throwsSQLException { return
    _res
        . getBigDecimal(columnLabel,scale) ;}@
    Override

    publicbyte
    [ ]getBytes( StringcolumnLabel) throwsSQLException { return
    _res
        . getBytes(columnLabel);}@
    Override

    publicDate
    getDate ( StringcolumnLabel) throwsSQLException { return
    _res
        . getDate(columnLabel);}@
    Override

    publicTime
    getTime ( StringcolumnLabel) throwsSQLException { return
    _res
        . getTime(columnLabel);}@
    Override

    publicTimestamp
    getTimestamp ( StringcolumnLabel) throwsSQLException { return
    _res
        . getTimestamp(columnLabel);}@
    Override

    publicInputStream
    getAsciiStream ( StringcolumnLabel) throwsSQLException { return
    _res
        . getAsciiStream(columnLabel);}@
    Override

    @Deprecated
    publicInputStream
    getUnicodeStream ( StringcolumnLabel) throwsSQLException { return
    _res
        . getUnicodeStream(columnLabel);}@
    Override

    publicInputStream
    getBinaryStream ( StringcolumnLabel) throwsSQLException { return
    _res
        . getBinaryStream(columnLabel);}@
    Override

    publicSQLWarning
    getWarnings ( )throwsSQLException { return
    _res
        . getWarnings();}@
    Override

    publicvoid
    clearWarnings ( )throwsSQLException { _res
    .
        clearWarnings();}@
    Override

    publicString
    getCursorName ( )throwsSQLException { return
    _res
        . getCursorName();}@
    Override

    publicResultSetMetaData
    getMetaData ( )throwsSQLException { return
    _res
        . getMetaData();}@
    Override

    publicObject
    getObject ( intcolumnIndex) throwsSQLException { return
    _res
        . getObject(columnIndex);}@
    Override

    publicObject
    getObject ( StringcolumnLabel) throwsSQLException { return
    _res
        . getObject(columnLabel);}@
    Override

    publicint
    findColumn ( StringcolumnLabel) throwsSQLException { return
    _res
        . findColumn(columnLabel);}@
    Override

    publicReader
    getCharacterStream ( intcolumnIndex) throwsSQLException { return
    _res
        . getCharacterStream(columnIndex);}@
    Override

    publicReader
    getCharacterStream ( StringcolumnLabel) throwsSQLException { return
    _res
        . getCharacterStream(columnLabel);}@
    Override

    publicBigDecimal
    getBigDecimal ( intcolumnIndex) throwsSQLException { return
    _res
        . getBigDecimal(columnIndex);}@
    Override

    publicBigDecimal
    getBigDecimal ( StringcolumnLabel) throwsSQLException { return
    _res
        . getBigDecimal(columnLabel);}@
    Override

    publicboolean
    isBeforeFirst ( )throwsSQLException { return
    _res
        . isBeforeFirst();}@
    Override

    publicboolean
    isAfterLast ( )throwsSQLException { return
    _res
        . isAfterLast();}@
    Override

    publicboolean
    isFirst ( )throwsSQLException { return
    _res
        . isFirst();}@
    Override

    publicboolean
    isLast ( )throwsSQLException { return
    _res
        . isLast();}@
    Override

    publicvoid
    beforeFirst ( )throwsSQLException { _res
    .
        beforeFirst();}@
    Override

    publicvoid
    afterLast ( )throwsSQLException { _res
    .
        afterLast();}@
    Override

    publicboolean
    first ( )throwsSQLException { return
    _res
        . first();}@
    Override

    publicboolean
    last ( )throwsSQLException { return
    _res
        . last();}@
    Override

    publicint
    getRow ( )throwsSQLException { return
    _res
        . getRow();}@
    Override

    publicboolean
    absolute ( introw) throwsSQLException { return
    _res
        . absolute(row);}@
    Override

    publicboolean
    relative ( introws) throwsSQLException { return
    _res
        . relative(rows);}@
    Override

    publicboolean
    previous ( )throwsSQLException { return
    _res
        . previous();}@
    Override

    publicvoid
    setFetchDirection ( intdirection) throwsSQLException { _res
    .
        setFetchDirection(direction);}@
    Override

    publicint
    getFetchDirection ( )throwsSQLException { return
    _res
        . getFetchDirection();}@
    Override

    publicvoid
    setFetchSize ( introws) throwsSQLException { _res
    .
        setFetchSize(rows);}@
    Override

    publicint
    getFetchSize ( )throwsSQLException { return
    _res
        . getFetchSize();}@
    Override

    publicint
    getType ( )throwsSQLException { return
    _res
        . getType();}@
    Override

    publicint
    getConcurrency ( )throwsSQLException { return
    _res
        . getConcurrency();}@
    Override

    publicboolean
    rowUpdated ( )throwsSQLException { return
    _res
        . rowUpdated();}@
    Override

    publicboolean
    rowInserted ( )throwsSQLException { return
    _res
        . rowInserted();}@
    Override

    publicboolean
    rowDeleted ( )throwsSQLException { return
    _res
        . rowDeleted();}@
    Override

    publicvoid
    updateNull ( intcolumnIndex) throwsSQLException { _res
    .
        updateNull(columnIndex);}@
    Override

    publicvoid
    updateBoolean ( intcolumnIndex, booleanx ) throwsSQLException { _res
    .
        updateBoolean(columnIndex,x) ;}@
    Override

    publicvoid
    updateByte ( intcolumnIndex, bytex ) throwsSQLException { _res
    .
        updateByte(columnIndex,x) ;}@
    Override

    publicvoid
    updateShort ( intcolumnIndex, shortx ) throwsSQLException { _res
    .
        updateShort(columnIndex,x) ;}@
    Override

    publicvoid
    updateInt ( intcolumnIndex, intx ) throwsSQLException { _res
    .
        updateInt(columnIndex,x) ;}@
    Override

    publicvoid
    updateLong ( intcolumnIndex, longx ) throwsSQLException { _res
    .
        updateLong(columnIndex,x) ;}@
    Override

    publicvoid
    updateFloat ( intcolumnIndex, floatx ) throwsSQLException { _res
    .
        updateFloat(columnIndex,x) ;}@
    Override

    publicvoid
    updateDouble ( intcolumnIndex, doublex ) throwsSQLException { _res
    .
        updateDouble(columnIndex,x) ;}@
    Override

    publicvoid
    updateBigDecimal ( intcolumnIndex, BigDecimalx ) throwsSQLException { _res
    .
        updateBigDecimal(columnIndex,x) ;}@
    Override

    publicvoid
    updateString ( intcolumnIndex, Stringx ) throwsSQLException { _res
    .
        updateString(columnIndex,x) ;}@
    Override

    publicvoid
    updateBytes ( intcolumnIndex, byte[ ]x) throwsSQLException { _res
    .
        updateBytes(columnIndex,x) ;}@
    Override

    publicvoid
    updateDate ( intcolumnIndex, Datex ) throwsSQLException { _res
    .
        updateDate(columnIndex,x) ;}@
    Override

    publicvoid
    updateTime ( intcolumnIndex, Timex ) throwsSQLException { _res
    .
        updateTime(columnIndex,x) ;}@
    Override

    publicvoid
    updateTimestamp ( intcolumnIndex, Timestampx ) throwsSQLException { _res
    .
        updateTimestamp(columnIndex,x) ;}@
    Override

    publicvoid
    updateAsciiStream ( intcolumnIndex, InputStreamx , intlength ) throwsSQLException { _res
    .
        updateAsciiStream(columnIndex,x, length) ;}@
    Override

    publicvoid
    updateBinaryStream ( intcolumnIndex, InputStreamx , intlength ) throwsSQLException { _res
    .
        updateBinaryStream(columnIndex,x, length) ;}@
    Override

    publicvoid
    updateCharacterStream ( intcolumnIndex, Readerx , intlength ) throwsSQLException { _res
    .
        updateCharacterStream(columnIndex,x, length) ;}@
    Override

    publicvoid
    updateObject ( intcolumnIndex, Objectx , intscaleOrLength ) throwsSQLException { _res
    .
        updateObject(columnIndex,x, scaleOrLength) ;}@
    Override

    publicvoid
    updateObject ( intcolumnIndex, Objectx ) throwsSQLException { _res
    .
        updateObject(columnIndex,x) ;}@
    Override

    publicvoid
    updateNull ( StringcolumnLabel) throwsSQLException { _res
    .
        updateNull(columnLabel);}@
    Override

    publicvoid
    updateBoolean ( StringcolumnLabel, booleanx ) throwsSQLException { _res
    .
        updateBoolean(columnLabel,x) ;}@
    Override

    publicvoid
    updateByte ( StringcolumnLabel, bytex ) throwsSQLException { _res
    .
        updateByte(columnLabel,x) ;}@
    Override

    publicvoid
    updateShort ( StringcolumnLabel, shortx ) throwsSQLException { _res
    .
        updateShort(columnLabel,x) ;}@
    Override

    publicvoid
    updateInt ( StringcolumnLabel, intx ) throwsSQLException { _res
    .
        updateInt(columnLabel,x) ;}@
    Override

    publicvoid
    updateLong ( StringcolumnLabel, longx ) throwsSQLException { _res
    .
        updateLong(columnLabel,x) ;}@
    Override

    publicvoid
    updateFloat ( StringcolumnLabel, floatx ) throwsSQLException { _res
    .
        updateFloat(columnLabel,x) ;}@
    Override

    publicvoid
    updateDouble ( StringcolumnLabel, doublex ) throwsSQLException { _res
    .
        updateDouble(columnLabel,x) ;}@
    Override

    publicvoid
    updateBigDecimal ( StringcolumnLabel, BigDecimalx ) throwsSQLException { _res
    .
        updateBigDecimal(columnLabel,x) ;}@
    Override

    publicvoid
    updateString ( StringcolumnLabel, Stringx ) throwsSQLException { _res
    .
        updateString(columnLabel,x) ;}@
    Override

    publicvoid
    updateBytes ( StringcolumnLabel, byte[ ]x) throwsSQLException { _res
    .
        updateBytes(columnLabel,x) ;}@
    Override

    publicvoid
    updateDate ( StringcolumnLabel, Datex ) throwsSQLException { _res
    .
        updateDate(columnLabel,x) ;}@
    Override

    publicvoid
    updateTime ( StringcolumnLabel, Timex ) throwsSQLException { _res
    .
        updateTime(columnLabel,x) ;}@
    Override

    publicvoid
    updateTimestamp ( StringcolumnLabel, Timestampx ) throwsSQLException { _res
    .
        updateTimestamp(columnLabel,x) ;}@
    Override

    publicvoid
    updateAsciiStream ( StringcolumnLabel, InputStreamx , intlength ) throwsSQLException { _res
    .
        updateAsciiStream(columnLabel,x, length) ;}@
    Override

    publicvoid
    updateBinaryStream ( StringcolumnLabel, InputStreamx , intlength ) throwsSQLException { _res
    .
        updateBinaryStream(columnLabel,x, length) ;}@
    Override

    publicvoid
    updateCharacterStream ( StringcolumnLabel, Readerreader , intlength ) throwsSQLException { _res
    .
        updateCharacterStream(columnLabel,reader, length) ;}@
    Override

    publicvoid
    updateObject ( StringcolumnLabel, Objectx , intscaleOrLength ) throwsSQLException { _res
    .
        updateObject(columnLabel,x, scaleOrLength) ;}@
    Override

    publicvoid
    updateObject ( StringcolumnLabel, Objectx ) throwsSQLException { _res
    .
        updateObject(columnLabel,x) ;}@
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
        moveToCurrentRow();}@
    Override

    publicStatement
    getStatement ( )throwsSQLException { return
    _res
        . getStatement();}@
    Override

    publicObject
    getObject ( intcolumnIndex, Map< String,Class< ?>>map) throwsSQLException { return
    _res
        . getObject(columnIndex,map) ;}@
    Override

    publicRef
    getRef ( intcolumnIndex) throwsSQLException { return
    _res
        . getRef(columnIndex);}@
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
    getObject ( StringcolumnLabel, Map< String,Class< ?>>map) throwsSQLException { return
    _res
        . getObject(columnLabel,map) ;}@
    Override

    publicRef
    getRef ( StringcolumnLabel) throwsSQLException { return
    _res
        . getRef(columnLabel);}@
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
    getDate ( intcolumnIndex, Calendarcal ) throwsSQLException { return
    _res
        . getDate(columnIndex,cal) ;}@
    Override

    publicDate
    getDate ( StringcolumnLabel, Calendarcal ) throwsSQLException { return
    _res
        . getDate(columnLabel,cal) ;}@
    Override

    publicTime
    getTime ( intcolumnIndex, Calendarcal ) throwsSQLException { return
    _res
        . getTime(columnIndex,cal) ;}@
    Override

    publicTime
    getTime ( StringcolumnLabel, Calendarcal ) throwsSQLException { return
    _res
        . getTime(columnLabel,cal) ;}@
    Override

    publicTimestamp
    getTimestamp ( intcolumnIndex, Calendarcal ) throwsSQLException { return
    _res
        . getTimestamp(columnIndex,cal) ;}@
    Override

    publicTimestamp
    getTimestamp ( StringcolumnLabel, Calendarcal ) throwsSQLException { return
    _res
        . getTimestamp(columnLabel,cal) ;}@
    Override

    publicURL
    getURL ( intcolumnIndex) throwsSQLException { return
    _res
        . getURL(columnIndex);}@
    Override

    publicURL
    getURL ( StringcolumnLabel) throwsSQLException { return
    _res
        . getURL(columnLabel);}@
    Override

    publicvoid
    updateRef ( intcolumnIndex, Refx ) throwsSQLException { _res
    .
        updateRef(columnIndex,x) ;}@
    Override

    publicvoid
    updateRef ( StringcolumnLabel, Refx ) throwsSQLException { _res
    .
        updateRef(columnLabel,x) ;}@
    Override

    publicvoid
    updateBlob ( intcolumnIndex, Blobx ) throwsSQLException { _res
    .
        updateBlob(columnIndex,x) ;}@
    Override

    publicvoid
    updateBlob ( StringcolumnLabel, Blobx ) throwsSQLException { _res
    .
        updateBlob(columnLabel,x) ;}@
    Override

    publicvoid
    updateClob ( intcolumnIndex, Clobx ) throwsSQLException { _res
    .
        updateClob(columnIndex,x) ;}@
    Override

    publicvoid
    updateClob ( StringcolumnLabel, Clobx ) throwsSQLException { _res
    .
        updateClob(columnLabel,x) ;}@
    Override

    publicvoid
    updateArray ( intcolumnIndex, Arrayx ) throwsSQLException { _res
    .
        updateArray(columnIndex,x) ;}@
    Override

    publicvoid
    updateArray ( StringcolumnLabel, Arrayx ) throwsSQLException { _res
    .
        updateArray(columnLabel,x) ;}@
    Override

    publicRowId
    getRowId ( intcolumnIndex) throwsSQLException { return
    _res
        . getRowId(columnIndex);}@
    Override

    publicRowId
    getRowId ( StringcolumnLabel) throwsSQLException { return
    _res
        . getRowId(columnLabel);}@
    Override

    publicvoid
    updateRowId ( intcolumnIndex, RowIdx ) throwsSQLException { _res
    .
        updateRowId(columnIndex,x) ;}@
    Override

    publicvoid
    updateRowId ( StringcolumnLabel, RowIdx ) throwsSQLException { _res
    .
        updateRowId(columnLabel,x) ;}@
    Override

    publicint
    getHoldability ( )throwsSQLException { return
    _res
        . getHoldability();}@
    Override

    publicboolean
    isClosed ( )throwsSQLException { return
    _res
        . isClosed();}@
    Override

    publicvoid
    updateNString ( intcolumnIndex, StringnString ) throwsSQLException { _res
    .
        updateNString(columnIndex,nString) ;}@
    Override

    publicvoid
    updateNString ( StringcolumnLabel, StringnString ) throwsSQLException { _res
    .
        updateNString(columnLabel,nString) ;}@
    Override

    publicvoid
    updateNClob ( intcolumnIndex, NClobnClob ) throwsSQLException { _res
    .
        updateNClob(columnIndex,nClob) ;}@
    Override

    publicvoid
    updateNClob ( StringcolumnLabel, NClobnClob ) throwsSQLException { _res
    .
        updateNClob(columnLabel,nClob) ;}@
    Override

    publicNClob
    getNClob ( intcolumnIndex) throwsSQLException { return
    _res
        . getNClob(columnIndex);}@
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
    updateSQLXML ( intcolumnIndex, SQLXMLxmlObject ) throwsSQLException { _res
    .
        updateSQLXML(columnIndex,xmlObject) ;}@
    Override

    publicvoid
    updateSQLXML ( StringcolumnLabel, SQLXMLxmlObject ) throwsSQLException { _res
    .
        updateSQLXML(columnLabel,xmlObject) ;}@
    Override

    publicString
    getNString ( intcolumnIndex) throwsSQLException { return
    _res
        . getNString(columnIndex);}@
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
    updateNCharacterStream ( intcolumnIndex, Readerx , longlength ) throwsSQLException { _res
    .
        updateNCharacterStream(columnIndex,x, length) ;}@
    Override

    publicvoid
    updateNCharacterStream ( StringcolumnLabel, Readerreader , longlength ) throwsSQLException { _res
    .
        updateNCharacterStream(columnLabel,reader, length) ;}@
    Override

    publicvoid
    updateAsciiStream ( intcolumnIndex, InputStreamx , longlength ) throwsSQLException { _res
    .
        updateAsciiStream(columnIndex,x, length) ;}@
    Override

    publicvoid
    updateBinaryStream ( intcolumnIndex, InputStreamx , longlength ) throwsSQLException { _res
    .
        updateBinaryStream(columnIndex,x, length) ;}@
    Override

    publicvoid
    updateCharacterStream ( intcolumnIndex, Readerx , longlength ) throwsSQLException { _res
    .
        updateCharacterStream(columnIndex,x, length) ;}@
    Override

    publicvoid
    updateAsciiStream ( StringcolumnLabel, InputStreamx , longlength ) throwsSQLException { _res
    .
        updateAsciiStream(columnLabel,x, length) ;}@
    Override

    publicvoid
    updateBinaryStream ( StringcolumnLabel, InputStreamx , longlength ) throwsSQLException { _res
    .
        updateBinaryStream(columnLabel,x, length) ;}@
    Override

    publicvoid
    updateCharacterStream ( StringcolumnLabel, Readerreader , longlength ) throwsSQLException { _res
    .
        updateCharacterStream(columnLabel,reader, length) ;}@
    Override

    publicvoid
    updateBlob ( intcolumnIndex, InputStreaminputStream , longlength ) throwsSQLException { _res
    .
        updateBlob(columnIndex,inputStream, length) ;}@
    Override

    publicvoid
    updateBlob ( StringcolumnLabel, InputStreaminputStream , longlength ) throwsSQLException { _res
    .
        updateBlob(columnLabel,inputStream, length) ;}@
    Override

    publicvoid
    updateClob ( intcolumnIndex, Readerreader , longlength ) throwsSQLException { _res
    .
        updateClob(columnIndex,reader, length) ;}@
    Override

    publicvoid
    updateClob ( StringcolumnLabel, Readerreader , longlength ) throwsSQLException { _res
    .
        updateClob(columnLabel,reader, length) ;}@
    Override

    publicvoid
    updateNClob ( intcolumnIndex, Readerreader , longlength ) throwsSQLException { _res
    .
        updateNClob(columnIndex,reader, length) ;}@
    Override

    publicvoid
    updateNClob ( StringcolumnLabel, Readerreader , longlength ) throwsSQLException { _res
    .
        updateNClob(columnLabel,reader, length) ;}@
    Override

    publicvoid
    updateNCharacterStream ( intcolumnIndex, Readerx ) throwsSQLException { _res
    .
        updateNCharacterStream(columnIndex,x) ;}@
    Override

    publicvoid
    updateNCharacterStream ( StringcolumnLabel, Readerreader ) throwsSQLException { _res
    .
        updateNCharacterStream(columnLabel,reader) ;}@
    Override

    publicvoid
    updateAsciiStream ( intcolumnIndex, InputStreamx ) throwsSQLException { _res
    .
        updateAsciiStream(columnIndex,x) ;}@
    Override

    publicvoid
    updateBinaryStream ( intcolumnIndex, InputStreamx ) throwsSQLException { _res
    .
        updateBinaryStream(columnIndex,x) ;}@
    Override

    publicvoid
    updateCharacterStream ( intcolumnIndex, Readerx ) throwsSQLException { _res
    .
        updateCharacterStream(columnIndex,x) ;}@
    Override

    publicvoid
    updateAsciiStream ( StringcolumnLabel, InputStreamx ) throwsSQLException { _res
    .
        updateAsciiStream(columnLabel,x) ;}@
    Override

    publicvoid
    updateBinaryStream ( StringcolumnLabel, InputStreamx ) throwsSQLException { _res
    .
        updateBinaryStream(columnLabel,x) ;}@
    Override

    publicvoid
    updateCharacterStream ( StringcolumnLabel, Readerreader ) throwsSQLException { _res
    .
        updateCharacterStream(columnLabel,reader) ;}@
    Override

    publicvoid
    updateBlob ( intcolumnIndex, InputStreaminputStream ) throwsSQLException { _res
    .
        updateBlob(columnIndex,inputStream) ;}@
    Override

    publicvoid
    updateBlob ( StringcolumnLabel, InputStreaminputStream ) throwsSQLException { _res
    .
        updateBlob(columnLabel,inputStream) ;}@
    Override

    publicvoid
    updateClob ( intcolumnIndex, Readerreader ) throwsSQLException { _res
    .
        updateClob(columnIndex,reader) ;}@
    Override

    publicvoid
    updateClob ( StringcolumnLabel, Readerreader ) throwsSQLException { _res
    .
        updateClob(columnLabel,reader) ;}@
    Override

    publicvoid
    updateNClob ( intcolumnIndex, Readerreader ) throwsSQLException { _res
    .
        updateNClob(columnIndex,reader) ;}@
    Override

    publicvoid
    updateNClob ( StringcolumnLabel, Readerreader ) throwsSQLException { _res
    .
        updateNClob(columnLabel,reader) ;}@
    Override

    public<
    T >TgetObject ( intcolumnIndex, Class< T>type) throwsSQLException { return
    _res
        . getObject(columnIndex,type) ;}@
    Override

    public<
    T >TgetObject ( StringcolumnLabel, Class< T>type) throwsSQLException { return
    _res
        . getObject(columnLabel,type) ;}@
    Override

    publicvoid
    updateObject ( intcolumnIndex, Objectx , SQLTypetargetSqlType , intscaleOrLength ) throwsSQLException { _res
    .
        updateObject(columnIndex,x, targetSqlType, scaleOrLength) ;}@
    Override

    publicvoid
    updateObject ( StringcolumnLabel, Objectx , SQLTypetargetSqlType , intscaleOrLength ) throwsSQLException { _res
    .
        updateObject(columnLabel,x, targetSqlType, scaleOrLength) ;}@
    Override

    publicvoid
    updateObject ( intcolumnIndex, Objectx , SQLTypetargetSqlType ) throwsSQLException { _res
    .
        updateObject(columnIndex,x, targetSqlType) ;}@
    Override

    publicvoid
    updateObject ( StringcolumnLabel, Objectx , SQLTypetargetSqlType ) throwsSQLException { _res
    .
        updateObject(columnLabel,x, targetSqlType) ;}@
    Override

    public<
    T >Tunwrap ( Class<T>iface) throwsSQLException { return
    _res
        . unwrap(iface);}@
    Override

    publicboolean
    isWrapperFor ( Class<?>iface) throwsSQLException { return
    _res
        . isWrapperFor(iface);}}
    