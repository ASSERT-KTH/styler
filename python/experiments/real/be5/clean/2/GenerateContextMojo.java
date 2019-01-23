package com.developmentontheedge.be5.maven.generate;

import com.developmentontheedge.be5.maven.Be5Mojo;
import com.developmentontheedge.be5.metadata.scripts.generate.GenerateContext;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;


@Mojo(name = "generate-context")
public class GenerateContextMojo extends Be5Mojo
{
    @Parameter(property = "GENERATE_CONTEXT_PATH")
    String generateContextPath;

    @Parameter(property = "SKIP_GENERATE_CONTEXT")
    boolean skipGenerateContextPath = false;

    @Parameter(property = "ENV_NAME")
    String environmentName;

    @Override
    public void execute() throws MojoFailureException
    {
        new GenerateContext()
                .setBe5ProjectPath(projectPath.getPath())
                .setProfileName(connectionProfileName)
                .setConnectionPassword(connectionPassword)
                .setGenerateContextPath(generateContextPath)
                .setSkipGenerateContextPath(skipGenerateContextPath)
                .setLogPath(logPath)
                .setLogger(logger)
                .setDebug(debug)
                .setEnvironmentName(environmentName)
                .execute();
    }
}
