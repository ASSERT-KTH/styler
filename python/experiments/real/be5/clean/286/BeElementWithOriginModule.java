package com.developmentontheedge.be5.metadata.model.base;


public interface BeElementWithOriginModule extends BeModelElement
{
    /**
     * @return name of the module where this element is really defined
     */
    public String getOriginModuleName();

    /**
     * Set the module where this element is really defined
     *
     * @param module
     */
    public void setOriginModuleName(String module);
}
