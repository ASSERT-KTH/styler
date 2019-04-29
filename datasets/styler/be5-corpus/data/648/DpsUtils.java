package com.developmentontheedge.be5.base.util;

import com.developmentontheedge.be5.base.exceptions.Be5Exception;
import com.developmentontheedge.be5.database.util.SqlUtils;
import com.developmentontheedge.beans.BeanInfoConstants;
import com.developmentontheedge.beans.DynamicProperty;
import com.developmentontheedge.beans.DynamicPropertySet;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DpsUtils
{
    public static Map<String, Object> toLinkedHashMap(DynamicPropertySet dps)
    {
        Map<String, Object> map = new LinkedHashMap<>(dps.size());
        for (DynamicProperty property : dps)
        {
            map.put(property.getName(), property.getValue());
        }

        return map;
    }

    public static void setValueIfOneTag(DynamicPropertySet dps, List<String> propertyNames)
    {
        for (String name : propertyNames)
        {
            DynamicProperty property = dps.getProperty(name);
            Objects.requireNonNull(property);
            String[][] tags = (String[][]) property.getAttribute(BeanInfoConstants.TAG_LIST_ATTR);
            if (tags.length == 1 && !property.isCanBeNull())
            {
                property.setValue(tags[0][0]);
            }
        }
    }

    public static <T extends DynamicPropertySet> T setValues(T dps, DynamicPropertySet values)
    {
        for (DynamicProperty valueProperty : values)
        {
            DynamicProperty property = dps.getProperty(valueProperty.getName());
            if (property != null)
            {
                property.setValue(valueProperty.getValue());
            }
        }
        return dps;
    }

    public static <T extends DynamicPropertySet> T setValues(T dps, Map<String, ?> values)
    {
        for (Map.Entry<String, ?> entry : values.entrySet())
        {
            DynamicProperty property = dps.getProperty(entry.getKey());
            if (property != null && !property.isReadOnly())
            {
                dps.setValue(entry.getKey(), entry.getValue());
            }
        }
        return dps;
    }

    public static <T extends DynamicPropertySet> T setValues(T dps, ResultSet resultSet)
    {
        try
        {
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++)
            {
                String name = metaData.getColumnName(i);

                DynamicProperty property = dps.getProperty(name);
                if (property != null)
                {
                    property.setValue(SqlUtils.getSqlValue(property.getType(), resultSet, i));
                }
            }
        }
        catch (SQLException e)
        {
            throw Be5Exception.internal(e);
        }

        return dps;
    }
}
