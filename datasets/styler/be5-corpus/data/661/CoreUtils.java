package com.developmentontheedge.be5.base.services;

import java.util.Map;

public interface CoreUtils
{
    String getSystemSettingInSection(String section, String param);

    String getSystemSettingInSection(String section, String param, String defValue);

    void setSystemSettingInSection(String section, String param, String value);

    Map<String, String> getSystemSettingsInSection(String section);

    String getSystemSetting(String param);

    String getSystemSetting(String param, String defValue);

    boolean getBooleanSystemSetting(String param, boolean defValue);

    boolean getBooleanSystemSetting(String param);

    String getModuleSetting(String module, String param);

    String getModuleSetting(String module, String param, String defValue);

    boolean getBooleanModuleSetting(String module, String param, boolean defValue);

    boolean getBooleanModuleSetting(String module, String param);

    String getUserSetting(String user, String param);

    void setUserSetting(String user, String param, String value);

    void removeUserSetting(String user, String param);
}
