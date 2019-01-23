/*
 * Yet Another UserAgent Analyzer
 * Copyright (C) 2013-2018 Niels Basjes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.basjes.parse.useragent.analyze.treewalker;

import nl.basjes.parse.useragent.analyze.InvalidParserConfigurationException;
import nl.basjes.parse.useragent.analyze.Matcher;
import nl.basjes.parse.useragent.analyze.treewalker.steps.WalkList;
import nl.basjes.parse.useragent.analyze.treewalker.steps.WalkList.WalkResult;
import nl.basjes.parse.useragent.parser.UserAgentTreeWalkerBaseVisitor;
import nl.basjes.parse.useragent.parser.UserAgentTreeWalkerParser;
import nl.basjes.parse.useragent.parser.UserAgentTreeWalkerParser.MatcherPathLookupContext;
import nl.basjes.parse.useragent.parser.UserAgentTreeWalkerParser.MatcherPathLookupPrefixContext;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;importorg.antlr
. v4.runtime.tree.ParseTree;importorg.antlr
. v4.runtime.tree.
RuleNode ;importorg.slf4j.

Logger ;importorg.slf4j.
LoggerFactory ;importjava.io.

Serializable
; import java . util .
    Map ; /**
 * This class gets the symbol table (1 value) uses that to evaluate
 * the expression against the parsed user agent
 */ public class TreeExpressionEvaluator implementsSerializable{privatestaticfinalLoggerLOG=
    LoggerFactory . getLogger (TreeExpressionEvaluator

    . class ) ;private
    final boolean verbose ;private
    final String requiredPatternText ;private
    final Matcher matcher ;private

    final WalkListwalkList; privatefinal
                                   String fixedValue;
                                   public TreeExpressionEvaluator( ParserRuleContext
        requiredPattern,Matcher matcher ,booleanverbose){this
        .requiredPatternText= requiredPattern .getText
        (); this .matcher
        =matcher; this .verbose=verbose;
        this . fixedValue =calculateFixedValue(requiredPattern );walkList=newWalkList (requiredPattern,matcher.getLookups (),
    matcher

    .
    getLookupSets ( ),verbose )
        ; }/**
     * @return The fixed value in case of a fixed value. NULL if a dynamic value
     */
    public

    String getFixedValue (){ returnfixedValue ;
        } private StringcalculateFixedValue(ParserRuleContextrequiredPattern) {

            returnnew
            UserAgentTreeWalkerBaseVisitor < String>( ){ @ Overrideprotected boolean
                shouldVisitNextChild ( RuleNode node,
            String

            currentResult)
            { return currentResult==null ;} @ Overrideprotected String
                aggregateResult ( String aggregate , String nextResult ){
            return

            nextResult==
            null ? aggregate:nextResult ;} @
                Override publicStringvisitMatcherPathLookup(MatcherPathLookupContextctx){ returnvisitLookups(ctx .matcher(),
            ctx
            .lookup
            , ctx .defaultValue) ;} @
                Override publicStringvisitMatcherPathLookupPrefix(MatcherPathLookupPrefixContextctx){ returnvisitLookups(ctx .matcher(),
            ctx

            . lookup ,ctx. defaultValue) ; }private String visitLookups( ParseTree
                matcherTree , Token lookup,TokendefaultValue)
                { Stringvalue = visit( matcherTree
                    ) ;if
                (
                value
                ==

                null){return null; } // Now we know this is a fixed value. Yet we can have a problem in the lookup that was // configured. If we have this then this is a FATAL error (it will fail always everywhere).Map<String,String>lookupMap=matcher.getLookups().
                get (lookup . getText( )
                    ) ; if(lookupMap == null){thrownew InvalidParserConfigurationException ("Missing lookup \""+
                lookup

                . getText ( )+"\" ");}StringresultingValue=lookupMap.
                get (value . toLowerCase( )
                    ) ;if ( resultingValue== null
                        ) {if(defaultValue!=null
                    )
                    { return defaultValue.
                        getText ( ) ; } throw newInvalidParserConfigurationException("Fixed value >>"+ value +"<< is missing in lookup: \""+
                lookup
                . getText(
            )

            +"\" "
            ) ; }returnresultingValue;} @Override public
                String visitPathFixedValue(UserAgentTreeWalkerParser.PathFixedValueContextctx){
            return
        ctx.value.getText()
    ;

    }

    } . visit(requiredPattern ); } // ------------------------------------------public WalkResult evaluate( ParseTree
        tree ,Stringkey ,
            Stringvalue){if( verbose) {LOG.
            info("Evaluate: {} => {}",key, value);
            LOG.info("Pattern : {}", requiredPatternText);
        LOG
        . info ( "WalkList: {}",walkList);} WalkResultresult=
        walkList .walk( tree
            ,value);if( verbose ) { LOG . info ("Evaluate: Result = {}",result==null?
        "null"
        : result.
    getValue

    ( ) );} return
        result ;}publicbooleanusesIsNull(
    )

    { return walkList.usesIsNull (
        ) ;}
    public

    WalkList getWalkListForUnitTesting (){ return
        walkList;}publicvoidpruneTrailingStepsThatCannotFail
    (
)
