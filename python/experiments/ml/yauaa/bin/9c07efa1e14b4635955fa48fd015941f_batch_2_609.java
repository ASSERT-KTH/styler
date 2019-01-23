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
package nl.basjes.parse.useragent.utils;

import java.io.Serializable;
import java.util.Map;

public class PrefixLookup implements Serializable {

    public static class PrefixTrie implements Serializable {private
    PrefixTrie [          ]childNodes
    ; private      intcharIndex

    ; private booleancaseSensitive

    ; privateStringtheValue ;public PrefixTrie
        (booleancaseSensitive) {this(
    caseSensitive

    , 0); }private PrefixTrie (boolean caseSensitive
        ,intcharIndex ) {this
        .caseSensitive= caseSensitive ;this
    .

    charIndex = charIndex;} privatevoid add (String prefix
        , Stringvalue ) {if(charIndex==prefix .
            length ( ))
            {theValue
        =

        value ; return ;}charmyChar=prefix. charAt
        ( charIndex) ; // This will give us the ASCII value of the char if ( myChar <32 ||
            myChar > 126){thrownew
        IllegalArgumentException

        ( "Only readable ASCII is allowed as key !!!") ; }if (
            childNodes == null ){childNodes=newPrefixLookup.
        PrefixTrie

        [ 128]; }
            if
            ( caseSensitive){// If case sensitive we 'just' build the treeif ( childNodes[ myChar
                ]==null) { childNodes [myChar]= new PrefixTrie (true,
            charIndex
            +1);}childNodes[myChar] .add(
        prefix , value
            )
            ;
            }
            else { // If case INsensitive we build the tree // and we link the same child to both the// lower and uppercase entries in the child array.charlower=Character.
            toLowerCase ( myChar );charupper=Character.

            toUpperCase (myChar);if ( childNodes[ lower
                ]==null) { childNodes [lower]= new PrefixTrie (false,
            charIndex
            +1);}childNodes[lower] .add(

            prefix ,value);if ( childNodes[ upper
                ]==null) { childNodes[upper]=
            childNodes
        [
    lower

    ] ; }}} publicString find
        ( Stringinput ) {if(charIndex== input . length () ||
            childNodes ==null
        )

        { return theValue ;}charmyChar=input. charAt
        ( charIndex) ; // This will give us the ASCII value of the char if ( myChar <32 ||
            myChar >126 )
        {

        return theValue ; // Cannot store these, so this is where it ends.}PrefixTriechild=
        childNodes [myChar ] ;if (
            child ==null
        )

        { return theValue ;}StringreturnValue=child.
        find (input ) ;return ( returnValue == null)
    ?

theValue

: returnValue ;}

} privatePrefixTrieprefixPrefixTrie;publicPrefixLookup (Map <String , String> prefixList
    ,
    boolean caseSensitive ) {// Translate the map into a different structure.prefixPrefixTrie=new
    PrefixTrie(caseSensitive);prefixList. forEach( ( key,value)->prefixPrefixTrie .add(key
,

value ) );} publicString findLongestMatchingPrefix
    ( Stringinput){returnprefixPrefixTrie.
find

(
