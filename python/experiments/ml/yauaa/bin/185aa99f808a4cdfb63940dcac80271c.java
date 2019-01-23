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

import nl.basjes.parse.useragent.analyze.InvalidParserConfigurationException
; importnl.basjes.parse.useragent.analyze.Matcher;importnl.
basjes .parse.useragent.analyze.treewalker.steps.WalkList;importnl.basjes.
parse .useragent.analyze.treewalker.steps.WalkList.WalkResult
; importnl.basjes.parse.useragent.parser.UserAgentTreeWalkerBaseVisitor
; importnl.basjes.parse.useragent.parser.UserAgentTreeWalkerParser;import
nl .basjes.parse.useragent.parser.UserAgentTreeWalkerParser.MatcherPathLookupContext;import
nl .basjes.parse.useragent.parser.UserAgentTreeWalkerParser
. MatcherPathLookupPrefixContext;importorg.antlr.v4.runtime
. ParserRuleContext;importorg.antlr.v4.runtime.Token
; importorg.antlr.v4.runtime.tree.ParseTree
; importorg.antlr.v4
. runtime.tree.RuleNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import
java . io . Serializable ;
    import java . util . Map ;/**
 * This class gets the symbol table (1 value) uses that to evaluate
 * the expression against the parsed user agent
 */publicclassTreeExpressionEvaluatorimplementsSerializable{private
    static final Logger LOG=

    LoggerFactory . getLogger (TreeExpressionEvaluator
    . class ) ;private
    final boolean verbose ;private
    final String requiredPatternText ;private

    final Matchermatcher; privatefinal
                                   WalkList walkList;
                                   private finalString fixedValue
        ;publicTreeExpressionEvaluator ( ParserRuleContextrequiredPattern,Matchermatcher,
        booleanverbose) { this.
        requiredPatternText=requiredPattern . getText(
        );this . matcher=matcher;this
        . verbose = verbose;this. fixedValue=calculateFixedValue(requiredPattern) ;walkList=newWalkList( requiredPattern,matcher
    .

    getLookups
    ( ) ,matcher. getLookupSets
        ( ),
    verbose

    ) ; }/**
     * @return The fixed value in case of a fixed value. NULL if a dynamic value
     */public StringgetFixedValue (
        ) { returnfixedValue;}privateString calculateFixedValue

            (ParserRuleContext
            requiredPattern ) {returnnew UserAgentTreeWalkerBaseVisitor< String >( )
                { @ Override protectedboolean
            shouldVisitNextChild

            (RuleNode
            node , StringcurrentResult) {return currentResult ==null ;
                } @ Override protected String aggregateResult ( Stringaggregate
            ,

            StringnextResult
            ) { returnnextResult== null? aggregate
                : nextResult;}@OverridepublicStringvisitMatcherPathLookup (MatcherPathLookupContextctx) {returnvisitLookups(ctx
            .
            matcher(
            ) , ctx.lookup ,ctx .
                defaultValue );}@OverridepublicStringvisitMatcherPathLookupPrefix (MatcherPathLookupPrefixContextctx) {returnvisitLookups(ctx
            .

            matcher ( ),ctx .lookup , ctx. defaultValue ); }
                private String visitLookups (ParseTreematcherTree,Token
                lookup ,Token defaultValue ){ String
                    value =visit
                (
                matcherTree
                )

                ;if(value ==null ) { returnnull;}// Now we know this is a fixed value. Yet we can have a problem in the lookup that was// configured. If we have this then this is a FATAL error (it will fail always everywhere).Map<String,String>lookupMap=matcher
                . getLookups( ) .get (
                    lookup . getText() ) ;if(lookupMap== null ){throw
                new

                InvalidParserConfigurationException ( "Missing lookup \"" +lookup.getText()+"\" ");}
                String resultingValue= lookupMap .get (
                    value .toLowerCase ( )) ;
                        if (resultingValue==null){
                    if
                    ( defaultValue !=null
                        ) { return defaultValue . getText ();}throw new InvalidParserConfigurationException("Fixed value >>"
                +
                value +"<< is missing in lookup: \""
            +

            lookup.
            getText ( )+"\" "); }return resultingValue
                ; }@OverridepublicStringvisitPathFixedValue(UserAgentTreeWalkerParser
            .
        PathFixedValueContextctx){returnctx.
    value

    .

    getText ( );} }. visit (requiredPattern ) ;} // ------------------------------------------
        public WalkResultevaluate( ParseTree
            tree,Stringkey,String value) {if(
            verbose){LOG.info ("Evaluate: {} => {}",
            key,value);LOG .info(
        "Pattern : {}"
        , requiredPatternText ) ;LOG.info("WalkList: {}" ,walkList)
        ; }WalkResultresult =
            walkList.walk(tree, value ) ; if ( verbose ){LOG.info("Evaluate: Result = {}"
        ,
        result ==null
    ?

    "null" : result.getValue (
        ) );}returnresult;
    }

    public boolean usesIsNull() {
        return walkList.
    usesIsNull

    ( ) ;}public WalkList
        getWalkListForUnitTesting(){returnwalkList
    ;
}
