package com.developmentontheedge.be5.metadata.scripts;

import com.developmentontheedge.be5.metadata.exception.FreemarkerSqlException;
import com.developmentontheedge.be5.metadata.exception.ProjectElementException;
import com.developmentontheedge.be5.metadata.freemarker.FreemarkerSqlHandler;
import com.developmentontheedge.be5.metadata.model.FreemarkerCatalog;
import com.developmentontheedge.be5.metadata.model.FreemarkerScript;
import com.developmentontheedge.be5.metadata.model.Module;
import com.developmentontheedge.be5.metadata.model.base.DataElementPath;
import com.developmentontheedge.be5.metadata.serialization.ModuleLoader2;
import com.developmentontheedge.be5.metadata.sql.BeSqlExecutor;
import com.developmentontheedge.be5.metadata.sql.DatabaseUtils;
import com.developmentontheedge.dbms.SqlExecutor;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;


public class AppData extends ScriptSupport<AppData>
{
    private String script = FreemarkerCatalog.DATA;

    private boolean ignoreMissing = false;

    @Override
    public void execute() throws ScriptException
    {
        init();

        PrintStream ps = null;
        try
        {
            ps = createPrintStream(be5Project.getName() + "_scripts_" + script.replace(';', '_').replace(':', '.') + ".sql");

            ModuleLoader2.addModuleScripts(be5Project);

            List<FreemarkerScript> scripts = new ArrayList<>();
            for (String scriptName : script.split(";"))
            {
                int pos = scriptName.indexOf(':');
                FreemarkerCatalog scriptsCatalog = be5Project.getApplication().getFreemarkerScripts();
                if (pos > 0)
                {
                    String moduleName = scriptName.substring(0, pos);
                    scriptName = scriptName.substring(pos + 1);
                    if (moduleName.equals("all"))
                    {
                        for (Module module : be5Project.getModules())
                        {
                            scriptsCatalog = module.getFreemarkerScripts();
                            if (scriptsCatalog == null)
                                continue;
                            FreemarkerScript script = scriptsCatalog.optScript(scriptName);
                            if (script == null)
                                continue;
                            scripts.add(script);
                        }
                        FreemarkerScript script = be5Project.getApplication().getFreemarkerScripts().optScript(scriptName);
                        if (script != null)
                        {
                            scripts.add(script);
                        }
                        continue;
                    }
                    else
                    {
                        Module module = be5Project.getModule(moduleName);
                        if (module == null)
                        {
                            if (ignoreMissing)
                            {
                                logger.error("Warning: module '" + moduleName + "' not found");
                                continue;
                            }
                            else
                                throw new ScriptException("Module '" + moduleName + "' not found");
                        }
                        scriptsCatalog = module.getFreemarkerScripts();
                    }
                }
                FreemarkerScript freemarkerScript = scriptsCatalog == null ? null : scriptsCatalog.optScript(scriptName);
                if (freemarkerScript == null)
                {
                    if (ignoreMissing)
                    {
                        logger.error("Warning: FTL script '" + scriptName + "' not found");
                        continue;
                    }
                    else
                        throw new ScriptException("FTL script '" + scriptName + "' not found");
                }
                scripts.add(freemarkerScript);
            }
            SqlExecutor sqlExecutor = new BeSqlExecutor(connector, ps);
            for (FreemarkerScript freemarkerScript : scripts)
            {
                executeScript(sqlExecutor, freemarkerScript);
            }
            DatabaseUtils.clearAllCache(sqlExecutor);
        }
        catch (ProjectElementException | FreemarkerSqlException e)
        {
            throw new ScriptException(e.getMessage(), e);
        }
        catch (Exception e)
        {
            throw new ScriptException(e.getMessage(), e);
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

    protected void executeScript(final SqlExecutor sqlExecutor, FreemarkerScript freemarkerScript) throws ProjectElementException, IOException
    {
        String compiled = freemarkerScript.getResult().validate();
        if (sqlPath != null)
        {
            Files.write(
                    sqlPath.toPath().resolve(
                            be5Project.getName() + "_script_" + freemarkerScript.getModule().getName() + "_"
                                    + freemarkerScript.getName() + ".compiled"), compiled.getBytes(StandardCharsets.UTF_8));
        }
        String sql = compiled.trim();
        if (sql.isEmpty())
            return;
        DataElementPath path = freemarkerScript.getCompletePath();
        if (debug)
            logger.error(sql);
        sqlExecutor.comment("Execute " + path);
        new FreemarkerSqlHandler(sqlExecutor, debug, logger).execute(freemarkerScript);
    }

    public AppData setScript(String script)
    {
        this.script = script;
        return this;
    }

    public AppData setIgnoreMissing(boolean ignoreMissing)
    {
        this.ignoreMissing = ignoreMissing;
        return this;
    }

    @Override
    public AppData me()
    {
        return this;
    }
}
