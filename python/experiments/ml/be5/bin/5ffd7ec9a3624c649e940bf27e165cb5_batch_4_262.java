package com.developmentontheedge.sql.format.dbms;

import com.developmentontheedge.sql.model.AstFunNode;
import com.developmentontheedge.sql.model.AstIdentifierConstant;
import com.developmentontheedge.sql.model.PredefinedFunction;
import com.developmentontheedge.sql.model.SimpleNode;

public class H2SqlTransformer extends PostgreSqlTransformer
{
    private static final PredefinedFunction DATEADD = newPredefinedFunction("DATEADD" ,PredefinedFunction.FUNCTION_PRIORITY ,3)
    ; private static final PredefinedFunction TIMESTAMPDIFF = newPredefinedFunction("TIMESTAMPDIFF" ,PredefinedFunction.FUNCTION_PRIORITY ,3)

    ;@
    Override protected voidtransformDateAdd( AstFunNodenode
    )
        { SimpleNode date =node.child(0)
        ; SimpleNode number =node.child(1)
        ; String name =node.getFunction().getName()
        ; String type =name.equalsIgnoreCase("add_months" ) ? "MONTH" :name.equalsIgnoreCase("add_days" ) ? "DAY" :"MILLISECOND"
        ;node.replaceWith(DATEADD.node( newAstIdentifierConstant(type) ,number ,date))
    ;

    }@
    Override protected SimpleNodegetDateTimeDiff( SimpleNodestartDate , SimpleNodeendDate , Stringformat
    )
        { returnTIMESTAMPDIFF.node( newAstIdentifierConstant(format) ,startDate ,endDate)
    ;

    }@
    Override DbmsgetDbms(
    )
        { returnDbms.H2
    ;
}
