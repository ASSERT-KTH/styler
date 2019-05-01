package com.developmentontheedge.be5.modules.core.services.impl;

import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.be5.modules.core.CoreBe5ProjectDbMockTest;
import com.developmentontheedge.be5.test.mocks.DbServiceMock;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Collections;

import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

public class Be5EventDbLoggerTest extends CoreBe5ProjectDbMockTest
{
    @Inject
    private Be5EventDbLogger be5EventDbLogger;

    @Before
    public void setUp()
    {
        DbServiceMock.clearMock();
    }

    @Test
    public void queryCompleted() throws Exception
    {
        Query query = meta.getQuery("testtable", "All records");
        be5EventDbLogger.queryCompleted(query, Collections.emptyMap(), 1,2);
        verify(DbServiceMock.mock).insert(eq("INSERT INTO be5events (user_name, IP, action, startTime, endTime, " +
                        "title, entity) VALUES (?, ?, ?, ?, ?, ?, ?)"),
                anyVararg());
    }

    @Test
    public void queryError() throws Exception
    {
        Query query = meta.getQuery("testtable", "All records");
        be5EventDbLogger.queryError(query, Collections.emptyMap(), 1,2, "error");
        verify(DbServiceMock.mock).insert(eq("INSERT INTO be5events (exception, user_name, IP, action, startTime, " +
                        "endTime, title, entity) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"),
                anyVararg());
    }
}
