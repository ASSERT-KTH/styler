package com.developmentontheedge.be5.server.queries.support;

import com.developmentontheedge.be5.query.model.TableModel;
import com.developmentontheedge.be5.query.services.TableModelService;
import com.developmentontheedge.be5.test.ServerBe5ProjectTest;
import org.junit.Test;

import javax.inject.Inject;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;


public class TableBuilderSupportTest extends ServerBe5ProjectTest
{
    @Inject
    private TableModelService tableModelService;

    @Test
    public void test()
    {
        initGuest();
        TableModel tableModel = tableModelService.
                create(meta.getQuery("testtableAdmin", "TestGroovyTable"), new HashMap<>());

        assertEquals("[{'name':'Guest','title':'Guest'}]", oneQuotes(jsonb.toJson(tableModel.getColumns())));

        assertEquals("[{'cells':[{'content':'[Guest]','options':{}}]}]"
                , oneQuotes(jsonb.toJson(tableModel.getRows())));
    }

}
