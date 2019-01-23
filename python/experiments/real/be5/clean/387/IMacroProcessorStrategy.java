package com.developmentontheedge.be5.metadata.sql.macro;

public interface IMacroProcessorStrategy
{
    // Functions

    @Macro("_DBMS_DATE_FORMAT")
    String dateFormat(String str);

    @Macro("_DBMS_DATETIME_FORMAT")
    String datetimeFormat(String str);

    @Macro("_DBMS_CONCAT")
    String concat(String... args);

    @Macro("_DBMS_ROUND")
    String round(String... args);

    @Macro("_DBMS_COALESCE")
    String coalesce(String... args);

    @Macro("_DBMS_CAST_INT_TO_VARCHAR")
    String castIntToVarchar(String input);

    @Macro("_DBMS_CAST_VARCHAR_TO_INT")
    String castVarcharToInt(String input);

    @Macro("_DBMS_CHAR_CAST_AS_DATE")
    String castAsDate(String input);

    @Macro("_DBMS_REPLACE")
    String replace(String... args);

    @Macro("_DBMS_SUBSTRING")
    String substring(String... args);

    @Macro("_DBMS_LENGTH")
    String length(String input);

    @Macro("_DBMS_LIMIT")
    String limit(String input);

    @Macro("_DBMS_FUNCTION_CHR")
    String charFunc(String code);

    @Macro("_DBMS_CHARINDEX")
    String indexOf(String... args);

    @Macro("_DBMS_GREATEST")
    String greatest(String... args);

    @Macro("_DBMS_LEAST")
    String least(String... args);

    @Macro("_DBMS_UPPER")
    String upper(String str);

    @Macro("_DBMS_LOWER")
    String lower(String str);

    @Macro("_DBMS_CURRENT_DATETIME")
    String currentDatetime();

    @Macro("_DBMS_CURRENT_DATE")
    String currentDate();

    @Macro("_DBMS_FIRST_DAY_OF_MONTH")
    String firstDayOfMonth(String date);

    @Macro("_DBMS_FIRST_DAY_OF_YEAR")
    String firstDayOfYear(String date);

    @Macro("_DBMS_FROM_FAKE_TABLE")
    String fromFakeTable();

    @Macro("_DBMS_YEAR")
    String year(String input);

    @Macro("_DBMS_MONTH")
    String month(String input);

    @Macro("_DBMS_DAY")
    String day(String input);

    @Macro("_DBMS_TIME_HH")
    String hour(String input);

    @Macro("_DBMS_TIME_MM")
    String minute(String input);

    @Macro("_DBMS_DATE_FORMAT_RU")
    String formatRusDate(String input);

    @Macro("_DBMS_LPAD")
    String lpad(String str, String iSize, String fill);

    String str(String val);

    @Macro("GENERIC_REF")
    String genericRef(String entity, String id);

    String genericRefLowLevel(String entity, String id);

    @Macro("JOIN_GENERIC_REF")
    String joinGenericRef(String table, String alias, String fromField);

    @Macro("_DBMS_CAST_AS_PK")
    String castAsPrimaryKey(String expression);

    @Macro("_DBMS_ADD_MONTHS")
    String addMonths(String date, String months);

    @Macro("_DBMS_ADD_DAYS")
    String addDays(String date, String days);

    @Macro("_DBMS_ADD_MILLIS")
    String addMillis(String date, String millis);

    @Macro("_DBMS_DAY_DIFF")
    String dayDiff(String date1, String date2);

    @Macro("_DBMS_TRIM")
    String trim(String str);

    String idCase(String expression);
}
