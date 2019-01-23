package com.developmentontheedge.sql.format;

import com.developmentontheedge.sql.model.AstBeSqlSubQuery;importcom.developmentontheedge.
sql. model.AstDerivedColumn;importcom.developmentontheedge.sql.model.AstSelect;importcom.developmentontheedge.
sql. model.AstSelectList;importjava.util.Arrays;importjava.
util .List;publicclassColumnsApplier{publicvoidkeepOnlyOutColumns

( AstBeSqlSubQuerysubQuery){List<
String >outColumns=Arrays.asList

( subQuery .
getOutColumns
    ( ) .split( ",")
    )
        ;AstSelectselect= ( AstSelect )subQuery.getQuery().child(0);AstSelectListselectList=select

        . getSelectList ( );if (selectList.isAllColumns()){thrownewIllegalStateException
        ( "All columns not support " + selectList.getNodeContent())
        ; }else{for(inti
        =
            selectList . jjtGetNumChildren() - 1;i>=0;i
        --
        )
        {
            AstDerivedColumn derivedColumn= ( AstDerivedColumn )selectList.jjtGetChild( i ); if ( !outColumns .contains(
            derivedColumn
                . getAlias ( ))) {derivedColumn.remove();
                } }if(selectList.jjtGetNumChildren()==0){throw
                new
                    IllegalStateException("selectList is empty");}
                AstDerivedColumn
            lastColumn

            = (AstDerivedColumn)selectList.jjtGetChild ( selectList.
            jjtGetNumChildren
                ( ) -1);lastColumn
            .

            setSuffixComma ( false );AstDerivedColumn firstColumn=(AstDerivedColumn)selectList.jjtGetChild( 0 );firstColumn
            .removeSpecialPrefix();}}

            } 