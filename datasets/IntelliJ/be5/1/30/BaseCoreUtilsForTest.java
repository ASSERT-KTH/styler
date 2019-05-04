package com.developmentontheedge.be5.base.test;

import com.developmentontheedge.be5.base.services.CoreUtils;

import java.util.Collections;
import java.util.Map;

import static org.mockito.Mockito.mock;


public class BaseCoreUtilsForTest implements CoreUtils
{
    public static CoreUtils mock = mock(CoreUtils.class);

    public static void clearMock()
    {
        mock = mock(CoreUtils.class);
    }

    @Override
    public String getSystemSettingInSection(String section, String param)
    {
        return getSystemSettingInSection(section, param, null);
    }

    @Override
    public String getSystemSettingInSection(String section, String param, String defValue)
    {
        return mock.getSystemSettingInSection(section, param, defValue);
    }

    @Override
    public void setSystemSettingInSection(String section, String param, String value)
    {

    }

    @Override
    public Map<String, String> getSystemSettingsInSection(String section)
    {
        return null;
    }

    @Override
    public String getSystemSetting(String param)
    {
        return null;
    }

    @Override
    public String getSystemSetting(String param, String defValue)
    {
        return defValue;
    }

    @Override
    public boolean getBooleanSystemSetting(String param, boolean defValue)
    {
        return defValue;
    }

    @Override
    public boolean getBooleanSystemSetting(String param)
    {
        return false;
    }

    @Override
    public String getModuleSetting(String module, String param)
    {
        return null;
    }

    @Override
    public String getModuleSetting(String module, String param, String defValue)
    {
        return defValue;
    }

    @Override
    public boolean getBooleanModuleSetting(String module, String param, boolean defValue)
    {
        return defValue;
    }

    @Override
    public boolean getBooleanModuleSetting(String module, String param)
    {
        return false;
    }

    @Override
    public String getUserSetting(String user, String param)
    {
        return null;
    }

    @Override
    public void setUserSetting(String user, String param, String value)
    {

    }

    @Override
    public void removeUserSetting(String user, String param)
    {

    }

    @Override
    public Map<String, Object> getColumnSettingForUser(String table_name, String query_name, String column_name, String user_name)
    {
        return Collections.emptyMap();
    }

    @Override
    public void setColumnSettingForUser(String table_name, String query_name, String column_name, String user_name, Map<String, Object> values)
    {

    }

    @Override
    public void removeColumnSettingForUser(String table_name, String query_name, String column_name, String user_name)
    {

    }
}
