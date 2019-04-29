package com.developmentontheedge.be5.metadata.scripts.generate;

import com.developmentontheedge.be5.metadata.exception.ProjectLoadException;
import com.developmentontheedge.be5.metadata.model.Entity;
import com.developmentontheedge.be5.metadata.scripts.ScriptSupport;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


public class GroovyDSLGenerator extends ScriptSupport<GroovyDSLGenerator>
{
    private static final Logger log = Logger.getLogger(GroovyDSLGenerator.class.getName());

    private int entityCount = 0;

    protected String fileName;

    @Override
    public void execute()
    {
        try
        {
            generate(fileName.replace(".", "/") + "GroovyDSL");
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void generate(String fileName) throws IOException
    {
        Configuration cfg = new Configuration();
        cfg.setClassForTemplateLoading(GroovyDSLGenerator.class, "/gdsl");
        cfg.setDefaultEncoding("UTF-8");

        File file = Paths.get(fileName + ".gdsl").toFile();

        if (file.exists() && !file.isDirectory())
        {
            log.info("Generate skipped, file exists: " + fileName);
            return;
        }

        log.info("File '" + file.toString() + "' not found, generate...");

        try
        {
            createDSL(fileName, cfg);
        }
        catch (ProjectLoadException e)
        {
            e.printStackTrace();
        }

        log.info("Generate successful: " + entityCount + " entities added.\n" + file.getAbsolutePath());
    }

    private void createDSL(String fileName, Configuration cfg) throws IOException, ProjectLoadException
    {
        Template serviceTpl = cfg.getTemplate("/entities.ftl");

        initProject();

        List<Entity> entities = be5Project.getAllEntities();

        Map<String, Object> input = new HashMap<>();
//        input.put("serviceClassName", serviceClassName);
//        input.put("packageName", packageName);

        List<String> entityNames = new ArrayList<>();
        entityCount = entities.size();
        for (Entity entity : entities)
        {
            if (entity.getName().startsWith("_")) continue;
            if (entity.getName().equals("properties")) continue; //groovy have getProperties()
            entityNames.add(entity.getName());
        }
        input.put("entityNames", entityNames);
        Utils.createFile(fileName + ".gdsl", serviceTpl, input);
    }

    public GroovyDSLGenerator setFileName(String fileName)
    {
        this.fileName = fileName;
        return this;
    }

    @Override
    public GroovyDSLGenerator me()
    {
        return this;
    }
}
