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
package nl.basjes.parse.useragent.analyze;

import org.antlr.v4.runtime.tree.ParseTree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public final class MatchesList implements Collection<MatchesList.Match> , Serializable {

    public static final class Match implements Serializable {
        private String key;
        private String value;
        private ParseTree result;
        public Match (String

        key ,Stringvalue ,ParseTree result ){ fill (key ,
            value,result) ;} publicvoidfill
        (

        String nKey ,StringnValue ,ParseTree nResult ){ this .key =
            nKey;this . value=
            nValue;this . result=
            nResult;} public StringgetKey
        (

        ) { returnkey; }
            public StringgetValue
        (

        ) { returnvalue; }
            public ParseTreegetResult
        (

        ) { returnresult; }
            } privateint
        size
    ;

    private int maxSize;
    private Match []

    allElements ;publicMatchesList (int

    newMaxSize ){maxSize =newMaxSize ;
        size = 0;

        allElements = newMatch
        [ maxSize ] ;for(inti
        = 0; i < maxSize; i ++ ){ allElements[i ]
            =newMatch( null , null,null) ;} }@Override
        public
    int

    size(
    ) { returnsize; }
        @ Overridepublic
    boolean

    isEmpty(
    ) { returnsize== 0
        ; } @ Overridepublic
    void

    clear(
    ) { size=0 ;
        } public booleanadd
    (

    String key ,Stringvalue ,ParseTree result ){ if (size >=
        maxSize ){ increaseCapacity () ;
            }allElements[size
        ]

        .fill(key,value,result) ;size ++;return
        true;}
        @ Overridepublic
    Iterator

    <Match
    > iterator(){ returnnewIterator <
        Match > (){intoffset= 0
            ; @ Override publicboolean

            hasNext(
            ) { returnoffset< size
                ; } @ Overridepublic
            Match

            next(
            ) { if(! hasNext
                ( )){thrownewNoSuchElementException (
                    "Array index out of bounds" ) ;}returnallElements[
                offset
                ++ ];}};}
            @
        Overridepublic
    Object

    []
    toArray (){ returnArrays. copyOf
        ( this.allElements,this.size) ;}privatestaticfinal
    int

    CAPACITY_INCREASE = 3 ; private void increaseCapacity(

    ) { intnewMaxSize= maxSize
        + CAPACITY_INCREASE ; Match[ ]newAllElements
        =newMatch [ newMaxSize ] ;System.arraycopy(
        allElements,0,newAllElements, 0, maxSize) ;for (inti
        = maxSize; i < newMaxSize; i ++ ){ newAllElements[i ]
            =newMatch( null , null,null) ;} allElements=newAllElements
        ;
        maxSize = newMaxSize;
        } public List<
    String

    > toStrings(){ List<String >
        result=newArrayList < > ( size);for(Matchmatch
        : this) {result .add (
            "{ \""+match.key + "\"=\""+match . value + "\" }"); } returnresult;
        }
        // ============================================================ // Everything else is NOT supported// ============================================================
    @

Override
public
boolean

    add(
    Match match ){throw newUnsupportedOperationException (
        ) ; }@Overridepublic
    boolean

    addAll(
    Collection < ?extendsMatch>collection ) {throw newUnsupportedOperationException (
        ) ; }@Overridepublic
    boolean

    remove(
    Object o ){throw newUnsupportedOperationException (
        ) ; }@Overridepublic
    boolean

    removeAll(
    Collection < ?>collection){throw newUnsupportedOperationException (
        ) ; }@Overridepublic
    boolean

    retainAll(
    Collection < ?>collection){throw newUnsupportedOperationException (
        ) ; }@Overridepublic
    boolean

    contains(
    Object o ){throw newUnsupportedOperationException (
        ) ; }@Overridepublic
    boolean

    containsAll(
    Collection < ?>collection){throw newUnsupportedOperationException (
        ) ; }@Overridepublic
    <

    T>
    T []toArray (T[ ]ts){throw newUnsupportedOperationException (
        ) ; }}