package com.developmentontheedge.be5.metadata.sql.macro;

public class M4MacroProcessorStrategy extends MacroProcessorStrategySupport
{

    @Override
    public String dateFormat(String str)
    {
        return "_DBMS_DATE_FORMAT(" + str + ")";
    }

    @Override
    public String datetimeFormat(String str)
    {
        return "_DBMS_DATETIME_FORMAT(" + str + ")";
    }

    @Override
    public String concat(String... args)
    {
        return "_DBMS_CONCAT( " + String.join(", ", args) + " )";
    }

    @Override
    public String round(String... args)
    {
        return "_DBMS_ROUND( " + String.join(",", args) + " )";
    }

    @Override
    public String coalesce(String... args)
    {
        return "_DBMS_COALESCE( " + String.join(", ", args) + " )";
    }

    @Override
    public String castIntToVarchar(String input)
    {
        return "_DBMS_CAST_INT_TO_VARCHAR(" + input + ")";
    }

    @Override
    public String castVarcharToInt(String input)
    {
        return "_DBMS_CAST_VARCHAR_TO_INT(" + input + ")";
    }

    @Override
    public String castAsDate(String input)
    {
        return "_DBMS_CHAR_CAST_AS_DATE(" + input + ")";
    }

    @Override
    public String replace(String... args)
    {
        return "_DBMS_REPLACE(" + args[0] + ", " + args[1] + ", " + args[2] + ")";
    }

    @Override
    public String substring(String... args)
    {
        return "_DBMS_SUBSTRING(" + String.join(", ", args) + ")";
    }

    @Override
    public String length(String input)
    {
        return "_DBMS_LENGTH(" + input + ")";
    }

    @Override
    public String limit(String input)
    {
        return "_DBMS_LIMIT(" + input + ")";
    }

    @Override
    public String charFunc(String code)
    {
        return "_DBMS_FUNCTION_CHR(" + code + ")";
    }

    @Override
    public String indexOf(String... args)
    {
        return "_DBMS_CHARINDEX(" + args[0] + ", " + args[1] + ")";
    }

    @Override
    public String greatest(String... args)
    {
        return "_DBMS_GREATEST(" + String.join(", ", args) + ")";
    }

    @Override
    public String least(String... args)
    {
        return "_DBMS_LEAST(" + String.join(", ", args) + ")";
    }

    @Override
    public String upper(String str)
    {
        return "_DBMS_UPPER(" + str + ")";
    }

    @Override
    public String lower(String str)
    {
        return "_DBMS_LOWER(" + str + ")";
    }

    @Override
    public String trim(String str)
    {
        return "_DBMS_TRIM(" + str + ")";
    }

    @Override
    public String currentDatetime()
    {
        return "_DBMS_CURRENT_DATETIME";
    }

    @Override
    public String currentDate()
    {
        return "_DBMS_CURRENT_DATE";
    }

    @Override
    public String firstDayOfMonth(String date)
    {
        return "_DBMS_FIRST_DAY_OF_MONTH(" + date + ")";
    }

    @Override
    public String firstDayOfYear(String date)
    {
        return "_DBMS_FIRST_DAY_OF_YEAR(" + date + ")";
    }

    @Override
    public String fromFakeTable()
    {
        return "_DBMS_FROM_FAKE_TABLE";
    }

    @Override
    public String year(String input)
    {
        return "_DBMS_YEAR(" + input + ")";
    }

    @Override
    public String month(String input)
    {
        return "_DBMS_MONTH(" + input + ")";
    }

    @Override
    public String day(String input)
    {
        return "_DBMS_DAY(" + input + ")";
    }

    @Override
    public String hour(String input)
    {
        return "_DBMS_TIME_HH(" + input + ")";
    }

    @Override
    public String minute(String input)
    {
        return "_DBMS_TIME_MM(" + input + ")";
    }

    @Override
    public String formatRusDate(String input)
    {
        return "_DBMS_DATE_FORMAT_RU(" + input + ")";
    }

    @Override
    public String lpad(String str, String iSize, String fill)
    {
        return "_DBMS_LPAD(" + str + ", " + iSize + ", " + fill + ")";
    }

    @Override
    public String str(String val)
    {
        return "\'" + val.replace("\'", "\'\'") + "\'";
    }

    @Override
    public String genericRef(String entity, String id)
    {
        return genericRefLowLevel(entity, id);
    }

    @Override
    public String genericRefLowLevel(String entity, String id)
    {
        return "GENERIC_REF(" + entity + ", " + id + ")";
    }

    @Override
    public String joinGenericRef(String table, String alias, String fromField)
    {
        return "JOIN_GENERIC_REF(" + table + ", " + alias + ", " + fromField + ")";
    }

    @Override
    public String castAsPrimaryKey(String expression)
    {
        return "_DBMS_CAST_AS_PK(" + expression + ")";
    }

    @Override
    public String addMonths(String date, String months)
    {
        return "_DBMS_ADD_MONTHS(" + date + ", " + months + ")";
    }

    @Override
    public String addDays(String date, String days)
    {
        return "_DBMS_ADD_DAYS(" + date + ", " + days + ")";
    }

    @Override
    public String addMillis(String date, String millis)
    {
        return "_DBMS_ADD_MILLIS(" + date + ", " + millis + ")";
    }

    @Override
    public String dayDiff(String date1, String date2)
    {
        return "_DBMS_DAY_DIFF(" + date1 + "," + date2 + ")";
    }

    @Override
    public String idCase(String expression)
    {
        return expression;
    }

}
