package com.developmentontheedge.be5.metadata.sql.type;

import com.developmentontheedge.be5.metadata.model.ColumnDef;
import com.developmentontheedge.be5.metadata.model.DataElementUtils;
import com.developmentontheedge.be5.metadata.model.IndexColumnDef;
import com.developmentontheedge.be5.metadata.model.IndexDef;
import com.developmentontheedge.be5.metadata.model.SqlColumnType;
import com.developmentontheedge.be5.metadata.model.TableDef;
import com.developmentontheedge.be5.metadata.sql.Rdbms;
import com.developmentontheedge.dbms.ExtendedSqlException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PostgresTypeManagerTest extends BaseTypeManagerTest
{
    @Test
    public void testTypes() throws ExtendedSqlException
    {
        TableDef def = createTable(Rdbms.POSTGRESQL);
        addColumn(def, "a", SqlColumnType.TYPE_BLOB);
        addColumn(def, "b", SqlColumnType.TYPE_BIGTEXT);
        addColumn(def, "c", SqlColumnType.TYPE_UINT);
        addColumn(def, "d", SqlColumnType.TYPE_UBIGINT);
        addColumn(def, "e", SqlColumnType.TYPE_DATETIME);
        assertEquals("DROP TABLE IF EXISTS \"table\";\n" +
                "CREATE TABLE \"table\" (\n" +
                "a BYTEA NOT NULL,\n" +
                "b TEXT NOT NULL,\n" +
                "c INT NOT NULL,\n" +
                "d BIGINT NOT NULL,\n" +
                "e TIMESTAMP NOT NULL);\n", def.getDdl());
        TableDef def2 = (TableDef) def.clone(def.getOrigin(), def.getName());
        ColumnDef col = addColumn(def2, "f", SqlColumnType.TYPE_KEY);
        col.setAutoIncrement(true);
        col.setPrimaryKey(true);
        assertEquals("DROP SEQUENCE IF EXISTS table_f_seq;\n" +
                "CREATE SEQUENCE table_f_seq;\n" +
                "ALTER TABLE \"table\" ADD COLUMN f BIGINT DEFAULT nextval('table_f_seq'::regclass) PRIMARY KEY;\n", def2.getDiffDdl(def, null));
        assertEquals("ALTER TABLE \"table\" DROP COLUMN f;\n" +
                "DROP SEQUENCE IF EXISTS table_f_seq;\n" +
                "DROP INDEX IF EXISTS table_pkey;\n", def.getDiffDdl(def2, null));
    }

    @Test
    public void testJsonB()
    {
        TableDef def = createTable(Rdbms.POSTGRESQL);
        ColumnDef col = addColumn(def, "rec", SqlColumnType.TYPE_BIGINT);
        IndexDef idx = new IndexDef("recidx", def.getIndices());
        DataElementUtils.save(idx);
        IndexColumnDef ic = new IndexColumnDef("rec", idx);
        DataElementUtils.save(ic);
        assertEquals("DROP TABLE IF EXISTS \"table\";\n" +
                "CREATE TABLE \"table\" (\n" +
                "rec BIGINT NOT NULL);\n" +
                "CREATE INDEX recidx ON \"table\"(rec);\n", def.getDdl());
        col.setTypeString(SqlColumnType.TYPE_JSONB);
        assertEquals("DROP TABLE IF EXISTS \"table\";\n" +
                "CREATE TABLE \"table\" (\n" +
                "rec JSONB NOT NULL);\n" +
                "CREATE INDEX recidx ON \"table\" USING GIN (rec);\n", def.getDdl());
    }

    @Test
    public void testCorrectType()
    {
        DbmsTypeManager typeManager = Rdbms.POSTGRESQL.getTypeManager();
        assertTypeTranslation(typeManager, "bytea", "BLOB");
        assertTypeTranslation(typeManager, "int2", "SMALLINT");
        assertTypeTranslation(typeManager, "int4", "INT");
        assertTypeTranslation(typeManager, "int8", "BIGINT");
        assertTypeTranslation(typeManager, "bpchar", "CHAR(255)");
        assertTypeTranslation(typeManager, "timestamp", "TIMESTAMP");
    }
}
