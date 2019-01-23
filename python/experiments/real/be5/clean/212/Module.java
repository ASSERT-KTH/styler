package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.model.base.BeModelElement;
import com.developmentontheedge.be5.metadata.model.base.BeVectorCollection;
import com.developmentontheedge.be5.metadata.util.Strings2;
import com.developmentontheedge.beans.annot.PropertyName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * BeanExplorer module model definition.
 */
@PropertyName("Module")
public class Module extends BeVectorCollection<BeModelElement>
{
    public static final String DAEMONS = "Daemons";
    public static final String LOCALIZATION = "Localization";
    public static final String SCRIPTS = "Scripts";
    public static final String MACROS = "Macros";
    public static final String SOURCES = "Sources";
    public static final String STATIC_PAGES = "Static pages";
    public static final String JS_FORMS = "JavaScript forms";
    public static final String MASS_CHANGES = "Mass changes";

    private String[] extras = Strings2.EMPTY;

    /**
     * Creates project with empty default structure.
     */
    public Module(String name, BeModelCollection<?> parent)
    {
        super(name, BeVectorCollection.class, parent);
        init();
    }

    /**
     * Init internal structure of the project.
     */
    protected void init()
    {
        put(new Daemons(this));
        put(new Localizations(LOCALIZATION, this));
        if (getProject() != null && getName().equals(getProject().getProjectOrigin()))
        {
            put(new FreemarkerCatalog(MACROS, this));
            put(new FreemarkerCatalog(SCRIPTS, this));
            put(new BeVectorCollection<BeModelElement>(SOURCES, SourceFileCollection.class, this).propagateCodeChange());
            put(new StaticPages(this));
            put(new JavaScriptForms(this));
        }
        propagateCodeChange();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Utility methods
    //

    public Localizations getLocalizations()
    {
        return (Localizations) this.get(LOCALIZATION);
    }

    @Override
    public <S extends BeModelElement> BeVectorCollection<S> getOrCreateCollection(String name, Class<S> clazz)
    {
        if (name.equals(PageCustomization.CUSTOMIZATIONS_COLLECTION) && clazz == PageCustomization.class)
        {
            PageCustomizations element = (PageCustomizations) get(name);
            if (element == null)
            {
                element = new PageCustomizations(this);
                DataElementUtils.saveQuiet(element);
            }
            @SuppressWarnings("unchecked") final BeVectorCollection<S> result = (BeVectorCollection<S>) element;
            return result;
        }

        return super.getOrCreateCollection(name, clazz);
    }

    public Entities getOrCreateEntityCollection()
    {
        Entities entities = (Entities) get(Entities.NAME);
        if (entities == null)
        {
            entities = new Entities(this);
            DataElementUtils.saveQuiet(entities);
        }
        return entities;
    }

    public Entities getEntityCollection()
    {
        return (Entities) getCollection(Entities.NAME, Entity.class);
    }

    public PageCustomizations getPageCustomizations()
    {
        return (PageCustomizations) getCollection(PageCustomization.CUSTOMIZATIONS_COLLECTION, PageCustomization.class);
    }

    public Daemons getDaemonCollection()
    {
        return (Daemons) getCollection(DAEMONS, Daemon.class);
    }

    public StaticPages getStaticPageCollection()
    {
        return (StaticPages) getCollection(STATIC_PAGES, LanguageStaticPages.class);
    }

    public JavaScriptForms getJavaScriptFormsCollection()
    {
        JavaScriptForms element = (JavaScriptForms) get(Module.JS_FORMS);
        if (element == null)
        {
            element = new JavaScriptForms(this);
            DataElementUtils.saveQuiet(element);
        }
        return element;
    }

    public FreemarkerCatalog getMacroCollection()
    {
        return (FreemarkerCatalog) this.get(MACROS);
    }

    public FreemarkerCatalog getFreemarkerScripts()
    {
        return (FreemarkerCatalog) this.get(SCRIPTS);
    }

    public MassChanges getMassChangeCollection()
    {
        String name = Module.MASS_CHANGES;
        MassChanges element = (MassChanges) get(name);

        if (element == null)
        {
            element = newMassChangeCollection();
            DataElementUtils.saveQuiet(element);
        }

        return element;
    }

    public MassChanges newMassChangeCollection()
    {
        return new MassChanges(this);
    }

    @SuppressWarnings("unchecked")
    public BeModelCollection<SourceFileCollection> getSourceFiles()
    {
        return (BeModelCollection<SourceFileCollection>) this.get(SOURCES);
    }

    public SourceFile addSourceFile(String nameSpace, String name)
    {
        return addSourceFileInternal(nameSpace, name);
    }

    public SourceFile addSourceFile(String nameSpace, String name, String content)
    {
        SourceFile sourceFile = addSourceFileInternal(nameSpace, name);
        sourceFile.setSource(content);
        return sourceFile;
    }

    private SourceFile addSourceFileInternal(String nameSpace, String name)
    {
        BeModelCollection<SourceFileCollection> sourceFiles = getSourceFiles();
        SourceFileCollection dc = sourceFiles.get(nameSpace);
        if (dc == null)
        {
            dc = new SourceFileCollection(nameSpace, sourceFiles);
            DataElementUtils.saveQuiet(dc);
        }
        SourceFile sourceFile = dc.get(name);
        if (sourceFile == null)
        {
            sourceFile = new SourceFile(name, dc);
            DataElementUtils.saveQuiet(sourceFile);
        }
        return sourceFile;
    }

    public SourceFile getSourceFile(String nameSpace, String name)
    {
        BeModelCollection<SourceFileCollection> sourceFiles = getSourceFiles();
        if (sourceFiles == null)
            return null;
        SourceFileCollection dc = sourceFiles.get(nameSpace);
        if (dc == null)
            return null;
        return dc.get(name);
    }

    public Set<String> getEntityNames()
    {
        BeModelCollection<Entity> dc = getEntityCollection();
        if (dc != null)
        {
            return dc.names().toSet();
        }
        return Collections.emptySet();
    }

    public Entity getEntity(String name)
    {
        Entity entity = null;
        BeModelCollection<Entity> dc = getEntityCollection();
        if (dc != null)
        {
            entity = dc.get(name);
            if (entity != null)
            {
                return entity;
            }
        }
        return null;
    }

    public Iterable<Entity> getEntities()
    {
        final BeModelCollection<Entity> entityCollection = getEntityCollection();

        if (entityCollection == null)
            return Collections.emptyList();

        return entityCollection;
    }

    public String[] getExtras()
    {
        return extras;
    }

    public void setExtras(String[] extras)
    {
        this.extras = extras;
    }

    public boolean hasExtra(String extra)
    {
        if (extras == null || extra == null)
            return false;

        for (int i = 0; i < extras.length; i++)
        {
            if (extra.equals(extras[i]))
                return true;
        }

        return false;
    }

    @Override
    public Module getModule()
    {
        return this;
    }

    /**
     * Returns all references, including entity references and columns.
     */
    public List<TableRef> findTableReferences()
    {
        final List<TableRef> tableReferences = new ArrayList<>();

        for (final Entity entity : getEntities())
        {
            final List<TableReference> allReferences = entity.getAllReferences();
            for (TableReference reference : allReferences)
            {
                if (!(reference instanceof TableRef))
                    throw new AssertionError();
                tableReferences.add((TableRef) reference);
            }
        }

        return tableReferences;
    }

    public List<TableDef> findTableDefinitions()
    {
        final List<TableDef> tableDefs = new ArrayList<>();

        for (Entity entity : getModule().getEntities())
        {
            final TableDef scheme = entity.findTableDefinition();
            if (scheme != null)
                tableDefs.add(scheme);
        }

        return tableDefs;
    }

}
