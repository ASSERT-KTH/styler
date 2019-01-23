package com.developmentontheedge.sql.format;

import com.developmentontheedge.sql.model.AstBeSqlSubQuery;
import com.developmentontheedge.sql.model.AstDerivedColumn;
import com.developmentontheedge.sql.model.AstSelect;import
com. developmentontheedge.sql.model.AstSelectList;importjava.util.Arrays;importjava.util

.List;publicclass

ColumnsApplier{ publicvoidkeepOnlyOutColumns(AstBeSqlSubQuerysubQuery){List<String>
outColumns= Arrays.asList(subQuery

. getOutColumns (
)
    . split (",") );
    AstSelect
        select=(AstSelect ) subQuery .getQuery().child(0);AstSelectListselectList=select.getSelectList

        ( ) ; if(selectList .isAllColumns()){thrownewIllegalStateException("All columns not support "
        + selectList . getNodeContent());}
        else {for(inti=selectList
        .
            jjtGetNumChildren ( )-1 ; i>=0;i--)
        {
        AstDerivedColumn
        derivedColumn
            = (AstDerivedColumn ) selectList .jjtGetChild(i) ; if( ! outColumns .contains (derivedColumn.
            getAlias
                ( ) ) ){derivedColumn .remove();}}
                if (selectList.jjtGetNumChildren()==0){thrownewIllegalStateException
                (
                    "selectList is empty");}AstDerivedColumnlastColumn
                =
            (

            AstDerivedColumn )selectList.jjtGetChild(selectList . jjtGetNumChildren(
            )
                - 1 );lastColumn.setSuffixComma
            (

            false ) ; AstDerivedColumnfirstColumn= (AstDerivedColumn)selectList.jjtGetChild(0) ; firstColumn.removeSpecialPrefix
            ();}}}