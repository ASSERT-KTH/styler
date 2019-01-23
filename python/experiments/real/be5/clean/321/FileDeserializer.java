package com.developmentontheedge.be5.metadata.serialization.yaml.deserializers;

import com.developmentontheedge.be5.metadata.exception.ReadException;
import com.developmentontheedge.be5.metadata.serialization.LoadContext;
import com.developmentontheedge.be5.metadata.serialization.ProjectFileSystem;
import com.developmentontheedge.be5.metadata.serialization.Serialization;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.MarkedYAMLException;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Node;

import java.io.StringReader;
import java.nio.file.Path;

abstract class FileDeserializer extends BaseDeserializer
{
    protected final Node content;

    private FileDeserializer(LoadContext loadContext, final String content, final Path path) throws ReadException
    {
        super(loadContext, path);
        if (content == null)
        {
            this.content = null;
            return;
        }

        try
        {
            this.content = new Yaml().compose(new StringReader(content));
        }
        catch (MarkedYAMLException e)
        {
            throw new ReadException(
                    new Exception((e.getProblemMark().getLine() + 1) + ":" + (e.getProblemMark().getColumn() + 1) + ": "
                            + e.getMessage()), path, ReadException.LEE_INVALID_STRUCTURE);
        }
        catch (YAMLException e)
        {
            throw new ReadException(new Exception(e.getMessage()), path, ReadException.LEE_INVALID_STRUCTURE);
        }
    }

    public FileDeserializer(LoadContext loadContext, final Path path) throws ReadException
    {
        this(loadContext, ProjectFileSystem.read(path), path);
    }

    public FileDeserializer(LoadContext loadContext, final Path path, boolean nullAble) throws ReadException
    {
        this(loadContext, ProjectFileSystem.read(path, nullAble), path);
    }

    public FileDeserializer(LoadContext loadContext)
    {
        super(loadContext);
        content = null;
    }

    public void deserialize() throws ReadException
    {
        if (content != null)
        {
            doDeserialize(Serialization.derepresent(content));
        }
    }

    protected abstract void doDeserialize(Object serializedRoot) throws ReadException;

    @SuppressWarnings("unused")
    protected Node getNodeByObject(Object object)
    {
        return null; // TODO implement me
    }
}