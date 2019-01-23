package com.developmentontheedge.be5.metadata.serialization;

public interface SerializationConstants
{
    String TAG_MODULES = "modules";
    String TAG_APPLICATION = "application";
    String TAG_CODE = "code";
    String TAG_ENTITIES = "entities";
    String TAG_CUSTOMIZATIONS = "customizations";
    String TAG_EXTRAS = "extras";
    String TAG_SETTINGS = "settings";
    String TAG_DAEMONS = "daemons";
    String TAG_SCRIPTS = "scripts";
    String TAG_MACRO_FILES = "includes";
    String TAG_STATIC_PAGES = "pages";
    String TAG_JS_FORMS = "forms";
    String TAG_MASS_CHANGES = "massChanges";

    // Scheme
    String TAG_SCHEME = "scheme";
    String TAG_REFERENCES = "references";
    String TAG_REFERENCE = "reference";
    String TAG_VIEW_DEFINITION = "viewDefinition";
    String TAG_COLUMNS = "columns";
    String TAG_INDICES = "indices";
    String TAG_OLD_NAMES = "oldNames";

    String TAG_PROJECT_FILE_STRUCTURE = "projectFileStructure";

    String TAG_LOCALIZATION_ENTRY = "entry";
    String TAG_LOCALIZATION_ENTRIES = "entries";
    String ATTR_LOCALIZATION_TOPICS = "topics";

    // Generic
    String ATTR_NAME = "name";
    String ATTR_REFERENCE = "name";
    String ATTR_ROLES = "roles";
    String ATTR_FEATURES = "features";
    String ATTR_ICON = "icon";
    String ATTR_FILEPATH = "file";
    String TAG_COMMENT = "doc";

    // Project
    String ATTR_PROJECT_NAME = "name";
    String ATTR_CONNECTION_PROFILE = "connectionProfileName";
    String ATTR_LOCALIZATIONS = "l10n";
    String ATTR_MODULE_PROJECT = "moduleProject";
    String TAG_BUGTRACKERS = "bugtrackers";

    // Entity
    String ATTR_ENTITY_NAME = "name";
    String ATTR_ENTITY_TYPE = "type";
    String ATTR_ENTITY_DISPLAY_NAME = "displayName";
    String ATTR_ENTITY_PRIMARY_KEY_COLUMN = "primaryKey";
    String ATTR_ENTITY_TEMPLATE = "template";

    // Query
    String ATTR_QUERY_CODE = "value";
    String ATTR_QUERY_OPERATIONS = "operations";

    // Operation
    String ATTR_OPERATION_TYPE = "type";

    String ATTR_CLASS_NAME = "className";

    // Connection profiles
    String TAG_CONNECTION_PROFILES = "connectionProfiles";
    String TAG_CONNECTION_PROFILES_INNER = "profiles";
    String TAG_PROPERTIES = "properties";
    String TAG_REQUESTED_PROPERTIES = "ask";

    // Security
    String TAG_SECURITY = "security";
    String TAG_ROLE_GROUPS = "groups";
    String TAG_ROLES = "roles";
}
