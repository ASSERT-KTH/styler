package com.developmentontheedge.be5.metadata.freemarker;

import com.developmentontheedge.be5.metadata.exception.ProjectElementException;
import com.developmentontheedge.be5.metadata.model.FreemarkerCatalog;
import com.developmentontheedge.be5.metadata.model.FreemarkerScript;
import com.developmentontheedge.be5.metadata.model.Module;
import com.developmentontheedge.be5.metadata.model.Project;
import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.model.base.BeModelElement;
import com.developmentontheedge.be5.metadata.model.base.DataElementPath;
import com.developmentontheedge.be5.metadata.model.base.TemplateElement;
import freemarker.cache.NullCacheStorage;
import freemarker.cache.SoftCacheStorage;
import freemarker.cache.TemplateLoader;
import freemarker.core.BuiltIn;
import freemarker.core.ParseException;
import freemarker.log.Logger;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.Map;

public final class FreemarkerUtils
{
    private static Configuration configTemplate;

    public static final String NULL = "\0"; // Special object to designate null

    static
    {
        try
        {
            Logger.selectLoggerLibrary(Logger.LIBRARY_NONE);
        }
        catch (ClassNotFoundException e)
        {
            // Ignore
        }
        BuiltIn.addBuiltIn("str", new Be4BuiltIns.Str());
        BuiltIn.addBuiltIn("asVarchar", new Be4BuiltIns.AsVarchar());
        BuiltIn.addBuiltIn("asDate", new Be4BuiltIns.AsDate());
        BuiltIn.addBuiltIn("asInt", new Be4BuiltIns.AsInt());
        BuiltIn.addBuiltIn("asPK", new Be4BuiltIns.AsPrimaryKey());
        BuiltIn.addBuiltIn("roles", new Be4BuiltIns.Roles());
        BuiltIn.addBuiltIn("orEmpty", new Be4BuiltIns.OrEmpty());
        BuiltIn.addBuiltIn("quote", new Be4BuiltIns.Quote());
        BuiltIn.addBuiltIn("dateFormat", new Be4BuiltIns.DateFormat());
        BuiltIn.addBuiltIn("dateFormatRus", new Be4BuiltIns.AsRusDate());
        BuiltIn.addBuiltIn("dateTimeFormat", new Be4BuiltIns.DateTimeFormat());
        BuiltIn.addBuiltIn("idCase", new Be4BuiltIns.IdCase());
        BuiltIn.addBuiltIn("upperFirst", new Be4BuiltIns.UpperFirst());
        BuiltIn.addBuiltIn("upper", new Be4BuiltIns.Upper());
        BuiltIn.addBuiltIn("lower", new Be4BuiltIns.Lower());
        BuiltIn.addBuiltIn("year", new Be4BuiltIns.Year());
        BuiltIn.addBuiltIn("month", new Be4BuiltIns.Month());
        BuiltIn.addBuiltIn("day", new Be4BuiltIns.Day());
        BuiltIn.addBuiltIn("hour", new Be4BuiltIns.Hour());
        BuiltIn.addBuiltIn("minute", new Be4BuiltIns.Minute());
        BuiltIn.addBuiltIn("yearStart", new Be4BuiltIns.YearStart());
        BuiltIn.addBuiltIn("monthStart", new Be4BuiltIns.MonthStart());
        BuiltIn.addBuiltIn("trim", new Be4BuiltIns.Trim());

        configTemplate = new Configuration();
        try
        {
            configTemplate.setSharedVariable("null", NULL);
        }
        catch (TemplateModelException e)
        {
            // Ignore
        }
        configTemplate.setSharedVariable("dayDiff", new DatabaseFunctions.DayDiff());
        configTemplate.setSharedVariable("addMillis", new DatabaseFunctions.AddMillis());
        configTemplate.setSharedVariable("addDays", new DatabaseFunctions.AddDays());
        configTemplate.setSharedVariable("addMonths", new DatabaseFunctions.AddMonths());
        configTemplate.setSharedVariable("chr", new DatabaseFunctions.Chr());
        configTemplate.setSharedVariable("concat", new DatabaseFunctions.Concat());
        configTemplate.setSharedVariable("round", new DatabaseFunctions.Round());
        configTemplate.setSharedVariable("coalesce", new DatabaseFunctions.Coalesce());
        configTemplate.setSharedVariable("genericRef", new DatabaseFunctions.GenericRef());
        configTemplate.setSharedVariable("if", new DatabaseFunctions.If());
        configTemplate.setSharedVariable("indexOf", new DatabaseFunctions.IndexOf());
        configTemplate.setSharedVariable("joinGenericRef", new DatabaseFunctions.JoinGenericRef());
        configTemplate.setSharedVariable("lpad", new DatabaseFunctions.Lpad());
        configTemplate.setSharedVariable("length", new DatabaseFunctions.Length());
        configTemplate.setSharedVariable("limit", new DatabaseFunctions.Limit());
        configTemplate.setSharedVariable("replace", new DatabaseFunctions.Replace());
        configTemplate.setSharedVariable("substring", new DatabaseFunctions.Substring());

        configTemplate.setSharedVariable("columnDef", new DatabaseFunctions.ColumnDefFunction());
        configTemplate.setSharedVariable("tableDef", new DatabaseFunctions.TableDefFunction());

        configTemplate.setLocalizedLookup(false);
        configTemplate.setTemplateUpdateDelay(0);
//        configTemplate.addAutoInclude( "beanexplorer4/common.ftl" ); //TODO
        configTemplate.setCacheStorage(new NullCacheStorage());
    }

    public static Configuration getConfiguration(Project project)
    {
        Configuration config = (Configuration) configTemplate.clone();
        config.setCacheStorage(new SoftCacheStorage());
        try
        {
            config.setSharedVariable("project", project);
        }
        catch (TemplateModelException e)
        {
            throw new RuntimeException("Unexpected error: " + e, e);
        }
        FreemarkerScript macroCollection = project.getMacroCollection().optScript(FreemarkerCatalog.MAIN_MACRO_LIBRARY);
        if (macroCollection != null)
        {
            config.addAutoInclude(macroCollection.getCompletePath().toString());
        }
        for (Module module : project.getModules())
        {
            FreemarkerCatalog collection = module.getMacroCollection();
            if (collection != null)
            {
                FreemarkerScript script = collection.optScript(FreemarkerCatalog.MAIN_MACRO_LIBRARY);
                if (script != null)
                {
                    config.addAutoInclude(script.getCompletePath().toString());
                }
            }
        }
        config.setTemplateLoader(new ProjectTemplateLoader(project));
        return config;
    }

    /**
     * Wrapper is necessary to prevent Freemarker from calling equals on our objects
     *
     * @author lan
     */
    static class Wrapper
    {
        TemplateElement templateSource;

        public Wrapper(TemplateElement templateSource)
        {
            this.templateSource = templateSource;
        }

        @Override
        public boolean equals(Object obj)
        {
            return true;
        }
    }

    public static class ProjectTemplateLoader implements TemplateLoader
    {
        private static final String OPTIONAL_SUFFIX = ":optional";
        private static final DataElementPath SYSTEM_PATH = DataElementPath.create("beanexplorer4");
        private static final Object STUB_ELEMENT = new Object();
        private final Project project;

        public ProjectTemplateLoader(Project project)
        {
            this.project = project;
        }

        @Override
        public Object findTemplateSource(String name) throws IOException
        {
            boolean optional = false;
            if (name.endsWith(OPTIONAL_SUFFIX))
            {
                name = name.substring(0, name.length() - OPTIONAL_SUFFIX.length());
                optional = true;
            }
            DataElementPath path = DataElementPath.create(name);
            if (path.isDescendantOf(project.getCompletePath()))
            {
                BeModelElement element = null;
                for (String component : path.getPathComponents())
                {
                    if (element == null)
                    {
                        element = project;
                    }
                    else
                    {
                        if (!(element instanceof BeModelCollection))
                        {
                            return optional ? STUB_ELEMENT : null;
                        }
                        element = ((BeModelCollection<?>) element).get(component);
                        if (element == null)
                        {
                            return optional ? STUB_ELEMENT : null;
                        }
                    }
                }
                if (element instanceof TemplateElement)
                {
                    return new Wrapper((TemplateElement) element);
                }
                if (optional)
                    return STUB_ELEMENT;
                throw new IOException(name + " cannot be interpreted as Freemarker template");
            }
            if (path.isDescendantOf(SYSTEM_PATH))
            {
                return path;
            }
            return optional ? STUB_ELEMENT : null;
        }

        @Override
        public long getLastModified(Object templateSource)
        {
            if (templateSource instanceof Wrapper)
            {
                return ((Wrapper) templateSource).templateSource.getLastModified();
            }
            return 0;
        }

        @Override
        public Reader getReader(Object templateSource, String encoding) throws IOException
        {
            if (templateSource instanceof Wrapper)
            {
                return new StringReader(((Wrapper) templateSource).templateSource.getTemplateCode());
            }

            // TODO - be5 has not special directory foe macros
            /**
             if(templateSource instanceof DataElementPath)
             {
             DataElementPath path = (DataElementPath)templateSource;
             try
             {
             return Files.newBufferedReader( ModuleUtils.getBasePath().resolve( "macro" ).resolve( path.getName() ) );
             }
             catch ( UnsupportedOperationException e )
             {
             // ignore: no system macros available
             }
             }*/

            return new StringReader("");
        }

        @Override
        public void closeTemplateSource(Object templateSource) throws IOException
        {
            // nothing
        }
    }

    /**
     * Special StringWriter which skips '\r' symbols
     *
     * @author lan
     */
    public static class ResultWriter extends Writer
    {
        StringBuilder sb = new StringBuilder();

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException
        {
            char[] filteredBuf = null;
            int skipped = 0;
            for (int i = 0; i < len; i++)
            {
                if (cbuf[i + off] == '\r')
                {
                    skipped++;
                    if (filteredBuf == null)
                    {
                        filteredBuf = new char[len];
                        System.arraycopy(cbuf, off, filteredBuf, 0, i);
                    }
                }
                else if (filteredBuf != null)
                {
                    filteredBuf[i - skipped] = cbuf[i + off];
                }
            }
            if (filteredBuf != null)
            {
                sb.append(filteredBuf, 0, len - skipped);
            }
            else
            {
                sb.append(cbuf, off, len);
            }
        }

        @Override
        public void flush() throws IOException
        {
        }

        @Override
        public void close() throws IOException
        {
        }

        @Override
        public String toString()
        {
            return sb.toString();
        }
    }

    public static ProjectElementException translateException(String templateName, Exception ex)
    {
        Exception result;
        int row = 1;
        int column = 1;
        if (ex instanceof TemplateException)
        {
            String[] stack = ((TemplateException) ex).getFTLInstructionStack().split("\n");
            Throwable cause = ex.getCause();
            result = new Exception(((TemplateException) ex).getMessageWithoutStackTop());
            if (cause != null)
            {
                result = new Exception(cause.getMessage());
            }
            for (String stackItem : stack)
            {
                try
                {
                    stackItem = stackItem.trim();
                    int openBracketPos = stackItem.lastIndexOf("[in template");
                    if (openBracketPos <= 0)
                        continue;
                    int nameStartPos = stackItem.indexOf('"', openBracketPos);
                    if (nameStartPos <= 0)
                        continue;
                    int nameEndPos = stackItem.indexOf('"', nameStartPos + 1);
                    if (nameEndPos <= 0)
                        continue;
                    // TODO: make parsing more robust
                    String name = stackItem.substring(nameStartPos + 1, nameEndPos);
                    int linePos = stackItem.lastIndexOf("line");
                    if (linePos <= 0)
                        continue;
                    linePos += "line".length() + 1;
                    int lineEndPos = stackItem.indexOf(',', linePos);
                    if (lineEndPos <= 0)
                        continue;
                    row = Integer.parseInt(stackItem.substring(linePos, lineEndPos));
                    int columnPos = stackItem.lastIndexOf("column");
                    if (columnPos <= 0)
                        continue;
                    columnPos += "column".length() + 1;
                    int columnEndPos = stackItem.indexOf(']', columnPos);
                    if (columnEndPos <= 0)
                        continue;
                    column = Integer.parseInt(stackItem.substring(columnPos, columnEndPos));
                    String property = null;
                    int propertyPos = name.lastIndexOf(':');
                    if (propertyPos > 0)
                    {
                        property = name.substring(propertyPos + 1);
                        name = name.substring(0, propertyPos);
                    }
                    result = new ProjectElementException(DataElementPath.create(name), property, row, column, result);
                }
                catch (NumberFormatException e)
                {
                    // Ignore
                }
            }
            if (result instanceof ProjectElementException)
            {
                return (ProjectElementException) result;
            }
        }
        else if (ex instanceof ParseException)
        {
            result = new Exception(ex.getMessage());
            row = ((ParseException) ex).getLineNumber();
            column = ((ParseException) ex).getColumnNumber();
            templateName = ((ParseException) ex).getTemplateName();
        }
        else
        {
            result = new Exception(ex.getMessage(), ex.getCause());
        }
        String name = templateName;
        String property = null;
        int propertyPos = name.lastIndexOf(':');
        if (propertyPos > 0)
        {
            property = name.substring(propertyPos + 1);
            name = name.substring(0, propertyPos);
        }
        return new ProjectElementException(DataElementPath.create(name), property, row, column, result);
    }

    public static String mergeTemplate(String name, String code, Map<String, Object> context, Configuration configuration) throws ProjectElementException
    {
        try
        {
            if (code.equals(" "))
                return code;
            Template template = new Template(name, code, configuration);
            ResultWriter out = new ResultWriter();
            template.process(context, out);
            return out.toString();
        }
        catch (TemplateException e)
        {
            throw FreemarkerUtils.translateException(name, e);
        }
        catch (ParseException e)
        {
            throw FreemarkerUtils.translateException(name, e);
        }
        catch (IOException e)
        {
            throw new InternalError("Unexpected IOException: " + e);
        }
    }

    public static void mergeTemplateByPath(String name, Map<String, Object> context, Configuration configuration, Writer out) throws ProjectElementException
    {
        try
        {
            final Template template = configuration.getTemplate(name);
            template.process(context, out);
            out.close();
        }
        catch (TemplateException e)
        {
            throw FreemarkerUtils.translateException(name, e);
        }
        catch (ParseException e)
        {
            throw FreemarkerUtils.translateException(name, e);
        }
        catch (IOException e)
        {
            throw new InternalError("Unexpected IOException: " + e);
        }
    }

    public static String mergeTemplateByPath(String name, Map<String, Object> context, Configuration configuration) throws ProjectElementException
    {
        ResultWriter out = new ResultWriter();
        mergeTemplateByPath(name, context, configuration, out);
        return out.toString().trim();
    }

    /**
     * Updates the string if necessary so that executing it as Freemarker script would
     * return an original string.
     *
     * @param input string to escape
     * @return the escaped string
     */
    public static String escapeFreemarker(String input)
    {
        if (input.contains("<#") || input.contains("${") || input.contains("<@"))
            return "<#noparse>" + input + "</#noparse>";
        return input;
    }
}
