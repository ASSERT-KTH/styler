package com.developmentontheedge.be5.metadata.sql.macro;

public class MySqlMacroProcessorStrategy extends MacroProcessorStrategySupport
{
    private static final String DEFAULT_DATE_FORMAT = "%Y-%m-%d";
    private static final String DEFAULT_DATETIME_FORMAT = "%Y-%m-%d %H:%i:%S";

    public MySqlMacroProcessorStrategy()
    {
    }

    @Override
    protected String quoteIdentifier(String id)
    {
        return '`' + id + '`';
    }

    @Override
    public String dateFormat(String str)
    {
        return "DATE_FORMAT(" + str + ", '" + DEFAULT_DATE_FORMAT + "')";
    }

    @Override
    public String datetimeFormat(String str)
    {
        return "DATE_FORMAT(" + str + ", '" + DEFAULT_DATETIME_FORMAT + "')";
    }

    @Override
    public String concat(String... args)
    {
        return "CONCAT( " + String.join(",", args) + " )";
    }

    @Override
    public String round(String... args)
    {
        return "ROUND( " + String.join(",", args) + " )";
    }

    @Override
    public String coalesce(String... args)
    {
        return "COALESCE( " + String.join(",", args) + " )";
    }

    @Override
    public String castIntToVarchar(String input)
    {
        return "CAST(" + input + " AS CHAR)";
    }

    @Override
    public String castAsDate(String input)
    {
        return "CAST(" + input + " AS DATE)";
    }

    @Override
    public String replace(String... args)
    {
        return "REPLACE(" + args[0] + "," + args[1] + "," + args[2] + ")";
    }

    @Override
    public String substring(String... args)
    {
        return "SUBSTRING(" + String.join(",", args) + " )";
    }

    @Override
    public String length(String input) // check me!
    {
        return "LENGTH(" + input + ")";
    }

    @Override
    public String charFunc(String code)
    {
        return "CHAR(" + code + ")";
    }

    @Override
    public String indexOf(String... args)
    {
        return "INSTR(" + args[0] + ", " + args[1] + ")";
    }

    @Override
    public String greatest(String... args)
    {
        return "GREATEST(" + String.join(",", args) + ")";
    }

    @Override
    public String least(String... args)
    {
        return "LEAST(" + String.join(",", args) + ")";
    }

    @Override
    public String upper(String str)
    {
        return "UPPER(" + str + ")";
    }

    @Override
    public String lower(String str)
    {
        return "LOWER(" + str + ")";
    }

    @Override
    public String currentDatetime()
    {
        return "NOW()";
    }

    @Override
    public String currentDate()
    {
        return "CURRENT_DATE";
    }

    @Override
    public String str(String val)
    {
        return "\'" + val.replace("\\", "\\\\").replace("\'", "\'\'") + "\'";
    }

    @Override
    public String genericRefLowLevel(String entity, String id)
    {
        return "CONCAT( " + str(entity + '.') + "," + id + ")";
    }

    @Override
    public String lpad(String str, String iSize, String fill)
    {
        return "LPAD(" + str + "," + iSize + "," + fill + ")";
    }

    @Override
    public String joinGenericRef(String table, String alias, String fromField)
    {
        return "LEFT JOIN " + table + "<parameter:_tcloneid_ default=\"\"/> " + alias + " ON " + fromField + " LIKE '" + table + ".%' AND "
                + castAsPrimaryKey(substring(fromField, String.valueOf(table.length() + 2))) + " = " + alias + ".ID";
    }

    @Override
    public String castAsPrimaryKey(String expression)
    {
        return "CAST( " + expression + " AS UNSIGNED )";
    }

    @Override
    public String limit(String input)
    {
        return "LIMIT " + input;
    }

    @Override
    public String year(String input)
    {
        return "YEAR(" + input + ")";
    }

    @Override
    public String month(String input)
    {
        return "MONTH(" + input + ")";
    }

    @Override
    public String day(String input)
    {
        return "DAY(" + input + ")";
    }

    @Override
    public String hour(String input)
    {
        return "DATE_FORMAT(" + input + ",'%H')";
    }

    @Override
    public String minute(String input)
    {
        return "DATE_FORMAT(" + input + ",'%i')";
    }

    @Override
    public String firstDayOfMonth(String date)
    {
        return "DATE_SUB(DATE(" + date + "),INTERVAL DAY(" + date + ")-1 DAY)";
    }

    @Override
    public String firstDayOfYear(String date)
    {
        return "DATE_FORMAT(" + date + " ,'%Y-01-01')";
    }

    @Override
    public String fromFakeTable()
    {
        return "FROM DUAL";
    }

    @Override
    public String idCase(String expression)
    {
        return expression;
    }

    @Override
    public String addMonths(String date, String months)
    {
        return "DATE_ADD(" + date + ", INTERVAL " + months + " MONTH)";
    }

    @Override
    public String addDays(String date, String days)
    {
        return "DATE_ADD(" + date + ", INTERVAL " + days + " DAYS)";
    }


    @Override
    public String addMillis(String date, String millis)
    {
        return "DATE_ADD(" + date + ", INTERVAL (" + millis + "*1000) MICROSECOND)";
    }

    @Override
    public String dayDiff(String date1, String date2)
    {
        return "DATEDIFF(" + date1 + "," + date2 + ")";
    }

    @Override
    public String castVarcharToInt(String input)
    {
        return "CAST( " + input + " AS SIGNED )";
    }

    @Override
    public String formatRusDate(String input)
    {
        return "DATE_FORMAT(" + input + ", '%d.%m.%Y')";
    }
}
