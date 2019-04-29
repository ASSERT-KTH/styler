package com.developmentontheedge.be5.metadata.model.base;

import java.nio.file.Path;

public interface BeFileBasedElement extends BeModelElement
{
    public Path getLinkedFile();

    public void setLinkedFile(Path path);
}
