package com.developmentontheedge.be5.databasemodel.impl;

import com.developmentontheedge.be5.base.services.GroovyRegister;
import com.developmentontheedge.be5.base.services.Meta;
import com.developmentontheedge.be5.base.util.DpsUtils;
import com.developmentontheedge.be5.database.DbService;
import com.developmentontheedge.be5.database.impl.SqlHelper;
import com.developmentontheedge.be5.databasemodel.EntityModel;
import com.developmentontheedge.be5.databasemodel.RecordModel;
import com.developmentontheedge.be5.databasemodel.groovy.EntityModelMetaClass;
import com.developmentontheedge.be5.databasemodel.groovy.RecordModelMetaClass;
import com.developmentontheedge.be5.databasemodel.helpers.ColumnsHelper;
import com.developmentontheedge.be5.metadata.model.ColumnDef;
import com.developmentontheedge.be5.metadata.model.Entity;
import com.developmentontheedge.beans.DynamicProperty;
import com.developmentontheedge.beans.DynamicPropertySet;
import com.developmentontheedge.beans.DynamicPropertySetSupport;
import com.developmentontheedge.sql.format.Ast;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.developmentontheedge.be5.metadata.DatabaseConstants.IS_DELETED_COLUMN_NAME;
import static java.util.Collections.emptyMap;


public class EntityModelBase<T> implements EntityModel<T>
{
    static
    {
        GroovyRegister.registerMetaClass(EntityModelMetaClass.class, EntityModelBase.class);
        GroovyRegister.registerMetaClass(RecordModelMetaClass.class, RecordModelBase.class);
    }

    private final DbService db;
    private final SqlHelper sqlHelper;
    private final ColumnsHelper columnsHelper;
    private final Meta meta;

    private final Entity entity;

    public EntityModelBase(DbService db, SqlHelper sqlHelper, ColumnsHelper columnsHelper,
                           Meta meta, Entity entity)
    {
        this.db = db;
        this.sqlHelper = sqlHelper;
        this.columnsHelper = columnsHelper;
        this.meta = meta;

        this.entity = entity;
    }

    @Override
    public RecordModel<T> getBy(Map<String, ?> conditions)
    {
        return getColumnsBy(Collections.emptyList(), conditions);
    }

    @Override
    public RecordModel<T> get(T id)
    {
        return getBy(Collections.singletonMap(getPrimaryKeyName(), id));
    }

    @Override
    public RecordModel<T> getColumns(List<String> columns, T id)
    {
        return getColumnsBy(columns, Collections.singletonMap(getPrimaryKeyName(), id));
    }

    @Override
    public RecordModel<T> getColumnsBy(List<String> columns, Map<String, ?> conditions)
    {
        Objects.requireNonNull(conditions);
        checkPrimaryKey(conditions);

        String sql = Ast.select(addPrimaryKeyColumnIfNotEmpty(columns))
                .from(entity.getName())
                .where(conditions).format();

        DynamicPropertySetSupport dps = db.select(sql,
                rs -> DpsUtils.setValues(getDps(), rs), sqlHelper.getWithoutConstants(conditions));

        return getRecordModel(dps);
    }

    @Override
    public List<RecordModel<T>> toList()
    {
        return toList(emptyMap());
    }

    @Override
    public RecordModel<T>[] toArray()
    {
        return toArray(emptyMap());
    }

    @Override
    public List<RecordModel<T>> toList(Map<String, ?> conditions)
    {
        Objects.requireNonNull(conditions);

        String sql = Ast.selectAll().from(entity.getName()).where(conditions).format();

        return db.list(sql, this::getRecordModel, sqlHelper.getWithoutConstants(conditions));
    }

    @Override
    public RecordModel<T>[] toArray(Map<String, ?> conditions)
    {
        Objects.requireNonNull(conditions);

        List<RecordModel<T>> recordModels = toList(conditions);
        RecordModel<T>[] arr = new RecordModel[recordModels.size()];
        return recordModels.toArray(arr);
    }

    private DynamicPropertySetSupport getDps()
    {
        DynamicPropertySetSupport dps = new DynamicPropertySetSupport();
        Map<String, ColumnDef> columns = meta.getColumns(entity);
        for (Map.Entry<String, ColumnDef> column : columns.entrySet())
        {
            dps.add(getDynamicProperty(column.getValue()));
        }
        return dps;
    }

    private DynamicProperty getDynamicProperty(ColumnDef columnDef)
    {
        return new DynamicProperty(columnDef.getName(), meta.getColumnType(columnDef));
    }

    private RecordModel<T> getRecordModel(DynamicPropertySet dps)
    {
        if (dps == null) return null;

        T primaryKey = (T) dps.getProperty(getPrimaryKeyName()).getValue();

        return new RecordModelBase<>(primaryKey, this, dps);
    }

    private RecordModel<T> getRecordModel(ResultSet rs)
    {
        return getRecordModel(DpsUtils.setValues(getDps(), rs));
    }

    private List<String> addPrimaryKeyColumnIfNotEmpty(List<String> columns)
    {
        List<String> columnsWithPK = columns;
        if (columns.size() > 0 && !columns.contains(getPrimaryKeyName()))
        {
            columnsWithPK = new ArrayList<>(columns);
            columnsWithPK.add(getPrimaryKeyName());
        }
        return columnsWithPK;
    }

    @Override
    public long count()
    {
        return count(Collections.emptyMap());
    }

    @Override
    public long count(Map<String, ?> conditions)
    {
        Objects.requireNonNull(conditions);

        String sql = Ast.selectCount().from(entity.getName()).where(conditions).format();

        return db.oneLong(sql, sqlHelper.getWithoutConstants(conditions));
    }

    @Override
    public boolean isEmpty()
    {
        return count() == 0;
    }

    @Override
    public boolean contains(Map<String, ?> conditions)
    {
        Objects.requireNonNull(conditions);
        return count(conditions) != 0;
    }

    @Override
    public boolean containsAll(Collection<Map<String, ?>> c)
    {
        return c.stream().allMatch(this::contains);
    }

    @Override
    public <R> R add(Map<String, ?> values)
    {
        Objects.requireNonNull(values);

        Map<String, Object> map = new LinkedHashMap<>(values);
        map.values().removeIf(Objects::isNull);

        columnsHelper.addInsertSpecialColumns(entity, map);
        columnsHelper.checkDpsColumns(entity, map);

        return sqlHelper.insert(entity.getName(), map);
    }

    @Override
    public final <R> R add(DynamicPropertySet dps)
    {
        Objects.requireNonNull(dps);

        return add(DpsUtils.toLinkedHashMap(dps));
    }

    @Override
    public <R> List<R> addAll(final Collection<Map<String, ?>> c)
    {
        final List<R> keys = new ArrayList<>(c.size());
        for (Map<String, ?> values : c)
        {
            keys.add(add(values));
        }
        return keys;
    }

    @Override
    public int set(T id, String propertyName, Object value)
    {
        Objects.requireNonNull(id);
        Objects.requireNonNull(propertyName);
        Objects.requireNonNull(value);
        return this.set(id, Collections.singletonMap(propertyName, value));
    }

    @Override
    public int set(T id, Map<String, ?> values)
    {
        Objects.requireNonNull(id);
        Objects.requireNonNull(values);

        Map<String, Object> map = new LinkedHashMap<>(values);

        columnsHelper.addUpdateSpecialColumns(entity, map);

        return sqlHelper.update(entity.getName(),
                Collections.singletonMap(getPrimaryKeyName(), checkPrimaryKey(id)),
                map);
    }

    @Override
    public int set(T id, DynamicPropertySet dps)
    {
        Objects.requireNonNull(id);
        Objects.requireNonNull(dps);

        return set(id, DpsUtils.toLinkedHashMap(dps));
    }

    @Override
    @SuppressWarnings("unchecked")
    public final int remove(T id)
    {
        Objects.requireNonNull(id);
        return removeWhereColumnIn(getPrimaryKeyName(), (T[]) new Object[]{id});
    }

    @Override
    public int remove(T[] ids)
    {
        return removeWhereColumnIn(getPrimaryKeyName(), ids);
    }

    @Override
    public int removeWhereColumnIn(String columnName, T[] ids)
    {
        Objects.requireNonNull(columnName);
        Objects.requireNonNull(ids);
        if (ids.length == 0) return 0;

        if (columnName.equals(getPrimaryKeyName())) checkPrimaryKey(ids);

        Map<String, ColumnDef> columns = meta.getColumns(entity);

        if (columns.containsKey(IS_DELETED_COLUMN_NAME))
        {
            Map<String, ?> values = columnsHelper.addDeleteSpecialValues(entity, new LinkedHashMap<>());
            return sqlHelper.updateIn(entity.getName(), columnName, ids, values);
        }
        else
        {
            return sqlHelper.deleteIn(entity.getName(), columnName, ids);
        }
    }

    @Override
    public int removeAll()
    {
        return removeBy(emptyMap());
    }

    @Override
    public int removeBy(Map<String, ?> conditions)
    {
        Objects.requireNonNull(conditions);

        Map<String, ColumnDef> columns = meta.getColumns(entity);
        if (columns.containsKey(IS_DELETED_COLUMN_NAME))
        {
            Map<String, ?> values = columnsHelper.addDeleteSpecialValues(entity, new LinkedHashMap<>());
            return sqlHelper.update(entity.getName(), conditions, values);
        }
        else
        {
            return sqlHelper.delete(entity.getName(), conditions);
        }
    }

    @Override
    public String getEntityName()
    {
        return this.entity.getName();
    }

    @Override
    public String getPrimaryKeyName()
    {
        return entity.getPrimaryKey();
    }

//
//    @Override
//    public List<RecordModel> collect()
//    {
//        return toList();
//    }
//
//    @Override
//    public <T> List<T> collect( Map<String, ?> values, BiFunction<R, Integer, T> lambda )
//    {
//        Objects.requireNonNull(values);
//        Objects.requireNonNull(lambda);
//
//        MultipleRecordsBase<List<T>>  records = new MultipleRecordsBase<>();
//        MultipleRecordsBase.LambdaDPSHandler<R, T> handler = new AbstractMultipleRecords.LambdaDPSHandler<>( lambda );
//        records.setHandler( handler );
//        records.getBy( values );
//        return handler.getResult();
//    }

    @Override
    public String toString()
    {
        return getClass().getSimpleName() + "[ entityName = " + getEntityName() + " ]";
    }

//    @Override
//    public void setMany( Map<String, String> values, Map<String, String> conditions )
//    {
//        Objects.requireNonNull(values);
//        Objects.requireNonNull(conditions);
//        setForceMany(values, conditions);
//    }

    private void checkPrimaryKey(Map<String, ?> conditions)
    {
        for (Map.Entry<String, ?> entry : conditions.entrySet())
        {
            if (entry.getKey().equalsIgnoreCase(getPrimaryKeyName())) checkPrimaryKey((T) entry.getValue());
        }
    }

    private void checkPrimaryKey(T[] ids)
    {
        for (T id : ids)
        {
            checkPrimaryKey(id);
        }
    }

    private T checkPrimaryKey(T id)
    {
        Class<?> primaryKeyColumnType = meta.getColumnType(entity, getPrimaryKeyName());

        if (id.getClass() != primaryKeyColumnType)
        {
            throw new RuntimeException("Primary key must be " + primaryKeyColumnType.getSimpleName() + " instead " + id.getClass().getSimpleName());
        }

        return id;
    }

//
//    @Override
//    public void setForceMany(String propertyName, String value, Map<String, String> conditions)
//    {
//        Objects.requireNonNull(propertyName);
//        Objects.requireNonNull(value);
//        Objects.requireNonNull(conditions);
//        setForceMany( Collections.singletonMap( propertyName, value ), conditions);
//    }

//    /**
//     * in process
//     */
//    @Override
//    public void setForceMany(Map<String, ?> values, Map<String, ?> conditions)
//    {
//        Objects.requireNonNull(values);
//        Objects.requireNonNull(conditions);
//
//        DynamicPropertySet dps = db.select(
//                Ast.selectAll().from(entity.getName()).where(conditions).limit(1).format(),
//                rs -> dpsHelper.addDpForColumns(entity, values.keySet(), rs), castValues(entity, conditions));
//
//        dpsHelper.updateValuesWithSpecial(dps, values);
//
//        db.update(dpsHelper.generateUpdateSqlForConditions(entity, dps, conditions),
//                ObjectArrays.concat(dpsHelper.getValuesFromJson(dps), castValues(entity, conditions), Object.class));
//    }

    @Override
    public Entity getEntity()
    {
        return entity;
    }
}
