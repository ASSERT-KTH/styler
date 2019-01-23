/*
 * Copyright (c) 2002-2018 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.collection.primitive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection
; importjava.util.HashSet
; importjava.util.Iterator
; importjava.util.List
; importjava.util.NoSuchElementException
; importjava.util.Objects
; importjava.util.Set;import
java .util.function.LongFunction;import

java .util.function.LongPredicate;importorg.neo4j.
collection .primitive.base.Empty;import

org . neo4j.graphdb.Resource;importstatic
java . util.Arrays.copyOf;importstaticorg.neo4j.

collection
. primitive .
PrimitiveCommons
    . closeSafely ; /**
 * Basic and common primitive int collection utils and manipulations.
 *
 * @see PrimitiveIntCollections
 * @see Primitive
 */publicclass PrimitiveLongCollections { public staticfinallong[]

    EMPTY_LONG_ARRAY = new long [ 0 ] ;privatestatic
    final
        PrimitiveLongIteratorEMPTY
        = new PrimitiveLongBaseIterator()
        {
            @ Overrideprotected
        boolean
    fetchNext(

    ) {returnfalse
    ;
        } } ;private PrimitiveLongCollections ()
    {

    throw new AssertionError ("no instance" ) ;} public static
    PrimitiveLongIterator
        iterator ( finallong...items ){return new
        PrimitiveLongResourceCollections
            . PrimitiveLongBaseResourceIterator ( Resource .EMPTY)

            {private
            int index =-1
            ;
                @ Overrideprotected boolean fetchNext() { return++ index<items. length&&
            next
        (items
    [

    index
    ] ) ; }} ;} // Concating public
    static
        PrimitiveLongIterator concat( PrimitiveLongIterator...primitiveLongIterators) { return concat(
    Arrays

    . asList ( primitiveLongIterators) );}public static PrimitiveLongIterator
    concat
        ( Iterable <PrimitiveLongIterator >primitiveLongIterators){return newPrimitiveLongConcatingIterator
    (

    primitiveLongIterators . iterator () ) ;} public static PrimitiveLongIterator filter
    (
        PrimitiveLongIterator source ,final LongPredicate filter
        )
            {return
            new PrimitiveLongFilteringIterator (source ) { @
            Override
                public booleantest(long item ){
            return
        filter.
    test

    ( item ) ;} } ;} public static PrimitiveLongResourceIterator filter
    (
        PrimitiveLongResourceIterator source ,final LongPredicate filter
        )
            {return
            new PrimitiveLongResourceFilteringIterator (source ) { @
            Override
                public booleantest(long item ){
            return
        filter.
    test

    (
    item ) ; }} ; }// Range public static PrimitiveLongIterator
    range
        ( long start, longend ) {return
    new

    PrimitiveLongRangeIterator ( start ,end ) ;} public static long
    single
        (
        PrimitiveLongIterator
            iterator , longdefaultItem){try{ if
            (
                !iterator . hasNext(
                ) ){
            closeSafely
            ( iterator ) ;returndefaultItem;}long
            item = iterator.next() ;
            if
                ( iterator .hasNext ( ) ) { throw new NoSuchElementException (
                        "More than one item in " + iterator+", first:"+item +", second:"
            +
            iterator. next ()
            ) ;}
        closeSafely
        ( iterator ) ; return
        item
            ;} catch( NoSuchElementException exception)
            { closeSafely(
        iterator
    ,

    exception
    ) ; throw exception; } }/**
     * Returns the index of the given item in the iterator(zero-based). If no items in {@code iterator}
     * equals {@code item} {@code -1} is returned.
     *
     * @param item the item to look for.
     * @param iterator of items.
     * @return index of found item or -1 if not found.
     */ public static int
    indexOf
        ( PrimitiveLongIterator iterator , long item) {for(inti= 0; iterator
        .
            hasNext ( ) ; i++){if (
            item
                == iterator.
            next
        (
        ) ){return
    i

    ; } } return- 1;}public static PrimitiveLongSet
    asSet
        ( Collection < Long>collection) {PrimitiveLongSetset=Primitive .longSet
        ( collection . size( ) )
        ;
            for(Longnext : collection)
        {
        set .add
    (

    next ) ; }return set ; }
    public
        static PrimitiveLongSet asSet (PrimitiveLongIteratoriterator){PrimitiveLongSet
        set = Primitive.longSet() ;
        while
            (iterator.hasNext ()){set .add
        (
        iterator .next
    (

    ) ) ; }return set ; }
    public
        static PrimitiveLongSet asSet (PrimitiveLongSetset) {PrimitiveLongSetresult=Primitive .longSet
        ( set . size());PrimitiveLongIterator
        iterator = set.iterator() ;
        while
            (iterator.hasNext ()){result .add
        (
        iterator .next
    (

    ) ) ; }return result; } public
    static
        PrimitiveLongSet asSet ( long...values) {PrimitiveLongSetresult =Primitive
        . longSet ( values. length )
        ;
            for(longvalue : values)
        {
        result .add
    (

    value ) ;}return result;}public static< T>PrimitiveLongObjectMap< T >
    copy
        (PrimitiveLongObjectMap<T > original ){PrimitiveLongObjectMap< T>copy=Primitive .longObjectMap
        (original.size ( )) ; original .
        visitEntries
            ((key, value) -> {copy
            . put(
        key ,value
        ) ;return
    false

    ; } ) ;return copy ; }
    public
        static int count (PrimitiveLongIterator
        iterator ) { intcount=0;for (;iterator.hasNext( ); iterator
        .   next
        (
        ) ,count
    ++

    ) { // Just loop through this}return count; } public static
    long
        []asArray ( PrimitiveLongIterator iterator ){long[]
        array = new long[
        8 ] ; inti=0;for (; iterator
        .
            hasNext ( ) ; i++) {
            if
                ( i >=array .length ) { array =copyOf
            (
            array,i<< 1 );}array[i
        ]

        = iterator . next (); }
        if
            ( i <array .length ) {array
        =
        copyOf (array
    ,

    i ) ;}return array; }publicstaticlong [ ]
    asArray
        (Iterator< Long > iterator ){long[]
        array = new long[
        8 ] ; inti=0;for (; iterator
        .
            hasNext ( ) ; i++) {
            if
                ( i >=array .length ) { array =copyOf
            (
            array,i<< 1 );}array[i
        ]

        = iterator . next (); }
        if
            ( i <array .length ) {array
        =
        copyOf (array
    ,

    i ) ; }returnarray
    ;
        } publicstatic
    PrimitiveLongIterator

    emptyIterator ( ) {return EMPTY ;}publicstatic PrimitiveLongIterator toPrimitiveIterator
    (
        final Iterator <Long>
        iterator
            ){
            return new PrimitiveLongBaseIterator()
            {
                @ Override protectedbooleanfetchNext() {
                if
                    ( iterator . hasNext()){Long
                    nextValue = iterator . next (
                    )
                        ; if (null == nextValue)
                    {
                    throw newIllegalArgumentException ("Cannot convert null Long to primitive long");} returnnext
                (
                nextValue .longValue
            (
        ))
    ;

    } return false ;}}
    ;
        } publicstaticPrimitiveLongSetemptySet
    (

    ) { return Empty. EMPTY_PRIMITIVE_LONG_SET; } public
    static
        PrimitiveLongSetsetOf(long ...values ) {Objects
        . requireNonNull ( values,"Values array is null") ;PrimitiveLongSetset =Primitive
        . longSet ( values. length )
        ;
            for(longvalue : values)
        {
        set .add
    (

    value ) ;}return set;}public static< T >Iterator<T >map ( final LongFunction <
    T
        > mapFunction ,finalPrimitiveLongIteratorsource){
        return
            newIterator
            < T >()
            {
                @ OverridepublicbooleanhasNext()
            {

            returnsource
            . hasNext ();
            }
                @ OverridepublicTnext (){returnmapFunction .apply
            (

            source.
            next ( ));
            }
                @ Override publicvoidremove(
            )
        {throw
    new

    UnsupportedOperationException
    ( ) ;}}; }/**
     * Pulls all items from the {@code iterator} and puts them into a {@link List}, boxing each long.
     *
     * @param iterator {@link PrimitiveLongIterator} to pull values from.
     * @return a {@link List} containing all items.
     */ public static List
    <
        Long>asList( PrimitiveLongIterator iterator ) {List<Long>out
        = new ArrayList<>() ;
        while
            (iterator.hasNext ()){out .add
        (
        iterator .next
    (

    ) ) ;}returnout ;} public static Iterator <
    Long
        > toIterator (finalPrimitiveLongIteratorprimIterator){
        return
            newIterator
            < Long >()
            {
                @ OverridepublicbooleanhasNext()
            {

            returnprimIterator
            . hasNext ();
            }
                @ OverridepublicLongnext()
            {

            returnprimIterator
            . next ();
            }
                @ Override publicvoidremove(
            )
        {throw
    new

    UnsupportedOperationException
    ( ) ; }} ; } /**
     * Wraps a {@link PrimitiveLongIterator} in a {@link PrimitiveLongResourceIterator} which closes
     * the provided {@code resource} in {@link PrimitiveLongResourceIterator#close()}.
     *
     * @param iterator {@link PrimitiveLongIterator} to convert
     * @param resource {@link Resource} to close in {@link PrimitiveLongResourceIterator#close()}
     * @return Wrapped {@link PrimitiveLongIterator}.
     */public
            static PrimitiveLongResourceIterator resourceIterator (
    final
        PrimitiveLongIterator iterator ,finalResource
        resource
            ){
            return new PrimitiveLongResourceIterator()
            {
                @ Override public void close (
                )
                    {if(resource!=null
                )
            {

            resource.
            close ( );}
            }
                @ Overridepubliclongnext()
            {

            returniterator
            . next ();
            }
                @ OverridepublicbooleanhasNext()
            {
        returniterator
    .

    hasNext
    ( ) ;}}; }/**
     * Convert primitive set into a plain old java {@link Set}, boxing each long.
     *
     * @param set {@link PrimitiveLongSet} set of primitive values.
     * @return a {@link Set} containing all items.
     */ public static Set
    <
        Long >toSet (PrimitiveLongSetset){ returntoSet
    (

    set
    . iterator ()); }/**
     * Pulls all items from the {@code iterator} and puts them into a {@link Set}, boxing each long.
     *
     * @param iterator {@link PrimitiveLongIterator} to pull values from.
     * @return a {@link Set} containing all items.
     */ public static Set
    <
        Long>toSet( PrimitiveLongIterator iterator ) {Set<Long>set
        = new HashSet<>() ;
        while
            (iterator .hasNext ()){addUnique (set
        ,
        iterator .next
    (

    ) ) ;}return set ; }privatestatic<T , Cextends Collection <T > > void
    addUnique
        ( C collection,Titem) { if (
        !
            collection . add( item ) ) {
                    throw new IllegalStateException ("Encountered an already added item:"
        +
    item

    +
    " when adding items uniquely to a collection:" + collection); }} /**
     * Deduplicates values in the sorted {@code values} array.
     *
     * @param values sorted array of long values.
     * @return the provided array if no duplicates were found, otherwise a new shorter array w/o duplicates.
     */publicstatic long [
    ]
        deduplicate ( long []
        values ) { int unique =0 ; for (inti= 0; i
        <
            values . length ;i++){
            long value = values [ i] ; for (int j= 0
            ;
                j < unique ; j++){ if
                (
                    value == values[j ]
                    ){ value
                =
            -
            1 ; // signal that this value is not unique break ;// we will not find more than one conflict }
            }   if
                (value!=-1 ) {// this has to be done outside the inner loop, otherwise we'd never accept a single one...values[unique
            ++
        ]
        = values [ i]; } }returnunique< values. length ? Arrays .copyOf
    (

    values
    , unique ) : values ; }
    /**
     * Base iterator for simpler implementations of {@link PrimitiveLongIterator}s.
     */
        public abstract staticclass
        PrimitiveLongBaseIterator implements PrimitiveLongIterator{
        private boolean hasNextDecided;

        privateboolean
        hasNext ; protectedlongnext
        ;
            @ Override publicboolean hasNext
            (
                ) { if(!hasNextDecided
                ) { hasNext=
            fetchNext
            ( );
        hasNextDecided

        =true
        ; } returnhasNext;
        }
            @ Override publiclongnext( )
            {
                if ( !hasNext ( ) ) {throw
            new
            NoSuchElementException ( "No more elements in "+
            this );
        }

        hasNextDecided
        = false ; returnnext;}

        /**
         * Fetches the next item in this iterator. Returns whether or not a next item was found. If a next
         * item was found, that value must have been set inside the implementation of this method
         * using {@link #next(long)}.
         */
        protected abstract booleanfetchNext ( ) ;
        /**
         * Called from inside an implementation of {@link #fetchNext()} if a next item was found.
         * This method returns {@code true} so that it can be used in short-hand conditionals
         * (TODO what are they called?), like:
         * <pre>
         * protected boolean fetchNext()
         * {
         *     return source.hasNext() ? next( source.next() ) : false;
         * }
         * </pre>
         *
         * @param nextItem the next item found.
         */
            protected boolean next(
            long nextItem ){
            next =nextItem
        ;
    hasNext

    = true ; return true ;
    }
        } public staticclassPrimitiveLongConcatingIterator extends PrimitiveLongBaseIterator{ privatefinal
        Iterator < ?extends

        PrimitiveLongIterator >iterators ;privatePrimitiveLongIterator currentIterator ;public PrimitiveLongConcatingIterator (
        Iterator
            <?extends PrimitiveLongIterator >iterators
        )

        {this
        . iterators =iterators;
        }
            @ Override protected boolean fetchNext ( ){if(currentIterator== null
            ||
                ! currentIterator .hasNext()) {
                while
                    ( iterators .hasNext()){
                    currentIterator = iterators.next() ;
                    if
                        (currentIterator
                    .
                hasNext
            (
            ) ){ break ; } }}return(currentIterator!= null &&currentIterator .hasNext()) &&next
        (

        currentIterator . next ())
        ;
            } protectedfinal
        PrimitiveLongIterator
    currentIterator

    ( ) { return currentIterator ; }
            } public
    abstract
        static class PrimitiveLongFilteringIterator extendsPrimitiveLongBaseIterator

        implementsLongPredicate { protected final
        PrimitiveLongIterator
            source;PrimitiveLongFilteringIterator ( PrimitiveLongIteratorsource
        )

        {this
        . source =source;
        }
            @ Override protectedbooleanfetchNext() {
            while
                ( source . hasNext()){long
                testItem = source. next ( )
                ;
                    if (test ( testItem)
                )
            {
            return next(
        testItem

        );
        } } return false; } @ Overridepublic
    abstract

    boolean test ( long testItem ) ;
            } public
    abstract
        staticclass PrimitiveLongResourceFilteringIterator extends PrimitiveLongFilteringIterator
        implements
            PrimitiveLongResourceIterator{ PrimitiveLongResourceFilteringIterator (PrimitiveLongIterator
        source

        ){
        super ( source);
        }
            @ Override public void close (
            )
                {if(source instanceofResource){((Resource
            )
        source
    )

    . close ( ) ; }
    }
        } public staticclass
        PrimitiveLongRangeIterator extends PrimitiveLongBaseIterator {private

        longcurrent ; privatefinal long end ;
        PrimitiveLongRangeIterator
            (longstart , longend
            ){this . current=
        start

        ;this
        . end =end;
        }
            @
            Override
                protected boolean fetchNext ( ) {try { returncurrent
            <=
            end
            &&
                next(current
            )
        ;
    }
finally
