package com.developmentontheedge.be5.metadata.freemarker;

import com.developmentontheedge.be5.metadata.model.Project;
import com.developmentontheedge.be5.metadata.model.RoleSet;
import com.developmentontheedge.be5.metadata.sql.Rdbms;
import com.developmentontheedge.be5.metadata.sql.macro.IMacroProcessorStrategy;
import freemarker.core.BuiltIn;
import freemarker.core.Environment;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import one.util.streamex.StreamEx;

public class Be4BuiltIns
{
    abstract static class DatabaseBuiltIn extends BuiltIn
    {
        @Override
        protected TemplateModel _eval(Environment env) throws TemplateException
        {
            Object projectObj = env.__getitem__("project");
            if (!(projectObj instanceof Project))
            {
                throw new TemplateModelException("Unable to access project");
            }
            Project project = (Project) projectObj;
            Rdbms system = project.getDatabaseSystem();
            if (system == null)
            {
                throw new TemplateModelException("Project database system is not defined");
            }
            String str = target.evalAndCoerceToString(env);
            return new SimpleScalar(process(project, system, FreemarkerUtils.NULL.equals(str) ? null : str));
        }

        abstract String process(Project project, Rdbms system, String input);
    }

    public static class Roles extends DatabaseBuiltIn
    {
        @Override
        String process(Project project, Rdbms system, String input)
        {
            RoleSet roleSet = new RoleSet(project);
            StreamEx.split(input, ",").map(String::trim).forEach(roleSet::add);
            return String.join(",", roleSet.getFinalRoles());
        }
    }

    public static class Quote extends DatabaseBuiltIn
    {
        @Override
        String process(Project project, Rdbms system, String input)
        {
            return system.getTypeManager().normalizeIdentifier(input);
        }
    }

    public static class Str extends DatabaseBuiltIn
    {
        @Override
        String process(Project project, Rdbms system, String input)
        {
            if (input == null)
                return "NULL";
            return system.getMacroProcessorStrategy().str(input);
        }
    }

    public static class AsVarchar extends DatabaseBuiltIn
    {
        @Override
        String process(Project project, Rdbms system, String input)
        {
            return system.getMacroProcessorStrategy().castIntToVarchar(input);
        }
    }

    public static class AsInt extends DatabaseBuiltIn
    {
        @Override
        String process(Project project, Rdbms system, String input)
        {
            return system.getMacroProcessorStrategy().castVarcharToInt(input);
        }
    }

    public static class AsDate extends DatabaseBuiltIn
    {
        @Override
        String process(Project project, Rdbms system, String input)
        {
            return system.getMacroProcessorStrategy().castAsDate(input);
        }
    }

    public static class AsPrimaryKey extends DatabaseBuiltIn
    {
        @Override
        String process(Project project, Rdbms system, String input)
        {
            return system.getMacroProcessorStrategy().castAsPrimaryKey(input);
        }
    }

    public static class IdCase extends DatabaseBuiltIn
    {
        @Override
        String process(Project project, Rdbms system, String input)
        {
            return system.getMacroProcessorStrategy().idCase(input);
        }
    }

    public static class UpperFirst extends DatabaseBuiltIn
    {
        @Override
        String process(Project project, Rdbms system, String input)
        {
            IMacroProcessorStrategy macroProcessor = system.getMacroProcessorStrategy();
            return macroProcessor.concat(macroProcessor.upper(macroProcessor.substring(input, "1", "1")),
                    macroProcessor.lower(macroProcessor.substring(input, "2")));
        }
    }

    public static class Upper extends DatabaseBuiltIn
    {
        @Override
        String process(Project project, Rdbms system, String input)
        {
            return system.getMacroProcessorStrategy().upper(input);
        }
    }

    public static class Lower extends DatabaseBuiltIn
    {
        @Override
        String process(Project project, Rdbms system, String input)
        {
            return system.getMacroProcessorStrategy().lower(input);
        }
    }

    public static class DateFormat extends DatabaseBuiltIn
    {
        @Override
        String process(Project project, Rdbms system, String input)
        {
            return system.getMacroProcessorStrategy().dateFormat(input);
        }
    }

    public static class DateTimeFormat extends DatabaseBuiltIn
    {
        @Override
        String process(Project project, Rdbms system, String input)
        {
            return system.getMacroProcessorStrategy().datetimeFormat(input);
        }
    }

    public static class Year extends DatabaseBuiltIn
    {
        @Override
        String process(Project project, Rdbms system, String input)
        {
            return system.getMacroProcessorStrategy().year(input);
        }
    }

    public static class Month extends DatabaseBuiltIn
    {
        @Override
        String process(Project project, Rdbms system, String input)
        {
            return system.getMacroProcessorStrategy().month(input);
        }
    }

    public static class Day extends DatabaseBuiltIn
    {
        @Override
        String process(Project project, Rdbms system, String input)
        {
            return system.getMacroProcessorStrategy().day(input);
        }
    }

    public static class Hour extends DatabaseBuiltIn
    {
        @Override
        String process(Project project, Rdbms system, String input)
        {
            return system.getMacroProcessorStrategy().hour(input);
        }
    }

    public static class Minute extends DatabaseBuiltIn
    {
        @Override
        String process(Project project, Rdbms system, String input)
        {
            return system.getMacroProcessorStrategy().minute(input);
        }
    }

    public static class AsRusDate extends DatabaseBuiltIn
    {
        @Override
        String process(Project project, Rdbms system, String input)
        {
            return system.getMacroProcessorStrategy().formatRusDate(input);
        }
    }

    public static class YearStart extends DatabaseBuiltIn
    {
        @Override
        String process(Project project, Rdbms system, String input)
        {
            return system.getMacroProcessorStrategy().firstDayOfYear(input);
        }
    }

    public static class MonthStart extends DatabaseBuiltIn
    {
        @Override
        String process(Project project, Rdbms system, String input)
        {
            return system.getMacroProcessorStrategy().firstDayOfMonth(input);
        }
    }

    public static class OrEmpty extends DatabaseBuiltIn
    {
        @Override
        String process(Project project, Rdbms system, String input)
        {
            IMacroProcessorStrategy macroProcessorStrategy = system.getMacroProcessorStrategy();
            if (input.startsWith("SELECT "))
                return macroProcessorStrategy.coalesce("(" + input + " )", macroProcessorStrategy.str(""));
            return macroProcessorStrategy.coalesce(input, macroProcessorStrategy.str(""));
        }
    }

    public static class Trim extends DatabaseBuiltIn
    {
        @Override
        String process(Project project, Rdbms system, String input)
        {
            return system.getMacroProcessorStrategy().trim(input);
        }
    }
}
