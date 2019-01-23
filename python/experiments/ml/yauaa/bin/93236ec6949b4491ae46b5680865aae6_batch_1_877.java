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
import nl.basjes.parse.useragent.analyze.treewalker.steps.
WalkList .WalkResult;importnl.basjes.parse.useragent.
parser .UserAgentTreeWalkerBaseVisitor;importnl.basjes.parse.useragent.
parser .UserAgentTreeWalkerParser;importnl.basjes.parse.useragent.parser.
UserAgentTreeWalkerParser .MatcherPathLookupContext;importnl.basjes.parse.useragent.parser.
UserAgentTreeWalkerParser .MatcherPathLookupPrefixContext;importorg.antlr.v4.
runtime .ParserRuleContext;importorg.antlr.v4.
runtime .Token;importorg.antlr.v4.runtime.
tree .ParseTree;importorg.antlr.v4.runtime.
tree .RuleNode;importorg.
slf4j .Logger;importorg.

slf4j .LoggerFactory;importjava.
io .Serializable;importjava.

util
. Map ; /**
 * This class gets the symbol table (1 value) uses that to evaluate
 * the expression against the parsed user agent
 */ public class
    TreeExpressionEvaluator implements Serializable { private static finalLoggerLOG=LoggerFactory.getLogger(TreeExpressionEvaluator
    . class ) ;private

    final boolean verbose ;private
    final String requiredPatternText ;private
    final Matcher matcher ;private
    final WalkList walkList ;private

    final StringfixedValue; publicTreeExpressionEvaluator
                                   ( ParserRuleContextrequiredPattern
                                   , Matchermatcher ,
        booleanverbose) { this.requiredPatternText=requiredPattern.
        getText() ; this.
        matcher=matcher ; this.
        verbose=verbose ; this.fixedValue=calculateFixedValue
        ( requiredPattern ) ;walkList=new WalkList(requiredPattern,matcher. getLookups(),matcher. getLookupSets()
    ,

    verbose
    ) ; }/**
     * @return The fixed value in case of a fixed value. NULL if a dynamic value
     */public String
        getFixedValue ()
    {

    return fixedValue ;}private StringcalculateFixedValue (
        ParserRuleContext requiredPattern ){returnnewUserAgentTreeWalkerBaseVisitor< String

            >(
            ) { @Overrideprotected booleanshouldVisitNextChild ( RuleNodenode ,
                String currentResult ) {return
            currentResult

            ==null
            ; } @Overrideprotected StringaggregateResult ( Stringaggregate ,
                String nextResult ) { return nextResult == null?
            aggregate

            :nextResult
            ; } @Overridepublic StringvisitMatcherPathLookup (
                MatcherPathLookupContext ctx){returnvisitLookups(ctx. matcher(), ctx.lookup,ctx
            .
            defaultValue)
            ; } @Overridepublic StringvisitMatcherPathLookupPrefix (
                MatcherPathLookupPrefixContext ctx){returnvisitLookups(ctx. matcher(), ctx.lookup,ctx
            .

            defaultValue ) ;}private StringvisitLookups ( ParseTreematcherTree , Tokenlookup ,
                Token defaultValue ) {Stringvalue=visit
                ( matcherTree) ; if( value
                    == null)
                {
                return
                null

                ;}// Now we know this is a fixed value. Yet we can have a problem in the lookup that was// configured. If we have this then this is a FATAL error (it will fail always everywhere). Map< String , String>lookupMap=matcher.getLookups().get(lookup.getText
                ( )) ; if( lookupMap
                    == null ){throw new InvalidParserConfigurationException("Missing lookup \""+lookup . getText()
                +

                "\" " ) ; }StringresultingValue=lookupMap.get(value.toLowerCase
                ( )) ; if( resultingValue
                    == null) { if( defaultValue
                        != null){returndefaultValue.
                    getText
                    ( ) ;}
                        throw new InvalidParserConfigurationException ( "Fixed value >>" + value+"<< is missing in lookup: \""+lookup . getText()
                +
                "\" " );
            }

            returnresultingValue
            ; } @OverridepublicStringvisitPathFixedValue (UserAgentTreeWalkerParser .
                PathFixedValueContext ctx){returnctx.value.
            getText
        ();}}.visit
    (

    requiredPattern

    ) ; }// ------------------------------------------public WalkResultevaluate ( ParseTreetree , Stringkey ,
        String value){ if
            (verbose){LOG. info( "Evaluate: {} => {}",key
            ,value);LOG. info("Pattern : {}"
            ,requiredPatternText);LOG. info("WalkList: {}"
        ,
        walkList ) ; }WalkResultresult=walkList. walk(tree
        , value); if
            (verbose){LOG. info ( "Evaluate: Result = {}" , result == null?"null":result.getValue
        (
        ) );
    }

    return result ;}public boolean
        usesIsNull (){returnwalkList.
    usesIsNull

    ( ) ;}public WalkList
        getWalkListForUnitTesting ()
    {

    return walkList ;}public void
        pruneTrailingStepsThatCannotFail(){walkList.
    pruneTrailingStepsThatCannotFail
(
