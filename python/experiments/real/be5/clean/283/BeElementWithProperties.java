package com.developmentontheedge.be5.metadata.model.base;

import java.util.Collection;

public interface BeElementWithProperties
{
    public Collection<String> getPropertyNames();

    public String getProperty(String name);

    public void setProperty(String name, String value);

}
