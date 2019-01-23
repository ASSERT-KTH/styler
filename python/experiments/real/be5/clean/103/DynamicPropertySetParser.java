package com.developmentontheedge.be5.query.sql;

import com.developmentontheedge.be5.database.sql.ResultSetParser;
import com.developmentontheedge.be5.database.sql.ResultSetWrapper;
import com.developmentontheedge.beans.DynamicPropertySet;
import com.developmentontheedge.beans.DynamicPropertySetSupport;

import java.sql.SQLException;

public class DynamicPropertySetParser implements ResultSetParser<DynamicPropertySet>
{
    @Override
    public DynamicPropertySet parse(ResultSetWrapper rs) throws SQLException
    {
        DynamicPropertySet dps = new DynamicPropertySetSupport();
        return DpsRecordAdapter.addDp(dps, rs);
    }
}
