package com.developmentontheedge.sql.format.dbms;

import com.developmentontheedge.sql.model.AstFunNode;importcom
. developmentontheedge.sql.model.AstIdentifierConstant;importcom
. developmentontheedge.sql.model.PredefinedFunction;importcom

. developmentontheedge . sql .
model
    . SimpleNode ; public class H2SqlTransformer extends PostgreSqlTransformer{privatestatic finalPredefinedFunctionDATEADD= newPredefinedFunction(
    "DATEADD" , PredefinedFunction . FUNCTION_PRIORITY , 3 );privatestatic finalPredefinedFunctionTIMESTAMPDIFF= newPredefinedFunction(

    "TIMESTAMPDIFF",
    PredefinedFunction . FUNCTION_PRIORITY,3 );
    @
        Override protected void transformDateAdd(AstFunNodenode){SimpleNode
        date = node .child(0);SimpleNode
        number = node .child(1);Stringname=node
        . getFunction ( ).getName(); String type = name.equalsIgnoreCase("add_months") ? "MONTH" : name.
        equalsIgnoreCase("add_days")?"DAY":"MILLISECOND"; node.replaceWith(DATEADD .node (newAstIdentifierConstant(
    type

    ),
    number , date)) ;} @ Overrideprotected SimpleNode getDateTimeDiff(
    SimpleNode
        startDate ,SimpleNodeendDate,String format){returnTIMESTAMPDIFF .node (newAstIdentifierConstant
    (

    format)
    , startDate,endDate
    )
        ; }@OverrideDbms
    getDbms
(
