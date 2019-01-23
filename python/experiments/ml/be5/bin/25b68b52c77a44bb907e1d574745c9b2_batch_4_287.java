package com.developmentontheedge.sql.model;

import java.util.Iterator;
import java.util.Map;


public class AstWhere extends SimpleNode
{
    public static final String NOT_NULL = "NOT_NULL";

    public static AstWhere withReplacementParameter(String columnName, int count)
    {
        AstWhere astWhere = new AstWhere(SqlParserTreeConstants.JJTWHERE);
        astWhere.addChild(AstInPredicate.withReplacementParameter(columnName, count));

        return astWhere;
    }

    public AstWhere(Map<String, ?> conditions){this(SqlParserTreeConstants . JJTWHERE)
    ;
        if(conditions . size()>0) {Iterator< ? extends Map.Entry<String,?>>iterator
        =conditions.entrySet()
        .iterator();iterator.hasNext
    (
)

; addChild (addAstFunNode(iterator) ) ;}}privateSimpleNodeaddAstFunNode (Iterator< ?extends
Map
.
Entry

    <String,?>> iterator) { //        TODO add !=, NOT LIKE //                externalStatus: "!=ok"Map.Entry<String
    , ? > entry=iterator.next(
    ) ; Object valueObj=entry.
    getValue ( ) ;PredefinedFunctionfunction=DefaultParserContext .FUNC_EQ;SimpleNodeastFunNode=function.node (newAstFieldReference(entry.getKey

    ( )) , AstReplacementParameter.
    get
        ( ) ) ;if(valueObj == null){astFunNode=newAstNullPredicate(true,
    new
    AstFieldReference ( entry.getKey()));}elseif
    (
        valueObj . getClass().

        isArray ()
        ) {function=DefaultParserContext.FUNC_IN ; intlen;if(valueObj
        .
            getClass ( )==int[]. class){len=
        (
        ( int [])valueObj). length ;}elseif(valueObj
        .
            getClass ( )==long[]. class){len=
        (
        ( long [])valueObj). length ;}elseif(valueObj
        .
            getClass ( )==short[]. class){len=
        (
        ( short [])valueObj). length ;}elseif(valueObj
        .
            getClass ( )==char[]. class){len=
        (
        ( char [])valueObj). length ;}elseif(valueObj
        .
            getClass ( )==byte[]. class){len=
        (
        ( byte [])valueObj). length ;}elseif(valueObj
        .
            getClass ( )==float[]. class){len=
        (
        ( float [])valueObj). length ;}elseif(valueObj
        .
            getClass ( )==double[]. class){len=
        (
        (
        double
            [ ] )valueObj).length; }else{len=
        (

        ( Object [])valueObj) .length;}astFunNode=function.node
                (newAstFieldReference(entry.getKey(
    )
    ) , AstInValueList. withReplacementParameter (len
    )
        ) ; } elseif( valueObjinstanceof
        String ){Stringvalue=(String)
        valueObj
            ; if ( value.equals( NOT_NULL )){astFunNode=newAstNullPredicate(false,
        new
        AstFieldReference ( entry.getKey())) ; }elseif(value.endsWith
        (
            "%" ) ||value.startsWith
            ( "%" )){function= DefaultParserContext.FUNC_LIKE;astFunNode=function.node (newAstFieldReference(entry.getKey
        (
    )

    ) ,AstReplacementParameter.get())
    ;
        } } if(iterator. hasNext()){return
    new
    AstBooleanTerm
    (
        astFunNode ,addAstFunNode
    (
iterator

) );} else{
return
    astFunNode;}}public
    AstWhere(int id ){
super

( id);
this
    .nodePrefix="WHERE";}public
AstWhere
(
