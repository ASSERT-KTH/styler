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

public final class MatchesList implements Collection<MatchesList.Match> ,Serializable
{ public staticfinal

class MatchimplementsSerializable {private String key; private Stringvalue ;
    privateParseTreeresult; publicMatch (Stringkey
,

String value ,ParseTreeresult ){ fill (key , value, result
    );} public voidfill
    (StringnKey , StringnValue
    ,ParseTreenResult ) {this
.

key = nKey;this .
    value =nValue
;

this . result=nResult ;
    } publicString
getKey

( ) {returnkey ;
    } publicString
getValue
(

) { returnvalue
; } publicParseTree

getResult (){ returnresult

; }}private intsize ;
private int maxSize;

private Match []
allElements ; public MatchesList(intnewMaxSize)
{ maxSize= newMaxSize ; size= 0 ; allElements= newMatch[ maxSize
    ];for( int i =0;i <maxSize ;i++
)
{

allElements[
i ] =newMatch (
null ,null
,

null)
; } }@Override public
int size ( ){
return

size;
} @ Overridepublicboolean isEmpty
( ) {return
size

== 0 ;}@ Overridepublic void clear( ) {size =
0 ;} public booleanadd (
    Stringkey,String
value

,ParseTreeresult){if(size>= maxSize) {increaseCapacity(
);}
allElements [size
]

.fill
( key,value, result); size
++ ; returntrue;}@Override public
    Iterator < Match >iterator

    ()
    { return newIterator< Match
        > ( ) {int
    offset

    =0
    ; @ Overridepublicboolean hasNext
        ( ){returnoffset<size ;
            } @ OverridepublicMatchnext(
        )
        { if(!hasNext()
    )
{throw
new

NoSuchElementException(
"Array index out of bounds" );} returnallElements[ offset
++ ];}};}@Override publicObject[]toArray
(

) { return Arrays . copyOf (this

. allElements ,this. size
) ; } privatestatic finalint
CAPACITY_INCREASE=3 ; private void increaseCapacity(){int
newMaxSize=maxSize+CAPACITY_INCREASE; Match[ ]newAllElements =new Match[newMaxSize
] ;System . arraycopy (allElements , 0 ,newAllElements ,0, maxSize
    );for( int i =maxSize;i <newMaxSize ;i++
)
{ newAllElements [i
] = newMatch
(

null ,null,null );} allElements
=newAllElements;maxSize = newMaxSize ; }publicList<String>toStrings
( ){ List< String> result
    =newArrayList<> ( size); for ( Match match:this ) {result.
add
( "{ \""+
match

.
key
+

"\"=\""+
match . value+"\" }" ); }
return result ;}// ============================================================// Everything else is NOT supported
// ============================================================

@Override
public boolean add(Matchmatch) { thrownew UnsupportedOperationException( )
; } @Overridepublicboolean
addAll

(Collection
< ? extendsMatch> collection) {
throw new UnsupportedOperationException();
}

@Override
public boolean remove(Objecto){ thrownew UnsupportedOperationException
( ) ;}@Override
public

booleanremoveAll
( Collection <?>collection){ thrownew UnsupportedOperationException
( ) ;}@Override
public

booleanretainAll
( Collection <?> collection) {
throw new UnsupportedOperationException();
}

@Override
public boolean contains(Objecto){ thrownew UnsupportedOperationException
( ) ;}@Override
public

booleancontainsAll
( Collection<? >collection) {thrownewUnsupportedOperationException( ); }
@ Override public<T>
T
[
