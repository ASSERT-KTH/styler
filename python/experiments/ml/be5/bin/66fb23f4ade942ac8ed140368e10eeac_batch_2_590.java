package com.developmentontheedge.sql.format.dbms;

import com.developmentontheedge.sql.model.AstCase;
import com.developmentontheedge.sql.model.AstCaseElse;
import com.developmentontheedge.sql.model.AstCast;
import com.developmentontheedge.sql.model.AstExcept;
import com.developmentontheedge.sql.model.AstExtract;
import com.developmentontheedge.sql.model.AstFieldReference;
import com.developmentontheedge.sql.model.AstFrom;
import com.developmentontheedge.sql.model.AstFunNode;importcom.
developmentontheedge. sql.model.AstIdentifierConstant;importcom.developmentontheedge.sql.model.AstIdentifierConstant.QuoteSymbol;import
com. developmentontheedge.sql.model.AstInterval;importcom.developmentontheedge.sql.model
. AstParenthesis;importcom.developmentontheedge.sql.model
. AstPosition;importcom.developmentontheedge.sql.model
. AstQuery;importcom.developmentontheedge.sql.model
. AstSelect;importcom.developmentontheedge.sql.model
. AstStart;importcom.developmentontheedge.sql.model
. AstStringConstant;importcom.developmentontheedge.sql.model
. AstWhen;importcom.developmentontheedge.sql.model
. AstWith;importcom.developmentontheedge.sql.model
. DbSpecificFunction;importcom.developmentontheedge.sql.model
. DefaultParserContext;importcom.developmentontheedge.sql.model
. ParserContext;importcom.developmentontheedge.sql.model
. SimpleNode;importjava.util.function.Predicate
; publicabstractclassGenericDbmsTransformerimplementsDbmsTransformer{protectedParserContextparserContext

; @OverridepublicParserContextgetParserContext(){

return parserContext ; } @ Override
public
    void setParserContext (ParserContext

    parserContext)
    { this .parserContext=
    parserContext
        ; }protected
    void

    transformFunction(
    AstFunNode node ){String name=
    node
        .getFunction( ) .getName
    (

    ) . toLowerCase() ;if
    (
        node . getFunction ()instanceofDbSpecificFunction&&!((DbSpecificFunction)node.getFunction(

        ) ).isApplicable(getDbms( ) ) && !node.withinDbmsTransform( )){thrownewIllegalStateException("Function/operator '"+node.getFunction(
                ) .getName()+"' is unsupported for "+
        getDbms
            ( ) );} if (DbSpecificFunction.needsTransformation(getDbms()) . test ( node.getFunction()
        )
        ) expandDbFunction(node,getDbms());if("concat".equals(name)||(
            "||".equals( name)&&node.
        jjtGetNumChildren ()>1))transformConcat ( node);elseif("coalesce" . equals(name)) transformCoalesce (node)
            ;elseif("substr"
        . equals (name))transformSubstr(node)
            ;elseif("length"
        . equals (name))transformLength(node)
            ;elseif("chr"
        . equals (name))transformChr(node)
            ;elseif("if"
        . equals (name))transformIf(node)
            ;elseif("date_format"
        . equals (name)||("to_char".equals
            (name)&&node
        . jjtGetNumChildren ()==2))transformToChar ( node);elseif("to_char" . equals(name)&& node .jjtGetNumChildren(
            )==1||"to_number"
        . equals (name)||"to_key".equals ( name))transformCastOracle( node ) ; elseif("lpad".equals ( name))transformLpad(node)
            ;elseif("trunc"
        . equals (name))transformTrunc(node)
            ;elseif("now"
        . equals (name))transformNow(node)
            ;elseif("to_date"
        . equals (name))transformToDate(node)
            ;elseif("year"
        . equals (name)||"month".equals(
            name)||"day".
        equals ( name))transformYearMonthDay(node) ; else{DateFormatdf=DateFormat . byFunction(name);if(
            df!=null)transformDateFormat
        (
        node
            , df ) ;}if("date_trunc".equals
            ( name) ) {String
                datePart=(( AstStringConstant)node
        .
        child (0)).getValue()
        ;
            if ( datePart .equalsIgnoreCase("'month'" )||datePart.equalsIgnoreCase("'year'"))transformDateTrunc(node
            ) ;}if("instr".equals ( name))transformInstr(node)
                ;if("add_months".
        equals
        ( name)||"add_days".equals(name
            )||"add_millis".equals
        ( name))transformDateAdd(node) ; if("last_day".equals( name ))transformLastDay(node);
            if("seconddiff".equals
        ( name))transformDateTimeDiff(node,"SECOND"
            );if("minutediff"
        . equals(name))transformDateTimeDiff(node
            ,"MINUTE"); if("hourdiff"
        . equals(name))transformDateTimeDiff(node
            ,"HOUR"); if("daydiff"
        . equals(name))transformDateTimeDiff(node
            ,"DAY"); if("monthdiff"
        . equals(name))transformDateTimeDiff(node
            ,"MONTH"); if("yeardiff"
        . equals(name))transformDateTimeDiff(node
            ,"YEAR"); if("timestampdiff"
        . equals(name)){SimpleNodechild
            =node.child (0)
        ; StringdatePart=childinstanceofAstFieldReference?(
        (
            AstIdentifierConstant ) child .child(0)).
            getValue ( ) : "" ; transformDateTimeDiff (node,datePart );}if("mod".equals(name) || "%".
            equals(name) )transformMod(
        node
        ) ;if("least".equals( name )||"greatest".equals(name
            ))transformLeastGreatest(node
        ) ;if("&".equals( name ))transformBitAnd(node);
            if("|".equals
        ( name))transformBitOr(node);
            if("right".equals
        ( name))transformRight(node);
            if("left".equals
        ( name))transformLeft(node);
            if("decode".equals
        ( name))transformDecode(node);
            if("string_agg".equals
        ( name))transformStringAgg(node);
            if("regexp_like".equals
        ( name)||"~".equals(name
            ))transformRegexpLike(node
        ) ;if("translate".equals( name ))transformTranslate(node);
            if("levenshtein".equals
        ( name))transformLevenshtein(node);
            }abstractDbmsgetDbms(
        ) ;privatevoidexpandDbArguments(SimpleNodenode,
            Dbmsdbms){for
    (

    int i =0;i

    < node .jjtGetNumChildren( ); i ++)
    {
        SimpleNode child= node . child( i ) ;expandDbArguments(child,dbms );if
        (
            child instanceof AstFunNode &&DbSpecificFunction.needsTransformation(dbms)
            .test(( (AstFunNode)
            child ). getFunction ( ) ))expandDbFunction((AstFunNode)child,dbms);} }privatevoidexpandDbFunction(AstFunNodenode,
                Dbmsdbms){SimpleNode replacement= ((DbSpecificFunction
        )
    node

    . getFunction ()) .getReplacement ( node,
    dbms
        ) ; expandDbArguments (replacement,dbms );node.replaceWith(replacement);}private voidtransformToChar(
        AstFunNodenode){ SimpleNodechild=
        node.child(1);
    if

    ( child instanceofAstStringConstant) {String
    formatString
        = ( ( AstStringConstant)child).getValue(
        ) ;DateFormat df =DateFormat
        .
            byFormatString ( formatString );if( df==null)thrownewIllegalArgumentException
            ( "Unknown date format: " + formatString);transformDateFormat(node,
            df ); } elsethrow
                new IllegalArgumentException ("Date format is not a String") ; }protectedvoid
            transformLevenshtein(AstFunNodenode ){}
        protected
        void
            transformTranslate ( AstFunNodenode){}
    protected

    void transformRegexpLike (AstFunNodenode ){
    }
    protected

    void transformStringAgg (AstFunNodenode ){
    }
    protected

    void transformDecode (AstFunNodenode ){
    }
    protected

    void transformRight (AstFunNodenode ){
    }
    protected

    void transformLeft (AstFunNodenode ){
    }
    protected

    void transformBitAnd (AstFunNodenode ){
    }
    protected

    void transformBitOr (AstFunNodenode ){
    }
    protected

    void transformLeastGreatest (AstFunNodenode ){
    }
    protected

    void transformMod (AstFunNodenode ){
    node
    .

    setFunction ( parserContext.getFunction ("mod"
    )
    )

    ; } protectedvoidtransformDateTimeDiff (AstFunNode
    node
        ,Stringformat){SimpleNodestartDate=node.child(
    node

    . jjtGetNumChildren ()- 2) ; SimpleNodeendDate
    =
        node . child (node.jjtGetNumChildren()-1) ; node.replaceWith
        ( getDateTimeDiff ( startDate,endDate,format));} protected abstractSimpleNodegetDateTimeDiff
        (SimpleNodestartDate,SimpleNodeendDate,String format) ;protectedvoidtransformLastDay
    (

    AstFunNode node ) {}protected voidtransformLastDayPostgres ( SimpleNodenode , SimpleNodedate)

    { } protectedvoidtransformDateAdd (AstFunNode
    node
    )

    { } protectedvoidtransformInstr (AstFunNode node ){
    }
    protected

    void transformDateTrunc (AstFunNodenode ){
    }
    protected

    void transformYearMonthDay (SimpleNodenode ){
    }
    protected

    void transformToDate (AstFunNodenode ){
    }
    protected

    void transformDateFormat (AstFunNodenode ,DateFormat
    df
    )

    { } protectedvoidtransformNow (AstFunNode
    node
    )

    { } protectedvoidtransformTrunc (AstFunNode node ){
    }
    protected

    void transformIf (AstFunNodenode ){
    node
    .

    replaceWith ( newAstCase( newAstWhen
    (
    node

    . child (0) ,node
    .
        child(1)) ,newAstCaseElse (node.child(2))) );}protectedvoidtransformLpad(AstFunNode
                node ){}protectedvoidtransformCastOracle(AstFunNodenode){}
    protected

    void transformChr (AstFunNodenode ){
    }
    protected

    void transformLength (AstFunNodenode ){
    }
    protected

    void transformSubstr (AstFunNodenode ){
    }
    protected

    void transformCoalesce (AstFunNodenode ){
    }
    /**
     * @pending add parenthesis
     */

    protected void transformConcat(AstFunNode node)
    {
    node

    . setFunction (DefaultParserContext. FUNC_CONCAT)
    ;
    }

    protected
    void transformCastExpression (AstCastcast ){
    SimpleNode
        diff=cast.child(0);
    // Detect date_trunc("MONTH", x) + (1 MONTH)::interval - (1 DAY)::interval and extract x

    Predicate < SimpleNode>truncMonth =AstFunNode
    .
        isFunction ( "date_trunc" ,AstStringConstant.isString("MONTH")
        ,
        x->true) ; Predicate <SimpleNode>truncMonthPlusMonth=AstFunNode .isFunction("+",truncMonth, AstInterval . isInterval("1 MONTH"
        ));Predicate < SimpleNode >truncMonthPlusMonthMinusDay=AstFunNode.isFunction ("-" ,truncMonthPlusMonth,AstInterval.isInterval("1 DAY"
        ));if ( "DATE" .equals(cast.getDataType ()
                )&&truncMonthPlusMonthMinusDay.test(diff)
        ) transformLastDayPostgres(cast,diff.child(0). child (0).child(1
            ));else transformCast(cast);}protectedvoidtransformCast(AstCastcast){}protectedvoidtransformExtractExpression
        (
            AstExtractextract){String
    dateField

    = extract .getDateField( ).
    toUpperCase
    (

    ) ; SimpleNodechild= extract.
    child
        ( 0 ) ;if(AstFunNode.isFunction("age").
        test ( child )){SimpleNodeparent=extract
        . jjtGetParent();if("YEAR".equals(dateField))
        {
            SimpleNode grandParent = parent.jjtGetParent();
            SimpleNode toReplace=extract;if(AstFunNode.
            isFunction
                ( "*" ) .test(parent)&&
                AstFunNode . isFunction ("+"
                ) .test(grandParent)&&AstExtract.isExtract("MONTH")
                        . test(grandParent.other(parent))){
                        dateField ="MONTH";toReplace=parent.jjtGetParent();}toReplace.replaceWith(getDateTimeDiff
                (
                    child . child(
                    1 ) ,child.child(0
                )
                ,dateField));}elseif("MONTH".equals( dateField)&&AstFunNode.isFunction( "+").test

            (
            parent ) &&AstFunNode.isFunction("*") . test(parent.child(1))&&parent
                    . child(1).children().anyMatch(AstExtract.isExtract("YEAR"
                    ) )){parent.jjtGetParent().replaceWith(getDateTimeDiff(child.child(1),child
            .
                child(0),dateField));}}elseif(childinstanceofAstParenthesis &&AstFunNode.isFunction("-") .test(child
            .
        child
        ( 0 )) && ( "DAY" .equals(dateField)||"EPOCH".equals(dateField))){AstFunNode
                date =(AstFunNode)child.child ( 0);extract.replaceWith(getDateTimeDiff
        (
            date . child (1) ,date.child(0)
            ,"EPOCH".equals(dateField)?"SECOND":"DAY")) ;}elsetransformExtract(extract) ;}protectedvoidtransformExtract( AstExtract extract
                    ) {}protectedvoid
        transformPosition
        (
            AstPositionposition){}
    protected

    void transformIdentifier (AstIdentifierConstantidentifier ){
    if
    (

    identifier . getQuoteSymbol() ==QuoteSymbol
    .
    BACKTICK

    ) identifier .setQuoteSymbol( QuoteSymbol.
    DOUBLE_QUOTE
        ) ;// TODO: automatically quote identifiers which must be quoted (like started from underscore)}protectedvoidtransformString ( AstStringConstantidentifier){
            }protectedAstFromtransformDualFrom(AstFromfrom){
        return
    from

    ; } protectedvoidtransformSelect (AstSelect
    select
    )

    { AstFrom from=select .getFrom
    (
        ) ;if
    (

    from == null||from .isDual
    (
        ) ) { select.from(transformDualFrom(
        from )) ; } } privatevoidtransformInterval(AstIntervalinterval
        )
            {SimpleNodemul=interval.jjtGetParent();
        if
    (

    AstFunNode . isFunction("*" ).
    test
        ( mul ) ){Stringtype=interval
        . getLiteral();Stringfn;switch(type){case
        "1 MONTH"
            : fn = "ADD_MONTHS";break;case"1 DAYS"
            : case"1 DAY"
            : fn="ADD_DAYS"
            ;
                break ;case
                    "1 MILLISECOND" : fn=
                    "ADD_MILLIS";
                break ;default
                : thrownew
                    IllegalStateException ( "Unsupported interval format: "+
                    interval.
                format ()
                    ) ; }SimpleNode
                    content=
                mul.
                    other ( interval); if (contentinstanceofAstParenthesis)content=
            (
            ( AstParenthesis ) content).child(0)
            ; SimpleNodeadd = mul.
                jjtGetParent ( );if( !AstFunNode.isFunction("+").
            test ( add ))thrownewIllegalStateException(
            "Interval grandparent is " +add+", expected addition");SimpleNodedate=add.other(mul
                ) ; AstFunNodeaddFunction= parserContext . getFunction (fn)
            . node ( date,content);date.
            pullUpPrefix ( ) ;SimpleNodetoReplace=add;if(toReplace.jjtGetParent ()instanceof
            AstParenthesis)toReplace=toReplace.
            jjtGetParent ( ) ;toReplace
            . replaceWith(addFunction);transformDateAdd ( addFunction)
                ; } }protectedvoidtransformWith(AstWith
            with){}protectedvoidtransformExcept
            (AstExceptnode){
        }
    @

    Override public voidtransformAst( AstStartstart
    )
    {

    recursiveProcessing ( start); }@
    Override
    public

    voidtransformQuery
    ( AstQuery start){ recursiveProcessing(
    start
        );}privatevoid
    recursiveProcessing

    (SimpleNode
    node ) {for( inti
    =
        0;i<node
    .

    jjtGetNumChildren ( );i ++)
    {
        SimpleNode child= node . child( i ) ;if(childinstanceofAstFunNode ){transformFunction
        (
            ( AstFunNode ) child);}if(child
            instanceof AstSelect) { transformSelect(
            (
                AstSelect)child); }if(
            child
            instanceof AstIdentifierConstant) { transformIdentifier(
            (
                AstIdentifierConstant)child); }if(
            child
            instanceof AstStringConstant) { transformString(
            (
                AstStringConstant)child); }if(
            child
            instanceof AstCast) { transformCastExpression(
            (
                AstCast)child); }if(
            child
            instanceof AstExtract) { transformExtractExpression(
            (
                AstExtract)child); }if(
            child
            instanceof AstPosition) { transformPosition(
            (
                AstPosition)child); }if(
            child
            instanceof AstInterval) { transformInterval(
            (
                AstInterval)child); }if(
            child
            instanceof AstWith) { transformWith(
            (
                AstWith)child); }if(
            child
            instanceof AstExcept) { transformExcept(
            (
                AstExcept)child); }recursiveProcessing(
            child
            ) ;} } }