package com.developmentontheedge.be5.metadata.model.editors;

import com.developmentontheedge.be5.metadata.model.base.BeModelElement;
import com.developmentontheedge.be5.metadata.util.Strings2;
import com.developmentontheedge.beans.editors.StringTagEditor;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

public class SourceFileSelector extends StringTagEditor
{
    private static final String NAMESPACE_PROPERTY = "namespace";

    @Override
    public String[] getTags()
    {
        try
        {
            return ((BeModelElement) getBean()).getProject().getApplication().getSourceFiles()
                    .get(getDescriptor().getValue(NAMESPACE_PROPERTY).toString()).names().toArray(String[]::new);
        }
        catch (Exception e)
        {
            return Strings2.EMPTY;
        }
    }

    public static PropertyDescriptor register(String name, Class<?> beanClass, String nameSpace) throws IntrospectionException
    {
        PropertyDescriptor pd = new PropertyDescriptor(name, beanClass);
        pd.setPropertyEditorClass(SourceFileSelector.class);
        pd.setValue(NAMESPACE_PROPERTY, nameSpace);
        return pd;
    }
}
