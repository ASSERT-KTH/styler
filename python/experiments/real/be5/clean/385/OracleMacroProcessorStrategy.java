package com.developmentontheedge.be5.metadata.sql.macro;

public class OracleMacroProcessorStrategy extends MacroProcessorStrategySupport
{
    private static final String DEFAULT_DATE_FORMAT = "YYYY-MM-DD";
    private static final String DEFAULT_DATETIME_FORMAT = "YYYY-MM-DD HH24:MI:SS";

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
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length - 1; i++)
        {
            sb.append("NVL( ").append(args[i]).append(", ");
        }
        sb.append(args[args.length - 1]);
        for (int i = 0; i < args.length - 1; i++)
        {
            sb.append(" )");
        }
        return sb.toString();
    }

    @Override
    public String castIntToVarchar(String input)
    {
        return "TO_CHAR(" + input + ")";
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
    public String limit(String input)
    {
        return "";  // Not supported by Oracle
    }

    @Override
    public String charFunc(String code)
    {
        return "CHR(" + code + ")";
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
        return "SYSDATE";
    }

    @Override
    public String currentDate()
    {
        return "SYSDATE";
    }

    @Override
    public String firstDayOfMonth(String date)
    {
        return "TRUNC(" + date + ",'MONTH')";
    }

    @Override
    public String firstDayOfYear(String date)
    {
        return "TRUNC(" + date + ",'YEAR')";
    }

    @Override
    public String fromFakeTable()
    {
        return " FROM DUAL ";
    }

    @Override
    public String year(String input)
    {
        return "TO_NUMBER(TO_CHAR(" + input + ",'YYYY'))";
    }

    @Override
    public String month(String input)
    {
        return "TO_NUMBER(TO_CHAR(" + input + ",'MM'))";
    }

    @Override
    public String day(String input)
    {
        return "TO_NUMBER(TO_CHAR(" + input + ",'DD'))";
    }

    @Override
    public String hour(String input)
    {
        return "TO_CHAR(" + input + ",'HH24')";
    }

    @Override
    public String minute(String input)
    {
        return "TO_CHAR(" + input + ",'MI')";
    }

    @Override
    public String lpad(String str, String iSize, String fill)
    {
        return "LPAD(" + str + "," + iSize + "," + fill + ")";
    }

    @Override
    public String str(String val)
    {
        return "\'" + val.replace("\'", "\'\'") + "\'";
    }

    @Override
    public String genericRefLowLevel(String entity, String id)
    {
        return '(' + str(entity + '.') + " || " + id + ")";
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
        return "CAST( " + expression + " AS VARCHAR2(15 CHAR) )";
    }

    @Override
    public String idCase(String expression)
    {
        return "UPPER(" + expression + ")";
    }

    @Override
    public String addMonths(String date, String months)
    {
        return "ADD_MONTHS(" + date + "," + months + ")";
    }

    @Override
    public String addDays(String date, String days)
    {
        return "((" + date + ")+(" + days + "))";
    }

    @Override
    public String addMillis(String date, String millis)
    {
        return "(" + date + "+(" + millis + "/86400000.0))";
    }

    @Override
    public String dayDiff(String date1, String date2)
    {
        return "TRUNC(" + date2 + "-" + date1 + ")";
    }

    @Override
    public String castVarcharToInt(String input)
    {
        return "TO_NUMBER(" + input + ")";
    }

    @Override
    public String formatRusDate(String input)
    {
        return "TO_CHAR(" + input + ",'DD.MM.YYYY')";
    }
}
