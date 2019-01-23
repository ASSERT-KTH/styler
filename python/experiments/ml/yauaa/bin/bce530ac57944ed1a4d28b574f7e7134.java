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
import nl.basjes.parse.useragent.analyze.treewalker.steps.WalkList.
WalkResult;importnl.basjes.parse.useragent.parser
. UserAgentTreeWalkerBaseVisitor;importnl.basjes.parse.useragent.parser
. UserAgentTreeWalkerParser;importnl.basjes.parse.useragent.parser.UserAgentTreeWalkerParser
. MatcherPathLookupContext;importnl.basjes.parse.useragent.parser.UserAgentTreeWalkerParser
. MatcherPathLookupPrefixContext;importorg.antlr.v4.runtime
. ParserRuleContext;importorg.antlr.v4.runtime
. Token;importorg.antlr.v4.runtime.tree
. ParseTree;importorg.antlr.v4.runtime.tree
. RuleNode;importorg.slf4j
. Logger;importorg.slf4j

. LoggerFactory;importjava.io
. Serializable;importjava.util

.
Map ; /**
 * This class gets the symbol table (1 value) uses that to evaluate
 * the expression against the parsed user agent
 */ public class TreeExpressionEvaluator
    implements Serializable { private static final LoggerLOG=LoggerFactory.getLogger(TreeExpressionEvaluator.
    class ) ; privatefinal

    boolean verbose ; privatefinal
    String requiredPatternText ; privatefinal
    Matcher matcher ; privatefinal
    WalkList walkList ; privatefinal

    String fixedValue;public TreeExpressionEvaluator(
                                   ParserRuleContext requiredPattern,
                                   Matcher matcher, boolean
        verbose){ this .requiredPatternText=requiredPattern.getText
        (); this .matcher
        =matcher; this .verbose
        =verbose; this .fixedValue=calculateFixedValue(
        requiredPattern ) ; walkList=newWalkList (requiredPattern,matcher.getLookups (),matcher.getLookupSets (),
    verbose

    )
    ; } /**
     * @return The fixed value in case of a fixed value. NULL if a dynamic value
     */publicString getFixedValue
        ( ){
    return

    fixedValue ; }privateString calculateFixedValue( ParserRuleContext
        requiredPattern ) {returnnewUserAgentTreeWalkerBaseVisitor<String >

            ()
            { @ Overrideprotectedboolean shouldVisitNextChild( RuleNode node, String
                currentResult ) { returncurrentResult
            ==

            null;
            } @ OverrideprotectedString aggregateResult( String aggregate, String
                nextResult ) { return nextResult == null ?aggregate
            :

            nextResult;
            } @ OverridepublicString visitMatcherPathLookup( MatcherPathLookupContext
                ctx ){returnvisitLookups(ctx.matcher (),ctx .lookup,ctx.
            defaultValue
            );
            } @ OverridepublicString visitMatcherPathLookupPrefix( MatcherPathLookupPrefixContext
                ctx ){returnvisitLookups(ctx.matcher (),ctx .lookup,ctx.
            defaultValue

            ) ; }privateString visitLookups( ParseTree matcherTree, Token lookup, Token
                defaultValue ) { Stringvalue=visit(
                matcherTree ); if (value ==
                    null ){
                return
                null
                ;

                }// Now we know this is a fixed value. Yet we can have a problem in the lookup that was// configured. If we have this then this is a FATAL error (it will fail always everywhere).Map <String , String >lookupMap=matcher.getLookups().get(lookup.getText(
                ) ); if (lookupMap ==
                    null ) {thrownew InvalidParserConfigurationException ("Missing lookup \""+lookup. getText ()+
                "\" "

                ) ; } StringresultingValue=lookupMap.get(value.toLowerCase(
                ) ); if (resultingValue ==
                    null ){ if (defaultValue !=
                        null ){returndefaultValue.getText
                    (
                    ) ; }throw
                        new InvalidParserConfigurationException ( "Fixed value >>" + value +"<< is missing in lookup: \""+lookup. getText ()+
                "\" "
                ) ;}
            return

            resultingValue;
            } @ OverridepublicStringvisitPathFixedValue( UserAgentTreeWalkerParser. PathFixedValueContext
                ctx ){returnctx.value.getText
            (
        );}}.visit(
    requiredPattern

    )

    ; } // ------------------------------------------publicWalkResult evaluate( ParseTree tree, String key, String
        value ){if (
            verbose){LOG.info ("Evaluate: {} => {}" ,key,
            value);LOG.info ("Pattern : {}",
            requiredPatternText);LOG.info ("WalkList: {}",
        walkList
        ) ; } WalkResultresult=walkList.walk (tree,
        value );if (
            verbose){LOG.info ( "Evaluate: Result = {}" , result == null ?"null":result.getValue(
        )
        ) ;}
    return

    result ; }publicboolean usesIsNull
        ( ){returnwalkList.usesIsNull
    (

    ) ; }publicWalkList getWalkListForUnitTesting
        ( ){
    return

    walkList ; }publicvoid pruneTrailingStepsThatCannotFail
        (){walkList.pruneTrailingStepsThatCannotFail
    (
)
