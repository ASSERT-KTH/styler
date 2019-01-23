package com.developmentontheedge.be5.metadata.serialization;

import com.developmentontheedge.be5.metadata.exception.ReadException;
import one.util.streamex.StreamEx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LoadContext
{
    private final List<ReadException> warnings = new ArrayList<>();

    public void addWarning(ReadException ex)
    {
        throw new RuntimeException(ex);
    }

    public List<ReadException> getWarnings()
    {
        return Collections.unmodifiableList(warnings);
    }

    /**
     * fail fast used
     */
    public void check()
    {
        if (!warnings.isEmpty())
        {
            throw new IllegalStateException("There are " + warnings.size() + " errors:\n" + StreamEx.of(warnings).joining("\n"));
        }
    }
}
