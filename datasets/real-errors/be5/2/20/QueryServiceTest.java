package com.developmentontheedge.be5.query.services;

import com.developmentontheedge.be5.base.exceptions.Be5Exception;
import com.developmentontheedge.be5.database.DbService;
import com.developmentontheedge.be5.metadata.RoleType;
import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.be5.query.QueryBe5ProjectDBTest;
import com.developmentontheedge.be5.query.QueryExecutor;
import com.developmentontheedge.be5.query.SqlQueryExecutor;
import com.developmentontheedge.be5.query.model.TableModel;
import com.developmentontheedge.be5.query.model.beans.QRec;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static java.util.Collections.emptyMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class QueryServiceTest extends QueryBe5ProjectDBTest
{
    @Inject
    private DbService db;
    @Inject
    private QueryExecutorFactory queryService;

    @Before
    public void insertOneRow()
    {
        setStaticUserInfo(RoleType.ROLE_GUEST);
        db.update("delete from testtable");
        db.insert("insert into testtable (name, value) VALUES (?, ?)", "user1", 1L);
        db.insert("insert into testtable (name, value) VALUES (?, ?)", "user2", 2L);
    }

    @Test
    public void testExecute()
    {
        Query query = meta.getQuery("testtable", "All records");
        List<QRec> dps = queryService.get(query, emptyMap()).execute();
        assertTrue(dps.size() > 0);

        assertEquals(String.class, dps.get(0).getProperty("name").getType());
    }

    @Test
    public void testCountFromQuery()
    {
        QueryExecutor sqlQueryBuilder = queryService.get(meta.getQuery("testtable", "All records"), emptyMap());

        assertTrue(sqlQueryBuilder.count() > 0);
    }

    @Test
    public void beAggregate1D()
    {
        Query query = meta.getQuery("testtable", "beAggregate1D");
        List<QRec> recs = queryService.get(query, new HashMap<>()).execute();

        assertEquals("3.0", recs.get(2).getString("Value"));
    }

    @Test
    public void testMultipleColumn()
    {
        Query query = meta.getQuery("testtable", "TestMultipleColumn");

        assertEquals("SELECT *\n" +
                "FROM testtable\n" +
                "WHERE name IN ('test1', 'test2') LIMIT 2147483647", queryService.
                getSqlQueryBuilder(query, Collections.singletonMap("name", Arrays.asList("test1", "test2"))).getFinalSql().getQuery().toString());
    }

    @Test
    public void testMultipleColumnLong()
    {
        Query query = meta.getQuery("testtable", "TestMultipleColumnLong");

        assertEquals("SELECT *\n" +
                "FROM testtable\n" +
                "WHERE ID IN (1, 2) LIMIT 2147483647", queryService.
                getSqlQueryBuilder(query, Collections.singletonMap("ID", Arrays.asList("1", "2"))).getFinalSql().getQuery().toString());
    }

    @Test
    public void testResolveTypeOfRefColumn()
    {
        Query query = meta.getQuery("testtable", "TestResolveRefColumn");

        assertEquals("SELECT *\n" +
                "FROM testtable\n" +
                "WHERE name = 'test' LIMIT 2147483647", queryService.
                getSqlQueryBuilder(query, Collections.singletonMap("name", "test")).getFinalSql().getQuery().toString());
    }

    @Test
    @Ignore
    public void testTestResolveRefColumnByAlias()
    {
        Query query = meta.getQuery("testtable", "TestResolveRefColumnByAlias");

        assertEquals("SELECT *\n" +
                "FROM testtable t\n" +
                "WHERE name = 'test' LIMIT 2147483647", queryService.
                getSqlQueryBuilder(query, Collections.singletonMap("name", "test")).getFinalSql().getQuery().toString());
    }

    @Test
    public void testIgnoreUnknownColumn()
    {
        Query query = meta.getQuery("testtable", "TestResolveRefColumn");
        List<QRec> list = queryService.get(query, Collections.singletonMap("unknownColumn", "test")).execute();
        assertEquals(list.size(), 0);
    }

    @Test(expected = Be5Exception.class)
    public void testResolveTypeOfRefColumnError()
    {
        Query query = meta.getQuery("testtable", "TestResolveRefColumnIllegalAE");
        QueryExecutor be5QueryExecutor = queryService.get(query, new HashMap<>());

        be5QueryExecutor.execute();
    }

    @Test(expected = Be5Exception.class)
    public void testResolveTypeOfRefColumnNPE()
    {
        Query query = meta.getQuery("testtable", "TestResolveRefColumnNPE");
        SqlQueryExecutor be5QueryExecutor = queryService.getSqlQueryBuilder(query, new HashMap<>());

        be5QueryExecutor.execute();
        assertEquals("", be5QueryExecutor.getFinalSql().getQuery().toString());
    }
}
