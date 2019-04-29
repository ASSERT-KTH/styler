package com.developmentontheedge.be5.databasemodel;


import com.developmentontheedge.beans.DynamicPropertySet;

import java.util.Map;

public interface RecordModel<T> extends DynamicPropertySet
{
    T getPrimaryKey();

    int remove();

    void update(String propertyName, Object value);

    void update(Map<String, Object> values);
}
