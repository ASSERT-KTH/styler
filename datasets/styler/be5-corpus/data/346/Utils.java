package com.developmentontheedge.be5.metadata.scripts.generate;

import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.Map;

public class Utils
{
    public static void createFile(String fileName, Template template, Map<String, Object> input)
    {
        try
        {
            Paths.get(fileName.substring(0, fileName.lastIndexOf("/"))).toFile().mkdirs();
            Writer fileWriter = new FileWriter(new File(fileName));

            try
            {
                template.process(input, fileWriter);
            }
            catch (TemplateException e)
            {
                e.printStackTrace();
            }
            finally
            {
                fileWriter.close();
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
