package com.developmentontheedge.be5.modules.core.controllers;

import com.developmentontheedge.be5.base.services.UserInfoProvider;
import com.developmentontheedge.be5.modules.core.services.LoginService;
import com.developmentontheedge.be5.server.servlet.support.JsonApiController;
import com.developmentontheedge.be5.web.Request;
import com.google.common.base.Splitter;

import javax.inject.Inject;
import java.util.List;


public class UserInfoController extends JsonApiController
{
    private final LoginService loginService;
    private final UserInfoProvider userInfoProvider;

    @Inject
    public UserInfoController(LoginService loginService, UserInfoProvider userInfoProvider)
    {
        this.loginService = loginService;
        this.userInfoProvider = userInfoProvider;
    }

    @Override
    public Object generate(Request req, String requestSubUrl)
    {
        switch (requestSubUrl)
        {
            case "":
                return loginService.getUserInfoModel();
            case "selectRoles":
                return selectRolesAndSendNewState(req);
            default:
                return null;
        }
    }

    private Object selectRolesAndSendNewState(Request req)
    {
        List<String> roles = Splitter.on(',').splitToList(req.getOrEmpty("roles"));

        List<String> availableCurrentRoles = loginService.getAvailableCurrentRoles(roles,
                userInfoProvider.get().getAvailableRoles());

        loginService.setCurrentRoles(availableCurrentRoles);

        return userInfoProvider.get().getCurrentRoles();
    }
}
