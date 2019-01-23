package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.exception.ProjectElementException;
import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.beans.annot.PropertyDescription;
import com.developmentontheedge.beans.annot.PropertyName;
import one.util.streamex.StreamEx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@PropertyName("Operation")
public class Operation extends EntityItem
{

    public static final String OPERATION_TYPE_JAVA = "Java";
    public static final String OPERATION_TYPE_JAVAFUNCTION = "Java/function";
    public static final String OPERATION_TYPE_SQL = "SQL";
    public static final String OPERATION_TYPE_JAVASCRIPT = "JavaScript";
    public static final String OPERATION_TYPE_JSSERVER = "JavaScript/server";
    public static final String OPERATION_TYPE_DOTNET = ".Net";
    public static final String OPERATION_TYPE_JAVADOTNET = "Java and .Net";
    public static final String OPERATION_TYPE_GROOVY = "Groovy";

    public static final int VISIBLE_ALWAYS = 0;
    public static final int VISIBLE_WHEN_HAS_RECORDS = -1;
    public static final int VISIBLE_WHEN_ONE_SELECTED_RECORD = 1;
    public static final int VISIBLE_WHEN_ANY_SELECTED_RECORDS = 2;
    public static final int VISIBLE_ALL_OR_SELECTED = 999;

    private static final String OPERATION_LOGGING_EVERYTHING = "everything";
    private static final String OPERATION_LOGGING_PARAMS = "params";
    private static final String OPERATION_LOGGING_EXEC = "exec";
    private static final String OPERATION_LOGGING_NONE = "none";

    private static final String[] OPERATION_TYPE = new String[]
            {
                    OPERATION_TYPE_JAVA,
                    OPERATION_TYPE_JAVAFUNCTION,
                    OPERATION_TYPE_SQL,
                    OPERATION_TYPE_JAVASCRIPT,
                    OPERATION_TYPE_JSSERVER,
                    OPERATION_TYPE_DOTNET,
                    OPERATION_TYPE_JAVADOTNET,
                    OPERATION_TYPE_GROOVY
            };
    private static final Integer[] VISIBILITY_OPTIONS = new Integer[]
            {
                    VISIBLE_ALWAYS,
                    VISIBLE_WHEN_HAS_RECORDS,
                    VISIBLE_WHEN_ONE_SELECTED_RECORD,
                    VISIBLE_WHEN_ANY_SELECTED_RECORDS,
                    VISIBLE_ALL_OR_SELECTED
            };
    private static final String[] VISIBILITY_OPTION_STRINGS = new String[]
            {
                    "Always",
                    "When the result set has records",
                    "When one record is selected",
                    "When any number of records is selected",
                    "Always, but add checkboxes, please"
            };
    private static final String[] OPERATION_LOGGING = new String[]
            {
                    OPERATION_LOGGING_EVERYTHING,
                    OPERATION_LOGGING_PARAMS,
                    OPERATION_LOGGING_EXEC,
                    OPERATION_LOGGING_NONE
            };

    private static final String EXTENDERS_COLLECTION = "Extenders";
    private static final Set<String> CUSTOMIZABLE_PROPERTIES =
            Collections.unmodifiableSet(StreamEx.of(
                    "roles",
                    "notSupported",
                    "executionPriority",
                    "logging",
                    "secure",
                    "confirm",
                    "contextID",
                    "categoryID",
                    "wellKnownName",
                    "records",
                    "code",
                    "icon",
                    "layout").toSet());

    public static String[] getOperationTypes()
    {
        return OPERATION_TYPE.clone();
    }

    public static String[] getVisibilityOptions()
    {
        return VISIBILITY_OPTION_STRINGS.clone();
    }

    public static String getDefaultLoggingOption()
    {
        return OPERATION_LOGGING_NONE;
    }

    public static String[] getOperationLoggingOptions()
    {
        return OPERATION_LOGGING.clone();
    }

    public static Operation createOperation(String name, String type, Entity entity)
    {
        String realType = OPERATION_TYPE_JAVA;
        for (String availableType : getOperationTypes())
        {
            if (availableType.equals(type))
            {
                realType = type;
                break;
            }
        }
        if (realType.equals(OPERATION_TYPE_JAVA))
            return new JavaOperation(name, entity);
        if (realType.equals(OPERATION_TYPE_JSSERVER))
            return new JavaScriptOperation(name, entity);
        if (realType.equals(OPERATION_TYPE_GROOVY))
            return new GroovyOperation(name, entity);
        return new Operation(name, type, entity);
    }


    private String type;
    private String code = "";
    private int records;
    private int executionPriority = 999999;
    private String logging = getDefaultLoggingOption();
    private boolean confirm = false;

    protected Operation(String name, String type, Entity entity)
    {
        super(name, entity.getOperations());
        this.type = type;
    }

    @Override
    public void fireCodeChanged()
    {
        getProject().getAutomaticSerializationService().fireCodeChanged(this);
        fireChanged();
    }

    @Override
    protected void fireChanged()
    {
        final BeModelCollection<Operation> operations = getEntity().getOperations();

        if (operations != null && operations.contains(getName()))
            super.fireChanged();
    }

    // ////////////////////////////////////////////////////////////////////////
    // Properties
    //

    @PropertyName("Operation type")
    @PropertyDescription("Operation type")
    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
        fireChanged();
    }

    @PropertyName("Operation code")
    @PropertyDescription("Operation code")
    public String getCode()
    {
        return getValue("code", code, "",
                () -> ((Operation) prototype).getCode());
    }

    public void setCode(String code)
    {
        this.code = customizeProperty("code", this.code, code == null ? "" : code);
        fireCodeChanged();
    }

    @PropertyName("Required number of records")
    @PropertyDescription("Number of records which must be selected to perform an operation")
    public int getRecords()
    {
        return getValue("records", records, VISIBLE_ALWAYS,
                () -> ((Operation) prototype).getRecords());
    }

    public void setRecords(int records)
    {
        this.records = customizeProperty("records", this.records, records);
        fireChanged();
    }

    /**
     * Wraps {@link Operation#getRecords()}.
     *
     * @return
     */
    @PropertyName("Visible when...")
    public String getVisibleWhen()
    {
        return VISIBILITY_OPTION_STRINGS[Arrays.asList(VISIBILITY_OPTIONS).indexOf(records)];
    }

    /**
     * Wraps {@link Operation#setRecords(int)}
     *
     * @param visibleWhen
     */
    public void setVisibleWhen(String visibleWhen)
    {
        this.setRecords(VISIBILITY_OPTIONS[Arrays.asList(VISIBILITY_OPTION_STRINGS).indexOf(visibleWhen)]);
        fireChanged();
    }

    @PropertyName("Execution priority")
    @PropertyDescription("Default value is 999999")
    public int getExecutionPriority()
    {
        return getValue("executionPriority", executionPriority, 999999,
                () -> ((Operation) prototype).getExecutionPriority());
    }

    public void setExecutionPriority(int executionPriority)
    {
        this.executionPriority = customizeProperty("executionPriority", this.executionPriority, executionPriority);
        fireChanged();
    }

    @PropertyName("Logging")
    @PropertyDescription("Logging")
    public String getLogging()
    {
        return getValue("logging", logging, OPERATION_LOGGING_NONE,
                () -> ((Operation) prototype).getLogging());
    }

    public void setLogging(String logging)
    {
        this.logging = customizeProperty("logging", this.logging, logging);
        fireChanged();
    }

    @PropertyName("Need to confirm operation")
    public boolean isConfirm()
    {
        return getValue("confirm", confirm, false,
                () -> ((Operation) prototype).isConfirm());
    }

    public void setConfirm(boolean confirm)
    {
        this.confirm = customizeProperty("confirm", this.confirm, confirm);
        fireChanged();
    }

    public BeModelCollection<OperationExtender> getOrCreateExtenders()
    {
        return getOrCreateCollection(EXTENDERS_COLLECTION, OperationExtender.class);
    }

    public BeModelCollection<OperationExtender> getExtenders()
    {
        return getCollection(EXTENDERS_COLLECTION, OperationExtender.class);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        Operation other = (Operation) obj;
        if (!getCode().equals(other.getCode()))
            return debugEquals("code");
        if (isConfirm() != other.isConfirm())
            return debugEquals("confirm");
        if (getExecutionPriority() != other.getExecutionPriority())
            return debugEquals("executionPriority");
        if (getLogging() == null)
        {
            if (other.getLogging() != null)
                return debugEquals("logging");
        }
        else if (!getLogging().equals(other.getLogging()))
            return debugEquals("logging");
        if (getRecords() != other.getRecords())
            return debugEquals("records");
        if (type == null)
        {
            if (other.type != null)
                return debugEquals("type");
        }
        else if (!type.equals(other.type))
            return debugEquals("type");
        if (!DataElementUtils.equals(getExtenders(), other.getExtenders()))
            return debugEquals("extenders");
        return true;
    }

    @Override
    public List<ProjectElementException> getErrors()
    {
        List<ProjectElementException> result = super.getErrors();
        try
        {
            ModelValidationUtils.checkValueInSet(this, "logging", logging, OPERATION_LOGGING);
        }
        catch (ProjectElementException e)
        {
            result.add(e);
        }
        try
        {
            ModelValidationUtils.checkValueInSet(this, "records", records, VISIBILITY_OPTIONS);
        }
        catch (ProjectElementException e)
        {
            result.add(e);
        }
        Set<String> missingEntries = getRoles().getMissingEntries();
        if (!missingEntries.isEmpty())
        {
            result.add(new ProjectElementException(getCompletePath(), "roles", "Unknown role(s): " + missingEntries));
        }
        // TODO: add more checks
        return result;
    }

    @Override
    public String getEntityItemType()
    {
        return "operation";
    }

    @Override
    public Collection<String> getCustomizableProperties()
    {
        return CUSTOMIZABLE_PROPERTIES;
    }

    public List<Query> findReferencingQueries()
    {
        final List<Query> queriesWithLinks = new ArrayList<>();
        final Entity entity = getEntity();
        final BeModelCollection<Query> queries = entity.getQueries();

        for (final Query query : queries)
            if (Arrays.asList(query.getOperationNames()).contains(getName()))
                queriesWithLinks.add(query);

        return queriesWithLinks;
    }
}
