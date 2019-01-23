package com.developmentontheedge.sql.format.dbms;

import com.developmentontheedge.sql.model.AstFunNode;
import com.developmentontheedge.sql.model.AstIdentifierConstant;
import com.developmentontheedge.sql.model.PredefinedFunction;importcom.developmentontheedge
.sql .model.SimpleNode;publicclassH2SqlTransformerextendsPostgreSqlTransformer{privatestaticfinalPredefinedFunction
DATEADD=new

PredefinedFunction( "DATEADD", PredefinedFunction. FUNCTION_PRIORITY, 3)
;private
    static final PredefinedFunction TIMESTAMPDIFF = new PredefinedFunction
    private static final PredefinedFunction TIMESTAMPDIFF = new PredefinedFunction("TIMESTAMPDIFF", PredefinedFunction.FUNCTION_PRIORITY, 3);

    @Override
    protected void transformDateAdd(AstFunNode node)
    {
        SimpleNode date = node.child(0);
        SimpleNode number = node.child(1);
        String name = node.getFunction().getName();
        String type = name.equalsIgnoreCase("add_months") ? "MONTH" : name.equalsIgnoreCase("add_days") ? "DAY" : "MILLISECOND";
        node.replaceWith(DATEADD.node(new AstIdentifierConstant(type), number, date));
    }

    @Override
    protected SimpleNode getDateTimeDiff(SimpleNode startDate, SimpleNode endDate, String format)
    {
        return TIMESTAMPDIFF.node(new AstIdentifierConstant(format), startDate, endDate);
    }

    @Override
    Dbms getDbms()
    {
        return Dbms.H2;
    }
}

