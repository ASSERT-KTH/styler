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

    private Match[] allElements;

    public MatchesList(int newMaxSize) {
        maxSize = newMaxSize;

        size = 0;
        allElements = new Match[maxSize];
        for (int i = 0; i < maxSize                       ; i ++) {allElements[ i
            ]=newMatch ( null ,null,null ); }}@
        Override
    public

    intsize
    ( ) {returnsize ;
        } @Override
    public

    booleanisEmpty
    ( ) {returnsize ==
        0 ; } @Override
    public

    voidclear
    ( ) {size= 0
        ; } publicboolean
    add

    ( String key,String value, ParseTree result) { if( size
        >= maxSize) { increaseCapacity( )
            ;}allElements[
        size

        ].fill(key,value,result ); size++;
        returntrue;
        } @Override
    public

    Iterator<
    Match >iterator() {returnnew Iterator
        < Match >(){intoffset =
            0 ; @ Overridepublic

            booleanhasNext
            ( ) {returnoffset <
                size ; } @Override
            public

            Matchnext
            ( ) {if( !
                hasNext ()){thrownew NoSuchElementException
                    ( "Array index out of bounds" );}returnallElements
                [
                offset ++];}};
            }
        @Override
    public

    Object[
    ] toArray() {returnArrays .
        copyOf (this.allElements,this.size );}privatestatic
    final

    int CAPACITY_INCREASE = 3 ; private voidincreaseCapacity

    ( ) {intnewMaxSize =
        maxSize + CAPACITY_INCREASE ;Match []
        newAllElements=new Match [ newMaxSize ];System.arraycopy
        (allElements,0,newAllElements ,0 ,maxSize ); for(int
        i =maxSize ; i <newMaxSize ; i ++) {newAllElements[ i
            ]=newMatch ( null ,null,null ); }allElements=
        newAllElements
        ; maxSize =newMaxSize
        ; } publicList
    <

    String >toStrings() {List< String
        >result=new ArrayList < > (size);for(Match
        match :this ){ result. add
            ("{ \""+match. key +"\"=\""+ match . value +"\" }") ; }returnresult
        ;
        } // ============================================================// Everything else is NOT supported
    // ============================================================

@
Override
public

    booleanadd
    ( Match match){ thrownew UnsupportedOperationException
        ( ) ;}@Override
    public

    booleanaddAll
    ( Collection <?extendsMatch> collection ){ thrownew UnsupportedOperationException
        ( ) ;}@Override
    public

    booleanremove
    ( Object o){ thrownew UnsupportedOperationException
        ( ) ;}@Override
    public

    booleanremoveAll
    ( Collection <?>collection){ thrownew UnsupportedOperationException
        ( ) ;}@Override
    public

    booleanretainAll
    ( Collection <?>collection){ thrownew UnsupportedOperationException
        ( ) ;}@Override
    public

    booleancontains
    ( Object o){ thrownew UnsupportedOperationException
        ( ) ;}@Override
    public

    booleancontainsAll
    ( Collection <?>collection){ thrownew UnsupportedOperationException
        ( ) ;}@Override
    public

    <T
    > T[] toArray(T []ts){ thrownew UnsupportedOperationException
        ( ) ;}}