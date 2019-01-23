package com.developmentontheedge.be5.maven;

import com.developmentontheedge.be5.metadata.scripts.AppSync;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;


@Mojo(name = "sync")
public class AppSyncMojo extends Be5Mojo
{
    @Parameter(property = "BE5_FORCE_UPDATE")
    private boolean forceUpdate = false;
//
//    @Parameter (property = "BE5_UPDATE_CLONES")
//    boolean updateClones;
//
//    @Parameter (property = "BE5_REMOVE_CLONES")
//    boolean removeClones;
//
//    @Parameter (property = "BE5_REMOVE_UNUSED_TABLES")
//    boolean removeUnusedTables;

    @Override
    public void execute()
    {
        new AppSync()
                .setBe5ProjectPath(projectPath.toPath())
                .setProfileName(connectionProfileName)
                .setConnectionPassword(connectionPassword)
                .setLogPath(logPath)
                .setLogger(logger)
                .setDebug(debug)
                .setForceUpdate(forceUpdate)
                .execute();
    }
}
