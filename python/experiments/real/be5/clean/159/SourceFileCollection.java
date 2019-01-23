package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.model.base.BeVectorCollection;


public class SourceFileCollection extends BeVectorCollection<SourceFile>
{
    public static final String NAMESPACE_JAVASCRIPT_OPERATION = "JavaScript operations";
    public static final String NAMESPACE_GROOVY_OPERATION = "Groovy operations";
    public static final String NAMESPACE_JAVASCRIPT_EXTENDER = "JavaScript extenders";
    public static final String NAMESPACE_GROOVY_EXTENDER = "Groovy extenders";

    public SourceFileCollection(String name, BeModelCollection<?> parent)
    {
        super(name, SourceFile.class, parent);
    }
}
