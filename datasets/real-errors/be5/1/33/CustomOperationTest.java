package com.developmentontheedge.be5.operation;

import com.developmentontheedge.be5.operation.model.OperationResult;
import com.developmentontheedge.be5.operation.util.Either;
import com.developmentontheedge.beans.json.JsonFactory;
import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;


public class CustomOperationTest extends OperationsSqlMockProjectTest
{
    @Test
    public void getParametersTest()
    {
        Either<Object, OperationResult> generate = generateOperation(
                "testtable", "All records", "CustomOperation", "0", Collections.emptyMap());

        oneAssert(generate);
    }

    @Test
    public void getParametersReload()
    {
        Either<Object, OperationResult> generate = generateOperation(
                "testtable", "All records", "CustomOperation", "0",
                ImmutableMap.of("name", "", "value", "2"));

        oneAssert(generate);
    }

    void oneAssert(Either<Object, OperationResult> generate)
    {
        Assert.assertEquals("{'values':{'name':'','value':'4'},'meta':{'/name':{'displayName':'name'},'/value':{'displayName':'value','readOnly':true}},'order':['/name','/value']}",
                oneQuotes(JsonFactory.bean(generate.getFirst())));
    }
}
