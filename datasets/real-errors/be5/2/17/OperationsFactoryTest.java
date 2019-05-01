package com.developmentontheedge.be5.operation;

import com.developmentontheedge.be5.base.FrontendConstants;
import com.developmentontheedge.be5.base.exceptions.Be5Exception;
import com.developmentontheedge.be5.operation.model.Operation;
import com.developmentontheedge.be5.operation.model.OperationStatus;
import com.developmentontheedge.beans.json.JsonFactory;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;

import static org.junit.Assert.assertEquals;


public class OperationsFactoryTest extends OperationsSqlMockProjectTest
{
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

//    @Test
//    public void generateErrorInPropertyOnGenerate()
//    {
//        expectedEx.expect(IllegalArgumentException.class);
//        expectedEx.expectMessage("Error in property (getParameters)");
//        //executeAndCheck("generateErrorInProperty");
//    }

    @Test
    public void generateErrorInPropertyOnExecute()
    {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Error in property (getParameters)");
        executeAndCheck("generateErrorInProperty");
    }

    @Test
    public void generateErrorStatusOnExecute()
    {
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("The operation can not be performed.");
        executeAndCheck("generateErrorStatus");
    }

    @Test
    public void generateErrorOnExecute()
    {
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Internal error occurred during operation: testtableAdmin.ErrorProcessing");
        executeAndCheck("generateError");
    }

    @Test
    public void executeErrorInProperty()
    {
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Error in property (invoke)");
        executeAndCheck("executeErrorInProperty");
    }

    @Test
    public void executeErrorStatus()
    {
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("An error occurred while performing operations.");
        executeAndCheck("executeErrorStatus");
    }

    @Test
    public void executeError()
    {
        expectedEx.expect(Be5Exception.class);
        expectedEx.expectMessage("Internal error occurred during operation: testtableAdmin.ErrorProcessing");
        executeAndCheck("executeError");
    }

    @Test
    public void executeOperationWithoutParams()
    {
        expectedEx.expect(Be5Exception.class);
        expectedEx.expectMessage("Internal error occurred during operation: testtableAdmin.ErrorProcessing");
        executeAndCheck("withoutParams");
    }

    private void executeAndCheck(String value)
    {
        Operation operation = operations.create("testtableAdmin", "ErrorProcessing")
                .setValues(Collections.singletonMap("name", value))
                .execute();

        assertEquals(OperationStatus.ERROR, operation.getStatus());
    }

    @Test
    public void executeOk()
    {
        Operation operation = operations.create("testtableAdmin", "ErrorProcessing")
                .setValues(Collections.emptyMap())
                .execute();

        assertEquals(OperationStatus.FINISHED, operation.getStatus());
    }

    @Test
    public void testBuilder()
    {
        Object generate = operations.create("testtableAdmin", "ErrorProcessing")
                .setValues(Collections.emptyMap())
                .setQueryName("All records")
                .setRecords(new Long[]{1L})
                .setOperationParams(Collections.singletonMap(FrontendConstants.SEARCH_PARAM, false))
                .generate();

        assertEquals("{'values':{'name':'','propertyForAnotherEntity':'text'},'meta':{'/name':{'displayName':'name','columnSize':'30'}," +
                        "'/propertyForAnotherEntity':{'displayName':'propertyForAnotherEntity'}},'order':['/name','/propertyForAnotherEntity']}",
                oneQuotes(JsonFactory.bean(generate).toString()));
    }

}
