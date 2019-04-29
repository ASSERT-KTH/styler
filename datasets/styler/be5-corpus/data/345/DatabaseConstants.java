package com.developmentontheedge.be5.metadata;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Temporary interface for imported and unclassified constants from BeanExplorer EE.
 */
public interface DatabaseConstants
{
    String HIDDEN_COLUMN_PREFIX = "___";
    String GLUE_COLUMN_PREFIX = "+";
    String EXTRA_HEADER_COLUMN_PREFIX = ";";

    String ALL_RECORDS_VIEW = "All records";
    String SELECTION_VIEW = "*** Selection view ***";

    String COL_ATTR_AGGREGATE = "aggregate";
    String COL_ATTR_ROLES = "roles";
    String COL_ATTR_BLANKNULLS = "blankNulls";

    String CSS_ROW_CLASS = HIDDEN_COLUMN_PREFIX + "css_class";

    String CURRENT_ROLE_LIST = "current-role-list";

    String ID_COLUMN_LABEL = HIDDEN_COLUMN_PREFIX + "ID";

    //TODO create enum COL_ATTR
    String COL_ATTR_NULLIF = "nullif";
    String COL_ATTR_LINK = "link";

    String L10N_TOPIC_DISPLAY_NAME = "displayName";
    String L10N_TOPIC_VIEW_NAME = "viewName";
    String L10N_TOPIC_VIEW_MENU_NAME = "viewMenuName";
    String L10N_TOPIC_VIEW_TITLE_NAME = "viewTitleName";
    String L10N_TOPIC_OPERATION_NAME = "operationName";
    String L10N_TOPIC_VIEW_SHORT_DESCRIPTION = "viewShortDescription";
    String L10N_TOPIC_PAGE = "page";
    String L10N_TOPIC_CODE = "code";
    String L10N_TOPIC_SCHEME = "scheme";
    String L10N_TOPIC_ATTRIBUTE = "attribute";
    String L10N_TOPIC_INSERT = "Insert";

    String PLATFORM_HTML = "HTML";

    String WHO_INSERTED_COLUMN_NAME = "whoInserted___";
    String WHO_MODIFIED_COLUMN_NAME = "whoModified___";
    String CREATION_DATE_COLUMN_NAME = "creationDate___";
    String MODIFICATION_DATE_COLUMN_NAME = "modificationDate___";
    String IP_INSERTED_COLUMN_NAME = "ipInserted___";
    String IP_MODIFIED_COLUMN_NAME = "ipModified___";

    String IS_DELETED_COLUMN_NAME = "isDeleted___";

    List<String> specialColumns = ImmutableList.<String>builder()
            .add(WHO_INSERTED_COLUMN_NAME)
            .add(WHO_MODIFIED_COLUMN_NAME)
            .add(CREATION_DATE_COLUMN_NAME)
            .add(MODIFICATION_DATE_COLUMN_NAME)
            .add(IP_INSERTED_COLUMN_NAME)
            .add(IP_MODIFIED_COLUMN_NAME)
            .add(IS_DELETED_COLUMN_NAME)
            .build();
}
