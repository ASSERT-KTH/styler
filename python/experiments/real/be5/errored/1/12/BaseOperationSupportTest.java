package com.developmentontheedge.be5.operation.support;

import com.developmentontheedge.be5.operation.OperationConstants;
import com.developmentontheedge.be5.operation.OperationsSqlMockProjectTest;
import com.developmentontheedge.be5.operation.OperationContext;
import com.developmentontheedge.be5.operation.OperationInfo;
import com.developmentontheedge.be5.operation.OperationResult;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class BaseOperationSupportTest extends OperationsSqlMockProjectTest
{
    private BaseOperationSupport operationSupport;

    @Before
    public void setUp()
    {
        operationSupport = new BaseOperationSupport() {
            @Override public void invoke(Object parameters) throws Exception { }
        };
        operationSupport.initialize(
                new OperationInfo(meta.getOperation("testtable", "CustomOperation")),
                new OperationContext(new Object[]{"1"}, "Test", Collections.singletonMap(OperationConstants.SELECTED_ROWS, "1")),
                OperationResult.create());
    }

    @Test
    public void redirectThisOperation()
    {
        operationSupport.redirectThisOperation();
        assertEquals("form/testtable/Test/CustomOperation/_selectedRows_=1", operationSupport.getResult().getDetails());
    }

    @Test
    public void getUrlForNewRecordId()
    {
        operationSupport.redirectThisOperationNewId("2");
        assertEquals("form/testtable/Test/CustomOperation/_selectedRows_=2", operationSupport.getResult().getDetails());
    }

    @Test
    public void redirectToTable()
    {
        operationSupport.redirectToTable("testtable", "All records", Collections.singletonMap("test", "1"));
        assertEquals("table/testtable/All records/test=1", operationSupport.getResult().getDetails());
    }

    @Test
    public void redirectToTable2()
    {
        operationSupport.redirectToTable(meta.getQuery("testtable", "All records"), Collections.singletonMap("test", "1"));
        assertEquals("table/testtable/All records/test=1", operationSupport.getResult().getDetails());
    }

}
