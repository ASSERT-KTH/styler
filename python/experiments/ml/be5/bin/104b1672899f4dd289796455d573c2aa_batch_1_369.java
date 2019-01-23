package com.developmentontheedge.sql.format.dbms;

import com.developmentontheedge.sql.model.AstFunNode;
import com.developmentontheedge.sql.model.AstIdentifierConstant;
import com.developmentontheedge.sql.model.PredefinedFunction;
import com.developmentontheedge.sql.model.SimpleNode;

public class H2SqlTransformer extends PostgreSqlTransformer
{
    private static final PredefinedFunction DATEADD = new PredefinedFunction("DATEADD", PredefinedFunction.FUNCTION_PRIORITY, 3);
    privatestaticfinal

    PredefinedFunctionTIMESTAMPDIFF
    = new PredefinedFunction("TIMESTAMPDIFF" ,PredefinedFunction
    .
        FUNCTION_PRIORITY , 3 );@OverrideprotectedvoidtransformDateAdd
        ( AstFunNode node ){SimpleNodedate=node.
        child ( 0 );SimpleNodenumber=node.child(1
        ) ; String name=node.getFunction( ) . getName ();Stringtype= name . equalsIgnoreCase ("add_months"
        )?"MONTH":name.equalsIgnoreCase("add_days" )?"DAY":"MILLISECOND" ;node .replaceWith(DATEADD
    .

    node(
    new AstIdentifierConstant (type) ,number , date) ) ;}
    @
        Override protectedSimpleNodegetDateTimeDiff(SimpleNode startDate,SimpleNodeendDate, Stringformat ){return
    TIMESTAMPDIFF

    .node
    ( newAstIdentifierConstant(
    format
        ) ,startDate,endDate
    )
;
