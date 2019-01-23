package com.developmentontheedge.be5.maven.generate;

import com.developmentontheedge.be5.maven.Be5Mojo;
import com.developmentontheedge.be5.metadata.scripts.wizard.ProjectWizard;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.InputStream;


@Mojo(name = "wizard")
public class ProjectWizardMojo extends Be5Mojo
{
    InputStream inputStream = System.in;

    @Override
    public void execute()
    {
        new ProjectWizard()
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
