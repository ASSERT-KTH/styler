package com.developmentontheedge.be5.metadata.sql.macro;

public class PostgresMacroProcessorStrategy extends MacroProcessorStrategySupport
{
    private static final String DEFAULT_DATE_FORMAT = "YYYY-MM-DD";
    private static final String DEFAULT_DATETIME_FORMAT = "YYYY-MM-DD HH24:MI:SS";

    public PostgresMacroProcessorStrategy()
    {
    }

    @Override
    public String dateFormat(String str)
    {
        return "TO_CHAR(" + str + ",'" + DEFAULT_DATE_FORMAT + "')";
    }

    @Override
    public String datetimeFormat(String str)
    {
        return "TO_CHAR(" + str + ",'" + DEFAULT_DATETIME_FORMAT + "')";
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
        return "CAST (" + input + " AS VARCHAR)";
    }

    @Override
    public String castAsDate(String input)
    {
        return "TO_DATE(" + input + ",'" + DEFAULT_DATE_FORMAT + "')";
    }

    @Override
    public String replace(String... args)
    {
        return "REPLACE(" + args[0] + "," + args[1] + "," + args[2] + ")";
    }

    @Override
    public String substring(String... args)
    {
        return "SUBSTR(" + String.join(",", args) + " )";
    }

    @Override
    public String length(String input)
    {
        return "LENGTH(" + input + ")";
    }

    @Override
    public String charFunc(String code)
    {
        return "CHR(" + code + ")";
    }

    @Override
    public String indexOf(String... args)
    {
        return "POSITION(" + args[1] + " IN " + args[0] + ")";
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
        return '(' + str(entity + '.') + " || CAST (" + id + " AS VARCHAR))";
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
        return "LIMIT " + input;
    }

    @Override
    public String year(String input)
    {
        return "EXTRACT(YEAR FROM " + input + ")";
    }

    @Override
    public String month(String input)
    {
        return "EXTRACT(MONTH FROM " + input + ")";
    }

    @Override
    public String day(String input)
    {
        return "EXTRACT(DAY FROM " + input + ")";
    }

    @Override
    public String hour(String input)
    {
        return "CAST(EXTRACT(HOUR FROM " + input + ") AS INT)";
    }

    @Override
    public String minute(String input)
    {
        return "CAST(EXTRACT(MINUTE FROM " + input + ") AS INT)";
    }

    @Override
    public String firstDayOfMonth(String date)
    {
        return "DATE_TRUNC('MONTH'," + date + ")";
    }

    @Override
    public String firstDayOfYear(String date)
    {
        return "DATE_TRUNC('YEAR'," + date + ")";
    }

    @Override
    public String fromFakeTable()
    {
        return " ";
    }

    @Override
    public String idCase(String expression)
    {
        return "LOWER(" + expression + ")";
    }

    @Override
    public String addMonths(String date, String months)
    {
        return date + " + (" + months + ") * '1 MONTH'::INTERVAL";
    }

    @Override
    public String addDays(String date, String days)
    {
        return date + " + (" + days + ") * '1 DAYS'::INTERVAL";
    }

    @Override
    public String addMillis(String date, String millis)
    {
        return "(" + date + " + INTERVAL '1 MILLISECOND' * (" + millis + "))";
    }

    @Override
    public String dayDiff(String date1, String date2)
    {
        return "EXTRACT( DAY FROM (" + date2 + "-" + date1 + ") )";
    }

    @Override
    public String castVarcharToInt(String input)
    {
        return "CAST( " + input + " AS BIGINT )";
    }

    @Override
    public String formatRusDate(String input)
    {
        return "TO_CHAR(" + input + ",'DD.MM.YYYY')";
    }
}
