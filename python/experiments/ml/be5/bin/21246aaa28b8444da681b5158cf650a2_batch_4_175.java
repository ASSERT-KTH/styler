package com.developmentontheedge.sql.format.dbms;

import com.developmentontheedge.sql.model.AstFunNode;
import com.developmentontheedge.sql.model.AstIdentifierConstant;
import com.developmentontheedge.sql.model.PredefinedFunction;
import com.developmentontheedge.sql.model.SimpleNode;

public class H2SqlTransformer extends PostgreSqlTransformer{
privatestatic
    finalPredefinedFunction DATEADD= newPredefinedFunction ("DATEADD" ,PredefinedFunction .FUNCTION_PRIORITY ,3 );privatestaticfinalPredefinedFunctionTIMESTAMPDIFF= newPredefinedFunction("TIMESTAMPDIFF",PredefinedFunction.FUNCTION_PRIORITY

        ,3);@Override
        protectedvoid transformDateAdd( AstFunNodenode ){ SimpleNodedate =node .child (0); SimpleNodenumber=node .child(

        1)
        ; String name=node .getFunction
        (
            ) . getName ();Stringtype=name
            . equalsIgnoreCase ( "add_months")?"MONTH":name.
            equalsIgnoreCase ( "add_days" )?"DAY":"MILLISECOND";node.replaceWith(
            DATEADD . node (newAstIdentifierConstant(type) , number , date));}@ Override protected SimpleNode getDateTimeDiff(
            SimpleNodestartDate,SimpleNodeendDate,Stringformat) {returnTIMESTAMPDIFF.node (new AstIdentifierConstant(format)
        ,

        startDate,
        endDate ) ;}@ OverrideDbms getDbms () { returnDbms
        .
            H2 ;}}