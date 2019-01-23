package com.developmentontheedge.be5.modules.core.operations.users;

import com.developmentontheedge.be5.modules.core.api.CoreFrontendActions;
import com.developmentontheedge.be5.modules.core.services.LoginService;
import com.developmentontheedge.be5.operation.model.OperationResult;
import com.developmentontheedge.be5.server.helpers.UserHelper;
import com.developmentontheedge.be5.server.operations.support.GOperationSupport;

import javax.inject.Inject;


public class Logout extends GOperationSupport
{
    @Inject
    private UserHelper userHelper;
    @Inject
    private LoginService loginService;

    @Override
    public void invoke(Object parameters) throws Exception
    {
        userHelper.logout();
        setResult(OperationResult.finished(null,
                CoreFrontendActions.updateUserAndOpenDefaultRoute(loginService.getUserInfoModel())));
    }
}
