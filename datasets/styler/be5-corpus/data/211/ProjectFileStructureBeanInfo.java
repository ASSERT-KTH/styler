package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.beans.BeanInfoEx;

public class ProjectFileStructureBeanInfo extends BeanInfoEx
{
    public ProjectFileStructureBeanInfo()
    {
        super(ProjectFileStructure.class);
    }

    @Override
    protected void initProperties() throws Exception
    {
        add("htmlDir");
        add("javaSourcesDir");
        add("jsDir");
        add("jsFormsDir");
        add("jsOperationsDir");
        add("jsExtendersDir");
        add("groovyOperationsDir");
        add("scriptsDir");
        add("iconsDir");
        add("l10nDir");
        add("entitiesDir");
        add("modulesDir");
        add("dataDir");
        add("macroDir");
        add("securityFile");
        add("daemonsFile");
        add("customizationFile");
        add("massChangesFile");
        add("pagesFile");
        add("pagesDir");
        add("jsFormsFile");
        add("selectedProfileFile");
    }
}
