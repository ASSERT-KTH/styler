package com.developmentontheedge.be5.metadata.sql.macro;

public class SqlServerMacroProcessorStrategy extends MacroProcessorStrategySupport
{
    @Override
    public String dateFormat(String str)
    {
        return "CONVERT( VARCHAR( 10 ), " + str + ", 120 )";
    }

    @Override
    public String datetimeFormat(String str)
    {
        return "CONVERT( VARCHAR( 19 ), " + str + ", 120 )";
    }

    @Override
    public String concat(String... args)
    {
        return "( " + String.join(" + ", args) + " )";
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
        return "CAST (" + input + " AS VARCHAR( 1000 ))";
    }

    @Override
    public String castAsDate(String input)
    {
        return "CONVERT( DATE, " + input + ", 120 )";
    }

    @Override
    public String replace(String... args)
    {
        return "REPLACE(" + args[0] + "," + args[1] + "," + args[2] + ")";
    }

    @Override
    public String substring(String... args)
    {
        if (args.length > 2)
            return "SUBSTRING(" + args[0] + "," + args[1] + "," + args[2] + ")";
        return "SUBSTRING(" + args[0] + "," + args[1] + ",100000)";
    }

    @Override
    public String length(String input)
    {
        return "LEN(" + input + ")";
    }

    @Override
    public String charFunc(String code)
    {
        return "CHAR(" + code + ")";
    }

    @Override
    public String indexOf(String... args)
    {
        if (args.length > 2)
            return "CHARINDEX(" + args[1] + "," + args[0] + "," + args[2] + ")";
        return "CHARINDEX(" + args[1] + "," + args[0] + ")";
    }

    @Override
    public String greatest(String... args)
    {
        return "( SELECT MAX(V) FROM( VALUES (" + String.join("), (", args) + ") ) ) AS GREATEST(V)";
    }

    @Override
    public String least(String... args)
    {
        return "( SELECT MIN(V) FROM( VALUES (" + String.join("), (", args) + ") ) ) AS LEAST(V)";
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
    public String trim(String str)
    {
        return "LTRIM(RTRIM(" + str + "))";
    }

    @Override
    public String currentDatetime()
    {
        return "GETDATE()";
    }

    @Override
    public String currentDate()
    {
        return "CAST(GETDATE() AS DATE)";
    }

    @Override
    public String str(String val)
    {
        return "\'" + val.replace("\'", "\'\'") + "\'";
    }

    @Override
    public String genericRefLowLevel(String entity, String id)
    {
        return '(' + str(entity + '.') + " + CAST (" + id + " AS VARCHAR( 20 )))";
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
        return "RIGHT(REPLICATE(" + fill + "," + iSize + ")+" + str + "," + iSize + ")";
    }

    @Override
    public String castAsPrimaryKey(String expression)
    {
        return "CAST( " + expression + " AS BIGINT )";
    }

    @Override
    public String limit(String input)
    {
        return "";
    }

    @Override
    public String year(String input)
    {
        return "DATEPART(YEAR," + input + ")";
    }

    @Override
    public String month(String input)
    {
        return "DATEPART(MONTH," + input + ")";
    }

    @Override
    public String day(String input)
    {
        return "DATEPART(DAY," + input + ")";
    }

    @Override
    public String hour(String input)
    {
        return "DATEPART(hh," + input + ")";
    }

    @Override
    public String minute(String input)
    {
        return "DATEPART(mi," + input + ")";
    }

    @Override
    public String firstDayOfMonth(String date)
    {
        return "DATEADD(MONTH,DATEDIFF(MONTH,0," + date + "),0)";
    }

    @Override
    public String firstDayOfYear(String date)
    {
        return "DATEADD(YEAR,DATEDIFF(YEAR,0," + date + "),0)";
    }

    @Override
    public String fromFakeTable()
    {
        return " ";
    }

    @Override
    public String idCase(String expression)
    {
        return expression;
    }

    @Override
    public String addMonths(String date, String months)
    {
        return "DATEADD(MONTH," + months + "," + date + ")";
    }

    @Override
    public String addDays(String date, String days)
    {
        return "DATEADD(DAY," + days + "," + date + ")";
    }

    @Override
    public String addMillis(String date, String millis)
    {
        return "DATEADD(MILLISECOND," + millis + "," + date + ")";
    }

    @Override
    public String dayDiff(String date1, String date2)
    {
        return "DATEDIFF(DAY," + date1 + "," + date2 + ")";
    }

    @Override
    public String castVarcharToInt(String input)
    {
        return "CAST( " + input + " AS BIGINT )";
    }

    @Override
    public String formatRusDate(String input)
    {
        return "CONVERT( VARCHAR( 10 ), " + input + ", 104 )";
    }
}
