package com.developmentontheedge.be5.metadata.freemarker;

import com.developmentontheedge.be5.metadata.exception.ReadException;
import com.developmentontheedge.be5.metadata.model.ColumnDef;
import com.developmentontheedge.be5.metadata.model.Project;
import com.developmentontheedge.be5.metadata.model.TableDef;
import com.developmentontheedge.be5.metadata.serialization.LoadContext;
import com.developmentontheedge.be5.metadata.serialization.SerializationConstants;
import com.developmentontheedge.be5.metadata.serialization.yaml.deserializers.YamlDeserializer;
import com.developmentontheedge.be5.metadata.sql.Rdbms;
import freemarker.core.Environment;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class DatabaseFunctions
{
    private abstract static class BeFunction implements TemplateMethodModelEx
    {

        protected Project getProject() throws TemplateModelException
        {
            Object projectObj = Environment.getCurrentEnvironment().__getitem__("project");
            if (!(projectObj instanceof Project))
            {
                throw new TemplateModelException("Unable to access project");
            }
            Project project = (Project) projectObj;
            return project;
        }

    }

    private abstract static class DbFunction extends BeFunction
    {
        @Override
        public Object exec(@SuppressWarnings("rawtypes") List args) throws TemplateModelException
        {
            Project project = getProject();
            Rdbms system = project.getDatabaseSystem();
            if (system == null)
            {
                throw new TemplateModelException("Project database system is not defined");
            }
            List<String> stringArgs = new ArrayList<>();
            for (int i = 0; i < args.size(); i++)
            {
                convertArguments(stringArgs, args.get(i));
            }
            return exec(system, stringArgs.toArray(new String[stringArgs.size()]));
        }

        protected void convertArguments(List<String> stringArgs, Object arg) throws TemplateModelException
        {
            if (arg instanceof TemplateNumberModel)
            {
                stringArgs.add(((TemplateNumberModel) arg).getAsNumber().toString());
            }
            else if (arg instanceof TemplateScalarModel)
            {
                stringArgs.add(((TemplateScalarModel) arg).getAsString());
            }
            else if (arg instanceof TemplateSequenceModel)
            {
                for (int i = 0; i < ((TemplateSequenceModel) arg).size(); i++)
                {
                    convertArguments(stringArgs, ((TemplateSequenceModel) arg).get(i));
                }
            }
            else
                throw new TemplateModelException("Invalid argument: " + arg);
        }

        protected abstract String exec(Rdbms system, String[] args) throws TemplateModelException;
    }

    public static class Concat extends DbFunction
    {
        @Override
        protected String exec(Rdbms system, String[] args) throws TemplateModelException
        {
            if (args.length < 2)
            {
                throw new TemplateModelException("concat: at least 2 arguments expected");
            }
            return system.getMacroProcessorStrategy().concat(args);
        }
    }

    public static class Round extends DbFunction
    {
        @Override
        protected String exec(Rdbms system, String[] args) throws TemplateModelException
        {
            if (args.length < 1)
            {
                throw new TemplateModelException("round: at least 1 arguments expected");
            }
            return system.getMacroProcessorStrategy().round(args);
        }
    }

    public static class Coalesce extends DbFunction
    {
        @Override
        protected String exec(Rdbms system, String[] args) throws TemplateModelException
        {
            if (args.length < 2)
            {
                throw new TemplateModelException("coalesce: at least 2 arguments expected");
            }
            return system.getMacroProcessorStrategy().coalesce(args);
        }
    }

    public static class GenericRef extends DbFunction
    {
        @Override
        protected String exec(Rdbms system, String[] args) throws TemplateModelException
        {
            if (args.length != 2)
            {
                throw new TemplateModelException("genericRef: 2 arguments expected (table, column)");
            }
            return system.getMacroProcessorStrategy().genericRef(args[0], args[1]);
        }
    }

    public static class If extends DbFunction
    {
        @Override
        protected String exec(Rdbms system, String[] args) throws TemplateModelException
        {
            if (args.length < 2 || args.length > 3)
            {
                throw new TemplateModelException("if: 2 or 3 arguments expected (condition, trueExpression[, falseExpression)");
            }
            return "CASE WHEN " + args[0] + " THEN " + args[1] + " ELSE " + (args.length > 2 ? args[2] : "''") + " END";
        }
    }

    public static class Substring extends DbFunction
    {
        @Override
        protected String exec(Rdbms system, String[] args) throws TemplateModelException
        {
            if (args.length < 2 || args.length > 3)
            {
                throw new TemplateModelException("substring: 2 or 3 arguments expected (string, start[, end])");
            }
            return system.getMacroProcessorStrategy().substring(args);
        }
    }

    public static class Lpad extends DbFunction
    {
        @Override
        protected String exec(Rdbms system, String[] args) throws TemplateModelException
        {
            if (args.length != 3)
            {
                throw new TemplateModelException("lpad: 3 arguments expected (string, length, fill)");
            }
            return system.getMacroProcessorStrategy().lpad(args[0], args[1], args[2]);
        }
    }

    public static class Replace extends DbFunction
    {
        @Override
        protected String exec(Rdbms system, String[] args) throws TemplateModelException
        {
            if (args.length != 3)
            {
                throw new TemplateModelException("replace: 3 arguments expected (source, toFind, replacement)");
            }
            return system.getMacroProcessorStrategy().replace(args[0], args[1], args[2]);
        }
    }

    public static class Length extends DbFunction
    {
        @Override
        protected String exec(Rdbms system, String[] args) throws TemplateModelException
        {
            if (args.length != 1)
            {
                throw new TemplateModelException("length: 1 argument expected (string)");
            }
            return system.getMacroProcessorStrategy().length(args[0]);
        }
    }

    public static class Chr extends DbFunction
    {
        @Override
        protected String exec(Rdbms system, String[] args) throws TemplateModelException
        {
            if (args.length != 1)
            {
                throw new TemplateModelException("chr: 1 argument expected (string)");
            }
            return system.getMacroProcessorStrategy().charFunc(args[0]);
        }
    }

    public static class IndexOf extends DbFunction
    {
        @Override
        protected String exec(Rdbms system, String[] args) throws TemplateModelException
        {
            if (args.length != 2)
            {
                throw new TemplateModelException("indexOf: 2 arguments expected (string, substring)");
            }
            return system.getMacroProcessorStrategy().indexOf(args[0], args[1]);
        }
    }

    public static class AddMonths extends DbFunction
    {
        @Override
        protected String exec(Rdbms system, String[] args) throws TemplateModelException
        {
            if (args.length != 2)
            {
                throw new TemplateModelException("addMonths: 2 arguments expected (date, months)");
            }
            return system.getMacroProcessorStrategy().addMonths(args[0], args[1]);
        }
    }

    public static class AddDays extends DbFunction
    {
        @Override
        protected String exec(Rdbms system, String[] args) throws TemplateModelException
        {
            if (args.length != 2)
            {
                throw new TemplateModelException("addDays: 2 arguments expected (date, days)");
            }
            return system.getMacroProcessorStrategy().addDays(args[0], args[1]);
        }
    }

    public static class AddMillis extends DbFunction
    {
        @Override
        protected String exec(Rdbms system, String[] args) throws TemplateModelException
        {
            if (args.length != 2)
            {
                throw new TemplateModelException("addMillis: 2 arguments expected (date, millis)");
            }
            return system.getMacroProcessorStrategy().addMillis(args[0], args[1]);
        }
    }

    public static class DayDiff extends DbFunction
    {
        @Override
        protected String exec(Rdbms system, String[] args) throws TemplateModelException
        {
            if (args.length != 2)
            {
                throw new TemplateModelException("dayDiff: 2 arguments expected (date1, date2)");
            }
            return system.getMacroProcessorStrategy().dayDiff(args[0], args[1]);
        }
    }

    public static class Limit extends DbFunction
    {
        @Override
        protected String exec(Rdbms system, String[] args) throws TemplateModelException
        {
            if (args.length != 1)
            {
                throw new TemplateModelException("limit: 1 argument expected (string)");
            }
            return system.getMacroProcessorStrategy().limit(args[0]);
        }
    }

    public static class JoinGenericRef extends DbFunction
    {
        @Override
        protected String exec(Rdbms system, String[] args) throws TemplateModelException
        {
            if (args.length != 3)
            {
                throw new TemplateModelException("joinGenericRef: 3 arguments expected (table, alias, fromField)");
            }
            return system.getMacroProcessorStrategy().joinGenericRef(args[0], args[1], args[2]);
        }
    }

    public static class ColumnDefFunction extends BeFunction
    {
        @Override
        public Object exec(@SuppressWarnings("rawtypes") List arguments) throws TemplateModelException
        {
            if (arguments.size() != 2)
            {
                throw new TemplateModelException("columnDef: Two arguments required: column name and column definition hash");
            }
            Object arg = arguments.get(0);
            if (!(arg instanceof TemplateScalarModel))
            {
                throw new TemplateModelException("columnDef: First argument must be a string");
            }
            String columnName = ((TemplateScalarModel) arg).getAsString();
            arg = arguments.get(1);
            if (!(arg instanceof TemplateHashModelEx))
            {
                throw new TemplateModelException("columnDef: Second argument must be a hash");
            }
            ColumnDef columnDef;
            Project project = getProject();
            try
            {
                LoadContext context = new LoadContext();
                LinkedHashMap<String, Object> columnContent = FtlToYaml.ftlToHash((TemplateHashModelEx) arg);
                columnDef = YamlDeserializer.readColumnDef(context, project, columnName, columnContent);
                if (!context.getWarnings().isEmpty())
                    throw context.getWarnings().get(0);
            }
            catch (ReadException e)
            {
                throw new TemplateModelException("columnDef: " + e.getMessage(), e);
            }
            return getProject().getDatabaseSystem().getTypeManager().getColumnDefinitionClause(columnDef);
        }
    }

    public static class TableDefFunction extends BeFunction
    {
        @Override
        public Object exec(@SuppressWarnings("rawtypes") List arguments) throws TemplateModelException
        {
            if (arguments.size() < 2 || arguments.size() > 3)
            {
                throw new TemplateModelException("tableDef: 2 or 3 argument required: table name, column definitions hash, index definitions hash (optional)");
            }
            Object arg = arguments.get(0);
            if (!(arg instanceof TemplateScalarModel))
            {
                throw new TemplateModelException("tableDef: First argument must be a string");
            }
            String tableName = ((TemplateScalarModel) arg).getAsString();
            LinkedHashMap<String, Object> tableDefHash = new LinkedHashMap<>();
            arg = arguments.get(1);
            if (!(arg instanceof TemplateHashModelEx))
            {
                throw new TemplateModelException("tableDef: Second argument must be a hash");
            }
            tableDefHash.put(SerializationConstants.TAG_COLUMNS, FtlToYaml.ftlToHash((TemplateHashModelEx) arg));
            if (arguments.size() > 2)
            {
                arg = arguments.get(2);
                if (!(arg instanceof TemplateHashModelEx))
                {
                    throw new TemplateModelException("tableDef: Third argument must be a hash");
                }
                tableDefHash.put(SerializationConstants.TAG_INDICES, FtlToYaml.ftlToHash((TemplateHashModelEx) arg));
            }
            TableDef tableDef;
            Project project = getProject();
            try
            {
                LoadContext context = new LoadContext();
                tableDef = YamlDeserializer.readTableDef(context, project, tableName, tableDefHash);
                if (!context.getWarnings().isEmpty())
                    throw context.getWarnings().get(0);
            }
            catch (ReadException e)
            {
                throw new TemplateModelException("tableDef: " + e.getMessage(), e);
            }
            return tableDef.getDdl();
        }
    }
}