package com.developmentontheedge.be5.metadata.sql.schema;

import com.developmentontheedge.be5.metadata.exception.ProcessInterruptedException;
import com.developmentontheedge.be5.metadata.sql.pojo.IndexInfo;
import com.developmentontheedge.be5.metadata.sql.pojo.SqlColumnInfo;
import com.developmentontheedge.be5.metadata.util.ProcessController;
import com.developmentontheedge.dbms.ExtendedSqlException;
import com.developmentontheedge.dbms.SqlExecutor;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface DbmsSchemaReader
{
    String getDefaultSchema(SqlExecutor sql) throws ExtendedSqlException;

    Map<String, String> readTableNames(SqlExecutor sql, String defSchema, ProcessController controller) throws SQLException, ProcessInterruptedException;

    Map<String, List<SqlColumnInfo>> readColumns(SqlExecutor sql, String defSchema, ProcessController controller) throws SQLException, ProcessInterruptedException;

    Map<String, List<IndexInfo>> readIndices(SqlExecutor sql, String defSchema, ProcessController controller) throws SQLException, ProcessInterruptedException;
}
