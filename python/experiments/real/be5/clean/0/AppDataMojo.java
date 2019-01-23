package com.developmentontheedge.be5.maven;

import com.developmentontheedge.be5.metadata.model.FreemarkerCatalog;
import com.developmentontheedge.be5.metadata.scripts.AppData;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;


@Mojo(name = "data")
public class AppDataMojo extends Be5Mojo
{
    @Parameter(property = "BE5_SCRIPT")
    String script = FreemarkerCatalog.DATA;

    @Parameter(property = "BE5_IGNORE_MISSING")
    boolean ignoreMissing = false;

    @Override
    public void execute()
    {
        new AppData()
                .setBe5ProjectPath(projectPath.toPath())
                .setProfileName(connectionProfileName)
                .setConnectionPassword(connectionPassword)
                .setLogPath(logPath)
                .setLogger(logger)
                .setDebug(debug)
                .setScript(script)
                .setIgnoreMissing(ignoreMissing)
                .execute();
    }
}
