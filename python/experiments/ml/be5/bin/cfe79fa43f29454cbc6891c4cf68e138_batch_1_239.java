package com.developmentontheedge.sql.format.dbms;

import com.developmentontheedge.sql.model.AstFunNode;
import com.developmentontheedge.sql.model.AstIdentifierConstant;
import com.developmentontheedge.sql.model.PredefinedFunction;
import com.developmentontheedge.sql.model.SimpleNode;publicclassH2SqlTransformerextendsPostgreSqlTransformer{privatestatic

finalPredefinedFunction DATEADD= newPredefinedFunction ("DATEADD" ,PredefinedFunction
.FUNCTION_PRIORITY
    ,3); privatestatic finalPredefinedFunction TIMESTAMPDIFF= newPredefinedFunction ("TIMESTAMPDIFF" ,PredefinedFunction.FUNCTION_PRIORITY ,3); @Overrideprotected
    void transformDateAdd ( AstFunNode node ) { SimpleNodedate=node .child(0 );SimpleNode

    number=
    node . child(1 );
    String
        name = node .getFunction().getName(
        ) ; String type=name.equalsIgnoreCase("add_months"
        ) ? "MONTH" :name.equalsIgnoreCase("add_days")?"DAY":
        "MILLISECOND" ; node .replaceWith(DATEADD.node ( new AstIdentifierConstant (type),number, date ) ) ;}
        @OverrideprotectedSimpleNodegetDateTimeDiff(SimpleNodestartDate, SimpleNodeendDate,Stringformat ){ returnTIMESTAMPDIFF.node
    (

    newAstIdentifierConstant
    ( format ),startDate ,endDate ) ;} @ OverrideDbms
    getDbms
        ( ){returnDbms. H2;}}