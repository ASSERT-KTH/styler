package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.beans.annot.PropertyName;

public abstract class SourceFileOperationExtender extends OperationExtender
{
    protected String namespace;
    private String fileName = getOperation().getEntity().getName() + " - " + getOperation().getName() + " - " + getName() + getFileExtension();
    private SourceFile file;

    public abstract String getFileExtension();

    public SourceFileOperationExtender(Operation owner, String module)
    {
        super(owner, module);
    }

//    /**
//     * Copy constructor
//     * @param owner
//     * @param orig
//     */
//    public SourceFileOperationExtender(Operation owner, SourceFileOperationExtender orig )
//    {
//        super( owner, orig );
//        namespace = orig.namespace;
//        setFileName( orig.getFileName() );
//        setCode( orig.getCode() );
//    }

//    /**
//     * Copy constructor
//     * @param owner
//     * @param orig
//     */
//    public SourceFileOperationExtender(Operation owner, OperationExtender orig )
//    {
//        super( owner, orig );
//    }

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

    public String getCode()
    {
        SourceFile file = getSourceFile();
        return file == null ? "" : file.getSource();
    }

    public SourceFile getSourceFile()
    {
        Project project = getProject();
        Module module = project.getModule(getOriginModuleName());
        SourceFile sourceFile = module == null ? null : module.getSourceFile(namespace, fileName);
        if (sourceFile != null)
            return sourceFile;
        if (file == null && module != project.getApplication())
            file = new SourceFile("(module code)", null);
        return file;
    }

    public void setCode(String code)
    {
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
            getProject().getApplication().addSourceFile(namespace, fileName, code);
        }
        else
        {
            file.setSource(code);
        }

        fireChanged();
    }

    public String getNamespace()
    {
        return namespace;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (!super.equals(obj) || getClass() != obj.getClass())
            return false;
        SourceFileOperationExtender other = (SourceFileOperationExtender) obj;
        return getCode().equals(other.getCode());
    }

//
//    @Override
//    public OperationExtender copyFor( Operation operation )
//    {
//        return new SourceFileOperationExtender( operation, this );
//    }
}
