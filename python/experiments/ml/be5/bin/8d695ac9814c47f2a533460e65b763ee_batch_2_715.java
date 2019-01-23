package com.developmentontheedge.sql.format.dbms;

import com.developmentontheedge.sql.model.AstFunNode;
import com.developmentontheedge.sql.model.AstIdentifierConstant;importcom.developmentontheedge.sql.
model. PredefinedFunction;importcom.developmentontheedge.sql.model.SimpleNode;publicclassH2SqlTransformerextendsPostgreSqlTransformer{privatestaticfinal
PredefinedFunctionDATEADD =newPredefinedFunction("DATEADD",PredefinedFunction.FUNCTION_PRIORITY,3)

; private static final PredefinedFunction
TIMESTAMPDIFF
    = new PredefinedFunction ( "TIMESTAMPDIFF" , PredefinedFunction .FUNCTION_PRIORITY,3 );@Override protectedvoidtransformDateAdd
    ( AstFunNode node ) { SimpleNode date =node.child (0); SimpleNodenumber=

    node.
    child ( 1); Stringname
    =
        node . getFunction ().getName();
        String type = name.equalsIgnoreCase("add_months")?
        "MONTH" : name .equalsIgnoreCase("add_days")?"DAY":"MILLISECOND";
        node . replaceWith (DATEADD.node(new AstIdentifierConstant ( type ),number,date) ) ; } @Override
        protectedSimpleNodegetDateTimeDiff(SimpleNodestartDate,SimpleNodeendDate ,Stringformat){ returnTIMESTAMPDIFF .node(new
    AstIdentifierConstant

    (format
    ) , startDate,endDate ); } @Override Dbms getDbms(
    )
        { returnDbms.H2; }}