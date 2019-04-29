package com.developmentontheedge.be5.modules.core.services.impl;

import com.developmentontheedge.be5.base.services.Be5Caches;
import com.developmentontheedge.be5.base.services.CoreUtils;
import com.developmentontheedge.be5.database.DbService;
import com.developmentontheedge.be5.database.util.SqlUtils;
import com.github.benmanes.caffeine.cache.Cache;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class CoreUtilsImpl implements CoreUtils
{
    public static final String MISSING_SETTING_VALUE = "some-absolutely-impossble-setting-value";

    private final Cache<String, String> systemSettingsCache;
    private final Cache<String, String> userSettingsCache;

    private final DbService db;

    @Inject
    public CoreUtilsImpl(DbService db, Be5Caches be5Caches)
    {
        this.db = db;

        systemSettingsCache = be5Caches.createCache("System settings");
        userSettingsCache = be5Caches.createCache("User settings");
    }

    /**
     * This method is working like calling method {@link #getSystemSettingInSection(String, String, String) getSystemSettingInSection}
     * with parameter defValue = null
     *
     * @param section system settings section name
     * @param param   parameter name
     * @return section parameter value
     */
    @Override
    public String getSystemSettingInSection(String section, String param)
    {
        return getSystemSettingInSection(section, param, null);
    }

    /**
     * Retrieving system settings parameter value for specified section and parameter. If there isn't such parameter, or
     * executing query throws any exception, then method will return defValue.
     * <br/>Results of method call are cached.
     *
     * @param section  system settings section name
     * @param param    parameter name
     * @param defValue default value for return, if there isn't such section or parameter
     * @return section parameter value
     */
    // it is deliberately not synchronized!
    // it is better to let 2 processes to do the same thing twice than
    // to block on network call
    @Override
    public String getSystemSettingInSection(String section, String param, String defValue)
    {
        Objects.requireNonNull(section);
        Objects.requireNonNull(param);

        String key = section + "." + param;

        String value = systemSettingsCache.get(key, (k) -> {
            String sql = "SELECT setting_value FROM systemSettings WHERE setting_name = ? AND section_name = ?";
            return db.oneString(sql, param, section);
        });

        if (MISSING_SETTING_VALUE.equals(value))
        {
            return defValue;
        }

        if (value == null)
        {
            systemSettingsCache.put(key, MISSING_SETTING_VALUE);
            return defValue;
        }

        return value;
    }

    /**
     * Set system settings parameter to the specified value, and saves it to the DB.
     * All of the parameters (section, param, value) must be already passed through the method
     *
     * @param section system settings section name
     * @param param   parameter name
     * @param value   parameter value
     */
    @Override
    public void setSystemSettingInSection(String section, String param, String value)
    {
        Objects.requireNonNull(section);
        Objects.requireNonNull(param);

        String queryUpdate = "UPDATE systemSettings SET setting_value = ?" +
                " WHERE section_name= ? AND setting_name = ?";

        if (0 == db.update(queryUpdate, value, section, param))
        {
            String queryInsert = "INSERT INTO systemSettings( section_name, setting_name, setting_value )" +
                    " VALUES ( ?, ?, ?)";
            db.insert(queryInsert, section, param, value);
        }
        String key = section + "." + param;
        systemSettingsCache.put(key, value);
    }

    /**
     * Get all settings for given section.
     *
     * @param section system settings section name
     * @return Map in the form parameter name - parameter value
     */
    @Override
    public Map<String, String> getSystemSettingsInSection(String section)
    {
        Objects.requireNonNull(section);

        String sql = "SELECT setting_name, setting_value FROM systemSettings WHERE section_name = ?";
        Map<String, String> settingsInSection = new HashMap<>();
        db.list(sql, rs -> {
            String param = rs.getString(1);
            String value = SqlUtils.stringFromDbObject(rs.getObject(2));
            String key = section + "." + param;
            if (value == null)
            {
                systemSettingsCache.put(key, MISSING_SETTING_VALUE);
            }
            else
            {
                systemSettingsCache.put(key, value);
            }
            return settingsInSection.put(param, value);
        }, section);
        return settingsInSection;
    }

    /**
     * Takes parameter param from the section "system", using the method with 3 parameters
     * {@link #setSystemSettingInSection(String, String, String) setSystemSettingInSection} method.
     *
     * @param param parameter name
     * @return parameter value
     */
    @Override
    public String getSystemSetting(String param)
    {
        return getSystemSettingInSection("system", param, null);
    }

    /**
     * Takes parameter param from the section "system", using the method with 3 parameters
     * {@link #setSystemSettingInSection(String, String, String) setSystemSettingInSection} method.
     *
     * @param param    parameter name
     * @param defValue this value is returned, when such parameter does not exists in DB
     * @return parameter value
     */
    @Override
    public String getSystemSetting(String param, String defValue)
    {
        return getSystemSettingInSection("system", param, defValue);
    }

    @Override
    public boolean getBooleanSystemSetting(String param, boolean defValue)
    {
        String check = getSystemSetting(param, null);
        if (check == null)
        {
            return defValue;
        }
        return Arrays.asList("TRUE", "YES", "1", "ON").contains(check.toUpperCase());
    }

    @Override
    public boolean getBooleanSystemSetting(String param)
    {
        return getBooleanSystemSetting(param, false);
    }

    /**
     * Takes parameter param from the section "module + '_module'", using the method with 3 parameters
     * {@link #getModuleSetting(String, String, String) getModuleSetting}(String, String, String),
     * where defValue is null
     * {@link #setSystemSettingInSection(String, String, String) setSystemSettingInSection} method.
     *
     * @param module module name
     * @param param  parameter name
     * @return module parameter value
     */
    @Override
    public String getModuleSetting(String module, String param)
    {
        return getModuleSetting(module, param, null);
    }

    /**
     * Takes parameter param from the section "module + '_module'", using the method with 3 parameters
     * {@link #setSystemSettingInSection(String, String, String) setSystemSettingInSection} method.
     *
     * @param module   module name
     * @param param    parameter name
     * @param defValue default value for return, if there isn't such section or parameter
     * @return module parameter value
     */
    @Override
    public String getModuleSetting(String module, String param, String defValue)
    {
        return getSystemSettingInSection(module.toUpperCase() + "_module", param, defValue);
    }

//    public <T> T getModuleSettingByType( String module, String param, T defValue, Class<T> clazz )
//    {
//        String val = getModuleSetting(module, param);
//        if( val == null )
//            return defValue;
//        return changeType( val, clazz );
//    }

    @Override
    public boolean getBooleanModuleSetting(String module, String param, boolean defValue)
    {
        String check = getModuleSetting(module, param, null);
        if (check == null)
        {
            return defValue;
        }
        return Arrays.asList("TRUE", "YES", "1", "ON").contains(check.toUpperCase());
    }

    @Override
    public boolean getBooleanModuleSetting(String module, String param)
    {
        return getBooleanModuleSetting(module, param, false);
    }

    /**
     * Retrieves specific user parameter from table user_prefs.
     *
     * @param user  user name
     * @param param parameter name
     * @return parameter value
     */
    @Override
    public String getUserSetting(String user, String param)
    {
        Objects.requireNonNull(user);
        Objects.requireNonNull(param);

        String key = user + "." + param;
        String value = userSettingsCache.get(key, k -> {
            String sql = "SELECT pref_value FROM user_prefs WHERE pref_name = ? AND user_name = ?";
            return db.oneString(sql, param, user);
        });

        if (MISSING_SETTING_VALUE.equals(value))
        {
            return null;
        }

        if (value == null)
        {
            userSettingsCache.put(key, MISSING_SETTING_VALUE);
            return null;
        }

        return value;
    }

    /**
     * Set`s up specified user parameter.
     * Some of the parameters are passed to {@link #removeUserSetting(String, String)}
     *
     * @param user  user name
     * @param param parameter name
     * @param value parameter value
     */
    @Override
    public void setUserSetting(String user, String param, String value)
    {
        Objects.requireNonNull(user);
        Objects.requireNonNull(param);

        final String sql = "UPDATE user_prefs SET pref_value = ? WHERE pref_name = ? AND user_name = ?";
        final String sql2 = "INSERT INTO user_prefs VALUES( ?, ?, ? )";

        if (db.update(sql, value, param, user) == 0)
        {
            db.insert(sql2, user, param, value);
        }
        userSettingsCache.put(user + "." + param, value);
    }

    /**
     * Removes specified user settings parameter.
     *
     * @param user  user name
     * @param param parameter name
     */
    @Override
    public void removeUserSetting(String user, String param)
    {
        Objects.requireNonNull(user);
        Objects.requireNonNull(param);

        db.update("DELETE FROM user_prefs WHERE pref_name = ? AND user_name = ?", param, user);
        userSettingsCache.invalidate(user + "." + param);
    }
}
