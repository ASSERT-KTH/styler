package com.developmentontheedge.be5.testbase;

import com.developmentontheedge.be5.base.model.UserInfo;
import com.developmentontheedge.be5.base.services.UserInfoProvider;
import com.developmentontheedge.be5.metadata.RoleType;

import java.util.List;
import java.util.Locale;

public class StaticUserInfoProvider implements UserInfoProvider
{
    public static UserInfo userInfo;

    @Override
    public UserInfo get()
    {
        return userInfo;
    }

    public static void setUserInfo(UserInfo newUserInfo)
    {
        userInfo = newUserInfo;
    }

    @Override
    public String getLanguage()
    {
        return getLocale().getLanguage().toLowerCase();
    }

    @Override
    public Locale getLocale()
    {
        return get().getLocale();
    }

    @Override
    public String getUserName()
    {
        return get().getUserName();
    }

    @Override
    public boolean isLoggedIn()
    {
        return !RoleType.ROLE_GUEST.equals(get().getUserName());
    }

    @Override
    public List<String> getAvailableRoles()
    {
        return get().getAvailableRoles();
    }

    @Override
    public List<String> getCurrentRoles()
    {
        return get().getCurrentRoles();
    }

    @Override
    public String getRemoteAddr()
    {
        return get().getRemoteAddr();
    }

    @Override
    public boolean isSystemDeveloper()
    {
        return getCurrentRoles().contains(RoleType.ROLE_SYSTEM_DEVELOPER);
    }
}
