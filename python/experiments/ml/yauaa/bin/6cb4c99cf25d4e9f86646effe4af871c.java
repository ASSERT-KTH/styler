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

public final class MatchesList implements Collection<MatchesList.Match>, Serializable {

    public static final class Match implements Serializable {
        private String key;
        private String value;
        private ParseTree result;

        public Match(String key, String value, ParseTree result) {
            fill(key, value, result);
        }

        public void fill(String nKey, String nValue, ParseTree nResult) {
            this.key = nKey;
            this.value = nValue;
            this.result = nResult;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public ParseTree getResult() {
            return result;
        }
    }

    private int size;
    private int maxSize;

    private Match[] allElements

        ; publicMatchesList( intnewMaxSize

        ) {maxSize= newMaxSize; size
            = 0 ;allElements

            = new Match[
            maxSize ] ; for(inti=
            0 ;i < maxSize ;i ++ ) {allElements [i] =
                newMatch(null , null ,null); }} @Overridepublic
            int
        size

        ()
        { return size;} @
            Override publicboolean
        isEmpty

        ()
        { return size==0 ;
            } @ Override publicvoid
        clear

        ()
        { size =0; }
            public boolean add(
        String

        key , Stringvalue, ParseTreeresult ) {if ( size>= maxSize
            ) {increaseCapacity ( ); }
                allElements[size]
            .

            fill(key,value,result); size++ ;returntrue
            ;}@
            Override publicIterator
        <

        Match>
        iterator (){return newIterator< Match
            > ( ){intoffset=0 ;
                @ Override public booleanhasNext

                ()
                { return offset<size ;
                    } @ Override publicMatch
                next

                ()
                { if (!hasNext (
                    ) ){thrownewNoSuchElementException( "Array index out of bounds"
                        ) ; }returnallElements[offset
                    ++
                    ] ;}};}@
                Override
            publicObject
        [

        ]toArray
        ( ){return Arrays.copyOf (
            this .allElements,this.size); }privatestaticfinalint
        CAPACITY_INCREASE

        = 3 ; private void increaseCapacity ()

        { int newMaxSize=maxSize +
            CAPACITY_INCREASE ; Match [] newAllElements=
            newMatch[ newMaxSize ] ; System.arraycopy(allElements
            ,0,newAllElements,0 ,maxSize ); for( inti=
            maxSize ;i < newMaxSize ;i ++ ) {newAllElements [i] =
                newMatch(null , null ,null); }allElements =newAllElements;
            maxSize
            = newMaxSize ;}
            public List <String
        >

        toStrings (){List <String> result
            =newArrayList< > ( size );for(Matchmatch:
            this ){ result. add( "{ \""
                +match.key+ "\"=\"" +match. value + "\" }" );} return result;}
            // ============================================================
            // Everything else is NOT supported // ============================================================@
        Override

    public
    boolean
    add

        (Match
        match ) {thrownew UnsupportedOperationException( )
            ; } @Overridepublicboolean
        addAll

        (Collection
        < ? extendsMatch>collection) { thrownew UnsupportedOperationException( )
            ; } @Overridepublicboolean
        remove

        (Object
        o ) {thrownew UnsupportedOperationException( )
            ; } @Overridepublicboolean
        removeAll

        (Collection
        < ? >collection){thrownew UnsupportedOperationException( )
            ; } @Overridepublicboolean
        retainAll

        (Collection
        < ? >collection){thrownew UnsupportedOperationException( )
            ; } @Overridepublicboolean
        contains

        (Object
        o ) {thrownew UnsupportedOperationException( )
            ; } @Overridepublicboolean
        containsAll

        (Collection
        < ? >collection){thrownew UnsupportedOperationException( )
            ; } @Overridepublic<
        T

        >T
        [ ]toArray( T[] ts){thrownew UnsupportedOperationException( )
            ; } }