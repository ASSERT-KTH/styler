package com.developmentontheedge.be5.operation.util;

import com.developmentontheedge.be5.operation.OperationConstants;

import java.util.HashMap;
import java.util.Map;

public class OperationUtils
{
    public static String[] selectedRows(String selectedRowsString)
    {
        if (selectedRowsString == null || selectedRowsString.trim().isEmpty()) return new String[0];
        return selectedRowsString.split(",");
    }

    public static Map<String, Object> replaceEmptyStringToNull(Map<String, Object> values)
    {
        HashMap<String, Object> map = new HashMap<>();
        for (Map.Entry<String, Object> entry : values.entrySet())
        {
            if ("".equals(entry.getValue()))
            {
                map.put(entry.getKey(), null);
            }
            else
            {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        return map;
    }

    public static Map<String, Object> paramsWithoutSelectedRows(Map<String, Object> redirectParams)
    {
        HashMap<String, Object> map = new HashMap<>(redirectParams);
        map.remove(OperationConstants.SELECTED_ROWS);

        return map;
    }
}
