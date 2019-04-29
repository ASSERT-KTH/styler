package com.developmentontheedge.be5.metadata.serialization;

import com.developmentontheedge.be5.metadata.QueryType;
import com.developmentontheedge.be5.metadata.model.Operation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class Fields
{
    private static final List<Field> PROJECT = Collections.unmodifiableList(Arrays.asList(
            new Field(SerializationConstants.ATTR_PROJECT_NAME),
            new Field(SerializationConstants.ATTR_CONNECTION_PROFILE)
    ));
    private static final List<Field> ENTITY = Collections.unmodifiableList(Arrays.asList(
            new Field(SerializationConstants.ATTR_ENTITY_NAME),
            new Field(SerializationConstants.ATTR_ENTITY_DISPLAY_NAME),
            new Field("order"),
            new Field(SerializationConstants.ATTR_ENTITY_PRIMARY_KEY_COLUMN),
            new Field("besql", false)
    ));
    private static final List<Field> OPERATION = Collections.unmodifiableList(Arrays.asList(
            new Field("name"),
            new Field("notSupported"),
            new Field("records", 0),
            new Field("executionPriority", 999999),
            new Field("logging", Operation.getDefaultLoggingOption()),
            new Field("secure", false),
            new Field("confirm", false),
            new Field("contextID"),
            new Field("categoryID"),
            new Field("wellKnownName", ""),
            new Field("layout", "")
    ));
    private static final List<Field> EXTENDER = Collections.unmodifiableList(Arrays.asList(
            new Field("invokeOrder", 0)
    ));
    private static final List<Field> DAEMON = Collections.unmodifiableList(Arrays.asList(
            new Field("name"),
            new Field("className"),
            new Field("configSection"),
            new Field("daemonType"),
            new Field("description"),
            new Field("slaveNo", 1)
    ));
    private static final List<Field> JS_FORMS = Collections.unmodifiableList(Arrays.asList(
            new Field("name"),
            new Field("module"),
            new Field("relativePath")
    ));
    private static final List<Field> QUERY = Collections.unmodifiableList(Arrays.asList(
            new Field("name"),
            new Field("menuName", ""),
            new Field("titleName"),
            new Field("type", QueryType.D1),
            new Field("notSupported"),
            new Field("newDataCheckQuery"),
            new Field("invisible", false),
            new Field("secure", false),
            new Field("slow", false),
            new Field("cacheable", false),
            new Field("replicated", false),
            new Field("defaultView", false),
            new Field("contextID"),
            new Field("categoryID"),
            new Field("templateQueryName", ""),
            new Field("shortDescription"),
            new Field("messageWhenEmpty"),
            new Field("parametrizingOperationName", ""),
            new Field("wellKnownName", ""),
            new Field("layout", "")
    ));
    private static final List<Field> TABLE_DEF = Collections.unmodifiableList(Arrays.asList(
            new Field("name"),
            new Field("startIdVariable", "")
    ));
    private static final List<Field> VIEW_DEF = Collections.unmodifiableList(Arrays.asList(
            new Field("name")
    ));
    private static final List<Field> COLUMN_DEF = Collections.unmodifiableList(Arrays.asList(
            new Field("name"),
            new Field("type"),
            new Field("canBeNull", false),
            new Field("autoIncrement", false),
            new Field("primaryKey", false),
            new Field("defaultValue")
    ));
    private static final List<Field> INDEX_DEF = Collections.unmodifiableList(Arrays.asList(
            new Field("name"),
            new Field("unique", false)
    ));
    private static final List<Field> FILE_STRUCTURE = Collections.unmodifiableList(Arrays.asList(
            new Field("htmlDir", "src/html"),
            new Field("javaSourcesDir", "src/main/java"),
            new Field("jsDir", "src/js"),
            new Field("jsFormsDir", "src/js/forms"),
            new Field("jsOperationsDir", "src/js/operations"),
            new Field("jsExtendersDir", "src/js/extenders"),
            new Field("scriptsDir", "src/ftl"),
            new Field("iconsDir", "src/icons"),
            new Field("l10nDir", "src/l10n"),
            new Field("entitiesDir", "src/meta/entities"),
            new Field("modulesDir", "src/meta/modules"),
            new Field("dataDir", "src/meta/data"),
            new Field("macroDir", "src/include"),
            new Field("customizationFile", "src/customization.yaml"),
            new Field("daemonsFile", "src/daemons.yaml"),
            new Field("securityFile", "src/security.yaml"),
            new Field("pagesFile", "src/pages.yaml"),
            new Field("pagesDir", "src/pages"),
            new Field("jsFormsFile", "src/forms.yaml")
    ));
    private static final List<Field> QUICK_FILTER = Collections.unmodifiableList(Arrays.asList(
            new Field("name"),
            new Field("queryParam"),
            new Field("targetQueryName"),
            new Field("filteringClass")
    ));
    private static final List<Field> QUERY_SETTINGS = Collections.unmodifiableList(Arrays.asList(
            new Field("maxRecordsPerPage", 0),
            new Field("maxRecordsPerPrintPage", 0),
            new Field("maxRecordsInDynamicDropDown", 20),
            new Field("colorSchemeID"),
            new Field("autoRefresh", 0),
            new Field("beautifier", "com.beanexplorer.web.html.HtmlTableBeautifier")
    ));

    private static final String CONNECION_PROFILE_PROVIDER_ID = "providerId";
    private static final String CONNECION_PROFILE_DRIVER_DEFINITION = "driverDefinition";

    private static final List<Field> CONNECTION_PROFILE = Collections.unmodifiableList(Arrays.asList(// removed providerId and driverDefinition from this list
            new Field("connectionUrl", ""),
            new Field("username"),
            new Field("password"),
            new Field("protected", false),
            new Field("tomcatPath"),
            new Field("tomcatAppName"),
            new Field("tomcatManagerScriptUserName"),
            new Field("tomcatManagerScriptPassword"),
            new Field("tomcatManagerReloadUrlTemplate")
    ));
    private static final List<Field> CONNECTION_PROFILE_READ = Collections.unmodifiableList(Arrays.asList(
            new Field(CONNECION_PROFILE_PROVIDER_ID),
            new Field(CONNECION_PROFILE_DRIVER_DEFINITION)
    ));

    /**
     * This class is not intended to be instantiated.
     */
    private Fields()
    {
        throw new AssertionError(); // not allowed
    }

    public static List<Field> project()
    {
        return PROJECT;
    }

    public static List<Field> entity()
    {
        return ENTITY;
    }

    public static List<Field> operation()
    {
        return OPERATION;
    }

    public static List<Field> extender()
    {
        return EXTENDER;
    }

    public static List<Field> query()
    {
        return QUERY;
    }

    public static List<Field> daemon()
    {
        return DAEMON;
    }

    public static List<Field> tableDef()
    {
        return TABLE_DEF;
    }

    public static List<Field> viewDef()
    {
        return VIEW_DEF;
    }

    public static List<Field> columnDef()
    {
        return COLUMN_DEF;
    }

    public static List<Field> indexDef()
    {
        return INDEX_DEF;
    }

    public static List<Field> projectFileStructure()
    {
        return FILE_STRUCTURE;
    }

    public static List<Field> quickFilter()
    {
        return QUICK_FILTER;
    }

    public static List<Field> querySettings()
    {
        return QUERY_SETTINGS;
    }

    public static List<Field> connectionProfile()
    {
        return CONNECTION_PROFILE;
    }

    public static List<Field> connectionProfileRead()
    {
        return CONNECTION_PROFILE_READ;
    }

    public static String connectionProfileProviderId()
    {
        return CONNECION_PROFILE_PROVIDER_ID;
    }

    public static String connectionProfileDriverDefinition()
    {
        return CONNECION_PROFILE_DRIVER_DEFINITION;
    }

    public static List<Field> jsForms()
    {
        return JS_FORMS;
    }
}
