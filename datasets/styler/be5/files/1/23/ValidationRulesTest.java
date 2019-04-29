package com.developmentontheedge.be5.operation.services.validation;

import com.developmentontheedge.be5.operation.OperationTestUtils;
import org.junit.Test;

import static com.developmentontheedge.be5.operation.services.validation.ValidationRules.Rule;
import static com.developmentontheedge.be5.operation.services.validation.ValidationRules.pattern;
import static com.developmentontheedge.be5.operation.services.validation.ValidationRules.range;
import static com.developmentontheedge.be5.operation.services.validation.ValidationRules.step;
import static com.developmentontheedge.be5.operation.services.validation.ValidationRules.unique;
import static org.junit.Assert.assertEquals;


public class ValidationRulesTest extends OperationTestUtils
{
    @Test
    public void rangeTest()
    {
        Rule test = range(0, 10);
        assertEquals("{'attr':{'max':'10','min':'0'},'type':'range'}", oneQuotes(jsonb.toJson(test)));

        test = range(100, 999, "enter 3 digits");
        assertEquals("{'attr':{'max':'999','min':'100'},'customMessage':'enter 3 digits','type':'range'}", oneQuotes(jsonb.toJson(test)));

        test = range(0, 0.5);
        assertEquals("{'attr':{'max':'0.5','min':'0.0'},'type':'range'}", oneQuotes(jsonb.toJson(test)));

        test = range(0, 0.5, "text");
        assertEquals("{'attr':{'max':'0.5','min':'0.0'},'customMessage':'text','type':'range'}", oneQuotes(jsonb.toJson(test)));
    }

    @Test
    public void stepTest()
    {
        Rule test = step(10);
        assertEquals("{'attr':'10','type':'step'}", oneQuotes(jsonb.toJson(test)));

        test = step(10, "enter an integer");
        assertEquals("{'attr':'10','customMessage':'enter an integer','type':'step'}", oneQuotes(jsonb.toJson(test)));

        test = step(0.5);
        assertEquals("{'attr':'0.5','type':'step'}", oneQuotes(jsonb.toJson(test)));

        test = step(0.01, "Must be monetary amount");
        assertEquals("{'attr':'0.01','customMessage':'Must be monetary amount','type':'step'}", oneQuotes(jsonb.toJson(test)));
    }

    @Test
    public void patternTest()
    {
        Rule test = pattern("[A-Za-zА-Яа-яЁё]");
        assertEquals("{'attr':'[A-Za-zА-Яа-яЁё]','type':'pattern'}", oneQuotes(jsonb.toJson(test)));

        test = pattern("[A-Za-zА-Яа-яЁё]", "Enter only en/ru letters");
        assertEquals("{'attr':'[A-Za-zА-Яа-яЁё]','customMessage':'Enter only en/ru letters','type':'pattern'}",
                oneQuotes(jsonb.toJson(test)));
    }

    @Test
    public void uniqueTest()
    {
        Rule test = unique("users");
        assertEquals("{'attr':{'tableName':'users'},'type':'unique'}", oneQuotes(jsonb.toJson(test)));
    }

    @Test
    public void manyTest()
    {
        Rule[] list = {pattern("^[0-9]+$"), unique("users")};
        assertEquals("[" +
                "{'attr':'^[0-9]+$','type':'pattern'}," +
                "{'attr':{'tableName':'users'},'type':'unique'}" +
                "]", oneQuotes(jsonb.toJson(list)));
    }

}