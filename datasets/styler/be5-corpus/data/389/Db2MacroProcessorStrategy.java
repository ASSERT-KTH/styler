package com.developmentontheedge.be5.metadata.sql.macro;

public class Db2MacroProcessorStrategy extends MacroProcessorStrategySupport
{
    public Db2MacroProcessorStrategy()
    {
    }

    @Override
    public String dateFormat(String str)
    {
        return "CAST(" + str + " AS CHAR(10))";
    }

    @Override
    public String datetimeFormat(String str)
    {
        return "CAST(" + str + " AS CHAR(19))";
    }

    @Override
    public String concat(String... args)
    {
        return "( " + String.join(" || ", args) + " )";
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
        return "RTRIM( CAST( " + input + " AS CHAR(254) ) )";
    }

    @Override
    public String castAsDate(String input)
    {
        return input; // This macro is absent in BeanExplorer
    }

    @Override
    public String replace(String... args)
    {
        return "REPLACE(" + args[0] + "," + args[1] + "," + args[2] + ")"; // This macro is absent in BeanExplorer
    }

    @Override
    public String substring(String... args)
    {
        return "SUBSTR(" + String.join(",", args) + " )";
    }

    @Override
    public String length(String input)
    {
        return "LENGTH(" + input + ")"; // This macro is absent in BeanExplorer
    }

    @Override
    public String charFunc(String code)
    {
        return "CHR(" + code + ")";
    }

    @Override
    public String indexOf(String... args)
    {
        return "INSTR(" + args[0] + ", " + args[1] + ")"; // This macro is absent in BeanExplorer
    }

    @Override
    public String greatest(String... args)
    {
        return "GREATEST(" + String.join(",", args) + ")"; // This macro is absent in BeanExplorer
    }

    @Override
    public String least(String... args)
    {
        return "LEAST(" + String.join(",", args) + ")"; // This macro is absent in BeanExplorer
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
        return "CURRENT_TIMESTAMP";
    }

    @Override
    public String currentDate()
    {
        return "CURRENT_DATE";
    }

    @Override
    public String str(String val)
    {
        return "\'" + val.replace("\'", "\'\'") + "\'";
    }

    @Override
    public String genericRefLowLevel(String entity, String id)
    {
        return '(' + str(entity + '.') + " || RTRIM( CAST( " + id + " AS CHAR( 20 ) ) ) )";
    }

    @Override
    public String joinGenericRef(String table, String alias, String fromField)
    {
        return "LEFT JOIN " + table + "<parameter:_tcloneid_ default=\"\"/> " + alias + " ON " + fromField + " LIKE '" + table + ".%' AND "
                + substring(fromField, String.valueOf(table.length() + 2)) + " = " + castIntToVarchar(alias + ".ID");
    }

    @Override
    public String lpad(String str, String iSize, String fill)
    {
        return "LPAD(" + str + "," + iSize + "," + fill + ")";
    }

    @Override
    public String castAsPrimaryKey(String expression)
    {
        return "CAST( " + expression + " AS BIGINT )";
    }

    @Override
    public String limit(String input)
    {
        return "FETCH FIRST " + input + " ROWS ONLY";
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
        return "HOUR(" + input + ")";
    }

    @Override
    public String minute(String input)
    {
        return "MINUTE(" + input + ")";
    }

    @Override
    public String firstDayOfMonth(String date)
    {
        // TODO: check
        return date + " - (DAY(" + date + ")-1) DAYS";
    }

    @Override
    public String firstDayOfYear(String date)
    {
        // TODO: check
        return date + " - (MONTH(" + date + ")-1) MONTHS - (DAY(" + date + ")-1) DAYS";
    }

    @Override
    public String fromFakeTable()
    {
        return " FROM SYSIBM.SYSDUMMY1 ";
    }

    @Override
    public String idCase(String expression)
    {
        return "UPPER(" + expression + ")";
    }

    @Override
    public String addDays(String date, String months)
    {
        return "(" + date + "+" + months + " days)";
    }

    @Override
    public String addMonths(String date, String days)
    {
        return "(" + date + "+" + days + " months)";
    }

    @Override
    public String addMillis(String date, String millis)
    {
        return "(" + date + "+(" + millis + "*1000) microseconds)";
    }

    @Override
    public String dayDiff(String date1, String date2)
    {
        return "DAYS(" + date2 + ") - DAYS(" + date1 + ")";
    }

    @Override
    public String castVarcharToInt(String input)
    {
        return "CAST( " + input + " AS BIGINT )";
    }

    @Override
    public String formatRusDate(String input)
    {
        return "VARCHAR_FORMAT(" + input + ",'DD.MM.YYYY')";
    }
}
