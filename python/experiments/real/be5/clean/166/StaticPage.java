package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.model.base.BeModelElement;
import com.developmentontheedge.be5.metadata.model.base.BeVectorCollection;
import com.developmentontheedge.beans.annot.PropertyDescription;
import com.developmentontheedge.beans.annot.PropertyName;

import java.util.Objects;

public class StaticPage extends BeVectorCollection<BeModelElement>
{
    private String content;
    private long id;    // now used internally for synchronization
    private String fileName = "";

    public StaticPage(String name, BeModelCollection<?> parent)
    {
        super(name, BeModelElement.class, parent);
        propagateCodeChange();
    }

    @PropertyName("Content")
    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
        fireCodeChanged();
    }

    @PropertyName("File name")
    @PropertyDescription("Leave this field empty if the page should be saved to the main static pages file.")
    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
        fireCodeChanged();
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(content);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        StaticPage other = (StaticPage) obj;
        return Objects.equals(content, other.content);
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
        fireCodeChanged();
    }
}
