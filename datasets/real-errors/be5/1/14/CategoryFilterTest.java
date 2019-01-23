package com.developmentontheedge.be5.query.util;

import com.developmentontheedge.sql.model.AstStart;
import com.developmentontheedge.sql.model.SqlQuery;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class CategoryFilterTest
{
    @Test
    public void testCategoryFilter()
    {
        AstStart start = SqlQuery.parse("SELECT t.a, q.b FROM myTable t, otherTable q JOIN oneMoreTable a ON (a.ID=q.ID) WHERE t.b > 2");
        new CategoryFilter("myTable", "ID", 123).apply(start);

        assertEquals("SELECT t.a, q.b FROM myTable t "
                + "INNER JOIN classifications ON classifications.categoryID = 123 "
                + "AND classifications.recordID ='myTable.'|| t.ID, otherTable q "
                + "INNER JOIN oneMoreTable a ON (a.ID = q.ID) "
                + "WHERE t.b > 2", start.format());
    }
}
