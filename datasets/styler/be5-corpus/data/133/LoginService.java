package com.developmentontheedge.be5.modules.core.services;

import com.developmentontheedge.be5.modules.core.model.UserInfoModel;
import com.developmentontheedge.be5.web.Request;

import java.util.List;


public interface LoginService
{
    UserInfoModel getUserInfoModel();

    boolean loginCheck(String username, String password);

    void saveUser(String username, Request req);

    void setCurrentRoles(List<String> roles);

    List<String> getAvailableCurrentRoles(List<String> roles, List<String> availableRoles);
}
