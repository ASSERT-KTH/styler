package com.developmentontheedge.be5.query.model.beans;

import com.developmentontheedge.be5.base.model.groovy.DynamicPropertySetMetaClass;
import com.developmentontheedge.be5.base.services.GroovyRegister;
import com.developmentontheedge.beans.DynamicPropertySetSupport;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.SQLException;


public class QRec extends DynamicPropertySetSupport
{
    static
    {
        GroovyRegister.registerMetaClass(DynamicPropertySetMetaClass.class, QRec.class);
    }

    /**
     * Retrieves first value. Useful when we need only one column
     *
     * @return value of column
     */
    public Object getValue()
    {
        return properties.get(0).getValue();
    }

    /**
     * Retrieves first value as a string.
     *
     * @return value of column
     */
    public String getString()
    {
        return valToString(getValue());
    }

    private static String valToString(Object val)
    {
        try
        {
            if (val == null)
                return null;
            if (val instanceof Blob)
                return new String(((Blob) val).getBytes(1, (int) ((Blob) val).length()));
            if (val instanceof byte[])
                return new String((byte[]) val, "UTF-8");
        }
        catch (UnsupportedEncodingException | SQLException e)
        {
            return null;
        }
        return val.toString();
    }

    /**
     * Retrieves first value as an int.
     *
     * @return integer value of column or <code>null</code> if it's <code>null</code>.
     */
    public Integer getInt()
    {
        return (null == getValue()) ? null : Integer.valueOf(getValue().toString());
    }

    /**
     * Retrieves first value as a long.
     *
     * @return long value of column or <code>null</code> if it's <code>null</code>.
     */
    public Long getLong()
    {
        return (null == getValue()) ? null : Long.valueOf(getValue().toString());
    }

    /**
     * Retrieves value of the specified column name as string.
     *
     * @param name column
     * @return value of column
     */
    public String getString(String name)
    {
        return valToString(getValue(name));
    }

    /**
     * Retrieves value of the specified column name as int.
     *
     * @param name column
     * @return value of column
     */
    public int getInt(String name)
    {
        return Integer.parseInt(getValue(name).toString());
    }

    /**
     * Retrieves value of the specified column name as long.
     *
     * @param name column
     * @return value of column
     */
    public long getLong(String name)
    {
        return Long.parseLong(getValue(name).toString());
    }

    public java.sql.Date getDate(String name)
    {
        java.util.Date date = (java.util.Date) getValue(name);
        if (date == null)
        {
            return null;
        }
        return new java.sql.Date(date.getTime());
    }

    public java.sql.Date getDate()
    {
        java.util.Date date = (java.util.Date) getValue();
        if (date == null)
        {
            return null;
        }
        return new java.sql.Date(date.getTime());
    }

    public InputStream getBinaryStream() throws SQLException
    {
        Object val = getValue();
        if (val == null)
        {
            return null;
        }
        else if (val instanceof byte[])
        {
            return new ByteArrayInputStream((byte[]) val);
        }
        return new BlobInputStream((Blob) val, properties.get(0).getName());
    }

    public InputStream getBinaryStream(String name) throws SQLException
    {
        Object val = getValue(name);

        if (val == null)
        {
            return null;
        }
        else if (val instanceof byte[])
        {
            return new ByteArrayInputStream((byte[]) val);
        }
        return new BlobInputStream((Blob) val, name);
    }

    public static final class BlobInputStream extends InputStream
    {
        Blob blob;
        InputStream is;
        String name;

        boolean isClosed;

        public BlobInputStream(Blob blob, String name) throws SQLException
        {
            this.blob = blob;
            this.name = name;
            is = blob.getBinaryStream();
        }

        @Override
        public int read() throws IOException
        {
            int ret = is.read();
            if (ret == -1)
            {
                //System.out.println( "autoclose: " + name );
                close();
            }
            return ret;
        }

        @Override
        public int read(byte[] b) throws IOException
        {
            int ret = is.read(b);
            if (ret < 1)
            {
                //System.out.println( "autoclose: " + name );
                close();
            }
            return ret;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException
        {
            int ret = is.read(b, off, len);
            if (ret < 1)
            {
                //System.out.println( "autoclose: " + name );
                close();
            }
            return ret;
        }

        @Override
        public long skip(long n) throws IOException
        {
            return is.skip(n);
        }

        @Override
        public int available() throws IOException
        {
            return is.available();
        }

        @Override
        public void close() throws IOException
        {
            if (isClosed)
            {
                return;
            }

            try
            {
                is.close();
                is = null;
                Method pmeth = blob.getClass().getMethod("isTemporary", new Class[0]);
                if (pmeth != null)
                {
                    boolean isTemporary = (Boolean) pmeth.invoke(blob, new Object[0]);
                    if (isTemporary)
                    {
                        pmeth = blob.getClass().getMethod("freeTemporary", new Class[0]);
                        if (pmeth != null)
                        {
                            pmeth.invoke(blob, new Object[0]);
                        }
                    }
                }
            }
            catch (NoSuchMethodException ignore)
            {
            }
            catch (Exception exc)
            {
                throw new IOException(exc.getMessage(), exc);
            }
            finally
            {
                isClosed = true;
                blob = null;
            }
        }

        @Override
        public void mark(int readlimit)
        {
            is.mark(readlimit);
        }

        @Override
        public void reset() throws IOException
        {
            is.reset();
        }

        @Override
        public boolean markSupported()
        {
            return is.markSupported();
        }

        @Override
        protected void finalize() throws Throwable
        {
            close();
        }
    }
}
