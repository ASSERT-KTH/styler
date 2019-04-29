package com.developmentontheedge.be5.query.impl;

import com.developmentontheedge.beans.DynamicProperty;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DynamicPropertyMeta
{

    private static final String META_INFO_PROPERTY = "metaInfo";

    @SuppressWarnings("unchecked")
    public static Map<String, Map<String, String>> get(DynamicProperty property)
    {
        Object info = property.getAttribute(META_INFO_PROPERTY);
        if (info != null)
        {
            return (Map<String, Map<String, String>>) property.getAttribute(META_INFO_PROPERTY);
        }
        else
        {
            return Collections.emptyMap();
        }
    }

    public static void set(DynamicProperty property, Map<String, Map<String, String>> meta)
    {
        property.setAttribute(META_INFO_PROPERTY, new HashMap<>(meta));
    }

    @SuppressWarnings("unchecked")
    public static void add(DynamicProperty property, Map<String, Map<String, String>> meta)
    {
        Object oldTags = property.getAttribute(META_INFO_PROPERTY);
        if (oldTags instanceof Map)
        {
            ((Map<String, Map<String, String>>) oldTags).putAll(meta);
        }
        else
        {
            property.setAttribute(META_INFO_PROPERTY, new HashMap<>(meta));
        }
    }

}
