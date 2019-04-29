package com.developmentontheedge.be5.databasemodel.groovy;

import com.developmentontheedge.be5.databasemodel.DatabaseModel;
import groovy.lang.DelegatingMetaClass;


public class DatabaseModelImplMetaClass extends DelegatingMetaClass
{
    public DatabaseModelImplMetaClass(Class<DatabaseModel> theClass)
    {
        super(theClass);
    }

    @Override
    public Object getProperty(Object object, String property)
    {
        return ((DatabaseModel) object).getEntity(property);
    }
}
