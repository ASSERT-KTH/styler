package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.exception.ProjectElementException;
import com.developmentontheedge.be5.metadata.model.base.DataElementPath;
import com.developmentontheedge.be5.metadata.model.base.TemplateElement;
import com.developmentontheedge.beans.annot.PropertyName;

import java.util.ArrayList;
import java.util.List;

public class FreemarkerScript extends SourceFile implements TemplateElement, FreemarkerScriptOrCatalog
{
    public FreemarkerScript(String name, FreemarkerCatalog origin)
    {
        super(name, origin);
    }

    @PropertyName("Result")
    public ParseResult getResult()
    {
        return getProject().mergeTemplate(this);
    }

    @Override
    public List<ProjectElementException> getErrors()
    {
        List<ProjectElementException> result = new ArrayList<>();
        ProjectElementException error = getResult().getError();
        if (error != null && !error.isNoError())
        {
            DataElementPath path = getCompletePath();
            if (error.getPath().equals(path.toString()))
                result.add(error);
            else
                result.add(new ProjectElementException(path, "source", error));
        }
        return result;
    }

    @Override
    public String getTemplateCode()
    {
        return getSource();
    }

    public String getRelativePath(FreemarkerCatalog scripts)
    {
        DataElementPath basePath = scripts.getCompletePath();
        return getCompletePath().getPathDifference(basePath);
    }

}
