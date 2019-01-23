package com.developmentontheedge.be5.maven;

import com.developmentontheedge.be5.metadata.util.ProcessController;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.nio.file.Paths;


public abstract class Be5Mojo extends AbstractMojo
{
    protected ProcessController logger = new MavenLogger(getLog());

    @Parameter(property = "BE5_PROJECT_PATH", defaultValue = "./")
    public File projectPath;

    @Parameter(property = "BE5_UNLOCK_PROTECTED_PROFILE")
    protected boolean unlockProtectedProfile = false;

    @Parameter(property = "BE5_DEBUG")
    protected boolean debug = false;

    @Parameter(property = "BE5_LOG_PATH")
    protected File logPath = Paths.get("target/sql").toFile();

    @Parameter(property = "BE5_PROFILE")
    public String connectionProfileName;

    @Parameter(property = "DB_PASSWORD")
    protected String connectionPassword;
}
