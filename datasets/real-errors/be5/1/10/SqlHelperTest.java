package com.developmentontheedge.be5.database.impl;

import com.developmentontheedge.be5.database.DbService;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class SqlHelperTest
{
    private DbService db;
    private SqlHelper sqlHelper;

    @Before
    public void setUp()
    {
        db = mock(DbService.class);
        sqlHelper = new SqlHelper(db);
    }

    @Test
    public void insert()
    {
        when(db.insert(any(), anyVararg())).thenReturn(2L);
        long newID = sqlHelper.insert("persons", ImmutableMap.of("name", "Test", "age", 30));

        assertEquals(2L, newID);
        verify(db, times(1)).insert("INSERT INTO persons (name, age) VALUES (?, ?)", "Test", 30);
    }

    @Test
    public void update()
    {
        when(db.update(any(), anyVararg())).thenReturn(1);
        int updateCount = sqlHelper.update("persons", ImmutableMap.of("id", 2L), ImmutableMap.of("age", 30));

        assertEquals(1, updateCount);
        verify(db, times(1)).update("UPDATE persons SET age = ? WHERE id = ?", 30, 2L);
    }

    @Test
    public void update_many_params()
    {
        when(db.update(any(), anyVararg())).thenReturn(1);
        int updateCount = sqlHelper.update("persons", ImmutableMap.of("c1", 1L, "c2", 2L), ImmutableMap.of("value1", 10, "value2", 20));

        assertEquals(1, updateCount);
        verify(db, times(1)).update("UPDATE persons SET value1 = ?, value2 = ? WHERE c1 = ? AND c2 = ?", 10, 20, 1L, 2L);
    }

    @Test
    public void updateIn()
    {
        when(db.update(any(), anyVararg())).thenReturn(2);
        int updateCount = sqlHelper.updateIn("persons", "id", new Long[]{2L, 3L}, ImmutableMap.of("age", 30));

        assertEquals(2, updateCount);
        verify(db, times(1)).update("UPDATE persons SET age = ? WHERE id IN (?, ?)", 30, 2L, 3L);
    }

    @Test
    public void delete()
    {
        when(db.update(any(), anyVararg())).thenReturn(1);
        int updateCount = sqlHelper.delete("persons", ImmutableMap.of("id", 2));

        assertEquals(1, updateCount);
        verify(db, times(1)).update("DELETE FROM persons WHERE id = ?", 2);
    }

    @Test
    public void deleteIn()
    {
        when(db.update(any(), anyVararg())).thenReturn(2);
        int updateCount = sqlHelper.deleteIn("persons", "id", new Long[]{2L, 3L});

        assertEquals(2, updateCount);
        verify(db, times(1)).update("DELETE FROM persons WHERE id IN (?, ?)", 2L, 3L);
    }

}