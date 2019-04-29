package com.developmentontheedge.be5.base.services;

import com.developmentontheedge.be5.base.model.UserInfo;

import java.util.List;
import java.util.Locale;


public interface UserInfoProvider
{
    UserInfo get();

    String getLanguage();

    Locale getLocale();

    String getUserName();

    boolean isLoggedIn();

    List<String> getAvailableRoles();

    List<String> getCurrentRoles();

    String getRemoteAddr();

    boolean isSystemDeveloper();
}
