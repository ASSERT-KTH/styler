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

import nl.basjes.parse.
useragent .analyze.InvalidParserConfigurationException;importnl.basjes.parse.
useragent .analyze.Matcher;importnl.basjes.parse.useragent.analyze.
treewalker .steps.WalkList;importnl.basjes.parse.useragent.analyze.treewalker.
steps .WalkList.WalkResult;importnl.basjes.parse.
useragent .parser.UserAgentTreeWalkerBaseVisitor;importnl.basjes.parse.
useragent .parser.UserAgentTreeWalkerParser;importnl.basjes.parse.useragent.
parser .UserAgentTreeWalkerParser.MatcherPathLookupContext;importnl.basjes.parse.useragent.
parser .UserAgentTreeWalkerParser.MatcherPathLookupPrefixContext;importorg.antlr.
v4 .runtime.ParserRuleContext;importorg.antlr.
v4 .runtime.Token;importorg.antlr.v4.
runtime .tree.ParseTree;importorg.antlr.v4.
runtime .tree.RuleNode;import
org .slf4j.Logger;import

org .slf4j.LoggerFactory;import
java .io.Serializable;import

java
. util . Map ; /**
 * This class gets the symbol table (1 value) uses that to evaluate
 * the expression against the parsed user agent
 */
    public class TreeExpressionEvaluator implements Serializable { privatestaticfinalLoggerLOG=LoggerFactory.getLogger
    ( TreeExpressionEvaluator . class)

    ; private final booleanverbose
    ; private final StringrequiredPatternText
    ; private final Matchermatcher
    ; private final WalkListwalkList

    ; privatefinalString fixedValue;
                                   public TreeExpressionEvaluator(
                                   ParserRuleContext requiredPattern, Matcher
        matcher,boolean verbose ){this.requiredPatternText=
        requiredPattern.getText ( );
        this.matcher = matcher;
        this.verbose = verbose;this.fixedValue
        = calculateFixedValue ( requiredPattern);walkList =newWalkList(requiredPattern, matcher.getLookups(), matcher.getLookupSets
    (

    )
    , verbose );} /**
     * @return The fixed value in case of a fixed value. NULL if a dynamic value
     */
        public StringgetFixedValue
    (

    ) { returnfixedValue; }private String
        calculateFixedValue ( ParserRuleContextrequiredPattern){returnnew UserAgentTreeWalkerBaseVisitor

            <String
            > ( ){@ Overrideprotected boolean shouldVisitNextChild( RuleNode
                node , String currentResult)
            {

            returncurrentResult
            == null ;}@ Overrideprotected String aggregateResult( String
                aggregate , String nextResult ) { return nextResult==
            null

            ?aggregate
            : nextResult ;}@ Overridepublic String
                visitMatcherPathLookup (MatcherPathLookupContextctx){returnvisitLookups( ctx.matcher( ),ctx.lookup
            ,
            ctx.
            defaultValue ) ;}@ Overridepublic String
                visitMatcherPathLookupPrefix (MatcherPathLookupPrefixContextctx){returnvisitLookups( ctx.matcher( ),ctx.lookup
            ,

            ctx . defaultValue); }private String visitLookups( ParseTree matcherTree, Token
                lookup , Token defaultValue){Stringvalue
                = visit( matcherTree ); if
                    ( value==
                null
                )
                {

                returnnull;} // Now we know this is a fixed value. Yet we can have a problem in the lookup that was// configured. If we have this then this is a FATAL error (it will fail always everywhere). Map < String,String>lookupMap=matcher.getLookups().get(lookup
                . getText( ) ); if
                    ( lookupMap ==null) { thrownewInvalidParserConfigurationException("Missing lookup \"" + lookup.getText
                (

                ) + "\" " );}StringresultingValue=lookupMap.get(value
                . toLowerCase( ) ); if
                    ( resultingValue== null ){ if
                        ( defaultValue!=null){return
                    defaultValue
                    . getText ()
                        ; } throw new InvalidParserConfigurationException ( "Fixed value >>"+value+"<< is missing in lookup: \"" + lookup.getText
                (
                ) +"\" "
            )

            ;}
            return resultingValue ;}@Overridepublic StringvisitPathFixedValue (
                UserAgentTreeWalkerParser .PathFixedValueContextctx){returnctx.
            value
        .getText();}}
    .

    visit

    ( requiredPattern );} // ------------------------------------------public WalkResult evaluate( ParseTree tree, String
        key ,Stringvalue )
            {if(verbose){ LOG. info("Evaluate: {} => {}"
            ,key,value); LOG.info
            ("Pattern : {}",requiredPatternText); LOG.info
        (
        "WalkList: {}" , walkList );}WalkResultresult= walkList.walk
        ( tree,value )
            ;if(verbose){ LOG . info ( "Evaluate: Result = {}" , result==null?"null":result
        .
        getValue ()
    )

    ; } returnresult; }
        public booleanusesIsNull(){return
    walkList

    . usesIsNull (); }
        public WalkListgetWalkListForUnitTesting
    (

    ) { returnwalkList; }
        publicvoidpruneTrailingStepsThatCannotFail(){
    walkList
.
