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

import nl.basjes.parse.useragent.analyze.InvalidParserConfigurationException;importnl.basjes.parse.useragent.analyze.Matcher;
import nl.basjes.parse.useragent.analyze.treewalker.steps.WalkList;importnl
. basjes.parse.useragent.analyze.treewalker.steps.
WalkList .WalkResult;importnl.basjes.parse.useragent.
parser .UserAgentTreeWalkerBaseVisitor;importnl.basjes.parse.useragent.parser.
UserAgentTreeWalkerParser ;importnl.basjes.parse.useragent.parser.UserAgentTreeWalkerParser.
MatcherPathLookupContext ;importnl.basjes.parse.useragent.
parser .UserAgentTreeWalkerParser.MatcherPathLookupPrefixContext;importorg.antlr.
v4 .runtime.ParserRuleContext;importorg.antlr.v4.
runtime .Token;importorg.antlr.v4.runtime.
tree .ParseTree;importorg.
antlr .v4.runtime.tree

. RuleNode;importorg.slf4j
. Logger;importorg.slf4j

.
LoggerFactory ; import java . io
    . Serializable ; import java . util.Map;/**
 * This class gets the symbol table (1 value) uses that to evaluate
 * the expression against the parsed user agent
 */publicclassTreeExpressionEvaluatorimplements
    Serializable { private staticfinal

    Logger LOG = LoggerFactory.
    getLogger ( TreeExpressionEvaluator .class
    ) ; private finalboolean
    verbose ; private finalString

    requiredPatternText ;privatefinal Matchermatcher
                                   ; privatefinal
                                   WalkList walkList; private
        finalStringfixedValue ; publicTreeExpressionEvaluator(ParserRuleContextrequiredPattern,
        Matchermatcher, boolean verbose)
        {this. requiredPatternText =requiredPattern
        .getText( ) ;this.matcher=
        matcher ; this .verbose=verbose ;this.fixedValue=calculateFixedValue (requiredPattern);walkList= newWalkList(
    requiredPattern

    ,
    matcher . getLookups() ,
        matcher .getLookupSets
    (

    ) , verbose); }/**
     * @return The fixed value in case of a fixed value. NULL if a dynamic value
     */ public
        String getFixedValue (){returnfixedValue; }

            privateString
            calculateFixedValue ( ParserRuleContextrequiredPattern) {return new UserAgentTreeWalkerBaseVisitor< String
                > ( ) {@
            Override

            protectedboolean
            shouldVisitNextChild ( RuleNodenode, StringcurrentResult ) {return currentResult
                == null ; } @ Override protected StringaggregateResult
            (

            Stringaggregate
            , String nextResult){ returnnextResult ==
                null ?aggregate:nextResult;}@Override publicStringvisitMatcherPathLookup( MatcherPathLookupContextctx){return
            visitLookups
            (ctx
            . matcher (), ctx. lookup
                , ctx.defaultValue);}@Override publicStringvisitMatcherPathLookupPrefix( MatcherPathLookupPrefixContextctx){return
            visitLookups

            ( ctx .matcher( ), ctx .lookup , ctx. defaultValue
                ) ; } privateStringvisitLookups(ParseTree
                matcherTree ,Token lookup ,Token defaultValue
                    ) {String
                value
                =
                visit

                (matcherTree); if( value == null){returnnull;}// Now we know this is a fixed value. Yet we can have a problem in the lookup that was// configured. If we have this then this is a FATAL error (it will fail always everywhere).Map<String,String>
                lookupMap =matcher . getLookups( )
                    . get (lookup. getText ());if ( lookupMap==null
                )

                { throw new InvalidParserConfigurationException("Missing lookup \""+lookup.getText()+"\" "
                ) ;} String resultingValue= lookupMap
                    . get( value .toLowerCase (
                        ) );if(resultingValue==
                    null
                    ) { if(
                        defaultValue != null ) { return defaultValue.getText() ; }thrownew
                InvalidParserConfigurationException
                ( "Fixed value >>"+
            value

            +"<< is missing in lookup: \""
            + lookup .getText()+ "\" ") ;
                } returnresultingValue;}@OverridepublicString
            visitPathFixedValue
        (UserAgentTreeWalkerParser.PathFixedValueContextctx){
    return

    ctx

    . value .getText( ); } }. visit (requiredPattern )
        ; }// ------------------------------------------public WalkResult
            evaluate(ParseTreetree,String key, Stringvalue)
            {if(verbose){ LOG.info
            ("Evaluate: {} => {}",key,value );LOG
        .
        info ( "Pattern : {}" ,requiredPatternText);LOG. info("WalkList: {}"
        , walkList); }
            WalkResultresult=walkList.walk ( tree , value ) ; if(verbose){LOG.
        info
        ( "Evaluate: Result = {}",
    result

    == null ?"null": result
        . getValue());}
    return

    result ; }publicboolean usesIsNull
        ( ){
    return

    walkList . usesIsNull() ;
        }publicWalkListgetWalkListForUnitTesting()
    {
return
