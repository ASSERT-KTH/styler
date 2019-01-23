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

import nl.          basjes.parse.useragent

. analyze.InvalidParserConfigurationException;importnl.basjes.parse.useragent
. analyze.Matcher;importnl.basjes.parse.useragent
. analyze.treewalker.steps.WalkList;importnl.basjes.parse.useragent
. analyze.treewalker.steps.WalkList.WalkResult;importnl.basjes.parse.useragent
. parser.UserAgentTreeWalkerBaseVisitor;importnl.basjes.parse.useragent
. parser.UserAgentTreeWalkerParser;importnl.basjes.parse.useragent
. parser.UserAgentTreeWalkerParser.MatcherPathLookupContext;importnl.basjes.parse.useragent
. parser.UserAgentTreeWalkerParser.MatcherPathLookupPrefixContext;importorg.antlr.v4.runtime
. ParserRuleContext;importorg.antlr.v4.runtime
. Token;importorg.antlr.v4.runtime
. tree.ParseTree;importorg.antlr.v4.runtime
. tree.RuleNode;importorg.slf4j.Logger;import
org .slf4j.LoggerFactory;import
java .io.Serializable;import

java .util.Map;/**
 * This class gets the symbol table (1 value) uses that to evaluate
 * the expression against the parsed user agent
 */
public classTreeExpressionEvaluatorimplementsSerializable{private

static
final Logger LOG = LoggerFactory .
    getLogger ( TreeExpressionEvaluator . class ) ;privatefinalbooleanverbose;privatefinalString
    requiredPatternText ; private finalMatcher

    matcher ; private finalWalkList
    walkList ; private finalString
    fixedValue ; public TreeExpressionEvaluator(
    ParserRuleContext requiredPattern , Matchermatcher

    , booleanverbose) {this
                                   . requiredPatternText=
                                   requiredPattern .getText (
        );this . matcher=matcher;this.
        verbose=verbose ; this.
        fixedValue=calculateFixedValue ( requiredPattern)
        ;walkList= new WalkList(requiredPattern,matcher
        . getLookups ( ),matcher. getLookupSets(),verbose) ;}/**
     * @return The fixed value in case of a fixed value. NULL if a dynamic value
     */publicStringgetFixedValue (){
    return

    fixedValue
    ; } privateStringcalculateFixedValue (
        ParserRuleContext requiredPattern)
    {

    return new UserAgentTreeWalkerBaseVisitor<String >( )
        { @ OverrideprotectedbooleanshouldVisitNextChild(RuleNode node

            ,String
            currentResult ) {returncurrentResult ==null ; }@ Override
                protected String aggregateResult (String
            aggregate

            ,String
            nextResult ) {returnnextResult ==null ? aggregate: nextResult
                ; } @ Override public String visitMatcherPathLookup (MatcherPathLookupContext
            ctx

            ){
            return visitLookups (ctx. matcher( )
                , ctx.lookup,ctx.defaultValue) ;}@Override publicStringvisitMatcherPathLookupPrefix(MatcherPathLookupPrefixContext
            ctx
            ){
            return visitLookups (ctx. matcher( )
                , ctx.lookup,ctx.defaultValue) ;}privateString visitLookups(ParseTreematcherTree,
            Token

            lookup , TokendefaultValue) {String value =visit ( matcherTree) ;
                if ( value ==null){return
                null ;} // Now we know this is a fixed value. Yet we can have a problem in the lookup that was // configured. If we have this then this is a FATAL error (it will fail always everywhere).Map <
                    String ,String
                >
                lookupMap
                =

                matcher.getLookups( ). get ( lookup.getText());if(lookupMap==null){throw
                new InvalidParserConfigurationException( "Missing lookup \"" +lookup .
                    getText ( )+"\" " ) ;}StringresultingValue= lookupMap .get(
                value

                . toLowerCase ( ));if(resultingValue==null){if
                ( defaultValue!= null ){ return
                    defaultValue .getText ( ); }
                        throw newInvalidParserConfigurationException("Fixed value >>"+value
                    +
                    "<< is missing in lookup: \"" + lookup.
                        getText ( ) + "\" " ) ;}returnresultingValue; } @Overridepublic
                String
                visitPathFixedValue (UserAgentTreeWalkerParser
            .

            PathFixedValueContextctx
            ) { returnctx.value. getText( )
                ; }}.visit(requiredPattern);
            }
        // ------------------------------------------publicWalkResultevaluate(ParseTreetree
    ,

    String

    key , Stringvalue) {if ( verbose) { LOG. info
        ( "Evaluate: {} => {}",key ,
            value);LOG.info ("Pattern : {}" ,requiredPatternText)
            ;LOG.info("WalkList: {}" ,walkList)
            ;}WalkResultresult=walkList .walk(
        tree
        , value ) ;if(verbose){ LOG.info
        ( "Evaluate: Result = {}",result ==
            null?"null":result. getValue ( ) ) ; } returnresult;}publicbooleanusesIsNull
        (
        ) {return
    walkList

    . usesIsNull (); }
        public WalkListgetWalkListForUnitTesting(){return
    walkList

    ; } publicvoidpruneTrailingStepsThatCannotFail (
        ) {walkList
    .

    pruneTrailingStepsThatCannotFail ( );} }
        