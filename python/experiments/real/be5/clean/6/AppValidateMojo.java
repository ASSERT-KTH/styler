package com.developmentontheedge.be5.maven;

import com.developmentontheedge.be5.metadata.scripts.AppValidate;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Usage example:
 * mvn be5:validate -DBE5_DEBUG=true
 */
@Mojo(name = "validate")
public class AppValidateMojo extends Be5Mojo
{
    @Parameter(property = "BE5_RDBMS")
    private String rdbmsName;

    @Parameter(property = "BE5_SKIP_VALIDATION")
    private boolean skipValidation = false;

    @Parameter(property = "BE5_CHECK_QUERY")
    private String queryPath;

    @Parameter(property = "BE5_CHECK_ROLES")
    private boolean checkRoles;

    @Parameter(property = "BE5_CHECK_DDL")
    private String ddlPath;

    @Parameter(property = "BE5_SAVE_PROJECT")
    private boolean saveProject;

    @Override
    public void execute()
    {
        new AppValidate()
                .setLogPath(logPath)
                .setLogger(logger)
                .setDebug(debug)
                .setBe5ProjectPath(projectPath.toPath())
                .setCheckQueryPath(queryPath)
                .setDdlPath(ddlPath)
                .setCheckRoles(checkRoles)
                .setRdbmsName(rdbmsName)
                .setSkipValidation(skipValidation)
                .setSaveProject(saveProject)
                .execute();
    }
}
