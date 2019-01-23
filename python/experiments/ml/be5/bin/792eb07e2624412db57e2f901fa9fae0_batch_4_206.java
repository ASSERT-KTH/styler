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
        _res.updateCharacterStream(columnIndex ,x , length) ; }@ Override public
        void
            updateObject(intcolumnIndex,Object x, intscaleOrLength)
        throws

        SQLException{
        _res . updateObject(columnIndex ,x , scaleOrLength) ; }
        @
            OverridepublicvoidupdateObject(int columnIndex,Object
        x

        )throws
        SQLException { _res.updateObject (columnIndex , x
        )
            ;}@OverridepublicvoidupdateNull
        (

        StringcolumnLabel
        ) throws SQLException{_res .updateNull ( columnLabel) ; }
        @
            OverridepublicvoidupdateBoolean(String columnLabel,boolean
        x

        )throws
        SQLException { _res.updateBoolean (columnLabel , x) ; }
        @
            OverridepublicvoidupdateByte(String columnLabel,byte
        x

        )throws
        SQLException { _res.updateByte (columnLabel , x) ; }
        @
            OverridepublicvoidupdateShort(String columnLabel,short
        x

        )throws
        SQLException { _res.updateShort (columnLabel , x) ; }
        @
            OverridepublicvoidupdateInt(String columnLabel,int
        x

        )throws
        SQLException { _res.updateInt (columnLabel , x) ; }
        @
            OverridepublicvoidupdateLong(String columnLabel,long
        x

        )throws
        SQLException { _res.updateLong (columnLabel , x) ; }
        @
            OverridepublicvoidupdateFloat(String columnLabel,float
        x

        )throws
        SQLException { _res.updateFloat (columnLabel , x) ; }
        @
            OverridepublicvoidupdateDouble(String columnLabel,double
        x

        )throws
        SQLException { _res.updateDouble (columnLabel , x) ; }
        @
            OverridepublicvoidupdateBigDecimal(String columnLabel,BigDecimal
        x

        )throws
        SQLException { _res.updateBigDecimal (columnLabel , x) ; }
        @
            OverridepublicvoidupdateString(String columnLabel,String
        x

        )throws
        SQLException { _res.updateString (columnLabel ,x) ;} @ Override
        public
            voidupdateBytes(StringcolumnLabel, byte[]
        x

        )throws
        SQLException { _res.updateBytes (columnLabel , x) ; }
        @
            OverridepublicvoidupdateDate(String columnLabel,Date
        x

        )throws
        SQLException { _res.updateDate (columnLabel , x) ; }
        @
            OverridepublicvoidupdateTime(String columnLabel,Time
        x

        )throws
        SQLException { _res.updateTime (columnLabel , x) ; }
        @
            OverridepublicvoidupdateTimestamp(String columnLabel,Timestamp
        x

        )throws
        SQLException { _res.updateTimestamp (columnLabel , x) ; }@ Override public
        void
            updateAsciiStream(StringcolumnLabel,InputStream x, intlength)
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
        _res . updateObject(columnLabel ,x , scaleOrLength) ; }
        @
            OverridepublicvoidupdateObject(String columnLabel,Object
        x

        )throws
        SQLException { _res.updateObject ( columnLabel
        ,
            x);}@Override
        public

        voidinsertRow
        ( ) throwsSQLException{ _res .
        insertRow
            ();}@Override
        public

        voidupdateRow
        ( ) throwsSQLException{ _res .
        updateRow
            ();}@Override
        public

        voiddeleteRow
        ( ) throwsSQLException{ _res .
        deleteRow
            ();}@Override
        public

        voidrefreshRow
        ( ) throwsSQLException{ _res .
        refreshRow
            ();}@Override
        public

        voidcancelRowUpdates
        ( ) throwsSQLException{ _res .
        cancelRowUpdates
            ();}@Override
        public

        voidmoveToInsertRow
        ( ) throwsSQLException{ _res .
        moveToInsertRow
            ();}@Override
        public

        voidmoveToCurrentRow
        ( ) throwsSQLException{ _res .
        moveToCurrentRow
            ( );}@Overridepublic
        Statement

        getStatement(
        ) throws SQLException{return _res. getStatement(); }@OverridepublicObject getObject( int columnIndex
        ,
            Map <String,Class<? >>map
        )

        throwsSQLException
        { return _res.getObject (columnIndex , map
        )
            ; }@OverridepublicRefgetRef(
        int

        columnIndex)
        throws SQLException {return_res .getRef ( columnIndex
        )
            ; }@OverridepublicBlobgetBlob(
        int

        columnIndex)
        throws SQLException {return_res .getBlob ( columnIndex
        )
            ; }@OverridepublicClobgetClob(
        int

        columnIndex)
        throws SQLException {return_res .getClob ( columnIndex
        )
            ; }@OverridepublicArraygetArray(
        int

        columnIndex)
        throws SQLException {return_res .getArray (columnIndex); }@OverridepublicObject getObject( String columnLabel
        ,
            Map <String,Class<? >>map
        )

        throwsSQLException
        { return _res.getObject (columnLabel , map
        )
            ; }@OverridepublicRefgetRef(
        String

        columnLabel)
        throws SQLException {return_res .getRef ( columnLabel
        )
            ; }@OverridepublicBlobgetBlob(
        String

        columnLabel)
        throws SQLException {return_res .getBlob ( columnLabel
        )
            ; }@OverridepublicClobgetClob(
        String

        columnLabel)
        throws SQLException {return_res .getClob ( columnLabel
        )
            ; }@OverridepublicArraygetArray(
        String

        columnLabel)
        throws SQLException {return_res .getArray ( columnLabel) ; }
        @
            Override publicDategetDate(intcolumnIndex ,Calendarcal
        )

        throwsSQLException
        { return _res.getDate (columnIndex , cal) ; }
        @
            Override publicDategetDate(StringcolumnLabel ,Calendarcal
        )

        throwsSQLException
        { return _res.getDate (columnLabel , cal) ; }
        @
            Override publicTimegetTime(intcolumnIndex ,Calendarcal
        )

        throwsSQLException
        { return _res.getTime (columnIndex , cal) ; }
        @
            Override publicTimegetTime(StringcolumnLabel ,Calendarcal
        )

        throwsSQLException
        { return _res.getTime (columnLabel , cal) ; }
        @
            Override publicTimestampgetTimestamp(intcolumnIndex ,Calendarcal
        )

        throwsSQLException
        { return _res.getTimestamp (columnIndex , cal) ; }
        @
            Override publicTimestampgetTimestamp(StringcolumnLabel ,Calendarcal
        )

        throwsSQLException
        { return _res.getTimestamp (columnLabel , cal
        )
            ; }@OverridepublicURLgetURL(
        int

        columnIndex)
        throws SQLException {return_res .getURL ( columnIndex
        )
            ; }@OverridepublicURLgetURL(
        String

        columnLabel)
        throws SQLException {return_res .getURL ( columnLabel) ; }
        @
            OverridepublicvoidupdateRef(int columnIndex,Ref
        x

        )throws
        SQLException { _res.updateRef (columnIndex , x) ; }
        @
            OverridepublicvoidupdateRef(String columnLabel,Ref
        x

        )throws
        SQLException { _res.updateRef (columnLabel , x) ; }
        @
            OverridepublicvoidupdateBlob(int columnIndex,Blob
        x

        )throws
        SQLException { _res.updateBlob (columnIndex , x) ; }
        @
            OverridepublicvoidupdateBlob(String columnLabel,Blob
        x

        )throws
        SQLException { _res.updateBlob (columnLabel , x) ; }
        @
            OverridepublicvoidupdateClob(int columnIndex,Clob
        x

        )throws
        SQLException { _res.updateClob (columnIndex , x) ; }
        @
            OverridepublicvoidupdateClob(String columnLabel,Clob
        x

        )throws
        SQLException { _res.updateClob (columnLabel , x) ; }
        @
            OverridepublicvoidupdateArray(int columnIndex,Array
        x

        )throws
        SQLException { _res.updateArray (columnIndex , x) ; }
        @
            OverridepublicvoidupdateArray(String columnLabel,Array
        x

        )throws
        SQLException { _res.updateArray (columnLabel , x
        )
            ; }@OverridepublicRowIdgetRowId(
        int

        columnIndex)
        throws SQLException {return_res .getRowId ( columnIndex
        )
            ; }@OverridepublicRowIdgetRowId(
        String

        columnLabel)
        throws SQLException {return_res .getRowId ( columnLabel) ; }
        @
            OverridepublicvoidupdateRowId(int columnIndex,RowId
        x

        )throws
        SQLException { _res.updateRowId (columnIndex , x) ; }
        @
            OverridepublicvoidupdateRowId(String columnLabel,RowId
        x

        )throws
        SQLException { _res.updateRowId ( columnLabel
        ,
            x );}@Overridepublic
        int

        getHoldability(
        ) throws SQLException{return _res .
        getHoldability
            ( );}@Overridepublic
        boolean

        isClosed(
        ) throws SQLException{return _res. isClosed () ; }
        @
            OverridepublicvoidupdateNString(int columnIndex,String
        nString

        )throws
        SQLException { _res.updateNString (columnIndex , nString) ; }
        @
            OverridepublicvoidupdateNString(String columnLabel,String
        nString

        )throws
        SQLException { _res.updateNString (columnLabel , nString) ; }
        @
            OverridepublicvoidupdateNClob(int columnIndex,NClob
        nClob

        )throws
        SQLException { _res.updateNClob (columnIndex , nClob) ; }
        @
            OverridepublicvoidupdateNClob(String columnLabel,NClob
        nClob

        )throws
        SQLException { _res.updateNClob (columnLabel , nClob
        )
            ; }@OverridepublicNClobgetNClob(
        int

        columnIndex)
        throws SQLException {return_res .getNClob ( columnIndex
        )
            ; }@OverridepublicNClobgetNClob(
        String

        columnLabel)
        throws SQLException {return_res .getNClob ( columnLabel
        )
            ; }@OverridepublicSQLXMLgetSQLXML(
        int

        columnIndex)
        throws SQLException {return_res .getSQLXML ( columnIndex
        )
            ; }@OverridepublicSQLXMLgetSQLXML(
        String

        columnLabel)
        throws SQLException {return_res .getSQLXML ( columnLabel) ; }
        @
            OverridepublicvoidupdateSQLXML(int columnIndex,SQLXML
        xmlObject

        )throws
        SQLException { _res.updateSQLXML (columnIndex , xmlObject) ; }
        @
            OverridepublicvoidupdateSQLXML(String columnLabel,SQLXML
        xmlObject

        )throws
        SQLException { _res.updateSQLXML (columnLabel , xmlObject
        )
            ; }@OverridepublicStringgetNString(
        int

        columnIndex)
        throws SQLException {return_res .getNString ( columnIndex
        )
            ; }@OverridepublicStringgetNString(
        String

        columnLabel)
        throws SQLException {return_res .getNString ( columnLabel
        )
            ; }@OverridepublicReadergetNCharacterStream(
        int

        columnIndex)
        throws SQLException {return_res .getNCharacterStream ( columnIndex
        )
            ; }@OverridepublicReadergetNCharacterStream(
        String

        columnLabel)
        throws SQLException {return_res .getNCharacterStream ( columnLabel) ; }@ Override public
        void
            updateNCharacterStream(intcolumnIndex,Reader x, longlength)
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
        _res . updateNClob(columnLabel ,reader , length) ; }
        @
            OverridepublicvoidupdateNCharacterStream(int columnIndex,Reader
        x

        )throws
        SQLException { _res.updateNCharacterStream (columnIndex , x) ; }
        @
            OverridepublicvoidupdateNCharacterStream(String columnLabel,Reader
        reader

        )throws
        SQLException { _res.updateNCharacterStream (columnLabel , reader) ; }
        @
            OverridepublicvoidupdateAsciiStream(int columnIndex,InputStream
        x

        )throws
        SQLException { _res.updateAsciiStream (columnIndex , x) ; }
        @
            OverridepublicvoidupdateBinaryStream(int columnIndex,InputStream
        x

        )throws
        SQLException { _res.updateBinaryStream (columnIndex , x) ; }
        @
            OverridepublicvoidupdateCharacterStream(int columnIndex,Reader
        x

        )throws
        SQLException { _res.updateCharacterStream (columnIndex , x) ; }
        @
            OverridepublicvoidupdateAsciiStream(String columnLabel,InputStream
        x

        )throws
        SQLException { _res.updateAsciiStream (columnLabel , x) ; }
        @
            OverridepublicvoidupdateBinaryStream(String columnLabel,InputStream
        x

        )throws
        SQLException { _res.updateBinaryStream (columnLabel , x) ; }
        @
            OverridepublicvoidupdateCharacterStream(String columnLabel,Reader
        reader

        )throws
        SQLException { _res.updateCharacterStream (columnLabel , reader) ; }
        @
            OverridepublicvoidupdateBlob(int columnIndex,InputStream
        inputStream

        )throws
        SQLException { _res.updateBlob (columnIndex , inputStream) ; }
        @
            OverridepublicvoidupdateBlob(String columnLabel,InputStream
        inputStream

        )throws
        SQLException { _res.updateBlob (columnLabel , inputStream) ; }
        @
            OverridepublicvoidupdateClob(int columnIndex,Reader
        reader

        )throws
        SQLException { _res.updateClob (columnIndex , reader) ; }
        @
            OverridepublicvoidupdateClob(String columnLabel,Reader
        reader

        )throws
        SQLException { _res.updateClob (columnLabel , reader) ; }
        @
            OverridepublicvoidupdateNClob(int columnIndex,Reader
        reader

        )throws
        SQLException { _res.updateNClob (columnIndex , reader) ; }
        @
            OverridepublicvoidupdateNClob(String columnLabel,Reader
        reader

        )throws
        SQLException {_res. updateNClob (columnLabel, reader) ;}@Override public< T >
        T
            getObject (intcolumnIndex,Class< T>type
        )

        throwsSQLException
        { return_res. getObject (columnIndex, type) ;}@Override public< T >
        T
            getObject (StringcolumnLabel,Class< T>type
        )

        throwsSQLException
        { return _res.getObject (columnLabel , type) ; }@ Override publicvoid updateObject (
        int
            columnIndex,Objectx,SQLType targetSqlType, intscaleOrLength )throwsSQLException
        {

        _res.
        updateObject ( columnIndex,x ,targetSqlType , scaleOrLength) ; }@ Override publicvoid updateObject (
        String
            columnLabel,Objectx,SQLType targetSqlType, intscaleOrLength )throwsSQLException
        {

        _res.
        updateObject ( columnLabel,x ,targetSqlType , scaleOrLength) ; }@ Override public
        void
            updateObject(intcolumnIndex,Object x, SQLTypetargetSqlType)
        throws

        SQLException{
        _res . updateObject(columnIndex ,x , targetSqlType) ; }@ Override public
        void
            updateObject(StringcolumnLabel,Object x, SQLTypetargetSqlType)
        throws

        SQLException{
        _res .updateObject( columnLabel ,x,targetSqlType); }@ Override public
        <
            T >Tunwrap(Class<T
        >

        iface)
        throws SQLException {return_res.unwrap( iface) ; }
        @
            Override publicbooleanisWrapperFor(Class<?
        >

    iface
    