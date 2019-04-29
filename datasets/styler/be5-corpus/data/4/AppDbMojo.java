package com.developmentontheedge.be5.maven;

import com.developmentontheedge.be5.metadata.scripts.AppDb;
import org.apache.maven.plugins.annotations.Mojo;


@Mojo(name = "create-db")
public class AppDbMojo extends Be5Mojo
{
    @Override
    public void execute()
    {
        new AppDb()
                .setBe5ProjectPath(projectPath.toPath())
                .setProfileName(connectionProfileName)
                .setConnectionPassword(connectionPassword)
                .setLogPath(logPath)
                .setLogger(logger)
                .setDebug(debug)
                .execute();
    }

}
