package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.exception.ReadException;
import com.developmentontheedge.be5.metadata.model.base.BeFileBasedElement;
import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.model.base.BeModelElementSupport;
import com.developmentontheedge.be5.metadata.serialization.ProjectFileSystem;
import com.developmentontheedge.beans.annot.PropertyName;

import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SourceFile extends BeModelElementSupport implements BeFileBasedElement
{
    private boolean loaded = false;
    private String content = "";
    private Path file;

    public SourceFile(String name, BeModelCollection<?> origin)
    {
        super(name, origin);
    }

    public String getSource()
    {
        if (!isLoaded())
            loadSource();

        return content;
    }

    public boolean isLoaded()
    {
        return loaded;
    }

    private void loadSource()
    {
        if (file != null)
            try
            {
                setSource(ProjectFileSystem.read(file));
            }
            catch (ReadException e)
            {
                throw new RuntimeException(e);
            }
    }

    public void setSource(String content)
    {
        this.content = content == null ? "" : content.replace("\r", "");
        updateLastModification();
        loaded = true;
    }

    public static String extractFileNameFromCode(final String code)
    {
        final Pattern pattern = Pattern.compile(" \\$Id: ([A-Za-z\\.]+\\.js)");
        final Matcher matcher = pattern.matcher(code);

        if (matcher.find())
        {
            return makeSafeFileName(matcher.group(1));
        }

        return null;
    }

    public static String makeSafeFileName(final String fileName)
    {
        return fileName.replaceAll("[\\<\\>\"\\:\\/\\*\\?]", "");
    }

    @Override
    public Path getLinkedFile()
    {
        return file;
    }

    @Override
    public void setLinkedFile(Path path)
    {
        this.file = path;
    }

    @PropertyName("Full file path")
    public String getFilePath()
    {
        return file == null ? null : file.toAbsolutePath().toString();
    }
}
