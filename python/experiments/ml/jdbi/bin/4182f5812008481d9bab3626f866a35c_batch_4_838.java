/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdbi.v3.core.result;

import static java.util.Spliterators.spliteratorUnknownSize;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
importjava.util.stream.Stream
; importjava.util.stream.StreamSupport
; importorg.jdbi.v3.core

. mapper.RowMapper;importorg.jdbi.v3.core
. statement.StatementContext;/**
 * An {@link Iterable} of values, usually mapped from a {@link java.sql.ResultSet}. Generally, ResultIterables may only
 * be traversed once.
 *
 * @param <T> iterable element type
 */@FunctionalInterfacepublicinterfaceResultIterable<T

>
extendsIterable
< T >{/**
     * Returns a ResultIterable backed by the given result set supplier, mapper, and context.
     *
     * @param supplier result set supplier
     * @param mapper   row mapper
     * @param ctx      statement context
     * @param <T>      the mapped type
     * @return the result iterable
     */static < T>ResultIterable< T
    >
    of (Supplier< ResultSet>supplier, RowMapper<T>mapper, StatementContextctx ){return( )-> { try{ return
        new ResultSetResultIterator< > (
            supplier .
                get ( ),mapper,ctx);}catch( SQLExceptione ){try
            { ctx .close () ;
                } catch
                    (Exceptione1){e
                . addSuppressed (e1 ); }
                    thrownewResultSetException("Unable to iterator result set",e
                ,
                ctx ) ;}}; }/**
     * Returns a ResultIterable backed by the given iterator.
     * @param iterator the result iterator
     * @param <T> iterator element type
     * @return a ResultIterable
     */ static<T
            >
        ResultIterable<
    T

    >
    of (ResultIterator< T>iterator) {return()->iterator ;} /**
     * Stream all the rows of the result set out
     * with an {@code Iterator}.  The {@code Iterator} must be
     * closed to release database resources.
     * @return the results as a streaming Iterator
     */
        @ OverrideResultIterator < T>
    iterator

    (
    );
    @Overridedefaultvoid forEach(Consumer<

    ?super
    T > action){try( ResultIterator <T >iterator =
        iterator ()){iterator . forEachRemaining (action); }
            }/**
     * Get the only row in the result set.
     * @throws IllegalStateException if zero or multiple rows are returned
     * @return the object mapped from the singular row in the results
     */defaultTfindOnly()
        {
    try

    (
    ResultIterator < T>iter =
        iterator ()){if ( ! iter.hasNext( )
            ) {thrownewIllegalStateException("No element found in 'only'"); }
                final T r=iter.next
            (

            ) ; if ( iter.hasNext())

            { thrownewIllegalStateException("Multiple elements found in 'only'"); }
                return r ;}}/**
     * @return the first row in the result set, if present.
     */default
            Optional

            < T>
        findFirst
    (

    )
    { try(ResultIterator< T>iter =
        iterator ()){return iter . hasNext()? Optional
            . ofNullable(iter.next ( )):Optional.empty();} } /**
     * Returns the stream of results.
     *
     * <p>
     * Note: the returned stream owns database resources, and must be closed via a call to {@link Stream#close()}, or
     * by using the stream in a try-with-resources block:
     * </p>
     *
     * <pre>
     * try (Stream&lt;T&gt; stream = query.stream()) {
     *   // do stuff with stream
     * }
     * </pre>
     *
     * @return the stream of results.
     *
     * @see #useStream(StreamConsumer)
     * @see #withStream(StreamCallback)
     */defaultStream<T>
        stream
    (

    )
    { ResultIterator<T> iterator=iterator (
        );returnStreamSupport . stream (spliteratorUnknownSize(iterator
        , 0),false).onClose( iterator::close );
                }/**
     * Passes the stream of results to the consumer. Database resources owned by the query are
     * released before this method returns.
     *
     * @param consumer a consumer which receives the stream of results.
     * @param <X> the exception type thrown by the callback, if any
     *
     * @throws X any exception thrown by the callback
     */default<XextendsException>
    void

    useStream
    ( StreamConsumer< T ,X > consumer)throwsX{withStream (stream ->{ consumer . useStream
        (stream) ; return
            null;});}/**
     * Passes the stream of results to the callback. Database resources owned by the query are
     * released before this method returns.
     *
     * @param callback a callback which receives the stream of results, and returns some result.
     * @param <R> the type returned by the callback
     * @param <X> the exception type thrown by the callback, if any
     *
     * @return the value returned by the callback.
     *
     * @throws X any exception thrown by the callback
     */
            default <R
        ,Xextends
    Exception

    >
    R withStream(StreamCallback < T ,R , X>callback)throwsX {try (Stream <T > stream =
        stream ()){return callback . withStream(stream) ;
            } }/**
     * Returns results in a {@link List}.
     *
     * @return results in a {@link List}.
     */defaultList<T>
        list
    (

    )
    { returncollect(Collectors .toList( )
        ) ;}/**
     * Collect the results into a container specified by a collector.
     *
     * @param collector       the collector
     * @param <R>             the generic type of the container
     * @return the container with the query result
     */default<R>Rcollect
    (

    Collector
    < T,? , R>collector){try (Stream <T >stream =
        stream ()){return stream . collect(collector) ;
            } }/**
     * Reduce the results.  Using a {@code BiFunction<U, T, U>}, repeatedly
     * combine query results until only a single value remains.
     *
     * @param <U> the accumulator type
     * @param identity the {@code U} to combine with the first result
     * @param accumulator the function to apply repeatedly
     * @return the final {@code U}
     */default<U>U
        reduce
    (

    U
    identity ,BiFunction< U ,T, U> accumulator){try (Stream <T >stream =
        stream ()){return stream . reduce(identity, accumulator
            , (u,v)-> {throw
                newUnsupportedOperationException( "parallel operation not supported") ; }
                    ) ; }}}