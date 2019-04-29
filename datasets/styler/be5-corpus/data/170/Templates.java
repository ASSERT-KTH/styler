package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.exception.ReadException;
import com.developmentontheedge.be5.metadata.serialization.LoadContext;
import com.developmentontheedge.be5.metadata.serialization.Serialization;
import com.developmentontheedge.be5.metadata.serialization.yaml.deserializers.YamlDeserializer;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.MarkedYAMLException;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Node;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Templates
{
    public static final String TEMPLATES_PROJECT_NAME = "templates";
    private static final List<String> TEMPLATES = Collections.unmodifiableList(Arrays.asList("dictionary"));

    public static Project getTemplatesProject() throws ReadException
    {
        Project prj = new Project(TEMPLATES_PROJECT_NAME, true);
        LoadContext lc = new LoadContext();
        for (String template : TEMPLATES)
        {
            URL url = Templates.class.getResource("templates/" + template + ".yaml");
            Node content;
            try (InputStream is = url.openStream())
            {
                content = new Yaml().compose(new InputStreamReader(is, StandardCharsets.UTF_8));
            }
            catch (MarkedYAMLException e)
            {
                throw new ReadException(
                        new Exception((e.getProblemMark().getLine() + 1) + ":" + (e.getProblemMark().getColumn() + 1) + ": "
                                + e.getMessage()), getPath(url), ReadException.LEE_INVALID_STRUCTURE);
            }
            catch (YAMLException | IOException e)
            {
                throw new ReadException(new Exception(e.getMessage()), getPath(url), ReadException.LEE_INVALID_STRUCTURE);
            }
            try
            {
                Object obj = Serialization.derepresent(content);
                @SuppressWarnings("unchecked")
                Map<String, Object> root = (Map<String, Object>) obj;
                @SuppressWarnings("unchecked")
                Map<String, Object> entityContent = (Map<String, Object>) root.get(template);
                DataElementUtils.saveQuiet(YamlDeserializer.readEntity(lc, template, entityContent, prj.getApplication()));
            }
            catch (RuntimeException e)
            {
                throw new ReadException(e, getPath(url), ReadException.LEE_INTERNAL_ERROR);
            }
            lc.check();
        }
        return prj;
    }

    protected static Path getPath(URL url)
    {
        try
        {
            return Paths.get(url.toURI());
        }
        catch (URISyntaxException | FileSystemNotFoundException e)
        {
            return Paths.get("internal", url.getPath());
        }
    }

    public static List<String> getTemplateNames()
    {
        return TEMPLATES;
    }
}
