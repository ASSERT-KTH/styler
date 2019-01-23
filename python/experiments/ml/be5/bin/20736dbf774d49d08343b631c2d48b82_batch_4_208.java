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
            expandDbFunction(node, getDbms());
        if ("concat".equals(name) || ("||".equals(name) && node.jjtGetNumChildren() > 1))
            transformConcat(node);
        else if ("coalesce".equals(name))
            transformCoalesce(node);
        else if ("substr".equals(name))
            transformSubstr(node);
        else if ("length".equals(name))
            transformLength(node);
        else if ("chr".equals(name))
            transformChr(node);
        else if ("if".equals(name))
            transformIf(node);
        else if ("date_format".equals(name) || ("to_char".equals(name) && node.jjtGetNumChildren() == 2))
            transformToChar(node);
        else if ("to_char".equals(name) && node.jjtGetNumChildren() == 1 || "to_number".equals(name) || "to_key".equals(name))
            transformCastOracle(node);
        else if ("lpad".equals(name))
            transformLpad(node);
        else if ("trunc".equals(name))
            transformTrunc(node);
        else if ("now".equals(name))
            transformNow(node);
        else if ("to_date".equals(name))
            transformToDate(node);
        else if ("year".equals(name) || "month".equals(name) || "day".equals(name))
            transformYearMonthDay(node);
        else
        {
            DateFormat df = DateFormat.byFunction(name);
            if (df != null)
                transformDateFormat(node, df);
        }
        if ("date_trunc".equals(name))
        {
            String datePart = ((AstStringConstant) node.child(0)).getValue();
            if (datePart.equalsIgnoreCase("'month'") || datePart.equalsIgnoreCase("'year'"))
                transformDateTrunc(node);
        }
        if ("instr".equals(name))
            transformInstr(node);
        if ("add_months".equals(name) || "add_days".equals(name) || "add_millis".equals(name))
            transformDateAdd(node);
        if ("last_day".equals(name))
            transformLastDay(node);
        if ("seconddiff".equals(name))
            transformDateTimeDiff(node, "SECOND");
        if ("minutediff".equals(name))
            transformDateTimeDiff(node, "MINUTE");
        if ("hourdiff".equals(name))
            transformDateTimeDiff(node, "HOUR");
        if ("daydiff".equals(name))
            transformDateTimeDiff(node, "DAY");
        if ("monthdiff".equals(name))
            transformDateTimeDiff(node, "MONTH");
        if ("yeardiff".equals(name))
            transformDateTimeDiff(node, "YEAR");
        if ("timestampdiff".equals(name))
        {
            SimpleNode child = node.child(0);StringdatePart
            =child instanceofAstFieldReference?( (AstIdentifierConstant)
        child
        . child(0)).getValue ( ):"";transformDateTimeDiff(node
            ,datePart);}
        if ("mod".equals(name) || "%".equals(name))
            transformMod(node);
        if ("least".equals(name)||
            "greatest".equals(name
        ) )transformLeastGreatest(node);if(
            "&".equals(name
        ) )transformBitAnd(node);if(
            "|".equals(name
        ) )transformBitOr(node);if(
            "right".equals(name
        ) )transformRight(node);if(
            "left".equals(name
        ) )transformLeft(node);if(
            "decode".equals(name
        ) )transformDecode(node);if ( "string_agg".equals(name))
            transformStringAgg(node);
        if ("regexp_like".equals(name)||
            "~".equals(name
        ) )transformRegexpLike(node);if(
            "translate".equals(name
    )

    ) transformTranslate (node);

    if ( "levenshtein".equals (name ) )transformLevenshtein
    (
        node ); } abstract DbmsgetDbms ( ) ;privatevoidexpandDbArguments(SimpleNode node,Dbms
        dbms
            ) { for (inti=0;i
            <node.jjtGetNumChildren ();
            i ++) { SimpleNode child =node.child(i);expandDbArguments(child,dbms );if(childinstanceofAstFunNode&&
                DbSpecificFunction.needsTransformation(dbms ). test((
        (
    AstFunNode

    ) child ).getFunction () ) )expandDbFunction
    (
        ( AstFunNode ) child,dbms) ;}}privatevoidexpandDbFunction(AstFunNodenode,Dbms dbms){
        SimpleNodereplacement=( (DbSpecificFunction)
        node.getFunction()).
    getReplacement

    ( node ,dbms) ;expandDbArguments
    (
        replacement , dbms );node.replaceWith(replacement
        ) ;} private voidtransformToChar
        (
            AstFunNode node ) {SimpleNodechild= node.child(1);
            if ( child instanceofAstStringConstant){StringformatString=
            ( (AstStringConstant ) child)
                . getValue (); DateFormat df=DateFormat
            .byFormatString(formatString );if
        (
        df
            == null )thrownewIllegalArgumentException(
    "Unknown date format: "

    + formatString );transformDateFormat (node
    ,
    df

    ) ; }elsethrow newIllegalArgumentException
    (
    "Date format is not a String"

    ) ; }protectedvoid transformLevenshtein(
    AstFunNode
    node

    ) { }protectedvoid transformTranslate(
    AstFunNode
    node

    ) { }protectedvoid transformRegexpLike(
    AstFunNode
    node

    ) { }protectedvoid transformStringAgg(
    AstFunNode
    node

    ) { }protectedvoid transformDecode(
    AstFunNode
    node

    ) { }protectedvoid transformRight(
    AstFunNode
    node

    ) { }protectedvoid transformLeft(
    AstFunNode
    node

    ) { }protectedvoid transformBitAnd(
    AstFunNode
    node

    ) { }protectedvoid transformBitOr(
    AstFunNode
        node){}protectedvoidtransformLeastGreatest(AstFunNodenode){
    }

    protected void transformMod(AstFunNode node) { node.
    setFunction
        ( parserContext . getFunction("mod"));}protectedvoid transformDateTimeDiff (AstFunNodenode
        , String format ){SimpleNodestartDate=node.child( node .jjtGetNumChildren(
        )-2);SimpleNodeendDate= node. child(node.
    jjtGetNumChildren

    ( ) - 1); node. replaceWith (getDateTimeDiff ( startDate,endDate

    , format )); }protected
    abstract
    SimpleNode

    getDateTimeDiff ( SimpleNodestartDate, SimpleNodeendDate , Stringformat
    )
    ;

    protected void transformLastDay(AstFunNode node)
    {
    }

    protected void transformLastDayPostgres(SimpleNode node,
    SimpleNode
    date

    ) { }protectedvoid transformDateAdd(
    AstFunNode
    node

    ) { }protectedvoid transformInstr(
    AstFunNode
    node

    ) { }protectedvoid transformDateTrunc(
    AstFunNode
    node

    ) { }protectedvoid transformYearMonthDay( SimpleNode node)
    {
    }

    protected void transformToDate(AstFunNode node)
    {
    }

    protected void transformDateFormat(AstFunNode node,
    DateFormat
    df

    ) { }protectedvoid transformNow(
    AstFunNode
        node){}protected voidtransformTrunc( AstFunNodenode){}protectedvoidtransformIf( AstFunNodenode){node.replaceWith(
                new AstCase(newAstWhen(node.child(0),
    node

    . child (1) ),
    new
    AstCaseElse

    ( node .child( 2)
    )
    )

    ) ; }protectedvoid transformLpad(
    AstFunNode
    node

    ) { }protectedvoid transformCastOracle(
    AstFunNode
    node

    ) { }protectedvoid transformChr(
    AstFunNode
    node

    ) { }protectedvoid transformLength(
    AstFunNode
    node

    )
    { } protectedvoidtransformSubstr (AstFunNode
    node
        ){}protectedvoidtransformCoalesce(AstFunNodenode
    )

    { } /**
     * @pending add parenthesis
     */protectedvoid transformConcat(
    AstFunNode
        node ) { node.setFunction(DefaultParserContext.FUNC_CONCAT
        )
        ;}protectedvoid transformCastExpression ( AstCastcast){SimpleNodediff =cast.child(0) ; // Detect date_trunc("MONTH", x) + (1 MONTH)::interval - (1 DAY)::interval and extract x Predicate<SimpleNode
        >truncMonth=AstFunNode . isFunction ("date_trunc",AstStringConstant.isString ("MONTH" ),x->true);Predicate
        <SimpleNode>truncMonthPlusMonth = AstFunNode .isFunction("+",truncMonth ,AstInterval
                .isInterval("1 MONTH"));Predicate
        < SimpleNode>truncMonthPlusMonthMinusDay=AstFunNode.isFunction("-",truncMonthPlusMonth , AstInterval.isInterval("1 DAY"))
            ;if("DATE" .equals(cast.getDataType())&&truncMonthPlusMonthMinusDay.test(diff))transformLastDayPostgres
        (
            cast,diff.child
    (

    0 ) .child( 0)
    .
    child

    ( 1 )); elsetransformCast
    (
        cast ) ; }protectedvoidtransformCast(AstCastcast){}
        protected void transformExtractExpression (AstExtractextract){StringdateField
        = extract.getDateField().toUpperCase();SimpleNodechild=
        extract
            . child ( 0);if(AstFunNode
            . isFunction("age").test(child
            )
                ) { SimpleNode parent=extract.jjtGetParent(
                ) ; if ("YEAR"
                . equals(dateField)){SimpleNodegrandParent=parent.jjtGetParent
                        ( );SimpleNodetoReplace=extract;if(AstFunNode.
                        isFunction ("*").test(parent)&&AstFunNode.isFunction("+").test
                (
                    grandParent ) &&AstExtract
                    . isExtract ("MONTH").test(
                grandParent
                .other(parent))){dateField="MONTH";toReplace =parent.jjtGetParent(); }toReplace.replaceWith

            (
            getDateTimeDiff ( child.child(1), child .child(0),dateField));}
                    else if("MONTH".equals(dateField)&&AstFunNode.isFunction("+").
                    test (parent)&&AstFunNode.isFunction("*").test(parent.child(1))&&
            parent
                .child(1).children().anyMatch(AstExtract.isExtract("YEAR" ))){parent.jjtGetParent ().replaceWith
            (
        getDateTimeDiff
        ( child .child ( 1 ) ,child.child(0),dateField));}}elseif
                ( childinstanceofAstParenthesis&&AstFunNode.isFunction ( "-").test(child.child
        (
            0 ) ) &&("DAY" .equals(dateField)||"EPOCH"
            .equals(dateField))){AstFunNodedate=(AstFunNode )child.child(0) ;extract.replaceWith(getDateTimeDiff ( date
                    . child(1)
        ,
        date
            .child(0)
    ,

    "EPOCH" . equals(dateField )?
    "SECOND"
    :

    "DAY" ) );} elsetransformExtract
    (
    extract

    ) ; }protectedvoid transformExtract(
    AstExtract
        extract ){}protectedvoidtransformPosition ( AstPositionposition){
            }protectedvoidtransformIdentifier(AstIdentifierConstantidentifier){
        if
    (

    identifier . getQuoteSymbol() ==QuoteSymbol
    .
    BACKTICK

    ) identifier .setQuoteSymbol( QuoteSymbol.
    DOUBLE_QUOTE
        ) ;// TODO: automatically quote identifiers which must be quoted (like started from underscore)
    }

    protected void transformString(AstStringConstant identifier)
    {
        } protected AstFrom transformDualFrom(AstFromfrom){
        return from; } protected void transformSelect(AstSelectselect){
        AstFrom
            from=select.getFrom();if(
        from
    ==

    null || from.isDual ()
    )
        { select . from(transformDualFrom(from)
        ) ;}}privatevoidtransformInterval(AstIntervalinterval){SimpleNodemul
        =
            interval . jjtGetParent ();if(AstFunNode
            . isFunction(
            "*" ).test
            (
                mul ))
                    { String type=
                    interval.
                getLiteral ()
                ; Stringfn
                    ; switch (type
                    ){
                case "1 MONTH":
                    fn = "ADD_MONTHS";
                    break;
                case"1 DAYS"
                    : case "1 DAY":fn = "ADD_DAYS";break;case"1 MILLISECOND":
            fn
            = "ADD_MILLIS" ; break;default:thrownewIllegalStateException
            ( "Unsupported interval format: "+ interval .format
                ( ) );}SimpleNode content=mul.other(interval)
            ; if ( contentinstanceofAstParenthesis)content=
            ( (AstParenthesis)content).child(0);SimpleNodeadd=
                mul . jjtGetParent() ; if ( !AstFunNode.
            isFunction ( "+" ).test(add))
            throw new IllegalStateException ("Interval grandparent is "+add+", expected addition");SimpleNodedate= add.other
            (mul);AstFunNodeaddFunction
            = parserContext . getFunction(
            fn ).node(date, content );
                date . pullUpPrefix();SimpleNodetoReplace
            =add;if(toReplace.
            jjtGetParent()instanceofAstParenthesis
        )
    toReplace

    = toReplace .jjtGetParent( );
    toReplace
    .

    replaceWith ( addFunction); transformDateAdd(
    addFunction
    )

    ;}
    } protected voidtransformWith( AstWithwith
    )
        {}protectedvoidtransformExcept
    (

    AstExceptnode
    ) { }@Override publicvoid
    transformAst
        (AstStartstart){
    recursiveProcessing

    ( start );} @Override
    public
        void transformQuery( AstQuery start ){ recursiveProcessing ( start);}privatevoid recursiveProcessing(SimpleNode
        node
            ) { for (inti=0;i
            < node. jjtGetNumChildren ()
            ;
                i++){SimpleNode child=node
            .
            child (i ) ;if
            (
                childinstanceofAstFunNode){ transformFunction((
            AstFunNode
            ) child) ; }if
            (
                childinstanceofAstSelect){ transformSelect((
            AstSelect
            ) child) ; }if
            (
                childinstanceofAstIdentifierConstant){ transformIdentifier((
            AstIdentifierConstant
            ) child) ; }if
            (
                childinstanceofAstStringConstant){ transformString((
            AstStringConstant
            ) child) ; }if
            (
                childinstanceofAstCast){ transformCastExpression((
            AstCast
            ) child) ; }if
            (
                childinstanceofAstExtract){ transformExtractExpression((
            AstExtract
            ) child) ; }if
            (
                childinstanceofAstPosition){ transformPosition((
            AstPosition
            ) child) ; }if
            (
                childinstanceofAstInterval){ transformInterval((
            AstInterval
            ) child) ; }if
            (
                childinstanceofAstWith){ transformWith((
            AstWith
            )child);}
        if
    (
child
