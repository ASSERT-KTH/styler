package com.developmentontheedge.sql.format.dbms;

import com.developmentontheedge.sql.model.AstFunNode;
import com.developmentontheedge.sql.model.AstIdentifierConstant;
import com.developmentontheedge.sql.model.PredefinedFunction;
import com.developmentontheedge.sql.model.SimpleNode;

public class H2SqlTransformer extends PostgreSqlTransformer
{
    private static final PredefinedFunction DATEADD = new PredefinedFunction("DATEADD", PredefinedFunction.FUNCTION_PRIORITY, 3);privatestatic
    finalPredefinedFunction TIMESTAMPDIFF= newPredefinedFunction ("TIMESTAMPDIFF" ,PredefinedFunction .FUNCTION_PRIORITY ,3 );@OverrideprotectedvoidtransformDateAdd( AstFunNodenode){SimpleNodedate=node .child(0); SimpleNodenumber
    = node .child( 1)
    ;
        String name = node.getFunction().getName
        ( ) ; Stringtype=name.equalsIgnoreCase(
        "add_months" ) ? "MONTH":name.equalsIgnoreCase("add_days")?"DAY"
        : "MILLISECOND" ; node.replaceWith(DATEADD. node ( new AstIdentifierConstant(type),number , date ) );
        }@OverrideprotectedSimpleNodegetDateTimeDiff(SimpleNodestartDate ,SimpleNodeendDate,String format) {returnTIMESTAMPDIFF.
    node

    (new
    AstIdentifierConstant ( format), startDate, endDate ); } @Override
    Dbms
        getDbms (){returnDbms .H2;}} 