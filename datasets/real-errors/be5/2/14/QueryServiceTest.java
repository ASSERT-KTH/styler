package com.developmentontheedge.be5.query.services;

import com.developmentontheedge.be5.base.exceptions.Be5Exception;
import com.developmentontheedge.be5.base.services.ProjectProvider;
import com.developmentontheedge.be5.database.DbService;
import com.developmentontheedge.be5.metadata.RoleType;
import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.be5.query.QueryBe5ProjectDBTest;
import com.developmentontheedge.be5.query.QueryExecutor;
import com.developmentontheedge.be5.query.SqlQueryExecutor;
import com.developmentontheedge.beans.DynamicPropertySet;
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
    private ProjectProvider projectProvider;
    @Inject
    private DbService db;
    @Inject
    private QueryExecutorFactory queryService;

    private Query query;

    @Before
    public void insertOneRow()
    {
        setStaticUserInfo(RoleType.ROLE_GUEST);
        query = meta.getQuery("testtable", "All records");
        db.update("delete from testtable");
        db.insert("insert into testtable (name, value) VALUES (?, ?)",
                "testBe5QueryExecutor", "1");
    }

    @Test
    public void testExecute()
    {
        List<DynamicPropertySet> dps = queryService.get(query, emptyMap()).execute();
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
    public void testMultipleColumn()
    {
        query = projectProvider.get().getEntity("testtable").getQueries().get("TestMultipleColumn");

        assertEquals("SELECT *\n" +
                "FROM testtable\n" +
                "WHERE name IN ('test1', 'test2') LIMIT 2147483647", queryService.
                getSqlQueryBuilder(query, Collections.singletonMap("name", Arrays.asList("test1", "test2"))).getFinalSql().getQuery().toString());
    }

    @Test
    public void testMultipleColumnLong()
    {
        query = projectProvider.get().getEntity("testtable").getQueries().get("TestMultipleColumnLong");

        assertEquals("SELECT *\n" +
                "FROM testtable\n" +
                "WHERE ID IN (1, 2) LIMIT 2147483647", queryService.
                getSqlQueryBuilder(query, Collections.singletonMap("ID", Arrays.asList("1", "2"))).getFinalSql().getQuery().toString());
    }

    @Test
    public void testResolveTypeOfRefColumn()
    {
        query = projectProvider.get().getEntity("testtable").getQueries().get("TestResolveRefColumn");

        assertEquals("SELECT *\n" +
                "FROM testtable\n" +
                "WHERE name = 'test' LIMIT 2147483647", queryService.
                getSqlQueryBuilder(query, Collections.singletonMap("name", "test")).getFinalSql().getQuery().toString());
    }

    @Test
    @Ignore
    public void testTestResolveRefColumnByAlias()
    {
        query = projectProvider.get().getEntity("testtable").getQueries().get("TestResolveRefColumnByAlias");

        assertEquals("SELECT *\n" +
                "FROM testtable t\n" +
                "WHERE name = 'test' LIMIT 2147483647", queryService.
                getSqlQueryBuilder(query, Collections.singletonMap("name", "test")).getFinalSql().getQuery().toString());
    }

    @Test
    public void testIgnoreUnknownColumn()
    {
        query = projectProvider.get().getEntity("testtable").getQueries().get("TestResolveRefColumn");
        List<DynamicPropertySet> list = queryService.get(query, Collections.singletonMap("unknownColumn", "test")).execute();
        assertEquals(list.size(), 0);
    }

    @Test(expected = Be5Exception.class)
    public void testResolveTypeOfRefColumnError()
    {
        query = projectProvider.get().getEntity("testtable").getQueries().get("TestResolveRefColumnIllegalAE");
        QueryExecutor be5QueryExecutor = queryService.get(query, new HashMap<>());

        be5QueryExecutor.execute();
    }

    @Test(expected = Be5Exception.class)
    public void testResolveTypeOfRefColumnNPE()
    {
        query = projectProvider.get().getEntity("testtable").getQueries().get("TestResolveRefColumnNPE");
        SqlQueryExecutor be5QueryExecutor = queryService.getSqlQueryBuilder(query, new HashMap<>());

        be5QueryExecutor.execute();
        assertEquals("", be5QueryExecutor.getFinalSql().getQuery().toString());
    }
}
