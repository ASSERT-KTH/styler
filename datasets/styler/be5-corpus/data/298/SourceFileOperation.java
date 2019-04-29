package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.beans.annot.PropertyName;

public abstract class SourceFileOperation extends Operation
{
    private String fileName = getDefaultFileName();
    private SourceFile file;

    public SourceFileOperation(String name, String type, Entity entity)
    {
        super(name, type, entity);
    }

    public abstract String getFileNameSpace();

    public abstract String getFileExtension();

    protected String getDefaultFileName()
    {
        return SourceFile.makeSafeFileName(getEntity().getName() + " - " + getName() + getFileExtension());
    }

    @PropertyName("Source file name")
    public String getFileName()
    {
        SourceFile sourceFile = getSourceFile();
        if (sourceFile != null)
            return sourceFile.getName();
        if (getProject().getProjectOrigin().equals(getOriginModuleName()))
            return fileName;
        return "(module code)";
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
        fireChanged();
    }

    @Override
    public String getCode()
    {
        if (customizedProperties != null && customizedProperties.contains("code"))
        {
            SourceFile file = getSourceFile();
            return file == null ? "" : file.getSource();
        }
        if (prototype == null)
            return "";
        return ((Operation) prototype).getCode();
    }

    public SourceFile getSourceFile()
    {
        Project project = getProject();
        Module module = project.getModule(getOriginModuleName());
        SourceFile sourceFile = module == null ? null : module.getSourceFile(getFileNameSpace(), fileName);
        if (sourceFile != null)
            return sourceFile;
        if (file == null && !getOriginModuleName().equals(project.getProjectOrigin()))
            file = new SourceFile("(module code)", null);
        return file;
    }

    @Override
    public void setCode(String code)
    {
        if (getCode().equals(code))
            return;
        SourceFile file = getSourceFile();
        if (file == null)
        {
            String newFileName = SourceFile.extractFileNameFromCode(code);
            if (newFileName != null)
            {
                fileName = newFileName;
            }
            file = getSourceFile();
        }
        if (file == null)
        {
            this.file = getProject().getApplication().addSourceFile(getFileNameSpace(), fileName, code);
        }
        else
        {
            file.setSource(code);
        }
        internalCustomizeProperty("code");
        fireCodeChanged();
    }
}
