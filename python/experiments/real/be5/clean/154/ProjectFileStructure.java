package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.base.BeModelElementSupport;
import com.developmentontheedge.beans.annot.PropertyDescription;
import com.developmentontheedge.beans.annot.PropertyName;

@PropertyName("Project file structure")
public class ProjectFileStructure extends BeModelElementSupport
{
    public static final String NAME = "File Structure";
    public static final String PROJECT_FILE_NAME_WITHOUT_SUFFIX = "project";
    public static final String FORMAT_SUFFIX = ".yaml";

    private String htmlDir = "src/html";
    private String javaSourcesDir = "src/main/java";

    private String jsDir = "src/js";
    private String jsFormsDir = "src/js/forms";
    private String jsQueriesDir = "src/js/queries";
    private String jsOperationsDir = "src/js/operations";
    private String jsExtendersDir = "src/js/extenders";

    private String groovyQueriesDir = "src/groovy/queries";
    private String groovyOperationsDir = "src/groovy/operations";
    private String groovyExtendersDir = "src/groovy/extenders";

    private String scriptsDir = "src/ftl";
    private String iconsDir = "src/icons";
    private String entitiesDir = "src/meta/entities";
    private String modulesDir = "src/meta/modules";
    private String dataDir = "src/meta/data";
    private String l10nDir = "src/l10n";
    private String securityFile = "src/security.yaml";
    private String pagesFile = "src/pages.yaml";
    private String pagesDir = "src/pages";
    private String daemonsFile = "src/daemons.yaml";
    private String jsFormsFile = "src/forms.yaml";
    private String customizationFile = "src/customization.yaml";
    private String massChangesFile = "src/massChanges.yaml";
    private String localConnectionProfilesFile = "src/connectionProfiles.local.yaml";
    private String remoteConnectionProfilesFile = "src/connectionProfiles.remote.yaml";
    private String macroDir = "src/include";
    private String selectedProfileFile = "src/profile.local";
    private String devFile = "src/dev.yaml";

    public ProjectFileStructure(final Project project)
    {
        super(ProjectFileStructure.NAME, project);
    }

    @Override
    public Project getProject()
    {
        return (Project) getOrigin();
    }

    @PropertyName("Directory for HTML files")
    public String getHtmlDir()
    {
        return htmlDir;
    }

    public void setHtmlDir(final String htmlDir)
    {
        this.htmlDir = htmlDir;
        fireChanged();
    }

    @PropertyName("Directory for Java source")
    public String getJavaSourcesDir()
    {
        return javaSourcesDir;
    }

    public void setJavaSourcesDir(final String javaSourcesDir)
    {
        this.javaSourcesDir = javaSourcesDir;
        fireChanged();
    }

    @PropertyName("Directory for JavaScript source")
    public String getJsDir()
    {
        return jsDir;
    }

    public void setJsDir(final String jsDir)
    {
        this.jsDir = jsDir;
        fireChanged();
    }

    @PropertyName("Directory for JavaScript forms")
    public String getJsFormsDir()
    {
        return jsFormsDir;
    }

    public void setJsFormsDir(final String jsFormsDir)
    {
        this.jsFormsDir = jsFormsDir;
        fireChanged();
    }

    @PropertyName("Directory for JavaScript extenders")
    public String getJsExtendersDir()
    {
        return jsExtendersDir;
    }

    public void setJsExtendersDir(final String jsExtendersDir)
    {
        this.jsExtendersDir = jsExtendersDir;
        fireChanged();
    }

    @PropertyName("Directory for Groovy extenders")
    public String getGroovyExtendersDir()
    {
        return groovyExtendersDir;
    }

    public void setGroovyExtendersDir(final String groovyExtendersDir)
    {
        this.groovyExtendersDir = groovyExtendersDir;
        fireChanged();
    }

    @PropertyName("Directory for JavaScript operations")
    public String getJsOperationsDir()
    {
        return jsOperationsDir;
    }

    public void setJsOperationsDir(final String jsOperationsDir)
    {
        this.jsOperationsDir = jsOperationsDir;
        fireChanged();
    }

    @PropertyName("Directory for Groovy operations")
    public String getGroovyOperationsDir()
    {
        return groovyOperationsDir;
    }

    public void setGroovyOperationsDir(final String groovyOperationsDir)
    {
        this.groovyOperationsDir = groovyOperationsDir;
        fireChanged();
    }

    @PropertyName("Directory for JavaScript queries")
    public String getJsQueriesDir()
    {
        return jsQueriesDir;
    }

    public void setJsQueriesDir(String jsQueriesDir)
    {
        this.jsQueriesDir = jsQueriesDir;
        fireChanged();
    }

    @PropertyName("Directory for Groovy queries")
    public String getGroovyQueriesDir()
    {
        return groovyQueriesDir;
    }

    public void setGroovyQueriesDir(String groovyQueriesDir)
    {
        this.groovyQueriesDir = groovyQueriesDir;
    }

    @PropertyName("Directory for FTL scripts")
    public String getScriptsDir()
    {
        return scriptsDir;
    }

    public void setScriptsDir(final String scriptsDir)
    {
        this.scriptsDir = scriptsDir;
        fireChanged();
    }

    @PropertyName("Directory for files with Freemarker includes")
    public String getMacroDir()
    {
        return macroDir;
    }

    public void setMacroDir(final String macroDir)
    {
        this.macroDir = macroDir;
        fireChanged();
    }

    @PropertyName("Directory for entities")
    public String getEntitiesDir()
    {
        return entitiesDir;
    }

    public void setEntitiesDir(final String entitiesDir)
    {
        this.entitiesDir = entitiesDir;
        fireChanged();
    }

    @PropertyName("Directory for modules")
    public String getModulesDir()
    {
        return modulesDir;
    }

    public void setModulesDir(final String modulesDir)
    {
        this.modulesDir = modulesDir;
        fireChanged();
    }

    @PropertyName("Directory for data files")
    public String getDataDir()
    {
        return dataDir;
    }

    public void setDataDir(final String dataDir)
    {
        this.dataDir = dataDir;
        fireChanged();
    }

    @PropertyName("Local connection profiles yaml file location")
    @PropertyDescription("'.yaml' suffix is mandatory. "
            + "The word 'local' means that these connections will be used by an one user, "
            + "and this file with local connections should be added to '.gitignore.'")
    public String getLocalConnectionProfilesFile()
    {
        return localConnectionProfilesFile;
    }

    public void setLocalConnectionProfilesFile(final String localConnectionProfilesFile)
    {
        this.localConnectionProfilesFile = localConnectionProfilesFile;
        fireChanged();
    }

    @PropertyName("Remote connection profiles yaml file location")
    @PropertyDescription(".yaml suffix is mandatory")
    public String getRemoteConnectionProfilesFile()
    {
        return remoteConnectionProfilesFile;
    }

    public void setRemoteConnectionProfilesFile(final String remoteConnectionProfilesFile)
    {
        this.remoteConnectionProfilesFile = remoteConnectionProfilesFile;
        fireChanged();
    }

    @PropertyName("Directory for icons")
    public String getIconsDir()
    {
        return iconsDir;
    }

    public void setIconsDir(final String iconsDir)
    {
        this.iconsDir = iconsDir;
        fireChanged();
    }

    @PropertyName("Directory for localization data")
    public String getL10nDir()
    {
        return l10nDir;
    }

    public void setL10nDir(final String l10nDir)
    {
        this.l10nDir = l10nDir;
        fireChanged();
    }

    @PropertyName("Security yaml file location")
    @PropertyDescription(".yaml suffix is mandatory")
    public String getSecurityFile()
    {
        return securityFile;
    }

    public void setSecurityFile(final String securityFile)
    {
        this.securityFile = securityFile;
        fireChanged();
    }

    @PropertyName("Daemons yaml file location")
    @PropertyDescription(".yaml suffix is mandatory")
    public String getDaemonsFile()
    {
        return daemonsFile;
    }

    public void setDaemonsFile(final String daemonsFile)
    {
        this.daemonsFile = daemonsFile;
        fireChanged();
    }

    @PropertyName("Application customizations yaml file location")
    @PropertyDescription(".yaml suffix is mandatory")
    public String getCustomizationFile()
    {
        return customizationFile;
    }

    public void setCustomizationFile(final String customizationFile)
    {
        this.customizationFile = customizationFile;
        fireChanged();
    }

    @PropertyName("Static pages file")
    @PropertyDescription(".yaml suffix is mandatory")
    public String getPagesFile()
    {
        return pagesFile;
    }

    public void setPagesFile(String pagesFile)
    {
        this.pagesFile = pagesFile;
        fireChanged();
    }

    @PropertyName("Directory for static pages")
    public String getPagesDir()
    {
        return pagesDir;
    }

    public void setPagesDir(String pagesDir)
    {
        this.pagesDir = pagesDir;
        fireChanged();
    }

    @PropertyName("JavaScript forms file")
    public String getJsFormsFile()
    {
        return jsFormsFile;
    }

    public void setJsFormsFile(String jsFormsFile)
    {
        this.jsFormsFile = jsFormsFile;
        fireChanged();
    }

    @PropertyName("Mass changes file")
    public String getMassChangesFile()
    {
        return massChangesFile;
    }

    public void setMassChangesFile(String massChangesFile)
    {
        this.massChangesFile = massChangesFile;
        fireChanged();
    }

    @PropertyName("File containing selected profile")
    public String getSelectedProfileFile()
    {
        return selectedProfileFile;
    }

    public void setSelectedProfileFile(String selectedProfileFile)
    {
        this.selectedProfileFile = selectedProfileFile;
        fireChanged();
    }

    public String getDevFile()
    {
        return devFile;
    }

    @Override
    protected void fireChanged()
    {
        getProject().fireCodeChanged();
    }

}
