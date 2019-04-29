package com.developmentontheedge.be5.metadata.util;

import com.developmentontheedge.be5.metadata.exception.ReadException;
import com.developmentontheedge.be5.metadata.model.Module;
import com.developmentontheedge.be5.metadata.model.Project;
import com.developmentontheedge.be5.metadata.serialization.Serialization;

public class ModuleUtils
{
    public static void addModuleScripts(Project project) throws ReadException
    {
        for (Module module : project.getModules())
        {
            Serialization.loadModuleMacros(module);
        }
    }
}
