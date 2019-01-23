package com.developmentontheedge.sql.format.dbms;import

com. developmentontheedge.sql.model.AstFunNode;importcom.developmentontheedge.sql.model
.AstIdentifierConstant ;importcom.developmentontheedge.sql.model.PredefinedFunction;importcom.developmentontheedge.sql
. model.SimpleNode;publicclassH2SqlTransformerextendsPostgreSqlTransformer{
private staticfinalPredefinedFunctionDATEADD=newPredefinedFunction("DATEADD",

PredefinedFunction . FUNCTION_PRIORITY , 3
)
    ; private static final PredefinedFunction TIMESTAMPDIFF = newPredefinedFunction("TIMESTAMPDIFF" ,PredefinedFunction.FUNCTION_PRIORITY ,3)
    ; @ Override protected void transformDateAdd ( AstFunNodenode){ SimpleNodedate=node .child(

    0)
    ; SimpleNode number=node .child
    (
        1 ) ; Stringname=node.getFunction(
        ) . getName ();Stringtype=name
        . equalsIgnoreCase ( "add_months")?"MONTH":name.equalsIgnoreCase("add_days"
        ) ? "DAY" :"MILLISECOND";node.replaceWith ( DATEADD . node(newAstIdentifierConstant(type ) , number ,date
        ));}@OverrideprotectedSimpleNodegetDateTimeDiff (SimpleNodestartDate,SimpleNode endDate, Stringformat){
    return

    TIMESTAMPDIFF.
    node ( newAstIdentifierConstant( format) , startDate, endDate );
    }
        @ OverrideDbmsgetDbms() {returnDbms.H2 ;} }