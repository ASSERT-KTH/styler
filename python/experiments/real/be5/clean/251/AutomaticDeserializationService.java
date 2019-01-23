package com.developmentontheedge.be5.metadata.model;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class AutomaticDeserializationService
{

    private final Map<Path, ManagedFileType> managedFiles = new HashMap<>();

    public AutomaticDeserializationService()
    {
    }

    public void registerFile(final Path file, final ManagedFileType type)
    {
        managedFiles.put(file, type);
    }

    public void unregisterFile(final Path file)
    {
        managedFiles.remove(file);
    }

    public boolean isRegistered(final Path file)
    {
        return managedFiles.containsKey(file);
    }

    /**
     * Returns a type of the given file if the file is registered,
     * or null otherwise.
     */
    public ManagedFileType getTypeOrNull(final Path file)
    {
        return managedFiles.get(file);
    }

}
