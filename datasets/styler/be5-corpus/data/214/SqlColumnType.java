package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.util.Strings2;
import com.developmentontheedge.beans.annot.PropertyName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.developmentontheedge.be5.metadata.model.SqlBoolColumnType.NO;
import static com.developmentontheedge.be5.metadata.model.SqlBoolColumnType.YES;

@PropertyName("Column type")
public class SqlColumnType
{
    public static final String TYPE_KEY = "KEYTYPE";
    public static final String TYPE_CHAR = "CHAR"; // +length
    public static final String TYPE_VARCHAR = "VARCHAR"; // +length
    public static final String TYPE_DECIMAL = "DECIMAL"; // +length,precision
    public static final String TYPE_CURRENCY = "CURRENCY";
    public static final String TYPE_DATE = "DATE";
    public static final String TYPE_DATETIME = "DATETIME";
    public static final String TYPE_TIMESTAMP = "TIMESTAMP";
    public static final String TYPE_INT = "INT";
    public static final String TYPE_UINT = "INT UNSIGNED";
    public static final String TYPE_BIGINT = "BIGINT";
    public static final String TYPE_UBIGINT = "BIGINT UNSIGNED";
    public static final String TYPE_SMALLINT = "SMALLINT";
    public static final String TYPE_ENUM = "ENUM";
    public static final String TYPE_BOOL = "BOOL";
    public static final String TYPE_TEXT = "TEXT";
    public static final String TYPE_BIGTEXT = "BIGTEXT";
    public static final String TYPE_MEDIUMBLOB = "MEDIUMBLOB";
    public static final String TYPE_BLOB = "BLOB";
    public static final String TYPE_JSONB = "JSONB";
    public static final String TYPE_UNKNOWN = "UNKNOWN";

    public static final String[] TYPES = new String[]{
            TYPE_KEY,
            TYPE_CHAR,
            TYPE_VARCHAR,
            TYPE_DECIMAL,
            TYPE_CURRENCY,
            TYPE_DATE,
            TYPE_DATETIME,
            TYPE_TIMESTAMP,
            TYPE_INT,
            TYPE_UINT,
            TYPE_BIGINT,
            TYPE_UBIGINT,
            TYPE_SMALLINT,
            TYPE_ENUM,
            TYPE_BOOL,
            TYPE_TEXT,
            TYPE_BIGTEXT,
            TYPE_MEDIUMBLOB,
            TYPE_BLOB,
            TYPE_JSONB,
            TYPE_UNKNOWN
    };

    private String typeName = TYPE_VARCHAR;
    private int size = 255;
    private int precision;
    private String[] enumValues;

    public static SqlColumnType unknown()
    {
        return new SqlColumnType(TYPE_UNKNOWN);
    }

    public SqlColumnType()
    {
    }

    public SqlColumnType(String type)
    {
        int pos1 = type.indexOf('(');
        int pos2 = type.lastIndexOf(')');
        if (pos1 > 0 && pos2 > 0)
        {
            typeName = type.substring(0, pos1);
            String[] fields = type.substring(pos1 + 1, pos2).split(",", -1);
            if (TYPE_ENUM.equals(typeName))
            {
                List<String> values = new ArrayList<>(fields.length);
                for (String field : fields)
                {
                    field = field.trim();
                    if (field.startsWith("'"))
                        field = field.substring(1);
                    if (field.endsWith("'"))
                        field = field.substring(0, field.length() - 1);
                    field = field.trim();
                    values.add(field);
                }
                Collections.sort(values);
                enumValues = values.toArray(new String[values.size()]);
            }
            else
            {
                if (fields.length > 0)
                {
                    try
                    {
                        size = Integer.parseInt(fields[0].trim());
                    }
                    catch (NumberFormatException e)
                    {
                    }
                }
                if (fields.length > 1)
                {
                    try
                    {
                        precision = Integer.parseInt(fields[1].trim());
                    }
                    catch (NumberFormatException e)
                    {
                    }
                }
            }
        }
        else
        {
            typeName = type;
        }
        typeName = typeName.toUpperCase();
    }

    public SqlColumnType(SqlColumnType orig)
    {
        this.typeName = orig.typeName;
        this.size = orig.size;
        this.precision = orig.precision;
        this.enumValues = orig.enumValues;
    }

    @PropertyName("Type name")
    public String getTypeName()
    {
        return typeName;
    }

    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
    }

    @PropertyName("Size")
    public int getSize()
    {
        return getTypeName().equals(TYPE_CURRENCY) ? 18 : size;
    }

    public void setSize(int size)
    {
        this.size = size;
    }

    public boolean isSizeHidden()
    {
        return !doesSupportSize();
    }

    public boolean doesSupportSize()
    {
        switch (typeName)
        {
            case TYPE_CHAR:
            case TYPE_VARCHAR:
            case TYPE_DECIMAL:
                return true;
            default:
                return false;
        }
    }

    @PropertyName("Precision")
    public int getPrecision()
    {
        return getTypeName().equals(TYPE_CURRENCY) ? 2 : precision;
    }

    public void setPrecision(int precision)
    {
        this.precision = precision;
    }

    public boolean isPrecisionHidden()
    {
        return !doesSupportPrecision();
    }

    public boolean doesSupportPrecision()
    {
        return typeName.equals(TYPE_DECIMAL);
    }

    @PropertyName("Enum values")
    public String[] getEnumValues()
    {
        return typeName.equals(TYPE_BOOL) ? new String[]{NO, YES}
                : (enumValues == null || !typeName.equals(TYPE_ENUM)) ? Strings2.EMPTY : enumValues;
    }

    public void setEnumValues(String[] enumValues)
    {
        this.enumValues = enumValues;
    }

    public boolean isEnumValuesHidden()
    {
        return !doesSupportEnumValues();
    }

    private boolean doesSupportEnumValues()
    {
        return typeName.equals(TYPE_ENUM);
    }

    public boolean isValid()
    {
        for (String type : TYPES)
        {
            if (type.equals(typeName))
                return true;
        }
        return false;
    }

    @Override
    public String toString()
    {
        switch (typeName)
        {
            case TYPE_CHAR:
            case TYPE_VARCHAR:
                return typeName + "(" + size + ")";
            case TYPE_DECIMAL:
                return typeName + "(" + size + "," + precision + ")";
            case TYPE_ENUM:
                StringBuilder sb = new StringBuilder(typeName);
                sb.append('(');
                if (enumValues != null)
                {
                    for (int i = 0; i < enumValues.length; i++)
                    {
                        if (i > 0)
                        {
                            sb.append(',');
                        }
                        sb.append('\'').append(enumValues[i].replace("'", "''")).append('\'');
                    }
                }
                sb.append(')');
                return sb.toString();
            default:
                return typeName;
        }
    }

    boolean doesSupportGeneratedKey()
    {
        switch (typeName)
        {
            case TYPE_KEY:
            case TYPE_DECIMAL:
            case TYPE_SMALLINT:
            case TYPE_INT:
            case TYPE_BIGINT:
            case TYPE_UBIGINT:
            case TYPE_UINT:
                return true;
            default:
                return false;
        }
    }

    public boolean isDateTime()
    {
        switch (typeName)
        {
            case TYPE_DATE:
            case TYPE_DATETIME:
            case TYPE_TIMESTAMP:
                return true;
            default:
                return false;
        }
    }

    public boolean isIntegral()
    {
        switch (typeName)
        {
            case TYPE_INT:
            case TYPE_UINT:
            case TYPE_BIGINT:
            case TYPE_UBIGINT:
            case TYPE_SMALLINT:
                return true;
            default:
                return false;
        }
    }

}
