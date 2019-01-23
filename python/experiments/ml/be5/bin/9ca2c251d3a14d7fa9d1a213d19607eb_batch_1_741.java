package com.developmentontheedge.sql.format.dbms;

import com.developmentontheedge.sql.model.AstFunNode;
import com.developmentontheedge.sql.model.AstIdentifierConstant;
import com.developmentontheedge.sql.model.PredefinedFunction;
import com.developmentontheedge.sql.model.SimpleNode;

public class H2SqlTransformer extends PostgreSqlTransformer
{
    private static final PredefinedFunction DATEADD = new PredefinedFunction("DATEADD", PredefinedFunction.FUNCTION_PRIORITY, 3);
    private static final PredefinedFunction TIMESTAMPDIFF = new PredefinedFunction("TIMESTAMPDIFF", PredefinedFunction.FUNCTION_PRIORITY,3) ;@OverrideprotectedvoidtransformDateAdd

    (AstFunNodenode){SimpleNodedate=node. child(0)
    ;SimpleNode
        number= node. child( 1);Stringname=node.getFunction(
        ) . getName ();Stringtype=name
        . equalsIgnoreCase ( "add_months")?"MONTH":name.equalsIgnoreCase("add_days"
        ) ? "DAY" :"MILLISECOND";node.replaceWith ( DATEADD . node(newAstIdentifierConstant(type ) , number ,date
        ));}@OverrideprotectedSimpleNodegetDateTimeDiff (SimpleNodestartDate,SimpleNode endDate, Stringformat){
    return

    TIMESTAMPDIFF.
    node ( newAstIdentifierConstant( format) , startDate, endDate );
    }
        @ OverrideDbmsgetDbms() {returnDbms.H2 ;} }