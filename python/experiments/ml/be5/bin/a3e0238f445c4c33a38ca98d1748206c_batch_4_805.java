package com.developmentontheedge.sql.format.dbms;

import com.developmentontheedge.sql.model
.AstFunNode;importcom
. developmentontheedge.sql.model.AstIdentifierConstant;importcom
. developmentontheedge.sql.model.PredefinedFunction;importcom
. developmentontheedge.sql.model.SimpleNode;publicclass

H2SqlTransformer extends PostgreSqlTransformer { private
static
    final PredefinedFunction DATEADD = new PredefinedFunction ( "DATEADD",PredefinedFunction. FUNCTION_PRIORITY,3) ;privatestatic
    final PredefinedFunction TIMESTAMPDIFF = new PredefinedFunction ( "TIMESTAMPDIFF",PredefinedFunction. FUNCTION_PRIORITY,3) ;@Override

    protectedvoid
    transformDateAdd ( AstFunNodenode) {SimpleNode
    date
        = node . child(0);SimpleNodenumber
        = node . child(1);Stringname
        = node . getFunction().getName();Stringtype
        = name . equalsIgnoreCase("add_months")?"MONTH" : name . equalsIgnoreCase("add_days")?"DAY" : "MILLISECOND" ; node.
        replaceWith(DATEADD.node(newAstIdentifierConstant( type),number, date) );}@
    Override

    protectedSimpleNode
    getDateTimeDiff ( SimpleNodestartDate, SimpleNodeendDate , Stringformat ) {return
    TIMESTAMPDIFF
        . node(newAstIdentifierConstant( format),startDate, endDate) ;}@
    Override

    DbmsgetDbms
    ( ){return
    Dbms
        . H2;}}
    