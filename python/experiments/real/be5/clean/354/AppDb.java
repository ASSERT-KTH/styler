package com.developmentontheedge.be5.metadata.scripts;

import com.developmentontheedge.be5.metadata.exception.FreemarkerSqlException;
import com.developmentontheedge.be5.metadata.exception.ProjectElementException;
import com.developmentontheedge.be5.metadata.model.DdlElement;
import com.developmentontheedge.be5.metadata.model.Entity;
import com.developmentontheedge.be5.metadata.model.FreemarkerCatalog;
import com.developmentontheedge.be5.metadata.model.FreemarkerScript;
import com.developmentontheedge.be5.metadata.model.Module;
import com.developmentontheedge.be5.metadata.model.TableDef;
import com.developmentontheedge.be5.metadata.model.ViewDef;
import com.developmentontheedge.be5.metadata.model.base.BeVectorCollection;
import com.developmentontheedge.be5.metadata.serialization.ModuleLoader2;
import com.developmentontheedge.be5.metadata.sql.BeSqlExecutor;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class AppDb extends ScriptSupport<AppDb>
{
    private BeSqlExecutor sql;
    private PrintStream ps;

    private String moduleName;
    private int createdTables = 0;
    private int createdViews = 0;

    public String getModule()
    {
        return moduleName;
    }

    public void setModule(String module)
    {
        this.moduleName = module;
    }

    @Override
    public void execute() throws ScriptException
    {
        init();

        try
        {
            ps = createPrintStream((moduleName == null ? be5Project.getName() : moduleName) + "_db.sql");

            sql = new BeSqlExecutor(connector, ps);

            if (moduleName != null)
            {
                Module module = be5Project.getModule(moduleName);
                if (module == null)
                {
                    throw new ScriptException("Module '" + moduleName + "' not found!");
                }
                createDb(module);
            }
            else
            {
                for (Module module : be5Project.getModules())
                {
                    if (ModuleLoader2.containsModule(module.getName()))
                        createDb(module);
                }
                createDb(be5Project.getApplication());
            }
            logger.info("Created tables: " + createdTables + ", created views: " + createdViews);
        }
        catch (ScriptException e)
        {
            throw e;
        }
        catch (ProjectElementException | FreemarkerSqlException e)
        {
            throw new ScriptException("Setup db error", e);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ScriptException("Setup db error", e);
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

    private void createDb(Module module) throws ProjectElementException
    {
        BeVectorCollection<FreemarkerScript> scripts = module.getOrCreateCollection(Module.SCRIPTS, FreemarkerScript.class);
        sql.executeScript(scripts.get(FreemarkerCatalog.PRE_DB_STEP), logger);
        execute(module);
        sql.executeScript(scripts.get(FreemarkerCatalog.POST_DB_STEP), logger);
    }

    private void execute(final Module module) throws ProjectElementException
    {
        boolean started = false;
        List<Entity> entities = new ArrayList<>(module.getOrCreateEntityCollection().getAvailableElements());
        entities.sort(Comparator.comparing(this::tablesFirstViews));

        for (Entity entity : entities)
        {
            DdlElement scheme = entity.getScheme();
            if (scheme instanceof TableDef || scheme instanceof ViewDef)
            {
                if (scheme instanceof TableDef)
                {
                    createdTables++;
                }
                else
                {
                    createdViews++;
                }

                if (scheme.withoutDbScheme())
                {
                    if (!started)
                    {
                        logger.setOperationName("[A] " + module.getCompletePath());
                        started = true;
                    }
                    processDdl(scheme);
                }
                else
                {
                    logger.setOperationName("Skip table with schema: " + scheme.getEntityName());
                }
            }
        }
    }

    /**
     * Define views after tables as there might be dependencies
     */
    private Integer tablesFirstViews(Entity entity)
    {
        if (entity.getScheme() instanceof TableDef)
        {
            return 0;
        }
        else
        {
            return 1;
        }
    }

    private void processDdl(final DdlElement tableDef) throws ProjectElementException
    {
        try
        {
            final String generatedQuery = tableDef.getDdl();
            sql.executeMultiple(generatedQuery);
        }
        catch (Exception e)
        {
            throw new ProjectElementException(tableDef, e);
        }
    }

    public int getCreatedTables()
    {
        return createdTables;
    }

    public int getCreatedViews()
    {
        return createdViews;
    }

    @Override
    public AppDb me()
    {
        return this;
    }
}
