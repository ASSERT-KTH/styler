package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.EntityLocalizations.LocalizationRow;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Projects
{

    /**
     * This class is not intended to be instantiated.
     */
    private Projects()
    {
        throw new AssertionError();
    }

    /**
     * Returns all localizations with the given entity, key and topic.
     *
     * @param project
     * @param entity
     * @param key     can be null
     * @param topic
     * @return
     */
    public static List<String> searchLocalizations(final Project project, final String entity, final String key, final String topic)
    {
        final List<String> values = new ArrayList<>();

        for (final Module module : project.allModules())
        {
            final Localizations localizations = module.getLocalizations();
            for (final LanguageLocalizations languageLocalizations : localizations)
            {
                final EntityLocalizations entityLocalizations = languageLocalizations.get(entity);
                if (entityLocalizations != null)
                {
                    final Set<LocalizationRow> rows = entityLocalizations.getRawRows();
                    for (final LocalizationRow row : rows)
                        if (key == null || row.getKey().equals(key) && row.getTopic().equals(topic))
                            values.add(row.getValue());
                }
            }
        }

        return values;
    }

    public static FreemarkerScript searchFreemarkerScript(final Project project, final Path path)
    {
        for (final Module module : project.allModules())
        {
            final FreemarkerCatalog scripts = module.getFreemarkerScripts();
            final FreemarkerScript script = Projects.searchIn(scripts, path);

            if (script != null)
                return script;

            final FreemarkerCatalog macros = module.getMacroCollection();
            final FreemarkerScript macro = Projects.searchIn(macros, path);

            if (macro != null)
                return macro;
        }

        return null;
    }

    /**
     * @param scripts can be null
     * @param path
     * @return found script or null
     */
    private static FreemarkerScript searchIn(final FreemarkerCatalog scripts, Path path)
    {
        if (scripts == null)
            return null;

        for (FreemarkerScriptOrCatalog scriptOrCatalog : scripts)
        {
            if (scriptOrCatalog instanceof FreemarkerScript)
            {
                FreemarkerScript freemarkerScript = (FreemarkerScript) scriptOrCatalog;
                if (freemarkerScript.getLinkedFile() != null && freemarkerScript.getLinkedFile().equals(path))
                    return freemarkerScript;
            }
            else
            {
                FreemarkerScript freemarkerScript = searchIn((FreemarkerCatalog) scriptOrCatalog, path);
                if (freemarkerScript != null)
                    return freemarkerScript;
            }
        }

        return null;
    }

}
