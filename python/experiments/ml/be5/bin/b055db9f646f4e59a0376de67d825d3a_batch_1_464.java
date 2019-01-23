package com.developmentontheedge.sql.format.dbms;

import com.developmentontheedge.
sql .model.AstFunNode;importcom.developmentontheedge.
sql .model.AstIdentifierConstant;importcom.developmentontheedge.
sql .model.PredefinedFunction;importcom.developmentontheedge.

sql . model . SimpleNode
;
    public class H2SqlTransformer extends PostgreSqlTransformer { private staticfinalPredefinedFunctionDATEADD =newPredefinedFunction( "DATEADD",PredefinedFunction
    . FUNCTION_PRIORITY , 3 ) ; private staticfinalPredefinedFunctionTIMESTAMPDIFF =newPredefinedFunction( "TIMESTAMPDIFF",PredefinedFunction

    .FUNCTION_PRIORITY
    , 3 );@ Overrideprotected
    void
        transformDateAdd ( AstFunNode node){SimpleNodedate=node
        . child ( 0);SimpleNodenumber=node
        . child ( 1);Stringname=node.getFunction(
        ) . getName ();Stringtype= name . equalsIgnoreCase ("add_months")?"MONTH": name . equalsIgnoreCase ("add_days"
        )?"DAY":"MILLISECOND";node.replaceWith (DATEADD.node( newAstIdentifierConstant (type),
    number

    ,date
    ) ) ;}@ Overrideprotected SimpleNode getDateTimeDiff( SimpleNode startDate,
    SimpleNode
        endDate ,Stringformat){ returnTIMESTAMPDIFF.node( newAstIdentifierConstant (format)
    ,

    startDate,
    endDate );}
    @
        Override DbmsgetDbms()
    {
return
