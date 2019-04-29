package com.developmentontheedge.be5.metadata.serialization.yaml.deserializers;

import com.developmentontheedge.be5.metadata.exception.ReadException;
import com.developmentontheedge.be5.metadata.model.DataElementUtils;
import com.developmentontheedge.be5.metadata.model.LanguageStaticPages;
import com.developmentontheedge.be5.metadata.model.ManagedFileType;
import com.developmentontheedge.be5.metadata.model.StaticPage;
import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.serialization.LoadContext;
import com.developmentontheedge.be5.metadata.serialization.SerializationConstants;

import java.nio.file.Path;
import java.util.Map;

public class StaticPagesDeserializer extends FileDeserializer
{
    private YamlDeserializer yamlDeserializer;
    private final BeModelCollection<LanguageStaticPages> target;

    StaticPagesDeserializer(YamlDeserializer yamlDeserializer, LoadContext loadContext, final Path path, final BeModelCollection<LanguageStaticPages> target) throws ReadException
    {
        super(loadContext, path, true);
        this.yamlDeserializer = yamlDeserializer;
        this.target = target;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doDeserialize(Object serializedRoot)
    {
        if (!(serializedRoot instanceof Map))
            return;

        final Map<String, Object> root = (Map<String, Object>) serializedRoot;
        final Map<String, Object> pagesByLanguage = (Map<String, Object>) root.get("pages");

        if (pagesByLanguage == null)
            return;

        for (final String language : pagesByLanguage.keySet())
        {
            final LanguageStaticPages langPages = new LanguageStaticPages(language, target);
            final Map<String, Object> serializedPages = (Map<String, Object>) pagesByLanguage.get(language);

            for (final String pageName : serializedPages.keySet())
            {
                final Object serializedContent = serializedPages.get(pageName);
                final StaticPage page = new StaticPage(pageName, langPages);
                final String content;

                if (serializedContent instanceof String)
                {
                    content = (String) serializedContent;
                }
                else if (serializedContent instanceof Map)
                {
                    // a. file: <fileName>
                    // b. code: file: <fileName>
                    //    customizations: <map>
                    // c. code: <content>
                    //    customizations: <map>

                    final Map<String, Object> mapPageContent = (Map<String, Object>) serializedContent;
                    if (mapPageContent.containsKey("file"))
                    {
                        final String fileName = (String) mapPageContent.get("file");
                        page.setFileName(fileName);
                        content = readStaticPageFileContent(fileName);
                    }
                    else
                    {
                        final Object codeObj = mapPageContent.get(SerializationConstants.TAG_CODE);

                        if (codeObj instanceof String)
                            content = (String) codeObj;
                        else if (codeObj instanceof Map)
                        {
                            final String fileName = ((Map<String, String>) codeObj).get("file");
                            page.setFileName(fileName);
                            content = readStaticPageFileContent(fileName);
                        }
                        else
                            content = "";

                        yamlDeserializer.readCustomizations(mapPageContent, page, false);
                    }
                }
                else
                {
                    content = "";
                }

                page.setContent(content);
                DataElementUtils.saveQuiet(page);
            }


            DataElementUtils.saveQuiet(langPages);
        }

        target.getProject().getAutomaticDeserializationService().registerFile(path, ManagedFileType.PAGES);
    }

    private String readStaticPageFileContent(final String fileName)
    {
        try
        {
            return yamlDeserializer.getFileSystem().readStaticPageFileContent(fileName);
        }
        catch (final ReadException e)
        {
            loadContext.addWarning(e);
        }

        return "";
    }

}
