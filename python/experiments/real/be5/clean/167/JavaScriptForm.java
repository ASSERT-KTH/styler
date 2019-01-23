package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.exception.ReadException;
import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.serialization.ModuleLoader2;
import com.developmentontheedge.be5.metadata.serialization.ProjectFileSystem;
import com.developmentontheedge.beans.annot.PropertyName;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;

public class JavaScriptForm extends SourceFile
{
    private String module;
    private String relativePath;

    public JavaScriptForm(String name, BeModelCollection<? extends SourceFile> origin)
    {
        super(name, origin);
        this.module = getProject().getProjectOrigin();
        this.relativePath = makeSafeFileName(name) + ".js";
        updateLocation();
    }

    @PropertyName("Module")
    public String getModuleName()
    {
        return module;
    }

    public void setModule(String module)
    {
        this.module = module;
        updateLocation();
        fireChanged();
    }

    @PropertyName("Relative path")
    public String getRelativePath()
    {
        return relativePath;
    }

    public void setRelativePath(String relativePath)
    {
        this.relativePath = relativePath;
        updateLocation();
        fireChanged();
    }

    private void updateLocation()
    {
        try
        {
            setLinkedFile(ModuleLoader2.getFileSystem(getProject(), module).getJavaScriptFormsFolder().resolve(relativePath));
        }
        catch (Exception e)
        {
            setLinkedFile(null);
        }
    }

    public void load() throws ReadException
    {
        updateLocation();
        if (getLinkedFile() == null)
        {
            throw new IllegalStateException("File is not set");
        }
        setSource(ProjectFileSystem.read(getLinkedFile()));
    }

    public void save() throws IOException
    {
        updateLocation();
        if (getLinkedFile() == null)
        {
            throw new IllegalStateException("File is not set");
        }
        Files.createDirectories(getLinkedFile().getParent());
        Files.write(getLinkedFile(), getSource().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(relativePath);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        JavaScriptForm other = (JavaScriptForm) obj;
        return getSource().equals(other.getSource());
    }

    @Override
    protected void fireChanged()
    {
        if (getOrigin().get(getName()) == this)
            getOrigin().fireCodeChanged();
    }
}
