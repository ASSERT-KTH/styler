package com.developmentontheedge.sql.format.dbms;

import com.developmentontheedge.sql.model.AstCase;
import com.developmentontheedge.sql.model.AstCaseElse;
import com.developmentontheedge.sql.model.AstCast;
import com.developmentontheedge.sql.model.AstExcept;
import com.developmentontheedge.sql.model.AstExtract;
import com.developmentontheedge.sql.model.AstFieldReference;
import com.developmentontheedge.sql.model.AstFrom;
import com.developmentontheedge.sql.model.AstFunNode;
import com.developmentontheedge.sql.model.AstIdentifierConstant;
import com.developmentontheedge.sql.model.AstIdentifierConstant.QuoteSymbol;
import com.developmentontheedge.sql.model.AstInterval;
import com.developmentontheedge.sql.model.AstParenthesis;
import com.developmentontheedge.sql.model.AstPosition;
import com.developmentontheedge.sql.model.AstQuery;
import com.developmentontheedge.sql.model.AstSelect;
import com.developmentontheedge.sql.model.AstStart;
import com.developmentontheedge.sql.model.AstStringConstant;
import com.developmentontheedge.sql.model.AstWhen;
import com.developmentontheedge.sql.model.AstWith;
import com.developmentontheedge.sql.model.DbSpecificFunction;
import com.developmentontheedge.sql.model.DefaultParserContext;
import com.developmentontheedge.sql.model.ParserContext;
import com.developmentontheedge.sql.model.SimpleNode;

import java.util.function.Predicate;

public abstract class GenericDbmsTransformer implements DbmsTransformer
{
    protected ParserContext parserContext;

    @Override
    public ParserContext getParserContext()
    {
        return parserContext;
    }

    @Override
    public void setParserContext(ParserContext parserContext)
    {
        this.parserContext = parserContext;
    }

    protected void transformFunction(AstFunNode node)
    {
        String name = node.getFunction().getName().toLowerCase();

        if (node.getFunction() instanceof DbSpecificFunction && !((DbSpecificFunction) node.getFunction()).isApplicable(getDbms())
                && !node.withinDbmsTransform())
        {
            throw new IllegalStateException("Function/operator '" + node.getFunction().getName() + "' is unsupported for " + getDbms());
        }
        if (DbSpecificFunction.needsTransformation(getDbms()).test(node.getFunction()))
            expandDbFunction(node, getDbms());if(
        "concat". equals(name)||("||".equals(name)&&node .jjtGetNumChildren ()>1))transformConcat(node);elseif("coalesce". equals( name))transformCoalesce(node);elseif ( "substr".equals
                    (name))transformSubstr
                ( node );elseif("length".equals
                    (name))transformLength
                ( node );elseif("chr".equals
                    (name))transformChr
                ( node );elseif("if".equals
                    (name))transformIf
                ( node );elseif("date_format".equals
                    (name)||(
                "to_char" . equals(name)&&node.jjtGetNumChildren
                    ()==2)
                ) transformToChar (node);elseif( "to_char" .equals(name)&&node . jjtGetNumChildren()==1 || "to_number".equals
                    (name)||"to_key"
                . equals (name))transformCastOracle(node ) ;elseif("lpad" . equals ( name))transformLpad(node ) ;elseif("trunc".equals
                    (name))transformTrunc
                ( node );elseif("now".equals
                    (name))transformNow
                ( node );elseif("to_date".equals
                    (name))transformToDate
                ( node );elseif("year".equals
                    (name)||"month"
                . equals (name)||"day".equals(
                    name))transformYearMonthDay(
                node ) ;else{DateFormatdf=DateFormat . byFunction(name);if ( df!=null)transformDateFormat(node
                    ,df);}
                if
                (
                    "date_trunc" . equals (name)){StringdatePart
                    = (( AstStringConstant )node
                        .child(0 )).
                getValue
                ( );if(datePart.equalsIgnoreCase(
                "'month'"
                    ) || datePart .equalsIgnoreCase("'year'" ))transformDateTrunc(node);}if("instr".
                    equals (name))transformInstr(node ) ;if("add_months".equals(
                        name)||"add_days".
                equals
                ( name)||"add_millis".equals(name
                    ))transformDateAdd(node
                ) ;if("last_day".equals( name ))transformLastDay(node) ; if("seconddiff".equals(name
                    ))transformDateTimeDiff(node
                , "SECOND");if("minutediff".equals
                    (name))transformDateTimeDiff
                ( node,"MINUTE");if("hourdiff"
                    .equals(name ))transformDateTimeDiff
                ( node,"HOUR");if("daydiff"
                    .equals(name ))transformDateTimeDiff
                ( node,"DAY");if("monthdiff"
                    .equals(name ))transformDateTimeDiff
                ( node,"MONTH");if("yeardiff"
                    .equals(name ))transformDateTimeDiff
                ( node,"YEAR");if("timestampdiff"
                    .equals(name )){
                SimpleNode child=node.child(0)
                    ;StringdatePart= childinstanceofAstFieldReference
                ? ((AstIdentifierConstant)child.child(
                0
                    ) ) . getValue():"";transformDateTimeDiff
                    ( node , datePart ) ; } if("mod". equals(name)||"%".equals(name) ) transformMod(
                    node);if ("least".
                equals
                ( name)||"greatest".equals( name ))transformLeastGreatest(node);
                    if("&".equals
                ( name))transformBitAnd(node) ; if("|".equals(name
                    ))transformBitOr(node
                ) ;if("right".equals(name
                    ))transformRight(node
                ) ;if("left".equals(name
                    ))transformLeft(node
                ) ;if("decode".equals(name
                    ))transformDecode(node
                ) ;if("string_agg".equals(name
                    ))transformStringAgg(node
                ) ;if("regexp_like".equals(name
                    )||"~".equals
                ( name))transformRegexpLike(node);
                    if("translate".equals
                ( name))transformTranslate(node) ; if("levenshtein".equals(name
                    ))transformLevenshtein(node
                ) ;}abstractDbmsgetDbms();
                    privatevoidexpandDbArguments(SimpleNode
                node ,Dbmsdbms){for(int
                    i=0;i
            <

            node . jjtGetNumChildren();

            i ++ ){SimpleNode child= node .child
            (
                i ); expandDbArguments ( child, dbms ) ;if(childinstanceofAstFunNode &&DbSpecificFunction.
                needsTransformation
                    ( dbms ) .test(((AstFunNode)
                    child).getFunction ())
                    ) expandDbFunction( ( AstFunNode ) child,dbms);}}privatevoidexpandDbFunction(AstFunNodenode ,Dbmsdbms){SimpleNodereplacement=
                        ((DbSpecificFunction)node .getFunction ())
                .
            getReplacement

            ( node ,dbms) ;expandDbArguments ( replacement,
            dbms
                ) ; node .replaceWith(replacement );}privatevoidtransformToChar(AstFunNodenode){ SimpleNodechild=
                node.child( 1);
                if(childinstanceofAstStringConstant){
            String

            formatString = ((AstStringConstant )child
            )
                . getValue ( );DateFormatdf=DateFormat.
                byFormatString (formatString ) ;if
                (
                    df == null )thrownewIllegalArgumentException ("Unknown date format: "+formatString);transformDateFormat
                    ( node , df);}elsethrownew
                    IllegalArgumentException ("Date format is not a String" ) ;}
                        protected void transformLevenshtein(AstFunNode node ){}
                    protectedvoidtransformTranslate( AstFunNodenode)
                {
                }
                    protected void transformRegexpLike(AstFunNodenode)
            {

            } protected voidtransformStringAgg( AstFunNodenode
            )
            {

            } protected voidtransformDecode( AstFunNodenode
            )
            {

            } protected voidtransformRight( AstFunNodenode
            )
            {

            } protected voidtransformLeft( AstFunNodenode
            )
            {

            } protected voidtransformBitAnd( AstFunNodenode
            )
            {

            } protected voidtransformBitOr( AstFunNodenode
            )
            {

            } protected voidtransformLeastGreatest( AstFunNodenode
            )
            {

            } protected voidtransformMod( AstFunNodenode
            )
            {

            node . setFunction(parserContext .getFunction
            (
            "mod"

            ) ) ;}protected voidtransformDateTimeDiff
            (
            AstFunNode

            node , Stringformat) {SimpleNode
            startDate
                =node.child(node.jjtGetNumChildren()-2
            )

            ; SimpleNode endDate=node .child ( node.
            jjtGetNumChildren
                ( ) - 1);node.replaceWith(getDateTimeDiff( startDate ,endDate,
                format ) ) ;}protectedabstractSimpleNodegetDateTimeDiff(SimpleNodestartDate , SimpleNodeendDate,
                Stringformat);protectedvoidtransformLastDay( AstFunNodenode ){}protected
            void

            transformLastDayPostgres ( SimpleNode node,SimpleNode date) { }protected void transformDateAdd(AstFunNode

            node ) {}protected voidtransformInstr
            (
            AstFunNode

            node ) {}protected voidtransformDateTrunc ( AstFunNodenode
            )
            {

            } protected voidtransformYearMonthDay( SimpleNodenode
            )
            {

            } protected voidtransformToDate( AstFunNodenode
            )
            {

            } protected voidtransformDateFormat( AstFunNodenode
            ,
            DateFormat

            df ) {}protected voidtransformNow
            (
            AstFunNode

            node ) {}protected voidtransformTrunc
            (
            AstFunNode

            node ) {}protected voidtransformIf ( AstFunNodenode
            )
            {

            node . replaceWith(new AstCase(
            new
            AstWhen

            ( node .child( 0)
            ,
            node

            . child (1) ),
            new
                AstCaseElse(node.child (2) )));}protectedvoidtransformLpad( AstFunNodenode){}protectedvoidtransformCastOracle
                        ( AstFunNodenode){}protectedvoidtransformChr(AstFunNodenode)
            {

            } protected voidtransformLength( AstFunNodenode
            )
            {

            } protected voidtransformSubstr( AstFunNodenode
            )
            {

            } protected voidtransformCoalesce( AstFunNodenode
            )
            {

            } /**
     * @pending add parenthesis
     */ protectedvoidtransformConcat (AstFunNode
            node
            )

            { node .setFunction( DefaultParserContext.
            FUNC_CONCAT
            )

            ; } protectedvoidtransformCastExpression (AstCast
            cast
            )

            {
            SimpleNode diff =cast. child(
            0
                );// Detect date_trunc("MONTH", x) + (1 MONTH)::interval - (1 DAY)::interval and extract xPredicate<SimpleNode>truncMonth=
            AstFunNode

            . isFunction ("date_trunc", AstStringConstant.
            isString
                ( "MONTH" ) ,x->true);Predicate
                <
                SimpleNode>truncMonthPlusMonth= AstFunNode . isFunction("+",truncMonth, AstInterval.isInterval("1 MONTH")) ; Predicate <SimpleNode>
                truncMonthPlusMonthMinusDay=AstFunNode. isFunction ( "-",truncMonthPlusMonth,AstInterval. isInterval( "1 DAY"));if("DATE".
                equals(cast. getDataType ( ))&&truncMonthPlusMonthMinusDay.test (diff
                        ))transformLastDayPostgres(cast,diff.
                child (0).child(0).child( 1 ));elsetransformCast(cast
                    );}protected voidtransformCast(AstCastcast){}protectedvoidtransformExtractExpression(AstExtractextract){StringdateField
                =
                    extract.getDateField()
            .

            toUpperCase ( );SimpleNode child=
            extract
            .

            child ( 0); if(
            AstFunNode
                . isFunction ( "age").test(child)){SimpleNode
                parent = extract .jjtGetParent();if(
                "YEAR" .equals(dateField)){SimpleNodegrandParent=parent.jjtGetParent
                (
                    ) ; SimpleNode toReplace=extract;if(
                    AstFunNode .isFunction("*").test(
                    parent
                        ) && AstFunNode .isFunction("+").
                        test ( grandParent )&&
                        AstExtract .isExtract("MONTH").test(grandParent.other(
                                parent ))){dateField="MONTH";toReplace=parent
                                . jjtGetParent();}toReplace.replaceWith(getDateTimeDiff(child.child(1)
                        ,
                            child . child(
                            0 ) ,dateField));}
                        else
                        if("MONTH".equals(dateField)&&AstFunNode.isFunction( "+").test(parent) &&AstFunNode.isFunction

                    (
                    "*" ) .test(parent.child( 1 ))&&parent.child(1).children
                            ( ).anyMatch(AstExtract.isExtract("YEAR"))){parent.jjtGetParent
                            ( ).replaceWith(getDateTimeDiff(child.child(1),child.child(0),dateField
                    )
                        );}}elseif(childinstanceofAstParenthesis&&AstFunNode.isFunction("-") .test(child.child( 0))&&
                    (
                "DAY"
                . equals (dateField ) || "EPOCH" .equals(dateField))){AstFunNodedate=(AstFunNode)child.
                        child (0);extract.replaceWith ( getDateTimeDiff(date.child(1)
                ,
                    date . child (0) ,"EPOCH".equals(dateField)
                    ?"SECOND":"DAY"));}elsetransformExtract(extract) ;}protectedvoidtransformExtract(AstExtract extract){}protectedvoid transformPosition (
                            AstPosition position){}
                protected
                void
                    transformIdentifier(AstIdentifierConstantidentifier)
            {

            if ( identifier.getQuoteSymbol ()
            ==
            QuoteSymbol

            . BACKTICK )identifier. setQuoteSymbol(
            QuoteSymbol
            .

            DOUBLE_QUOTE ) ;// TODO: automatically quote identifiers which must be quoted (like started from underscore)} protectedvoid
            transformString
                ( AstStringConstantidentifier){}protected AstFrom transformDualFrom(AstFromfrom
                    ){returnfrom;}protectedvoidtransformSelect
                (
            AstSelect

            select ) {AstFromfrom =select
            .
            getFrom

            ( ) ;if( from==
            null
                || from.
            isDual

            ( ) ){select .from
            (
                transformDualFrom ( from ));}}private
                void transformInterval( AstInterval interval ) {SimpleNodemul=interval.
                jjtGetParent
                    ();if(AstFunNode.isFunction("*"
                )
            .

            test ( mul)) {String
            type
                = interval . getLiteral();Stringfn
                ; switch(type){case"1 MONTH":fn="ADD_MONTHS";break
                ;
                    case "1 DAYS" : case"1 DAY":fn="ADD_DAYS"
                    ; break;
                    case "1 MILLISECOND":fn
                    =
                        "ADD_MILLIS" ;break
                            ; default :throw
                            newIllegalStateException
                        ( "Unsupported interval format: "+
                        interval .format
                            ( ) );
                            }SimpleNode
                        content =mul
                            . other (interval
                            );
                        if(
                            content instanceof AstParenthesis)content = ((AstParenthesis)content).
                    child
                    ( 0 ) ;SimpleNodeadd=mul.jjtGetParent
                    ( ); if (!
                        AstFunNode . isFunction("+") .test(add))thrownew
                    IllegalStateException ( "Interval grandparent is " +add+", expected addition");
                    SimpleNode date=add.other(mul);AstFunNodeaddFunction=parserContext.
                        getFunction ( fn). node ( date ,content)
                    ; date . pullUpPrefix();SimpleNodetoReplace=
                    add ; if (toReplace.jjtGetParent()instanceofAstParenthesis)toReplace= toReplace.jjtGetParent
                    ();toReplace.replaceWith
                    ( addFunction ) ;transformDateAdd
                    ( addFunction);}}protected void transformWith(
                        AstWith with ){}protectedvoidtransformExcept
                    (AstExceptnode){}@
                    OverridepublicvoidtransformAst(
                AstStart
            start

            ) { recursiveProcessing(start );
            }
            @

            Override public voidtransformQuery( AstQuerystart
            )
            {

            recursiveProcessing(
            start ) ;}private voidrecursiveProcessing
            (
                SimpleNodenode){for
            (

            inti
            = 0 ;i< node.
            jjtGetNumChildren
                ();i++
            )

            { SimpleNode child=node .child
            (
                i ); if ( childinstanceof AstFunNode ) {transformFunction((AstFunNode) child);
                }
                    if ( child instanceofAstSelect){transformSelect((
                    AstSelect )child ) ;}
                    if
                        (childinstanceofAstIdentifierConstant) {transformIdentifier(
                    (
                    AstIdentifierConstant )child ) ;}
                    if
                        (childinstanceofAstStringConstant) {transformString(
                    (
                    AstStringConstant )child ) ;}
                    if
                        (childinstanceofAstCast) {transformCastExpression(
                    (
                    AstCast )child ) ;}
                    if
                        (childinstanceofAstExtract) {transformExtractExpression(
                    (
                    AstExtract )child ) ;}
                    if
                        (childinstanceofAstPosition) {transformPosition(
                    (
                    AstPosition )child ) ;}
                    if
                        (childinstanceofAstInterval) {transformInterval(
                    (
                    AstInterval )child ) ;}
                    if
                        (childinstanceofAstWith) {transformWith(
                    (
                    AstWith )child ) ;}
                    if
                        (childinstanceofAstExcept) {transformExcept(
                    (
                    AstExcept )child ) ;}
                    recursiveProcessing
                        (child);} }}