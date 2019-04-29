package com.developmentontheedge.be5.base.services.impl;

import com.developmentontheedge.be5.base.exceptions.Be5Exception;
import com.developmentontheedge.be5.base.services.ProjectProvider;
import com.developmentontheedge.be5.metadata.exception.ProjectLoadException;
import com.developmentontheedge.be5.metadata.model.Project;
import com.developmentontheedge.be5.metadata.serialization.ModuleLoader2;
import com.developmentontheedge.be5.metadata.serialization.WatchDir;
import com.google.inject.Stage;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


public class ProjectProviderImpl implements ProjectProvider
{
    private static final Logger log = Logger.getLogger(ProjectProviderImpl.class.getName());

    private Project project;
    private Map<String, Project> initModulesMap;
    private List<Runnable> callOnReload = new ArrayList<>();

    private WatchDir watcher = null;

    private volatile boolean dirty = false;

    private final Stage stage;

    @Inject
    public ProjectProviderImpl(Stage stage)
    {
        this.stage = stage;
    }

    @Override
    public void addToReload(Runnable supplier)
    {
        callOnReload.add(supplier);
    }

    @Override
    public synchronized Project get()
    {
        if (dirty || project == null)
        {
            Project oldProject = project;
            project = loadProject();

            if (oldProject != null)
            {
                callOnReload.forEach(Runnable::run);
            }
        }

        return project;
    }

    private Project loadProject()
    {
        try
        {
            if (watcher != null) watcher.stop();

            try
            {
                return ModuleLoader2.findAndLoadProjectWithModules(dirty);
            }
            catch (RuntimeException e)
            {
                if (stage == Stage.DEVELOPMENT)
                {
                    project = null;
                }
                throw new RuntimeException("Can't load project.", e);
            }
            finally
            {
                if (stage == Stage.DEVELOPMENT)
                {
                    if (initModulesMap == null) initModulesMap = ModuleLoader2.getModulesMap();
                    watcher = new WatchDir(initModulesMap)
                            .onModify(onModify -> dirty = true)
                            .start();
                }
            }
        }
        catch (ProjectLoadException | IOException e)
        {
            throw Be5Exception.internal("Can not load project", e);
        }
        finally
        {
            dirty = false;
        }
    }

    @Override
    public synchronized void reloadProject()
    {
        this.dirty = true;
        get();
    }

}
