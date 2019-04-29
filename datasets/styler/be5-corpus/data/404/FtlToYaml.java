package com.developmentontheedge.be5.metadata.freemarker;

import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Class to convert Freemarker objects to Yaml elements
 *
 * @author lan
 */
public class FtlToYaml
{
    public static Object ftlToObject(TemplateModel model) throws TemplateModelException
    {
        if (model instanceof TemplateBooleanModel)
        {
            return ((TemplateBooleanModel) model).getAsBoolean();
        }
        if (model instanceof TemplateScalarModel)
        {
            return ((TemplateScalarModel) model).getAsString();
        }
        if (model instanceof TemplateHashModelEx)
        {
            return ftlToHash((TemplateHashModelEx) model);
        }
        if (model instanceof TemplateNumberModel)
        {
            return ((TemplateNumberModel) model).getAsNumber();
        }
        if (model instanceof TemplateSequenceModel)
        {
            return ftlToArray((TemplateSequenceModel) model);
        }
        throw new TemplateModelException("Unsupported value: " + model);
    }

    public static ArrayList<Object> ftlToArray(TemplateSequenceModel model) throws TemplateModelException
    {
        ArrayList<Object> result = new ArrayList<>();
        int length = model.size();
        for (int i = 0; i < length; i++)
        {
            try
            {
                result.add(ftlToObject(model.get(i)));
            }
            catch (TemplateModelException e)
            {
                throw new TemplateModelException("[" + i + "]: " + e.getMessage(), e);
            }
        }
        return result;
    }

    public static LinkedHashMap<String, Object> ftlToHash(TemplateHashModelEx model) throws TemplateModelException
    {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        TemplateCollectionModel keys = model.keys();
        TemplateModelIterator iterator = keys.iterator();
        while (iterator.hasNext())
        {
            TemplateModel next = iterator.next();
            if (!(next instanceof TemplateScalarModel))
            {
                throw new TemplateModelException("Invalid key: " + next);
            }
            String key = ((TemplateScalarModel) next).getAsString();
            TemplateModel value = model.get(key);
            try
            {
                result.put(key, ftlToObject(value));
            }
            catch (TemplateModelException e)
            {
                throw new TemplateModelException(key + ": " + e.getMessage(), e);
            }
        }
        return result;
    }

}
