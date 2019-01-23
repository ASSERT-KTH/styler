package com.developmentontheedge.be5.modules.core.operations.system;

import com.developmentontheedge.be5.base.services.ProjectProvider;
import com.developmentontheedge.be5.server.operations.support.OperationSupport;

import javax.inject.Inject;


public class ReloadProject extends OperationSupport
{
    @Inject
    private ProjectProvider projectProvider;

    @Override
    public void invoke(Object parameters) throws Exception
    {
        projectProvider.reloadProject();
    }
}
