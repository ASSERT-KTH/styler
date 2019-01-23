package com.developmentontheedge.be5.metadata.scripts;

import com.developmentontheedge.be5.metadata.exception.FreemarkerSqlException;
import com.developmentontheedge.be5.metadata.exception.ProcessInterruptedException;
import com.developmentontheedge.be5.metadata.model.ColumnDef;
import com.developmentontheedge.be5.metadata.model.DataElementUtils;
import com.developmentontheedge.be5.metadata.model.DdlElement;
import com.developmentontheedge.be5.metadata.model.Entity;
import com.developmentontheedge.be5.metadata.model.EntityType;
import com.developmentontheedge.be5.metadata.model.IndexColumnDef;
import com.developmentontheedge.be5.metadata.model.IndexDef;
import com.developmentontheedge.be5.metadata.model.Module;
import com.developmentontheedge.be5.metadata.model.Project;
import com.developmentontheedge.be5.metadata.model.SqlColumnType;
import com.developmentontheedge.be5.metadata.model.TableDef;
import com.developmentontheedge.be5.metadata.model.ViewDef;
import com.developmentontheedge.be5.metadata.sql.BeSqlExecutor;
import com.developmentontheedge.be5.metadata.sql.DatabaseUtils;
import com.developmentontheedge.be5.metadata.sql.Rdbms;
import com.developmentontheedge.be5.metadata.sql.pojo.IndexInfo;
import com.developmentontheedge.be5.metadata.sql.pojo.SqlColumnInfo;
import com.developmentontheedge.be5.metadata.sql.schema.DbmsSchemaReader;
import com.developmentontheedge.be5.metadata.sql.type.DbmsTypeManager;
import com.developmentontheedge.be5.metadata.sql.type.DefaultTypeManager;
import com.developmentontheedge.be5.metadata.util.NullLogger;
import com.developmentontheedge.be5.metadata.util.ProcessController;
import com.developmentontheedge.dbms.DbmsType;
import com.developmentontheedge.dbms.ExtendedSqlException;
import com.developmentontheedge.dbms.MultiSqlParser;

import java.io.IOException;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;


public class AppSync extends ScriptSupport<AppSync>
{
    private boolean forceUpdate;
//
//    @Parameter (property = "BE5_UPDATE_CLONES")
//    boolean updateClones;
//
//    @Parameter (property = "BE5_REMOVE_CLONES")
//    boolean removeClones;
//
//    @Parameter (property = "BE5_REMOVE_UNUSED_TABLES")
//    boolean removeUnusedTables;

    private BeSqlExecutor sqlExecutor;

    ///////////////////////////////////////////////////////////////////

    @Override
    public void execute() throws ScriptException
    {
        init();

        PrintStream ps = createPrintStream(be5Project.getName() + "_sync_ddl.sql");

        try
        {
            sqlExecutor = new BeSqlExecutor(connector, ps);

            if (be5Project.getDebugStream() != null)
            {
                be5Project.getDebugStream().println("Modules and extras for " + be5Project.getName() + ":");
                be5Project.allModules()
                        .map(m -> "- " + m.getName() + ": " + (m.getExtras() == null ? "" : String.join(", ", m.getExtras())))
                        .forEach(be5Project.getDebugStream()::println);
            }

            readSchema();
            createEntities();
            if (sqlExecutor.getConnector().getType() != DbmsType.MYSQL)
            {
                createViews();
            }

            String ddlString = getDdlStatements(false);
            ddlString = MultiSqlParser.normalize(be5Project.getDatabaseSystem().getType(), ddlString);

            if (ddlString.isEmpty())
            {
                logger.info("Database scheme is up-to-date");
                return;
            }

            if (forceUpdate)
            {
                sqlExecutor.startSection("Sync schema");
                logger.setOperationName("[>] Schema");
                sqlExecutor.executeMultiple(ddlString);
                sqlExecutor.startSection(null);
            }
            else
            {
                logger.error("The following statements should be executed to update database scheme:");
                logger.error(ddlString);
                logger.error("Use -DBE5_FORCE_UPDATE=true, for apply");
            }

            checkSynchronizationStatus();
            logger.setOperationName("Finished");
        }
        catch (FreemarkerSqlException | ExtendedSqlException | SQLException e) //ReadException | ProjectLoadException | SQLException e )
        {
            if (debug)
                throw new ScriptException("Synchronisation error: " + e.getMessage(), e);
            throw new ScriptException("Synchronisation error: " + e.getMessage());
        }
        catch (IOException | ProcessInterruptedException e)
        {
            throw new ScriptException("Synchronisation error: " + e.getMessage(), e);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new ScriptException("Synchronisation error: " + t.getMessage(), t);
        }
        finally
        {
            if (ps != null)
            {
                ps.close();
            }
        }

        logSqlFilePath();
    }

    protected void checkSynchronizationStatus()
    {
        // TODO
/*        List<ProjectElementException> warnings = databaseSynchronizer.getWarnings();
        if(!warnings.isEmpty())
        {
            logger.error( "Synchronization of " + databaseSynchronizer.getProject().getName()+" produced "+warnings.size()+" warning(s):" );
            for(ProjectElementException warning : warnings)
            {
                displayError( warning );
            }
        }
*/
    }

    ///////////////////////////////////////////////////////////////////////////
    // Read database structure
    //

    private final List<String> warnings = new ArrayList<>();
    private String defSchema = null;

    private Map<String, String> tableTypes;
    private Map<String, List<SqlColumnInfo>> columns;
    private Map<String, List<IndexInfo>> indices;
    private List<Entity> entities;

    private void readSchema() throws ExtendedSqlException, SQLException, ProcessInterruptedException
    {
        logger.info("Read database scheme ...");
        long time = System.currentTimeMillis();

        ProcessController controller = new NullLogger();

        Rdbms rdbms = DatabaseUtils.getRdbms(connector);
        DbmsSchemaReader schemaReader = rdbms.getSchemaReader();

        defSchema = schemaReader.getDefaultSchema(sqlExecutor);
        tableTypes = schemaReader.readTableNames(sqlExecutor, defSchema, controller);
        columns = schemaReader.readColumns(sqlExecutor, defSchema, controller);
        indices = schemaReader.readIndices(sqlExecutor, defSchema, controller);

        if (debug)
        {
            if (!warnings.isEmpty())
            {
                logger.error(warnings.size() + " warning(s) during loading the project from " + sqlExecutor.getConnector().getConnectString());
                Collections.sort(warnings);
                for (String warning : warnings)
                {
                    logger.error(warning);
                }
            }
        }

        logger.info("comleted, " + (System.currentTimeMillis() - time) + "ms.");
    }

    private void createEntities()
    {
        Rdbms databaseSystem = DatabaseUtils.getRdbms(connector);
        DbmsTypeManager typeManager = databaseSystem == null ? new DefaultTypeManager() : databaseSystem.getTypeManager();
        boolean casePreserved = typeManager.normalizeIdentifierCase("aA").equals("aA");

        entities = new ArrayList<>();
        Project project = new Project("internal-db");
        project.setDatabaseSystem(be5Project.getDatabaseSystem());
        Module module = new Module("temp", project);

        for (String table : tableTypes.keySet())
        {
            if (!"TABLE".equals(tableTypes.get(table.toLowerCase())))
                continue;

            List<SqlColumnInfo> columnInfos = columns.get(table.toLowerCase());
            if (columnInfos == null)
                continue;

            Entity entity = new Entity(table, module, EntityType.TABLE);
            entities.add(entity);

            TableDef tableDef = new TableDef(entity);
            for (SqlColumnInfo info : columnInfos)
            {
                ColumnDef column = new ColumnDef(info.getName(), tableDef.getColumns());
                column.setType(createColumnType(info));
                typeManager.correctType(column.getType());
                column.setPrimaryKey(info.getName().equalsIgnoreCase(entity.getPrimaryKey()));    // PENDING
                column.setCanBeNull(info.isCanBeNull());
                String defaultValue = info.getDefaultValue();
                column.setAutoIncrement(info.isAutoIncrement());
                if (!info.isAutoIncrement())
                {
                    column.setDefaultValue(defaultValue);
                }
                if (column.isPrimaryKey() && typeManager.getKeyType().equals(typeManager.getTypeClause(column.getType())))
                {
                    column.getType().setTypeName(SqlColumnType.TYPE_KEY);
                }
//                column.setOriginModuleName( module.getName() );
                DataElementUtils.saveQuiet(column);
            }

            List<IndexInfo> indexInfos = indices.get(table.toLowerCase(Locale.ENGLISH));
            if (indexInfos != null)
            {
                INDEX:
                for (IndexInfo info : indexInfos)
                {
                    if (!casePreserved)
                        info.setName(info.getName().toUpperCase(Locale.ENGLISH));

                    IndexDef index = new IndexDef(info.getName(), tableDef.getIndices());
                    index.setUnique(info.isUnique());

                    for (String indexCol : info.getColumns())
                    {
                        IndexColumnDef indexColumnDef = IndexColumnDef.createFromString(indexCol, index);
                        if (tableDef.getColumns().get(indexColumnDef.getName()) == null)
                        {
                            if (debug)
                            {
                                warnings.add("Unsupported functional index found: " + index.getName() + " (problem is here: "
                                        + indexCol + "); skipped");
                            }
                            continue INDEX;
                        }

                        DataElementUtils.saveQuiet(indexColumnDef);
                    }

                    if (index.isUnique() && index.getSize() == 1)
                    {
                        IndexColumnDef indexColumnDef = index.iterator().next();
                        if (!indexColumnDef.isFunctional())
                        {
                            if (index.getName().equalsIgnoreCase(table + "_pkey"))
                            {
                                entity.setPrimaryKey(indexColumnDef.getName());
                                continue;
                            }
                        }
                    }
                    DataElementUtils.saveQuiet(index);
                }
            }
            DataElementUtils.saveQuiet(tableDef);
        }
    }

    /**
     * For MySQL only now
     */
    private void createViews() throws ExtendedSqlException, SQLException
    {
        for (Entity entity : entities)
        {
            final String table = entity.getName();
            if (!"VIEW".equalsIgnoreCase(tableTypes.get(table)))
                continue;

            String createTable;
            ResultSet rs = sqlExecutor.executeNamedQuery("sql.getTableDefinition", table);
            try
            {
                if (!rs.next())
                    continue;

                createTable = rs.getString(2);
            }
            finally
            {
                sqlExecutor.getConnector().close(rs);
            }

            int as = createTable.indexOf(" AS ");
            if (as < 0)
                continue;

            createTable = createTable.substring(as + " AS ".length());
            ViewDef def = new ViewDef(entity);
            def.setDefinition(createTable);
            DataElementUtils.saveQuiet(def);
        }
    }

    private static SqlColumnType createColumnType(final SqlColumnInfo info)
    {
        SqlColumnType type = new SqlColumnType();
        String[] enumValues = info.getEnumValues();
        if (enumValues != null)
        {
            if (isBool(enumValues))
            {
                type.setTypeName(SqlColumnType.TYPE_BOOL);
            }
            else
            {
                type.setTypeName(SqlColumnType.TYPE_ENUM);
                Arrays.sort(enumValues);
                type.setEnumValues(enumValues);
            }
        }
        else
        {
            type.setTypeName(info.getType());
            type.setSize(info.getSize());
            type.setPrecision(info.getPrecision());
        }
        return type;
    }

    protected static boolean isBool(final String[] enumValues)
    {
        if (enumValues.length != 2)
        {
            return false;
        }

        final String val0 = enumValues[0];
        final String val1 = enumValues[1];

        return isNoYes(val0, val1) || isNoYes(val1, val0);
    }

    private static boolean isNoYes(final String val0, final String val1)
    {
        return val0.equals("no") && val1.equals("yes");
    }

    ///////////////////////////////////////////////////////////////////////////
    // Synchronization
    //

    protected String getDdlStatements(boolean dangerousOnly) throws ExtendedSqlException
    {
        Map<String, DdlElement> oldSchemes = new HashMap<>();
        Map<String, DdlElement> newSchemes = new HashMap<>();

        for (Module module : be5Project.getModulesAndApplication())
        {
            for (Entity entity : module.getEntities())
            {
                DdlElement scheme = entity.isAvailable() ? entity.getScheme() : null;
                if (scheme != null)
                {
                    String normalizedName = entity.getName().toLowerCase();
                    newSchemes.put(normalizedName, scheme);
                }
            }
        }

        for (Entity entity : entities)
        {
            DdlElement scheme = entity.isAvailable() ? entity.getScheme() : null;
            if (scheme != null)
            {
                String normalizedName = entity.getName().toLowerCase();
                oldSchemes.put(normalizedName, scheme);
            }
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, DdlElement> entity : newSchemes.entrySet())
        {
            DdlElement oldScheme = oldSchemes.get(entity.getKey());
            DdlElement newScheme = entity.getValue();

            if (newScheme.withoutDbScheme())
            {
//                if (!dangerousOnly)
//                {
//                    // PENDING - list of other type
//                    //warnings.addAll(newScheme.getWarnings());
//                }

                if (oldScheme == null)
                {
                    if (!dangerousOnly)
                    {
                        sb.append(newScheme.getCreateDdl());
                    }
                    continue;
                }

                if (newScheme.equals(oldScheme) || newScheme.getDiffDdl(oldScheme, null).isEmpty())
                    continue;

                // PENDING
                if (oldScheme instanceof TableDef && newScheme instanceof TableDef)
                    fixPrimaryKey((TableDef) oldScheme, (TableDef) newScheme);

                sb.append(dangerousOnly ? newScheme.getDangerousDiffStatements(oldScheme, sqlExecutor)
                        : newScheme.getDiffDdl(oldScheme, sqlExecutor));
            }
            else
            {
                logger.info("Skip table with schema: " + newScheme.getEntityName());
            }
        }

        //clonedEntity();

        return sb.toString();
    }

    private void clonedEntity()
    {
//        if( updateClones || removeClones || removeUnusedTables)
//        {
//            for( Entity entity : entities )
//            {
//                TableDef oldScheme = entity.findTableDefinition();
//
//                if( !oldScheme.withoutDbScheme() )
//                {
//                    logger.info("Skip table with schema: " + oldScheme.getEntityName());
//                    continue;
//                }
//
//                TableDef newScheme = (TableDef) getDdlForClone(newSchemes, entity.getName());
//
//                if( newScheme == null )
//                {
//                    if( removeUnusedTables && newSchemes.get(entity.getName().toLowerCase()) == null )
//                        sb.append(oldScheme.getDropDdl());
//                }
//                else // process clone
//                {
//                    if( removeClones )
//                    {
//                        sb.append(oldScheme.getDropDdl());
//
//                    }
//                    else if( updateClones)
//                    {
//                        String cloneId = entity.getName().substring(newScheme.getEntityName().length());
//                        Entity curEntity = newScheme.getEntity();
//                        Entity renamedEntity = curEntity.clone(curEntity.getOrigin(), entity.getName(), false);
//                        newScheme = renamedEntity.findTableDefinition();
//                        syncCloneDdl(oldScheme, newScheme, cloneId);
//                        if (!newScheme.equals(oldScheme) && !newScheme.getDiffDdl(oldScheme, null).isEmpty())
//                        {
//                            sb.append(dangerousOnly ? newScheme.getDangerousDiffStatements(oldScheme, sqlExecutor)
//                                    : newScheme.getDiffDdl(oldScheme, sqlExecutor));
//                        }
//                    }
//                }
//            }
//        }
    }

    private void fixPrimaryKey(TableDef orphanDdl, TableDef ddl)
    {
        ColumnDef pk = ddl.getColumns().get(ddl.getEntity().getPrimaryKey());
        // Orphans have no primary key set: try to set the same column as in original table
        if (pk != null)
        {
            ColumnDef orphanPk = orphanDdl.getColumns().getCaseInsensitive(pk.getName());
            if (orphanPk != null)
            {
                orphanDdl.getIndicesUsingColumn(orphanPk.getName()).stream().filter(idx -> idx.getSize() == 1 && idx.isUnique())
                        .findFirst().ifPresent(idx -> {
                    // Remove primary key index
                    DataElementUtils.remove(idx);
                    orphanDdl.getEntity().setPrimaryKey(orphanPk.getName());
                    orphanPk.setPrimaryKey(true);
                });
            }
        }
    }

    private static final Pattern CLONE_ID = Pattern.compile("(\\d+)$");
//    private static DdlElement getDdlForClone( Map<String, DdlElement> schemes, String cloneName )
//    {
//        String name = cloneName.toLowerCase();
//        Matcher matcher = CLONE_ID.matcher( name );
//        if ( !matcher.find() )
//            return null;
//        String cloneId = matcher.group();
//        return IntStreamEx.range( name.length() - cloneId.length(), name.length() )
//                .mapToObj( len -> schemes.get( name.substring( 0, len ) ) ).nonNull().findFirst().orElse( null );
//    }
//
//    // Fix known changes in cloned table and in normal table
//    private void syncCloneDdl( TableDef cloneDdl, TableDef mainDdl, String cloneId )
//    {
//        // Copy special columns
//        List<String> specialColumns = Arrays.asList( "transportstatus", "linkrule", "linkstatus", "origid" );
//        for(String colName : specialColumns)
//        {
//            ColumnDef cloneCol = cloneDdl.getColumns().getCaseInsensitive( colName );
//            ColumnDef mainCol = mainDdl.getColumns().getCaseInsensitive( colName );
//            if(cloneCol != null && mainCol == null)
//            {
//                DataElementUtils.save( cloneCol.clone( mainDdl.getColumns(), cloneCol.getName() ) );
//            }
//        }
//
//        // Map indexes as clone indexes may have different names
//        Function<? super IndexDef, ? extends List<String>> classifier = indexDef -> indexDef.stream().map( IndexColumnDef::getDefinition )
//                .toList();
//        Map<List<String>, Deque<IndexDef>> ddlMap = cloneDdl.getIndices().stream().groupingTo( classifier, ArrayDeque::new );
//        for ( IndexDef indexDef : mainDdl.getIndices().stream().toList() )
//        {
//            List<String> key = classifier.apply( indexDef );
//            Deque<IndexDef> list = ddlMap.get( key );
//            IndexDef oldIdx = list == null ? null : list.poll();
//            String newName = oldIdx == null ? indexDef.getName() + cloneId : oldIdx.getName();
//            mainDdl.renameIndex( indexDef.getName(), newName );
//        }
//        // Copy indexes for special columns
//        ddlMap.values().stream().flatMap( Deque::stream )
//                .filter( idx -> specialColumns.stream().anyMatch( col -> idx.getCaseInsensitive( col ) != null ) )
//                .map( idx -> idx.clone( mainDdl.getIndices(), idx.getName() ) )
//                .forEach( DataElementUtils::save );
//        fixPrimaryKey( cloneDdl, mainDdl );
//    }

    public AppSync setForceUpdate(boolean forceUpdate)
    {
        this.forceUpdate = forceUpdate;
        return me();
    }

    @Override
    public AppSync me()
    {
        return this;
    }
}
