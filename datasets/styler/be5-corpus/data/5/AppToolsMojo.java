package com.developmentontheedge.be5.maven;

import com.developmentontheedge.be5.metadata.scripts.AppTools;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.InputStream;


@Mojo(name = "tools")
public class AppToolsMojo extends Be5Mojo
{
    InputStream inputStream = System.in;

    @Override
    public void execute() throws MojoFailureException
    {
        new AppTools()
                .setBe5ProjectPath(projectPath.getPath())
                .setProfileName(connectionProfileName)
                .setConnectionPassword(connectionPassword)
                .setLogPath(logPath)
                .setLogger(logger)
                .setDebug(debug)
                .setInputStream(inputStream)
                .execute();
    }
}
