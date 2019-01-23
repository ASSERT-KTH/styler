package com.developmentontheedge.be5.modules.core.services;

import com.developmentontheedge.be5.base.services.Be5Caches;
import com.developmentontheedge.be5.base.services.CoreUtils;
import com.developmentontheedge.be5.database.DbService;
import com.developmentontheedge.be5.databasemodel.DatabaseModel;
import com.developmentontheedge.be5.databasemodel.EntityModel;
import com.developmentontheedge.be5.modules.core.CoreBe5ProjectDBTest;
import com.developmentontheedge.be5.modules.core.services.impl.CoreUtilsImpl;
import com.google.inject.internal.util.ImmutableMap;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class CoreUtilsTest extends CoreBe5ProjectDBTest
{
    @Inject
    private DatabaseModel database;
    @Inject
    private DbService db;
    @Inject
    private CoreUtils utils;
    @Inject
    private Be5Caches be5Caches;

    @Before
    public void before()
    {
        db.update("DELETE FROM systemSettings");
        db.update("DELETE FROM user_prefs");
        db.update("DELETE FROM columnSettings");
        be5Caches.clearAll();
    }

    @Test
    public void getSystemSettingInSection()
    {
        database.getEntity("systemSettings").add(ImmutableMap.of(
                "section_name", "system",
                "setting_name", "app_name",
                "setting_value", "Test App"));
        assertEquals( "Test App", utils.
                getSystemSettingInSection("system", "app_name", "Be5 Application"));
    }

    @Test
    public void getSystemSettingInSectionNotFound()
    {
        assertEquals("Be5 Application", utils.getSystemSettingInSection("system", "app_name", "Be5 Application"));
    }

    @Test
    public void getSystemSettingNotFound()
    {
        assertEquals(null, utils.getSystemSetting("app_name"));
        assertEquals("No value", utils.getSystemSetting("app_name", "No value"));
    }

    @Test
    public void setSystemSettingInSection()
    {
        utils.setSystemSettingInSection("system", "app_name", "Name 1");
        assertEquals("Name 1", utils.getSystemSetting("app_name"));

        utils.setSystemSettingInSection("system", "app_name", "Name 2");
        assertEquals("Name 2", utils.getSystemSetting("app_name"));
    }

    @Test
    public void getSystemSettingsInSectionTest()
    {
        utils.setSystemSettingInSection("system", "app_name", "App");
        utils.setSystemSettingInSection("system", "app_url", "Url");

        assertEquals("{app_name=App, app_url=Url}", utils.getSystemSettingsInSection("system").toString());

        assertEquals("App", utils.getSystemSetting("app_name"));
        assertEquals("Url", utils.getSystemSetting("app_url"));
    }

    @Test
    public void getBooleanSystemSetting()
    {
        assertEquals(false, utils.getBooleanSystemSetting("is_active"));
        assertEquals(CoreUtilsImpl.MISSING_SETTING_VALUE, be5Caches.getCache("System settings").getIfPresent("system.is_active"));
        assertEquals(true, utils.getBooleanSystemSetting("is_active", true));

        database.getEntity("systemSettings").add(ImmutableMap.of(
                "section_name", "system",
                "setting_name", "is_active",
                "setting_value", "true"));
        be5Caches.clearAll();

        assertEquals(true, utils.getBooleanSystemSetting("is_active"));
    }

    @Test
    public void getModuleSetting()
    {
        assertEquals(false, utils.getBooleanModuleSetting("core", "is_active"));
        assertEquals(true, utils.getBooleanModuleSetting("core", "is_active", true));

        assertEquals(null, utils.getModuleSetting("core", "is_active"));
        assertEquals("false", utils.getModuleSetting("core", "is_active", "false"));

        database.getEntity("systemSettings").add(ImmutableMap.of(
                "section_name", "CORE_module",
                "setting_name", "is_active",
                "setting_value", "true"));
        be5Caches.clearAll();

        assertEquals(true, utils.getBooleanModuleSetting("core", "is_active"));
    }

    @Test
    public void getUserSetting()
    {
        assertEquals(null, utils.getUserSetting("testName", "companyID"));
        assertEquals(CoreUtilsImpl.MISSING_SETTING_VALUE, be5Caches.getCache("User settings").getIfPresent("testName.companyID"));

        assertEquals(null, utils.getUserSetting("testName", "companyID"));

        database.getEntity("user_prefs").add(ImmutableMap.of(
                "user_name", "testName",
                "pref_name", "companyID",
                "pref_value", "123"));
        be5Caches.clearAll();

        assertEquals("123", utils.getUserSetting("testName", "companyID"));

        utils.removeUserSetting("testName", "companyID");

        assertEquals(null, utils.getUserSetting("testName", "companyID"));
    }

    @Test
    public void setUserSettingTest()
    {
        utils.setUserSetting("testName", "companyID", "1");
        assertEquals("1", utils.getUserSetting("testName", "companyID"));

        utils.setUserSetting("testName", "companyID", "2");
        assertEquals("2", utils.getUserSetting("testName", "companyID"));
    }

    @Test
    public void getColumnSettingForUserTest()
    {
        assertEquals(Collections.emptyMap(), utils.getColumnSettingForUser("users", "All records", "User", TEST_USER));

        database.getEntity("columnSettings").add(new HashMap<String, Object>() {{
            put("queryID", 0);
            put("table_name", "users");
            put("query_name", "All records");
            put("column_name", "User");
            put("user_name", TEST_USER);
            put("quick", "yes");
        }});
        be5Caches.clearAll();

        assertEquals("yes", utils.getColumnSettingForUser("users", "All records", "User", TEST_USER).get("quick"));

        utils.removeColumnSettingForUser("users", "All records", "User", TEST_USER);

        assertEquals(Collections.emptyMap(), utils.getColumnSettingForUser("users", "All records", "User", TEST_USER));
    }

    @Test
    public void setColumnSettingForUserTest()
    {
        EntityModel<Long> columnSettings = database.getEntity("columnSettings");
        utils.setColumnSettingForUser("users", "All records", "User", TEST_USER,
                Collections.singletonMap("quick", "yes"));
        assertEquals("yes",
                utils.getColumnSettingForUser("users", "All records", "User", TEST_USER).get("quick"));
        assertEquals("yes", columnSettings.getBy(com.google.common.collect.ImmutableMap.of(
                "table_name", "users",
                "query_name", "All records",
                "column_name", "User")).getValueAsString("quick"));

        utils.setColumnSettingForUser("users", "All records", "User", TEST_USER,
                Collections.singletonMap("quick", "no"));
        assertEquals("no",
                utils.getColumnSettingForUser("users", "All records", "User", TEST_USER).get("quick"));
        assertEquals("no", columnSettings.getBy(com.google.common.collect.ImmutableMap.of(
                "table_name", "users",
                "query_name", "All records",
                "column_name", "User")).getValueAsString("quick"));
    }

    @Test(expected = java.lang.NullPointerException.class)
    public void getBooleanModuleSettingNull()
    {
        utils.getBooleanModuleSetting("test", null);
    }

    @Test(expected = java.lang.NullPointerException.class)
    public void getBooleanModuleSettingNull2()
    {
        utils.getBooleanModuleSetting(null, "test");
    }

    @Test(expected = java.lang.NullPointerException.class)
    public void getUserSettingNullParams()
    {
        utils.getUserSetting(null, "test");
    }

    @Test(expected = java.lang.NullPointerException.class)
    public void getUserSettingNullParams2()
    {
        utils.getUserSetting("test", null);
    }
}
