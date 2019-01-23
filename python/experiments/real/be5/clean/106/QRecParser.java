package com.developmentontheedge.be5.query.sql;

import com.developmentontheedge.be5.database.sql.ResultSetParser;
import com.developmentontheedge.be5.database.sql.ResultSetWrapper;
import com.developmentontheedge.be5.query.model.beans.QRec;

import java.sql.SQLException;

public class QRecParser implements ResultSetParser<QRec>
{
    @Override
    public QRec parse(ResultSetWrapper rs) throws SQLException
    {
        QRec dps = new QRec();
        return DpsRecordAdapter.addDp(dps, rs);
    }
}
