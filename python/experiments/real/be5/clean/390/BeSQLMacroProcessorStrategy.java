package com.developmentontheedge.be5.metadata.sql.macro;

public class BeSQLMacroProcessorStrategy extends PostgresMacroProcessorStrategy
{
    @Override
    public String castAsPrimaryKey(String expression)
    {
        return "TO_KEY( " + expression + " )";
    }

    @Override
    public String genericRef(String entity, String id)
    {
        return "GENERIC_REF(" + str(entity) + ", " + id + ")";
    }

    @Override
    public String addMonths(String date, String months)
    {
        return "ADD_MONTHS(" + date + ", " + months + ")";
    }

    @Override
    public String addDays(String date, String days)
    {
        return "ADD_DAYS(" + date + ", " + days + ")";
    }

    @Override
    public String addMillis(String date, String millis)
    {
        return "ADD_MILLIS(" + date + ", " + millis + ")";
    }
}
