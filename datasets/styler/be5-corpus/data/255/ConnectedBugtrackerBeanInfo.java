package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.beans.BeanInfoEx;

public class ConnectedBugtrackerBeanInfo extends BeanInfoEx
{

    public ConnectedBugtrackerBeanInfo()
    {
        super(ConnectedBugtracker.class);
    }

    @Override
    protected void initProperties() throws Exception
    {
        add("name");
        add("projectId");
    }

}
