package com.developmentontheedge.be5.server.helpers;

import com.developmentontheedge.be5.base.exceptions.Be5Exception;
import com.developmentontheedge.be5.base.services.Meta;
import com.developmentontheedge.be5.base.services.UserAwareMeta;
import com.developmentontheedge.be5.base.util.DpsUtils;
import com.developmentontheedge.be5.metadata.model.ColumnDef;
import com.developmentontheedge.be5.metadata.model.Entity;
import com.developmentontheedge.be5.metadata.model.GroovyOperation;
import com.developmentontheedge.be5.metadata.model.JavaOperation;
import com.developmentontheedge.be5.metadata.model.Operation;
import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.be5.metadata.model.base.BeModelElement;
import com.developmentontheedge.be5.metadata.util.Strings2;
import com.developmentontheedge.be5.operation.services.validation.ValidationRules;
import com.developmentontheedge.be5.operation.util.FilterUtil;
import com.developmentontheedge.be5.query.services.QueriesService;
import com.developmentontheedge.beans.BeanInfoConstants;
import com.developmentontheedge.beans.DynamicProperty;
import com.developmentontheedge.beans.DynamicPropertySet;
import com.developmentontheedge.sql.model.AstBeParameterTag;
import com.developmentontheedge.sql.model.AstStart;
import com.developmentontheedge.sql.model.SqlQuery;
import com.google.common.math.LongMath;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.developmentontheedge.be5.metadata.DatabaseConstants.HIDDEN_COLUMN_PREFIX;
import static com.developmentontheedge.be5.metadata.DatabaseConstants.specialColumns;
import static com.developmentontheedge.be5.metadata.model.SqlColumnType.TYPE_BIGINT;
import static com.developmentontheedge.be5.metadata.model.SqlColumnType.TYPE_BOOL;
import static com.developmentontheedge.be5.metadata.model.SqlColumnType.TYPE_CHAR;
import static com.developmentontheedge.be5.metadata.model.SqlColumnType.TYPE_CURRENCY;
import static com.developmentontheedge.be5.metadata.model.SqlColumnType.TYPE_DECIMAL;
import static com.developmentontheedge.be5.metadata.model.SqlColumnType.TYPE_INT;
import static com.developmentontheedge.be5.metadata.model.SqlColumnType.TYPE_TEXT;
import static com.developmentontheedge.be5.metadata.model.SqlColumnType.TYPE_UBIGINT;
import static com.developmentontheedge.be5.metadata.model.SqlColumnType.TYPE_UINT;
import static com.developmentontheedge.be5.metadata.model.SqlColumnType.TYPE_VARCHAR;
import static com.developmentontheedge.be5.operation.services.validation.ValidationRules.range;
import static com.developmentontheedge.be5.operation.services.validation.ValidationRules.step;


public class DpsHelper
{
    private static final Logger log = Logger.getLogger(DpsHelper.class.getName());

    private final Meta meta;
    private final UserAwareMeta userAwareMeta;
    private final QueriesService queries;

    @Inject
    public DpsHelper(Meta meta, QueriesService queries, UserAwareMeta userAwareMeta)
    {
        this.meta = meta;
        this.userAwareMeta = userAwareMeta;
        this.queries = queries;
    }

//    public <T extends DynamicPropertySet> T addDp(T dps, BeModelElement modelElements, ResultSet resultSet, Map<String, Object> operationParams)
//    {
//        addDp(dps, modelElements, operationParams);
//        return setValues(dps, resultSet);
//    }

//    public <T extends DynamicPropertySet> T addDpWithoutTags(T dps, BeModelElement modelElements, ResultSet resultSet)
//    {
//        addDpWithoutTags(dps, modelElements);
//        return setValues(dps, resultSet);
//    }

//    public DynamicPropertySet getDpsForColumns(BeModelElement modelElements, Collection<String> columnNames, ResultSet resultSet)
//    {
//        DynamicPropertySet dps = getDpsForColumns(modelElements, columnNames);
//        return setValues(dps, resultSet);
//    }

//    public DynamicPropertySet getDpsExcludeAutoIncrement(BeModelElement modelElements, ResultSet resultSet)
//    {
//        DynamicPropertySet dps = getDpsExcludeAutoIncrement(modelElements);
//        return setValues(dps, resultSet);
//    }

    public <T extends DynamicPropertySet> T addDpExcludeAutoIncrement(T dps, BeModelElement modelElements,
                                                                      Map<String, Object> operationParams, Map<String, ? super Object> values)
    {
        addDpExcludeAutoIncrement(dps, modelElements, operationParams);
        return DpsUtils.setValues(dps, values);
    }

    public <T extends DynamicPropertySet> T addDpExcludeAutoIncrement(T dps, BeModelElement modelElements, Map<String, Object> operationParams)
    {
        List<String> excludedColumns = Collections.emptyList();
        if (meta.getColumn(getEntity(modelElements), getEntity(modelElements).getPrimaryKey()) != null &&
                meta.getColumn(getEntity(modelElements), getEntity(modelElements).getPrimaryKey()).isAutoIncrement())
        {
            excludedColumns = Collections.singletonList(getEntity(modelElements).getPrimaryKey());
        }

        return addDpExcludeColumns(dps, modelElements, excludedColumns, operationParams);
    }

    public <T extends DynamicPropertySet> T addDp(T dps, BeModelElement modelElements, Map<String, Object> operationParams)
    {
        return addDpExcludeColumns(dps, modelElements, Collections.emptyList(), operationParams);
    }

    public <T extends DynamicPropertySet> T addDpWithoutTags(T dps, BeModelElement modelElements)
    {
        return addDpsExcludedColumnsWithoutTags(dps, modelElements, Collections.emptyList());
    }

    public <T extends DynamicPropertySet> T addDpExcludeColumns(T dps, BeModelElement modelElements,
                                                                Collection<String> columnNames, Map<String, Object> operationParams, Map<String, ? super Object> presetValues)
    {
        addDpExcludeColumns(dps, modelElements, columnNames, operationParams);

        DpsUtils.setValues(dps, presetValues);
        setOperationParams(dps, operationParams);

        return dps;
    }

    public <T extends DynamicPropertySet> T addDpExcludeColumns(T dps, BeModelElement modelElements,
                                                                Collection<String> columnNames, Map<String, Object> operationParams)
    {
        addDpsExcludedColumnsWithoutTags(dps, modelElements, columnNames);

        addTags(dps, modelElements, dps.asMap().keySet().stream().filter(i -> !columnNames.contains(i)).collect(Collectors.toList()), operationParams);
        setOperationParams(dps, operationParams);

        return dps;
    }

    public <T extends DynamicPropertySet> T addTags(T dps, BeModelElement modelElements, Collection<String> columnNames, Map<String, Object> operationParams)
    {
        Map<String, ColumnDef> columns = getColumnsWithoutSpecial(modelElements);
        for (String propertyName : columnNames)
        {
            DynamicProperty property = dps.getProperty(propertyName);
            ColumnDef columnDef = columns.get(property.getName());
            if (columnDef != null) addTags(property, columnDef, operationParams);
        }
        return dps;
    }

    public <T extends DynamicPropertySet> T addDpsExcludedColumnsWithoutTags(T dps, BeModelElement modelElements, Collection<String> excludedColumns)
    {
        Map<String, ColumnDef> columns = getColumnsWithoutSpecial(modelElements);

        ArrayList<String> excludedColumnsList = new ArrayList<>(excludedColumns);
        for (Map.Entry<String, ColumnDef> entry : columns.entrySet())
        {
            if (!excludedColumnsList.contains(entry.getKey()))
            {
                DynamicProperty dynamicProperty = getDynamicProperty(entry.getValue());
                addMeta(dynamicProperty, entry.getValue(), modelElements);
                dps.add(dynamicProperty);
            }
            excludedColumnsList.remove(entry.getKey());
        }

        for (String propertyName : excludedColumnsList)
        {
            log.warning("Column " + propertyName + " not found in " + getEntity(modelElements));
        }
        return dps;
    }

    public <T extends DynamicPropertySet> T addDpForColumns(T dps, BeModelElement modelElements, Collection<String> columnNames,
                                                            Map<String, Object> operationParams, Map<String, ? super Object> presetValues)
    {
        addDpForColumns(dps, modelElements, columnNames, operationParams);

        DpsUtils.setValues(dps, presetValues);
        setOperationParams(dps, operationParams);

        return dps;
    }

    public <T extends DynamicPropertySet> T addDpForColumns(T dps, BeModelElement modelElements, Collection<String> columnNames,
                                                            Map<String, Object> operationParams)
    {
        addDpForColumnsWithoutTags(dps, modelElements, columnNames);

        addTags(dps, modelElements, columnNames, operationParams);
        setOperationParams(dps, operationParams);

        return dps;
    }

    public <T extends DynamicPropertySet> T addDynamicProperties(T dps, BeModelElement modelElements,
                                                                 Collection<String> propertyNames, Map<String, Object> operationParams)
    {
        Map<String, ColumnDef> columns = getColumnsWithoutSpecial(modelElements);

        for (String propertyName : propertyNames)
        {
            ColumnDef columnDef = columns.get(propertyName);
            DynamicProperty dynamicProperty = getDynamicProperty(columnDef);
            addMeta(dynamicProperty, columnDef, modelElements);
            addTags(dynamicProperty, columnDef, operationParams);

            dps.add(dynamicProperty);
        }
        return dps;
    }

    public <T extends DynamicPropertySet> T addDpForColumnsWithoutTags(T dps, BeModelElement modelElements, Collection<String> columnNames,
                                                                       Map<String, ? super Object> presetValues)
    {
        addDpForColumnsWithoutTags(dps, modelElements, columnNames);

        addMeta(dps, modelElements);

        DpsUtils.setValues(dps, presetValues);

        return dps;
    }

    public <T extends DynamicPropertySet> T addDpForColumnsBase(T dps, BeModelElement modelElements, Collection<String> columnNames,
                                                                Map<String, ? super Object> presetValues)
    {
        addDpForColumnsBase(dps, modelElements, columnNames);

        DpsUtils.setValues(dps, presetValues);

        return dps;
    }

    public <T extends DynamicPropertySet> T addMeta(T dps, BeModelElement modelElements)
    {
        Map<String, ColumnDef> columns = getColumnsWithoutSpecial(modelElements);
        for (DynamicProperty property : dps)
        {
            ColumnDef columnDef = columns.get(property.getName());
            if (columnDef != null) addMeta(property, columnDef, modelElements);
        }
        return dps;
    }

    public <T extends DynamicPropertySet> T addDpForColumnsWithoutTags(T dps, BeModelElement modelElements, Collection<String> columnNames)
    {
        Map<String, ColumnDef> columns = getColumnsWithoutSpecial(modelElements);
        for (String propertyName : columnNames)
        {
            ColumnDef columnDef = columns.get(propertyName);
            if (columnDef != null)
            {
                DynamicProperty dynamicProperty = getDynamicProperty(columnDef);
                addMeta(dynamicProperty, columnDef, modelElements);
                dps.add(dynamicProperty);
            }
            else
            {
                throw Be5Exception.internal(getEntity(modelElements) + "' not contain column " + propertyName);
            }
        }
        return dps;
    }

    public DynamicProperty getDynamicProperty(ColumnDef columnDef)
    {
        return new DynamicProperty(columnDef.getName(), meta.getColumnType(columnDef));
    }

    public <T extends DynamicPropertySet> T addDpBase(T dps, BeModelElement modelElements)
    {
        Map<String, ColumnDef> columns = getColumnsWithoutSpecial(modelElements);
        for (Map.Entry<String, ColumnDef> column : columns.entrySet())
        {
            dps.add(getDynamicProperty(column.getValue()));
        }
        return dps;
    }

    public <T extends DynamicPropertySet> T addDpForColumnsBase(T dps, BeModelElement modelElements, Collection<String> columnNames)
    {
        Map<String, ColumnDef> columns = getColumnsWithoutSpecial(modelElements);
        for (String propertyName : columnNames)
        {
            ColumnDef columnDef = columns.get(propertyName);
            if (columnDef != null)
            {
                DynamicProperty dynamicProperty = getDynamicProperty(columnDef);
                dps.add(dynamicProperty);
            }
            else
            {
                throw Be5Exception.internal(getEntity(modelElements) + "' not contain column " + propertyName);
            }
        }
        return dps;
    }

    public <T extends DynamicPropertySet> T addParamsFromQuery(T dps, BeModelElement modelElements,
                                                               Query query, Map<String, Object> operationParams)
    {
        AstStart ast;
        try
        {
            ast = SqlQuery.parse(meta.getQueryCode(query));
        }
        catch (RuntimeException e)
        {
            log.log(Level.SEVERE, "SqlQuery.parse error: ", e);
            throw Be5Exception.internalInQuery(query, e);
        }

        List<String> usedParams = ast.tree().select(AstBeParameterTag.class).map(AstBeParameterTag::getName).toList();
        Map<String, ColumnDef> entityColumns = meta.getColumns(getEntity(modelElements));
        for (String param: usedParams)
        {
            if (entityColumns.containsKey(param))
            {
                addDpForColumns(dps, modelElements, Collections.singletonList(param), operationParams);
            }
            else
            {
                dps.add(new DynamicProperty(param, String.class));
            }
        }

        return dps;
    }

    public DynamicProperty addMeta(DynamicProperty dp, ColumnDef columnDef, BeModelElement modelElements)
    {
        if (modelElements.getClass() == Query.class)
        {
            dp.setDisplayName(userAwareMeta.getColumnTitle(
                    columnDef.getEntity().getName(),
                    modelElements.getName(),
                    columnDef.getName()
            ));
        }
        else if (modelElements.getClass() == Entity.class)
        {
            dp.setDisplayName(userAwareMeta.getColumnTitle(
                    columnDef.getEntity().getName(),
                    columnDef.getName()
            ));
        }
        else if (modelElements.getClass() == JavaOperation.class || modelElements.getClass() == GroovyOperation.class)
        {
            dp.setDisplayName(userAwareMeta.getLocalizedOperationField(
                    columnDef.getEntity().getName(),
                    modelElements.getName(),
                    columnDef.getName()
            ));
        }

        if (columnDef.getDefaultValue() != null)
        {
            dp.setValue(meta.getColumnDefaultValue(columnDef));
        }

        if (columnDef.isCanBeNull() ||
                (columnDef.getTypeString().equals(TYPE_BOOL) && columnDef.getDefaultValue() != null))
        {
            dp.setCanBeNull(true);
        }

        String typeName = columnDef.getType().getTypeName();

        if (TYPE_TEXT.equals(typeName))
        {
            dp.setAttribute(BeanInfoConstants.EXTRA_ATTRS, new String[][]{{"inputType", "textArea"}});
        }

        if (TYPE_VARCHAR.equals(typeName) || TYPE_CHAR.equals(typeName))
        {
            dp.setAttribute(BeanInfoConstants.COLUMN_SIZE_ATTR, columnDef.getType().getSize());
        }

        if (TYPE_DECIMAL.equals(typeName))
        {
            int size = columnDef.getType().getSize();
            dp.setAttribute(BeanInfoConstants.VALIDATION_RULES, Arrays.asList(
                    getRange(size, false),
                    step(getPrecision(columnDef.getType().getPrecision()))
            ));
        }

        if (TYPE_CURRENCY.equals(typeName))
        {
            dp.setAttribute(BeanInfoConstants.VALIDATION_RULES, Arrays.asList(
                    getRange(columnDef.getType().getSize(), false),
                    step(0.01)
            ));
        }

        if (TYPE_INT.equals(typeName) || TYPE_UINT.equals(typeName))
        {
            boolean unsigned = TYPE_UINT.equals(typeName);

            dp.setAttribute(BeanInfoConstants.VALIDATION_RULES, Arrays.asList(
                    range(unsigned ? 0 : Integer.MIN_VALUE, Integer.MAX_VALUE),
                    step(1)
            ));
        }

        if (TYPE_BIGINT.equals(typeName) || TYPE_UBIGINT.equals(typeName))
        {
            boolean unsigned = TYPE_UBIGINT.equals(typeName);

            dp.setAttribute(BeanInfoConstants.VALIDATION_RULES, Arrays.asList(
                    range(unsigned ? 0 : Long.MIN_VALUE, Long.MAX_VALUE),
                    step(1)
            ));
        }

        if (columnDef.getName().endsWith(HIDDEN_COLUMN_PREFIX)) dp.setHidden(true);

        return dp;
    }

    public String getPrecision(int precision)
    {
        switch (precision)
        {
            case 0:
                return "1";
            case 1:
                return "0.1";
            case 2:
                return "0.01";
            case 3:
                return "0.001";
            case 4:
                return "1.0E-4";
            default:
                return "1.0E-" + Integer.toString(precision);
        }
    }

    public ValidationRules.Rule getRange(int size, boolean unsigned)
    {
        if (size <= 18)
        {
            return range(unsigned ? 0 : -LongMath.pow(10, size), LongMath.pow(10, size));
        }
        else
        {
            return range(unsigned ? 0 : -Math.pow(10, size), Math.pow(10, size));
        }
    }

    public void addTags(DynamicProperty dp, ColumnDef columnDef, Map<String, Object> operationParams)
    {
        String tableName = columnDef.getTableTo();
        String[][] tags = null;
        if (columnDef.getType().getTypeName().equals(TYPE_BOOL))
        {
            tags = queries.getTagsYesNo();
        }
        else if (columnDef.getType().getEnumValues() != Strings2.EMPTY)
        {
            tags = queries.getTagsFromEnum(columnDef);
        }
        else if (tableName != null && meta.getEntity(tableName) != null)
        {
            Map<String, Object> operationParamsWithoutFilter = FilterUtil.getOperationParamsWithoutFilter(operationParams);
            String propertyName = dp.getName();
            if (operationParamsWithoutFilter.containsKey(propertyName))
            {
                tags = getTagForPrimaryKeyValue(tableName, operationParamsWithoutFilter.get(propertyName));
            }
            else
            {
                tags = queries.getTagsFromSelectionView(tableName, operationParamsWithoutFilter);
            }
        }

        if (tags != null)
        {
            dp.setAttribute(BeanInfoConstants.TAG_LIST_ATTR, tags);
        }
    }

    private String[][] getTagForPrimaryKeyValue(String tableName, Object value)
    {
        return queries.getTagsFromSelectionView(tableName,
                Collections.singletonMap(meta.getEntity(tableName).getPrimaryKey(), value));
    }

    public Object[] getValues(DynamicPropertySet dps)
    {
        return StreamSupport.stream(dps.spliterator(), false)
                .map(DynamicProperty::getValue).toArray();
    }

    public <T extends DynamicPropertySet> T addLabel(T dps, String text)
    {
        dps.add(getLabel("infoLabel", text));
        return dps;
    }

    public <T extends DynamicPropertySet> T addLabel(T dps, String name, String text)
    {
        dps.add(getLabel(name, text));
        return dps;
    }

    public <T extends DynamicPropertySet> T addLabelRaw(T dps, String text)
    {
        dps.add(getLabelRaw("infoLabel", text));
        return dps;
    }

    public <T extends DynamicPropertySet> T addLabelRaw(T dps, String name, String text)
    {
        dps.add(getLabelRaw(name, text));
        return dps;
    }

    public DynamicProperty getLabel(String text)
    {
        return getLabel("infoLabel", text);
    }

    public DynamicProperty getLabel(String name, String text)
    {
        DynamicProperty label = new DynamicProperty(name, String.class, text);
        label.setAttribute(BeanInfoConstants.LABEL_FIELD, true);
        label.setAttribute(BeanInfoConstants.CAN_BE_NULL, true);
        label.setReadOnly(true);
        return label;
    }

    public DynamicProperty getLabelRaw(String text)
    {
        return getLabelRaw("infoLabel", text);
    }

    public DynamicProperty getLabelRaw(String name, String text)
    {
        DynamicProperty label = getLabel(name, text);
        label.setAttribute(BeanInfoConstants.RAW_VALUE, true);

        return label;
    }

    public Map<String, Object> getAsMap(DynamicPropertySet dps, Map<String, Object> presetValues)
    {
        Map<String, Object> values = getAsMap(dps);
        values.putAll(presetValues);

        return values;
    }

    public Map<String, Object> getAsMap(DynamicPropertySet dps)
    {
        Map<String, Object> values = new HashMap<>();
        dps.forEach(p -> values.put(p.getName(), p.getValue()));

        return values;
    }

    public Map<String, Object> getAsMapStringValues(DynamicPropertySet dps)
    {
        Map<String, Object> values = new HashMap<>();
        dps.forEach(p -> {
            if (p.getValue() != null && !p.getBooleanAttribute(BeanInfoConstants.LABEL_FIELD))
            {
                values.put(p.getName(), p.getValue().toString());
            }
        });

        return values;
    }

    public <T extends DynamicPropertySet> T setOperationParams(T dps, Map<String, Object> operationParams)
    {
        Map<String, ?> params = FilterUtil.getOperationParamsWithoutFilter(operationParams);

        for (Map.Entry<String, ?> entry : params.entrySet())
        {
            DynamicProperty property = dps.getProperty(entry.getKey());
            if (property != null)
            {
                property.setValue(entry.getValue());
                property.setReadOnly(true);
            }
        }
        return dps;
    }

    private Entity getEntity(BeModelElement modelElements)
    {
        if (modelElements.getClass() == Entity.class)
        {
            return (Entity) modelElements;
        }
        else if (modelElements.getClass() == Query.class)
        {
            return ((Query) modelElements).getEntity();
        }
        else if (modelElements.getClass() == JavaOperation.class || modelElements.getClass() == GroovyOperation.class)
        {
            return ((Operation) modelElements).getEntity();
        }
        else
        {
            throw new RuntimeException("not supported modelElements");
        }
    }

    protected Map<String, ColumnDef> getColumnsWithoutSpecial(BeModelElement modelElements)
    {
        Map<String, ColumnDef> columns = meta.getColumns(getEntity(modelElements));
        for (String specialColumnsName : specialColumns)
        {
            columns.remove(specialColumnsName);
        }
        return columns;
    }
}
