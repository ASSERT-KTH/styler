package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.model.base.BeModelElement;
import com.developmentontheedge.be5.metadata.model.base.BeVectorCollection;
import com.developmentontheedge.be5.metadata.model.base.DataElementPath;
import com.developmentontheedge.beans.annot.PropertyName;

import java.util.ArrayList;
import java.util.List;

@PropertyName("Freemarker Scripts")
public class FreemarkerCatalog extends BeVectorCollection<FreemarkerScriptOrCatalog> implements FreemarkerScriptOrCatalog
{
    public static final String MAIN_MACRO_LIBRARY = "common";

    public static final String PRE_DB_STEP = "Pre-db";
    public static final String POST_DB_STEP = "Post-db";
    public static final String PRE_META_STEP = "Pre-meta";
    public static final String POST_META_STEP = "Post-meta";
    public static final String POST_META_APP_STEP = "Post-app-meta";
    public static final String PRE_LOCALE_STEP = "Pre-locale";
    public static final String POST_LOCALE_STEP = "Post-locale";
    public static final String DATA = "data";

    public FreemarkerCatalog(String name, BeModelCollection<?> parent)
    {
        super(name, FreemarkerScriptOrCatalog.class, parent);
        propagateCodeChange();
    }

    public static String[] getPredefinedStepNames()
    {
        return new String[]{PRE_DB_STEP, POST_DB_STEP, PRE_META_STEP, POST_META_STEP, PRE_LOCALE_STEP, POST_LOCALE_STEP, DATA};
    }

    public FreemarkerScript optScript(String relativePath)
    {
        FreemarkerScriptOrCatalog element = this;
        for (String component : DataElementPath.create(relativePath).getPathComponents())
        {
            if (!(element instanceof FreemarkerCatalog))
            {
                return null;
            }
            element = ((FreemarkerCatalog) element).get(component);
            if (element == null)
            {
                return null;
            }
        }
        if (element instanceof FreemarkerScript)
        {
            return (FreemarkerScript) element;
        }
        return null;
    }

    /**
     * Collects scripts recursively.
     */
    public List<FreemarkerScript> getScripts()
    {
        List<FreemarkerScript> result = new ArrayList<>();
        for (FreemarkerScriptOrCatalog scriptOrCatalog : this)
        {
            if (scriptOrCatalog instanceof FreemarkerScript)
            {
                result.add((FreemarkerScript) scriptOrCatalog);
            }
            else
            {
                result.addAll(((FreemarkerCatalog) scriptOrCatalog).getScripts());
            }
        }
        return result;
    }

    @Override
    protected void fireElementAdded(Object source, String dataElementName)
    {
        super.fireElementAdded(source, dataElementName);
        final FreemarkerScriptOrCatalog addedElement = get(dataElementName);
        if (addedElement instanceof FreemarkerScript)
            getProject().getAutomaticSerializationService().fireCodeAdded((FreemarkerScript) addedElement);
    }

    @Override
    protected void fireElementRemoved(Object source, String dataElementName, BeModelElement oldElement)
    {
        super.fireElementRemoved(source, dataElementName, oldElement);
        if (oldElement instanceof FreemarkerScript)
            getProject().getAutomaticSerializationService().fireCodeRemoved((FreemarkerScript) oldElement);
    }
}
